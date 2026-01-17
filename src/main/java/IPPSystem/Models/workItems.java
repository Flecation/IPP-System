package IPPSystem.Models;

import java.sql.Date;

public class workItems extends projects {
    private int workItemId,assignWorkItemId;
    private String workItemName;

    public workItems(){}

    //for the child class
    public workItems(int assignProjectId, int workItemId) {
        super(assignProjectId);
        this.workItemId = workItemId;
    }

    public workItems(int assignProjectId, int workItemId, Date startDate, Date endDate,double projectDuration) {
        super(assignProjectId, startDate, endDate,projectDuration);
        this.workItemId = workItemId;
    }

    public workItems(int projectTypeId, int workItemId, double minDuration, double maxDuration) {
        super(projectTypeId, minDuration, maxDuration);
        this.workItemId = workItemId;
    }

    public workItems(String projectInstanceName, String workItemName, double projectDuration) {
        super(projectInstanceName, projectDuration);
        this.workItemName = workItemName;
    }

    public workItems(String projectStatus,String assignStatus, Date startDate, Date endDate, double projectDuration) {
        super(projectStatus,assignStatus, startDate, endDate, projectDuration);
    }

    //for the workItems details
    public workItems(int projectId,int workItemId, String workItemName, double minDuration, double maxDuration, double minCost, double maxCost, double minLaborQty, double maxLaborQty) {
        super(projectId, minDuration, maxDuration, minCost, maxCost, minLaborQty, maxLaborQty);
        this.workItemId = workItemId;
        this.workItemName = workItemName;
    }

    //for the assign work items (view)
    public workItems(int assignProjectId, int workItemId, String workItemName, double projectDuration, double projectCost, double projectLaborQty, Date startDate, Date endDate) {
        super(assignProjectId, projectDuration, projectCost, projectLaborQty, startDate, endDate);
        this.workItemId = workItemId;
        this.workItemName = workItemName;
    }

    //for the assign work items (insert)
    public workItems(String projectInstanceName, String workItemName, double projectDuration, double projectCost, double projectLaborQty, Date startDate, Date endDate) {
        super(projectInstanceName, projectDuration, projectCost, projectLaborQty, startDate, endDate);
        this.workItemName = workItemName;
    }

    public workItems( int assignWorkItemId,Date startDate, Date endDate, double projectDuration) {
        super(startDate, endDate, projectDuration);
        this.assignWorkItemId = assignWorkItemId;
    }

    public int getAssignWorkItemId() {
        return assignWorkItemId;
    }

    public void setAssignWorkItemId(int assignWorkItemId) {
        this.assignWorkItemId = assignWorkItemId;
    }

    public String getWorkItemName() {
        return workItemName;
    }

    public void setWorkItemName(String workItemName) {
        this.workItemName = workItemName;
    }

    public int getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(int workItemId) {
        this.workItemId = workItemId;
    }
}
