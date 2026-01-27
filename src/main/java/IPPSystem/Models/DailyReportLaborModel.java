package IPPSystem.Models;

public class DailyReportLaborModel {
    private int dailyReportLaborId;
    private int dailyReportId;
    private int laborId;
    private double workHours;
    private double dailyWage;
    private String remark;

    // Getters and setters
    public int getDailyReportLaborId() {
        return dailyReportLaborId;
    }

    public void setDailyReportLaborId(int dailyReportLaborId) {
        this.dailyReportLaborId = dailyReportLaborId;
    }

    public int getDailyReportId() {
        return dailyReportId;
    }

    public void setDailyReportId(int dailyReportId) {
        this.dailyReportId = dailyReportId;
    }

    public int getLaborId() {
        return laborId;
    }

    public void setLaborId(int laborId) {
        this.laborId = laborId;
    }

    public double getWorkHours() {
        return workHours;
    }

    public void setWorkHours(double workHours) {
        this.workHours = workHours;
    }

    public double getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(double dailyWage) {
        this.dailyWage = dailyWage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}