package IPPSystem.Controllers;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.Interfaces.*;
import IPPSystem.Models.*;
import IPPSystem.Utils.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.HashMap;
import java.util.Map;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
public class workItemDetailsController implements loadPaneAware, NavAware, TabStateful, SearchablePage, SuggestablePage, ReloadablePage {

    //    ==== title ====
    @FXML private Label workItemTitle,workItemTitleStatus;
    // ===== Top cards =====
    @FXML private Label actualCost;
    @FXML private Label actualCostPercent;
    @FXML private ProgressBar actualCostProgress;

    @FXML private Label earnValuePercent;
    @FXML private ProgressBar earnValueProgress;

    @FXML private Label totalCost;
    @FXML private Label totalEarnValue;
    @FXML private Label usedEarnValue;

    // ===== SPI / CPI =====
    @FXML private Label spiStatusLbl;
    @FXML private Circle spiCircle;
    @FXML private Label spiCircleRate;

    @FXML private Label cpiStatusLbl;
    @FXML private Circle cpiCircle;
    @FXML private Label cpiCircleRate;

    // ===== View-only WorkItem info =====
    @FXML private VBox viewOnlyWorkItemInfo;
    @FXML private Label viewBudget;
    @FXML private Label viewPlanStartDate;
    @FXML private Label viewPlanEndDate;
    @FXML private Label viewActualStartDate;
    @FXML private Label viewActualEndDate;
    @FXML private Label viewDuration;
    @FXML private Label viewTotalLabors;

    // ===== Skill + Task tables =====
    @FXML private TableView<skills> viewSkillTable;
    @FXML private TableColumn<skills, String> viewSkillCol;
    @FXML private TableColumn<skills, Double> viewQtyCol;

    // ===== Task table =====
    @FXML private TableView<tasks> taskTable;
    @FXML private TableColumn<tasks, String> taskNameCol;
    @FXML private TableColumn<tasks, Double> taskDurationCol;
    @FXML private TableColumn<tasks, Date> taskPlanStartDateCol;
    @FXML private TableColumn<tasks, Date> taskPlanEndDateCol;
    @FXML private TableColumn<tasks, Date> taskActualStartDate;
    @FXML private TableColumn<tasks, Date> taskActualEndDateCol;
    @FXML private TableColumn<tasks, String> TaskStatusCol;
    @FXML private TableColumn<tasks, Void> taskActionCol;

    // In your FXML: fx:id="backToProjectDetails"
    @FXML private Button backToProjectDetails;

    @FXML private Button wItemEditBtn;

    private workItems workItem;
    private final calculationHelper helper = calculationHelper.getInstance();
    private boolean canEditTasks = true;

    // ===== Planning stage flag (used for Actual date display) =====
    private boolean isPlanningStage = false;

    // ===== Sidebar search support =====
    private final ObservableList<tasks> allTasks = FXCollections.observableArrayList();
    private final ObservableList<skills> allSkills = FXCollections.observableArrayList();
    private final FilteredList<tasks> filteredTasks = new FilteredList<>(allTasks, t -> true);
    private final FilteredList<skills> filteredSkills = new FilteredList<>(allSkills, s -> true);
    private String searchQuery = "";


    private StackPane loadPane;

    private users loginUser = session.getInstance().getUser();
    private projects project;
    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }


    // Flexible date parser: supports "2026-01-14" and "14-JAN-2026"
    private static final DateTimeFormatter FLEX_DATE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE) // yyyy-MM-dd
            .appendOptional(DateTimeFormatter.ofPattern("d-MMM-uuuu", Locale.ENGLISH))
            .appendOptional(DateTimeFormatter.ofPattern("dd-MMM-uuuu", Locale.ENGLISH))
            .toFormatter(Locale.ENGLISH);


    private sideBarPaneController nav;

    @Override
    public void setNav(sideBarPaneController nav){
        this.nav = nav;
    }

    @Override
    public Map<String, Object> exportState() {
        Map<String, Object> s = new HashMap<>();

        if (workItem != null) {
            s.put("assignWorkItemId", workItem.getAssignWorkItemId());
        }
        if (project != null) {
            s.put("assignProjectId", project.getAssignProjectId());
        }
        return s;
    }

    @Override
    public void importState(Map<String, Object> state) {
        if (state == null) return;

        Object wiObj = state.get("assignWorkItemId");
        Object pjObj = state.get("assignProjectId");
        if (!(wiObj instanceof Integer assignWorkItemId)) return;
        if (!(pjObj instanceof Integer assignProjectId)) return;

        // 1) Re-find the project (for labels/back navigation)
        projects foundProject = null;
        try {
            for (projects p : storage.getInstance().getAllProjects()) {
                if (p != null && p.getAssignProjectId() == assignProjectId) {
                    foundProject = p;
                    break;
                }
            }
        } catch (Exception ignored) {}

        // 2) Re-find the work item from DB by project
        workItems foundItem = null;
        try {
            var list = database.getAllWorkItemsByAssignProject(assignProjectId);
            for (workItems wi : list) {
                if (wi != null && wi.getAssignWorkItemId() == assignWorkItemId) {
                    foundItem = wi;
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (foundItem != null) {
            this.project = foundProject;         // ✅ keep it in the controller field
            setWorkItem(foundItem, foundProject);
        }

    }

    private sideBarPaneController requireNav() {
        if (nav != null) return nav;

        if (loadPane != null) {
            nav = (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        }

        if (nav == null) {
            System.out.println("[workItemDetailsController] nav is NULL. loadPane="
                    + (loadPane == null ? "NULL" : System.identityHashCode(loadPane)));
        }
        return nav;
    }

    @Override
    public void onReload() {
        if (workItem == null) return;

        // Re-run your existing loader (refreshes tables + dashboard + labels)
        setWorkItem(workItem, project);
    }


    users user = session.getInstance().getUser();
    @FXML
    public void initialize() {
        if(user.getUserRole().equals(role.MANAGER.toString())){
            taskActionCol.setVisible(true);
        }else {
            taskActionCol.setVisible(false);
        }
        wItemEditBtn.setGraphic(utils.iconSet(FontAwesomeSolid.EDIT));
        wItemEditBtn.setOnAction(e->{
            messageBoxService.toast("Not Allow To Edit",
                    "Buy Premium To Edit!!",
                    notificationType.WARNING);});

        // ---- Task table mapping ----
        if (taskNameCol != null) taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        if (taskDurationCol != null) taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("projectDuration"));
        if (taskDurationCol != null) {
            taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("projectDuration"));

            taskDurationCol.setCellFactory(col -> new TableCell<tasks, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.valueOf(utils.roundDays(item)));
                    }
                }
            });
        }


        if (taskPlanStartDateCol != null) taskPlanStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskPlanEndDateCol != null) taskPlanEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // Your tasks model currently mirrors planned into actual in UI
        if (taskActualStartDate != null) taskActualStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        if (taskActualEndDateCol != null) taskActualEndDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // Display rule: if project is in Planning, show "-" for Actual dates
        if (taskActualStartDate != null) {
            taskActualStartDate.setCellFactory(col -> new TableCell<tasks, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setText(null); return; }
                    if (isPlanningStage) { setText("-"); return; }
                    setText(item == null ? "-" : utils.dateFormat(item));
                }
            });
        }
        if (taskActualEndDateCol != null) {
            taskActualEndDateCol.setCellFactory(col -> new TableCell<tasks, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) { setText(null); return; }
                    if (isPlanningStage) { setText("-"); return; }
                    setText(item == null ? "-" : utils.dateFormat(item));
                }
            });
        }
        if (TaskStatusCol != null) TaskStatusCol.setCellValueFactory(new PropertyValueFactory<>("projectStatus"));

        // ---- Skill table ----
        if (viewSkillCol != null) viewSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        if (viewQtyCol != null) viewQtyCol.setCellValueFactory(new PropertyValueFactory<>("projectLaborQty"));

        // Nice placeholders
        if (taskTable != null) taskTable.setPlaceholder(new Label("No tasks to show."));
        if (viewSkillTable != null) viewSkillTable.setPlaceholder(new Label("No skills assigned."));

        // Bind tables to filtered lists (for sidebar search)
        if (taskTable != null) taskTable.setItems(filteredTasks);
        if (viewSkillTable != null) viewSkillTable.setItems(filteredSkills);

        // ---- Action column: Edit button ----
        setupTaskActionColumn();

    }

    public void setWorkItem(workItems item, projects project) {
        this.workItem = item;
        this.project = project;
        if (item == null) return;

        // Supervisor can't edit (same style as your projectDetailsController)
        boolean isSupervisor = (loginUser != null)
                && role.SUPERVISOR.toString().equals(loginUser.getUserRole());
        this.canEditTasks = !isSupervisor;

        // Load tables (async to avoid UI freeze)
        final int assignWorkItemId = item.getAssignWorkItemId();

        javafx.concurrent.Task<java.lang.Object[]> loadTablesTask = new javafx.concurrent.Task<>() {
            @Override
            protected java.lang.Object[] call() {
                javafx.collections.ObservableList<IPPSystem.Models.tasks> tasksList =
                        database.getAllTasksByAssignWorkItem(assignWorkItemId);
                javafx.collections.ObservableList<IPPSystem.Models.skills> skillsList =
                        database.getAllSkillByAssignWorkItemDetails(assignWorkItemId);
                return new java.lang.Object[]{tasksList, skillsList};
            }
        };

        loadTablesTask.setOnSucceeded(ev -> {
            try {
                javafx.collections.ObservableList<IPPSystem.Models.tasks> tasksList = (javafx.collections.ObservableList<IPPSystem.Models.tasks>) loadTablesTask.getValue()[0];
                javafx.collections.ObservableList<IPPSystem.Models.skills> skillsList = (javafx.collections.ObservableList<IPPSystem.Models.skills>) loadTablesTask.getValue()[1];

                allTasks.setAll(tasksList);
                allSkills.setAll(skillsList);
                applySearchFilters();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadTablesTask.setOnFailed(ev -> {
            loadTablesTask.getException().printStackTrace();
            try {
                IPPSystem.Utils.messageBoxService.toast(
                        "Failed to load work item details",
                        String.valueOf(loadTablesTask.getException().getMessage()),
                        IPPSystem.Constants.notificationType.ERROR
                );
            } catch (Exception ignored) {}
        });

        new Thread(loadTablesTask, "load-workitem-tables").start();

        // Load baseline fields
        reloadFieldsFromModel();

        // Load EVM numbers (BAC/PV/EV/AC/CPI/SPI) from calculation helper
        loadDashboardAsync(item.getAssignWorkItemId(), LocalDate.now());
        // Navigate within the same tab (no need to store loadPane)
        backToProjectDetails.setOnAction(e -> {
            sideBarPaneController n = requireNav();
            if (n == null) return;

            n.openInnerView("projectDetails.fxml", ctrl -> {
                if (ctrl instanceof projectDetailsController c) {
                    // If project is null (new tab case), use foundProject from importState
                    if (this.project != null) c.setProjectData(this.project);
                }
            });

            if (this.project != null) {
                linkButton.getInstance().setTabButtonName(this.project.getProjectInstanceName() + " View");
            }
        });
        workItemTitle.setText(item.getWorkItemName());
        workItemTitleStatus.setText(item.getProjectStatus()); // no "- " here
        String _st = (workItemTitleStatus.getText() == null) ? "" : workItemTitleStatus.getText().trim().toLowerCase();
        isPlanningStage = _st.contains("planning");
        utils.applyStatusPill(workItemTitleStatus, workItemTitleStatus.getText());

    }

    // ------------------------------------------------------------
    // Task action column
    // ------------------------------------------------------------
    private void setupTaskActionColumn() {
        if (taskActionCol == null) return;

        taskActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            {
                if(loginUser.getUserRole().equals(role.SUPERVISOR.toString()))editBtn.setDisable(true);
                editBtn.getStyleClass().add("task-action-button");
                editBtn.setStyle("-fx-background-color:#4176f2; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:4 10 4 10;");
                editBtn.setOnAction(e -> {
                    messageBoxService.toast("Not Allow To Edit",
                            "Buy Premium To Edit!!",
                            notificationType.WARNING);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                editBtn.setDisable(!canEditTasks);
                setGraphic(editBtn);
            }
        });
    }

    private void saveTaskEditAsync(tasks updated) {
        if (updated == null || workItem == null) return;

        Task<Boolean> save = new Task<>() {
            @Override
            protected Boolean call() {
                return database.editAssignTasks(updated, assignStatus.CUSTOM);
            }

            @Override
            protected void succeeded() {
                Boolean ok = getValue();
                if (ok != null && ok) {
                    messageBoxService.toast("Saved", "Task updated successfully.", notificationType.SUCCESS);

                    if (taskTable != null) {
                        javafx.concurrent.Task<javafx.collections.ObservableList<IPPSystem.Models.tasks>> reloadTasksTask =
                                new javafx.concurrent.Task<>() {
                                    @Override
                                    protected javafx.collections.ObservableList<IPPSystem.Models.tasks> call() {
                                        return database.getAllTasksByAssignWorkItem(workItem.getAssignWorkItemId());
                                    }
                                };
                        reloadTasksTask.setOnSucceeded(ev2 -> taskTable.setItems(reloadTasksTask.getValue()));
                        reloadTasksTask.setOnFailed(ev2 -> reloadTasksTask.getException().printStackTrace());
                        new Thread(reloadTasksTask, "reload-tasks").start();
                        taskTable.refresh();
                    }

                    loadDashboardAsync(workItem.getAssignWorkItemId(), LocalDate.now());
                } else {
                    messageBoxService.toast("Update Failed", "Database returned false.", notificationType.WRONG);
                }
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                messageBoxService.toast("Update Failed",
                        ex == null ? "Unknown error" : ex.getMessage(),
                        notificationType.WRONG);
            }
        };

        Thread t = new Thread(save);
        t.setDaemon(true);
        t.start();
    }

    // ------------------------------------------------------------
    // Dashboard
    // ------------------------------------------------------------
    private void loadDashboardAsync(int assignWorkItemId, LocalDate asOf) {
        Task<calculationHelper.ProjectDashboard> task = new Task<>() {
            @Override
            protected calculationHelper.ProjectDashboard call() {
                return helper.getWorkItemDashboardOnlyNumbers(assignWorkItemId, asOf);
            }

            @Override
            protected void succeeded() {
                calculationHelper.ProjectDashboard d = getValue();
                if (d == null) return;

                double bac = d.bac();
                double ev  = d.ev();
                double ac  = d.ac();

                // ===== Cost card =====
                safeSet(totalCost, formatMoney(bac));
                safeSet(actualCost, formatMoney(ac));
                safeSet(actualCostPercent, formatPercent(ac, bac));
                if (actualCostProgress != null) actualCostProgress.setProgress(clamp01(bac <= 0 ? 0 : ac / bac));

                // ===== Earned value card =====
                safeSet(totalEarnValue, formatMoney(bac));
                safeSet(usedEarnValue, formatMoney(ev));
                safeSet(earnValuePercent, formatPercent(ev, bac));
                if (earnValueProgress != null) earnValueProgress.setProgress(clamp01(bac <= 0 ? 0 : ev / bac));

                // ===== SPI / CPI =====
                safeSet(spiCircleRate, formatIndex(d.spi()));
                safeSet(spiStatusLbl, statusTextForIndex(d.spi(), true));
                applyCircleProgress(spiCircle, d.spi());

                safeSet(cpiCircleRate, formatIndex(d.cpi()));
                safeSet(cpiStatusLbl, statusTextForIndex(d.cpi(), false));
                applyCircleProgress(cpiCircle, d.cpi());
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    // ONLY paste these edited parts into your existing file:

    private void reloadFieldsFromModel() {
        if (workItem == null) return;

        safeSet(viewBudget, formatMoney(workItem.getProjectCost()));
        safeSet(viewPlanStartDate, utils.dateFormat(workItem.getStartDate()));
        safeSet(viewPlanEndDate, utils.dateFormat(workItem.getEndDate()));

        // Actual date display rule:
        if (isPlanningStage) {
            safeSet(viewActualStartDate, "-");
            safeSet(viewActualEndDate, "-");
        } else {
            // Your current model mirrors planned dates into actual labels
            safeSet(viewActualStartDate, utils.dateFormat(workItem.getStartDate()));
            safeSet(viewActualEndDate, utils.dateFormat(workItem.getEndDate()));
        }
// FIX: duration should be work item duration (not project duration)
        double durDays = workItem.getProjectDuration(); // if your model uses this for workItem duration
        safeSet(viewDuration, utils.roundDays(durDays) + " Days");

        safeSet(viewTotalLabors, formatQty(workItem.getProjectLaborQty()));
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


    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------
    private Double tryParseDouble(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s.trim().replace(",", "")); }
        catch (NumberFormatException e) { return null; }
    }

    private LocalDate tryParseDate(String s) {
        if (s == null) return null;
        try { return LocalDate.parse(s.trim(), FLEX_DATE); }
        catch (DateTimeParseException e) { return null; }
    }

    private void safeSet(Label lbl, String v) {
        if (lbl != null) lbl.setText(v == null ? "" : v);
    }

    private double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }

    private String formatMoney(double value) {
        if (value <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(value) + " MMK";
    }

    private String formatNumber(double v) {
        if (v <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(v);
    }

    private String formatQty(double q) {
        if (q <= 0) return "-";
        return new DecimalFormat("#,##0.##").format(q) + " persons";
    }

    private String formatPercent(double numerator, double denominator) {
        if (denominator <= 0) return "-";
        long pct = Math.round((numerator / denominator) * 100.0);
        return pct + "%";
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
    // ------------------------------------------------------------
// Sidebar Search (same behavior as workItemDetails search bar)
// ------------------------------------------------------------
    @Override
    public void onSearch(String query) {
        this.searchQuery = (query == null) ? "" : query.trim().toLowerCase();
        applySearchFilters();
    }

    @Override
    public List<String> getSuggestions(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (q.isBlank()) return java.util.Collections.emptyList();

        Set<String> out = new LinkedHashSet<>();

        for (tasks t : allTasks) {
            if (t == null) continue;
            String name = safeLower(t.getTaskName());
            if (!name.isBlank() && name.contains(q)) out.add(t.getTaskName());
        }
        for (skills s : allSkills) {
            if (s == null) continue;
            String name = safeLower(s.getSkillName());
            if (!name.isBlank() && name.contains(q)) out.add(s.getSkillName());
        }
        return new ArrayList<>(out);
    }

    private void applySearchFilters() {
        final String q = (searchQuery == null) ? "" : searchQuery.trim();

        filteredTasks.setPredicate(t -> {
            if (t == null) return false;
            if (q.isBlank()) return true;

            String name = safeLower(t.getTaskName());
            String status = safeLower(t.getProjectStatus());
            String dur = String.valueOf(utils.roundDays(t.getProjectDuration()));
            String ps = (t.getStartDate() == null) ? "" : utils.dateFormat(t.getStartDate()).toLowerCase();
            String pe = (t.getEndDate() == null) ? "" : utils.dateFormat(t.getEndDate()).toLowerCase();

            return name.contains(q)
                    || status.contains(q)
                    || dur.contains(q)
                    || ps.contains(q)
                    || pe.contains(q);
        });

        filteredSkills.setPredicate(s -> {
            if (s == null) return false;
            if (q.isBlank()) return true;
            String name = safeLower(s.getSkillName());
            return name.contains(q);
        });

        if (taskTable != null) taskTable.refresh();
        if (viewSkillTable != null) viewSkillTable.refresh();
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
