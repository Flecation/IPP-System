package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.skills;
import IPPSystem.Models.tasks;
import IPPSystem.Models.workItems;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.sql.Date;
import java.text.DecimalFormat;

public class workItemDetailsController {

    @FXML private Label actualCost;
    @FXML private Label actualCostPercent;
    @FXML private ProgressBar actualCostProgress;

    @FXML private Label earnValuePercent;
    @FXML private ProgressBar earnValueProgress;

    @FXML private Label totalCost;
    @FXML private Label totalEarnValue;
    @FXML private Label usedEarnValue;

    @FXML private Label spiStatusLbl;
    @FXML private Circle spiCircle;
    @FXML private Label spiCircleRate;

    @FXML private Label cpiStatusLbl;
    @FXML private Circle cpiCircle;
    @FXML private Label cpiCircleRate;

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

    @FXML private VBox viewOnlyWorkItemInfo;
    @FXML private Label viewBudget;
    @FXML private Label viewPlanStartDate;
    @FXML private Label viewPlanEndDate;
    @FXML private Label viewActualStartDate;
    @FXML private Label viewActualEndDate;
    @FXML private Label viewDuration;
    @FXML private Label viewTotalLabors;

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

    @FXML private Button backBtn;

    private workItems workItem;

    @FXML
    private void initialize() {
        if (taskNameCol != null) taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        if (taskDurationCol != null) taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("projectDuration"));
        if (taskPlanStartDateCol != null) taskPlanStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskPlanEndDateCol != null) taskPlanEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        if (taskActualStartDate != null) taskActualStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskActualEndDateCol != null) taskActualEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        if (TaskStatusCol != null) TaskStatusCol.setCellValueFactory(new PropertyValueFactory<>("projectStatus"));

        if (viewSkillCol != null) viewSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (viewQtyCol != null) viewQtyCol.setCellValueFactory(new PropertyValueFactory<>("projectLaborQty"));

        if (editConfirmBtn != null) editConfirmBtn.setOnAction(e -> onConfirmEdits());
        if (editRevertBtn != null) editRevertBtn.setOnAction(e -> loadWorkItemIntoEditFields(workItem));
    }

    public void setWorkItem(workItems item) {
        this.workItem = item;
        if (item == null) return;

        loadWorkItemIntoEditFields(item);
        loadWorkItemIntoViewFields(item);

        if (taskTable != null) {
            taskTable.setItems(database.getAllTasksByAssignWorkItem(item.getAssignWorkItemId()));
        }

        if (viewSkillTable != null) {
            viewSkillTable.setItems(database.getAllSkillByAssignWorkItemDetails(item.getAssignWorkItemId()));
        }

        // Basic cost/EV placeholders (your model doesn't have actual vs planned cost yet)
        double total = Math.max(0, item.getProjectCost());
        double used = total; // placeholder: 100% used
        double progress01 = total == 0 ? 0 : used / total;

        safeSet(totalCost, formatMoney(total));
        safeSet(totalEarnValue, formatMoney(total));
        safeSet(actualCost, formatMoney(used));
        safeSet(usedEarnValue, formatMoney(used));

        safeSet(actualCostPercent, formatPercent(progress01));
        safeSet(earnValuePercent, formatPercent(progress01));

        if (actualCostProgress != null) actualCostProgress.setProgress(clamp01(progress01));
        if (earnValueProgress != null) earnValueProgress.setProgress(clamp01(progress01));

        // SPI/CPI placeholders
        safeSet(spiStatusLbl, "-");
        safeSet(cpiStatusLbl, "-");
        safeSet(spiCircleRate, "-");
        safeSet(cpiCircleRate, "-");
    }

    private void loadWorkItemIntoEditFields(workItems item) {
        if (item == null) return;

        if (editBudgetTxt != null) editBudgetTxt.setText(String.valueOf(item.getProjectCost()));
        if (editLaborQtyTxt != null) editLaborQtyTxt.setText(String.valueOf(item.getProjectLaborQty()));
        if (editDurationTxt != null) editDurationTxt.setText(String.valueOf(item.getProjectDuration()));

        safeSet(editActualStartDateLbl, formatDate(item.getStartDate()));
        safeSet(editActualEndDateLbl, formatDate(item.getEndDate()));

        // No planned dates in model right now → just mirror actual
        if (editPlanStartDateTxt != null) editPlanStartDateTxt.setText(formatDate(item.getStartDate()));
        if (editPlanEndDateTxt != null) editPlanEndDateTxt.setText(formatDate(item.getEndDate()));
    }

    private void loadWorkItemIntoViewFields(workItems item) {
        if (item == null) return;

        safeSet(viewBudget, formatMoney(item.getProjectCost()));
        safeSet(viewPlanStartDate, formatDate(item.getStartDate()));
        safeSet(viewPlanEndDate, formatDate(item.getEndDate()));
        safeSet(viewActualStartDate, formatDate(item.getStartDate()));
        safeSet(viewActualEndDate, formatDate(item.getEndDate()));
        safeSet(viewDuration, formatDuration(item.getProjectDuration()));
        safeSet(viewTotalLabors, formatQty(item.getProjectLaborQty()));
    }

    private void onConfirmEdits() {
        // NOTE: Your DB update functions for workItems aren't implemented in DAO yet,
        // so here we only refresh UI from the edited fields without saving.
        if (workItem == null) return;

        Double budget = tryParseDouble(readTrim(editBudgetTxt));
        Double labors = tryParseDouble(readTrim(editLaborQtyTxt));
        Double duration = tryParseDouble(readTrim(editDurationTxt));

        if (budget != null) workItem.setProjectCost(budget);
        if (labors != null) workItem.setProjectLaborQty(labors);
        if (duration != null) workItem.setProjectDuration(duration);

        loadWorkItemIntoViewFields(workItem);
    }

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

    private String formatPercent(double p01) {
        int pct = (int) Math.round(clamp01(p01) * 100.0);
        return pct + "%";
    }

    private String formatDate(Date d) {
        return d == null ? "-" : d.toString();
    }

    private String formatDuration(double d) {
        if (d <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(d);
    }

    private String formatQty(double q) {
        if (q <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(q) + " persons";
    }
}