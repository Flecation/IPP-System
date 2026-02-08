package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.PaginationHelper;
import IPPSystem.Utils.messageBoxService;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * engineerViewController
 *
 * - TableView + paginationBox
 * - Status filter: All / Active / Inactive
 * - Action column:
 *    - View (opens mgSEPersonalDetail.fxml)
 *    - Resign (when active) -> isActive=false
 *    - UnResign (when inactive) -> isActive=true
 *
 * Uses messageBoxService.confirm() (your custom confirm box).
 */
public class engineerViewController {

    // ===== Top actions =====
    @FXML private Button addNewEngineerBtn;

    // ===== Filter =====
    @FXML private ComboBox<StatusFilter> managerSpStatusCombo;

    // ===== Table =====
    @FXML private TableView<users> supervisorTable;
    @FXML private TableColumn<users, String> nameCol;
    @FXML private TableColumn<users, String> cProjectCol;
    @FXML private TableColumn<users, String> activeCol;
    @FXML private TableColumn<users, String> sDateCol;
    @FXML private TableColumn<users, String> eDateCol;
    @FXML private TableColumn<users, Void> actionCol;

    // ===== Pagination UI =====
    @FXML private HBox paginationBox;

    // ===== Stats labels =====
    @FXML private Label newHireQty;
    @FXML private Label resignedQty;
    @FXML private Label totalSupervisorQty;
    @FXML private Label activeSupervisorQty;

    // ===== Data =====
    private List<users> allEngineers = new ArrayList<>();
    private PaginationHelper<users> pagination;

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public enum StatusFilter {
        ALL("All"),
        ACTIVE("Active"),
        INACTIVE("Inactive");

        private final String label;
        StatusFilter(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    @FXML
    void addNewEngineer(ActionEvent event) {
        // ✅ open your create-project-like modal
        session.getInstance().getNavigationController().showModal("createSupervisorModel.fxml");
    }

    // ✅ If you connect ComboBox in FXML: onAction="#onStatusFilterChanged"
    @FXML
    private void onStatusFilterChanged(ActionEvent event) {
        applyFilterAndRefresh();
    }

    @FXML
    public void initialize() {

        setupTable();

        reloadEngineersFromDB();

        pagination = new PaginationHelper<>(8);
        pagination.setOnPageChanged(this::renderTablePage);

        managerSpStatusCombo.getItems().setAll(StatusFilter.values());
        managerSpStatusCombo.setValue(StatusFilter.ALL);

        // works even if you don't set onAction in FXML
        managerSpStatusCombo.setOnAction(e -> applyFilterAndRefresh());

        applyFilterAndRefresh();

        loadEngineerStats();
    }

    private void setupTable() {

        nameCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        safe(cell.getValue() == null ? null : cell.getValue().getUserName(), "-")
                )
        );

        cProjectCol.setCellValueFactory(cell -> {
            users u = cell.getValue();
            String projectName = (u == null) ? "-" : getCurrentProjectNameSafe(u.getUserId());
            return new javafx.beans.property.SimpleStringProperty(projectName);
        });

        activeCol.setCellValueFactory(cell -> {
            users u = cell.getValue();
            boolean active = u != null && u.isActive();
            return new javafx.beans.property.SimpleStringProperty(active ? "Active" : "Inactive");
        });

        sDateCol.setCellValueFactory(cell -> {
            users u = cell.getValue();
            var d = (u == null) ? null : u.getUserStartDate();
            return new javafx.beans.property.SimpleStringProperty(d == null ? "-" : df.format(d));
        });

        eDateCol.setCellValueFactory(cell -> {
            users u = cell.getValue();
            var d = (u == null) ? null : u.getUserEndDate();
            return new javafx.beans.property.SimpleStringProperty(d == null ? "-" : df.format(d));
        });

        // ✅ IMPORTANT: updateItem must rebuild button state because JavaFX reuses TableCell objects.
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewBtn = new Button("View");
            private final Button statusBtn = new Button(); // Resign / UnResign (dynamic)
            private final HBox box = new HBox(8, viewBtn, statusBtn);

            {
                viewBtn.getStyleClass().add("action-btn");
                statusBtn.getStyleClass().add("danger-btn"); // will be swapped in updateItem

                viewBtn.setOnAction(e -> {
                    users u = getCurrentRowUser();
                    if (u == null) return;

                    // ✅ This loads /View/mgSEPersonalDetail.fxml and calls setEngineer(user)
                    utils.viewUserInfo(u, supervisorTable);
                });

                statusBtn.setOnAction(e -> {
                    users u = getCurrentRowUser();
                    if (u == null) return;

                    if (u.isActive()) {
                        confirmResign(u);
                    } else {
                        confirmUnResign(u);
                    }
                });
            }

            private users getCurrentRowUser() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) return null;
                return getTableView().getItems().get(getIndex());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setText(null);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                users u = getCurrentRowUser();
                if (u == null) {
                    setGraphic(null);
                    return;
                }

                if (u.isActive()) {
                    statusBtn.setText("Resign");
                    statusBtn.getStyleClass().removeAll("success-btn");
                    if (!statusBtn.getStyleClass().contains("danger-btn")) statusBtn.getStyleClass().add("danger-btn");
                } else {
                    statusBtn.setText("UnResign");
                    statusBtn.getStyleClass().removeAll("danger-btn");
                    if (!statusBtn.getStyleClass().contains("success-btn")) statusBtn.getStyleClass().add("success-btn");
                }

                setGraphic(box);
            }
        });
    }

    private void confirmResign(users u) {
        String title = "Confirm Resign";
        String msg = "Set this user as inactive?\n\nName: " + safe(u.getUserName(), "-") +
                "\n\nIf you click Confirm, this user will be marked as inactive.";

        messageBoxService.confirm(
                title,
                msg,
                notificationType.WARNING,
                () -> {
                    try {
                        userDatabase.delete(u.getUserId()); // isActive=false, endDate=today
                        refreshUI();
                        messageBoxService.toast("Success", "User marked as inactive.", notificationType.SUCCESS);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        messageBoxService.toast("Error", "Failed to resign user.", notificationType.ERROR);
                    }
                },
                null
        );
    }

    private void confirmUnResign(users u) {
        String title = "Confirm UnResign";
        String msg = "Set this user as active again?\n\nName: " + safe(u.getUserName(), "-") +
                "\n\nIf you click Confirm, this user will be marked as active.";

        messageBoxService.confirm(
                title,
                msg,
                notificationType.INFO,
                () -> {
                    try {
                        // requires userDatabase.reactivate(id)
                        userDatabase.reactivate(u.getUserId()); // isActive=true, endDate=NULL
                        refreshUI();
                        messageBoxService.toast("Success", "User marked as active.", notificationType.SUCCESS);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        messageBoxService.toast("Error", "Failed to activate user.", notificationType.ERROR);
                    }
                },
                null
        );
    }

    private void reloadEngineersFromDB() {
        allEngineers = userDatabase.getUserByRole(role.SUPERVISOR.toString());
        if (allEngineers == null) allEngineers = new ArrayList<>();
    }

    private void updateTablePlaceholder(StatusFilter filter) {
        String msg;
        if (filter == StatusFilter.INACTIVE) {
            msg = "No InActive Supervisor";
        } else if (filter == StatusFilter.ACTIVE) {
            msg = "No Active Supervisor";
        } else {
            msg = "No Supervisor Found";
        }
        Label label = new Label(msg);
        label.getStyleClass().add("text-color");
        supervisorTable.setPlaceholder(label);
    }

    private void applyFilterAndRefresh() {
        StatusFilter selected = managerSpStatusCombo.getValue();

        List<users> filtered = allEngineers.stream()
                .filter(u -> {
                    if (selected == null || selected == StatusFilter.ALL) return true;
                    if (selected == StatusFilter.ACTIVE) return u.isActive();
                    if (selected == StatusFilter.INACTIVE) return !u.isActive();
                    return true;
                })
                .toList();

        updateTablePlaceholder(selected);

        // ✅ clear old rows when switching filters
        supervisorTable.getItems().clear();

        pagination.setData(filtered);
        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);
    }

    private void renderTablePage(List<users> pageData) {
        supervisorTable.setItems(FXCollections.observableArrayList(pageData));
        pagination.buildButtons(paginationBox);
    }

    private void refreshUI() {
        reloadEngineersFromDB();
        applyFilterAndRefresh();
        loadEngineerStats();
    }

    private String getCurrentProjectNameSafe(int engineerId) {
        try {
            List<projects> list = database.getProjectsByEngineer(engineerId);
            if (list == null || list.isEmpty()) return "-";
            projects p = list.get(0);
            String name = (p == null) ? null : p.getProjectInstanceName();
            return safe(name, "-");
        } catch (Exception e) {
            return "-";
        }
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

        Thread t = new Thread(task, "load-engineer-stats");
        t.setDaemon(true);
        t.start();
    }

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        return t.isEmpty() ? fallback : t;
    }
}
