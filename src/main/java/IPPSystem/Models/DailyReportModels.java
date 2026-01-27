package IPPSystem.Models;

import java.sql.Date;

public class DailyReportModels {

    // DailyReport model (for dailyreports table)
    public static class DailyReportModel {
        private int dailyReportId;
        private int assignProjectId;
        private Date reportDate;
        private int supervisorId;
        private String weather;
        private String generalRemark;
        private String issue;

        // Constructors
        public DailyReportModel() {}

        public DailyReportModel(int assignProjectId, Date reportDate, int supervisorId,
                                String weather, String generalRemark, String issue) {
            this.assignProjectId = assignProjectId;
            this.reportDate = reportDate;
            this.supervisorId = supervisorId;
            this.weather = weather;
            this.generalRemark = generalRemark;
            this.issue = issue;
        }

        // Getters and Setters
        public int getDailyReportId() { return dailyReportId; }
        public void setDailyReportId(int dailyReportId) { this.dailyReportId = dailyReportId; }

        public int getAssignProjectId() { return assignProjectId; }
        public void setAssignProjectId(int assignProjectId) { this.assignProjectId = assignProjectId; }

        public Date getReportDate() { return reportDate; }
        public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

        public int getSupervisorId() { return supervisorId; }
        public void setSupervisorId(int supervisorId) { this.supervisorId = supervisorId; }

        public String getWeather() { return weather; }
        public void setWeather(String weather) { this.weather = weather; }

        public String getGeneralRemark() { return generalRemark; }
        public void setGeneralRemark(String generalRemark) { this.generalRemark = generalRemark; }

        public String getIssue() { return issue; }
        public void setIssue(String issue) { this.issue = issue; }
    }

    // DailyReportLabor model (for dailyreportlabors table)
    public static class DailyReportLaborModel {
        private int dailyReportLaborId;
        private int dailyReportId;
        private int laborId;
        private double workHours;
        private double dailyWage;
        private String remark;

        // Constructors
        public DailyReportLaborModel() {}

        public DailyReportLaborModel(int dailyReportId, int laborId, double workHours,
                                     double dailyWage, String remark) {
            this.dailyReportId = dailyReportId;
            this.laborId = laborId;
            this.workHours = workHours;
            this.dailyWage = dailyWage;
            this.remark = remark;
        }

        // Getters and Setters
        public int getDailyReportLaborId() { return dailyReportLaborId; }
        public void setDailyReportLaborId(int dailyReportLaborId) { this.dailyReportLaborId = dailyReportLaborId; }

        public int getDailyReportId() { return dailyReportId; }
        public void setDailyReportId(int dailyReportId) { this.dailyReportId = dailyReportId; }

        public int getLaborId() { return laborId; }
        public void setLaborId(int laborId) { this.laborId = laborId; }

        public double getWorkHours() { return workHours; }
        public void setWorkHours(double workHours) { this.workHours = workHours; }

        public double getDailyWage() { return dailyWage; }
        public void setDailyWage(double dailyWage) { this.dailyWage = dailyWage; }

        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    // DailyReportTask model (for dailyreporttasks table)
    public static class DailyReportTaskModel {
        private int dailyReportTaskId;
        private int dailyReportId;
        private int assignTaskId;
        private String progressDescription;
        private double workHours;
        private double completedQty;
        private boolean isCompleted;

        // Constructors
        public DailyReportTaskModel() {}

        public DailyReportTaskModel(int dailyReportId, int assignTaskId, String progressDescription,
                                    double workHours, double completedQty, boolean isCompleted) {
            this.dailyReportId = dailyReportId;
            this.assignTaskId = assignTaskId;
            this.progressDescription = progressDescription;
            this.workHours = workHours;
            this.completedQty = completedQty;
            this.isCompleted = isCompleted;
        }

        // Getters and Setters
        public int getDailyReportTaskId() { return dailyReportTaskId; }
        public void setDailyReportTaskId(int dailyReportTaskId) { this.dailyReportTaskId = dailyReportTaskId; }

        public int getDailyReportId() { return dailyReportId; }
        public void setDailyReportId(int dailyReportId) { this.dailyReportId = dailyReportId; }

        public int getAssignTaskId() { return assignTaskId; }
        public void setAssignTaskId(int assignTaskId) { this.assignTaskId = assignTaskId; }

        public String getProgressDescription() { return progressDescription; }
        public void setProgressDescription(String progressDescription) { this.progressDescription = progressDescription; }

        public double getWorkHours() { return workHours; }
        public void setWorkHours(double workHours) { this.workHours = workHours; }

        public double getCompletedQty() { return completedQty; }
        public void setCompletedQty(double completedQty) { this.completedQty = completedQty; }

        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }
    }
}