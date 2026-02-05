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
        javafx.concurrent.Task<int[]> task = new javafx.concurrent.Task<>() {
            @Override
            protected int[] call() {
                return new int[]{
                        database.getTotalLabors(),
                        database.getNewHires(),
                        database.getActiveLabors(),
                        database.getResignedLaborsCount()
                };
            }
        };

        task.setOnSucceeded(e -> {
            int[] v = task.getValue();
            totalLaborQty.setText(String.valueOf(v[0]));
            newHireQty.setText(String.valueOf(v[1]));
            activeLaborQty.setText(String.valueOf(v[2]));
            resignedQty.setText(String.valueOf(v[3]));
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task, "load-labor-stats").start();
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

        statusFilterCombo.getItems().setAll("All", "Active", "Resigned");
        statusFilterCombo.setValue("All");

        // Load skills async
        javafx.concurrent.Task<java.util.List<String>> skillTask = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<String> call() {
                return database.getAllSkills();
            }
        };

        skillTask.setOnSucceeded(e -> {
            java.util.List<String> skills = skillTask.getValue();
            skillFilterCombo.getItems().setAll("All");
            if (skills != null) skillFilterCombo.getItems().addAll(skills);
            skillFilterCombo.setValue("Skill");
        });

        skillTask.setOnFailed(e -> skillTask.getException().printStackTrace());

        new Thread(skillTask, "load-skill-filter").start();
    }


    @FXML
    private void applyFilters() {

        final String selectedSkill = skillFilterCombo.getValue() == null ? "All" : skillFilterCombo.getValue();
        final String selectedStatus = statusFilterCombo.getValue() == null ? "All" : statusFilterCombo.getValue();

        // Load labor list async (DB can be slow)
        javafx.concurrent.Task<java.util.List<IPPSystem.Models.labors>> task = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<IPPSystem.Models.labors> call() {
                return database.getAllLaborsSortedByAssignment();
            }
        };

        task.setOnSucceeded(e -> {
            java.util.List<IPPSystem.Models.labors> laborList = task.getValue();

            laborsPane.getChildren().clear();

            if (laborList != null) {
                for (IPPSystem.Models.labors labor : laborList) {

                    boolean matchesSkill = selectedSkill.equals("All") ||
                            (labor.getSkillName() != null &&
                                    labor.getSkillName().equalsIgnoreCase(selectedSkill));

                    boolean matchesStatus =
                            selectedStatus.equals("All") ||
                                    (selectedStatus.equals("Active") && labor.isActive()) ||
                                    (selectedStatus.equals("Resigned") && !labor.isActive());

                    if (matchesSkill && matchesStatus) {
                        addLaborRow(labor);
                    }
                }
            }

            updateLaborStats(); // async now
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task, "load-labors").start();
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
