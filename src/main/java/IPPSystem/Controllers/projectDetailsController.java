package IPPSystem.Controllers;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.workItems;
import IPPSystem.Utils.calculationHelper;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class projectDetailsController extends viewProjectsController {

    // ===== Header =====
    @FXML private Label projectName;
    @FXML private Label projectStatus;
    @FXML private Label projectGeneral;
    @FXML private Button backToViewProjectBtn;

    // ===== Top cards =====
    @FXML private Label dayCompleteLbl;
    @FXML private Label completedDay;
    @FXML private Label totalDay;
    @FXML private ProgressBar dayCompleteProgress;

    @FXML private Label wiCompleteLbl;
    @FXML private Label completedWi;
    @FXML private Label totalWi;
    @FXML private ProgressBar wiCompleteProgress;

    @FXML private Label earnedValueLbl;
    @FXML private Label usedEarnValue;
    @FXML private Label totalEarnValue;
    @FXML private ProgressBar earnValueProgress;

    // ===== Project Info (view-only) =====
    @FXML private VBox viewOnlyProjectInfo;
    @FXML private Label projectViewLevel;
    @FXML private Label projectViewStartDate;
    @FXML private Label projectViewDuration;
    @FXML private Label projectViewContract;
    @FXML private Label projectViewEndDate;
    @FXML private Label projectViewAddress;
    @FXML private ProgressBar projectProgress;
    @FXML private Label projectProgressLbl;

    // ===== Project Info (edit) =====
    @FXML private VBox editProjectInfo;
    @FXML private TextField editContractTxt;
    @FXML private TextField editDurationTxt;
    @FXML private ComboBox<enumDuration> durationComboBox;
    @FXML private DatePicker editStartDate;
    @FXML private DatePicker editEndDate;
    @FXML private TextField editAddressTxt;
    @FXML private Button editConfirmBtn;
    @FXML private Button editRevertBtn;

    // floating labels (your note)
    @FXML private Label editContactLbl;   // Contract Value label (typo in FXML: Contact)
    @FXML private Label editDurationLbl;  // Duration label
    @FXML private Label editAddressLbl;   // Address label
    @FXML private Label levelLbl;         // exists in FXML header row

    // ===== CPI / SPI widgets =====
    @FXML private Circle spiProgressCircle;
    @FXML private Label spiLbl;
    @FXML private Label spiStatusLbl;
    @FXML private Label spiPvLbl;
    @FXML private Label spiEvLbl;

    @FXML private Circle cpiProgressCircle;
    @FXML private Label cpiLbl;
    @FXML private Label cpiStatusLbl;
    @FXML private Label cpiPvLbl;
    @FXML private Label cpiEvLbl;

    // ===== Work items table =====
    @FXML private TableView<workItems> workItemTable;
    @FXML private TableColumn<workItems, String> workItemNameCol;
    @FXML private TableColumn<workItems, String> workItemStatusCol;
    @FXML private TableColumn<workItems, Number> workItemCostCol;
    @FXML private TableColumn<workItems, Number> workItemDurationCol;
    @FXML private TableColumn<workItems, java.sql.Date> workItemStartCol;
    @FXML private TableColumn<workItems, java.sql.Date> workItemEndCol;

    private projects project;
    private final calculationHelper helper = calculationHelper.getInstance();

    @FXML
    public void initialize() {

        // Fill duration units if you use them
        if (durationComboBox != null) {
            durationComboBox.getItems().setAll(enumDuration.values());
        }

        // Floating label behavior (simple + reliable)
        utils.setFloatTextFieldStyle( editContactLbl,editContractTxt);
        utils.setFloatTextFieldStyle( editDurationLbl,editDurationTxt);
        utils.setFloatTextFieldStyle( editAddressLbl,editAddressTxt);

        if (editConfirmBtn != null) editConfirmBtn.setOnAction(e -> onConfirmEdits());
        if (editRevertBtn != null) editRevertBtn.setOnAction(e -> loadProjectIntoEditFields(project));

        if (backToViewProjectBtn != null) {
            backToViewProjectBtn.setOnAction(e -> utils.openFxml("viewProjects.fxml", null));
        }

        // Double-click open work item details
        if (workItemTable != null) {
            workItemTable.setRowFactory(tv -> {
                TableRow<workItems> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && !row.isEmpty()) {
                        utils.openWorkItemDetails(row.getItem(), null);
                    }
                });
                return row;
            });
        }
    }

    public void setProjectData(projects project) {
        this.project = project;
        if (project == null) return;

        // Toggle view/edit by role (your old logic)
        boolean isSupervisor = loginUser != null && role.SUPERVISOR.toString().equals(loginUser.getUserRole());
        if (editProjectInfo != null) editProjectInfo.setVisible(!isSupervisor);
        if (viewOnlyProjectInfo != null) viewOnlyProjectInfo.setVisible(isSupervisor);

        // Header
        safeSet(projectName, project.getProjectInstanceName());
        safeSet(projectStatus, project.getProjectStatus());
        safeSet(projectGeneral,
                (project.getProjectTypeName() != null ? project.getProjectTypeName() : "") +
                        (project.getProjectBuildingName() != null ? (", " + project.getProjectBuildingName()) : "")
        );

        // View section
        safeSet(projectViewLevel, project.getProjectLevelName());
        safeSet(projectViewAddress, project.getProjectLocation());
        safeSet(projectViewContract, formatMoney(project.getProjectCost()));
        safeSet(projectViewStartDate, utils.dateFormat(project.getStartDate()));
        safeSet(projectViewEndDate, utils.dateFormat(project.getEndDate()));
        safeSet(projectViewDuration, utils.getOnlyOneDuration(project.getProjectDuration(), enumDuration.DAY));
        utils.durationShowHelper(project,durationComboBox,editDurationTxt);

        // Edit fields
        loadProjectIntoEditFields(project);

        // Work items table
        refreshWorkItems();

        // Dashboard metrics
        loadDashboardAsync(project.getAssignProjectId(), LocalDate.now());
    }

    private void loadDashboardAsync(int projectId, LocalDate asOf) {
        Task<calculationHelper.ProjectDashboard> task = new Task<>() {
            @Override
            protected calculationHelper.ProjectDashboard call() {
                return helper.getProjectDashboard(projectId, asOf);
            }

            @Override
            protected void succeeded() {
                calculationHelper.ProjectDashboard d = getValue();
                if (d == null) return;

                // ===== Completed Days card =====
                int elapsed = d.elapsedDays();
                int total = d.totalDays();

                double day01 = (total <= 0) ? 0 : Math.min(1.0, elapsed / (double) total);

                completedDay.setText(elapsed + " days");
                totalDay.setText(total + " days");
                dayCompleteLbl.setText(Math.round(day01 * 100) + "%");
                dayCompleteProgress.setProgress(day01);

                // ==== for the project progress =====

                double progress01 = d.progressRatio();   // already 0..1
                projectProgress.setProgress(progress01);
                projectProgressLbl.setText(Math.round(progress01 * 100) + "%");


                // ===== Completed WorkItems card =====
                int doneWi = d.completedWorkItems();
                int totalWiCount = d.totalWorkItems();
                double wi01 = (totalWiCount <= 0) ? 0 : clamp01(doneWi / (double) totalWiCount);

                safeSet(completedWi, String.valueOf(doneWi) + " items");
                safeSet(totalWi, String.valueOf(totalWiCount) + " items");
                safeSet(wiCompleteLbl, formatPercent(wi01));
                if (wiCompleteProgress != null) wiCompleteProgress.setProgress(wi01);

                // ===== Earned Value card =====
                double bac = d.bac();
                double ev = d.ev();
                double ev01 = (bac <= 0) ? 0 : clamp01(ev / bac);

                safeSet(usedEarnValue, formatMoney(ev));
                safeSet(totalEarnValue, formatMoney(bac));
                safeSet(earnedValueLbl, formatPercent(ev01));
                if (earnValueProgress != null) earnValueProgress.setProgress(ev01);

                // ===== SPI + CPI panels =====
                safeSet(spiLbl, formatIndex(d.spi()));
                safeSet(spiStatusLbl, statusTextForIndex(d.spi(), true));
                safeSet(spiPvLbl, formatMoney(d.pv()));
                safeSet(spiEvLbl, formatMoney(d.ev()));
                applyCircleProgress(spiProgressCircle, d.spi()); // optional visual

                safeSet(cpiLbl, formatIndex(d.cpi()));
                safeSet(cpiStatusLbl, statusTextForIndex(d.cpi(), false));
                safeSet(cpiPvLbl, formatMoney(d.pv()));
                safeSet(cpiEvLbl, formatMoney(d.ev()));
                applyCircleProgress(cpiProgressCircle, d.cpi()); // optional visual
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void refreshWorkItems() {
        if (project == null || workItemTable == null) return;

        Runnable r = () -> {
            workItemTable.setItems(database.getAllWorkItemsByAssignProject(project.getAssignProjectId()));
            workItemTable.refresh();
        };

        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    private void loadProjectIntoEditFields(projects p) {
        if (p == null) return;

        if (editContractTxt != null) editContractTxt.setText(String.valueOf(p.getProjectCost()));
        if (editDurationTxt != null) editDurationTxt.setText(String.valueOf(p.getProjectDuration()));

        if (durationComboBox != null) durationComboBox.setValue(enumDuration.DAY);

        if (editStartDate != null) editStartDate.setValue(utils.toLocalDate(p.getStartDate()));
        if (editEndDate != null) editEndDate.setValue(utils.toLocalDate(p.getEndDate()));

        if (editAddressTxt != null) editAddressTxt.setText(p.getProjectLocation() == null ? "" : p.getProjectLocation());

        // keep your “Level” floating label area (you currently show it on the top row)
        safeSet(levelLbl, p.getProjectLevelName());
    }

    private void onConfirmEdits() {
        if (project == null) return;

        // 1) Read inputs
        Double cost = tryParseDouble(editContractTxt.getText());
        Double duration = tryParseDouble(editDurationTxt.getText()); // duration in days
        LocalDate start = (editStartDate == null) ? null : editStartDate.getValue();

        // 2) Validate
        if (cost == null || cost < 0) {
            utils.setAlertBox(root, "Invalid Cost", "Please enter a valid cost (>= 0).",
                    notificationType.WRONG, true);
            return;
        }

        if (duration == null || duration <= 0) {
            utils.setAlertBox(root, "Invalid Duration", "Please enter a valid duration (> 0 days).",
                    notificationType.WRONG, true);
            return;
        }

        if (start == null) {
            utils.setAlertBox(root, "Invalid Start Date", "Please choose a start date.",
                    notificationType.WRONG, true);
            return;
        }

        // 3) Calculate end date from start + duration
        // If duration is 1 day: end = start
        long days = (long) Math.ceil(duration);
        if (days < 1) days = 1;

        LocalDate end = start.plusDays(days - 1);

        // (Optional) Set end date in UI so user sees it
        if (editEndDate != null) {
            editEndDate.setValue(end);
        }

        // 4) Call procedure
        try {
            database.callUpdateProjectBaseline(
                    project.getAssignProjectId(),
                    cost,
                    start,
                    end,
                    duration
            );

            // 5) Refresh UI
            refreshWorkItems();
            loadDashboardAsync(project.getAssignProjectId(), LocalDate.now());

            // 6) Success message
            utils.setAlertBox(root, "Success", "Project baseline updated successfully.",
                    notificationType.SUCCESS, true);

        } catch (RuntimeException ex) {
            utils.setAlertBox(root, "Database Error",
                    ex.getMessage(), notificationType.WRONG, true);
        }
    }

    private Double tryParseDouble(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        s = s.replace(",", ""); // allow "1,000"
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return null; }
    }



    // ------------------------------
    // UI helpers
    // ------------------------------
    private void safeSet(Label lbl, String v) {
        if (lbl != null) lbl.setText(v == null ? "" : v);
    }

    private double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }

    private String formatPercent(double p01) {
        int pct = (int) Math.round(clamp01(p01) * 100.0);
        return pct + "%";
    }

    private String formatMoney(double value) {
        if (value <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(value) + " MMK";
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
        if (idx == null) {
            circle.setStrokeDashOffset(2 * Math.PI * circle.getRadius());
            return;
        }

        // simple static ring fill: treat 1.0 as full ring, clamp 0..1
        double radius = circle.getRadius();
        double circumference = 2 * Math.PI * radius;

        circle.getStrokeDashArray().setAll(circumference);
        circle.setStrokeDashOffset(circumference * (1 - clamp01(idx)));
    }
}
