package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.PaginationHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mgSEListViewController {

    @FXML
    private Button managerSpCreateBtn;

    @FXML
    private ComboBox<String> managerSpProjectTypeCombo;

    @FXML
    private ComboBox<String> managerSpStatusCombo;

    @FXML
    private VBox managerSupervisorListPane;

    @FXML
    private HBox paginationBox;

    @FXML
    private Button paginationNextBtn;

    @FXML
    private Button paginationPrevBtn;

    public List<users> allEngineers = new ArrayList<>();

    private PaginationHelper<users> pagination;

    @FXML
    public void initialize() {

        allEngineers =
                userDatabase.getUserByRole(role.SUPERVISOR.toString());

        pagination = new PaginationHelper<>(5);

        pagination.setOnPageChanged(this::renderPage);

        pagination.setData(allEngineers);

        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);


        // Fetch project types
        HashMap<Integer, String> projectTypes = database.getAllProjectTypes();
        managerSpProjectTypeCombo.getItems().addAll(projectTypes.values());

        // Fetch project statuses
        managerSpStatusCombo.getItems().addAll("Active" , "Unactive");
        managerSpProjectTypeCombo.setOnAction(e -> filterEngineers());
        managerSpStatusCombo.setOnAction(e -> filterEngineers());

    }


    private void renderPage(List<users> pageData) {

        managerSupervisorListPane.getChildren().clear();

        for (users engineer : pageData) {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/View/mgSERow.fxml"));

                Parent row = loader.load();
                mgSERowController controller = loader.getController();

                controller.setData(engineer);
                controller.setOnDelete(this::refreshUI);

                managerSupervisorListPane.getChildren().add(row);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pagination.buildButtons(paginationBox);
    }


    private void refreshUI() {

        allEngineers.sort(
                (a, b) -> Boolean.compare(b.isActive(), a.isActive())
        );

        pagination.setData(allEngineers);
        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);
    }


    @FXML
    private void filterEngineers() {

        String selectedTypeName = managerSpProjectTypeCombo.getValue();
        String selectedStatus = managerSpStatusCombo.getValue();

        Integer typeId;
        Boolean isActive;

        // Convert type name to ID using your projectTypes HashMap
        HashMap<Integer, String> projectTypes = database.getAllProjectTypes();
        typeId = projectTypes.entrySet().stream().filter(entry -> entry.getValue().equals(selectedTypeName)).findFirst().map(Map.Entry::getKey).orElse(null);

        // Convert status string to boolean
        if (selectedStatus != null) {
            isActive = selectedStatus.equalsIgnoreCase("Active");
        } else {
            isActive = null;
        }

        // Filter the list
        List<users> filteredList = allEngineers.stream()
                .filter(u -> {
                    boolean matchesType = true;
                    boolean matchesStatus = true;

                    // Filter by project type
                    if (typeId != null) {
                        matchesType = u.getProjectTypeId() == typeId; // you need a getter in users model
                    }

                    // Filter by active status
                    if (isActive != null) {
                        matchesStatus = u.isActive() == isActive; // or u.getUserStatus() == 1
                    }

                    return matchesType && matchesStatus;
                })
                .toList();

        // Update pagination
        pagination.setData(filteredList);
        pagination.goToPage(1);
    }



}

