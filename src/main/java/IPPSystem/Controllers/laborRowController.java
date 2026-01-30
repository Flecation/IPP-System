package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.labors;
import com.fasterxml.jackson.annotation.JacksonInject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class laborRowController {

    @FXML
    private Button actionResignBtn;

    @FXML
    private Label assignedProject;

    @FXML
    private Label laborEndDate;

    @FXML
    private Label laborNRC;

    @FXML
    private Label laborName;

    @FXML
    private Label laborPhone;

    @FXML
    private Label laborStartDate;

    @FXML
    private Label laborStatus;

    private labors currentLabor;
    private Runnable refreshCallback;

    @FXML
    void actionResign(ActionEvent event) {

        if (currentLabor == null || !currentLabor.isActive()) return;

        boolean success = database.resignLabor(currentLabor.getLaborId());

        if (success) {
            currentLabor.setActive(false);
            currentLabor.setLaborEndDate(
                    Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
            );
            updateStatusUI(currentLabor);

            if (refreshCallback != null) {
                refreshCallback.run();
            }
        }
    }




    public void setData(labors labor, Runnable refreshCallback) {
        this.currentLabor = labor;
        this.refreshCallback = refreshCallback;

        laborName.setText(labor.getLaborName());
        laborNRC.setText(labor.getLaborNRC());
        laborPhone.setText(labor.getLaborPhone());
        laborStartDate.setText(labor.getLaborStartDate().toString());
        laborEndDate.setText(labor.getLaborEndDate() != null ? labor.getLaborEndDate().toString() : "-");

        updateStatusUI(labor);

        String projectName = database.getAssignedProjectName(labor.getLaborId());
        assignedProject.setText(projectName);
    }



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






