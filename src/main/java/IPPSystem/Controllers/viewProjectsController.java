package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class viewProjectsController extends sideBarPaneController{

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
    public void initialize(){
        getProjects = database.getAllProjects();
        allBtn.setOnAction(e -> utils.showProjectCards(getProjects,projectContainer));
        completedBtn.setOnAction(e ->{});
        activeBtn.setOnAction(e -> {});
        planningBtn.setOnAction( e -> {});

        utils.showProjectCards(getProjects,projectContainer);

    }



}
