//engineerViewController

package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.calculationDatabase;
import IPPSystem.DAO.database;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.users;
import IPPSystem.Utils.PaginationHelper;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class engineerViewController {
    @FXML
    private Label emptyLabel;

    @FXML
    private Button addNewEngineerBtn;

    @FXML
    private ComboBox<StatusFilter> managerSpStatusCombo;

    @FXML
    private VBox managerSupervisorListPane;

    @FXML
    private HBox paginationBox;

    @FXML
    private Button paginationNextBtn;

    @FXML
    private Button paginationPrevBtn;

    @FXML
    private Label newHireQty;

    @FXML
    private Label resignedQty;

    @FXML
    private Label totalSupervisorQty;
    @FXML
    private Label activeSupervisorQty;


    private StackPane loadPane;




    public void setLoadPane(StackPane pane){
        this.loadPane = pane;
    }

    public List<users> allEngineers = new ArrayList<>();

    private PaginationHelper<users> pagination;


    @FXML
    void addNewEngineer(ActionEvent event) {

        session.getInstance()
                .getNavigationController()
                .showModal("createSupervisorModal.fxml");

    }




    public enum StatusFilter {
        ALL("All"),
        ACTIVE("Active"),
        INACTIVE("Inactive");

        private final String label;

        StatusFilter(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label; // what shows in ComboBox
        }
    }




    @FXML
    public void initialize() {

        // Load all supervisors
        allEngineers = userDatabase.getUserByRole(role.SUPERVISOR.toString());

        // Setup pagination
        pagination = new PaginationHelper<>(5);
        pagination.setOnPageChanged(this::renderPage);
        pagination.setData(allEngineers);
        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);

        // Setup filter dropdown
        managerSpStatusCombo.getItems().addAll(StatusFilter.values());
        managerSpStatusCombo.setValue(StatusFilter.ALL); // default selection
        managerSpStatusCombo.setOnAction(e -> filterEngineers());

        loadEngineerStats();
    }

    @FXML
    private void filterEngineers() {

        StatusFilter selectedFilter = managerSpStatusCombo.getValue();

        List<users> filteredList = allEngineers.stream()
                .filter(u -> {

                    if (selectedFilter == null || selectedFilter == StatusFilter.ALL)
                        return true;

                    if (selectedFilter == StatusFilter.ACTIVE)
                        return u.isActive();

                    if (selectedFilter == StatusFilter.INACTIVE)
                        return !u.isActive();

                    return true;
                })
                .toList();

        // Refresh pagination with filtered data
        pagination.setData(filteredList);
        pagination.goToPage(1);
    }



    private void loadEngineerStats() {

        javafx.concurrent.Task<int[]> task = new javafx.concurrent.Task<>() {
            @Override
            protected int[] call() {
                int total = database.getTotalEngineersCount();
                int newHire = database.getNewEngineersThisMonth();
                int active = database.getActiveEngineersCount();
                int resigned = database.getResignedEngineersCount();
                return new int[]{total, newHire, active, resigned};
            }
        };

        task.setOnSucceeded(e -> {
            int[] v = task.getValue();
            totalSupervisorQty.setText(String.valueOf(v[0]));
            newHireQty.setText(String.valueOf(v[1]));
            activeSupervisorQty.setText(String.valueOf(v[2]));
            resignedQty.setText(String.valueOf(v[3]));
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task, "load-engineer-stats").start();
    }




    private void renderPage(List<users> pageData) {

        managerSupervisorListPane.getChildren().clear();

        for (users engineer : pageData) {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/View/engineerRow.fxml"));

                Parent row = loader.load();
                engineerRowController controller = loader.getController();

                controller.setData(engineer);
                controller.setOnDelete(this::refreshUI);

                controller.setOnView(this::openPersonalInfoPage);

                managerSupervisorListPane.getChildren().add(row);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pagination.buildButtons(paginationBox);
    }



//    private void refreshAfterDelete() {
//
//        // Reload data from DB
//        allEngineers = userDatabase.getUserByRole(role.SUPERVISOR.toString());
//
//        // Re-apply current filter
//        filterEngineers();
//
//        // 🔥 Update the statistics
//        loadEngineerStats();
//    }






    private void refreshUI() {

        allEngineers.sort(
                (a, b) -> Boolean.compare(b.isActive(), a.isActive())
        );

        pagination.setData(allEngineers);
        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);

        allEngineers = userDatabase.getUserByRole(role.SUPERVISOR.toString());

        // 2️⃣ Apply current filter again (keeps dropdown state)
        filterEngineers();

        // 3️⃣ Update dashboard statistics
        loadEngineerStats();
    }



    private void openPersonalInfoPage(users engineer) {
        // No need to keep a loadPane reference; utils resolves the correct per-tab loadPane
        utils.viewUserInfo(engineer, managerSupervisorListPane);
    }







}

