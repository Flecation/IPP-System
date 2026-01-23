package IPPSystem.Controllers;

import IPPSystem.Models.projects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class projectDetailsController {

    @FXML
    private Label projectGeneral;

    @FXML
    private Label projectName;

    @FXML
    private Label projectStatus;

    @FXML
    public void initialize(){
        System.out.println("Project Details Controller loaded!");
    }

    //  Method to receive a projects object and display its data
    public void setProjectData(projects project){
        if(project != null){
            projectName.setText(project.getProjectInstanceName());
            projectStatus.setText(project.getProjectStatus());
            projectGeneral.setText(project.getProjectTypeName() + " - " + project.getProjectLocation());
        }
    }
}
