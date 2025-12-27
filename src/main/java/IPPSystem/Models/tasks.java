package IPPSystem.Models;

public class tasks extends workItems{
        protected  int taskId;
        protected String taskName;

        public tasks(){}


    //for the task details
    public tasks(int assignProjectId, int workItemId, int taskId, String taskName, double minDuration, double maxDuration) {
        super(assignProjectId, workItemId, minDuration, maxDuration);
        this.taskId = taskId;
        this.taskName = taskName;
    }

    //for the assign tasks( auto assign or custom assign)
    public tasks(int assignProjectId, int workItemId, int taskId, String taskName, double duration, double cost, double laborQty) {
        super(assignProjectId, workItemId, duration, cost, laborQty);
        this.taskId = taskId;
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
