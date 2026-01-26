package IPPSystem.Controllers;

import IPPSystem.Models.projects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class projectDetailsController {

    @FXML
    private Label completedDay;

    @FXML
    private Label completedTask;

    @FXML
    private Label cpiEvLbl;

    @FXML
    private Label cpiLbl;

    @FXML
    private Circle cpiProgressCircle;

    @FXML
    private Label cpiPvLbl;

    @FXML
    private Label cpiStatusLbl;

    @FXML
    private Label dayCompleteLbl;

    @FXML
    private ProgressBar dayCompleteProgress;

    @FXML
    private ProgressBar earnValueProgress;

    @FXML
    private Label earnedValueLbl;

    @FXML
    private Label editAddressLbl;

    @FXML
    private TextField editAddressTxt;

    @FXML
    private Button editConfirmBtn;

    @FXML
    private Label editContactLbl;

    @FXML
    private TextField editContractTxt;

    @FXML
    private Label editDurationLbl;

    @FXML
    private TextField editDurationTxt;

    @FXML
    private DatePicker editEndDate;

    @FXML
    private Label editLevelLbl;

    @FXML
    private TextField editLevelTxt;

    @FXML
    private VBox editProjectInfo;

    @FXML
    private Button editResetBtn;

    @FXML
    private DatePicker editStartDate;

    @FXML
    private Label projectGeneral;

    @FXML
    private Label projectName;

    @FXML
    private Label projectStatus;

    @FXML
    private TableView<?> projectTableView;

    @FXML
    private Label projectViewAddress;

    @FXML
    private Label projectViewContract;

    @FXML
    private Label projectViewDuration;

    @FXML
    private Label projectViewEndDate;

    @FXML
    private Label projectViewFinishLbl;

    @FXML
    private ProgressBar projectViewFinishProgress;

    @FXML
    private Label projectViewLevel;

    @FXML
    private Label projectViewStartDate;

    @FXML
    private Label spiEvLbl;

    @FXML
    private Label spiLbl;

    @FXML
    private Circle spiProgressCircle;

    @FXML
    private Label spiPvLbl;

    @FXML
    private Label spiStatusLbl;

    @FXML
    private Label taskCompleteLbl;

    @FXML
    private ProgressBar taskCompleteProgress;

    @FXML
    private Label totalDay;

    @FXML
    private Label totalEarnValue;

    @FXML
    private Label totalTask;

    @FXML
    private Label usedEarnValue;

    @FXML
    private VBox viewOnlyProjectView;

    @FXML
    public void initialize(){
        System.out.println("Project Details Controller loaded!");
    }

    //  Method to receive a projects object and display its data
    public void setProjectData(projects project){
        if(project != null){
            projectName.setText(project.getProjectInstanceName());
            projectStatus.setText(project.getProjectStatus());
            projectGeneral.setText(project.getProjectTypeName() + " - " + project.getProjectLocation());
        }
    }
}
