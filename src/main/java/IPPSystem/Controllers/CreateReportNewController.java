package IPPSystem.Controllers;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Recompiled controller that matches your FXML ids (CreateReportNewController.java)
 * and includes the daily-report flow we discussed.
 *
 * Usage from your project/work-item selection screen:
 *   controller.setContext(assignProjectId, assignWorkItemId, supervisorId, projectName);
 *
 * Notes:
 *  - This uses dailyReport.issue for Issues text, and dailyReport.generalRemark for Comments text.
 *  - Progress rows are stored temporarily in dailyReportTasks.remark (until you wire real tasks).
 */
public class CreateReportNewController {

    // ---------------- FXML ----------------
    @FXML private Button addCommentsBtn;
    @FXML private Button addIssuesBtn;
    @FXML private Button addProgressBtn;
    @FXML private Button btnAddLabor;

    @FXML private TableColumn<LaborRow, String> colId;
    @FXML private TableColumn<LaborRow, String> colSkill;
    @FXML private TableColumn<LaborRow, Number> colWage;
    @FXML private TableColumn<LaborRow, Number> colHours;
    @FXML private TableColumn<LaborRow, String> colRemarks;

    @FXML private VBox commentsContainer;
    @FXML private VBox issuesContainer;
    @FXML private TableView<LaborRow> laborTable;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox progressContainer;

    @FXML private TextField projectNameField1;
    @FXML private DatePicker reportDatePicker;
    @FXML private Button submitReportBtn;
    @FXML private ComboBox<String> weatherTypeComboBox;

    // ---------------- STATE ----------------
    private int assignProjectId = -1;
    private int assignWorkItemId = -1;
    private int supervisorId = 1;
    private Integer currentDailyReportId = null;

    private int progressCount = 0;
    private int issuesCount = 0;
    private int commentsCount = 0;

    private final ObservableList<LaborRow> laborRows = FXCollections.observableArrayList();

    // ---------------- DB CONFIG (change or override with setDbConfig) ----------------
    private String jdbcUrl  = "jdbc:mysql://localhost:3306/ippSystemDatabase?useSSL=false&serverTimezone=UTC";
    private String jdbcUser = "root";
    private String jdbcPass = "root";

    @FXML
    private void initialize() {
        // default date
        if (reportDatePicker != null) {
            reportDatePicker.setValue(LocalDate.now());
            reportDatePicker.valueProperty().addListener((obs, o, n) -> loadOrClear());
        }

        // weather list
        if (weatherTypeComboBox != null) {
            weatherTypeComboBox.setItems(FXCollections.observableArrayList(
                    "Sunny", "Cloudy", "Rainy", "Windy", "Stormy", "Foggy", "Other"
            ));
        }

        // labor table
        if (laborTable != null) laborTable.setItems(laborRows);
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("laborName"));
        if (colSkill != null) colSkill.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (colWage != null) colWage.setCellValueFactory(new PropertyValueFactory<>("dailyWage"));
        if (colHours != null) colHours.setCellValueFactory(new PropertyValueFactory<>("workHours"));
        if (colRemarks != null) colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        // ensure at least 1 issue/comment row
        if (issuesContainer != null && issuesContainer.getChildren().isEmpty()) addIssueRow(null);
        if (commentsContainer != null && commentsContainer.getChildren().isEmpty()) addCommentRow(null);
    }

    // ---------------- PUBLIC API ----------------
    public void setDbConfig(String url, String user, String pass) {
        this.jdbcUrl = url;
        this.jdbcUser = user;
        this.jdbcPass = pass;
    }

    public void setContext(int assignProjectId, int assignWorkItemId, int supervisorId, String projectName) {
        this.assignProjectId = assignProjectId;
        this.assignWorkItemId = assignWorkItemId;
        this.supervisorId = supervisorId;
        if (projectNameField1 != null) projectNameField1.setText(Objects.requireNonNullElse(projectName, ""));
        loadOrClear();
    }

    public void addLaborToTable(String laborName, String skillName, double dailyWage, double hours, String remarks) {
        laborRows.add(new LaborRow(laborName, skillName, dailyWage, hours, remarks));
    }

    // ---------------- FLOW ----------------
    private void loadOrClear() {
        if (assignProjectId <= 0 || assignWorkItemId <= 0 || reportDatePicker == null || reportDatePicker.getValue() == null) {
            clearUiForNewReport();
            return;
        }

        try (Connection con = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            DailyReportLoaded loaded = loadDailyReportByKey(con, assignProjectId, assignWorkItemId, reportDatePicker.getValue());
            if (loaded == null) {
                clearUiForNewReport();
                return;
            }

            currentDailyReportId = loaded.dailyReportId;
            if (weatherTypeComboBox != null) weatherTypeComboBox.setValue(loaded.weather);

            // issues
            if (issuesContainer != null) {
                issuesContainer.getChildren().clear();
                issuesCount = 0;
                if (loaded.issuesText != null && !loaded.issuesText.isBlank()) {
                    for (String line : loaded.issuesText.split("\\r?\\n")
                    ) addIssueRow(line.trim());
                } else {
                    addIssueRow(null);
                }
            }

            // comments
            if (commentsContainer != null) {
                commentsContainer.getChildren().clear();
                commentsCount = 0;
                if (loaded.commentsText != null && !loaded.commentsText.isBlank()) {
                    for (String line : loaded.commentsText.split("\\r?\\n")) addCommentRow(line.trim());
                } else {
                    addCommentRow(null);
                }
            }

            // labors
            laborRows.setAll(loaded.labors);

            // progress
            if (progressContainer != null) {
                progressContainer.getChildren().clear();
                progressCount = 0;
                for (String p : loaded.progressLines) addProgressRow(p);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", ex.getMessage());
        }
    }

    private void clearUiForNewReport() {
        currentDailyReportId = null;
        if (weatherTypeComboBox != null) weatherTypeComboBox.setValue(null);
        laborRows.clear();

        if (progressContainer != null) {
            progressContainer.getChildren().clear();
            progressCount = 0;
        }

        if (issuesContainer != null) {
            issuesContainer.getChildren().clear();
            issuesCount = 0;
            addIssueRow(null);
        }

        if (commentsContainer != null) {
            commentsContainer.getChildren().clear();
            commentsCount = 0;
            addCommentRow(null);
        }
    }

    // ---------------- UI: Progress / Issues / Comments ----------------
    @FXML
    private void handleAddProgress() {
        addProgressRow(null);
    }

    private void addProgressRow(String initialText) {
        progressCount++;

        HBox rowContainer = new HBox(10);
        rowContainer.setStyle("-fx-alignment: CENTER_LEFT; -fx-spacing: 10px;");

        TextField textField = new TextField();
        textField.setPromptText("Enter progress description " + progressCount + "...");
        textField.setPrefWidth(400);
        textField.setMinWidth(400);
        textField.setMaxWidth(400);
        textField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        if (initialText != null) textField.setText(initialText);

        Button cancelBtn = new Button("X");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-border-color: black; -fx-border-radius: 10px;");
        cancelBtn.setPrefWidth(100);

        cancelBtn.setOnAction(e -> {
            rowContainer.setMinHeight(0);
            rowContainer.setPrefHeight(0);
            rowContainer.setMaxHeight(0);
            rowContainer.setVisible(false);
            rowContainer.setManaged(false);

            PauseTransition pause = new PauseTransition(Duration.millis(50));
            pause.setOnFinished(event -> progressContainer.getChildren().remove(rowContainer));
            pause.play();
        });

        rowContainer.getChildren().addAll(textField, cancelBtn);
        if (progressContainer != null) progressContainer.getChildren().add(rowContainer);
    }

    @FXML
    private void handleAddIssue() {
        addIssueRow(null);
    }

    private void addIssueRow(String initialText) {
        issuesCount++;

        HBox rowContainer = new HBox(10);
        rowContainer.setStyle("-fx-alignment: CENTER_LEFT;");

        TextField textField = new TextField();
        textField.setPromptText("Describe issue/risk " + issuesCount + "...");
        textField.setPrefWidth(400);
        textField.setMinWidth(400);
        textField.setMaxWidth(400);
        if (initialText != null) textField.setText(initialText);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-border-color: black; -fx-border-radius: 10px;");
        cancelBtn.setOnAction(e -> issuesContainer.getChildren().remove(rowContainer));

        rowContainer.getChildren().addAll(textField, cancelBtn);
        if (issuesContainer != null) issuesContainer.getChildren().add(rowContainer);
    }

    @FXML
    private void handleAddComment() {
        addCommentRow(null);
    }

    private void addCommentRow(String initialText) {
        commentsCount++;

        HBox rowContainer = new HBox(10);
        rowContainer.setStyle("-fx-alignment: CENTER_LEFT;");

        TextField textField = new TextField();
        textField.setPromptText("Enter comment " + commentsCount + "...");
        textField.setPrefWidth(400);
        textField.setMinWidth(400);
        textField.setMaxWidth(400);
        if (initialText != null) textField.setText(initialText);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-border-color: black; -fx-border-radius: 10px;");
        cancelBtn.setOnAction(e -> commentsContainer.getChildren().remove(rowContainer));

        rowContainer.getChildren().addAll(textField, cancelBtn);
        if (commentsContainer != null) commentsContainer.getChildren().add(rowContainer);
    }

    // ---------------- SAVE ----------------
    @FXML
    private void handleSubmitReport() {
        if (assignProjectId <= 0 || assignWorkItemId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Missing Context", "Please set assignProjectId and assignWorkItemId first.");
            return;
        }
        if (reportDatePicker == null || reportDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Date", "Please choose a report date.");
            return;
        }

        String weather = weatherTypeComboBox == null ? "" : Objects.requireNonNullElse(weatherTypeComboBox.getValue(), "");
        String issuesText = collectTextLines(issuesContainer);
        String commentsText = collectTextLines(commentsContainer);
        List<String> progressLines = collectProgressLines(progressContainer);

        try (Connection con = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            con.setAutoCommit(false);

            DailyReportHeaderResult hdr = getOrCreateDailyReport(con, assignProjectId, assignWorkItemId, reportDatePicker.getValue(), supervisorId,
                    weather, issuesText, commentsText);

            // only for NEW report -> insert labors + assignWorkers (skip duplicates)
            if (hdr.isNew) {
                for (LaborRow lr : laborRows) {
                    Integer laborId = findLaborIdByName(con, lr.getLaborName());
                    if (laborId == null) {
                        con.rollback();
                        showAlert(Alert.AlertType.ERROR, "Labor Not Found",
                                "Cannot find laborId for laborName='" + lr.getLaborName() + "'.\n" +
                                        "Implement labor picker or change findLaborIdByName logic.");
                        return;
                    }
                    insertAssignWorkerIgnore(con, assignProjectId, laborId);
                    insertDailyReportLaborIgnore(con, hdr.dailyReportId, laborId, lr.getDailyWage(), lr.getWorkHours(), lr.getRemarks());
                }
            }

            // always replace progress rows (temporary)
            replaceDailyReportProgressText(con, hdr.dailyReportId, progressLines);

            con.commit();
            currentDailyReportId = hdr.dailyReportId;

            showAlert(Alert.AlertType.INFORMATION, "Saved", hdr.isNew
                    ? "Daily report created and saved."
                    : "Daily report already existed. Labors skipped; progress updated.");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", ex.getMessage());
        }
    }

    // ---------------- DB ----------------
    private DailyReportLoaded loadDailyReportByKey(Connection con, int pid, int wid, LocalDate date) throws SQLException {
        String hdrSql = "SELECT dailyReportId, weather, issue, generalRemark FROM dailyReport WHERE assignProjectId=? AND assignWorkItemId=? AND reportDate=? LIMIT 1";

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

        ObservableList<LaborRow> labors = FXCollections.observableArrayList();
        String laborSql = "SELECT l.laborName, s.skillName, drl.dailyWage, drl.workHour, drl.remark " +
                "FROM dailyReportLabors drl JOIN labors l ON l.laborId = drl.laborId JOIN skills s ON s.skillId = l.skillId " +
                "WHERE drl.dailyReportId = ? ORDER BY l.laborName";
        try (PreparedStatement ps = con.prepareStatement(laborSql)) {
            ps.setInt(1, dailyReportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    labors.add(new LaborRow(
                            rs.getString("laborName"),
                            rs.getString("skillName"),
                            rs.getDouble("dailyWage"),
                            rs.getDouble("workHour"),
                            rs.getString("remark")
                    ));
                }
            }
        }

        List<String> progressLines = new ArrayList<>();
        String progSql = "SELECT remark FROM dailyReportTasks WHERE dailyReportId = ? ORDER BY dailyReportTaskId";
        try (PreparedStatement ps = con.prepareStatement(progSql)) {
            ps.setInt(1, dailyReportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String line = rs.getString("remark");
                    if (line != null && !line.isBlank()) progressLines.add(line);
                }
            }
        }

        return new DailyReportLoaded(dailyReportId, weather, issues, comments, labors, progressLines);
    }

    private DailyReportHeaderResult getOrCreateDailyReport(Connection con,
                                                           int pid,
                                                           int wid,
                                                           LocalDate date,
                                                           int supervisorId,
                                                           String weather,
                                                           String issuesText,
                                                           String commentsText) throws SQLException {
        String findSql = "SELECT dailyReportId FROM dailyReport WHERE assignProjectId=? AND assignWorkItemId=? AND reportDate=? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(findSql)) {
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDate(3, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new DailyReportHeaderResult(rs.getInt(1), false);
            }
        }

        String insSql = "INSERT INTO dailyReport(assignProjectId, assignWorkItemId, reportDate, supervisorId, weather, issue, generalRemark) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(insSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pid);
            ps.setInt(2, wid);
            ps.setDate(3, Date.valueOf(date));
            ps.setInt(4, supervisorId);
            ps.setString(5, weather);
            ps.setString(6, issuesText);
            ps.setString(7, commentsText);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new DailyReportHeaderResult(keys.getInt(1), true);
            }
        }
        throw new SQLException("Failed to create daily report (no generated key)." );
    }

    private void replaceDailyReportProgressText(Connection con, int dailyReportId, List<String> progressLines) throws SQLException {
        try (PreparedStatement del = con.prepareStatement("DELETE FROM dailyReportTasks WHERE dailyReportId = ?")) {
            del.setInt(1, dailyReportId);
            del.executeUpdate();
        }

        String ins = "INSERT INTO dailyReportTasks(dailyReportId, remark) VALUES(?,?)";
        try (PreparedStatement ps = con.prepareStatement(ins)) {
            for (String line : progressLines) {
                String v = line == null ? "" : line.trim();
                if (v.isEmpty()) continue;
                ps.setInt(1, dailyReportId);
                ps.setString(2, v);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertAssignWorkerIgnore(Connection con, int pid, int laborId) throws SQLException {
        String sql = "INSERT IGNORE INTO assignWorkers(assignProjectId, laborId) VALUES(?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pid);
            ps.setInt(2, laborId);
            ps.executeUpdate();
        }
    }

    private void insertDailyReportLaborIgnore(Connection con, int dailyReportId, int laborId, double dailyWage, double workHour, String remark) throws SQLException {
        String sql = "INSERT IGNORE INTO dailyReportLabors(dailyReportId, laborId, dailyWage, workHour, remark) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ps.setInt(2, laborId);
            ps.setDouble(3, dailyWage);
            ps.setDouble(4, workHour);
            ps.setString(5, remark);
            ps.executeUpdate();
        }
    }

    private Integer findLaborIdByName(Connection con, String laborName) throws SQLException {
        String sql = "SELECT laborId FROM labors WHERE laborName = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, laborName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }

    // ---------------- collectors + alert ----------------
    private static String collectTextLines(VBox container) {
        if (container == null) return "";
        List<String> lines = new ArrayList<>();
        for (Node n : container.getChildren()) {
            if (n instanceof HBox hb) {
                for (Node child : hb.getChildren()) {
                    if (child instanceof TextField tf) {
                        String v = tf.getText() == null ? "" : tf.getText().trim();
                        if (!v.isEmpty()) lines.add(v);
                    }
                }
            }
        }
        return String.join("\n", lines);
    }

    private static List<String> collectProgressLines(VBox container) {
        List<String> lines = new ArrayList<>();
        if (container == null) return lines;
        for (Node n : container.getChildren()) {
            if (n instanceof HBox hb) {
                for (Node child : hb.getChildren()) {
                    if (child instanceof TextField tf) {
                        String v = tf.getText() == null ? "" : tf.getText().trim();
                        if (!v.isEmpty()) lines.add(v);
                    }
                }
            }
        }
        return lines;
    }

    private static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // ---------------- models ----------------
    public static class LaborRow {
        private final StringProperty laborName = new SimpleStringProperty();
        private final StringProperty skillName = new SimpleStringProperty();
        private final DoubleProperty dailyWage = new SimpleDoubleProperty();
        private final DoubleProperty workHours = new SimpleDoubleProperty();
        private final StringProperty remarks = new SimpleStringProperty();

        public LaborRow(String laborName, String skillName, double dailyWage, double workHours, String remarks) {
            this.laborName.set(Objects.requireNonNullElse(laborName, ""));
            this.skillName.set(Objects.requireNonNullElse(skillName, ""));
            this.dailyWage.set(dailyWage);
            this.workHours.set(workHours);
            this.remarks.set(Objects.requireNonNullElse(remarks, ""));
        }

        public String getLaborName() { return laborName.get(); }
        public String getSkillName() { return skillName.get(); }
        public double getDailyWage() { return dailyWage.get(); }
        public double getWorkHours() { return workHours.get(); }
        public String getRemarks() { return remarks.get(); }
    }

    private record DailyReportHeaderResult(int dailyReportId, boolean isNew) {}

    private record DailyReportLoaded(
            int dailyReportId,
            String weather,
            String issuesText,
            String commentsText,
            ObservableList<LaborRow> labors,
            List<String> progressLines
    ) {}
}
