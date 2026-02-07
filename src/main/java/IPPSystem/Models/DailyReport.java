package IPPSystem.Models;

import java.time.LocalDate;

public class DailyReport {
    private int reportId; // ID လိုလာပြီ
    private int assignProjectId;
    private LocalDate reportDate;
    private String weatherType, workAffect, weatherNote, issues, comments,projectType,projectStatus;
    private int supervisorId;
    private int assignWorkItemId;

    // UI Display အတွက် Field အပိုများ
    private String projectName;
    private String supervisorName;
    private String projectTypeName; // NEW




    public String getProjectTypeName() {
        return projectTypeName;
    }

    public void setProjectTypeName(String projectTypeName) {
        this.projectTypeName = projectTypeName;
    }

    public int getAssignWorkItemId() {
        return assignWorkItemId;
    }

    public void setAssignWorkItemId(int assignWorkItemId) {
        this.assignWorkItemId = assignWorkItemId;
    }


    public DailyReport(int reportId, int assignProjectId, String projectName, String projectTypeName,
                       LocalDate reportDate, String issues, String weatherType,
                       String comments, String supervisorName) {
        this.reportId = reportId;
        this.assignProjectId = assignProjectId;
        this.projectName = projectName;
        this.projectTypeName = projectTypeName;
        this.reportDate = reportDate;
        this.issues = issues;
        this.weatherType = weatherType;
        this.comments = comments;
        this.supervisorName = supervisorName;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    // ==== No-arg constructor ထပ်ထည့်မယ် ====
    public DailyReport() {
        // Empty constructor for database operations
    }

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

    // ==== Setters ထပ်ထည့်မယ် ====
    public void setReportId(int reportId) { this.reportId = reportId; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }
    public void setAssignProjectId(int assignProjectId) { this.assignProjectId = assignProjectId; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public void setWeatherType(String weatherType) { this.weatherType = weatherType; }
    public void setWorkAffect(String workAffect) { this.workAffect = workAffect; }
    public void setWeatherNote(String weatherNote) { this.weatherNote = weatherNote; }
    public void setIssues(String issues) { this.issues = issues; }
    public void setComments(String comments) { this.comments = comments; }
    public void setSupervisorId(int supervisorId) { this.supervisorId = supervisorId; }

    // ==== Additional method for formatted date display ====
    public String getFormattedDate(String pattern) {
        return reportDate.format(java.time.format.DateTimeFormatter.ofPattern(pattern));
    }
}

