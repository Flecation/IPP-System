package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class viewProjectsController extends sideBarPaneController {

    @FXML
    private VBox viewProjectPane;

    @FXML
    private Button activeBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button allBtn;

    @FXML
    private Button completedBtn;

    @FXML
    private Button planningBtn;

    @FXML
    private VBox projectContainer;

    @FXML
    private Label noProjectLbl,choiceUserLbl;

    @FXML
    private ComboBox<String> choiceUsersBox, choiceProjectTypesBox;

    private enum StatusFilter {
        ALL, ACTIVE, COMPLETED, PLANNING
    }

    private StatusFilter statusFilter = StatusFilter.ALL;

    protected users loginUser = user;

    @FXML
    public void initialize() {

        if (loginUser.getUserRole().equals(role.SUPERVISOR.toString())){
            choiceUsersBox.setDisable(true);
            choiceUsersBox.setVisible(false);
            addBtn.setVisible(false);
            choiceUserLbl.setVisible(false);
        }else {
            choiceUsersBox.setVisible(true);
            choiceUsersBox.setDisable(false);
            choiceUserLbl.setVisible(true);
            addBtn.setVisible(true);
        }
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
        boolean check = loginUser.getUserRole().equals(role.SUPERVISOR.toString());

        // Clear the container FIRST to prevent duplication
        projectContainer.getChildren().clear();

        if (check){
            source.setAll(data.getProjectsByUserId(loginUser.getUserId()));
        }else{
            source.setAll(data.getAllProjects());
        }

        ObservableList<projects> filtered = FXCollections.observableArrayList();

        String selectedUser = choiceUsersBox.getSelectionModel().getSelectedItem();
        String selectedType = choiceProjectTypesBox.getSelectionModel().getSelectedItem();

        // For supervisor, they should always see only their own projects
        // unless they specifically filter by other supervisors (which is disabled)
        for (projects p : source) {
            if (p == null) continue;

            // Apply status filter
            if (statusFilter != StatusFilter.ALL) {
                String status = safeLower(p.getProjectStatus());
                if (statusFilter == StatusFilter.ACTIVE && !status.equals("active")) continue;
                if (statusFilter == StatusFilter.COMPLETED && !status.equals("completed")) continue;
                if (statusFilter == StatusFilter.PLANNING && !status.equals("planning")) continue;
            }

            // Apply project type filter
            if (selectedType != null && !selectedType.equals("All")) {
                String typeName = p.getProjectTypeName();
                if (typeName == null || !typeName.equalsIgnoreCase(selectedType)) continue;
            }

            // Apply user filter (only for non-supervisors or if supervisor filter is enabled)
            if (!check && selectedUser != null && !selectedUser.equals("All")) {
                String supervisor = p.getUserName();
                if (supervisor == null || !supervisor.equalsIgnoreCase(selectedUser)) continue;
            }

            filtered.add(p);
        }

        // Now show the cards - the container is already cleared
        utils.showProjectCards(filtered, projectContainer);

        // Show/hide "no projects" label
        if (filtered.isEmpty()) {
            noProjectLbl.setVisible(true);
            projectContainer.getChildren().add(noProjectLbl);
        }else{
            noProjectLbl.setVisible(false);
        }
    }

    private String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
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