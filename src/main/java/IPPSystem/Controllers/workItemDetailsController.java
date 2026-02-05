package IPPSystem.Controllers;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.Models.*;
import IPPSystem.Utils.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;


public class workItemDetailsController implements loadPaneAware {

//    ==== title ====
    @FXML private Label workItemTitle,workItemTitleStatus;
    // ===== Top cards =====
    @FXML private Label actualCost;
    @FXML private Label actualCostPercent;
    @FXML private ProgressBar actualCostProgress;

    @FXML private Label earnValuePercent;
    @FXML private ProgressBar earnValueProgress;

    @FXML private Label totalCost;
    @FXML private Label totalEarnValue;
    @FXML private Label usedEarnValue;

    // ===== SPI / CPI =====
    @FXML private Label spiStatusLbl;
    @FXML private Circle spiCircle;
    @FXML private Label spiCircleRate;

    @FXML private Label cpiStatusLbl;
    @FXML private Circle cpiCircle;
    @FXML private Label cpiCircleRate;

    // ===== View-only WorkItem info =====
    @FXML private VBox viewOnlyWorkItemInfo;
    @FXML private Label viewBudget;
    @FXML private Label viewPlanStartDate;
    @FXML private Label viewPlanEndDate;
    @FXML private Label viewActualStartDate;
    @FXML private Label viewActualEndDate;
    @FXML private Label viewDuration;
    @FXML private Label viewTotalLabors;

    // ===== Skill + Task tables =====
    @FXML private TableView<skills> viewSkillTable;
    @FXML private TableColumn<skills, String> viewSkillCol;
    @FXML private TableColumn<skills, Double> viewQtyCol;

    // ===== Task table =====
    @FXML private TableView<tasks> taskTable;
    @FXML private TableColumn<tasks, String> taskNameCol;
    @FXML private TableColumn<tasks, Double> taskDurationCol;
    @FXML private TableColumn<tasks, Date> taskPlanStartDateCol;
    @FXML private TableColumn<tasks, Date> taskPlanEndDateCol;
    @FXML private TableColumn<tasks, Date> taskActualStartDate;
    @FXML private TableColumn<tasks, Date> taskActualEndDateCol;
    @FXML private TableColumn<tasks, String> TaskStatusCol;
    @FXML private TableColumn<tasks, Void> taskActionCol;

    // In your FXML: fx:id="backToProjectDetails"
    @FXML private Button backToProjectDetails;

    @FXML private Button wItemEditBtn;

    private workItems workItem;
    private final calculationHelper helper = calculationHelper.getInstance();
    private boolean canEditTasks = true;

    private StackPane loadPane;

    private users loginUser = session.getInstance().getUser();
    private projects project;
    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }


    // Flexible date parser: supports "2026-01-14" and "14-JAN-2026"
    private static final DateTimeFormatter FLEX_DATE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE) // yyyy-MM-dd
            .appendOptional(DateTimeFormatter.ofPattern("d-MMM-uuuu", Locale.ENGLISH))
            .appendOptional(DateTimeFormatter.ofPattern("dd-MMM-uuuu", Locale.ENGLISH))
            .toFormatter(Locale.ENGLISH);

    @FXML
    public void initialize() {
        wItemEditBtn.setGraphic(utils.iconSet(FontAwesomeSolid.EDIT));
        wItemEditBtn.setOnAction(e->{
            messageBoxService.toast("Not Allow To Edit",
                    "If you want to edit buy Premium!!",
                    notificationType.WARNING);});

        // ---- Task table mapping ----
        if (taskNameCol != null) taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        if (taskDurationCol != null) taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("projectDuration"));
        if (taskPlanStartDateCol != null) taskPlanStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskPlanEndDateCol != null) taskPlanEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // Your tasks model currently mirrors planned into actual in UI
        if (taskActualStartDate != null) taskActualStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskActualEndDateCol != null) taskActualEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        if (TaskStatusCol != null) TaskStatusCol.setCellValueFactory(new PropertyValueFactory<>("projectStatus"));

        // ---- Skill table ----
        if (viewSkillCol != null) viewSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (viewQtyCol != null) viewQtyCol.setCellValueFactory(new PropertyValueFactory<>("projectLaborQty"));

        // Nice placeholders
        if (taskTable != null) taskTable.setPlaceholder(new Label("No tasks to show."));
        if (viewSkillTable != null) viewSkillTable.setPlaceholder(new Label("No skills assigned."));

        // ---- Action column: Edit button ----
        setupTaskActionColumn();

    }

    public void setWorkItem(workItems item, projects project) {
        this.workItem = item;
        this.project = project;
        if (item == null) return;

        // Supervisor can't edit (same style as your projectDetailsController)
        boolean isSupervisor = (loginUser != null)
                && role.SUPERVISOR.toString().equals(loginUser.getUserRole());
        this.canEditTasks = !isSupervisor;

        // Load tables (async to avoid UI freeze)
        final int assignWorkItemId = item.getAssignWorkItemId();

        javafx.concurrent.Task<java.lang.Object[]> loadTablesTask = new javafx.concurrent.Task<>() {
            @Override
            protected java.lang.Object[] call() {
                javafx.collections.ObservableList<IPPSystem.Models.tasks> tasksList =
                        database.getAllTasksByAssignWorkItem(assignWorkItemId);
                javafx.collections.ObservableList<IPPSystem.Models.skills> skillsList =
                        database.getAllSkillByAssignWorkItemDetails(assignWorkItemId);
                return new java.lang.Object[]{tasksList, skillsList};
            }
        };

        loadTablesTask.setOnSucceeded(ev -> {
            try {
                if (taskTable != null) taskTable.setItems((javafx.collections.ObservableList<IPPSystem.Models.tasks>) loadTablesTask.getValue()[0]);
                if (viewSkillTable != null) viewSkillTable.setItems((javafx.collections.ObservableList<IPPSystem.Models.skills>) loadTablesTask.getValue()[1]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadTablesTask.setOnFailed(ev -> {
            loadTablesTask.getException().printStackTrace();
            try {
                IPPSystem.Utils.messageBoxService.toast(
                        "Failed to load work item details",
                        String.valueOf(loadTablesTask.getException().getMessage()),
                        IPPSystem.Constants.notificationType.ERROR
                );
            } catch (Exception ignored) {}
        });

        new Thread(loadTablesTask, "load-workitem-tables").start();

        // Load baseline fields
        reloadFieldsFromModel();

        // Load EVM numbers (BAC/PV/EV/AC/CPI/SPI) from calculation helper
        loadDashboardAsync(item.getAssignWorkItemId(), LocalDate.now());
        // Navigate within the same tab (no need to store loadPane)
        backToProjectDetails.setOnAction(e -> utils.openProjectDetails(project, backToProjectDetails));

        workItemTitle.setText(item.getWorkItemName());
        workItemTitleStatus.setText("- "+item.getProjectStatus());
    }

    // ------------------------------------------------------------
    // Task action column
    // ------------------------------------------------------------
    private void setupTaskActionColumn() {
        if (taskActionCol == null) return;

        taskActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");

            {
                editBtn.setStyle("-fx-background-color:#4176f2; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:4 10 4 10;");
                editBtn.setOnAction(e -> {
                    tasks t = getTableView().getItems().get(getIndex());
                    openEditTaskDialog(t);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                editBtn.setDisable(!canEditTasks);
                setGraphic(editBtn);
            }
        });
    }

    // ------------------------------------------------------------
    // Task action column
    // ------------------------------------------------------------

    private void openEditTaskDialog(tasks task) {
        if (task == null) return;

        if (!canEditTasks) {
            messageBoxService.toast("No Permission",
                    "Supervisor role cannot edit tasks.",
                    notificationType.WRONG);
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText(task.getTaskName());

        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField durationTxt = new TextField(String.valueOf(task.getProjectDuration()));
        TextField startTxt = new TextField(task.getStartDate() == null ? "" : task.getStartDate().toString());
        TextField endTxt = new TextField(task.getEndDate() == null ? "" : task.getEndDate().toString());

        durationTxt.setPromptText("Duration (days)");
        startTxt.setPromptText("Start date (yyyy-MM-dd)");
        endTxt.setPromptText("End date (yyyy-MM-dd)");

        grid.addRow(0, new Label("Duration"), durationTxt);
        grid.addRow(1, new Label("Start"), startTxt);
        grid.addRow(2, new Label("End"), endTxt);

        dialog.getDialogPane().setContent(grid);

        Node saveBtn = dialog.getDialogPane().lookupButton(saveBtnType);
        if (saveBtn != null) {
            saveBtn.setDisable(false);
            durationTxt.textProperty().addListener((obs, o, n) -> saveBtn.setDisable(tryParseDouble(n) == null));
        }

        dialog.showAndWait().ifPresent(btn -> {
            if (btn != saveBtnType) return;

            Double dur = tryParseDouble(durationTxt.getText());
            LocalDate start = tryParseDate(startTxt.getText());
            LocalDate end = tryParseDate(endTxt.getText());

            if (dur == null || dur <= 0) {
                messageBoxService.toast("Invalid Duration", "Please enter a duration (> 0).", notificationType.WRONG);
                return;
            }
            if (start == null || end == null) {
                messageBoxService.toast("Invalid Date",
                        "Use yyyy-MM-dd or dd-MMM-yyyy (e.g., 2026-01-14 or 14-JAN-2026).",
                        notificationType.WRONG);
                return;
            }
            if (end.isBefore(start)) {
                messageBoxService.toast("Invalid Date Range", "End date cannot be before start date.", notificationType.WRONG);
                return;
            }
// Optional: force a consistent display format (yyyy-MM-dd)
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override public String toString(LocalDate date) {
                return date == null ? "" : fmt.format(date);
            }
            @Override public LocalDate fromString(String s) {
                return (s == null || s.trim().isEmpty()) ? null : LocalDate.parse(s.trim(), fmt);
            }
        };
         tasks updated = new tasks();
            updated.setAssignTaskId(task.getAssignTaskId());
            updated.setProjectDuration(dur);
            updated.setStartDate(Date.valueOf(start));
            updated.setEndDate(Date.valueOf(end));
            saveTaskEditAsync(updated);
        });
    }


    private void saveTaskEditAsync(tasks updated) {
        if (updated == null || workItem == null) return;

        Task<Boolean> save = new Task<>() {
            @Override
            protected Boolean call() {
                return database.editAssignTasks(updated, assignStatus.CUSTOM);
            }

            @Override
            protected void succeeded() {
                Boolean ok = getValue();
                if (ok != null && ok) {
                    messageBoxService.toast("Saved", "Task updated successfully.", notificationType.SUCCESS);

                    if (taskTable != null) {
                        javafx.concurrent.Task<javafx.collections.ObservableList<IPPSystem.Models.tasks>> reloadTasksTask =
                                new javafx.concurrent.Task<>() {
                                    @Override
                                    protected javafx.collections.ObservableList<IPPSystem.Models.tasks> call() {
                                        return database.getAllTasksByAssignWorkItem(workItem.getAssignWorkItemId());
                                    }
                                };
                        reloadTasksTask.setOnSucceeded(ev2 -> taskTable.setItems(reloadTasksTask.getValue()));
                        reloadTasksTask.setOnFailed(ev2 -> reloadTasksTask.getException().printStackTrace());
                        new Thread(reloadTasksTask, "reload-tasks").start();
                        taskTable.refresh();
                    }

                    loadDashboardAsync(workItem.getAssignWorkItemId(), LocalDate.now());
                } else {
                    messageBoxService.toast("Update Failed", "Database returned false.", notificationType.WRONG);
                }
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                messageBoxService.toast("Update Failed",
                        ex == null ? "Unknown error" : ex.getMessage(),
                        notificationType.WRONG);
            }
        };

        Thread t = new Thread(save);
        t.setDaemon(true);
        t.start();
    }

    // ------------------------------------------------------------
    // Dashboard
    // ------------------------------------------------------------
    private void loadDashboardAsync(int assignWorkItemId, LocalDate asOf) {
        Task<calculationHelper.ProjectDashboard> task = new Task<>() {
            @Override
            protected calculationHelper.ProjectDashboard call() {
                return helper.getWorkItemDashboardOnlyNumbers(assignWorkItemId, asOf);
            }

            @Override
            protected void succeeded() {
                calculationHelper.ProjectDashboard d = getValue();
                if (d == null) return;

                double bac = d.bac();
                double ev  = d.ev();
                double ac  = d.ac();

                // ===== Cost card =====
                safeSet(totalCost, formatMoney(bac));
                safeSet(actualCost, formatMoney(ac));
                safeSet(actualCostPercent, formatPercent(ac, bac));
                if (actualCostProgress != null) actualCostProgress.setProgress(clamp01(bac <= 0 ? 0 : ac / bac));

                // ===== Earned value card =====
                safeSet(totalEarnValue, formatMoney(bac));
                safeSet(usedEarnValue, formatMoney(ev));
                safeSet(earnValuePercent, formatPercent(ev, bac));
                if (earnValueProgress != null) earnValueProgress.setProgress(clamp01(bac <= 0 ? 0 : ev / bac));

                // ===== SPI / CPI =====
                safeSet(spiCircleRate, formatIndex(d.spi()));
                safeSet(spiStatusLbl, statusTextForIndex(d.spi(), true));
                applyCircleProgress(spiCircle, d.spi());

                safeSet(cpiCircleRate, formatIndex(d.cpi()));
                safeSet(cpiStatusLbl, statusTextForIndex(d.cpi(), false));
                applyCircleProgress(cpiCircle, d.cpi());
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void reloadFieldsFromModel() {
        if (workItem == null) return;

        // View-only labels
        safeSet(viewBudget, formatMoney(workItem.getProjectCost()));
        safeSet(viewPlanStartDate, utils.dateFormat(workItem.getStartDate()));
        safeSet(viewPlanEndDate, utils.dateFormat(workItem.getEndDate()));

        safeSet(viewActualStartDate, utils.dateFormat(workItem.getStartDate()));
        safeSet(viewActualEndDate, utils.dateFormat(workItem.getEndDate()));

        safeSet(viewDuration, formatNumber(workItem.getProjectDuration()));
        safeSet(viewTotalLabors, formatQty(workItem.getProjectLaborQty()));
    }

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------
    private Double tryParseDouble(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s.trim().replace(",", "")); }
        catch (NumberFormatException e) { return null; }
    }

    private LocalDate tryParseDate(String s) {
        if (s == null) return null;
        try { return LocalDate.parse(s.trim(), FLEX_DATE); }
        catch (DateTimeParseException e) { return null; }
    }

    private void safeSet(Label lbl, String v) {
        if (lbl != null) lbl.setText(v == null ? "" : v);
    }

    private double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }

    private String formatMoney(double value) {
        if (value <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(value) + " MMK";
    }

    private String formatNumber(double v) {
        if (v <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(v);
    }

    private String formatQty(double q) {
        if (q <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(q) + " persons";
    }

    private String formatPercent(double numerator, double denominator) {
        if (denominator <= 0) return "-";
        long pct = Math.round((numerator / denominator) * 100.0);
        return pct + "%";
    }

    private String formatIndex(Double v) {
        if (v == null) return "-";
        return new DecimalFormat("0.00").format(v);
    }

    private String statusTextForIndex(Double idx, boolean isSchedule) {
        if (idx == null) return "No Data";

        if (isSchedule) {
            if (idx >= 1.05) return "Ahead of Schedule";
            if (idx >= 0.95) return "On Schedule";
            return "Behind Schedule";
        } else {
            if (idx >= 1.05) return "Under Budget";
            if (idx >= 0.95) return "On Budget";
            return "Over Budget";
        }
    }

    private void applyCircleProgress(Circle circle, Double idx) {
        if (circle == null) return;

        double radius = circle.getRadius();
        double circumference = 2 * Math.PI * radius;

        circle.getStrokeDashArray().setAll(circumference);

        // 1.0 => full ring, clamp anything above 1 to full ring
        double p = (idx == null) ? 0 : clamp01(idx);
        circle.setStrokeDashOffset(circumference * (1 - p));
    }
}
