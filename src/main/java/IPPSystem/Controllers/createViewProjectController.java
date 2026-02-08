package IPPSystem.Controllers;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.projectStatus;
import IPPSystem.Constants.notificationType;
import IPPSystem.DAO.database;
import IPPSystem.Models.skills;
import IPPSystem.Models.tasks;
import IPPSystem.Models.workItems;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.createProjectDraft;

import IPPSystem.Utils.storage;
import IPPSystem.Utils.messageBoxService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Utils.utils;

import javafx.application.Platform;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class createViewProjectController implements loadPaneAware {

    private javafx.scene.layout.StackPane loadPane;

    @Override
    public void setLoadPane(javafx.scene.layout.StackPane loadPane) {
        this.loadPane = loadPane;
    }

    private sideBarPaneController parent() {
        // Resolve per-tab loadPane dynamically so this controller can work without injection
        javafx.scene.layout.StackPane lp = (loadPane != null) ? loadPane : utils.findTabLoadPane(projectCancelBtn);
        if (lp == null) return null;

        Object p = lp.getProperties().get("SIDEBAR_CONTROLLER");
        return (p instanceof sideBarPaneController) ? (sideBarPaneController) p : null;
    }

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
    @FXML private TextField areaTxt,heightTxt,storyTxt,unitsTxt,unitTxt;

    @FXML private Button projectConfirmBtn;
    @FXML private Button projectCancelBtn;

    // ===== Categories =====
    @FXML private TableView<workItems> wItemTable;
    @FXML private TableColumn<workItems, String> wINameCol;
    @FXML private TableColumn<workItems, Void> wIActionCol;

    // ===== Work item details =====
    @FXML private TextField wIBudgetTxt;
    @FXML private TextField wISDateTxt;
    @FXML private TextField wIEDateTxt;
    @FXML private TextField wIDurationTxt; // FIXED: must be TextField in FXML

    // ===== Skills =====
    @FXML private TableView<SkillRow> skillTable;
    @FXML private TableColumn<SkillRow, String> skillNameCol;
    @FXML private TableColumn<SkillRow, Double> skillQtyCol;
    @FXML private TableColumn<SkillRow, Void> skillActionCol;

    // ===== Tasks =====
    @FXML private TableView<TaskRow> taskTable;
    @FXML private TableColumn<TaskRow, String> taskNameCol;
    @FXML private TableColumn<TaskRow, Double> taskPlanQtyCol;
    @FXML private TableColumn<TaskRow, Double> taskDurationCol;
    @FXML private TableColumn<TaskRow, String> taskSDateCol;
    @FXML private TableColumn<TaskRow, String> taskEDateCol;
    @FXML private TableColumn<TaskRow, Void> taskActionCol;
    // ===== Local state =====
    private final ObservableList<workItems> categoryList = FXCollections.observableArrayList();
    private final ObservableList<SkillRow> skillList = FXCollections.observableArrayList();
    private final ObservableList<TaskRow> taskList = FXCollections.observableArrayList();

    private workItems selectedWorkItem;

    private storage data = storage.getInstance();

    // WorkItemId -> Baseline
    private final Map<Integer, WorkItemBaseline> baselineMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Work category table
        wINameCol.setCellValueFactory(new PropertyValueFactory<>("workItemName"));
        wItemTable.setItems(categoryList);

// Action col (Edit/Delete - Premium gated)
        wIActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button edit = makeIconBtn("✎");
            private final Button del  = makeIconBtn("🗑");
            private final HBox box = new HBox(8, edit, del);

            {
                box.setAlignment(Pos.CENTER);
                edit.setOnAction(e -> toastNotAllowed());
                del.setOnAction(e -> toastNotAllowed());
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // selection listener
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
            private final Button edit = makeIconBtn("✎");
            private final Button del  = makeIconBtn("🗑");
            private final HBox box = new HBox(8, edit, del);

            {
                box.setAlignment(Pos.CENTER);
                edit.setOnAction(e -> toastNotAllowed());
                del.setOnAction(e -> toastNotAllowed());
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });


        // Tasks table


        // Tasks table
        taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        taskPlanQtyCol.setCellValueFactory(new PropertyValueFactory<>("plannedQty"));
        taskDurationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        taskSDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        taskEDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        taskTable.setItems(taskList);
        taskActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button edit = makeIconBtn("✎");
            private final Button del  = makeIconBtn("🗑");
            private final HBox box = new HBox(8, edit, del);

            {
                box.setAlignment(Pos.CENTER);
                edit.setOnAction(e -> toastNotAllowed());
                del.setOnAction(e -> toastNotAllowed());
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

// ===== Make preview fields read-only + show toast on click =====
        lockReadOnly(pTypeTxt);
        lockReadOnly(pManagerTxt);
        lockReadOnly(pLevelTxt);
        lockReadOnly(pBuildingTxt);
        lockReadOnly(pSDateTxt);
        lockReadOnly(pEDateTxt);
        lockReadOnly(pContractValueTxt);
        lockReadOnly(pDurationTxt);
        lockReadOnly(pAddressTxt);

        lockReadOnly(wIBudgetTxt);
        lockReadOnly(wISDateTxt);
        lockReadOnly(wIEDateTxt);
        lockReadOnly(wIDurationTxt);



        // Button actions


        projectConfirmBtn.setOnAction(e -> onConfirmSave());
        projectCancelBtn.setOnAction(e -> {
            sideBarPaneController p = parent();
            if (p != null) p.openInnerView("viewProjects.fxml");
        }); // go back

        // If user came from createProject.fxml, auto-fill and auto-generate template data.
        Platform.runLater(this::loadFromDraftAndAutoGenerate);
    }

    /**
     * Called when arriving from createProject.fxml (createProjectController -> createProjectDraft).
     * Fills project fields, loads categories, and pre-fills skills/tasks using your DB templates.
     */
    private void loadFromDraftAndAutoGenerate() {
        try {
            createProjectDraft d = createProjectDraft.getInstance();
            if (d == null || d.instanceName == null || d.instanceName.trim().isEmpty()) {
                return; // opened directly (not from createProject)
            }

            // ----- Fill Project Info UI -----
            if (projectTitle != null) projectTitle.setText(d.instanceName);
            if (pManagerTxt != null) pManagerTxt.setText(nvlStr(d.supervisorName));
            if (pTypeTxt != null) pTypeTxt.setText(nvlStr(d.projectTypeName));
            if (pBuildingTxt != null) pBuildingTxt.setText(nvlStr(d.buildingName));
            if (pLevelTxt != null) pLevelTxt.setText(nvlStr(d.levelName));
            if (pAddressTxt != null) pAddressTxt.setText(nvlStr(d.address));
            if (pSDateTxt != null && d.startDate != null) pSDateTxt.setText(d.startDate.toString());
            if (pEDateTxt != null && d.endDate != null) pEDateTxt.setText(d.endDate.toString());
            if (pDurationTxt != null && d.duration != null) pDurationTxt.setText(String.valueOf(d.duration));
            if (pContractValueTxt != null && d.contractValue != null) pContractValueTxt.setText(String.valueOf(d.contractValue));
            safeSetNumber(areaTxt, d.area);
            if (unitsTxt != null) safeSetNumber(unitsTxt, d.units);
            if (unitTxt != null) safeSetNumber(unitTxt, d.units);
            safeSetNumber(storyTxt, d.stories);
            safeSetNumber(heightTxt, d.height);


            // ----- Auto-generate categories/skills/tasks from template -----
            autoGenerateFromTemplates();

        } catch (Exception ex) {
            // keep user on page even if auto-fill fails
            error(ex.getMessage());
        }
    }

    private void autoGenerateFromTemplates() {
        // Ensure needed fields exist
        if (pTypeTxt == null || pBuildingTxt == null || pLevelTxt == null) return;

        // 1) categories
        loadWorkCategoriesFromTemplate();
        if (categoryList.isEmpty()) return;

        // 2) skills + tasks templates (pre-fill)
        int typeId = getDraftOrResolveTypeId(pTypeTxt.getText());
        int buildingId = getDraftOrResolveBuildingId(pBuildingTxt.getText());
        int levelId = getDraftOrResolveLevelId(pLevelTxt.getText());

        LocalDate projectStart = parseLocalDateOrNull(pSDateTxt == null ? null : pSDateTxt.getText());
        if (projectStart == null) projectStart = LocalDate.now();

        // Clear any previous
        skillList.clear();
        taskList.clear();

        // For each work item, generate:
        for (workItems wi : categoryList) {
            if (wi == null) continue;

            // ----- skills template: pick min as default -----
            ObservableList<skills> skillOptions = database.getSkillDetails(typeId, wi.getWorkItemId());
            for (skills s : skillOptions) {
                double laborQty = Math.max(1, s.getMinRequireLabors());
                double wage = Math.max(0, s.getMinDailyWage());
                SkillRow row = new SkillRow(wi.getWorkItemId(), s.getSkillId(), s.getSkillName(), laborQty, wage);
                boolean exist = skillList.stream().anyMatch(x -> x.workItemId == row.workItemId && x.skillId == row.skillId);
                if (!exist) skillList.add(row);
            }

            // ----- tasks template: chain by minDuration -----
            ObservableList<tasks> taskOptions = database.getAllTasksForAutoGeneration(typeId, wi.getWorkItemId(), buildingId, levelId);
            LocalDate cursor = projectStart;
            for (tasks t : taskOptions) {
                double dur = Math.max(1, t.getMinDuration());
                long durDays = Math.max(1L, Math.round(dur));

                LocalDate s = cursor;
                LocalDate e = cursor.plusDays(durDays - 1);
                cursor = e.plusDays(1);

                TaskRow row = new TaskRow(
                        wi.getWorkItemId(),
                        t.getTaskId(),
                        t.getTaskName(),
                        0,            // plannedQty (user can edit later)
                        "",           // unit (user can edit later)
                        durDays,
                        s.toString(),
                        e.toString()
                );
                boolean exist = taskList.stream().anyMatch(x -> x.workItemId == row.workItemId && x.taskId == row.taskId);
                if (!exist) taskList.add(row);
            }

            // ----- baseline from generated skills+tasks -----
            WorkItemBaseline b = baselineMap.computeIfAbsent(wi.getWorkItemId(), k -> new WorkItemBaseline());

            // duration = sum of task durations for this work item (fallback: project duration)
            double wDur = taskList.stream()
                    .filter(x -> x.workItemId == wi.getWorkItemId())
                    .mapToDouble(x -> x.duration)
                    .sum();

            if (wDur <= 0) {
                Double pd = parseDoubleOrNull(pDurationTxt == null ? null : pDurationTxt.getText());
                wDur = nvlDouble(pd, 1);
            }

            // start/end from tasks
            LocalDate wStart = taskList.stream()
                    .filter(x -> x.workItemId == wi.getWorkItemId())
                    .map(x -> parseLocalDateOrNull(x.startDate))
                    .filter(Objects::nonNull)
                    .min(LocalDate::compareTo)
                    .orElse(projectStart);

            LocalDate wEnd = taskList.stream()
                    .filter(x -> x.workItemId == wi.getWorkItemId())
                    .map(x -> parseLocalDateOrNull(x.endDate))
                    .filter(Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElse(wStart.plusDays(Math.max(1L, Math.round(wDur)) - 1));

            // labor = sum of generated skill labors
            double wLabor = skillList.stream()
                    .filter(x -> x.workItemId == wi.getWorkItemId())
                    .mapToDouble(x -> x.laborQty)
                    .sum();

            // cost estimate = sum(laborQty * wage) * duration
            double dailyCost = skillList.stream()
                    .filter(x -> x.workItemId == wi.getWorkItemId())
                    .mapToDouble(x -> x.laborQty * x.dailyWage)
                    .sum();
            double wCost = dailyCost * wDur;

            b.start = wStart;
            b.end = wEnd;
            b.duration = wDur;
            b.laborQty = wLabor;
            b.cost = wCost;
        }

        // refresh UI
        if (!categoryList.isEmpty()) {
            wItemTable.getSelectionModel().selectFirst();
        }
        refreshSkillTaskTables();
    }


    private void safeSetText(TextField tf, String val) {
        if (tf != null) tf.setText(val == null ? "" : val);
    }

    private void safeSetNumber(TextField tf, Double val) {
        if (tf != null) tf.setText(val == null ? "" : String.valueOf(val));
    }

    private String nvlStr(String s) {
        return s == null ? "" : s;
    }

    // ========================= TEMPLATE LOAD =========================

    private void loadWorkCategoriesFromTemplate() {
        try {
            int typeId = getDraftOrResolveTypeId(pTypeTxt.getText());
            int buildingId = getDraftOrResolveBuildingId(pBuildingTxt.getText());
            int levelId = getDraftOrResolveLevelId(pLevelTxt.getText());

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
    }

    private void applyWorkItemFormToBaseline() {
        if (selectedWorkItem == null) return;
        WorkItemBaseline b = baselineMap.computeIfAbsent(selectedWorkItem.getWorkItemId(), k -> new WorkItemBaseline());

        b.cost = parseDoubleOrNull(wIBudgetTxt.getText());
        b.start = parseLocalDateOrNull(wISDateTxt.getText());
        b.end = parseLocalDateOrNull(wIEDateTxt.getText());
        b.duration = parseDoubleOrNull(wIDurationTxt.getText());
    }

    private void clearWorkItemForm() {
        wIBudgetTxt.clear();
        wISDateTxt.clear();
        wIEDateTxt.clear();
        wIDurationTxt.clear();
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

            int typeId = getDraftOrResolveTypeId(pTypeTxt.getText());
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

            int typeId = getDraftOrResolveTypeId(pTypeTxt.getText());
            int buildingId = getDraftOrResolveBuildingId(pBuildingTxt.getText());
            int levelId = getDraftOrResolveLevelId(pLevelTxt.getText());

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
            sideBarPaneController parent = parent();
            if (p != null) parent.openInnerView("viewProjects.fxml");

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


    private int getDraftOrResolveTypeId(String typeName) {
        Integer id = createProjectDraft.getInstance() == null ? null : createProjectDraft.getInstance().projectTypeId;
        if (id != null) return id;
        return resolveProjectTypeId(typeName);
    }
    private int getDraftOrResolveBuildingId(String buildingName) {
        Integer id = createProjectDraft.getInstance() == null ? null : createProjectDraft.getInstance().buildingId;
        if (id != null) return id;
        return resolveBuildingId(buildingName);
    }
    private int getDraftOrResolveLevelId(String levelName) {
        Integer id = createProjectDraft.getInstance() == null ? null : createProjectDraft.getInstance().levelId;
        if (id != null) return id;
        return resolveLevelId(levelName);
    }

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


    // ===== Premium-gated UI helpers =====
    private void toastNotAllowed() {
        messageBoxService.toast("Not Allow To Edit", "Buy Premium To Edit!!", notificationType.INFO);
    }

    private Button makeIconBtn(String iconText) {
        Button b = new Button(iconText);
        b.setFocusTraversable(false);
        b.getStyleClass().add("cp-icon-btn"); // safe even if css doesn't contain it
        b.setMinWidth(34);
        b.setPrefWidth(34);
        b.setMinHeight(30);
        b.setPrefHeight(30);
        return b;
    }

    private void lockReadOnly(TextField tf) {
        if (tf == null) return;
        tf.setEditable(false);
        tf.setFocusTraversable(false);
        tf.setOnMouseClicked(e -> {
            toastNotAllowed();
            e.consume();
        });
        tf.setOnKeyTyped(e -> e.consume());
        tf.setOnKeyPressed(e -> e.consume());
    }

}