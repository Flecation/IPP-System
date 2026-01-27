package IPPSystem.Models;

import java.sql.Date;

public class DailyReportModel {
    private int dailyReportId;
    private int assignProjectId;
    private Date reportDate;
    private int supervisorId;
    private String weather;
    private String generalRemark;
    private String issue;

    // Getters and setters
    public int getDailyReportId() {
        return dailyReportId;
    }

    public void setDailyReportId(int dailyReportId) {
        this.dailyReportId = dailyReportId;
    }

    public int getAssignProjectId() {
        return assignProjectId;
    }

    public void setAssignProjectId(int assignProjectId) {
        this.assignProjectId = assignProjectId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getGeneralRemark() {
        return generalRemark;
    }

    public void setGeneralRemark(String generalRemark) {
        this.generalRemark = generalRemark;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}