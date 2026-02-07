package IPPSystem.Controllers;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.workItems;
import IPPSystem.Utils.*;
import IPPSystem.Interfaces.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;

import java.text.DecimalFormat;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;

import static IPPSystem.Controllers.dashboardController.loginUser;

public class projectDetailsController implements loadPaneAware, SearchablePage, SuggestablePage, NavAware, TabStateful {

    @FXML private Label projectName;
    @FXML private Label projectStatus;
    @FXML private Label projectGeneral;
    @FXML private Button backToViewProjectBtn;

    @FXML private Label dayCompleteLbl;
    @FXML private Label completedDay;
    @FXML private Label totalDay;
    @FXML private ProgressBar dayCompleteProgress;

    @FXML private Label wiCompleteLbl;
    @FXML private Label completedWi;
    @FXML private Label totalWi;
    @FXML private ProgressBar wiCompleteProgress;

    @FXML private Label earnedValueLbl;
    @FXML private Label usedEarnValue;
    @FXML private Label totalEarnValue;
    @FXML private ProgressBar earnValueProgress;

    @FXML private VBox viewOnlyProjectInfo;
    @FXML private Label projectViewLevel;
    @FXML private Label projectViewStartDate;
    @FXML private Label projectViewDuration;
    @FXML private Label projectViewContract;
    @FXML private Label projectViewEndDate;
    @FXML private Label projectViewAddress;
    @FXML private ProgressBar projectProgress;
    @FXML private Label projectProgressLbl;

    @FXML private Circle spiProgressCircle;
    @FXML private Label spiLbl;
    @FXML private Label spiStatusLbl;
    @FXML private Label spiPvLbl;
    @FXML private Label spiEvLbl;

    @FXML private Circle cpiProgressCircle;
    @FXML private Label cpiLbl;
    @FXML private Label cpiStatusLbl;
    @FXML private Label cpiPvLbl;
    @FXML private Label cpiEvLbl;

    @FXML private TableView<workItems> workItemTable;
    @FXML private TableColumn<workItems, String> workItemNameCol;
    @FXML private TableColumn<workItems, String> workItemStatusCol;
    @FXML private TableColumn<workItems, Number> workItemCostCol;
    @FXML private TableColumn<workItems, Number> workItemDurationCol;
    @FXML private TableColumn<workItems, java.sql.Date> workItemStartCol;
    @FXML private TableColumn<workItems, java.sql.Date> workItemEndCol;
    @FXML private Button pDetailEditBtn;

    private ObservableList<workItems> allWorkItems = FXCollections.observableArrayList();
    private String searchQuery = "";

    @Override
    public void onSearch(String query) {
        this.searchQuery = (query == null) ? "" : query.trim().toLowerCase();
        applyWorkItemSearchFilter();
    }

    private void applyWorkItemSearchFilter() {
        if (workItemTable == null) return;
        if (allWorkItems == null) {
            workItemTable.setItems(FXCollections.observableArrayList());
            workItemTable.refresh();
            return;
        }

        String q = (searchQuery == null) ? "" : searchQuery.trim();
        if (q.isEmpty()) {
            workItemTable.setItems(allWorkItems);
            workItemTable.refresh();
            return;
        }

        List<workItems> filtered = new ArrayList<>();
        for (workItems wi : allWorkItems) {
            if (wi == null) continue;

            String name = safeLower(wi.getWorkItemName());
            String st   = safeLower(wi.getProjectStatus());

            // FIX: don’t copy status for cost/duration
            String cost = String.valueOf(wi.getProjectCost());       // if your model has getProjectCost()
            String dur  = String.valueOf(wi.getProjectDuration());   // if your model has getProjectDuration()

            if (name.contains(q) || st.contains(q) || cost.contains(q) || dur.contains(q)) {
                filtered.add(wi);
            }
        }

        workItemTable.setItems(FXCollections.observableArrayList(filtered));
        workItemTable.refresh();
    }

    @Override
    public List<String> getSuggestions(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();
        if (allWorkItems == null || allWorkItems.isEmpty()) return List.of();

        Set<String> out = new LinkedHashSet<>();
        for (workItems wi : allWorkItems) {
            if (wi == null) continue;

            // suggestions from key work-item fields
            addIfMatch(out, wi.getWorkItemName(), q);
            addIfMatch(out, wi.getProjectStatus(), q);

            // if you have more fields in workItems, you can enable these:
            // addIfMatch(out, wi.getAssignStatus(), q);
            // addIfMatch(out, wi.getSupervisorName(), q);

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

    private String safeLower(String s) { return s == null ? "" : s.toLowerCase(); }

    private sideBarPaneController nav;
    @Override public void setNav(sideBarPaneController nav){ this.nav = nav; }

    protected storage data = storage.getInstance();

    @Override
    public Map<String, Object> exportState() {
        Map<String, Object> s = new HashMap<>();
        if (project != null) s.put("assignProjectId", project.getAssignProjectId());
        return s;
    }

    @Override
    public void importState(Map<String, Object> state) {
        if (state == null) return;
        Object idObj = state.get("assignProjectId");
        if (!(idObj instanceof Integer assignProjectId)) return;

        projects found = null;
        for (projects p : data.getAllProjects()) {
            if (p != null && p.getAssignProjectId() == assignProjectId) {
                found = p;
                break;
            }
        }
        if (found != null) setProjectData(found);
    }

    private projects project;
    private final calculationHelper helper = calculationHelper.getInstance();

    private StackPane loadPane;
    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
        if (this.loadPane != null) {
            this.nav = (sideBarPaneController) this.loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        }
    }

    private sideBarPaneController requireNav() {
        if (nav != null) return nav;
        if (loadPane != null) nav = (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        return nav;
    }

    @FXML
    public void initialize() {
        pDetailEditBtn.setGraphic(utils.iconSet(FontAwesomeSolid.EDIT));

        backToViewProjectBtn.setOnAction(e -> {
            sideBarPaneController n = requireNav();
            if (n == null) return;
            n.openInnerView("viewProjects.fxml", ctrl -> {});
            linkButton.getInstance().setTabButtonName("Project View");
        });

        workItemTable.setRowFactory(tv -> {
            TableRow<workItems> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    sideBarPaneController n = requireNav();
                    if (n == null) return;
                    nav.openInnerView("workItemDetails.fxml", ctrl -> {
                        if (ctrl instanceof workItemDetailsController c) {
                            c.setWorkItem(row.getItem(), project);
                            c.setNav(nav);
                        }
                    });
                    linkButton.getInstance().setTabButtonName(row.getItem().getWorkItemName() + " View");
                }
            });
            return row;
        });

        workItemDurationCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.valueOf(utils.roundDays(item.doubleValue())));
            }
        });

        workItemCostCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : utils.formatCompactMoneyMMK(item.doubleValue()));
            }
        });

        pDetailEditBtn.setOnAction(e -> {
            messageBoxService.toast("Not Allow To Edit", "Buy Premium To Edit!!", notificationType.INFO);
        });
    }

    public void setProjectData(projects project) {
        this.project = project;
        if (project == null) return;

        boolean isSupervisor = loginUser != null && role.SUPERVISOR.toString().equals(loginUser.getUserRole());
        pDetailEditBtn.setVisible(!isSupervisor);

        safeSet(projectName, project.getProjectInstanceName());
        projectStatus.setText(project.getProjectStatus());
        utils.applyStatusPill(projectStatus, projectStatus.getText());

        safeSet(projectGeneral,
                (project.getProjectTypeName() != null ? project.getProjectTypeName() : "") +
                        (project.getProjectBuildingName() != null ? (", " + project.getProjectBuildingName()) : "")
        );

        safeSet(projectViewLevel, project.getProjectLevelName());
        safeSet(projectViewAddress, project.getProjectLocation());
        safeSet(projectViewContract, formatMoney(project.getProjectCost()));
        safeSet(projectViewStartDate, utils.dateFormat(project.getStartDate()));
        safeSet(projectViewEndDate, utils.dateFormat(project.getEndDate()));
        safeSet(projectViewDuration, utils.getOnlyOneDuration(project.getProjectDuration(), enumDuration.DAY));

        refreshWorkItems();

        loadDashboardAsync(project.getAssignProjectId(), LocalDate.now());
    }

    // VIEWMODEL to keep DB out of FX thread
    private record ProjectDashVM(calculationHelper.ProjectDashboard d, int completedDaysCount) {}

    private void loadDashboardAsync(int projectId, LocalDate asOf) {
        Task<ProjectDashVM> task = new Task<>() {
            @Override
            protected ProjectDashVM call() {
                calculationHelper.ProjectDashboard d = helper.getProjectDashboard(projectId, asOf);
                int completedDaysCount = database.getCompletedDaysByAssignProject(projectId);
                return new ProjectDashVM(d, completedDaysCount);
            }

            @Override
            protected void succeeded() {
                ProjectDashVM vm = getValue();
                if (vm == null || vm.d == null) return;

                calculationHelper.ProjectDashboard d = vm.d;
                int completedDaysCount = vm.completedDaysCount;

                int total = d.totalDays();
                double day01 = (total <= 0) ? 0 : clamp01(completedDaysCount / (double) total);

                completedDay.setText(completedDaysCount + " days");
                totalDay.setText(total + " days");
                dayCompleteLbl.setText(Math.round(day01 * 100) + "%");
                dayCompleteProgress.setProgress(day01);

                double progress01 = d.progressRatio();
                projectProgress.setProgress(progress01);
                projectProgressLbl.setText(Math.round(progress01 * 100) + "%");

                int doneWi = d.completedWorkItems();
                int totalWiCount = d.totalWorkItems();
                double wi01 = (totalWiCount <= 0) ? 0 : clamp01(doneWi / (double) totalWiCount);

                safeSet(completedWi, doneWi + " items");
                safeSet(totalWi, totalWiCount + " items");
                safeSet(wiCompleteLbl, formatPercent(wi01));
                wiCompleteProgress.setProgress(wi01);

                double bac = d.bac();
                double ev = d.ev();
                double ev01 = (bac <= 0) ? 0 : clamp01(ev / bac);

                safeSet(usedEarnValue, formatMoney(ev));
                safeSet(totalEarnValue, formatMoney(bac));
                safeSet(earnedValueLbl, formatPercent(ev01));
                earnValueProgress.setProgress(ev01);

                safeSet(spiLbl, formatIndex(d.spi()));
                safeSet(spiStatusLbl, statusTextForIndex(d.spi(), true));
                safeSet(spiPvLbl, formatMoney(d.pv()));
                safeSet(spiEvLbl, formatMoney(d.ev()));
                applyCircleProgress(spiProgressCircle, d.spi());

                safeSet(cpiLbl, formatIndex(d.cpi()));
                safeSet(cpiStatusLbl, statusTextForIndex(d.cpi(), false));
                safeSet(cpiPvLbl, formatMoney(d.pv()));
                safeSet(cpiEvLbl, formatMoney(d.ev()));
                applyCircleProgress(cpiProgressCircle, d.cpi());
            }
        };

        Thread t = new Thread(task, "project-dashboard");
        t.setDaemon(true);
        t.start();
    }

    private void refreshWorkItems() {
        if (project == null || workItemTable == null) return;

        Task<ObservableList<workItems>> task = new Task<>() {
            @Override
            protected ObservableList<workItems> call() {
                return database.getAllWorkItemsByAssignProject(project.getAssignProjectId());
            }
        };

        task.setOnSucceeded(e -> {
            allWorkItems.setAll(task.getValue());
            applyWorkItemSearchFilter();
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            try {
                messageBoxService.toast("Failed to load work items",
                        String.valueOf(task.getException().getMessage()),
                        notificationType.ERROR);
            } catch (Exception ignored) {}
        });

        new Thread(task, "load-workitems").start();
    }

    private void safeSet(Label lbl, String v) { if (lbl != null) lbl.setText(v == null ? "" : v); }

    private double clamp01(double v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }

    private String formatPercent(double p01) {
        int pct = (int) Math.round(clamp01(p01) * 100.0);
        return pct + "%";
    }

    private String formatMoney(double value) {
        if (value <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(value) + " MMK";
    }

    private String formatIndex(Double v) {
        if (v == null) return "-";
        return new DecimalFormat("0.00").format(v);
    }

    private String statusTextForIndex(Double idx, boolean isSchedule) {
        if (idx == null) return "No Data";
        if (isSchedule) {
            if (idx >= 1.05) return "Ahead of Schedule";
            if (idx >= 0.95) return "On Schedule";
            return "Behind Schedule";
        } else {
            if (idx >= 1.05) return "Under Budget";
            if (idx >= 0.95) return "On Budget";
            return "Over Budget";
        }
    }

    private void applyCircleProgress(Circle circle, Double idx) {
        if (circle == null) return;

        double radius = circle.getRadius();
        double circumference = 2 * Math.PI * radius;
        circle.getStrokeDashArray().setAll(circumference);

        // optional scale improvement: show 0..2.0 range
        double p = (idx == null) ? 0 : clamp01(idx / 2.0);
        circle.setStrokeDashOffset(circumference * (1 - p));
    }
}
