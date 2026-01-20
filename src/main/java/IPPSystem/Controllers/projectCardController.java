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
    private Label projectLocation;

    @FXML
    private Label siteManager;

    public void setData(projects p){
        projectLocation.setText(p.getProjectLocation());
        duration.setText(p.getProjectDuration()+ "Years");
        siteManager.setText(p.getUserName());

        detailsBtn.setOnAction(e->
        {
            System.out.println("View Details : " + p.getProjectInstanceName());
        });
    }
}
