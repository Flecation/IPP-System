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
    private Label projectStatus;
    @FXML
    private Label projectType;
    @FXML
    private Label suprName;

    private projects project;
    private viewProjectsController parentController;

    @FXML
    public void initialize() {
        detailsBtn.setOnAction(e -> {
            if (parentController != null && project != null) {
                parentController.openProjectDetails(project);
            }
        });
    }

    public void setParentController(viewProjectsController controller) {
        this.parentController = controller;
    }

    public void setData(projects p) {
        this.project = p; // ✅ VERY IMPORTANT

        projectLocation.setText(p.getProjectLocation());
        duration.setText(p.getProjectDuration() + " Years");
        suprName.setText(p.getUserName());
        projectStatus.setText(p.getProjectStatus());
        projectType.setText(p.getProjectTypeName());
        projectName.setText(p.getProjectInstanceName());

        applyStatusStyle(p.getProjectStatus());
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
