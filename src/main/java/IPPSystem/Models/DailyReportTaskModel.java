package IPPSystem.Models;

public class DailyReportTaskModel {
    private int dailyReportTaskId;
    private int dailyReportId;
    private int assignTaskId;
    private String progressDescription;
    private double workHours;
    private double completedQty;
    private boolean isCompleted;

    // Getters and setters
    public int getDailyReportTaskId() {
        return dailyReportTaskId;
    }

    public void setDailyReportTaskId(int dailyReportTaskId) {
        this.dailyReportTaskId = dailyReportTaskId;
    }

    public int getDailyReportId() {
        return dailyReportId;
    }

    public void setDailyReportId(int dailyReportId) {
        this.dailyReportId = dailyReportId;
    }

    public int getAssignTaskId() {
        return assignTaskId;
    }

    public void setAssignTaskId(int assignTaskId) {
        this.assignTaskId = assignTaskId;
    }

    public String getProgressDescription() {
        return progressDescription;
    }

    public void setProgressDescription(String progressDescription) {
        this.progressDescription = progressDescription;
    }

    public double getWorkHours() {
        return workHours;
    }

    public void setWorkHours(double workHours) {
        this.workHours = workHours;
    }

    public double getCompletedQty() {
        return completedQty;
    }

    public void setCompletedQty(double completedQty) {
        this.completedQty = completedQty;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}