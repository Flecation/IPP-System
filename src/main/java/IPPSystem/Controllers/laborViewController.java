package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.DAO.database;
import IPPSystem.Interfaces.*;
import IPPSystem.Models.labors;
import IPPSystem.Models.skills;
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

public class laborViewController implements loadPaneAware,
        SearchablePage, SuggestablePage, ReloadablePage, TabStateful {

    // ===== Top stats =====
    @FXML private Label totalLaborQty;
    @FXML private Label newHireQty;
    @FXML private Label activeLaborQty;
    @FXML private Label resignedQty;

    // ===== Actions =====
    @FXML private Button addLaborBtn;

    // ===== Filters =====
    @FXML private ComboBox<String> skillFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;

    // ===== Table =====
    @FXML private TableView<labors> laborTable;
    @FXML private TableColumn<labors, String> nameCol;
    @FXML private TableColumn<labors, String> aProjectCol;
    @FXML private TableColumn<labors, String> phoneCol;
    @FXML private TableColumn<labors, String> nrcCol;
    @FXML private TableColumn<labors, String> activeCol;
    @FXML private TableColumn<labors, String> sDateCol;
    @FXML private TableColumn<labors, String> eDateCol;
    @FXML private TableColumn<labors, Void> actionCol;

    // ===== Pagination UI =====
    @FXML private HBox paginationBox;

    private StackPane loadPane;

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private List<labors> allLabors = new ArrayList<>();
    private PaginationHelper<labors> pagination;

    // ===== Sidebar search query =====
    private String searchQuery = "";

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    public void initialize() {
        setupTable();

        pagination = new PaginationHelper<>(8); // your page size
        pagination.setOnPageChanged(this::renderTablePage);

        // Status filter
        statusFilterCombo.getItems().setAll("All", "Active", "Resigned");
        statusFilterCombo.setValue("All");
        statusFilterCombo.setOnAction(e -> applyFiltersAndRefresh());

        // Skill filter (load from skill table, NOT from labors join)
        skillFilterCombo.getItems().setAll("All");
        skillFilterCombo.setValue("All");
        skillFilterCombo.setOnAction(e -> applyFiltersAndRefresh());
        loadSkillFilterFromSkillTable();

        refreshAll();
    }

    // ====== Add overlay (same style as project add) ======
    @FXML
    private void clickAddLabor(ActionEvent event) {
        StackPane lp = (loadPane != null) ? loadPane : utils.findTabLoadPane(addLaborBtn);
        if (lp == null) return;

        sideBarPaneController parent = (sideBarPaneController) lp.getProperties().get("SIDEBAR_CONTROLLER");
        if (parent != null) parent.openAddOverlay("createLaborModal.fxml"); // your add form fxml
    }

    // ====== Reload action (sidebar reload button calls this) ======
    @Override
    public void onReload() {
        refreshAll();
    }

    private void refreshAll() {
        updateLaborStats();
        reloadLaborsFromDB();
    }

    // ====== Search action (sidebar search bar calls this) ======
    @Override
    public void onSearch(String query) {
        this.searchQuery = (query == null) ? "" : query.trim().toLowerCase();
        applyFiltersAndRefresh();
    }

    // ====== Suggestions (sidebar suggestion popup calls this) ======
    @Override
    public List<String> getSuggestions(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();

        Set<String> out = new LinkedHashSet<>();

        for (labors l : allLabors) {
            if (l == null) continue;

            addIfMatch(out, l.getLaborName(), q);
            addIfMatch(out, l.getLaborPhone(), q);
            addIfMatch(out, l.getLaborNRC(), q);
            addIfMatch(out, l.getSkillName(), q);

            // assigned project name (your DB helper)
            String ap = database.getAssignedProjectName(l.getLaborId());
            addIfMatch(out, ap, q);

            // status word
            addIfMatch(out, l.isActive() ? "Active" : "Resigned", q);

            if (out.size() >= 8) break;
        }

        return new ArrayList<>(out);
    }

    private void addIfMatch(Set<String> out, String value, String q) {
        if (value == null) return;
        String v = value.trim();
        if (v.isEmpty()) return;
        if (v.toLowerCase().contains(q)) out.add(v);
    }

    // ====== New Tab support ======
    @Override
    public Map<String, Object> exportState() {
        Map<String, Object> s = new HashMap<>();
        s.put("skill", skillFilterCombo.getValue());
        s.put("status", statusFilterCombo.getValue());
        s.put("search", searchQuery);
        s.put("page", pagination == null ? 1 : pagination.getCurrentPage());
        return s;
    }

    @Override
    public void importState(Map<String, Object> state) {
        if (state == null) return;

        Object skill = state.get("skill");
        Object status = state.get("status");
        Object search = state.get("search");
        Object page = state.get("page");

        if (skill instanceof String v && skillFilterCombo.getItems().contains(v)) {
            skillFilterCombo.setValue(v);
        }
        if (status instanceof String v && statusFilterCombo.getItems().contains(v)) {
            statusFilterCombo.setValue(v);
        }
        if (search instanceof String v) {
            searchQuery = v;
        }

        // reload data then go to page
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

        nameCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue() == null ? "-" : safe(c.getValue().getLaborName(), "-"))
        );

        aProjectCol.setCellValueFactory(c -> {
            if (c.getValue() == null) return new javafx.beans.property.SimpleStringProperty("-");
            String p = database.getAssignedProjectName(c.getValue().getLaborId());
            return new javafx.beans.property.SimpleStringProperty(p == null ? "Not Assigned" : p);
        });

        phoneCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue() == null ? "-" : safe(c.getValue().getLaborPhone(), "-"))
        );

        nrcCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue() == null ? "-" : safe(c.getValue().getLaborNRC(), "-"))
        );

        activeCol.setCellValueFactory(c -> {
            boolean active = c.getValue() != null && c.getValue().isActive();
            return new javafx.beans.property.SimpleStringProperty(active ? "Active" : "Resigned");
        });

        sDateCol.setCellValueFactory(c -> {
            var d = c.getValue() == null ? null : c.getValue().getLaborStartDate();
            return new javafx.beans.property.SimpleStringProperty(d == null ? "-" : df.format(d));
        });

        eDateCol.setCellValueFactory(c -> {
            var d = c.getValue() == null ? null : c.getValue().getLaborEndDate();
            return new javafx.beans.property.SimpleStringProperty(d == null ? "-" : df.format(d));
        });

        // ✅ Action column buttons (Resign / UnResign)
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button actionBtn = new Button();
            private final HBox box = new HBox(actionBtn);

            {
                box.setSpacing(6);

                actionBtn.setOnAction(e -> {
                    labors l = getTableView().getItems().get(getIndex());
                    if (l == null) return;

                    if (l.isActive()) confirmResign(l);
                    else confirmUnResign(l);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                labors l = getTableView().getItems().get(getIndex());
                if (l == null) {
                    setGraphic(null);
                    return;
                }

                if (l.isActive()) {
                    actionBtn.setText("Resign");
                    actionBtn.getStyleClass().removeAll("success-btn");
                    if (!actionBtn.getStyleClass().contains("danger-btn")) actionBtn.getStyleClass().add("danger-btn");
                } else {
                    actionBtn.setText("UnResign");
                    actionBtn.getStyleClass().removeAll("danger-btn");
                    if (!actionBtn.getStyleClass().contains("success-btn")) actionBtn.getStyleClass().add("success-btn");
                }

                setGraphic(box);
            }
        });
    }

    // ===== Data load =====
    private void reloadLaborsFromDB() {
        javafx.concurrent.Task<List<labors>> task = new javafx.concurrent.Task<>() {
            @Override protected List<labors> call() {
                return database.getAllLaborsSortedByAssignment();
            }
        };

        task.setOnSucceeded(e -> {
            allLabors = task.getValue();
            if (allLabors == null) allLabors = new ArrayList<>();
            applyFiltersAndRefresh();
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread t = new Thread(task, "load-labors");
        t.setDaemon(true);
        t.start();
    }

    private void applyFiltersAndRefresh() {

        String skill = skillFilterCombo.getValue() == null ? "All" : skillFilterCombo.getValue();
        String status = statusFilterCombo.getValue() == null ? "All" : statusFilterCombo.getValue();
        String q = (searchQuery == null) ? "" : searchQuery;

        List<labors> filtered = allLabors.stream().filter(l -> {
            if (l == null) return false;

            // skill filter
            if (!"All".equalsIgnoreCase(skill)) {
                if (l.getSkillName() == null || !l.getSkillName().equalsIgnoreCase(skill)) return false;
            }

            // status filter
            if (!"All".equalsIgnoreCase(status)) {
                if ("Active".equalsIgnoreCase(status) && !l.isActive()) return false;
                if ("Resigned".equalsIgnoreCase(status) && l.isActive()) return false;
            }

            // search filter (name/phone/nrc/skill/project/status)
            if (!q.isBlank()) {
                String name = safe(l.getLaborName(), "").toLowerCase();
                String phone = safe(l.getLaborPhone(), "").toLowerCase();
                String nrc = safe(l.getLaborNRC(), "").toLowerCase();
                String skillName = safe(l.getSkillName(), "").toLowerCase();
                String ap = safe(database.getAssignedProjectName(l.getLaborId()), "").toLowerCase();
                String st = (l.isActive() ? "active" : "resigned");

                if (!(name.contains(q) || phone.contains(q) || nrc.contains(q) || skillName.contains(q) || ap.contains(q) || st.contains(q))) {
                    return false;
                }
            }

            return true;
        }).toList();

        updateTablePlaceholder(status);

        pagination.setData(filtered);
        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);
    }

    private void renderTablePage(List<labors> pageData) {
        laborTable.setItems(FXCollections.observableArrayList(pageData));
        pagination.buildButtons(paginationBox);
    }

    private void updateTablePlaceholder(String status) {
        String msg;
        if ("Active".equalsIgnoreCase(status)) msg = "No Active Labor";
        else if ("Resigned".equalsIgnoreCase(status)) msg = "No Resigned Labor";
        else msg = "No Labor Found";

        Label label = new Label(msg);
        label.getStyleClass().add("text-color");
        laborTable.setPlaceholder(label);
    }

    private void loadSkillFilterFromSkillTable() {
        try {
            List<String> names = new ArrayList<>();
            for (skills s : database.getAllSkill()) {
                if (s != null && s.getSkillName() != null && !s.getSkillName().isBlank()) {
                    names.add(s.getSkillName());
                }
            }
            skillFilterCombo.getItems().setAll("All");
            skillFilterCombo.getItems().addAll(names);
            if (skillFilterCombo.getValue() == null) skillFilterCombo.setValue("All");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLaborStats() {
        javafx.concurrent.Task<int[]> task = new javafx.concurrent.Task<>() {
            @Override protected int[] call() {
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

        Thread t = new Thread(task, "load-labor-stats");
        t.setDaemon(true);
        t.start();
    }

    private void confirmResign(labors l) {
        messageBoxService.confirm(
                "Confirm Resign",
                "Set this labor as resigned?\n\nName: " + safe(l.getLaborName(), "-"),
                notificationType.WARNING,
                () -> {
                    boolean ok = database.resignLabor(l.getLaborId());
                    if (ok) messageBoxService.toast("Success", "Labor marked as resigned.", notificationType.SUCCESS);
                    else messageBoxService.toast("Error", "Failed to resign labor.", notificationType.ERROR);
                    refreshAll();
                },
                () -> {}
        );
    }

    private void confirmUnResign(labors l) {
        messageBoxService.confirm(
                "Confirm UnResign",
                "Set this labor as active again?\n\nName: " + safe(l.getLaborName(), "-"),
                notificationType.INFO,
                () -> {
                    boolean ok = database.reactivateLabor(l.getLaborId()); // make sure you have this
                    if (ok) messageBoxService.toast("Success", "Labor marked as active.", notificationType.SUCCESS);
                    else messageBoxService.toast("Error", "Failed to activate labor.", notificationType.ERROR);
                    refreshAll();
                },
                () -> {}
        );
    }

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        return t.isEmpty() ? fallback : t;
    }
}
