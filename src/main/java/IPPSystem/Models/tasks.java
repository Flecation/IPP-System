package IPPSystem.Models;

import java.sql.Date;

public class tasks extends workItems{
    private int assignTaskId;
    private int taskId;
    private String taskName;
    private double plannedQty;
    private String unitOfMeasure;


    public tasks(){}

    //for the tasks details
    public tasks(int projectTypeId, int workItemId, int taskId, String taskName, double minDuration, double maxDuration) {
        super(projectTypeId, workItemId, minDuration, maxDuration);
        this.taskId = taskId;
        this.taskName = taskName;
    }

    //for the assign tasks (view)
    public tasks(int assignTaskId,int assignProjectId, int workItemId, int taskId, String taskName, Date startDate, Date endDate, double projectDuration) {
        super(assignProjectId, workItemId, startDate, endDate, projectDuration);
        this.assignTaskId = assignTaskId;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    //for the assign tasks (inserts)
    public tasks(String projectInstanceName, String workItemName, String taskName, double projectDuration) {
        super(projectInstanceName, workItemName, projectDuration);
        this.taskName = taskName;
    }

    public tasks(int assignProjectId, int workItemId, int taskId, Date startDate, Date endDate, double projectDuration) {
        super(assignProjectId, workItemId, startDate, endDate, projectDuration);
        this.taskId = taskId;
    }

    public tasks(int assignProjectId, int workItemId, int taskId, Date startDate, Date endDate, double projectDuration, double plannedQty, String unitOfMeasure) {
        super(assignProjectId, workItemId, startDate, endDate, projectDuration);
        this.taskId = taskId;
        this.plannedQty = plannedQty;
        this.unitOfMeasure = unitOfMeasure;
    }

    public tasks( int assignTaskId, String taskName, String projectStatus,String assignStatus, Date startDate, Date endDate, double projectDuration) {
        super(projectStatus,assignStatus, startDate, endDate, projectDuration);
        this.assignTaskId = assignTaskId;
        this.taskName = taskName;
    }

    public tasks(int assignTaskId, int assignWorkItemId, int taskId, String taskName, Date startDate, Date endDate, double projectDuration) {
        super(assignWorkItemId, startDate, endDate, projectDuration);
        this.assignTaskId = assignTaskId;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    public tasks(int assignTaskId, String taskName,String projectStatus, String assignStatus, double projectDuration, Date startDate, Date endDate ) {
        super(projectStatus, assignStatus, startDate, endDate, projectDuration);
        this.assignTaskId = assignTaskId;
        this.taskName = taskName;
    }

    public tasks(int assignTaskId, String taskName, String projectStatus, String assignStatus, double projectDuration, Date startDate, Date endDate, double plannedQty, String unitOfMeasure) {
        super(projectStatus, assignStatus, startDate, endDate, projectDuration);
        this.assignTaskId = assignTaskId;
        this.taskName = taskName;
        this.plannedQty = plannedQty;
        this.unitOfMeasure = unitOfMeasure;
    }

    public int getAssignTaskId() {
        return assignTaskId;
    }

    public void setAssignTaskId(int assignTaskId) {
        this.assignTaskId = assignTaskId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public double getPlannedQty() {
        return plannedQty;
    }

    public void setPlannedQty(double plannedQty) {
        this.plannedQty = plannedQty;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
