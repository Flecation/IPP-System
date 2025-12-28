package IPPSystem.Models;

import java.sql.Date;

public class tasks extends workItems{
    private int taskId;
    private String taskName;

    public tasks(){}

    //for the tasks details
    public tasks(int projectTypeId, int workItemId, int taskId, String taskName, double minDuration, double maxDuration) {
        super(projectTypeId, workItemId, minDuration, maxDuration);
        this.taskId = taskId;
        this.taskName = taskName;
    }

    //for the assign tasks (view)
    public tasks(int assignProjectId, int workItemId, int taskId, String taskName, Date startDate, Date endDate, double projectDuration) {
        super(assignProjectId, workItemId, startDate, endDate, projectDuration);
        this.taskId = taskId;
        this.taskName = taskName;
    }

    //for the assign tasks (inserts)
    public tasks(String projectInstanceName, String workItemName, String taskName, double projectDuration) {
        super(projectInstanceName, workItemName, projectDuration);
        this.taskName = taskName;
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
}
