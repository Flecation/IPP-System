package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.labors;
import IPPSystem.Utils.session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import java.sql.Date;
import java.time.LocalDate;

public class laborViewController implements Initializable {

    @FXML private Label activeLaborQty;
    @FXML private VBox laborsPane;
    @FXML private Label newHireQty;
    @FXML private Label totalLaborQty;
    @FXML private ComboBox<String> skillFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Label resignedQty;
    @FXML
    private Button addLaborBtn;


    @FXML
    void clickAddLabor(ActionEvent event) {
        session.getInstance()
                .getNavigationController()
                .showModal("createLaborModal.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateLaborStats();
        loadFilterOptions();
        loadLaborRows();

        skillFilterCombo.setOnAction(e -> applyFilters());
        statusFilterCombo.setOnAction(e -> applyFilters());

    }

    public void updateLaborStats() {
        totalLaborQty.setText(String.valueOf(database.getTotalLabors()));
        newHireQty.setText(String.valueOf(database.getNewHires()));
        activeLaborQty.setText(String.valueOf(database.getActiveLabors()));
        resignedQty.setText(String.valueOf(database.getResignedLaborsCount()));
    }

    private void loadFilterOptions() {
        // Status options
        statusFilterCombo.getItems().setAll("All", "Active", "Resigned");
        statusFilterCombo.setValue("Status");

        // Skill options from DB
        List<String> skills = database.getAllSkills();
        skillFilterCombo.getItems().clear();
        skillFilterCombo.getItems().add("All");
        skillFilterCombo.getItems().addAll(skills);
        skillFilterCombo.setValue("Skill");
    }

    @FXML
    public void loadLaborRows() {
        resetFilters();
    }

    @FXML
    private void applyFilters() {
        String selectedSkill = skillFilterCombo.getValue();
        String selectedStatus = statusFilterCombo.getValue();

        laborsPane.getChildren().clear();

        // Data comes pre-sorted from the Database:
        // [Assigned] -> [Active but Unassigned] -> [Resigned]
        List<labors> laborList = database.getAllLaborsSortedByAssignment();

        for (labors labor : laborList) {
            boolean matchesSkill = true;
            boolean matchesStatus = true;

            // 1. Skill Filter Logic
            if (selectedSkill != null && !selectedSkill.equals("All")) {
                matchesSkill = labor.getSkillName() != null &&
                        labor.getSkillName().equalsIgnoreCase(selectedSkill);
            }

            // 2. Status Filter Logic
            if (selectedStatus != null && !selectedStatus.equals("All")) {
                if (selectedStatus.equals("Active")) {
                    matchesStatus = labor.isActive();
                } else if (selectedStatus.equals("Resigned")) {
                    matchesStatus = !labor.isActive();
                }
            }


            if (matchesSkill && matchesStatus) {
                addLaborRow(labor);
            }
        }
    }




    private void addLaborRow(labors labor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/laborRow.fxml"));
            Parent row = loader.load();
            laborRowController controller = loader.getController();

            controller.setData(labor, this::applyFilters);

            laborsPane.getChildren().add(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetFilters() {
        skillFilterCombo.setValue("Skill");
        statusFilterCombo.setValue("Status");



        // Clear and reload everything
        laborsPane.getChildren().clear();
        List<labors> laborList = database.getAllLaborsSortedByAssignment();
        for(labors l : laborList) {
            addLaborRow(l);
        }
    }
}