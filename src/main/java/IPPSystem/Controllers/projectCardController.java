package IPPSystem.Controllers;

import IPPSystem.Models.projects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;

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
        projectLocation.setText(p.getProjectLocation());
        duration.setText(p.getProjectDuration() + " Months");

        supervisorName.setText(p.getUserName());
        System.out.println(p.getUserName());


        projectStatus.setText(p.getProjectStatus());
        projectType.setText(p.getProjectTypeName());
        projectName.setText(p.getProjectInstanceName());
        detailsBtn.setOnMouseClicked(event -> showDetails());
        applyStatusStyle(p.getProjectStatus());
    }

    private void showDetails(){

        if (loadPane == null) {
            return;
        }

        String fxml = (detailsFxml == null || detailsFxml.isBlank()) ? "projectDetails.fxml" : detailsFxml;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/" + fxml));
            Parent newContent = loader.load();

            projectDetailsController controller = loader.getController();
            controller.setProjectData(project);

            if (newContent instanceof Region region) {
                region.prefWidthProperty().bind(loadPane.widthProperty());
                region.prefHeightProperty().bind(loadPane.heightProperty());
                region.setMaxWidth(Double.MAX_VALUE);
                region.setMaxHeight(Double.MAX_VALUE);
            }

            loadPane.getChildren().setAll(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void applyStatusStyle(String status) {

        projectStatus.getStyleClass().removeAll(
                "status-active",
                "status-completed",
                "status-planning"
        );

        switch (status.toLowerCase()) {
            case "active":
                projectStatus.getStyleClass().add("status-active");
                break;
            case "completed":
                projectStatus.getStyleClass().add("status-completed");
                break;
            case "planning":
                projectStatus.getStyleClass().add("status-planning");
                break;
        }
    }
}
