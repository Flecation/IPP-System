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

import java.util.*;

public class mgSEListViewController {

    @FXML
    private Button managerSpCreateBtn;

    @FXML
    private ComboBox<String> managerSortingCombo;

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


        managerSortingCombo.getItems().addAll("All","A-Z","Z-A");
        managerSortingCombo.setValue("All");


        managerSpStatusCombo.getItems().addAll("Active" , "Unactive");
        managerSortingCombo.setOnAction(e -> applySortingAndPagination());

        managerSpStatusCombo.setOnAction(e -> filterEngineers());

    }


    private void applySortingAndPagination() {

        String sortType = managerSortingCombo.getValue();

        List<users> sortedList = new ArrayList<>(allEngineers);

        if ("A-Z".equals(sortType)) {
            sortedList.sort(
                    Comparator.comparing(
                            users::getUserName,
                            String.CASE_INSENSITIVE_ORDER
                    )
            );
        } else if ("Z-A".equals(sortType)) {
            sortedList.sort(
                    Comparator.comparing(
                            users::getUserName,
                            String.CASE_INSENSITIVE_ORDER
                    ).reversed()
            );
        }

        pagination.setData(sortedList);
        pagination.goToPage(1); // reset to first page
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

                // 🔥 VIEW BUTTON HANDLER
                controller.setOnView(this::openPersonalInfoPage);

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

        String selectedStatus = managerSpStatusCombo.getValue();

        Boolean isActive;


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


                    // Filter by active status
                    if (isActive != null) {
                        matchesStatus = u.isActive() == isActive; // or u.getUserStatus() == 1
                    }

//                    return matchesType && matchesStatus;
                    return  matchesStatus;
                })
                .toList();

        // Update pagination
        pagination.setData(filteredList);
        pagination.goToPage(1);
    }




    private void openPersonalInfoPage(users engineer) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/View/mgSEListInfo.fxml"));

            Parent page = loader.load();

            mgSEPersonalInfoController controller =
                    loader.getController();

            controller.setEngineer(engineer); // 🔥 pass selected engineer

            managerSupervisorListPane.getChildren().setAll(page);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

