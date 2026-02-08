package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Interfaces.*;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.PaginationHelper;
import IPPSystem.Utils.messageBoxService;
import IPPSystem.Utils.utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class engineerViewController implements loadPaneAware,
        SearchablePage, SuggestablePage, ReloadablePage, TabStateful {

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

    private StackPane loadPane;

    // ===== Data =====
    private List<users> allSupervisors = new ArrayList<>();
    private PaginationHelper<users> pagination;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    // Search text from sidebar
    private String searchQuery = "";

    // Cache current project name per supervisorId (avoid repeated DB calls)
    private final Map<Integer, String> currentProjectCache = new ConcurrentHashMap<>();

    public enum StatusFilter {
        ALL("All"),
        ACTIVE("Active"),
        INACTIVE("Inactive");

        private final String label;
        StatusFilter(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    public void initialize() {

        setupTable();

        pagination = new PaginationHelper<>(8);
        pagination.setOnPageChanged(this::renderTablePage);

        managerSpStatusCombo.getItems().setAll(StatusFilter.values());
        managerSpStatusCombo.setValue(StatusFilter.ALL);
        managerSpStatusCombo.setOnAction(e -> applyFiltersAndRefresh());

        refreshAll();
    }

    // ===== Add supervisor (use overlay like project add phase) =====
    @FXML
    void addNewEngineer(ActionEvent event) {
        sideBarPaneController sb = getSideBar();
        if (sb != null) sb.openAddOverlay("createSupervisorModal.fxml");
    }

    // ===== sidebar reload =====
    @Override
    public void onReload() {
        refreshAll();
    }

    private void refreshAll() {
        reloadSupervisorsFromDB();
        applyFiltersAndRefresh();
        loadSupervisorStats();
    }

    // ===== sidebar search =====
    @Override
    public void onSearch(String query) {
        this.searchQuery = (query == null) ? "" : query.trim().toLowerCase();
        applyFiltersAndRefresh();
    }

    // ===== sidebar suggestions =====
    @Override
    public List<String> getSuggestions(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();

        LinkedHashSet<String> out = new LinkedHashSet<>();

        for (users u : allSupervisors) {
            if (u == null) continue;

            addIfMatch(out, u.getUserName(), q);
            addIfMatch(out, u.getUserEmail(), q);
            addIfMatch(out, u.getUserPhone(), q);

            String p = getCurrentProjectNameCached(u.getUserId());
            addIfMatch(out, p, q);

            addIfMatch(out, u.isActive() ? "Active" : "Inactive", q);

            if (out.size() >= 8) break;
        }

        return new ArrayList<>(out);
    }

    private void addIfMatch(Set<String> out, String v, String q) {
        if (v == null) return;
        String s = v.trim();
        if (s.isEmpty()) return;
        if (s.toLowerCase().contains(q)) out.add(s);
    }

    // ===== New Tab state =====
    @Override
    public Map<String, Object> exportState() {
        Map<String, Object> s = new HashMap<>();
        s.put("status", managerSpStatusCombo.getValue() == null ? StatusFilter.ALL.name() : managerSpStatusCombo.getValue().name());
        s.put("search", searchQuery);
        s.put("page", pagination == null ? 1 : pagination.getCurrentPage());
        return s;
    }

    @Override
    public void importState(Map<String, Object> state) {
        if (state == null) return;

        Object status = state.get("status");
        Object search = state.get("search");
        Object page = state.get("page");

        if (status instanceof String st) {
            try {
                managerSpStatusCombo.setValue(StatusFilter.valueOf(st));
            } catch (Exception ignored) {
                managerSpStatusCombo.setValue(StatusFilter.ALL);
            }
        }
        if (search instanceof String s) {
            searchQuery = s;
        }

        refreshAll();

        int p = 1;
        if (page instanceof Number n) p = n.intValue();
        final int finalPage = p;

        javafx.application.Platform.runLater(() -> {
            if (pagination != null) pagination.goToPage(finalPage);
            if (pagination != null) pagination.buildButtons(paginationBox);
        });
    }

    // ===== Table setup =====
    private void setupTable() {

        nameCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        safe(cell.getValue() == null ? null : cell.getValue().getUserName(), "-")
                )
        );

        cProjectCol.setCellValueFactory(cell -> {
            users u = cell.getValue();
            String projectName = (u == null) ? "-" : getCurrentProjectNameCached(u.getUserId());
            return new javafx.beans.property.SimpleStringProperty(projectName == null ? "-" : projectName);
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

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewBtn = new Button("View");
            private final Button statusBtn = new Button();
            private final HBox box = new HBox(8, viewBtn, statusBtn);

            {
                viewBtn.getStyleClass().add("action-btn");
                statusBtn.getStyleClass().add("danger-btn");

                viewBtn.setOnAction(e -> {
                    users u = getRowUser();
                    if (u == null) return;
                    sideBarPaneController sb = getSideBar();
                    if (sb == null) return;

                    sb.openInnerView("mgSEPersonalDetail.fxml", ctrl -> {
                        if (ctrl instanceof mgSEPersonalDetailController detail) {
                            detail.setEngineer(u);
                        }
                    });

                });

                statusBtn.setOnAction(e -> {
                    users u = getRowUser();
                    if (u == null) return;
                    if (u.isActive()) confirmResign(u);
                    else confirmUnResign(u);
                });
            }

            private users getRowUser() {
                if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) return null;
                return getTableView().getItems().get(getIndex());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);

                if (empty) { setGraphic(null); return; }

                users u = getRowUser();
                if (u == null) { setGraphic(null); return; }

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
        messageBoxService.confirm(
                "Confirm Resign",
                "Set this user as inactive?\n\nName: " + safe(u.getUserName(), "-"),
                notificationType.WARNING,
                () -> {
                    try {
                        userDatabase.delete(u.getUserId()); // isActive=false, endDate=today
                        messageBoxService.toast("Success", "User marked as inactive.", notificationType.SUCCESS);
                        refreshAll();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        messageBoxService.toast("Error", "Failed to resign user.", notificationType.ERROR);
                    }
                },
                null
        );
    }

    private void confirmUnResign(users u) {
        messageBoxService.confirm(
                "Confirm UnResign",
                "Set this user as active again?\n\nName: " + safe(u.getUserName(), "-"),
                notificationType.INFO,
                () -> {
                    try {
                        userDatabase.reactivate(u.getUserId()); // isActive=true, endDate=NULL
                        messageBoxService.toast("Success", "User marked as active.", notificationType.SUCCESS);
                        refreshAll();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        messageBoxService.toast("Error", "Failed to activate user.", notificationType.ERROR);
                    }
                },
                null
        );
    }

    private void reloadSupervisorsFromDB() {
        allSupervisors = userDatabase.getUserByRole(role.SUPERVISOR.toString());
        if (allSupervisors == null) allSupervisors = new ArrayList<>();
        currentProjectCache.clear();
    }

    private void applyFiltersAndRefresh() {
        StatusFilter selected = managerSpStatusCombo.getValue();
        if (selected == null) selected = StatusFilter.ALL;

        final StatusFilter st = selected;
        final String q = (searchQuery == null) ? "" : searchQuery;

        List<users> filtered = allSupervisors.stream()
                .filter(u -> {
                    if (u == null) return false;

                    // status filter
                    if (st == StatusFilter.ACTIVE && !u.isActive()) return false;
                    if (st == StatusFilter.INACTIVE && u.isActive()) return false;

                    // search filter
                    if (!q.isBlank()) {
                        String name = safe(u.getUserName(), "").toLowerCase();
                        String email = safe(u.getUserEmail(), "").toLowerCase();
                        String phone = safe(u.getUserPhone(), "").toLowerCase();
                        String proj = safe(getCurrentProjectNameCached(u.getUserId()), "").toLowerCase();
                        String statusWord = u.isActive() ? "active" : "inactive";

                        if (!(name.contains(q) || email.contains(q) || phone.contains(q) || proj.contains(q) || statusWord.contains(q))) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();

        updateTablePlaceholder(st);

        supervisorTable.getItems().clear();
        pagination.setData(filtered);
        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);
    }

    private void renderTablePage(List<users> pageData) {
        supervisorTable.setItems(FXCollections.observableArrayList(pageData));
        pagination.buildButtons(paginationBox);
    }

    private void updateTablePlaceholder(StatusFilter filter) {
        String msg;
        if (filter == StatusFilter.INACTIVE) msg = "No InActive Supervisor";
        else if (filter == StatusFilter.ACTIVE) msg = "No Active Supervisor";
        else msg = "No Supervisor Found";

        Label label = new Label(msg);
        label.getStyleClass().add("text-color");
        supervisorTable.setPlaceholder(label);
    }

    private String getCurrentProjectNameCached(int supervisorId) {
        return currentProjectCache.computeIfAbsent(supervisorId, this::getCurrentProjectNameSafe);
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

    private void loadSupervisorStats() {
        javafx.concurrent.Task<int[]> task = new javafx.concurrent.Task<>() {
            @Override protected int[] call() {
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

        Thread t = new Thread(task, "load-supervisor-stats");
        t.setDaemon(true);
        t.start();
    }

    private sideBarPaneController getSideBar() {
        StackPane lp = (loadPane != null) ? loadPane : utils.findTabLoadPane(addNewEngineerBtn);
        if (lp == null) return null;
        Object p = lp.getProperties().get("SIDEBAR_CONTROLLER");
        return (p instanceof sideBarPaneController sb) ? sb : null;
    }

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        return t.isEmpty() ? fallback : t;
    }
}
