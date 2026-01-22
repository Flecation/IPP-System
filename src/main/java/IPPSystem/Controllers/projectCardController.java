


    package IPPSystem.Controllers;

import IPPSystem.Models.projects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

    public class projectCardController {

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
        private Button projectStatus;

        @FXML
        private Label projectType;

        @FXML
        private Label suprName;

        public void setData(projects p){
            projectLocation.setText(p.getProjectLocation());
            duration.setText(p.getProjectDuration()+ "Years");
            suprName.setText(p.getUserName());
            projectStatus.setText(p.getProjectStatus());
            projectType.setText(p.getProjectTypeName());
            projectName.setText(p.getProjectInstanceName());

            detailsBtn.setOnAction(e->
            {
                System.out.println("View Details : " + p.getProjectInstanceName());
            });
        }
    }


