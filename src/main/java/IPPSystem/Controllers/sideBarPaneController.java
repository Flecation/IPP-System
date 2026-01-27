package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.storage;
import IPPSystem.Utils.themeToggle;
import IPPSystem.Utils.utils;
import com.almasb.fxgl.ui.InGamePanel;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;

import javafx.fxml.FXML;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class sideBarPaneController extends navigationPaneController{

    @FXML
    private BorderPane addNewPane,basePane;

    @FXML
    private HBox backBtn;

    @FXML
    private Region bottomRegion;

    @FXML
    private HBox changePasswordBtn;

    @FXML
    private Label changePasswordIconBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    private Button createAccCancelBtn;

    @FXML
    private Button createAccCreateBtn;

    @FXML
    private DatePicker createAccDob;

    @FXML
    private Label createAccEmailLbl;

    @FXML
    private TextField createAccEmailTxt;

    @FXML
    private Label createAccFNameLbl;

    @FXML
    private TextField createAccFNameTxt;

    @FXML
    private ImageView createAccImage;

    @FXML
    private Label createAccLNameLbl;

    @FXML
    private TextField createAccLNameTxt;

    @FXML
    private HBox createAccPane;

    @FXML
    private Label createAccPhoneLbl;

    @FXML
    private TextField createAccPhoneTxt;

    @FXML
    private Label createAccPwLbl;

    @FXML
    private TextField createAccPwTxt;

    @FXML
    private Button createAccUploadImgBtn;

    @FXML
    private Label darkIcon;

    @FXML
    private Label darkSymbolIcon;

    @FXML
    private Label dashboardIconBtn;

    @FXML
    private HBox dashboardViewBtn;

    @FXML
    private HBox directoryViewBox;

    @FXML
    private Label iconBackBtn;

    @FXML
    private VBox iconSettingBar;

    @FXML
    private VBox iconSideBar;

    @FXML
    private Label imageEditBtn;

    @FXML
    private ImageView imageIconBtn;

    @FXML
    private Region leftRegion;

    @FXML
    private StackPane light;

    @FXML
    private Label lightIcon;

    @FXML
    private Label lightSymbolIcon;

    @FXML
    private StackPane loadPane;

    @FXML
    private HBox logoutBtn;

    @FXML
    private Label logoutIconBtn;

    @FXML
    private Label nameViewText;

    @FXML
    private Label newTabBtn;

    @FXML
    private Label notificationBtn;

    @FXML
    private VBox profileBox;

    @FXML
    private HBox profileViewBtn;

    @FXML
    private Label profileViewIconBtn;

    @FXML
    private Label projectIconBtn;

    @FXML
    private HBox projectViewBtn;

    @FXML
    private Label reloadBtn;

    @FXML
    private Label reportIconBtn;

    @FXML
    private HBox reportViewBtn;

    @FXML
    private Button revertBtn;

    @FXML
    private Region rightRegion;

    @FXML
    private Label roleViewText;

    @FXML
    private Label searchBtn;

    @FXML
    private Label searchClearBtn;

    @FXML
    private TextField searchTextField;

    @FXML
    private HBox searchViewBox;

    @FXML
    private VBox settingBar;

    @FXML
    private Label settingIconBtn;

    @FXML
    private ToggleButton settingToggleBox;

    @FXML
    private Circle settingToggleCircle;

    @FXML
    private StackPane settingTogglePane;

    @FXML
    private HBox settingViewBtn;

    @FXML
    private Label showIconSettingBtn;

    @FXML
    private Label showIconSideBtn;

    @FXML
    private Label showSettingBtn;

    @FXML
    private Label showSideBtn;

    @FXML
    private VBox sideBar;

    @FXML
    private StackPane sideBarStackPane;

    @FXML
    private ToggleButton toggleBox;

    @FXML
    private HBox toggleBtn;

    @FXML
    private Circle toggleCircle;

    @FXML
    private StackPane toggleIconBtn;

    @FXML
    private StackPane togglePane;

    @FXML
    private Label toggleSymbolText;

    @FXML
    private Region topRegion;

    @FXML
    private Label userDobLbl;

    @FXML
    private Label userEmailLbl;

    @FXML
    private TextField userEmailTxtField;

    @FXML
    private Label userIconBtn;

    @FXML
    private ImageView userImage;

    @FXML
    private HBox userInfoCard;

    @FXML
    private Label userNameLbl;

    @FXML
    private Label userPhoneLbl;

    @FXML
    private TextField userPhoneTxtField;

    @FXML
    private Label userRoleLbl;

    @FXML
    private HBox userViewBtn;

    @FXML
    private ImageView userViewImage;

    @FXML
    private Label userViewLbl,addNewTitle;

    @FXML
    private Button addNewExitBtn;

    @FXML
    private VBox createLaborPane,createProjectPane,createReportPane;

    private users loginUser = user;

    protected storage data = storage.getInstance();


    @FXML
    public void initialize() {
        utils.setFloatTextFieldStyle(userEmailLbl,userEmailTxtField);
        utils.setFloatTextFieldStyle(userPhoneLbl,userPhoneTxtField);


        utils.setFloatTextFieldStyle(createAccPhoneLbl,createAccPhoneTxt);
        utils.setFloatTextFieldStyle(createAccEmailLbl,createAccEmailTxt);
        utils.setFloatTextFieldStyle(createAccLNameLbl,createAccLNameTxt);
        utils.setFloatTextFieldStyle(createAccFNameLbl,createAccFNameTxt);


        addNewPane.setVisible(false);
        basePane.setVisible(true);


        // Set initial state - show sideBar, hide settingBar and iconSideBar
        showSidebar(sideBar, 200);

        // Setup button click handlers
        setupNavigationHandlers();
        setupSidebarToggleHandlers();
        setupSettingsHandlers();
        setupProfileHandlers();

        //for the toggle
        toggleBox.setOnAction(event -> {
            translateCircle(toggleCircle,settingToggleCircle);
        });

        settingToggleBox.setOnAction(event->translateCircle(toggleCircle,settingToggleCircle));
        toggleCircle.setOnMouseClicked(event ->translateCircle(toggleCircle,settingToggleCircle) );


    populateUserInfo();

    // Set up the icons of the names
    utils.setToolTip(projectIconBtn,"Project View");
    utils.setToolTip(dashboardIconBtn,"DashBoard View");
    if (loginUser != null) {
        String userBtn = loginUser.getUserRole().equals(role.MANAGER.toString()) ? "Supervisor View" : "Labor View";
        utils.setToolTip(userIconBtn, userBtn);
    }
    utils.setToolTip(reportIconBtn,"Report View");
    utils.setToolTip(profileViewIconBtn,"user Profile");
    utils.setToolTip(logoutIconBtn,"Logout");
    utils.setToolTip(iconBackBtn,"Back");
    utils.setToolTip(changePasswordIconBtn,"Change Password");
    utils.setToolTip(settingIconBtn,"setting");
    utils.setToolTip(darkIcon,"dark mode");
    utils.setToolTip(lightIcon,"light mode");

    reloadBtn.setOnMouseClicked(e->data.reload());

    setFirstPage();
}

private void setFirstPage(){
    utils.openFxml("viewProjects.fxml", loadPane);

}

private void setupNavigationHandlers() {
    // Dashboard navigation
    dashboardViewBtn.setOnMouseClicked(e -> {
        utils.openFxml("dashboard.fxml", loadPane);
        linkButton.setTabButtonName("Dashboard");
    });
    dashboardIconBtn.setOnMouseClicked(e -> {
        utils.openFxml("dashboard.fxml", loadPane);
//        linkButton.setTabButtonName();
    });

    // Project navigation
    projectViewBtn.setOnMouseClicked(e -> {
        utils.openFxml("viewProjects.fxml", loadPane);
        linkButton.setTabButtonName("Projects");
    });
    projectIconBtn.setOnMouseClicked(e -> {
        utils.openFxml("viewProjects.fxml", loadPane);
//        linkButton.setTabButtonName();
    });

    // User navigation
    userViewBtn.setOnMouseClicked(e -> {
        System.out.println();

    });
    userIconBtn.setOnMouseClicked(e -> {
        System.out.println();
    });

    // Report navigation
    reportViewBtn.setOnMouseClicked(e -> {
        System.out.println();

    });
    reportIconBtn.setOnMouseClicked(e -> {
        System.out.println();

    });
}

private void setupSidebarToggleHandlers() {
    showIconSideBtn.setOnMouseClicked(e -> showSidebar(iconSideBar, 60));
    showSideBtn.setOnMouseClicked(e -> showSidebar(sideBar, 200));
}

private void setupSettingsHandlers() {
    settingViewBtn.setOnMouseClicked(e -> showSidebar(settingBar, 200));
    settingIconBtn.setOnMouseClicked(e -> showSidebar(iconSettingBar, 60));

    backBtn.setOnMouseClicked(e -> showSidebar(sideBar, 200));
    iconBackBtn.setOnMouseClicked(e -> showSidebar(iconSideBar, 60));

    showIconSettingBtn.setOnMouseClicked(e -> showSidebar(iconSettingBar, 60));
    showSettingBtn.setOnMouseClicked(e -> showSidebar(settingBar, 200));
}

private void setupProfileHandlers() {
    if (profileViewBtn != null) {
        profileViewBtn.setOnMouseClicked(e -> showSidebar(profileBox, 200));
    }
    if (profileViewIconBtn != null) {
        profileViewIconBtn.setOnMouseClicked(e -> showSidebar(profileBox, 200));
    }

    if (revertBtn != null) {
        revertBtn.setOnMouseClicked(e -> {
            populateUserInfo();
            showSidebar(sideBar, 200);
        });
    }

    if (confirmBtn != null) {
        confirmBtn.setOnMouseClicked(e -> {
            applyProfileEdits();
            showSidebar(sideBar, 200);
        });
    }
}

private void showSidebar(VBox target, double width) {
    setBoxVisible(sideBar, target == sideBar);
    setBoxVisible(iconSideBar, target == iconSideBar);
    setBoxVisible(settingBar, target == settingBar);
    setBoxVisible(iconSettingBar, target == iconSettingBar);
    setBoxVisible(profileBox, target == profileBox);

    sideBarStackPane.setMinWidth(width);
    sideBarStackPane.setPrefWidth(width);
    sideBarStackPane.setMaxWidth(width);
}

private static void setBoxVisible(VBox box, boolean visible) {
    if (box == null) {
        return;
    }
    box.setVisible(visible);
    box.setManaged(visible);
}

private void populateUserInfo() {
    if (loginUser == null) {
        return;
    }

    if (nameViewText != null) {
        nameViewText.setText(loginUser.getUserName());
    }

    if (roleViewText != null) {
        roleViewText.setText(loginUser.getUserRole());
    }

    if (userNameLbl != null) {
        userNameLbl.setText(loginUser.getUserName());
    }

    if (userRoleLbl != null) {
        userRoleLbl.setText(loginUser.getUserRole());
    }

    if (userDobLbl != null && loginUser.getUserDOB() != null) {
        userDobLbl.setText(new SimpleDateFormat("dd-MM-yyyy").format(loginUser.getUserDOB()));
    }

    if (userEmailTxtField != null) {
        userEmailTxtField.setText(loginUser.getUserEmail() == null ? "" : loginUser.getUserEmail());
    }

    if (userPhoneTxtField != null) {
        userPhoneTxtField.setText(loginUser.getUserPhone() == null ? "" : loginUser.getUserPhone());
    }
}

private void applyProfileEdits() {
    if (loginUser == null) {
        return;
    }

    if (userEmailTxtField != null) {
        loginUser.setUserEmail(userEmailTxtField.getText());
    }
    if (userPhoneTxtField != null) {
        loginUser.setUserPhone(userPhoneTxtField.getText());
    }

    populateUserInfo();
}

private void translateCircle(Circle circle, Circle circle1) {
    TranslateTransition moving = new TranslateTransition(Duration.millis(300), circle);
    TranslateTransition moving1 = new TranslateTransition(Duration.millis(300), circle1);
    if (themeToggle.isDarkMode()) {
        moving.setToX(-10);
        moving1.setToX(-10);
    } else {
        moving.setToX(10);
        moving1.setToX(10);
    }
    moving.setOnFinished(event -> utils.changeTheme());
    ParallelTransition run = new ParallelTransition(moving1, moving);
    run.play();
}

public void addNew(String text) {
    if (text.equalsIgnoreCase("project")) {
        createAccPane.setVisible(false);
        createProjectPane.setVisible(true);
        createReportPane.setVisible(false);
        createLaborPane.setVisible(false);

    } else if (text.equalsIgnoreCase("user")) {
        createAccPane.setVisible(true);
        createProjectPane.setVisible(false);
        createReportPane.setVisible(false);
        createLaborPane.setVisible(false);
    } else if (text.equalsIgnoreCase("labor")) {
        createAccPane.setVisible(false);
        createProjectPane.setVisible(false);
        createReportPane.setVisible(false);
        createLaborPane.setVisible(true);
    } else if (text.equalsIgnoreCase("report")) {
        createAccPane.setVisible(false);
        createProjectPane.setVisible(false);
        createReportPane.setVisible(true);
        createLaborPane.setVisible(false);
    } else {
        return;
    }
}

}