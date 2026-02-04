package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.storage;
import IPPSystem.Utils.themeToggle;
import IPPSystem.Utils.utils;
import com.almasb.fxgl.ui.InGamePanel;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;

import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import org.controlsfx.glyphfont.FontAwesome;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class sideBarPaneController extends navigationPaneController{

    @FXML
    private BorderPane addNewPane,basePane;

    @FXML
    private StackPane addNew;

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
    private Circle imageCircle, imageBtn; // Changed from logoIcon to locoIcon

    @FXML
    private Circle locoIcon; // Added this field (exists in FXML)

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
    private Label userViewLbl, addNewTitle;

    @FXML
    private Button addNewExitBtn;

    @FXML
    private Label dashboardIcon, projectIcon, userIcon, reportIcon, settingIcon, logoutIcon, changePwIcon, profileIcon;

    @FXML
    private VBox createLaborPane, createProjectPane, createReportPane;

    @FXML
    private Button createAccCancelBtn; // Exists in FXML but wasn't in controller

    @FXML
    private Button createAccCreateBtn; // Exists in FXML but wasn't in controller

    // Removed userViewImage field as it doesn't exist in FXML
    // Removed imageIconBtn field as it doesn't exist in FXML

    private users loginUser = user;

    protected storage data = storage.getInstance();

    @FXML
    public void initialize() {
        // ... rest of your initialize method remains the same
        utils.setFloatTextFieldStyle(userEmailLbl,userEmailTxtField);
        utils.setFloatTextFieldStyle(userPhoneLbl,userPhoneTxtField);

        lightDarkIconChange();

        setIcons();

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
        toggleBox.setOnAction(event -> translateCircle(toggleCircle,settingToggleCircle));
        toggleBtn.setOnMouseClicked(event -> translateCircle(toggleCircle,settingToggleCircle));
        settingToggleBox.setOnAction(event->translateCircle(toggleCircle,settingToggleCircle));
        toggleCircle.setOnMouseClicked(event ->translateCircle(toggleCircle,settingToggleCircle));

        populateUserInfo();

        // Set up the icons of the names
        utils.setToolTip(projectIconBtn,"Project View");
        utils.setToolTip(dashboardIconBtn,"DashBoard View");
        if (loginUser != null) {
            String userBtn = loginUser.getUserRole().equals(role.MANAGER.toString()) ? "Supervisor View" : "Labor View";
            utils.setToolTip(userIconBtn, userBtn);
            userViewLbl.setText(userBtn);
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

//        utils.switchNewScene(logoutIcon,"login.fxml");

    }

    // ... rest of your methods remain the same
    private void setFirstPage(){
        utils.openFxml("viewProjects.fxml", loadPane);
    }

    private void setIcons(){
        // For Assigning the icons
        // Main icons
        // Icon with text
        dashboardIcon.setGraphic(utils.iconSet(FontAwesomeSolid.DESKTOP));
        projectIcon.setGraphic(utils.iconSet(FontAwesomeSolid.FOLDER));
        userIcon.setGraphic(utils.iconSet(FontAwesomeSolid.USER));
        reportIcon.setGraphic(utils.iconSet(FontAwesomeSolid.CHART_BAR));
        settingIcon.setGraphic(utils.iconSet(FontAwesomeSolid.COG));
        profileIcon.setGraphic(utils.iconSet(FontAwesomeSolid.USER));
        changePwIcon.setGraphic(utils.iconSet(FontAwesomeSolid.KEY));
        lightSymbolIcon.setGraphic(utils.iconSet(FontAwesomeSolid.SUN));
        darkSymbolIcon.setGraphic(utils.iconSet(FontAwesomeSolid.MOON));
        logoutIcon.setGraphic(utils.iconSet(FontAwesomeSolid.SIGN_OUT_ALT));

        // back and to icon
        iconBackBtn.setGraphic(utils.iconSet(FontAwesomeSolid.CARET_LEFT));
        showIconSideBtn.setGraphic(utils.iconSet(FontAwesomeSolid.CARET_LEFT));
        showSideBtn.setGraphic(utils.iconSet(FontAwesomeSolid.CARET_RIGHT));
        showSettingBtn.setGraphic(utils.iconSet(FontAwesomeSolid.CARET_RIGHT));

        // Icons only
        dashboardIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.DESKTOP));
        projectIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.FOLDER));
        userIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.USER));
        reportIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.CHART_BAR));
        settingIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.COG));
        profileViewIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.USER));
        logoutIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.SIGN_OUT_ALT));
        changePasswordIconBtn.setGraphic(utils.iconSet(FontAwesomeSolid.KEY));
        darkIcon.setGraphic(utils.iconSet(FontAwesomeSolid.MOON));
        lightIcon.setGraphic(utils.iconSet(FontAwesomeSolid.SUN));

        // Action icons
        reloadBtn.setGraphic(utils.iconSet(FontAwesomeSolid.SYNC_ALT));
        notificationBtn.setGraphic(utils.iconSet(FontAwesomeSolid.BELL));
        searchBtn.setGraphic(utils.iconSet(FontAwesomeSolid.SEARCH));
        searchClearBtn.setGraphic(utils.iconSet(FontAwesomeSolid.TIMES));
        newTabBtn.setGraphic(utils.iconSet(FontAwesomeSolid.EXTERNAL_LINK_ALT));
    }

    private void lightDarkIconChange(){
        // Get the CURRENT theme state (after it was changed)
        if (themeToggle.isDarkMode()){
            // It's DARK mode now, so show MOON icons, hide SUN icons
            darkIcon.setVisible(true);
            darkSymbolIcon.setVisible(true);
            lightIcon.setVisible(false);
            lightSymbolIcon.setVisible(false);
            toggleSymbolText.setText("Dark Mode");// Also update the text
        } else {
            // It's LIGHT mode now, so show SUN icons, hide MOON icons
            darkIcon.setVisible(false);
            darkSymbolIcon.setVisible(false);
            lightSymbolIcon.setVisible(true);
            lightIcon.setVisible(true);
            toggleSymbolText.setText("Light Mode"); // Also update the text
        }
    }

    private void setupNavigationHandlers() {
        // Dashboard navigation
        dashboardViewBtn.setOnMouseClicked(e -> {
            utils.openFxml("dashboard.fxml", loadPane);
            linkButton.setTabButtonName("Dashboard");
        });
        dashboardIconBtn.setOnMouseClicked(e -> {
            utils.openFxml("dashboard.fxml", loadPane);
        });

        // Project navigation
        projectViewBtn.setOnMouseClicked(e -> {
            utils.openFxml("viewProjects.fxml", loadPane);
            linkButton.setTabButtonName("Projects");
        });
        projectIconBtn.setOnMouseClicked(e -> {
            utils.openFxml("viewProjects.fxml", loadPane);
        });

        // User navigation
        userViewBtn.setOnMouseClicked(e -> {

           if( loginUser.getUserRole().equals(role.MANAGER.toString()))
           {
               utils.openFxml("mgSEListView.fxml",loadPane);
           }else if(loginUser.getUserRole().equals(role.SUPERVISOR.toString())){
               utils.openFxml("laborView.fxml",loadPane);
           }


        });
        userIconBtn.setOnMouseClicked(e -> {
            System.out.println();
        });

        // Report navigation
        reportViewBtn.setOnMouseClicked(e -> {
            utils.openFxml("report.fxml", loadPane);
            linkButton.setTabButtonName("Report");
        });
        reportIconBtn.setOnMouseClicked(e -> {
            utils.openFxml("report.fxml", loadPane);
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
            });
        }

        if (confirmBtn != null) {
            confirmBtn.setOnMouseClicked(e -> {
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

    public static void setBoxVisible(VBox box, boolean visible) {
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

    private void translateCircle(Circle circle, Circle circle1) {
        TranslateTransition moving = new TranslateTransition(Duration.millis(300), circle);
        TranslateTransition moving1 = new TranslateTransition(Duration.millis(300), circle1);

        // Get the CURRENT state BEFORE changing it
        boolean isCurrentlyDarkMode = themeToggle.isDarkMode();

        if (isCurrentlyDarkMode) {
            // Currently dark mode, so we're switching to light mode
            moving.setToX(10);
            moving1.setToX(10);
        } else {
            // Currently light mode, so we're switching to dark mode
            moving.setToX(-10);
            moving1.setToX(-10);
        }

        moving.setOnFinished(event -> {
            // Change the theme first
            utils.changeTheme();
            // Then update the icons based on the NEW theme state
            lightDarkIconChange();
        });

        ParallelTransition run = new ParallelTransition(moving1, moving);
        run.play();
    }
}