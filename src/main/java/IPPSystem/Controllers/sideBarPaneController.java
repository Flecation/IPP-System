package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.*;
import com.almasb.fxgl.ui.InGamePanel;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;

import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

import IPPSystem.Interfaces.*;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class sideBarPaneController extends navigationPaneController{

    // holds the controller loaded into the add overlay (addNew)
    private Object currentAddController;


    /**
     * addNewPane is an overlay container defined as StackPane in sideBarPane.fxml.
     * It must match the FXML type to avoid FXMLLoader injection errors.
     */
    @FXML
    private StackPane addNewPane;

    @FXML
    private BorderPane basePane;


    @FXML
    private StackPane addNew;

    @FXML
    private HBox backBtn;

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
    private Circle logoIcon; // Added this field (exists in FXML)


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
    private Label userViewLbl;
    @FXML
    private Label dashboardIcon, projectIcon, userIcon, reportIcon, settingIcon, logoutIcon, changePwIcon, profileIcon;

    @FXML private Region overlayCatcher;


    //    ========== For the Search Bar Text =====================
    private final Popup suggestionPopup = new Popup();
    private final ListView<String> suggestionList = new ListView<>();
    private final PauseTransition debounce = new PauseTransition(Duration.millis(180));

    /** This is the controller of the currently loaded center page (viewProjectsController etc.). */
    private Object currentInnerController;

    private void setupSuggestionPopup() {
        suggestionList.getStyleClass().add("suggestion-list");
        suggestionList.setPrefHeight(220);

        suggestionPopup.setAutoHide(true);
        suggestionPopup.setHideOnEscape(true);
        suggestionPopup.getContent().add(suggestionList);

        // Click suggestion = fill + trigger search action
        suggestionList.setOnMouseClicked(e -> {
            String sel = suggestionList.getSelectionModel().getSelectedItem();
            if (sel != null) {
                searchTextField.setText(sel);
                searchTextField.positionCaret(sel.length());
                hideSuggestions();
                triggerSearch(sel);
            }
        });

        // Keyboard selection
        suggestionList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String sel = suggestionList.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    searchTextField.setText(sel);
                    searchTextField.positionCaret(sel.length());
                    hideSuggestions();
                    triggerSearch(sel);
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                hideSuggestions();
                searchTextField.requestFocus();
            }
        });
    }

    private void setupLiveSuggestions() {
        // Debounced typing to avoid spamming suggestions
        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            debounce.stop();
            debounce.setOnFinished(ev -> {
                String q = newVal == null ? "" : newVal.trim();
                if (q.isEmpty()) {
                    hideSuggestions();
                    triggerSearch("");
                    return;
                }
                updateSuggestions(q);
                triggerSearch(q);
            });
            debounce.playFromStart();
        });

        // Navigation keys
        searchTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN && suggestionPopup.isShowing()) {
                suggestionList.requestFocus();
                suggestionList.getSelectionModel().selectFirst();
                e.consume();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                hideSuggestions();
            }
        });

        // If user clicks in field, re-show suggestions (if any)
        searchTextField.focusedProperty().addListener((obs, was, is) -> {
            if (!is) hideSuggestions();
        });
    }

    private void updateSuggestions(String query) {
        List<String> suggestions = Collections.emptyList();

        Object ctrl = currentInnerController;
        if (ctrl instanceof SuggestablePage sp) {
            try {
                suggestions = sp.getSuggestions(query);
            } catch (Exception ignored) {
                suggestions = Collections.emptyList();
            }
        }

        if (suggestions == null || suggestions.isEmpty()) {
            hideSuggestions();
            return;
        }

        suggestionList.setItems(FXCollections.observableArrayList(suggestions));
        showSuggestionsBelow(searchTextField);
    }

    private void triggerSearch(String query) {
        Object ctrl = currentInnerController;
        if (ctrl instanceof SearchablePage searchable) {
            searchable.onSearch(query);
        }
    }

    private void showSuggestionsBelow(Node anchor) {
        Bounds b = anchor.localToScreen(anchor.getBoundsInLocal());
        if (b == null) return;

        // Width matches search field
        suggestionList.setPrefWidth(Math.max(260, b.getWidth()));

        if (!suggestionPopup.isShowing()) {
            suggestionPopup.show(anchor, b.getMinX(), b.getMaxY() + 2);
        } else {
            suggestionPopup.setX(b.getMinX());
            suggestionPopup.setY(b.getMaxY() + 2);
        }
    }

    private void hideSuggestions() {
        if (suggestionPopup.isShowing()) suggestionPopup.hide();
    }

    private void clearSearchTextOnly() {
        // only clear the text the user typed
        searchTextField.clear();

        // hide popup if it is open
        hideSuggestions();

        // optional UX: put focus back to search field
        searchTextField.requestFocus();

        // IMPORTANT:
        // Don't reload DB, don't clear cached lists.
        // Clearing the text will trigger your existing textProperty listener
        // and call triggerSearch("") automatically (if you set it up that way).
    }

    private users loginUser = user;

    protected storage data = storage.getInstance();

    private String currentInnerFxml = "viewProjects.fxml";

    public Object getCurrentInnerController() {
        return currentInnerController;
    }


    private static class ViewEntry {
        final Parent view;
        final Object controller;
        ViewEntry(Parent view, Object controller) {
            this.view = view;
            this.controller = controller;
        }
    }

    private final HashMap<String, ViewEntry> viewCache = new HashMap<>();

    //    For the catch the data in the load Pane and if click the open in new Tab btn to be work well
    public void openInnerView(String fxml) {
        openInnerView(fxml, null);
        //    System.out.println("openInnerView -> " + fxml + " | loadPane=" + System.identityHashCode(loadPane));

    }
    public void openInnerView(String fxml, java.util.function.Consumer<Object> controllerHook) {
        try {
            ViewEntry entry = viewCache.get(fxml);

            if (entry == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/" + fxml));
                Parent view = loader.load();
                Object controller = loader.getController();

                // ✅ inject loadPane into inner controllers
                if (controller instanceof loadPaneAware aware) {
                    aware.setLoadPane(loadPane);
                }

                entry = new ViewEntry(view, controller);
                viewCache.put(fxml, entry);
            }

            // ✅ ALWAYS update current controller (even when cached)
            currentInnerController = entry.controller;
            currentAddController = entry.controller;

            // ✅ allow caller to pass projectId etc.
            if (controllerHook != null) {
                controllerHook.accept(entry.controller);
            }

            loadPane.getChildren().setAll(entry.view);
            entry.view.toFront();
            currentInnerFxml = fxml;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @FXML
    public void initialize() {


//        ============ search bar action ================
        setupSuggestionPopup();
        setupLiveSuggestions();
        searchClearBtn.setOnMouseClicked(e->clearSearchTextOnly());

        // ✅ Let any inner controller access this sideBarPaneController (per-tab safe)
        loadPane.getProperties().put("SIDEBAR_CONTROLLER", this);

        // ✅ Critical: mark THIS sidebar root as belonging to THIS tab's inner loadPane.
        // utils.findTabLoadPane(...) will walk up parent nodes to find this and avoid cross-tab switching.
        if (loadPane != null) {
            loadPane.getProperties().put("TAB_LOAD_PANE", loadPane);
        }
        if (basePane != null && loadPane != null) {
            basePane.getProperties().put("TAB_LOAD_PANE", loadPane);
        }
        // ... rest of your initialize method remains the same
        utils.setFloatTextFieldStyle(userEmailLbl,userEmailTxtField);
        utils.setFloatTextFieldStyle(userPhoneLbl,userPhoneTxtField);

        lightDarkIconChange();
        circleMove();

        newTabBtn.setOnMouseClicked(e -> {

            String inner = currentInnerFxml;

            // 1) export state from current inner controller (if supported)
            java.util.Map<String, Object> state = java.util.Collections.emptyMap();
            if (currentInnerController instanceof IPPSystem.Interfaces.TabStateful ts) {
                state = ts.exportState();
            }

            String title = "New Tab";
            if (linkButton.getTabButton() != null) {
                title = linkButton.getTabButton().getText();
            }

            final java.util.Map<String, Object> finalState = state;

            // 2) create new tab, load same inner, then import state
            linkButton.createTabWithInitialInner("sideBarPane.fxml", title, inner, newSidebar -> {

                Object newInnerCtrl = newSidebar.getCurrentInnerController();
                if (newInnerCtrl instanceof IPPSystem.Interfaces.TabStateful ts2) {
                    ts2.importState(finalState);
                }
            });
        });


        utils.setToolTip(newTabBtn, "Open new tab");
        setIcons();

        addNewPane.setVisible(false);
        basePane.setVisible(true);

        setupAddOverlayOutsideClick();
        currentAddController = null;

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


        setupAddOverlayOutsideClick();      // call once here


    }

    //    For the add pane
    public void openAddOverlay(String fxml) {
        try {
            // We reuse openInnerView() (same loader/cache logic + loadPaneAware injection),
            // but we must NOT replace the current center page permanently.
            //
            // Flow:
            // 1) snapshot current center page (loadPane children + currentInner pointers)
            // 2) call openInnerView(fxml) to load into loadPane using the standard pipeline
            // 3) move that loaded root into overlay container (addNew)
            // 4) restore previous center page immediately
            final String prevFxml = currentInnerFxml;
            final Object prevController = currentInnerController;
            final java.util.List<Node> prevCenterChildren = new java.util.ArrayList<>(loadPane.getChildren());

            // Load using your standard pipeline; capture controller for later if needed
            openInnerView(fxml, controller -> currentAddController = controller);

            // Grab the loaded view (openInnerView puts it into loadPane)
            Parent loadedView = null;
            if (!loadPane.getChildren().isEmpty() && loadPane.getChildren().get(0) instanceof Parent p) {
                loadedView = p;
            }

            // Restore previous center page
            loadPane.getChildren().setAll(prevCenterChildren);
            currentInnerFxml = prevFxml;
            currentInnerController = prevController;

            // Put loaded view into overlay content holder
            addNew.getChildren().clear();
            if (loadedView != null) {
                addNew.getChildren().add(loadedView);
            }

            // Show overlay (and ensure it is on top)
            addNewPane.setVisible(true);
            addNewPane.setManaged(true);
            addNewPane.toFront();

            // Block clicks behind + dim background (keep your existing behavior)
            if (basePane != null) {
                basePane.setDisable(true);
                basePane.setOpacity(0.35);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupAddOverlayOutsideClick() {

        overlayCatcher.setOnMouseClicked(e -> {
            if (!addNewPane.isVisible()) return;

            // If you loaded a form controller that supports AddOverlayForm
            if (currentAddController instanceof IPPSystem.Interfaces.AddOverlayForm form) {

                // if empty -> close
                if (!form.hasUnsavedChanges()) {
                    closeAddOverlay();
                    e.consume();
                    return;
                }

                // if has data -> confirm discard
                messageBoxService.confirm(
                        "Discard changes?",
                        "You didn’t save anything. Do you want to close?",
                        notificationType.WARNING,
                        this::closeAddOverlay,
                        () -> {}
                );

                e.consume();
                return;
            }

            // fallback: just close
            closeAddOverlay();
            e.consume();
        });
    }


    public void closeAddOverlay() {
        addNew.getChildren().clear();
        addNewPane.setVisible(false);
        addNewPane.setManaged(false);

        basePane.setDisable(false);
        basePane.setOpacity(1.0);

        currentAddController = null;
    }

    private void handleOverlayOutsideClick() {

        // If form empty -> close immediately
        if (isAddFormEmpty()) {
            closeAddOverlay(); // or close current tab if you have that
            return;
        }

        // If user typed something -> ask confirm
        messageBoxService.confirm(
                "Discard changes?",
                "You didn’t save anything. Do you want to close?",
                notificationType.WARNING,
                () -> closeAddOverlay(),   // YES
                () -> {}                   // NO
        );
    }


    private boolean isAddFormEmpty() {
        if (addNew == null) return true;
        return isNodeTreeEmpty(addNew);
    }

    private boolean isNodeTreeEmpty(javafx.scene.Parent parent) {
        for (javafx.scene.Node n : parent.getChildrenUnmodifiable()) {

            // TextField
            if (n instanceof javafx.scene.control.TextField tf) {
                if (tf.getText() != null && !tf.getText().trim().isEmpty()) return false;
            }

            // TextArea
            if (n instanceof javafx.scene.control.TextArea ta) {
                if (ta.getText() != null && !ta.getText().trim().isEmpty()) return false;
            }

            // ComboBox
            if (n instanceof javafx.scene.control.ComboBox<?> cb) {
                if (cb.getValue() != null) return false;
            }

            // DatePicker
            if (n instanceof javafx.scene.control.DatePicker dp) {
                if (dp.getValue() != null) return false;
            }

            // CheckBox (optional: if checked counts as input)
            if (n instanceof javafx.scene.control.CheckBox chk) {
                if (chk.isSelected()) return false;
            }

            // Recurse down
            if (n instanceof javafx.scene.Parent p) {
                if (!isNodeTreeEmpty(p)) return false;
            }
        }
        return true; // nothing found
    }


    // ... rest of your methods remain the same
    private void setFirstPage(){
        if(user.getUserRole().equals(role.MANAGER.toString())){
            openInnerView("allProjectDashboard.fxml");
        }
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
        boolean isManager = loginUser.getUserRole().equals(role.MANAGER.toString());
        dashboardViewBtn.setOnMouseClicked(e -> {
            if (isManager) {
                openInnerView("allProjectDashboard.fxml");
                linkButton.setTabButtonName("Overall Dashboard");
            }else{
                openInnerView("currentProjectDashboard.fxml");
                linkButton.setTabButtonName("Current Dashboard");
            }

        });
        dashboardIconBtn.setOnMouseClicked(e -> {
            if (isManager) {
                openInnerView("allProjectDashboard.fxml");
                linkButton.setTabButtonName("Overall Dashboard");
            }else{
                openInnerView("currentProjectDashboard.fxml");
                linkButton.setTabButtonName("Current Dashboard");
            }
        });

        // Project navigation
        projectViewBtn.setOnMouseClicked(e -> {
            openInnerView("viewProjects.fxml");
            linkButton.setTabButtonName("Projects View");
        });
        projectIconBtn.setOnMouseClicked(e -> {
            openInnerView("viewProjects.fxml");
            linkButton.setTabButtonName("Projects View");
        });

        // User navigation
        userViewBtn.setOnMouseClicked(e -> {
            if( isManager)
            {
                openInnerView("engineerView.fxml");
                linkButton.setTabButtonName("Supervisors View");
            }else{
                openInnerView("laborView.fxml");
                linkButton.setTabButtonName("Labors View");
            }


        });
        userIconBtn.setOnMouseClicked(e->{
            if( isManager)
            {
                openInnerView("engineerView.fxml");
                linkButton.setTabButtonName("Supervisors View");
            }else{
                openInnerView("laborView.fxml");
                linkButton.setTabButtonName("Labors View");
            }
        });

        // Report navigation
        reportViewBtn.setOnMouseClicked(e -> {
            openInnerView("report.fxml");
            linkButton.setTabButtonName("Report View");
        });
        reportIconBtn.setOnMouseClicked(e -> {
            openInnerView("report.fxml");
            linkButton.setTabButtonName("Report View");
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
            darkIcon.setVisible(true);
            darkSymbolIcon.setVisible(true);
            lightIcon.setVisible(false);
            lightSymbolIcon.setVisible(false);
            toggleSymbolText.setText("Dark Mode");
        } else {
            // Currently light mode, so we're switching to dark mode
            moving.setToX(-10);
            moving1.setToX(-10);
            darkIcon.setVisible(false);
            darkSymbolIcon.setVisible(false);
            lightSymbolIcon.setVisible(true);
            lightIcon.setVisible(true);
            toggleSymbolText.setText("Light Mode");
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

    private void circleMove(){
        boolean isCurrentlyDarkMode = themeToggle.isDarkMode();
        if (isCurrentlyDarkMode) {
            // Currently dark mode, so we're switching to light mode
            toggleCircle.setTranslateX(10);
            settingToggleCircle.setTranslateX(10);
        } else {
            // Currently light mode, so we're switching to dark mode
            toggleCircle.setTranslateX(-10);
            settingToggleCircle.setTranslateX(-10);
        }
    }
}