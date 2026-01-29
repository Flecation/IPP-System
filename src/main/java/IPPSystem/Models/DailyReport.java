package IPPSystem.Models;

import java.time.LocalDate;

public class DailyReport {
    private int reportId; // ID လိုလာပြီ
    private int assignProjectId;
    private LocalDate reportDate;
    private String weatherType, workAffect, weatherNote, issues, comments;
    private int supervisorId;

    // UI Display အတွက် Field အပိုများ
    private String projectName;
    private String supervisorName;

    // Constructor for Saving (Create)
    public DailyReport(int assignProjectId, LocalDate reportDate, String weatherType, String workAffect, String weatherNote, String issues, String comments, int supervisorId) {
        this.assignProjectId = assignProjectId;
        this.reportDate = reportDate;
        this.weatherType = weatherType;
        this.workAffect = workAffect;
        this.weatherNote = weatherNote;
        this.issues = issues;
        this.comments = comments;
        this.supervisorId = supervisorId;
    }

    // Constructor for Viewing (List/Table)
    public DailyReport(int reportId, String projectName, LocalDate reportDate, String issues, String supervisorName) {
        this.reportId = reportId;
        this.projectName = projectName;
        this.reportDate = reportDate;
        this.issues = issues;
        this.supervisorName = supervisorName;
    }

    // Full Constructor (View Details)
    public DailyReport(int reportId, int assignProjectId, String projectName, LocalDate reportDate, String weatherType, String workAffect, String weatherNote, String issues, String comments, String supervisorName) {
        this.reportId = reportId;
        this.assignProjectId = assignProjectId;
        this.projectName = projectName;
        this.reportDate = reportDate;
        this.weatherType = weatherType;
        this.workAffect = workAffect;
        this.weatherNote = weatherNote;
        this.issues = issues;
        this.comments = comments;
        this.supervisorName = supervisorName;
    }

    // Getters
    public int getReportId() { return reportId; }
    public String getProjectName() { return projectName; }
    public String getSupervisorName() { return supervisorName; }
    public int getAssignProjectId() { return assignProjectId; }
    public LocalDate getReportDate() { return reportDate; }
    public String getWeatherType() { return weatherType; }
    public String getWorkAffect() { return workAffect; }
    public String getWeatherNote() { return weatherNote; }
    public String getIssues() { return issues; }
    public String getComments() { return comments; }
    public int getSupervisorId() { return supervisorId; }
}