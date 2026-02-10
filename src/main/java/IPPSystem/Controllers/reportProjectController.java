package IPPSystem.Controllers;

import IPPSystem.Models.DailyReport;
import IPPSystem.Models.projects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class reportProjectController {

    @FXML
    private Label projectName;

    @FXML
    private Label projectStatus;

    @FXML
    private Rectangle statusColor;

    @FXML
    private VBox projectCardRoot;

    private DailyReport report;

    public void setData(projects p) {
        if (p == null) {
            projectName.setText("-");
            projectStatus.setText("-");
            projectName.getStyleClass().setAll("project-name");
            projectStatus.getStyleClass().setAll("status-default");
            statusColor.setFill(javafx.scene.paint.Color.valueOf("#6b7280")); // Default gray color
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

        // Set status color and CSS class based on status
        String colorCode;
        String cssClass;

        // Normalize the status string (case-insensitive, trim spaces)
        String normalizedStatus = status.toLowerCase().trim();

        switch (normalizedStatus) {
            case "planning" -> {
                colorCode = "#f59e0b"; // Orange
                cssClass = "status-planning";
            }
            case "inprogress" -> {
                colorCode = "#38bdf8"; // Light Blue
                cssClass = "status-in-progress";
            }
            case "delay", "delayed" -> {
                colorCode = "#7c3aed"; // Purple
                cssClass = "status-delayed";
            }
            case "finished", "completed" -> {
                colorCode = "#22c55e"; // Green
                cssClass = "status-finished";
            }
            case "cancelled" -> {
                colorCode = "#ef4444"; // Red (added for cancelled)
                cssClass = "status-cancelled";
            }
            case "risk" -> {
                colorCode = "#f97316"; // Orange-Red (added for risk)
                cssClass = "status-risk";
            }
            default -> {
                colorCode = "#6b7280"; // Gray
                cssClass = "status-default";
            }
        }

        // Set the rectangle color
        statusColor.setFill(javafx.scene.paint.Color.valueOf(colorCode));

        // Add CSS class for the status label
        projectStatus.getStyleClass().add(cssClass);
    }

    public VBox getProjectCardRoot() {
        return projectCardRoot;
    }
}