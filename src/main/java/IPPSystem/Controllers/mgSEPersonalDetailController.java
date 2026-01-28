package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
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

    private  users engineer;
    private StackPane loadPane;


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


        loadProjectCards();
        loadOtherEngineers();
    }

    public void loadProjectCards() {

        projectPane.getChildren().clear();

        List<projects> projects = database.getProjectsByEngineer(engineer.getUserId());
        System.out.println("Loading project cards...");

        for (projects project : projects) {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/View/projectCardByOne.fxml"));

                Parent card = loader.load();

                projectCardController controller = loader.getController();
                controller.setData(project, loadPane);

                projectPane.getChildren().add(card);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void loadOtherEngineers() {

        otherEngineersPane.getChildren().clear();

        ObservableList<users> users = (ObservableList<users>) database.getAllSupervisors();

        System.out.println("Loading project cards...");

        for (users user : users) {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/View/otherEngineers.fxml"));

                Parent card = loader.load();

                otherEngineersController controller = loader.getController();
                controller.setOtherEngineer(user);

                otherEngineersPane.getChildren().add(card);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
