package IPPSystem.Controllers;

import IPPSystem.Constants.projectStatus;
import IPPSystem.Constants.role;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.loadPaneAware;
import IPPSystem.Utils.session;
import IPPSystem.Utils.storage;
import IPPSystem.Utils.utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static IPPSystem.Controllers.navigationPaneController.user;
import static IPPSystem.Utils.utils.showProjectCards;

public class viewProjectsController implements loadPaneAware {

    @FXML private VBox viewProjectPane;
    @FXML private Button activeBtn;
    @FXML private Button addBtn;
    @FXML private Button allBtn;
    @FXML private Button completedBtn;
    @FXML private Button planningBtn;
    @FXML private VBox projectContainer;
    @FXML private Label noProjectLbl, choiceUserLbl;
    @FXML private ComboBox<String> choiceUsersBox, choiceProjectTypesBox;

    protected final storage data = storage.getInstance();
    protected final users loginUser = session.getInstance().getUser();

    private enum StatusFilter {
        ALL, ACTIVE, COMPLETED, PLANNING
    }

    private StackPane loadPane;

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    private void onAddNewProject() {
        sideBarPaneController parent =
                (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");

        if (parent != null) {
            parent.openAddOverlay("createProject.fxml"); // <-- your add page fxml
        } else {
            System.out.println("SIDEBAR_CONTROLLER not found on loadPane.");
        }
    }

    @FXML
    private void onClose() {
        sideBarPaneController parent =
                (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        if (parent != null) parent.closeAddOverlay();
    }


    private StatusFilter statusFilter = StatusFilter.ALL;

    @FXML
    public void initialize() {
        if (loginUser.getUserRole().equals(role.SUPERVISOR.toString())) {
            choiceUsersBox.setDisable(true);
            choiceUsersBox.setVisible(false);
            addBtn.setVisible(false);
            choiceUserLbl.setVisible(false);
        } else {
            choiceUsersBox.setVisible(true);
            choiceUsersBox.setDisable(false);
            choiceUserLbl.setVisible(true);
            addBtn.setVisible(true);
        }

        addBtn.setOnAction(e->{
            onAddNewProject();
        });

        setAllDataInChoiceBox();

        allBtn.setOnAction(e -> {
            statusFilter = StatusFilter.ALL;
            applyFilters();
        });

        activeBtn.setOnAction(e -> {
            statusFilter = StatusFilter.ACTIVE;
            applyFilters();
        });

        completedBtn.setOnAction(e -> {
            statusFilter = StatusFilter.COMPLETED;
            applyFilters();
        });

        planningBtn.setOnAction(e -> {
            statusFilter = StatusFilter.PLANNING;
            applyFilters();
        });

        choiceUsersBox.setOnAction(e -> applyFilters());
        choiceProjectTypesBox.setOnAction(e -> applyFilters());

        applyFilters();
    }

    private void applyFilters() {
        ObservableList<projects> source = FXCollections.observableArrayList();
        boolean isSupervisor = loginUser.getUserRole().equals(role.SUPERVISOR.toString());

        projectContainer.getChildren().clear();

        if (isSupervisor) {
            source.setAll(data.getProjectsByUserId(loginUser.getUserId()));
        } else {
            source.setAll(data.getAllProjects());
        }

        ObservableList<projects> filtered = FXCollections.observableArrayList();

        String selectedUser = choiceUsersBox.getSelectionModel().getSelectedItem();
        String selectedType = choiceProjectTypesBox.getSelectionModel().getSelectedItem();

        for (projects p : source) {
            if (p == null) continue;

            // ✅ FIXED: status filter using ProjectStatus enum
            if (statusFilter != StatusFilter.ALL) {
                projectStatus ps = projectStatus.fromString(p.getProjectStatus());

                if (statusFilter == StatusFilter.ACTIVE) {
                    if (ps == null || !ps.isActive()) continue;
                }

                if (statusFilter == StatusFilter.COMPLETED) {
                    if (ps == null || !ps.isCompleted()) continue;
                }

                if (statusFilter == StatusFilter.PLANNING) {
                    if (ps != projectStatus.PLANNING) continue;
                }
            }

            // project type filter
            if (selectedType != null && !selectedType.equals("All")) {
                String typeName = p.getProjectTypeName();
                if (typeName == null || !typeName.equalsIgnoreCase(selectedType)) continue;
            }

            // user filter (only for non-supervisors)
            if (!isSupervisor && selectedUser != null && !selectedUser.equals("All")) {
                String supervisor = p.getUserName();
                if (supervisor == null || !supervisor.equalsIgnoreCase(selectedUser)) continue;
            }

            filtered.add(p);
        }

        showProjectCards(filtered, projectContainer, loadPane);


        if (filtered.isEmpty()) {
            noProjectLbl.setVisible(true);
            projectContainer.getChildren().add(noProjectLbl);
        } else {
            noProjectLbl.setVisible(false);
        }
    }

    private void setAllDataInChoiceBox() {
        choiceUsersBox.getItems().clear();
        choiceProjectTypesBox.getItems().clear();

        choiceUsersBox.getItems().add("All");
        choiceProjectTypesBox.getItems().add("All");

        for (String type : data.getProjectTypes().values()) {
            if (type != null) choiceProjectTypesBox.getItems().add(type);
        }

        for (users u : data.getAllUsers()) {
            if (u != null && u.getUserRole() != null
                    && u.getUserRole().equals(role.SUPERVISOR.toString())) {
                choiceUsersBox.getItems().add(u.getUserName());
            }
        }

        choiceProjectTypesBox.getSelectionModel().selectFirst();
        choiceUsersBox.getSelectionModel().selectFirst();
    }
}
