package IPPSystem.Controllers;

import IPPSystem.DAO.databaseConnection;
import IPPSystem.Models.users;
import IPPSystem.Utils.session;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

//import static jdk.vm.ci.aarch64.AArch64.lr;

/**
 * Create Daily Report (CreateReportNew.fxml) - Create path only.
 *
 * FIX for your FXML error:
 *  - Any method referenced by onAction="#..." MUST be annotated with @FXML (or be public).
 *  - This controller provides: addAssignedLabor, handleAddProgress, handleAddIssue, submitReport.
 *
 * NOTE: column/table names follow your latest schema discussions:
 *  - assignWorkers uses workerId (not laborId)
 *  - assignWorkItemSkillDetails uses assignStatusId (not assignStatus)
 *
 * If your actual table/column names differ, tell me the exact names and I will adjust the SQL.
 */
public class CreateReportNewController implements Initializable {

    // ---------------- FXML ----------------
    @FXML private Label currentAssignedProjectName;

    @FXML private ComboBox<WorkItemOption> workItemCombo;
    @FXML private ComboBox<TaskOption> taskCombo;
    @FXML private DatePicker todayReportDate;

    @FXML private ComboBox<String> weatherConditionBox;
    @FXML private TextArea otherWeatherCondition;

    @FXML private ComboBox<SkillOption> laborSkillCombo;
    @FXML private ComboBox<LaborOption> laborNameCombo;
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
    private Integer assignProjectId;      // current in-progress project
    private String assignProjectName;
    private Integer supervisorId;

    private Integer selectedLaborId = null;
//    private boolean laborSelectionCommitted = false;
    private HBox progressAddRow;



    private final ObservableList<LaborRow> laborRows = FXCollections.observableArrayList();
    // Progress descriptions are handled like Issues (dynamic rows in progressContainer)

    // Adjust these IDs to your DB constants
    private static final int STATUS_IN_PROGRESS = 2;
    private static final int STATUS_FINISHED = 4;
    private static final int ASSIGN_STATUS_AUTO = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupWeather();
        setupDatePickerNoFuture();
        setupLaborTable();
        setupNumericFilters();
        if (progressContainer != null) {
            progressContainer.getChildren().clear();

            // button row at bottom
            progressAddRow = new HBox(10, addProgressBtn);
            progressAddRow.setPadding(new Insets(5));
            progressContainer.getChildren().add(progressAddRow);

            // first progress row above it
            addProgressRow(null);
        }


        if (todayLaborTable != null) todayLaborTable.setItems(laborRows);

        setFormEnabled(false);

        // cascade workItem -> tasks/skills
        if (workItemCombo != null) {
            workItemCombo.valueProperty().addListener((obs, o, n) -> {
                loadTasksForSelectedWorkItem();
                loadSkillsForSelectedWorkItem();
                refreshRemainQty();
            });
        }
        if (taskCombo != null) {
            taskCombo.valueProperty().addListener((obs, o, n) -> refreshRemainQty());
        }

        if (todayReportDate != null) {
            todayReportDate.setValue(LocalDate.now());
            todayReportDate.valueProperty().addListener((obs, o, n) -> refreshRemainQty());
        }

        if (laborSkillCombo != null) {
            laborSkillCombo.valueProperty().addListener((obs, o, n) -> {
                selectedLaborId = null;

                if (laborNameCombo != null) {
                    laborNameCombo.getItems().clear();
                    laborNameCombo.setValue(null);

                    boolean enable = (n != null);
                    laborNameCombo.setDisable(!enable);

                    if (enable) {
                        // load initial dropdown list immediately (top 20)
                        laborNameCombo.getItems().setAll(queryLaborSuggestions(n.skillId(), ""));
                    }
                }

                autoFillDailyWage();
            });

        }

        setupLaborNameCombo();

        if (issuesContainer != null) {
            issuesContainer.getChildren().clear();
            addIssueRow(null);
        }

        // (Optional) also wire in code, but FXML onAction will still work
        if (addAssignedLaborBtn != null) addAssignedLaborBtn.setOnAction(this::addAssignedLabor);
        if (addProgressBtn != null) addProgressBtn.setOnAction(this::handleAddProgress);
        if (addIssuesBtn != null) addIssuesBtn.setOnAction(this::handleAddIssue);
        if (submitReportBtn != null) submitReportBtn.setOnAction(this::submitReport);

        loadCurrentProjectContext();
    }

    // ---------------- Context ----------------
    private void loadCurrentProjectContext() {
        users u = session.getInstance().getUser();
        if (u == null || u.getUserId() <= 0) {
            showError("Login required", "No logged-in user found.");
            return;
        }
        this.supervisorId = u.getUserId();

        try (Connection con = databaseConnection.getConnection()) {
            String sql = "SELECT assignProjectId, projectInstanceName " +
                    "FROM assignProjects WHERE supervisorId=? AND projectStatus=? " +
                    "ORDER BY assignProjectId DESC LIMIT 1";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, supervisorId);
                ps.setInt(2, STATUS_IN_PROGRESS);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        this.assignProjectId = rs.getInt("assignProjectId");
                        this.assignProjectName = rs.getString("projectInstanceName");
                    }
                }
            }

            if (assignProjectId == null || assignProjectId <= 0) {
                if (currentAssignedProjectName != null) currentAssignedProjectName.setText("No In-Progress Project");
                setFormEnabled(false);
                return;
            }

            if (currentAssignedProjectName != null) currentAssignedProjectName.setText(assignProjectName);
            setFormEnabled(true);

            loadWorkItemsForProject();
            if (workItemCombo != null && !workItemCombo.getItems().isEmpty()) {
                workItemCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Load Error", ex.getMessage());
        }
    }

    private void setFormEnabled(boolean enabled) {
        if (workItemCombo != null) workItemCombo.setDisable(!enabled);
        if (taskCombo != null) taskCombo.setDisable(!enabled);
        if (todayReportDate != null) todayReportDate.setDisable(!enabled);
        if (weatherConditionBox != null) weatherConditionBox.setDisable(!enabled);
        if (otherWeatherCondition != null) otherWeatherCondition.setDisable(!enabled);

        if (laborSkillCombo != null) laborSkillCombo.setDisable(!enabled);
        if (laborNameCombo != null) laborNameCombo.setDisable(true); // until skill selected
        if (laborDailyWadgePerDay != null) laborDailyWadgePerDay.setDisable(!enabled);
        if (laborWorkHourPerDay != null) laborWorkHourPerDay.setDisable(!enabled);
        if (remarkForLabor != null) remarkForLabor.setDisable(!enabled);
        if (addAssignedLaborBtn != null) addAssignedLaborBtn.setDisable(!enabled);

        if (todayLaborTable != null) todayLaborTable.setDisable(!enabled);

        if (materialCostField != null) materialCostField.setDisable(!enabled);
        if (remainTask != null) { remainTask.setDisable(true); remainTask.setEditable(false); }
        if (completedTask != null) completedTask.setDisable(!enabled);

        if (progressContainer != null) progressContainer.setDisable(!enabled);
        if (addProgressBtn != null) addProgressBtn.setDisable(!enabled);

        if (generalRemark != null) generalRemark.setDisable(!enabled);

        if (issuesContainer != null) issuesContainer.setDisable(!enabled);
        if (addIssuesBtn != null) addIssuesBtn.setDisable(!enabled);

        if (submitReportBtn != null) submitReportBtn.setDisable(!enabled);
    }

    // ---------------- Setup: weather/date ----------------
    private void setupWeather() {
        if (weatherConditionBox == null) return;
        weatherConditionBox.setItems(FXCollections.observableArrayList(
                "Sunny", "Partly Cloudy", "Cloudy", "Light Rain", "Heavy Rain",
                "Thunderstorm", "Windy", "Foggy", "Other"
        ));
        weatherConditionBox.valueProperty().addListener((obs, o, n) -> {
            if (otherWeatherCondition == null) return;
            boolean isOther = "Other".equalsIgnoreCase(String.valueOf(n));
            otherWeatherCondition.setDisable(!isOther);
            if (!isOther) otherWeatherCondition.clear();
        });
        if (otherWeatherCondition != null) otherWeatherCondition.setDisable(true);
    }

    private void setupDatePickerNoFuture() {
        if (todayReportDate == null) return;
        todayReportDate.setDayCellFactory(dp -> new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) return;
                setDisable(item.isAfter(LocalDate.now()));
            }
        });
    }

    // ---------------- Numeric filters ----------------
    private void setupNumericFilters() {
        if (materialCostField != null) {
            materialCostField.setTextFormatter(new TextFormatter<>(change -> {
                String n = change.getControlNewText();
                if (n.isEmpty()) return change;
                return n.matches("\\d+") ? change : null;
            }));
        }
        addDecimalFilter(laborDailyWadgePerDay);
        addDecimalFilter(laborWorkHourPerDay);
        addDecimalFilter(completedTask);
    }

    private void addDecimalFilter(TextField tf) {
        if (tf == null) return;
        tf.setTextFormatter(new TextFormatter<>(change -> {
            String n = change.getControlNewText();
            if (n.isEmpty()) return change;
            return n.matches("\\d*(\\.\\d*)?") ? change : null;
        }));
    }

    // ---------------- Labor table ----------------
    private void setupLaborTable() {
        if (laborNameCol != null) laborNameCol.setCellValueFactory(new PropertyValueFactory<>("laborName"));
        if (laborSkillCol != null) laborSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (dailyWadgeCol != null) dailyWadgeCol.setCellValueFactory(new PropertyValueFactory<>("dailyWage"));
        if (workHourCol != null) workHourCol.setCellValueFactory(new PropertyValueFactory<>("workHours"));
        if (remarkCol != null) remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));

        if (actionCol != null) {
            actionCol.setCellFactory(col -> new TableCell<>() {
                private final Button edit = new Button("Edit");
                private final Button del  = new Button("Delete");
                private final HBox box = new HBox(8, edit, del);

                {
                    edit.setOnAction(e -> {
                        int idx = getIndex();
                        if (idx < 0 || idx >= getTableView().getItems().size()) return;
                        LaborRow row = getTableView().getItems().get(idx);
                        loadLaborRowToForm(row);
                    });
                    del.setOnAction(e -> {
                        int idx = getIndex();
                        if (idx < 0 || idx >= getTableView().getItems().size()) return;
                        LaborRow row = getTableView().getItems().get(idx);
                        laborRows.remove(row);
                    });
                }

                @Override protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : box);
                }
            });
        }
    }

    // ---------------- Labor Combo (editable, suggestion-only allowed) ----------------
    private void setupLaborNameCombo() {
        if (laborNameCombo == null) return;

        laborNameCombo.setEditable(false);   // ✅ choicebox style
        laborNameCombo.setDisable(true);     // until skill selected
        laborNameCombo.setVisibleRowCount(12);

        // converter only affects display text
        laborNameCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(LaborOption object) {
                return object == null ? "" : object.name();
            }
            @Override public LaborOption fromString(String string) { return null; }
        });

        // when user selects
        laborNameCombo.valueProperty().addListener((obs, o, n) -> {
            selectedLaborId = (n == null) ? null : n.laborId();
        });

        // when user opens dropdown, refresh list (top 20) for current skill
        laborNameCombo.setOnShowing(e -> {
            SkillOption skill = laborSkillCombo == null ? null : laborSkillCombo.getValue();
            if (skill == null) {
                laborNameCombo.getItems().clear();
                return;
            }
            laborNameCombo.getItems().setAll(queryLaborSuggestions(skill.skillId(), "")); // "" = no prefix, show list
        });
    }

    /**
     * Suggest labor if:
     *  - assigned to current project OR
     *  - not assigned anywhere OR
     *  - not assigned to any OTHER in-progress project
     */
    private List<LaborOption> queryLaborSuggestions(int skillId, String prefix) {
        List<LaborOption> list = new ArrayList<>();
        if (assignProjectId == null || assignProjectId <= 0) return list;
        String p = (prefix == null) ? "" : prefix.trim();



        String sql =
                "SELECT l.laborId, l.laborName " +
                        "FROM labors l " +
                        "WHERE l.skillId = ? " +
                        "  AND l.laborName LIKE ?" +
                        "  AND (" +
                        "        EXISTS (SELECT 1 FROM assignWorkers aw WHERE aw.workerId = l.laborId AND aw.assignProjectId = ?)" +
                        "        OR NOT EXISTS (SELECT 1 FROM assignWorkers aw WHERE aw.workerId = l.laborId)" +
                        "        OR NOT EXISTS (" +
                        "            SELECT 1 " +
                        "            FROM assignWorkers aw " +
                        "            JOIN assignProjects ap ON ap.assignProjectId = aw.assignProjectId " +
                        "            WHERE aw.workerId = l.laborId " +
                        "              AND aw.assignProjectId <> ? " +
                        "              AND ap.projectStatus = ?" +
                        "        )" +
                        "      ) " +
                        "ORDER BY " +
                        "  (EXISTS(SELECT 1 FROM assignWorkers aw WHERE aw.workerId = l.laborId AND aw.assignProjectId = ?)) DESC, " +
                        "  l.laborName " +
                        "LIMIT 20";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, skillId);
            ps.setString(2, (prefix == null ? "" : prefix.trim()) + "%");
            ps.setInt(3, assignProjectId);
            ps.setInt(4, assignProjectId);
            ps.setInt(5, STATUS_IN_PROGRESS);
            ps.setInt(6, assignProjectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LaborOption(rs.getInt("laborId"), rs.getString("laborName")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private void autoFillDailyWage() {
        if (laborDailyWadgePerDay == null) return;
        SkillOption so = laborSkillCombo == null ? null : laborSkillCombo.getValue();
        WorkItemOption wi = workItemCombo == null ? null : workItemCombo.getValue();
        if (so == null || wi == null) return;

        // assignWorkItemSkillDetails uses assignStatusId
        String sql =
                "SELECT awsd.dailyWagePerLabor " +
                        "FROM assignWorkItemSkills aws " +
                        "JOIN assignWorkItemSkillDetails awsd ON awsd.assignWorkItemSkillId = aws.assignWorkItemSkillId " +
                        "WHERE aws.assignWorkItemId = ? AND aws.skillId = ? AND awsd.assignStatusId = ? " +
                        "ORDER BY awsd.assignWorkItemSkillDetailId DESC LIMIT 1";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, wi.assignWorkItemId());
            ps.setInt(2, so.skillId());
            ps.setInt(3, ASSIGN_STATUS_AUTO);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    laborDailyWadgePerDay.setText(String.valueOf(rs.getDouble("dailyWagePerLabor")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ===================== FXML HANDLERS =====================

    @FXML
    private void addAssignedLabor(ActionEvent event) {
        SkillOption skill = laborSkillCombo == null ? null : laborSkillCombo.getValue();
        if (skill == null) { warn("Validation", "Please select a skill."); return; }
        if (selectedLaborId == null) { warn("Validation", "Please select a labor from suggestions."); return; }

        String name = "";
        if (laborNameCombo != null) {
            LaborOption sel = laborNameCombo.getValue();
            name = sel != null ? sel.name() : trim(laborNameCombo.getEditor().getText());
        }
        if (name.isEmpty()) { warn("Validation", "Please select a labor."); return; }

        double wage = parseDouble(trim(laborDailyWadgePerDay == null ? null : laborDailyWadgePerDay.getText()));
        double hours = parseDouble(trim(laborWorkHourPerDay == null ? null : laborWorkHourPerDay.getText()));
        if (wage <= 0) { warn("Validation", "Daily wage must be > 0."); return; }
        if (hours <= 0) { warn("Validation", "Work hours must be > 0."); return; }

        String remark = trim(remarkForLabor == null ? null : remarkForLabor.getText());

        boolean dup = laborRows.stream().anyMatch(r -> Objects.equals(r.getLaborId(), selectedLaborId));
        if (dup) { warn("Validation", "This labor is already added."); return; }

        laborRows.add(new LaborRow(selectedLaborId, name, skill.skillId(), skill.skillName(), wage, hours, remark));

        selectedLaborId = null;
        if (laborNameCombo != null) {
            laborNameCombo.getItems().clear();
            laborNameCombo.setValue(null);
            laborNameCombo.getEditor().clear();
            laborNameCombo.setDisable(true);
        }
        if (laborSkillCombo != null) laborSkillCombo.getSelectionModel().clearSelection();
        if (laborDailyWadgePerDay != null) laborDailyWadgePerDay.clear();
        if (laborWorkHourPerDay != null) laborWorkHourPerDay.clear();
        if (remarkForLabor != null) remarkForLabor.clear();
    }

    @FXML
    private void handleAddProgress(ActionEvent event) {
        TaskOption t = taskCombo == null ? null : taskCombo.getValue();
        if (t == null) { warn("Validation", "Please choose a task first."); return; }

        addProgressRow(null);
    }

    @FXML
    private void handleAddIssue(ActionEvent event) {
        addIssueRow(null);
    }

    @FXML
    private void submitReport(ActionEvent event) {
        if (assignProjectId == null || assignProjectId <= 0) {
            showError("No current project", "You don't have an in-progress project.");
            return;
        }

        WorkItemOption workItem = workItemCombo == null ? null : workItemCombo.getValue();
        if (workItem == null) { warn("Validation", "Please select a work item."); return; }

        LocalDate date = todayReportDate == null ? null : todayReportDate.getValue();
        if (date == null) { warn("Validation", "Please choose a date."); return; }
        if (date.isAfter(LocalDate.now())) { warn("Validation", "Future date is not allowed."); return; }

        String weather = weatherConditionBox == null ? "" : Objects.toString(weatherConditionBox.getValue(), "");
        if ("Other".equalsIgnoreCase(weather) && otherWeatherCondition != null) {
            String extra = trim(otherWeatherCondition.getText());
            if (!extra.isEmpty()) weather = "Other: " + extra;
        }

        String issuesText = collectIssues();
        String remark = trim(generalRemark == null ? null : generalRemark.getText());

        try (Connection con = databaseConnection.getConnection()) {
            con.setAutoCommit(false);


            int dailyReportId = getOrCreateDailyReportId(
                    con,
                    assignProjectId,
                    workItem.assignWorkItemId(),
                    date,
                    supervisorId == null ? 0 : supervisorId,
                    weather,
                    remark,
                    issuesText
            );

            deleteDailyReportTasks(con, dailyReportId);
            deleteDailyReportLabors(con, dailyReportId);
            for (LaborRow lr : laborRows) {
                Integer laborId = lr.getLaborId();
                if (laborId == null) continue;

                insertAssignWorkerIgnore(con, assignProjectId, laborId);
                callAddDailyReportLabor(con, dailyReportId, laborId, lr.workHours.get(), lr.dailyWage.get(), lr.remark.get());
            }

            // Tasks: single selected task + progress descriptions (same style as issues)
            TaskOption task = taskCombo == null ? null : taskCombo.getValue();
            if (task == null) {
                con.rollback();
                warn("Validation", "Please choose a task.");
                return;
            }

            String progressDesc = collectProgressDescriptions();
            double completedQty = parseDouble(trim(completedTask == null ? null : completedTask.getText()));
            double dailyCost = parseDouble(trim(materialCostField == null ? null : materialCostField.getText()));
            double workHours = 0; // (you can add a dedicated task-hours field later)

            double remain = parseDouble(trim(remainTask == null ? null : remainTask.getText()));
            boolean isCompleted = remain > 0 && completedQty >= remain;

            deleteDailyReportTasks(con, dailyReportId);
            // OLD
// OLD
// callAddDailyReportTask(con, dailyReportId, task.assignTaskId(), ...);

// NEW
            deleteDailyReportTasks(con, dailyReportId); // keep this if you want only 1 task row per report
            insertDailyReportTask(con, dailyReportId, task.assignTaskId(),
                    progressDesc, workHours, completedQty, dailyCost, isCompleted);


            con.commit();
            info("Saved", "Daily report saved successfully.");

            laborRows.clear();
            if (progressContainer != null) {
                progressContainer.getChildren().clear();
                addProgressRow(null);
            }
            refreshRemainQty();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Save Error", ex.getMessage());
        }
    }

    // ---------------- Progress UI ----------------
    private void addIssueRow(String initial) {
        if (issuesContainer == null) return;

        TextArea ta = new TextArea();
        ta.setWrapText(true);
        ta.setPrefRowCount(2);
        ta.setPromptText("Issue / risk...");
        if (initial != null) ta.setText(initial);

        Button remove = new Button("Remove");
        HBox box = new HBox(10, ta, remove);
        box.setPadding(new Insets(5));

        remove.setOnAction(e -> {
            issuesContainer.getChildren().remove(box);
            if (issuesContainer.getChildren().isEmpty()) addIssueRow(null);
        });

        issuesContainer.getChildren().add(box);
    }

    // Progress description rows (same style as issues): "#" + TextField + Remove
    // Progress description rows: "#" + TextArea (wrap) + Remove
    private void addProgressRow(String initial) {
        if (progressContainer == null) return;

        Label hash = new Label("#");
        hash.setMinWidth(18);
        hash.setStyle("-fx-font-weight: bold;");

        TextArea ta = new TextArea();
        ta.setWrapText(true);
        ta.setPromptText("Progress description...");
        ta.setPrefRowCount(2);
        ta.setMinHeight(50);
        if (initial != null) ta.setText(initial);

        Button remove = new Button("Remove");

        HBox row = new HBox(10, hash, ta, remove);
        row.setPadding(new Insets(5));
        HBox.setHgrow(ta, javafx.scene.layout.Priority.ALWAYS);

        remove.setOnAction(e -> {
            progressContainer.getChildren().remove(row);

            // keep at least one progress row above the button
            boolean hasRow = progressContainer.getChildren().stream()
                    .anyMatch(n -> n instanceof HBox hb && hb != progressAddRow);
            if (!hasRow) addProgressRow(null);
        });

        // insert above the add button row
        int idx = (progressAddRow == null)
                ? progressContainer.getChildren().size()
                : progressContainer.getChildren().indexOf(progressAddRow);

        if (idx < 0) idx = progressContainer.getChildren().size();
        progressContainer.getChildren().add(idx, row);
    }



    private String collectProgressDescriptions() {
        if (progressContainer == null) return "";
        StringBuilder sb = new StringBuilder();

        for (var n : progressContainer.getChildren()) {
            if (n instanceof HBox hb) {
                for (var c : hb.getChildren()) {
                    if (c instanceof TextArea ta) {
                        String t = trim(ta.getText());
                        if (!t.isEmpty()) {
                            if (sb.length() > 0) sb.append("\n");
                            sb.append(t);
                        }
                    }
                }
            }
        }
        return sb.toString();
    }


    // ---------------- Remain qty ----------------
    private void refreshRemainQty() {
        if (remainTask != null) remainTask.setText("0");
        TaskOption t = taskCombo == null ? null : taskCombo.getValue();
        if (t == null) return;

        String sql =
                "SELECT at.plannedQty - COALESCE((" +
                        "   SELECT SUM(drt.completedQty) " +
                        "   FROM dailyReportTasks drt " +
                        "   JOIN dailyReports dr ON dr.dailyReportId = drt.dailyReportId " +
                        "   WHERE drt.assignTaskId = at.assignTaskId" +
                        "), 0) AS remainQty " +
                        "FROM assignTasks at WHERE at.assignTaskId = ?";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.assignTaskId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && remainTask != null) {
                    remainTask.setText(String.valueOf(rs.getDouble("remainQty")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- Save helpers ----------------
    private int getOrCreateDailyReportId(Connection con,
                                         int pid, int wid, LocalDate date,
                                         int supervisorId,
                                         String weather, String generalRemark, String issueText) throws SQLException {

        String sql =
                "INSERT INTO dailyReports(assignProjectId, assignWorkItemId, reportDate, supervisorId, weather, generalRemark, issue) " +
                        "VALUES(?,?,?,?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "  supervisorId=VALUES(supervisorId), " +
                        "  weather=VALUES(weather), " +
                        "  generalRemark=VALUES(generalRemark), " +
                        "  issue=VALUES(issue), " +
                        "  dailyReportId = LAST_INSERT_ID(dailyReportId)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDate(3, Date.valueOf(date));
            ps.setInt(4, supervisorId);
            ps.setString(5, weather);
            ps.setString(6, generalRemark);
            ps.setString(7, issueText);

            ps.executeUpdate();

            // For MySQL, LAST_INSERT_ID trick makes generatedKeys return the existing id too.
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        // Fallback (should rarely happen)
        String find = "SELECT dailyReportId FROM dailyReports WHERE assignProjectId=? AND assignWorkItemId=? AND reportDate=? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(find)) {
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDate(3, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("dailyReportId");
            }
        }

        throw new SQLException("Failed to create/find dailyReportId");
    }


    private void deleteDailyReportLabors(Connection con, int dailyReportId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM dailyReportLabors WHERE dailyReportId=?")) {
            ps.setInt(1, dailyReportId);
            ps.executeUpdate();
        }
    }


    private void insertDailyReportLabor(Connection con, int dailyReportId, int laborId,
                                        double workHours, double dailyWage, String remark) throws SQLException {

        String sql =
                "INSERT INTO dailyReportLabors(dailyReportId, laborId, workHours, dailyWage, remark) " +
                        "VALUES(?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ps.setInt(2, laborId);
            ps.setDouble(3, workHours);
            ps.setDouble(4, dailyWage);
            ps.setString(5, remark);
            ps.executeUpdate();
        }
    }

    private void insertDailyReportTask(Connection con,
                                       int dailyReportId, int assignTaskId,
                                       String progressDesc,
                                       double workHours, double completedQty,
                                       double dailyCost, boolean isCompleted) throws SQLException {

        String sql =
                "INSERT INTO dailyReportTasks(dailyReportId, assignTaskId, progressDescription, workHours, completedQty, dailyCost, isCompleted) " +
                        "VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ps.setInt(2, assignTaskId);
            ps.setString(3, progressDesc);
            ps.setDouble(4, workHours);
            ps.setDouble(5, completedQty);
            ps.setDouble(6, dailyCost);
            ps.setBoolean(7, isCompleted);
            ps.executeUpdate();
        }
    }


    private void callAddDailyReportLabor(Connection con, int dailyReportId, int laborId,
                                         double workHours, double dailyWage, String remark) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{CALL addDailyReportLabor(?,?,?,?,?)}")) {
            cs.setInt(1, dailyReportId);
            cs.setInt(2, laborId);
            cs.setDouble(3, workHours);
            cs.setDouble(4, dailyWage);
            cs.setString(5, remark);
            cs.execute();
        }
    }

    private void deleteDailyReportTasks(Connection con, int dailyReportId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM dailyReportTasks WHERE dailyReportId=?")) {
            ps.setInt(1, dailyReportId);
            ps.executeUpdate();
        }
    }

    private void insertAssignWorkerIgnore(Connection con, int pid, int laborId) throws SQLException {
        String check = "SELECT 1 FROM assignWorkers WHERE assignProjectId=? AND workerId=? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(check)) {
            ps.setInt(1, pid);
            ps.setInt(2, laborId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return;
            }
        }
        String ins = "INSERT INTO assignWorkers(assignProjectId, workerId) VALUES(?,?)";
        try (PreparedStatement ps = con.prepareStatement(ins)) {
            ps.setInt(1, pid);
            ps.setInt(2, laborId);
            ps.executeUpdate();
        }
    }

    // ---------------- Combo loads ----------------
    private void loadWorkItemsForProject() {
        if (workItemCombo == null) return;
        workItemCombo.getItems().clear();
        if (assignProjectId == null || assignProjectId <= 0) return;

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
        } catch (Exception ex) { ex.printStackTrace(); }
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
        } catch (Exception ex) { ex.printStackTrace(); }

        if (!taskCombo.getItems().isEmpty()) taskCombo.getSelectionModel().selectFirst();
    }

    private void loadSkillsForSelectedWorkItem() {
        if (laborSkillCombo == null) return;
        laborSkillCombo.getItems().clear();
        WorkItemOption wi = workItemCombo == null ? null : workItemCombo.getValue();
        if (wi == null) return;

        String sql =
                "SELECT DISTINCT aws.skillId, s.skillName " +
                        "FROM assignWorkItemSkills aws " +
                        "JOIN skills s ON s.skillId = aws.skillId " +
                        "WHERE aws.assignWorkItemId = ? " +
                        "ORDER BY s.skillName";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, wi.assignWorkItemId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    laborSkillCombo.getItems().add(new SkillOption(rs.getInt("skillId"), rs.getString("skillName")));
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        selectedLaborId = null;
        if (laborNameCombo != null) {
            laborNameCombo.getItems().clear();
            laborNameCombo.setValue(null);
            laborNameCombo.getEditor().clear();
            laborNameCombo.setDisable(true);
        }
        if (laborDailyWadgePerDay != null) laborDailyWadgePerDay.clear();
    }

    // ---------------- Misc utils ----------------
    private void loadLaborRowToForm(LaborRow row) {
        if (row == null) return;
        laborRows.remove(row);

        if (laborSkillCombo != null) {
            for (SkillOption so : laborSkillCombo.getItems()) {
                if (so.skillId() == row.skillId.get()) {
                    laborSkillCombo.getSelectionModel().select(so);
                    break;
                }
            }
        }

        selectedLaborId = row.getLaborId();
        if (laborNameCombo != null) {
            laborNameCombo.setDisable(false);
            LaborOption lo = new LaborOption(selectedLaborId == null ? -1 : selectedLaborId, row.laborName.get());
            laborNameCombo.getItems().setAll(lo);
            laborNameCombo.setValue(lo);
            laborNameCombo.getEditor().setText(lo.name());
        }
        if (laborDailyWadgePerDay != null) laborDailyWadgePerDay.setText(String.valueOf(row.dailyWage.get()));
        if (laborWorkHourPerDay != null) laborWorkHourPerDay.setText(String.valueOf(row.workHours.get()));
        if (remarkForLabor != null) remarkForLabor.setText(row.remark.get());
    }

    private String collectIssues() {
        if (issuesContainer == null) return "";
        StringBuilder sb = new StringBuilder();
        for (var n : issuesContainer.getChildren()) {
            if (n instanceof HBox hb) {
                for (var c : hb.getChildren()) {
                    if (c instanceof TextArea ta) {
                        String t = trim(ta.getText());
                        if (!t.isEmpty()) {
                            if (sb.length() > 0) sb.append("\n");
                            sb.append(t);
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private static double parseDouble(String s) {
        if (s == null) return 0;
        String t = s.trim();
        if (t.isEmpty()) return 0;
        try { return Double.parseDouble(t); } catch (Exception e) { return 0; }
    }

    private void warn(String title, String msg) { alert(Alert.AlertType.WARNING, title, msg); }
    private void info(String title, String msg) { alert(Alert.AlertType.INFORMATION, title, msg); }
    private void showError(String title, String msg) { alert(Alert.AlertType.ERROR, title, msg); }

    private void alert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }

    // ---------------- Option + Row Types ----------------
    public record WorkItemOption(int assignWorkItemId, String name) {
        @Override public String toString() { return name; }
    }
    public record TaskOption(int assignTaskId, String taskName) {
        @Override public String toString() { return taskName; }
    }
    public record SkillOption(int skillId, String skillName) {
        @Override public String toString() { return skillName; }
    }

    public record LaborOption(int laborId, String name) {
        @Override public String toString() { return name; }
    }

    public static class LaborRow {
        public final IntegerProperty laborId = new SimpleIntegerProperty();
        public final StringProperty laborName = new SimpleStringProperty();
        public final IntegerProperty skillId = new SimpleIntegerProperty();
        public final StringProperty skillName = new SimpleStringProperty();
        public final DoubleProperty dailyWage = new SimpleDoubleProperty();
        public final DoubleProperty workHours = new SimpleDoubleProperty();
        public final StringProperty remark = new SimpleStringProperty();

        public LaborRow(Integer laborId, String laborName, int skillId, String skillName,
                        double dailyWage, double workHours, String remark) {
            this.laborId.set(laborId == null ? -1 : laborId);
            this.laborName.set(laborName);
            this.skillId.set(skillId);
            this.skillName.set(skillName);
            this.dailyWage.set(dailyWage);
            this.workHours.set(workHours);
            this.remark.set(remark);
        }

        public Integer getLaborId() { int v = laborId.get(); return v <= 0 ? null : v; }
        public String getLaborName() { return laborName.get(); }
        public String getSkillName() { return skillName.get(); }
        public double getDailyWage() { return dailyWage.get(); }
        public double getWorkHours() { return workHours.get(); }
        public String getRemark() { return remark.get(); }
    }



}
