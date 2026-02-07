package IPPSystem.Models;

public class DailyReportLaborView {

    private String laborName;
    private String skillName;
    private Double dailyWage;
    private Double workHours;
    private String remark;

    public DailyReportLaborView(
            String laborName,
            String skillName,
            Double dailyWage,
            Double workHours,
            String remark
    ) {
        this.laborName = laborName;
        this.skillName = skillName;
        this.dailyWage = dailyWage;
        this.workHours = workHours;
        this.remark = remark;
    }

    public String getLaborName() {
        return laborName;
    }

    public String getSkillName() {
        return skillName;
    }

    public Double getDailyWage() {
        return dailyWage;
    }

    public Double getWorkHours() {
        return workHours;
    }

    public String getRemark() {
        return remark;
    }
}
