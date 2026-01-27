package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.workItems;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class projectDetailsController {

    @FXML private Label completedDay;
    @FXML private Label completedTask;
    @FXML private Label cpiEvLbl;
    @FXML private Label cpiLbl;
    @FXML private Circle cpiProgressCircle;
    @FXML private Label cpiPvLbl;
    @FXML private Label cpiStatusLbl;

    @FXML private Label dayCompleteLbl;
    @FXML private ProgressBar dayCompleteProgress;

    @FXML private ProgressBar earnValueProgress;
    @FXML private Label earnedValueLbl;

    @FXML private Label editAddressLbl;
    @FXML private TextField editAddressTxt;
    @FXML private Button editConfirmBtn;
    @FXML private Label editContactLbl;
    @FXML private TextField editContractTxt;
    @FXML private Label editDurationLbl;
    @FXML private TextField editDurationTxt;
    @FXML private DatePicker editEndDate;
    @FXML private Label editLevelLbl;
    @FXML private TextField editLevelTxt;
    @FXML private VBox editProjectInfo;
    @FXML private Button editRevertBtn;
    @FXML private DatePicker editStartDate;

    @FXML private Label projectGeneral;
    @FXML private Label projectName;
    @FXML private Label projectStatus;

    @FXML private TableView<workItems> workItemTable;
    @FXML private TableColumn<workItems, String> workItemNameCol;
    @FXML private TableColumn<workItems, String> workItemStatusCol;
    @FXML private TableColumn<workItems, Number> workItemCostCol;
    @FXML private TableColumn<workItems, Number> workItemDurationCol;
    @FXML private TableColumn<workItems, java.sql.Date> workItemStartCol;
    @FXML private TableColumn<workItems, java.sql.Date> workItemEndCol;

    @FXML private Label projectViewAddress;
    @FXML private Label projectViewContract;
    @FXML private Label projectViewDuration;
    @FXML private Label projectViewEndDate;
    @FXML private Label projectViewFinishLbl;
    @FXML private ProgressBar projectViewFinishProgress;
    @FXML private Label projectViewLevel;
    @FXML private Label projectViewStartDate;

    @FXML private Label spiEvLbl;
    @FXML private Label spiLbl;
    @FXML private Circle spiProgressCircle;
    @FXML private Label spiPvLbl;
    @FXML private Label spiStatusLbl;

    @FXML private Label taskCompleteLbl;
    @FXML private ProgressBar taskCompleteProgress;

    @FXML private Label totalDay;
    @FXML private Label totalEarnValue;
    @FXML private Label totalTask;
    @FXML private Label usedEarnValue;

    @FXML private VBox viewOnlyProjectInfo;

    private projects project;

    @FXML
    private void initialize() {
        if (editConfirmBtn != null) {
            editConfirmBtn.setOnAction(e -> onConfirmEdits());
        }
        if (editRevertBtn != null) {
            editRevertBtn.setOnAction(e -> loadProjectIntoEditFields(project));
        }

        if (workItemNameCol != null) workItemNameCol.setCellValueFactory(new PropertyValueFactory<>("workItemName"));
        if (workItemStatusCol != null) workItemStatusCol.setCellValueFactory(new PropertyValueFactory<>("projectStatus"));
        if (workItemCostCol != null) workItemCostCol.setCellValueFactory(new PropertyValueFactory<>("projectCost"));
        if (workItemDurationCol != null) workItemDurationCol.setCellValueFactory(new PropertyValueFactory<>("projectDuration"));
        if (workItemStartCol != null) workItemStartCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (workItemEndCol != null) workItemEndCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        if (workItemTable != null) {
            workItemTable.setRowFactory(tv -> {
                TableRow<workItems> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && !row.isEmpty()) {
                       utils.openWorkItemDetails(row.getItem(),null);
                    }
                });
                return row;
            });
        }
        if (editConfirmBtn != null) editConfirmBtn.setOnAction(e -> onConfirmEdits());
        if (editRevertBtn != null) editRevertBtn.setOnAction(e -> loadProjectIntoEditFields(project));
    }

    public void setProjectData(projects project) {
        this.project = project;
        if (project == null) return;

        // Header
        safeSet(projectName, project.getProjectInstanceName());
        safeSet(projectStatus, project.getProjectStatus());
        safeSet(projectGeneral,
                project.getProjectTypeName() + " - " + project.getProjectLocation());

        // View-only info
        safeSet(projectViewLevel, project.getProjectLevelName());
        safeSet(projectViewAddress, project.getProjectLocation());
        safeSet(projectViewDuration, formatDuration(project.getProjectDuration()));
        safeSet(projectViewContract, formatMoney(project.getProjectCost()));

        safeSet(projectViewStartDate, formatDate(project.getStartDate()));
        safeSet(projectViewEndDate, formatDate(project.getEndDate()));

        // Progress (days)
        ProgressInfo days = computeDaysProgress(project.getStartDate(), project.getEndDate());
        safeSet(completedDay, days.completedText);
        safeSet(totalDay, days.totalText);
        safeSet(dayCompleteLbl, days.percentText);
        safeSet(projectViewFinishLbl, days.percentText);

        if (dayCompleteProgress != null) dayCompleteProgress.setProgress(days.progress01);
        if (projectViewFinishProgress != null) projectViewFinishProgress.setProgress(days.progress01);

        // Earned value (placeholder logic: used = progress% of total cost)
        double totalCost = Math.max(0, project.getProjectCost());
        double usedCost = totalCost * clamp01(days.progress01);

        safeSet(totalEarnValue, formatMoney(totalCost));
        safeSet(usedEarnValue, formatMoney(usedCost));
        safeSet(earnedValueLbl, formatPercent(days.progress01));
        if (earnValueProgress != null) earnValueProgress.setProgress(days.progress01);

        // Tasks/SPI/CPI are not available in [projects](cci:2://file:///d:/IPP-System/src/main/java/IPPSystem/Models/projects.java:4:0-482:1) model currently -> keep safe defaults
        safeSet(taskCompleteLbl, "0%");
        if (taskCompleteProgress != null) taskCompleteProgress.setProgress(0);

        safeSet(spiLbl, "-");
        safeSet(spiStatusLbl, "-");
        safeSet(spiPvLbl, "-");
        safeSet(spiEvLbl, "-");

        safeSet(cpiLbl, "-");
        safeSet(cpiStatusLbl, "-");
        safeSet(cpiPvLbl, "-");
        safeSet(cpiEvLbl, "-");

        loadProjectIntoEditFields(project);
        if (workItemTable != null) {
            workItemTable.setItems(database.getAllWorkItemsByAssignProject(project.getAssignProjectId()));
        }
    }

    private void onConfirmEdits() {
        if (project == null) return;

        // Apply edits to UI fields (and to project object where possible)
        String newLevel = readTrim(editLevelTxt);
        String newAddress = readTrim(editAddressTxt);

        Double newContract = tryParseDouble(readTrim(editContractTxt));
        Double newDuration = tryParseDouble(readTrim(editDurationTxt));

        LocalDate start = editStartDate != null ? editStartDate.getValue() : null;
        LocalDate end = editEndDate != null ? editEndDate.getValue() : null;

        if (newAddress != null) {
            project.setProjectLocation(newAddress);
            safeSet(projectViewAddress, newAddress);
            safeSet(projectGeneral, project.getProjectTypeName() + " - " + newAddress);
        }

        if (newLevel != null) {
            project.setProjectLevelName(newLevel);
            safeSet(projectViewLevel, newLevel);
        }

        if (newContract != null) {
            project.setProjectCost(newContract);
            safeSet(projectViewContract, formatMoney(newContract));
        }

        if (newDuration != null) {
            project.setProjectDuration(newDuration);
            safeSet(projectViewDuration, formatDuration(newDuration));
        }

        if (start != null) {
            project.setStartDate(Date.valueOf(start));
            safeSet(projectViewStartDate, start.toString());
        }
        if (end != null) {
            project.setEndDate(Date.valueOf(end));
            safeSet(projectViewEndDate, end.toString());
        }

        // Recompute progress after date edits
        ProgressInfo days = computeDaysProgress(project.getStartDate(), project.getEndDate());
        safeSet(completedDay, days.completedText);
        safeSet(totalDay, days.totalText);
        safeSet(dayCompleteLbl, days.percentText);
        safeSet(projectViewFinishLbl, days.percentText);
        if (dayCompleteProgress != null) dayCompleteProgress.setProgress(days.progress01);
        if (projectViewFinishProgress != null) projectViewFinishProgress.setProgress(days.progress01);
    }

    private void loadProjectIntoEditFields(projects p) {
        if (p == null) return;

        if (editLevelTxt != null) editLevelTxt.setText(nullToEmpty(p.getProjectLevelName()));
        if (editAddressTxt != null) editAddressTxt.setText(nullToEmpty(p.getProjectLocation()));
        if (editContractTxt != null) editContractTxt.setText(String.valueOf(p.getProjectCost()));
        if (editDurationTxt != null) editDurationTxt.setText(String.valueOf(p.getProjectDuration()));

        if (editStartDate != null) editStartDate.setValue(toLocalDate(p.getStartDate()));
        if (editEndDate != null) editEndDate.setValue(toLocalDate(p.getEndDate()));
    }

    private LocalDate toLocalDate(Date d) {
        return d == null ? null : d.toLocalDate();
    }

    private void safeSet(Label lbl, String value) {
        if (lbl != null) lbl.setText(value == null ? "" : value);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private String readTrim(TextField tf) {
        if (tf == null) return null;
        String s = tf.getText();
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private Double tryParseDouble(String s) {
        if (s == null) return null;
        try {
            return Double.parseDouble(s.replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatDate(Date d) {
        return d == null ? "-" : d.toString();
    }

    private String formatDuration(double months) {
        if (months <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(months) + " Months";
    }

    private String formatMoney(double value) {
        if (value <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(value) + " MMK";
    }

    private String formatPercent(double progress01) {
        int pct = (int) Math.round(clamp01(progress01) * 100.0);
        return pct + "%";
    }

    private double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }

    private ProgressInfo computeDaysProgress(Date start, Date end) {
        ProgressInfo info = new ProgressInfo();

        LocalDate s = start == null ? null : start.toLocalDate();
        LocalDate e = end == null ? null : end.toLocalDate();

        if (s == null || e == null || !e.isAfter(s)) {
            info.progress01 = 0;
            info.percentText = "0%";
            info.completedText = "-";
            info.totalText = "-";
            return info;
        }

        long total = ChronoUnit.DAYS.between(s, e);
        long done = ChronoUnit.DAYS.between(s, LocalDate.now());

        if (done < 0) done = 0;
        if (done > total) done = total;

        double p = (total == 0) ? 0 : (done * 1.0 / total);

        info.progress01 = clamp01(p);
        info.percentText = formatPercent(info.progress01);
        info.completedText = done + " days";
        info.totalText = total + " days";
        return info;
    }

    private static class ProgressInfo {
        double progress01;
        String percentText;
        String completedText;
        String totalText;
    }

    private void setWorkItems(){

    }
}