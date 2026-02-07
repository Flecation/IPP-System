package IPPSystem.Controllers;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Constants.projectStatus;
import IPPSystem.Interfaces.NavAware;
import IPPSystem.Models.projects;
import IPPSystem.Utils.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;

public class projectCardController implements NavAware {
    private sideBarPaneController nav;

    @Override
    public void setNav(sideBarPaneController nav){
        this.nav = nav;
    }


    @FXML
    private Button detailsBtn;

    @FXML
    private Label duration;
    @FXML
    private Label projectID;
    @FXML
    private Label projectLocation;
    @FXML
    private Label projectName;
    @FXML
    private Label projectStatus;
    @FXML
    private Label projectType;
    @FXML
    private Label supervisorName;

    private projects project;

    private StackPane loadPane;
    private String detailsFxml;




    public void setData(projects p,StackPane pane) {
        this.project = p;
        this.loadPane = pane;
        if (p == null) {
            return;
        }

        if (projectID != null) {
            projectID.setText(utils.generateProjectId(p.getAssignProjectId()));
        }
        HashMap<enumDuration,Double > d = utils.getDuration(p.getProjectDuration());
        String month = d.get(enumDuration.MONTH)+" Months";
        projectLocation.setText(p.getProjectLocation());
        duration.setText(month);
        supervisorName.setText(p.getUserName());
        projectStatus.setText(p.getProjectStatus());
        projectType.setText(p.getProjectTypeName());
        projectName.setText(p.getProjectInstanceName());
        // Use any node inside the current tab so utils can find the correct per-tab loadPane
        detailsBtn.setOnMouseClicked(event -> {
            nav.openInnerView("projectDetails.fxml", ctrl -> {
                if (ctrl instanceof projectDetailsController c) {
                    c.setProjectData(project);
                    c.setNav(nav); // ✅ inject directly
                }
            });

        });
        applyStatusStyle(p.getProjectStatus());
    }




    private void applyStatusStyle(String status) {

        projectStatus.getStyleClass().removeAll(
                "status-active",
                "status-completed",
                "status-planning"
        );

        if (status.equals(IPPSystem.Constants.projectStatus.PLANNING.toString())){
            projectStatus.getStyleClass().add("status-planning");
        }else if(status.equals(IPPSystem.Constants.projectStatus.PROGRESSING.toString())){
            projectStatus.getStyleClass().add("status-active");
        }else if(status.equals(IPPSystem.Constants.projectStatus.FINISH.toString())){
            projectStatus.getStyleClass().add("status-finished");
        }
    }
}
