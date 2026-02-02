package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.Models.skills;
import IPPSystem.Models.tasks;
import IPPSystem.Models.workItems;
import IPPSystem.Utils.calculationHelper;
import IPPSystem.Utils.utils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class workItemDetailsController extends viewProjectsController {

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

    // ===== Edit section =====
    @FXML private VBox editShowSkillBox;
    @FXML private VBox editWorkItemInfo;

    @FXML private TextField editBudgetTxt;
    @FXML private TextField editLaborQtyTxt;
    @FXML private TextField editDurationTxt;

    @FXML private Label editActualStartDateLbl;
    @FXML private Label editActualEndDateLbl;

    @FXML private TextField editPlanStartDateTxt;
    @FXML private TextField editPlanEndDateTxt;

    @FXML private Button editConfirmBtn;
    @FXML private Button editRevertBtn;

    // Floating label controls (you added these in FXML)
    @FXML private Label editBudgetLbl;
    @FXML private Label editLaborQtyLbl;
    @FXML private Label editPlanStartDateLbl;
    @FXML private Label editPlanEndDateLbl;

    // ===== View-only section =====
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

    @FXML private TableView<tasks> taskTable;
    @FXML private TableColumn<tasks, String> taskNameCol;
    @FXML private TableColumn<tasks, Double> taskDurationCol;
    @FXML private TableColumn<tasks, Date> taskPlanStartDateCol;
    @FXML private TableColumn<tasks, Date> taskPlanEndDateCol;
    @FXML private TableColumn<tasks, Date> taskActualStartDate;
    @FXML private TableColumn<tasks, Date> taskActualEndDateCol;
    @FXML private TableColumn<tasks, String> TaskStatusCol;
    @FXML private TableColumn<tasks, Button> taskActionCol;

    @FXML private Button backToProjectDetailss;

    private workItems workItem;
    private final calculationHelper helper = calculationHelper.getInstance();

    // Flexible date parser: supports "2026-01-14" and "14-JAN-2026"
    private static final DateTimeFormatter FLEX_DATE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)                  // yyyy-MM-dd
            .appendOptional(DateTimeFormatter.ofPattern("d-MMM-uuuu", Locale.ENGLISH)) // 14-JAN-2026
            .appendOptional(DateTimeFormatter.ofPattern("dd-MMM-uuuu", Locale.ENGLISH))
            .toFormatter(Locale.ENGLISH);

    @FXML
    public void initialize() {

        // Task table mapping (keep your existing property names)
        if (taskNameCol != null) taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        if (taskDurationCol != null) taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("projectDuration"));
        if (taskPlanStartDateCol != null) taskPlanStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskPlanEndDateCol != null) taskPlanEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // If you later add real actualStart/actualEnd fields in tasks model,
        // switch these factories to "actualStartDate" / "actualEndDate".
        if (taskActualStartDate != null) taskActualStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskActualEndDateCol != null) taskActualEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        if (TaskStatusCol != null) TaskStatusCol.setCellValueFactory(new PropertyValueFactory<>("projectStatus"));

        // Skill table
        if (viewSkillCol != null) viewSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (viewQtyCol != null) viewQtyCol.setCellValueFactory(new PropertyValueFactory<>("projectLaborQty"));

        // Floating labels (your note)
        utils.setFloatTextFieldStyle(editBudgetLbl, editBudgetTxt);
        utils.setFloatTextFieldStyle(editLaborQtyLbl, editLaborQtyTxt);
        utils.setFloatTextFieldStyle(editPlanStartDateLbl, editPlanStartDateTxt);
        utils.setFloatTextFieldStyle(editPlanEndDateLbl, editPlanEndDateTxt);

        if (editConfirmBtn != null) editConfirmBtn.setOnAction(e -> onConfirmEdits());
        if (editRevertBtn != null) editRevertBtn.setOnAction(e -> reloadFieldsFromModel());

        if (backToProjectDetailss != null) backToProjectDetailss.setOnAction(e -> utils.openFxml("viewProjects.fxml", null));
    }

    public void setWorkItem(workItems item) {
        this.workItem = item;
        if (item == null) return;

        // Only manager can edit (match your projectDetailsController logic)
        boolean isSupervisor = (loginUser != null)
                && role.SUPERVISOR.toString().equals(loginUser.getUserRole());

        if (editWorkItemInfo != null) editWorkItemInfo.setVisible(!isSupervisor);
        if (viewOnlyWorkItemInfo != null) viewOnlyWorkItemInfo.setVisible(isSupervisor);

        // Load tables (baseline lists)
        if (taskTable != null) taskTable.setItems(database.getAllTasksByAssignWorkItem(item.getAssignWorkItemId()));
        if (viewSkillTable != null) viewSkillTable.setItems(database.getAllSkillByAssignWorkItemDetails(item.getAssignWorkItemId()));

        refreshEditSkillBox();

        // Load baseline (from model first)
        reloadFieldsFromModel();

        // Load EVM numbers (BAC/PV/EV/AC/CPI/SPI) from stored procedure
        loadDashboardAsync(item.getAssignWorkItemId(), LocalDate.now());
    }

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
                double pv  = d.pv();
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

                // Optional: if you want to show PV somewhere later, you now have it as pv.
                // (Your current workItemDetails.fxml does not include PV labels.)
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void refreshEditSkillBox() {
        if (editShowSkillBox == null || workItem == null) return;

        editShowSkillBox.getChildren().clear();

        var list = database.getAllSkillByAssignWorkItemDetails(workItem.getAssignWorkItemId());
        if (list == null || list.isEmpty()) {
            Label empty = new Label("No skills assigned.");
            empty.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");
            editShowSkillBox.getChildren().add(empty);
            return;
        }

        for (skills s : list) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(s.getSkillName());

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Your table uses projectLaborQty, so we keep consistent
            Label qty = new Label(formatQtyOnly(s.getProjectLaborQty()));

            row.getChildren().addAll(name, spacer, qty);
            editShowSkillBox.getChildren().add(row);
        }
    }

    private void reloadFieldsFromModel() {
        if (workItem == null) return;

        // Edit fields
        if (editBudgetTxt != null) editBudgetTxt.setText(String.valueOf(workItem.getProjectCost()));
        if (editLaborQtyTxt != null) editLaborQtyTxt.setText(String.valueOf(workItem.getProjectLaborQty()));
        if (editDurationTxt != null) editDurationTxt.setText(String.valueOf(workItem.getProjectDuration()));

        // You currently store only baseline dates in workItem model (from latest detail record)
        safeSet(editActualStartDateLbl, utils.dateFormat(workItem.getStartDate()));
        safeSet(editActualEndDateLbl, utils.dateFormat(workItem.getEndDate()));

        if (editPlanStartDateTxt != null) editPlanStartDateTxt.setText(utils.dateFormat(workItem.getStartDate()));
        if (editPlanEndDateTxt != null) editPlanEndDateTxt.setText(utils.dateFormat(workItem.getEndDate()));

        // View-only labels
        safeSet(viewBudget, formatMoney(workItem.getProjectCost()));
        safeSet(viewPlanStartDate, utils.dateFormat(workItem.getStartDate()));
        safeSet(viewPlanEndDate, utils.dateFormat(workItem.getEndDate()));
        safeSet(viewActualStartDate, utils.dateFormat(workItem.getStartDate()));
        safeSet(viewActualEndDate, utils.dateFormat(workItem.getEndDate()));
        safeSet(viewDuration, formatNumber(workItem.getProjectDuration()));
        safeSet(viewTotalLabors, formatQty(workItem.getProjectLaborQty()));
    }

    private void onConfirmEdits() {
        if (workItem == null) return;

        Parent overlayRoot = getOverlayRoot();

        Double cost = tryParseDouble(readTrim(editBudgetTxt));
        Double laborQty = tryParseDouble(readTrim(editLaborQtyTxt));
        Double duration = tryParseDouble(readTrim(editDurationTxt));
        LocalDate start = tryParseDate(readTrim(editPlanStartDateTxt));

        if (cost == null || cost < 0) {
            utils.setAlertBox(overlayRoot, "Invalid Budget", "Please enter a valid budget (>= 0).", notificationType.WRONG, true);
            return;
        }
        if (laborQty == null || laborQty < 0) {
            utils.setAlertBox(overlayRoot, "Invalid Labor Qty", "Please enter a valid labor quantity (>= 0).", notificationType.WRONG, true);
            return;
        }
        if (duration == null || duration <= 0) {
            utils.setAlertBox(overlayRoot, "Invalid Duration", "Please enter a duration (> 0).", notificationType.WRONG, true);
            return;
        }
        if (start == null) {
            utils.setAlertBox(overlayRoot, "Invalid Start Date", "Use yyyy-MM-dd or dd-MMM-yyyy (e.g., 2026-01-14 or 14-JAN-2026).", notificationType.WRONG, true);
            return;
        }

        int durDays = (int) Math.ceil(duration);
        LocalDate end = start.plusDays(durDays - 1);

        // show calculated end date in the UI
        if (editPlanEndDateTxt != null) editPlanEndDateTxt.setText(end.format(DateTimeFormatter.ISO_LOCAL_DATE));

        try {
            // IMPORTANT: you must implement this method in database.java (see section 2 below)
//            database.callUpdateWorkItemBaseline(
//                    workItem.getAssignWorkItemId(),
//                    cost,
//                    laborQty,
//                    duration,
//                    start,
//                    end
//            );

            // Update local model so Revert uses latest
            workItem.setProjectCost(cost);
            workItem.setProjectLaborQty(laborQty);
            workItem.setProjectDuration(duration);
            workItem.setStartDate(Date.valueOf(start));
            workItem.setEndDate(Date.valueOf(end));

            reloadFieldsFromModel();
            loadDashboardAsync(workItem.getAssignWorkItemId(), LocalDate.now());

            utils.setAlertBox(overlayRoot, "Success", "Work item baseline updated successfully.", notificationType.SUCCESS, true);

        } catch (RuntimeException ex) {
            utils.setAlertBox(overlayRoot, "Update Failed", ex.getMessage(), notificationType.WRONG, true);
        }
    }

    private Parent getOverlayRoot() {
        if (backToProjectDetailss != null && backToProjectDetailss.getParent() instanceof Parent p) return p;
        if (editConfirmBtn != null && editConfirmBtn.getScene() != null) return editConfirmBtn.getScene().getRoot();
        if (taskTable != null && taskTable.getScene() != null) return taskTable.getScene().getRoot();
        return null;
    }

    // ------------------------------
    // Helper functions
    // ------------------------------
    private String readTrim(TextField tf) {
        if (tf == null || tf.getText() == null) return null;
        String s = tf.getText().trim();
        return s.isEmpty() ? null : s;
    }

    private Double tryParseDouble(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s.replace(",", "")); }
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

    private String formatQtyOnly(double q) {
        if (q <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(q);
    }

    // ratio-based percent (EV/BAC, AC/BAC) but progressbars still clamp
    private String formatPercent(double numerator, double denominator) {
        if (denominator <= 0) return "-";
        double ratio = numerator / denominator;
        long pct = Math.round(ratio * 100.0);
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

        double p = (idx == null) ? 0 : clamp01(idx); // 1.0 => full ring
        circle.setStrokeDashOffset(circumference * (1 - p));
    }
}
