package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.labors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class laborRowController {

    @FXML private Button actionResignBtn;
    @FXML private Label assignedProject;
    @FXML private Label laborEndDate;
    @FXML private Label laborNRC;
    @FXML private Label laborName;
    @FXML private Label laborPhone;
    @FXML private Label laborStartDate;
    @FXML private Label laborStatus;

    private labors currentLabor;
    private Runnable refreshCallback; // callback to parent
    private final database db = new database();

    // Called by parent to pass data and callback
    public void setData(labors labor, Runnable refreshCallback) {
        this.currentLabor = labor;
        this.refreshCallback = refreshCallback;

        laborName.setText(labor.getLaborName());
        laborNRC.setText(labor.getLaborNRC());
        laborPhone.setText(labor.getLaborPhone());
        laborStartDate.setText(labor.getLaborStartDate().toString());
        laborEndDate.setText(labor.getLaborEndDate() != null ? labor.getLaborEndDate().toString() : "-");

        assignedProject.setText(db.getAssignedProjectName(labor.getLaborId()));

        updateStatusUI(labor);
    }

    @FXML
    void actionResign(ActionEvent event) {
        if (currentLabor == null || !currentLabor.isActive()) return;

        boolean success = db.resignLabor(currentLabor.getLaborId());

        if (success) {
            // update model
            currentLabor.setActive(false);
            currentLabor.setLaborEndDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            // update this row UI instantly
            laborEndDate.setText(currentLabor.getLaborEndDate().toString());
            updateStatusUI(currentLabor);

            // notify parent controller to refresh the list and stats
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        }
    }

    // update row status UI
    private void updateStatusUI(labors labor) {
        laborStatus.getStyleClass().removeAll("active", "inactive");

        if (labor.isActive()) {
            laborStatus.setText("Active");
            laborStatus.setTextFill(Color.GREEN);
            laborStatus.getStyleClass().add("active");

            actionResignBtn.setText("Resign");
            actionResignBtn.setDisable(false);
        } else {
            laborStatus.setText("Resigned");
            laborStatus.setTextFill(Color.GRAY);
            laborStatus.getStyleClass().add("inactive");

            actionResignBtn.setText("Resigned");
            actionResignBtn.setDisable(true);
        }
    }
}
