package IPPSystem.Controllers;

import IPPSystem.Constants.projectStatus;
import IPPSystem.Constants.role;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.loadPaneAware;
import IPPSystem.Utils.session;
import IPPSystem.Utils.storage;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

    // Navigation can resolve the correct loadPane from any node inside the tab.
    // We keep this field for backward compatibility with loadPaneAware injection.
    private StackPane loadPane;

    @Override
    public void setLoadPane(StackPane loadPane) { this.loadPane = loadPane; }

    @FXML
    private void onAddNewProject() {
        // Resolve the current tab's loadPane from a node in this view
        StackPane lp = (loadPane != null) ? loadPane : utils.findTabLoadPane(viewProjectPane);
        if (lp == null) return;

        sideBarPaneController parent =
                (sideBarPaneController) lp.getProperties().get("SIDEBAR_CONTROLLER");

        if (parent != null) {
            parent.openAddOverlay("createProject.fxml"); // <-- your add page fxml
        } else {
            System.out.println("SIDEBAR_CONTROLLER not found on loadPane.");
        }
    }

    @FXML
    private void onClose() {
        StackPane lp = (loadPane != null) ? loadPane : utils.findTabLoadPane(viewProjectPane);
        if (lp == null) return;

        sideBarPaneController parent =
                (sideBarPaneController) lp.getProperties().get("SIDEBAR_CONTROLLER");
        if (parent != null) parent.closeAddOverlay();
    }


    private StatusFilter statusFilter = StatusFilter.ALL;

    @FXML
    public void initialize() {

        boolean isSupervisor = loginUser.getUserRole().equals(role.SUPERVISOR.toString());

        if (isSupervisor) {
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

        addBtn.setOnAction(e -> onAddNewProject());

        // status filter handlers
        allBtn.setOnAction(e -> { statusFilter = StatusFilter.ALL; applyFilters(); });
        activeBtn.setOnAction(e -> { statusFilter = StatusFilter.ACTIVE; applyFilters(); });
        completedBtn.setOnAction(e -> { statusFilter = StatusFilter.COMPLETED; applyFilters(); });
        planningBtn.setOnAction(e -> { statusFilter = StatusFilter.PLANNING; applyFilters(); });

        choiceUsersBox.setOnAction(e -> applyFilters());
        choiceProjectTypesBox.setOnAction(e -> applyFilters());


        // load dropdown data first, then auto "click" All on first open
        setAllDataInChoiceBoxAsync(() -> Platform.runLater(() -> allBtn.fire()));
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

        // Preferred: derive per-tab loadPane from any node in this view
        showProjectCards(filtered, projectContainer, viewProjectPane);


        if (filtered.isEmpty()) {
            noProjectLbl.setVisible(true);
            projectContainer.getChildren().add(noProjectLbl);
        } else {
            noProjectLbl.setVisible(false);
        }
    }

    private void setAllDataInChoiceBoxAsync(Runnable onDoneUi) {

        Task<ChoiceData> task = new Task<>() {
            @Override
            protected ChoiceData call() {

                ObservableList<String> userNames = FXCollections.observableArrayList();
                ObservableList<String> types = FXCollections.observableArrayList();

                userNames.add("All");
                types.add("All");

                // project types
                data.getProjectTypes().values().forEach(t -> {
                    if (t != null && !t.isBlank()) types.add(t);
                });

                // supervisors list
                for (users u : data.getAllUsers()) {
                    if (u != null
                            && role.SUPERVISOR.toString().equals(u.getUserRole())
                            && u.getUserName() != null
                            && !u.getUserName().isBlank()) {
                        userNames.add(u.getUserName());
                    }
                }

                return new ChoiceData(userNames, types);
            }
        };

        task.setOnSucceeded(e -> {
            ChoiceData cd = task.getValue();

            choiceUsersBox.setItems(cd.userNames);
            choiceProjectTypesBox.setItems(cd.types);

            choiceUsersBox.getSelectionModel().selectFirst();
            choiceProjectTypesBox.getSelectionModel().selectFirst();

            if (onDoneUi != null) onDoneUi.run();
        });

        task.setOnFailed(e -> {
            System.out.println("Failed to load choice boxes: " +
                    (task.getException() == null ? "unknown" : task.getException().getMessage()));

            choiceUsersBox.getItems().setAll("All");
            choiceProjectTypesBox.getItems().setAll("All");
            choiceUsersBox.getSelectionModel().selectFirst();
            choiceProjectTypesBox.getSelectionModel().selectFirst();

            if (onDoneUi != null) onDoneUi.run();
        });

        Thread t = new Thread(task, "load-choice-boxes");
        t.setDaemon(true);
        t.start();
    }


    private static class ChoiceData {
        final ObservableList<String> userNames;
        final ObservableList<String> types;

        ChoiceData(ObservableList<String> userNames, ObservableList<String> types) {
            this.userNames = userNames;
            this.types = types;
        }
    }


}
