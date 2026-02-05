package IPPSystem.Controllers;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.projectStatus;
import IPPSystem.DAO.database;
import IPPSystem.Models.skills;
import IPPSystem.Models.tasks;
import IPPSystem.Models.workItems;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class createViewProjectController extends sideBarPaneController {

    // ===== Project Info =====
    @FXML private Label projectTitle;

    @FXML private TextField pManagerTxt;
    @FXML private TextField pTypeTxt;
    @FXML private TextField pLevelTxt;
    @FXML private TextField pBuildingTxt;
    @FXML private TextField pAddressTxt;
    @FXML private TextField pSDateTxt;
    @FXML private TextField pEDateTxt;
    @FXML private TextField pDurationTxt;
    @FXML private TextField pContractValueTxt;

    @FXML private Button projectConfirmBtn;
    @FXML private Button projectCancelBtn;
    @FXML private Button pRedoBtn;

    // ===== Categories =====
    @FXML private TableView<workItems> wItemTable;
    @FXML private TableColumn<workItems, String> wINameCol;
    @FXML private TableColumn<workItems, Void> wIActionCol;
    @FXML private Button wIRedoBtn;

    // ===== Work item details =====
    @FXML private TextField wIBudgetTxt;
    @FXML private TextField wISDateTxt;
    @FXML private TextField wIEDateTxt;
    @FXML private TextField wIDurationTxt; // FIXED: must be TextField in FXML
    @FXML private TextField wITWorkerTxt;
    @FXML private Button wIDetailRedoBtn;

    // ===== Skills =====
    @FXML private TableView<SkillRow> skillTable;
    @FXML private TableColumn<SkillRow, String> skillNameCol;
    @FXML private TableColumn<SkillRow, Double> skillQtyCol;
    @FXML private TableColumn<SkillRow, Void> skillActionCol;
    @FXML private Button skillAddBtn;
    @FXML private Button skillRedoBtn;

    // ===== Tasks =====
    @FXML private TableView<TaskRow> taskTable;
    @FXML private TableColumn<TaskRow, String> taskNameCol;
    @FXML private TableColumn<TaskRow, Double> taskPlanQtyCol;
    @FXML private TableColumn<TaskRow, Double> taskDurationCol;
    @FXML private TableColumn<TaskRow, String> taskSDateCol;
    @FXML private TableColumn<TaskRow, String> taskEDateCol;
    @FXML private TableColumn<TaskRow, Void> taskActionCol;
    @FXML private Button taskAddBtn;
    @FXML private Button taskRedoBtn;

    // ===== Local state =====
    private final ObservableList<workItems> categoryList = FXCollections.observableArrayList();
    private final ObservableList<SkillRow> skillList = FXCollections.observableArrayList();
    private final ObservableList<TaskRow> taskList = FXCollections.observableArrayList();

    private workItems selectedWorkItem;

    // WorkItemId -> Baseline
    private final Map<Integer, WorkItemBaseline> baselineMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Work category table
        wINameCol.setCellValueFactory(new PropertyValueFactory<>("workItemName"));
        wItemTable.setItems(categoryList);

        // Action col (remove from list)
        wIActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button del = new Button("Delete");
            {
                del.setOnAction(e -> {
                    workItems wi = getTableView().getItems().get(getIndex());
                    removeWorkItemAndChildren(wi);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : del);
            }
        });

        // selection listener
        wItemTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedWorkItem = n;
            loadSelectedWorkItemToForm();
            refreshSkillTaskTables();
        });

        // Skills table
        skillNameCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        skillQtyCol.setCellValueFactory(new PropertyValueFactory<>("laborQty"));
        skillTable.setItems(skillList);
        skillActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button del = new Button("Delete");
            {
                del.setOnAction(e -> {
                    SkillRow row = getTableView().getItems().get(getIndex());
                    skillList.remove(row);
                    refreshSkillTaskTables();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : del);
            }
        });

        // Tasks table
        taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        taskPlanQtyCol.setCellValueFactory(new PropertyValueFactory<>("plannedQty"));
        taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        taskSDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        taskEDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        taskTable.setItems(taskList);
        taskActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button del = new Button("Delete");
            {
                del.setOnAction(e -> {
                    TaskRow row = getTableView().getItems().get(getIndex());
                    taskList.remove(row);
                    refreshSkillTaskTables();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : del);
            }
        });

        // Button actions
        wIRedoBtn.setOnAction(e -> loadWorkCategoriesFromTemplate());
        wIDetailRedoBtn.setOnAction(e -> clearWorkItemForm());
        pRedoBtn.setOnAction(e -> clearProjectForm());
        skillRedoBtn.setOnAction(e -> clearSkillForSelected());
        taskRedoBtn.setOnAction(e -> clearTaskForSelected());

        skillAddBtn.setOnAction(e -> addSkillForSelected());
        taskAddBtn.setOnAction(e -> addTaskForSelected());

        projectConfirmBtn.setOnAction(e -> onConfirmSave());
        projectCancelBtn.setOnAction(e -> openInnerView("viewProjects.fxml")); // go back
    }

    // ========================= TEMPLATE LOAD =========================

    private void loadWorkCategoriesFromTemplate() {
        try {
            int typeId = resolveProjectTypeId(pTypeTxt.getText());
            int buildingId = resolveBuildingId(pBuildingTxt.getText());
            int levelId = resolveLevelId(pLevelTxt.getText());

            ObservableList<workItems> fromDb = database.getAllWorkItemsForAutoGeneration(typeId, buildingId, levelId);
            categoryList.setAll(fromDb);

            // init baseline map
            baselineMap.clear();
            for (workItems wi : categoryList) {
                baselineMap.put(wi.getWorkItemId(), new WorkItemBaseline());
            }

            info("Loaded " + categoryList.size() + " work categories.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    // ========================= WORK ITEM FORM =========================

    private void loadSelectedWorkItemToForm() {
        if (selectedWorkItem == null) {
            clearWorkItemForm();
            return;
        }
        WorkItemBaseline b = baselineMap.get(selectedWorkItem.getWorkItemId());
        if (b == null) b = new WorkItemBaseline();

        wIBudgetTxt.setText(b.cost == null ? "" : String.valueOf(b.cost));
        wISDateTxt.setText(b.start == null ? "" : b.start.toString());
        wIEDateTxt.setText(b.end == null ? "" : b.end.toString());
        wIDurationTxt.setText(b.duration == null ? "" : String.valueOf(b.duration));
        wITWorkerTxt.setText(b.laborQty == null ? "" : String.valueOf(b.laborQty));
    }

    private void applyWorkItemFormToBaseline() {
        if (selectedWorkItem == null) return;
        WorkItemBaseline b = baselineMap.computeIfAbsent(selectedWorkItem.getWorkItemId(), k -> new WorkItemBaseline());

        b.cost = parseDoubleOrNull(wIBudgetTxt.getText());
        b.start = parseLocalDateOrNull(wISDateTxt.getText());
        b.end = parseLocalDateOrNull(wIEDateTxt.getText());
        b.duration = parseDoubleOrNull(wIDurationTxt.getText());
        b.laborQty = parseDoubleOrNull(wITWorkerTxt.getText());
    }

    private void clearWorkItemForm() {
        wIBudgetTxt.clear();
        wISDateTxt.clear();
        wIEDateTxt.clear();
        wIDurationTxt.clear();
        wITWorkerTxt.clear();
    }

    private void removeWorkItemAndChildren(workItems wi) {
        if (wi == null) return;
        categoryList.remove(wi);
        baselineMap.remove(wi.getWorkItemId());
        skillList.removeIf(s -> s.workItemId == wi.getWorkItemId());
        taskList.removeIf(t -> t.workItemId == wi.getWorkItemId());

        if (wi == selectedWorkItem) {
            selectedWorkItem = null;
            clearWorkItemForm();
        }
        refreshSkillTaskTables();
    }

    // ========================= SKILLS =========================

    private void addSkillForSelected() {
        try {
            if (selectedWorkItem == null) { error("Select a Work Category first."); return; }

            int typeId = resolveProjectTypeId(pTypeTxt.getText());
            ObservableList<skills> skillOptions = database.getSkillDetails(typeId, selectedWorkItem.getWorkItemId());
            if (skillOptions.isEmpty()) { error("No skill template found for this work item."); return; }

            ChoiceDialog<skills> dialog = new ChoiceDialog<>(skillOptions.get(0), skillOptions);
            dialog.setTitle("Add Worker Type");
            dialog.setHeaderText(null);
            dialog.setContentText("Choose Worker Type:");

            Optional<skills> chosen = dialog.showAndWait();
            if (chosen.isEmpty()) return;

            double laborQty = askDouble("Labor Qty", "Enter labor quantity:");
            double dailyWage = askDouble("Daily Wage", "Enter daily wage per labor:");

            SkillRow row = new SkillRow(selectedWorkItem.getWorkItemId(), chosen.get().getSkillId(),
                    chosen.get().getSkillName(), laborQty, dailyWage);

            // avoid duplicates by skillId+workItem
            boolean exist = skillList.stream().anyMatch(s -> s.workItemId == row.workItemId && s.skillId == row.skillId);
            if (exist) { error("This worker type is already added to this work category."); return; }

            skillList.add(row);
            refreshSkillTaskTables();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void clearSkillForSelected() {
        if (selectedWorkItem == null) return;
        skillList.removeIf(s -> s.workItemId == selectedWorkItem.getWorkItemId());
        refreshSkillTaskTables();
    }

    // ========================= TASKS =========================

    private void addTaskForSelected() {
        try {
            if (selectedWorkItem == null) { error("Select a Work Category first."); return; }

            int typeId = resolveProjectTypeId(pTypeTxt.getText());
            int buildingId = resolveBuildingId(pBuildingTxt.getText());
            int levelId = resolveLevelId(pLevelTxt.getText());

            ObservableList<tasks> taskOptions = database.getAllTasksForAutoGeneration(
                    typeId, selectedWorkItem.getWorkItemId(), buildingId, levelId
            );

            if (taskOptions.isEmpty()) { error("No task template found for this work item."); return; }

            ChoiceDialog<tasks> dialog = new ChoiceDialog<>(taskOptions.get(0), taskOptions);
            dialog.setTitle("Add Task");
            dialog.setHeaderText(null);
            dialog.setContentText("Choose Task:");

            Optional<tasks> chosen = dialog.showAndWait();
            if (chosen.isEmpty()) return;

            double plannedQty = askDouble("Planned Qty", "Enter planned quantity:");
            String uom = askText("Unit", "Enter unit of measure (example: m2, pcs):", "");

            double duration = askDouble("Duration", "Enter duration (days):");
            LocalDate s = askDate("Start Date", "Enter start date (yyyy-MM-dd):");
            LocalDate e = askDate("End Date", "Enter end date (yyyy-MM-dd):");

            TaskRow row = new TaskRow(selectedWorkItem.getWorkItemId(), chosen.get().getTaskId(),
                    chosen.get().getTaskName(), plannedQty, uom, duration, s.toString(), e.toString());

            boolean exist = taskList.stream().anyMatch(t -> t.workItemId == row.workItemId && t.taskId == row.taskId);
            if (exist) { error("This task is already added to this work category."); return; }

            taskList.add(row);
            refreshSkillTaskTables();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void clearTaskForSelected() {
        if (selectedWorkItem == null) return;
        taskList.removeIf(t -> t.workItemId == selectedWorkItem.getWorkItemId());
        refreshSkillTaskTables();
    }

    private void refreshSkillTaskTables() {
        if (selectedWorkItem == null) {
            skillTable.setItems(skillList.filtered(s -> false));
            taskTable.setItems(taskList.filtered(t -> false));
            return;
        }
        int wid = selectedWorkItem.getWorkItemId();
        skillTable.setItems(skillList.filtered(s -> s.workItemId == wid));
        taskTable.setItems(taskList.filtered(t -> t.workItemId == wid));
    }

    // ========================= CONFIRM SAVE =========================

    private void onConfirmSave() {
        try {
            // apply current work item form
            applyWorkItemFormToBaseline();

            // validate project fields
            String typeName = req(pTypeTxt.getText(), "Type");
            String managerName = req(pManagerTxt.getText(), "Manager");
            String buildingName = req(pBuildingTxt.getText(), "Building");
            String levelName = req(pLevelTxt.getText(), "Level");
            String address = req(pAddressTxt.getText(), "Address");

            LocalDate ps = parseDate(req(pSDateTxt.getText(), "Project Start Date"));
            LocalDate pe = parseDate(req(pEDateTxt.getText(), "Project End Date"));

            double contractValue = parseDouble(req(pContractValueTxt.getText(), "Contract Value"), "Contract Value");
            double duration = parseDouble(req(pDurationTxt.getText(), "Duration"), "Duration");

            int projectTypeId = resolveProjectTypeId(typeName);
            int buildingId = resolveBuildingId(buildingName);
            int levelId = resolveLevelId(levelName);
            int managerId = resolveManagerId(managerName);

            if (categoryList.isEmpty()) {
                error("Load or add at least one Work Category first (click Categories -> Redo).");
                return;
            }

            // 1) create/assign project
            projects p = new projects(
                    projectTypeId,
                    projectTitle.getText(),
                    buildingId,
                    levelId,
                    0,0,0,0,
                    managerId,
                    address,
                    0,                 // overhead
                    contractValue,     // cost
                    0,                 // labor qty (we will update later)
                    duration,
                    Date.valueOf(ps),
                    Date.valueOf(pe)
            );

            boolean ok = database.setAssignProject(p, projectStatus.PLANNING, assignStatus.CUSTOM);
            if (!ok) { error("Project create failed (assignFullProject returned false)."); return; }

            // IMPORTANT: your DAO doesn't return new assignProjectId.
            // We get it by searching latest project with same name (best practical way with current DAO).
            int assignProjectId = findLatestAssignProjectId(projectTitle.getText());
            if (assignProjectId <= 0) { error("Project created but cannot find assignProjectId."); return; }

            // 2) assign work items
            for (workItems wi : categoryList) {
                WorkItemBaseline b = baselineMap.get(wi.getWorkItemId());
                if (b == null) b = new WorkItemBaseline();

                workItems assignWi = new workItems(assignProjectId, wi.getWorkItemId(),
                        Date.valueOf(nvlDate(b.start, ps)),
                        Date.valueOf(nvlDate(b.end, pe)),
                        nvlDouble(b.duration, duration)
                );

                assignWi.setProjectCost(nvlDouble(b.cost, 0));
                assignWi.setProjectLaborQty(nvlDouble(b.laborQty, 0));

                database.setAssignWorkItems(assignWi, projectStatus.PLANNING, assignStatus.CUSTOM);
            }

            // 3) after assigning work items, load assignedWorkItemId by name (because SP does not return workItemId)
            Map<String, Integer> assignWorkItemIdByName = database.getAllWorkItemsByAssignProject(assignProjectId)
                    .stream()
                    .collect(Collectors.toMap(
                            workItems::getWorkItemName,
                            workItems::getAssignWorkItemId,
                            (a,b) -> a
                    ));

            // 4) add skills (needs assignWorkItemId)
            for (SkillRow s : skillList) {
                String workItemName = findWorkItemNameById(s.workItemId);
                Integer assignWorkItemId = assignWorkItemIdByName.get(workItemName);
                if (assignWorkItemId == null) continue;

                skills sk = new skills(assignWorkItemId, s.skillId, s.laborQty, s.dailyWage);
                database.setSkillsToWorkItem(sk, assignStatus.CUSTOM);
            }

            // 5) assign tasks (needs assignProjectId + workItemId)
            for (TaskRow t : taskList) {
                tasks tk = new tasks(
                        assignProjectId,
                        t.workItemId,
                        t.taskId,
                        Date.valueOf(parseDate(t.startDate)),
                        Date.valueOf(parseDate(t.endDate)),
                        t.duration,
                        t.plannedQty,
                        t.unit
                );
                database.setAssignTaskToWorkItem(tk, projectStatus.PLANNING, assignStatus.CUSTOM);
            }

            // 6) update project baseline totals (optional but good)
            double totalLabor = baselineMap.values().stream().mapToDouble(b -> nvlDouble(b.laborQty, 0)).sum();
            projects update = new projects(assignProjectId, duration, contractValue, totalLabor,
                    Date.valueOf(ps), Date.valueOf(pe));
            database.updateAssignProject(update, assignStatus.CUSTOM);

            data.reload(); // refresh storage cache
            info("Project saved successfully.");
            openInnerView("viewProjects.fxml");

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private int findLatestAssignProjectId(String projectName) {
        int id = -1;
        for (projects p : database.getAllProjects()) {
            if (p.getProjectInstanceName() != null && p.getProjectInstanceName().equalsIgnoreCase(projectName)) {
                id = Math.max(id, p.getAssignProjectId());
            }
        }
        return id;
    }

    // ========================= ID RESOLVERS =========================

    private int resolveProjectTypeId(String typeName) {
        Map<Integer,String> map = database.getAllProjectTypes();
        return findKeyByValue(map, typeName, "Project Type");
    }

    private int resolveBuildingId(String buildingName) {
        Map<Integer,String> map = database.getAllBuildings();
        return findKeyByValue(map, buildingName, "Building");
    }

    private int resolveLevelId(String levelName) {
        Map<Integer,String> map = database.getAllLevels();
        return findKeyByValue(map, levelName, "Level");
    }

    private int resolveManagerId(String managerName) {
        for (users u : database.getAllUsers()) {
            if (u.getUserName() != null && u.getUserName().equalsIgnoreCase(managerName.trim())) {
                return u.getUserId();
            }
        }
        throw new IllegalArgumentException("Manager name not found in users: " + managerName);
    }

    private int findKeyByValue(Map<Integer,String> map, String value, String label) {
        if (value == null) throw new IllegalArgumentException(label + " is required.");
        String v = value.trim();
        for (var e : map.entrySet()) {
            if (e.getValue() != null && e.getValue().equalsIgnoreCase(v)) return e.getKey();
        }
        throw new IllegalArgumentException(label + " not found: " + value);
    }

    private String findWorkItemNameById(int workItemId) {
        for (workItems wi : categoryList) {
            if (wi.getWorkItemId() == workItemId) return wi.getWorkItemName();
        }
        return null;
    }

    // ========================= UTIL =========================

    private void clearProjectForm() {
        pManagerTxt.clear();
        pTypeTxt.clear();
        pLevelTxt.clear();
        pBuildingTxt.clear();
        pAddressTxt.clear();
        pSDateTxt.clear();
        pEDateTxt.clear();
        pDurationTxt.clear();
        pContractValueTxt.clear();
    }

    private String req(String v, String field) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        return v.trim();
    }

    private double parseDouble(String v, String field) {
        try { return Double.parseDouble(v.trim()); }
        catch (Exception e) { throw new IllegalArgumentException(field + " must be a number."); }
    }

    private Double parseDoubleOrNull(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        return Double.parseDouble(v.trim());
    }

    private LocalDate parseDate(String v) {
        try { return LocalDate.parse(v.trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Date must be yyyy-MM-dd. Example: 2026-02-04"); }
    }

    private LocalDate parseLocalDateOrNull(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        return parseDate(v);
    }

    private LocalDate nvlDate(LocalDate a, LocalDate fallback) {
        return a == null ? fallback : a;
    }

    private double nvlDouble(Double a, double fallback) {
        return a == null ? fallback : a;
    }

    private double askDouble(String title, String message) {
        TextInputDialog d = new TextInputDialog("0");
        d.setTitle(title);
        d.setHeaderText(null);
        d.setContentText(message);
        String val = d.showAndWait().orElseThrow(() -> new IllegalArgumentException("Cancelled."));
        return parseDouble(val, title);
    }

    private String askText(String title, String message, String def) {
        TextInputDialog d = new TextInputDialog(def);
        d.setTitle(title);
        d.setHeaderText(null);
        d.setContentText(message);
        return d.showAndWait().orElse(def);
    }

    private LocalDate askDate(String title, String message) {
        TextInputDialog d = new TextInputDialog(LocalDate.now().toString());
        d.setTitle(title);
        d.setHeaderText(null);
        d.setContentText(message);
        String val = d.showAndWait().orElseThrow(() -> new IllegalArgumentException("Cancelled."));
        return parseDate(val);
    }

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    // ========================= INNER ROW TYPES =========================

    public static class WorkItemBaseline {
        public Double cost;       // budget
        public LocalDate start;
        public LocalDate end;
        public Double duration;   // days
        public Double laborQty;   // total workers
    }

    public static class SkillRow {
        public int workItemId;
        public int skillId;
        public String skillName;
        public double laborQty;
        public double dailyWage;

        public SkillRow(int workItemId, int skillId, String skillName, double laborQty, double dailyWage) {
            this.workItemId = workItemId;
            this.skillId = skillId;
            this.skillName = skillName;
            this.laborQty = laborQty;
            this.dailyWage = dailyWage;
        }

        public String getSkillName() { return skillName; }
        public double getLaborQty() { return laborQty; }
    }

    public static class TaskRow {
        public int workItemId;
        public int taskId;
        public String taskName;
        public double plannedQty;
        public String unit;
        public double duration;
        public String startDate;
        public String endDate;

        public TaskRow(int workItemId, int taskId, String taskName, double plannedQty, String unit,
                       double duration, String startDate, String endDate) {
            this.workItemId = workItemId;
            this.taskId = taskId;
            this.taskName = taskName;
            this.plannedQty = plannedQty;
            this.unit = unit;
            this.duration = duration;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getTaskName() { return taskName; }
        public double getPlannedQty() { return plannedQty; }
        public double getDuration() { return duration; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
    }
}
