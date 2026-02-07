package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.DAO.laborDatabase;
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
    private void clickAddLabor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/createLaborModal.fxml"));
            Parent modal = loader.load();

            createLaborController controller = loader.getController();

            // Set callback: after adding a labor, refresh list + stats
            controller.setOnLaborAdded(() -> {
                applyFilters();   // rebuild labor rows with current filter
                updateLaborStats(); // refresh total/new/active/resigned numbers
            });

        session.getInstance()
                .getNavigationController()
                .showModal("createLaborModal.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void updateLaborStats() {
        // Ensure data is fresh from database
        totalLaborQty.setText(String.valueOf(database.getTotalLabors()));
        newHireQty.setText(String.valueOf(database.getNewHires()));
        activeLaborQty.setText(String.valueOf(database.getActiveLabors()));
        resignedQty.setText(String.valueOf(database.getResignedLaborsCount()));
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFilterOptions();
        resetFilters();     // load rows
        updateLaborStats(); // load numbers

        skillFilterCombo.setOnAction(e -> applyFilters());
        statusFilterCombo.setOnAction(e -> applyFilters());
    }

    private void loadFilterOptions() {

        statusFilterCombo.getItems().setAll("All", "Active", "Inactive");
        statusFilterCombo.setValue("All");

        List<String> skills = laborDatabase.getAllSkillNames();
        skillFilterCombo.getItems().setAll("All");
        skillFilterCombo.getItems().addAll(skills);
        skillFilterCombo.setValue("Skill");
    }


    @FXML
    private void applyFilters() {

        String selectedSkill = skillFilterCombo.getValue();
        String selectedStatus = statusFilterCombo.getValue();

        laborsPane.getChildren().clear();

        List<labors> laborList = database.getAllLaborsSortedByAssignment();

        for (labors labor : laborList) {

            boolean matchesSkill = selectedSkill.equals("All") ||
                    (labor.getSkillName() != null &&
                            labor.getSkillName().equalsIgnoreCase(selectedSkill));

            boolean matchesStatus =
                    selectedStatus.equals("All") ||
                            (selectedStatus.equals("Active") && labor.isActive()) ||
                            (selectedStatus.equals("Inactive") && !labor.isActive());

            if (matchesSkill && matchesStatus) {
                addLaborRow(labor);
            }
        }

        updateLaborStats();   // 🔥 stats update after filtering
    }



    private void addLaborRow(labors labor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/laborRow.fxml"));
            Parent row = loader.load();

            laborRowController controller = loader.getController();

            // Pass labor + callback to update parent UI after resign
            controller.setData(labor, this::refreshAfterChange);

            laborsPane.getChildren().add(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void refreshAfterChange() {
        // reload filtered list
        applyFilters();   // will rebuild the rows according to skill/status

        // refresh stats
        updateLaborStats();
    }


    @FXML
    private void resetFilters() {
        skillFilterCombo.setValue("All");
        statusFilterCombo.setValue("All");
        applyFilters();
    }



}
