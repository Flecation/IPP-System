package IPPSystem.Controllers;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.workItems;
import IPPSystem.Utils.calculationHelper;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;

public class projectDetailsController extends viewProjectsController{

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
    @FXML private Label levelLbl;
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
    @FXML private Label projectViewLevel;
    @FXML private Label projectViewStartDate;

    @FXML private Label spiEvLbl;
    @FXML private Label spiLbl;
    @FXML private Circle spiProgressCircle;
    @FXML private Label spiPvLbl;
    @FXML private Label spiStatusLbl;

    @FXML private Label wiCompleteLbl;
    @FXML private ProgressBar wiCompleteProgress;

    @FXML private Label totalDay;
    @FXML private Label totalEarnValue;
    @FXML private Label totalTask;
    @FXML private Label usedEarnValue;

    @FXML private VBox viewOnlyProjectInfo,moreDataVbox;

    @FXML private Button backToViewProjectBtn;

    @FXML private ComboBox<enumDuration> durationComboBox;

    @FXML private Label completedWi,totalWi;

    private projects project;

    private calculationHelper calculate = calculationHelper.getInstance();


    @FXML
    public void initialize() {

        utils.setFloatTextFieldStyle(editAddressLbl,editAddressTxt);
        utils.setFloatTextFieldStyle(editDurationLbl,editDurationTxt);
        utils.setFloatTextFieldStyle(editContactLbl,editContractTxt);

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
        if (backToViewProjectBtn != null) backToViewProjectBtn.setOnAction(event -> utils.openFxml("viewProjects.fxml",null));
    }

    public void setProjectData(projects project) {
        this.project = project;
        if (project == null) return;

        if (loginUser.getUserRole().equals(role.SUPERVISOR.toString())){
            editProjectInfo.setVisible(false);
            viewOnlyProjectInfo.setVisible(true);
        }else{
            editProjectInfo.setVisible(true);
            viewOnlyProjectInfo.setVisible(false);
        }

        calculate.calculate(project);

//        For the duration path
        utils.durationShowHelper(project,durationComboBox,editDurationTxt);


        // Header
        utils.safeSet(projectName, project.getProjectInstanceName());
        utils.safeSet(projectStatus, project.getProjectStatus());
        utils.safeSet(projectGeneral,
                project.getProjectTypeName() + " - " + project.getProjectBuildingName());

        // View-only info
        utils.safeSet(projectViewLevel, project.getProjectLevelName());
        utils.safeSet(projectViewAddress, project.getProjectLocation());
//        utils.safeSet(projectViewDuration,);
        utils.safeSet(projectViewContract, utils.formatMoney(project.getProjectCost()));
        utils.safeSet(projectViewStartDate,utils.dateFormat(project.getStartDate()));
        utils.safeSet(projectViewEndDate, utils.dateFormat(project.getEndDate()));
        utils.safeSet(projectViewDuration,utils.getOnlyOneDuration(project.getProjectDuration(),enumDuration.DAY));


//        Complete Day Box
//        dayCompleteLbl
//        completedDay
//        dayCompleteProgress
        utils.safeSet(totalDay,utils.getOnlyOneDuration(project.getProjectDuration(),enumDuration.DAY));


        // Progress (workItems)
//        wiCompleteLbl.setText();
//        completedWi
//        wiCompleteProgress
//        utils.safeSet(totalWi,);


        // Earned value (placeholder logic: used = progress% of total cost)
//        earnedValueLbl;
//        totalEarnValue;
//        earnValueProgress;
//        usedEarnValue;

        // Tasks/SPI/CPI are not available in [projects](cci:2://file:///d:/IPP-System/src/main/java/IPPSystem/Models/projects.java:4:0-482:1) model currently -> keep safe defaults
//        utils.safeSet(wiCompleteLbl, "0%");
//        if (wiCompleteProgress != null) wiCompleteProgress.setProgress(0);

        utils.safeSet(spiLbl, "-");
        utils.safeSet(spiStatusLbl, "-");
        utils.safeSet(spiPvLbl, "-");
        utils.safeSet(spiEvLbl, "-");

        utils.safeSet(cpiLbl, "-");
        utils.safeSet(cpiStatusLbl, "-");
        utils.safeSet(cpiPvLbl, "-");
        utils.safeSet(cpiEvLbl, "-");

        loadProjectIntoEditFields(project);
        refreshWorkItems();
    }

    public void refreshWorkItems() {
        if (project == null || workItemTable == null) return;

        Runnable load = () -> {
            ObservableList<workItems> items = database.getAllWorkItemsByAssignProject(project.getAssignProjectId());
            workItemTable.setItems(items);
            workItemTable.refresh();
        };

        if (Platform.isFxApplicationThread()) {
            load.run();
        } else {
            Platform.runLater(load);
        }
    }


    private void onConfirmEdits() {
        if (project == null) return;

        // Apply edits to UI fields (and to project object where possible)
        String newLevel = utils.readTrim(editLevelTxt);
        String newAddress = utils.readTrim(editAddressTxt);

        Double newContract = utils.tryParseDouble(utils.readTrim(editContractTxt));
        Double newDuration = utils.tryParseDouble(utils.readTrim(editDurationTxt));

        LocalDate start = editStartDate != null ? editStartDate.getValue() : null;
        LocalDate end = editEndDate != null ? editEndDate.getValue() : null;

        workItems wi = new workItems();

        // Recompute progress after date edits
//        ProgressInfo days = computeDaysProgress(project.getStartDate(), project.getEndDate());
//        utils.safeSet(completedDay, days.completedText);
//        utils.safeSet(totalDay, days.totalText);
//        utils.safeSet(dayCompleteLbl, days.percentText);
//        if (dayCompleteProgress != null) dayCompleteProgress.setProgress(days.progress01);
    }

    private void loadProjectIntoEditFields(projects p) {
        if (p == null) return;

        if (editLevelTxt != null) editLevelTxt.setText(nullToEmpty(p.getProjectLevelName()));
        if (editAddressTxt != null) editAddressTxt.setText(nullToEmpty(p.getProjectLocation()));
        if (editContractTxt != null) editContractTxt.setText(String.valueOf(p.getProjectCost()));
        if (editDurationTxt != null) editDurationTxt.setText(String.valueOf(p.getProjectDuration()));

        if (editStartDate != null) editStartDate.setValue(utils.toLocalDate(p.getStartDate()));
        if (editEndDate != null) editEndDate.setValue(utils.toLocalDate(p.getEndDate()));
    }


    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }


}