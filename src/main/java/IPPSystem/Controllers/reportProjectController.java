package IPPSystem.Controllers;

import IPPSystem.Models.DailyReport;
import IPPSystem.Models.projects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class reportProjectController {


    @FXML
    private Label projectName;

    @FXML
    private Label projectStatus;

    @FXML
//    private Label projectType;

    private DailyReport report;

    public void setData(projects p) {
        if (p == null) {
            projectName.setText("-");
            projectStatus.setText("-");
            projectName.getStyleClass().setAll("project-name");
            projectStatus.getStyleClass().setAll("status-default");
            return;
        }

        // Project name
        projectName.setText(p.getProjectInstanceName());
        projectName.getStyleClass().setAll("project-name");

        // Project status
        String status = p.getProjectStatus();
        projectStatus.setText(status);

        // Remove old status classes
        projectStatus.getStyleClass().removeIf(c -> c.startsWith("status-"));

        // Add class based on status
        switch (status.toLowerCase()) {
            case "planning" -> projectStatus.getStyleClass().add("status-planning");
            case "inprogress" -> projectStatus.getStyleClass().add("status-in-progress");
            case "finished" -> projectStatus.getStyleClass().add("status-finished");
            case "delayed", "risk" -> projectStatus.getStyleClass().add("status-delayed");
            case "cancelled" -> projectStatus.getStyleClass().add("status-cancelled");
            default -> projectStatus.getStyleClass().add("status-default");
        }
    }

    // reportProjectController.java
    @FXML
    private VBox projectCardRoot; // root node of reportProjects.fxml

    public VBox getProjectCardRoot() {
        return projectCardRoot;
    }




}
