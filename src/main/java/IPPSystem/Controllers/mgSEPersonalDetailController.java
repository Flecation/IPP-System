package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Interfaces.*;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.PaginationHelper;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.stream.Collectors;

public class mgSEPersonalDetailController implements
        loadPaneAware, SearchablePage, SuggestablePage, ReloadablePage, TabStateful {

    // ===== Top progress =====
    @FXML private Label workloadPercent;
    @FXML private ProgressBar workloadProgress;

    @FXML private Label performancePercent;
    @FXML private ProgressBar performanceProgress;

    // ===== Profile =====
    @FXML private Circle supervisorImage;
    @FXML private Label sNameTitle;
    @FXML private Label role;
    @FXML private Label status;

    @FXML private Label sNameLbl;
    @FXML private Label sEmailLbl;
    @FXML private Label sDobLbl;
    @FXML private Label sPhoneLbl;
    @FXML private Label sAddressLbl;

    // ===== Other list + projects =====
    @FXML private VBox otherEngineersPane;
    @FXML private VBox projectContainer;

    // ===== Pagination UI for projects =====
    @FXML private HBox projectPaginationBox;
    @FXML private Button backBtn;

    private StackPane loadPane;

    private users engineer;

    @FXML
    private void clickBack(ActionEvent event) {
        if (loadPane == null) return;

        sideBarPaneController sb =
                (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");

        if (sb != null) {
            // go back to supervisor list page in same tab
            sb.openInnerView("engineerView.fxml");
            // optional tab name (if you want)
            // sb.getLinkButton().setTabButtonName("Supervisor View");
        }
    }


    private PaginationHelper<projects> projectPagination;
    private static final int PROJECT_PAGE_SIZE = 9;

    // ===== keep original + filtered list =====
    private List<projects> allProjects = new ArrayList<>();
    private List<projects> filteredProjects = new ArrayList<>();

    // ===== sidebar search text =====
    private String searchQuery = "";

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    public void initialize() {
        projectPagination = new PaginationHelper<>(PROJECT_PAGE_SIZE);
        projectPagination.setOnPageChanged(this::renderProjectPage);
    }

    // Called by otherEngineersController when you click another engineer
    public void setEngineer(users engineer) {
        this.engineer = engineer;
        if (engineer == null) return;

        renderProfile(engineer);
        refreshAll(); // load projects + stats
        Platform.runLater(this::loadOtherEngineers);
    }

    // ===== sidebar reload =====
    @Override
    public void onReload() {
        refreshAll();
    }

    private void refreshAll() {
        if (engineer == null) return;

        // stats
        double workload = database.getWorkload(engineer.getUserId());
        workloadProgress.setProgress(workload);
        workloadPercent.setText((int) (workload * 100) + "%");

        double performance = database.getPerformance(engineer.getUserId());
        performanceProgress.setProgress(performance);
        performancePercent.setText((int) (performance * 100) + "%");

        // projects
        List<projects> all = database.getProjectsByEngineer(engineer.getUserId());
        allProjects = (all == null) ? new ArrayList<>() : new ArrayList<>(all);

        applyProjectSearchFilter(); // this will update pagination + UI
    }

    // ===== sidebar search =====
    @Override
    public void onSearch(String query) {
        this.searchQuery = (query == null) ? "" : query.trim().toLowerCase();
        applyProjectSearchFilter();
    }

    // ===== sidebar suggestions =====
    @Override
    public List<String> getSuggestions(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();

        LinkedHashSet<String> out = new LinkedHashSet<>();

        for (projects p : allProjects) {
            if (p == null) continue;

            addIfMatch(out, p.getProjectInstanceName(), q);
            addIfMatch(out, tryGetProjectName(p), q);
            addIfMatch(out, tryGetProjectTypeName(p), q);
            addIfMatch(out, tryGetStatusName(p), q);

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

    // ===== new tab state =====
    @Override
    public Map<String, Object> exportState() {
        Map<String, Object> s = new HashMap<>();
        s.put("engineerId", engineer == null ? null : engineer.getUserId());
        s.put("search", searchQuery);
        s.put("page", projectPagination == null ? 1 : projectPagination.getCurrentPage());
        return s;
    }

    @Override
    public void importState(Map<String, Object> state) {
        if (state == null) return;

        Object search = state.get("search");
        Object page = state.get("page");

        if (search instanceof String s) searchQuery = s;

        refreshAll();

        int p = 1;
        if (page instanceof Number n) p = n.intValue();
        final int finalPage = p;

        Platform.runLater(() -> {
            if (projectPagination != null) projectPagination.goToPage(finalPage);
            if (projectPaginationBox != null) projectPagination.buildButtons(projectPaginationBox);
        });
    }

    // ===== apply filter + pagination =====
    private void applyProjectSearchFilter() {
        if (projectPagination == null) return;

        String q = (searchQuery == null) ? "" : searchQuery;

        if (q.isBlank()) {
            filteredProjects = new ArrayList<>(allProjects);
        } else {
            filteredProjects = allProjects.stream()
                    .filter(p -> projectMatches(p, q))
                    .collect(Collectors.toList());
        }

        setupProjectPagination(filteredProjects);
    }

    private boolean projectMatches(projects p, String q) {
        if (p == null) return false;

        String instance = safe(p.getProjectInstanceName(), "").toLowerCase();
        String name = safe(tryGetProjectName(p), "").toLowerCase();
        String type = safe(tryGetProjectTypeName(p), "").toLowerCase();
        String st = safe(tryGetStatusName(p), "").toLowerCase();

        return instance.contains(q) || name.contains(q) || type.contains(q) || st.contains(q);
    }

    private void setupProjectPagination(List<projects> list) {

        boolean needPaging = list.size() > PROJECT_PAGE_SIZE;

        if (projectPaginationBox != null) {
            projectPaginationBox.setVisible(needPaging);
            projectPaginationBox.setManaged(needPaging);
        }

        projectPagination.setData(list);

        if (list.isEmpty()) {
            projectContainer.getChildren().clear();
            Label empty = new Label(searchQuery == null || searchQuery.isBlank()
                    ? "No projects assigned."
                    : "No projects match your search.");
            empty.getStyleClass().add("info-text");
            projectContainer.getChildren().add(empty);

            if (projectPaginationBox != null) projectPaginationBox.getChildren().clear();
        } else {
            projectPagination.goToPage(1);
        }
    }

    private void renderProjectPage(List<projects> pageData) {

        projectContainer.getChildren().clear();

        if (pageData == null || pageData.isEmpty()) {
            Label empty = new Label(searchQuery == null || searchQuery.isBlank()
                    ? "No projects assigned."
                    : "No projects match your search.");
            empty.getStyleClass().add("info-text");
            projectContainer.getChildren().add(empty);

            if (projectPaginationBox != null) projectPaginationBox.getChildren().clear();
            return;
        }

        // Render cards like viewProjectsController
        ObservableList<projects> obs = FXCollections.observableArrayList(pageData);
        utils.showProjectCards(obs, projectContainer, projectContainer);

        if (projectPaginationBox != null) {
            projectPagination.buildButtons(projectPaginationBox);
        }
    }

    // ===== profile render =====
    private void renderProfile(users engineer) {
        String name = safe(engineer.getUserName(), "-");
        sNameTitle.setText(name);
        sNameLbl.setText(name);

        role.setText(safe(engineer.getUserRole(), "Engineer"));
        status.setText(engineer.isActive() ? "Active" : "Inactive");

        sEmailLbl.setText(safe(engineer.getUserEmail(), "-"));
        sPhoneLbl.setText(safe(engineer.getUserPhone(), "-"));
        sAddressLbl.setText(safe(engineer.getUserAddress(), "-"));
        sDobLbl.setText(engineer.getUserDOB() != null ? engineer.getUserDOB().toString() : "-");
    }

    // ===== other engineers list (unchanged) =====
    public void loadOtherEngineers() {
        if (otherEngineersPane == null) return;

        otherEngineersPane.getChildren().clear();

        @SuppressWarnings("unchecked")
        ObservableList<users> usersList = (ObservableList<users>) database.getAllSupervisors();
        if (usersList == null) return;

        usersList.sort((u1, u2) -> {
            if (engineer != null && u1.getUserId() == engineer.getUserId()) return -1;
            if (engineer != null && u2.getUserId() == engineer.getUserId()) return 1;
            return 0;
        });

        for (users u : usersList) {
            try {
                javafx.fxml.FXMLLoader loader =
                        new javafx.fxml.FXMLLoader(getClass().getResource("/View/otherEngineers.fxml"));

                javafx.scene.Parent card = loader.load();

                otherEngineersController controller = loader.getController();

                boolean isCurrent = engineer != null && u.getUserId() == engineer.getUserId();
                controller.setOtherEngineer(u, isCurrent, this);

                otherEngineersPane.getChildren().add(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ===== safe getters (because your projects model varies across screens) =====
    private String tryGetProjectName(projects p) {
        try {
            // if you have getProjectName()
            return (String) p.getClass().getMethod("getProjectName").invoke(p);
        } catch (Exception ignored) {}
        return null;
    }

    private String tryGetProjectTypeName(projects p) {
        try {
            return (String) p.getClass().getMethod("getProjectTypeName").invoke(p);
        } catch (Exception ignored) {}
        return null;
    }

    private String tryGetStatusName(projects p) {
        try {
            return (String) p.getClass().getMethod("getProjectStatusName").invoke(p);
        } catch (Exception ignored) {}
        try {
            return (String) p.getClass().getMethod("getStatusName").invoke(p);
        } catch (Exception ignored) {}
        return null;
    }

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        return t.isEmpty() ? fallback : t;
    }
}
