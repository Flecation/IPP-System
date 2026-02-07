package IPPSystem.Controllers;

import IPPSystem.DAO.databaseConnection;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for View/CreateReportNew.fxml
 *
 * Flow:
 * 1) Default date = today
 * 2) When user selects workItem (or date changes):
 *    - Look up dailyReports by (assignProjectId, assignWorkItemId, reportDate)
 *    - If exists: load weather/generalRemark/issue + labors + tasks
 *    - If not: clear inputs for new report
 *
 * Save behavior (matches your rule):
 * - dailyReports has UNIQUE(assignProjectId, assignWorkItemId, reportDate)
 * - If header is new: insert header, then insert labors + ensure assignWorkers
 * - If header already exists: do NOT insert labors again; tasks can be replaced/updated
 *
 * Material cost:
 * - Your current dailyReports table doesn't have a materialCost column.
 * - This controller stores material cost as a special row in dailyReportTasks:
 *     assignTaskId = NULL, progressDescription = '__MATERIAL_COST__', dailyCost = <materialCost>
 */
public class CreateReportNewController implements Initializable {

    // ---------------- FXML ----------------
    @FXML private ScrollPane mainScrollPane;

    @FXML private Label currentAssignedProjectName;

    @FXML private ComboBox<WorkItemOption> workItemCombo;
    @FXML private ComboBox<TaskOption> taskCombo;
    @FXML private DatePicker todayReportDate;

    @FXML private ComboBox<String> weatherConditionBox;
    @FXML private TextArea otherWeatherCondition; // optional extra notes

    @FXML private ComboBox<SkillOption> laborSkillCombo;
    @FXML private TextField laborName;
    @FXML private TextField laborDailyWadgePerDay;
    @FXML private TextField laborWorkHourPerDay;
    @FXML private TextArea remarkForLabor;
    @FXML private Button addAssignedLaborBtn;

    @FXML private TableView<LaborRow> todayLaborTable;
    @FXML private TableColumn<LaborRow, String> laborNameCol;
    @FXML private TableColumn<LaborRow, String> laborSkillCol;
    @FXML private TableColumn<LaborRow, Number> dailyWadgeCol;
    @FXML private TableColumn<LaborRow, Number> workHourCol;
    @FXML private TableColumn<LaborRow, String> remarkCol;
    @FXML private TableColumn<LaborRow, Void> actionCol;

    @FXML private TextField materialCostField;

    @FXML private TextField remainTask;
    @FXML private TextField completedTask;
    @FXML private VBox progressContainer;
    @FXML private Button addProgressBtn;

    @FXML private TextArea generalRemark;

    @FXML private VBox issuesContainer;
    @FXML private Button addIssuesBtn;

    @FXML private Button submitReportBtn;

    // ---------------- STATE ----------------
    private int assignProjectId = -1;
    private int supervisorId = -1;

    private Integer currentDailyReportId = null;
    private boolean currentReportIsNew = true;

    private final ObservableList<LaborRow> laborRows = FXCollections.observableArrayList();
    private final ObservableList<ProgressRow> progressRows = FXCollections.observableArrayList();

    private static final String MATERIAL_MARKER = "__MATERIAL_COST__";

    // ---------------- Public context setter ----------------
    /**
     * Call this after loading FXML to set current project context.
     */
    public void setContext(int assignProjectId, String projectName, int supervisorId) {
        this.assignProjectId = assignProjectId;
        this.supervisorId = supervisorId;
        if (currentAssignedProjectName != null) currentAssignedProjectName.setText(projectName);

        loadWorkItemsForProject();
        refreshReportView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupWeather();
        setupLaborTable();
        todayLaborTable.setItems(laborRows);

        if (todayReportDate != null) {
            todayReportDate.setValue(LocalDate.now());
            todayReportDate.valueProperty().addListener((obs, o, n) -> refreshReportView());
        }

        if (workItemCombo != null) {
            workItemCombo.valueProperty().addListener((obs, o, n) -> {
                loadTasksForSelectedWorkItem();
                refreshReportView();
            });
        }

        loadSkills();

        // start with one issue row
        if (issuesContainer != null) {
            issuesContainer.getChildren().clear();
            addIssueRow(null);
        }
    }

    private void setupWeather() {
        if (weatherConditionBox == null) return;
        weatherConditionBox.setItems(FXCollections.observableArrayList(
                "Sunny", "Cloudy", "Rainy", "Windy", "Stormy", "Foggy", "Other"
        ));
    }

    private void setupLaborTable() {
        if (laborNameCol != null) laborNameCol.setCellValueFactory(new PropertyValueFactory<>("laborName"));
        if (laborSkillCol != null) laborSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (dailyWadgeCol != null) dailyWadgeCol.setCellValueFactory(new PropertyValueFactory<>("dailyWage"));
        if (workHourCol != null) workHourCol.setCellValueFactory(new PropertyValueFactory<>("workHours"));
        if (remarkCol != null) remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));

        if (actionCol != null) {
            actionCol.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("Remove");
                {
                    btn.setOnAction(e -> {
                        LaborRow row = getTableView().getItems().get(getIndex());
                        laborRows.remove(row);
                    });
                }
                @Override protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            });
        }
    }

    // ---------------- UI actions ----------------
    @FXML
    private void addAssignedLabor(ActionEvent event) {
        SkillOption skill = laborSkillCombo == null ? null : laborSkillCombo.getValue();
        String name = trim(laborName == null ? null : laborName.getText());

        double wage;
        double hours;
        try {
            wage = Double.parseDouble(trim(laborDailyWadgePerDay == null ? null : laborDailyWadgePerDay.getText()));
            hours = Double.parseDouble(trim(laborWorkHourPerDay == null ? null : laborWorkHourPerDay.getText()));
        } catch (Exception ex) {
            alert(Alert.AlertType.WARNING, "Validation", "Daily wage and work hours must be numbers.");
            return;
        }
        if (skill == null) {
            alert(Alert.AlertType.WARNING, "Validation", "Please select a skill.");
            return;
        }
        if (name.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation", "Please enter labor name.");
            return;
        }

        String remark = trim(remarkForLabor == null ? null : remarkForLabor.getText());
        laborRows.add(new LaborRow(null, name, skill.skillId(), skill.skillName(), wage, hours, remark));

        if (laborName != null) laborName.clear();
        if (laborDailyWadgePerDay != null) laborDailyWadgePerDay.clear();
        if (laborWorkHourPerDay != null) laborWorkHourPerDay.clear();
        if (remarkForLabor != null) remarkForLabor.clear();
    }

    @FXML
    private void handleAddProgress(ActionEvent event) {
        TaskOption t = taskCombo == null ? null : taskCombo.getValue();
        if (t == null) {
            alert(Alert.AlertType.WARNING, "Validation", "Please choose a task first.");
            return;
        }
        progressRows.add(new ProgressRow(
                t.assignTaskId(),
                t.taskName(),
                "",          // description
                0,           // workHours
                0,           // completedQty
                0,           // dailyCost
                false        // isCompleted
        ));
        renderProgressRows();
    }

    @FXML
    private void handleAddIssue(ActionEvent event) {
        addIssueRow(null);
    }

    @FXML
    private void submitReportBtn(ActionEvent event) {
        if (assignProjectId <= 0) {
            alert(Alert.AlertType.ERROR, "Missing Context", "assignProjectId is not set. Call setContext(...) first.");
            return;
        }
        WorkItemOption workItem = workItemCombo == null ? null : workItemCombo.getValue();
        if (workItem == null) {
            alert(Alert.AlertType.WARNING, "Validation", "Please select a work item.");
            return;
        }
        LocalDate date = todayReportDate == null ? null : todayReportDate.getValue();
        if (date == null) {
            alert(Alert.AlertType.WARNING, "Validation", "Please choose a date.");
            return;
        }

        String weather = weatherConditionBox == null ? "" : Objects.toString(weatherConditionBox.getValue(), "");
        String issuesText = collectTextAreas(issuesContainer);
        String commentsText = trim(generalRemark == null ? null : generalRemark.getText());

        double materialCost = parseDouble(materialCostField == null ? null : materialCostField.getText());

        try (Connection con = databaseConnection.getConnection()) {
            con.setAutoCommit(false);

            DailyReportHeaderResult hdr = getOrCreateDailyReport(con, assignProjectId, workItem.assignWorkItemId(), date,
                    supervisorId, weather, commentsText, issuesText);

            // Only insert labors when header is NEW (your rule)
            if (hdr.isNew) {
                for (LaborRow lr : laborRows) {
                    Integer laborId = lr.laborId.get();
                    if (laborId == null) {
                        laborId = findLaborIdByName(con, lr.laborName.get());
                    }
                    if (laborId == null) {
                        con.rollback();
                        alert(Alert.AlertType.ERROR, "Labor not found",
                                "Cannot find labor in DB: " + lr.laborName.get() + ".\n" +
                                        "Please create/select labors from Labor module first (so laborId exists).");
                        return;
                    }

                    // assignWorkers: workerId = laborId in your schema
                    insertAssignWorkerIgnore(con, assignProjectId, laborId);

                    // dailyReportLabors
                    insertDailyReportLaborIgnore(con, hdr.dailyReportId, laborId,
                            lr.workHours.get(), lr.dailyWage.get(), lr.remark.get());
                }
            }

            // Replace tasks for this report (simple + predictable)
            replaceDailyReportTasks(con, hdr.dailyReportId, progressRows, materialCost);

            con.commit();

            // refresh view
            refreshReportView();

            alert(Alert.AlertType.INFORMATION, "Saved",
                    hdr.isNew
                            ? "Daily report created and saved."
                            : "Daily report already existed. Labors skipped; tasks updated.");

        } catch (Exception ex) {
            ex.printStackTrace();
            alert(Alert.AlertType.ERROR, "Save Error", ex.getMessage());
        }
    }

    // ---------------- Load / Refresh ----------------
    private void refreshReportView() {
        currentDailyReportId = null;
        currentReportIsNew = true;

        if (assignProjectId <= 0) return;
        WorkItemOption wi = workItemCombo == null ? null : workItemCombo.getValue();
        LocalDate date = todayReportDate == null ? null : todayReportDate.getValue();
        if (wi == null || date == null) return;

        try (Connection con = databaseConnection.getConnection()) {
            DailyReportLoaded loaded = loadDailyReportByKey(con, assignProjectId, wi.assignWorkItemId(), date);

            if (loaded == null) {
                clearUI();
                return;
            }

            currentDailyReportId = loaded.dailyReportId;
            currentReportIsNew = false;

            if (weatherConditionBox != null) weatherConditionBox.setValue(loaded.weather);

            if (generalRemark != null) generalRemark.setText(nvl(loaded.commentsText));

            // issues
            if (issuesContainer != null) {
                issuesContainer.getChildren().clear();
                if (loaded.issuesText != null && !loaded.issuesText.trim().isEmpty()) {
                    for (String line : loaded.issuesText.split("\\r?\\n")) addIssueRow(line.trim());
                } else {
                    addIssueRow(null);
                }
            }

            // labors
            laborRows.setAll(loaded.labors);

            // tasks
            progressRows.setAll(loaded.tasks);
            renderProgressRows();

            // material cost pulled from special row
            if (materialCostField != null) materialCostField.setText(String.valueOf(loaded.materialCost));

            // completed/remain count
            updateRemainCompleted(con, wi.assignWorkItemId(), loaded.tasks);

        } catch (Exception ex) {
            ex.printStackTrace();
            alert(Alert.AlertType.ERROR, "Load Error", ex.getMessage());
        }
    }

    private void clearUI() {
        if (weatherConditionBox != null) weatherConditionBox.setValue(null);
        if (generalRemark != null) generalRemark.clear();
        if (materialCostField != null) materialCostField.clear();

        if (issuesContainer != null) {
            issuesContainer.getChildren().clear();
            addIssueRow(null);
        }

        laborRows.clear();
        progressRows.clear();
        if (progressContainer != null) progressContainer.getChildren().clear();

        if (completedTask != null) completedTask.setText("0");
        if (remainTask != null) remainTask.setText("0");
    }

    // ---------------- Dynamic UI components ----------------
    private void addIssueRow(String initial) {
        if (issuesContainer == null) return;

        TextArea ta = new TextArea();
        ta.setWrapText(true);
        ta.setPrefRowCount(2);
        ta.setPromptText("Issue / risk...");
        if (initial != null) ta.setText(initial);

        Button remove = new Button("Remove");
        remove.setOnAction(e -> {
            issuesContainer.getChildren().removeIf(node -> node == ta.getParent());
            if (issuesContainer.getChildren().isEmpty()) addIssueRow(null);
        });

        HBox box = new HBox(10, ta, remove);
        box.setPadding(new Insets(5));
        issuesContainer.getChildren().add(box);
    }

    private void renderProgressRows() {
        if (progressContainer == null) return;
        progressContainer.getChildren().clear();

        for (ProgressRow pr : progressRows) {
            VBox card = new VBox(8);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e8e8e8; -fx-border-radius: 10;");

            Label title = new Label(pr.taskName.get());
            title.setStyle("-fx-font-weight: bold;");

            TextArea desc = new TextArea(pr.progressDescription.get());
            desc.setPromptText("Progress description...");
            desc.setPrefRowCount(2);
            desc.setWrapText(true);

            TextField hours = new TextField(String.valueOf(pr.workHours.get()));
            hours.setPromptText("Work hours");

            TextField qty = new TextField(String.valueOf(pr.completedQty.get()));
            qty.setPromptText("Completed qty");

            TextField cost = new TextField(String.valueOf(pr.dailyCost.get()));
            cost.setPromptText("Task cost");

            CheckBox completed = new CheckBox("Completed");
            completed.setSelected(pr.isCompleted.get());

            Button remove = new Button("Remove");
            remove.setOnAction(e -> {
                progressRows.remove(pr);
                renderProgressRows();
            });

            // bind
            desc.textProperty().addListener((o, ov, nv) -> pr.progressDescription.set(nv));
            hours.textProperty().addListener((o, ov, nv) -> pr.workHours.set(parseDouble(nv)));
            qty.textProperty().addListener((o, ov, nv) -> pr.completedQty.set(parseDouble(nv)));
            cost.textProperty().addListener((o, ov, nv) -> pr.dailyCost.set(parseDouble(nv)));
            completed.selectedProperty().addListener((o, ov, nv) -> pr.isCompleted.set(nv));

            HBox row1 = new HBox(10, new Label("Hours:"), hours, new Label("Qty:"), qty);
            HBox row2 = new HBox(10, new Label("Cost:"), cost, completed, remove);

            card.getChildren().addAll(title, desc, row1, row2);
            progressContainer.getChildren().add(card);
        }
    }

    // ---------------- DB: load combos ----------------
    private void loadSkills() {
        if (laborSkillCombo == null) return;
        laborSkillCombo.getItems().clear();
        String sql = "SELECT skillId, skillName FROM skills ORDER BY skillId";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                laborSkillCombo.getItems().add(new SkillOption(rs.getInt("skillId"), rs.getString("skillName")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadWorkItemsForProject() {
        if (workItemCombo == null) return;
        workItemCombo.getItems().clear();
        if (assignProjectId <= 0) return;

        String sql =
                "SELECT awi.assignWorkItemId, wi.projectWorkItemName " +
                        "FROM assignWorkItems awi " +
                        "JOIN workItems wi ON wi.projectWorkItemId = awi.projectWorkItemId " +
                        "WHERE awi.assignProjectId = ? " +
                        "ORDER BY awi.assignWorkItemId";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, assignProjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    workItemCombo.getItems().add(new WorkItemOption(
                            rs.getInt("assignWorkItemId"),
                            rs.getString("projectWorkItemName")
                    ));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadTasksForSelectedWorkItem() {
        if (taskCombo == null) return;
        taskCombo.getItems().clear();
        WorkItemOption wi = workItemCombo == null ? null : workItemCombo.getValue();
        if (wi == null) return;

        String sql =
                "SELECT at.assignTaskId, t.projectTaskName " +
                        "FROM assignTasks at " +
                        "JOIN tasks t ON t.projectTaskId = at.projectTaskId " +
                        "WHERE at.assignWorkItemId = ? " +
                        "ORDER BY at.assignTaskId";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, wi.assignWorkItemId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    taskCombo.getItems().add(new TaskOption(rs.getInt("assignTaskId"), rs.getString("projectTaskName")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- DB: load report by key ----------------
    private DailyReportLoaded loadDailyReportByKey(Connection con, int pid, int wid, LocalDate date) throws SQLException {
        String hdrSql = "SELECT dailyReportId, weather, issue, generalRemark FROM dailyReports " +
                "WHERE assignProjectId=? AND assignWorkItemId=? AND reportDate=? LIMIT 1";

        Integer dailyReportId;
        String weather;
        String issues;
        String comments;

        try (PreparedStatement ps = con.prepareStatement(hdrSql)) {
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDate(3, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                dailyReportId = rs.getInt("dailyReportId");
                weather = rs.getString("weather");
                issues = rs.getString("issue");
                comments = rs.getString("generalRemark");
            }
        }

        // labors
        ObservableList<LaborRow> labors = FXCollections.observableArrayList();
        String laborSql = "SELECT drl.laborId, l.laborName, s.skillId, s.skillName, drl.dailyWage, drl.workHours, drl.remark " +
                "FROM dailyReportLabors drl " +
                "JOIN labors l ON l.laborId = drl.laborId " +
                "JOIN skills s ON s.skillId = l.skillId " +
                "WHERE drl.dailyReportId = ? ORDER BY l.laborName";

        try (PreparedStatement ps = con.prepareStatement(laborSql)) {
            ps.setInt(1, dailyReportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    labors.add(new LaborRow(
                            rs.getInt("laborId"),
                            rs.getString("laborName"),
                            rs.getInt("skillId"),
                            rs.getString("skillName"),
                            rs.getDouble("dailyWage"),
                            rs.getDouble("workHours"),
                            rs.getString("remark")
                    ));
                }
            }
        }

        // tasks + material cost row
        ObservableList<ProgressRow> tasks = FXCollections.observableArrayList();
        double materialCost = 0;

        String taskSql = "SELECT dailyReportTaskId, assignTaskId, progressDescription, workHours, completedQty, dailyCost, isCompleted " +
                "FROM dailyReportTasks WHERE dailyReportId = ? ORDER BY dailyReportTaskId";

        try (PreparedStatement ps = con.prepareStatement(taskSql)) {
            ps.setInt(1, dailyReportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer assignTaskId = (Integer) rs.getObject("assignTaskId");
                    String desc = rs.getString("progressDescription");
                    double hours = rs.getDouble("workHours");
                    double qty = rs.getDouble("completedQty");
                    double cost = rs.getDouble("dailyCost");
                    boolean done = rs.getBoolean("isCompleted");

                    if (assignTaskId == null && MATERIAL_MARKER.equals(desc)) {
                        materialCost = cost;
                        continue;
                    }

                    String taskName = assignTaskId == null ? "Other" : lookupTaskName(con, assignTaskId);
                    tasks.add(new ProgressRow(assignTaskId, taskName, nvl(desc), hours, qty, cost, done));
                }
            }
        }

        return new DailyReportLoaded(dailyReportId, weather, issues, comments, materialCost, labors, tasks);
    }

    private String lookupTaskName(Connection con, int assignTaskId) {
        String sql = "SELECT t.projectTaskName " +
                "FROM assignTasks at JOIN tasks t ON t.projectTaskId = at.projectTaskId " +
                "WHERE at.assignTaskId = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignTaskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("projectTaskName");
            }
        } catch (SQLException ignored) {}
        return "Task #" + assignTaskId;
    }

    private void updateRemainCompleted(Connection con, int assignWorkItemId, ObservableList<ProgressRow> tasks) throws SQLException {
        int total = 0;
        String sql = "SELECT COUNT(*) AS c FROM assignTasks WHERE assignWorkItemId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignWorkItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) total = rs.getInt("c");
            }
        }

        int completed = 0;
        for (ProgressRow pr : tasks) if (pr.isCompleted.get()) completed++;

        if (completedTask != null) completedTask.setText(String.valueOf(completed));
        if (remainTask != null) remainTask.setText(String.valueOf(Math.max(0, total - completed)));
    }

    // ---------------- DB: header insert/find ----------------
    private DailyReportHeaderResult getOrCreateDailyReport(Connection con,
                                                           int pid,
                                                           int wid,
                                                           LocalDate date,
                                                           int supervisorId,
                                                           String weather,
                                                           String generalRemark,
                                                           String issue) throws SQLException {
        String findSql = "SELECT dailyReportId FROM dailyReports WHERE assignProjectId=? AND assignWorkItemId=? AND reportDate=? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(findSql)) {
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDate(3, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new DailyReportHeaderResult(rs.getInt("dailyReportId"), false);
            }
        }

        String insSql = "INSERT INTO dailyReports(assignProjectId, assignWorkItemId, reportDate, supervisorId, weather, generalRemark, issue) " +
                "VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(insSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDate(3, Date.valueOf(date));
            ps.setObject(4, supervisorId <= 0 ? null : supervisorId);
            ps.setString(5, weather);
            ps.setString(6, generalRemark);
            ps.setString(7, issue);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new DailyReportHeaderResult(keys.getInt(1), true);
            }
        }
        throw new SQLException("Failed to create daily report header (no key returned).");
    }

    // ---------------- DB: insert detail rows ----------------
    private void insertAssignWorkerIgnore(Connection con, int pid, int laborId) throws SQLException {
        // assignWorkers table uses workerId
        String sql = "INSERT IGNORE INTO assignWorkers(assignProjectId, workerId) VALUES(?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pid);
            ps.setInt(2, laborId);
            ps.executeUpdate();
        }
    }

    private void insertDailyReportLaborIgnore(Connection con, int dailyReportId, int laborId,
                                              double workHours, double dailyWage, String remark) throws SQLException {
        String sql = "INSERT IGNORE INTO dailyReportLabors(dailyReportId, laborId, workHours, dailyWage, remark) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ps.setInt(2, laborId);
            ps.setDouble(3, workHours);
            ps.setDouble(4, dailyWage);
            ps.setString(5, remark);
            ps.executeUpdate();
        }
    }

    private void replaceDailyReportTasks(Connection con, int dailyReportId, ObservableList<ProgressRow> tasks, double materialCost) throws SQLException {
        // delete old tasks (simple approach)
        try (PreparedStatement del = con.prepareStatement("DELETE FROM dailyReportTasks WHERE dailyReportId = ?")) {
            del.setInt(1, dailyReportId);
            del.executeUpdate();
        }

        String ins = "INSERT INTO dailyReportTasks(dailyReportId, assignTaskId, progressDescription, workHours, completedQty, dailyCost, isCompleted) " +
                "VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(ins)) {
            // 1) material cost row (if any)
            if (materialCost > 0) {
                ps.setInt(1, dailyReportId);
                ps.setObject(2, null);
                ps.setString(3, MATERIAL_MARKER);
                ps.setDouble(4, 0);
                ps.setDouble(5, 0);
                ps.setDouble(6, materialCost);
                ps.setBoolean(7, false);
                ps.addBatch();
            }

            // 2) task rows
            for (ProgressRow pr : tasks) {
                ps.setInt(1, dailyReportId);
                ps.setObject(2, pr.assignTaskId.get() == null ? null : pr.assignTaskId.get());
                ps.setString(3, pr.progressDescription.get());
                ps.setDouble(4, pr.workHours.get());
                ps.setDouble(5, pr.completedQty.get());
                ps.setDouble(6, pr.dailyCost.get());
                ps.setBoolean(7, pr.isCompleted.get());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private Integer findLaborIdByName(Connection con, String laborName) throws SQLException {
        String sql = "SELECT laborId FROM labors WHERE laborName = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, laborName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("laborId");
            }
        }
        return null;
    }

    // ---------------- helpers ----------------
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static String nvl(String s) { return s == null ? "" : s; }

    private static double parseDouble(String s) {
        try { return Double.parseDouble(trim(s)); } catch (Exception e) { return 0; }
    }

    private static void alert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static String collectTextAreas(VBox container) {
        if (container == null) return "";
        List<String> lines = new ArrayList<>();
        for (var node : container.getChildren()) {
            if (node instanceof HBox hb) {
                for (var child : hb.getChildren()) {
                    if (child instanceof TextArea ta) {
                        String v = trim(ta.getText());
                        if (!v.isEmpty()) lines.add(v);
                    }
                }
            }
        }
        return String.join("\n", lines);
    }

    // ---------------- DTOs ----------------
    public record WorkItemOption(int assignWorkItemId, String name) {
        @Override public String toString() { return name; }
    }

    public record TaskOption(int assignTaskId, String taskName) {
        @Override public String toString() { return taskName; }
    }

    public record SkillOption(int skillId, String skillName) {
        @Override public String toString() { return skillName; }
    }

    public static class LaborRow {
        private final IntegerProperty laborId = new SimpleIntegerProperty();
        private final StringProperty laborName = new SimpleStringProperty();
        private final IntegerProperty skillId = new SimpleIntegerProperty();
        private final StringProperty skillName = new SimpleStringProperty();
        private final DoubleProperty dailyWage = new SimpleDoubleProperty();
        private final DoubleProperty workHours = new SimpleDoubleProperty();
        private final StringProperty remark = new SimpleStringProperty();

        public LaborRow(Integer laborId, String laborName, int skillId, String skillName,
                        double dailyWage, double workHours, String remark) {
            this.laborId.set(laborId == null ? 0 : laborId);
            this.laborName.set(Objects.requireNonNullElse(laborName, ""));
            this.skillId.set(skillId);
            this.skillName.set(Objects.requireNonNullElse(skillName, ""));
            this.dailyWage.set(dailyWage);
            this.workHours.set(workHours);
            this.remark.set(Objects.requireNonNullElse(remark, ""));
        }

        public IntegerProperty laborIdProperty() { return laborId; }
        public String getLaborName() { return laborName.get(); }
        public String getSkillName() { return skillName.get(); }
        public double getDailyWage() { return dailyWage.get(); }
        public double getWorkHours() { return workHours.get(); }
        public String getRemark() { return remark.get(); }
    }

    public static class ProgressRow {
        private final ObjectProperty<Integer> assignTaskId = new SimpleObjectProperty<>();
        private final StringProperty taskName = new SimpleStringProperty();
        private final StringProperty progressDescription = new SimpleStringProperty();
        private final DoubleProperty workHours = new SimpleDoubleProperty();
        private final DoubleProperty completedQty = new SimpleDoubleProperty();
        private final DoubleProperty dailyCost = new SimpleDoubleProperty();
        private final BooleanProperty isCompleted = new SimpleBooleanProperty();

        public ProgressRow(Integer assignTaskId, String taskName, String progressDescription,
                           double workHours, double completedQty, double dailyCost, boolean isCompleted) {
            this.assignTaskId.set(assignTaskId);
            this.taskName.set(Objects.requireNonNullElse(taskName, ""));
            this.progressDescription.set(Objects.requireNonNullElse(progressDescription, ""));
            this.workHours.set(workHours);
            this.completedQty.set(completedQty);
            this.dailyCost.set(dailyCost);
            this.isCompleted.set(isCompleted);
        }
    }

    private record DailyReportHeaderResult(int dailyReportId, boolean isNew) {}

    private record DailyReportLoaded(
            int dailyReportId,
            String weather,
            String issuesText,
            String commentsText,
            double materialCost,
            ObservableList<LaborRow> labors,
            ObservableList<ProgressRow> tasks
    ) {}
}
