package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.skills;
import IPPSystem.Models.tasks;
import IPPSystem.Models.workItems;
import IPPSystem.Utils.calculationHelper;
import IPPSystem.Utils.utils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Work Item Details screen controller.
 *
 * NOTE:
 * - This controller is intentionally "view-only" to match the current FXML.
 * - If you later add an edit panel, you can re-introduce edit fields + handlers.
 */
public class workItemDetailsController extends viewProjectsController {

    // ===== Header =====
    @FXML private Button backToProjectDetails;

    // ===== KPI cards =====
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

    // ===== Work item info (view-only) =====
    @FXML private VBox viewOnlyWorkItemInfo;
    @FXML private Label viewBudget;
    @FXML private Label viewPlanStartDate;
    @FXML private Label viewPlanEndDate;
    @FXML private Label viewActualStartDate;
    @FXML private Label viewActualEndDate;
    @FXML private Label viewDuration;
    @FXML private Label viewTotalLabors;

    // ===== Skill table =====
    @FXML private TableView<skills> viewSkillTable;
    @FXML private TableColumn<skills, String> viewSkillCol;
    @FXML private TableColumn<skills, Double> viewQtyCol;

    // ===== Task table =====
    @FXML private TableView<tasks> taskTable;
    @FXML private TableColumn<tasks, String> taskNameCol;
    @FXML private TableColumn<tasks, Double> taskDurationCol;
    @FXML private TableColumn<tasks, Date> taskPlanStartDateCol;
    @FXML private TableColumn<tasks, Date> taskPlanEndDateCol;
    @FXML private TableColumn<tasks, String> TaskStatusCol;

    private workItems workItem;
    private final calculationHelper helper = calculationHelper.getInstance();
    private projects parentProject;

    @FXML
    public void initialize() {

        // ---- Task table mapping (keep your model property names) ----
        if (taskNameCol != null) taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        if (taskDurationCol != null) taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("projectDuration"));
        if (taskPlanStartDateCol != null) taskPlanStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskPlanEndDateCol != null) taskPlanEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        if (TaskStatusCol != null) TaskStatusCol.setCellValueFactory(new PropertyValueFactory<>("projectStatus"));

        // ---- Skill table ----
        if (viewSkillCol != null) viewSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (viewQtyCol != null) viewQtyCol.setCellValueFactory(new PropertyValueFactory<>("projectLaborQty"));

        // Nice placeholders
        if (taskTable != null) taskTable.setPlaceholder(new Label("No tasks to show."));
        if (viewSkillTable != null) viewSkillTable.setPlaceholder(new Label("No skills assigned."));

        // Back button
        if (backToProjectDetails != null) {
            backToProjectDetails.setOnAction(e -> utils.openProjectDetails(parentProject,null));
        }
    }

    /**
     * Call this after loading the FXML to populate the screen.
     */
    public void setWorkItem(workItems item, projects project) {
        this.parentProject = project;
        this.workItem = item;
        if (item == null) return;

        // Show/Hide the "Edit" ability here later if you add it.
        // For now: just keep view-only visible.
        if (viewOnlyWorkItemInfo != null) viewOnlyWorkItemInfo.setVisible(true);

        // Load tables
        if (taskTable != null) {
            taskTable.setItems(database.getAllTasksByAssignWorkItem(item.getAssignWorkItemId()));
        }
        if (viewSkillTable != null) {
            viewSkillTable.setItems(database.getAllSkillByAssignWorkItemDetails(item.getAssignWorkItemId()));
        }

        // Load baseline fields
        reloadFieldsFromModel();

        // Load EVM numbers (BAC/PV/EV/AC/CPI/SPI) from calculation helper
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

                double bac = d.bac();   // Budget at Completion
                double ev  = d.ev();    // Earned Value
                double ac  = d.ac();    // Actual Cost

                // ===== Cost card =====
                safeSet(totalCost, formatMoney(bac));
                safeSet(actualCost, formatMoney(ac));
                safeSet(actualCostPercent, formatPercent(ac, bac));
                if (actualCostProgress != null) {
                    actualCostProgress.setProgress(clamp01(bac <= 0 ? 0 : ac / bac));
                }

                // ===== Earned value card =====
                safeSet(totalEarnValue, formatMoney(bac));
                safeSet(usedEarnValue, formatMoney(ev));
                safeSet(earnValuePercent, formatPercent(ev, bac));
                if (earnValueProgress != null) {
                    earnValueProgress.setProgress(clamp01(bac <= 0 ? 0 : ev / bac));
                }

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

    // ------------------------------
    // Helper functions
    // ------------------------------
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

        // 1.0 => full ring, clamp anything above 1 to full ring
        double p = (idx == null) ? 0 : clamp01(idx);
        circle.setStrokeDashOffset(circumference * (1 - p));
    }
}
