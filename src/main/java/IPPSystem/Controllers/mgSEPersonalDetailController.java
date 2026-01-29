package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.PaginationHelper;
import IPPSystem.Utils.utils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class mgSEPersonalDetailController {

    @FXML
    private HBox paginationBox;

    @FXML
    private Label role;

    @FXML
    private Label Address;

    @FXML
    private Label Email;

    @FXML
    private Circle EngineerListToggle;

    @FXML
    private Label Phone;

    @FXML
    private Label dob;

    @FXML
    private Label engineerName1;

    @FXML
    private Label engineerName2;


    @FXML
    private Label performancePercent;

    @FXML
    private ProgressBar performanceProgress;

    @FXML
    private HBox projectPane;

    @FXML
    private VBox otherEngineersPane;

    @FXML
    private Label status1;

    @FXML
    private Label workloadPercent;

    @FXML
    private ProgressBar workloadProgress;



    private users engineer;
    private StackPane loadPane;


    // 1. Define the PaginationHelper
    private PaginationHelper<projects> projectPagination;

    @FXML
    public void initialize() {
        projectPagination = new PaginationHelper<>(2);

        // 3. Link the listener to the render method
        projectPagination.setOnPageChanged(this::renderProjectPage);
    }


    public void setLoadPane(StackPane pane){
        this.loadPane = pane;
    }



    public void setEngineer(users engineer) {
        this.engineer = engineer;

        engineerName1.setText(engineer.getUserName());
        engineerName2.setText(engineer.getUserName());
        role.setText("Engineer");
        Phone.setText(engineer.getUserPhone());
        Email.setText(engineer.getUserEmail());
        Address.setText(engineer.getUserAddress());
        dob.setText(engineer.getUserDOB().toString());

        String statusText = engineer.isActive() ? "Active" : "Inactive";
        status1.setText(statusText);

        // 1. Current Workload (30% means 30/100 units of work assigned today)
        double workload = database.getWorkload(engineer.getUserId());
        workloadProgress.setProgress(workload);
        workloadPercent.setText((int)(workload * 100) + "%");

        // 2. All-time Performance (30% means they finished 3 out of 10 projects)
        double performance = database.getPerformance(engineer.getUserId());
        performanceProgress.setProgress(performance);
        performancePercent.setText((int)(performance * 100) + "%");


        List<projects> allEngineerProjects = database.getProjectsByEngineer(engineer.getUserId());

        // IMPORTANT: Clear the UI immediately if there are no projects
        if (allEngineerProjects == null || allEngineerProjects.isEmpty()) {
            projectPane.getChildren().clear();
            paginationBox.getChildren().clear(); // Hide pagination if no data

            // Optional: Add a "No Projects" message
            projectPane.getChildren().add(new javafx.scene.control.Label("No projects assigned."));

            // Update pagination helper with empty list to reset internal state
            projectPagination.setData(new java.util.ArrayList<>());
        } else {
            // Normal pagination flow
            projectPagination.setData(allEngineerProjects);
            projectPagination.goToPage(1);
        }

        loadOtherEngineers();
    }

    private void renderProjectPage(List<projects> pageData) {
        // This is the first thing that should happen to prevent "ghost" projects
        projectPane.getChildren().clear();

        if (pageData != null && !pageData.isEmpty()) {
            for (projects project : pageData) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/projectCardByOne.fxml"));
                    Parent card = loader.load();
                    projectCardController controller = loader.getController();
                    controller.setData(project, loadPane);
                    projectPane.getChildren().add(card);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        projectPagination.buildButtons(paginationBox);
    }



//    public void loadProjectCards() {
//
//        projectPane.getChildren().clear();
//
//        List<projects> projects = database.getProjectsByEngineer(engineer.getUserId());
////        System.out.println("Loading project cards...");
//
//        for (projects project : projects) {
//            try {
//                FXMLLoader loader =
//                        new FXMLLoader(getClass().getResource("/View/projectCardByOne.fxml"));
//
//                Parent card = loader.load();
//
//                projectCardController controller = loader.getController();
//                controller.setData(project, loadPane);
//
//                projectPane.getChildren().add(card);
//
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


    public void loadOtherEngineers() {

        otherEngineersPane.getChildren().clear();

        ObservableList<users> users = (ObservableList<users>) database.getAllSupervisors();

//        System.out.println("Loading engineers...");

        // Put current engineer first
        users.sort((u1, u2) -> {
            if (u1.getUserId() == engineer.getUserId()) return -1;
            if (u2.getUserId() == engineer.getUserId()) return 1;
            return 0;
        });

        for (users user : users) {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/View/otherEngineers.fxml"));

                Parent card = loader.load();

                otherEngineersController controller = loader.getController();

                boolean isCurrentEngineer = user.getUserId() == engineer.getUserId();

                controller.setOtherEngineer(user, isCurrentEngineer, this);

                otherEngineersPane.getChildren().add(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }







}
