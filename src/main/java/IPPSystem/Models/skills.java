package IPPSystem.Models;

public class skills extends workItems{
    protected int skillId ;
    protected String skillName;
    protected double minRequireLabors,maxRequireLabors,basicDailyWage,maxDailyWage;

    public skills (){}

    //for the child class
    public skills(int skillId) {
        this.skillName = skillName;
    }


    //for the child class
    public skills(int assignProjectId, int workItemId, int skillId) {
        super(assignProjectId, workItemId);
        this.skillId = skillId;
    }

    //for the skill details
    public skills(int assignProjectId, int workItemId, int skillId, String skillName, double minRequireLabors, double maxRequireLabors, double basicDailyWage, double maxDailyWage) {
        super(assignProjectId, workItemId);
        this.skillId = skillId;
        this.skillName = skillName;
        this.minRequireLabors = minRequireLabors;
        this.maxRequireLabors = maxRequireLabors;
        this.basicDailyWage = basicDailyWage;
        this.maxDailyWage = maxDailyWage;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public double getMinRequireLabors() {
        return minRequireLabors;
    }

    public void setMinRequireLabors(double minRequireLabors) {
        this.minRequireLabors = minRequireLabors;
    }

    public double getMaxRequireLabors() {
        return maxRequireLabors;
    }

    public void setMaxRequireLabors(double maxRequireLabors) {
        this.maxRequireLabors = maxRequireLabors;
    }

    public double getBasicDailyWage() {
        return basicDailyWage;
    }

    public void setBasicDailyWage(double basicDailyWage) {
        this.basicDailyWage = basicDailyWage;
    }

    public double getMaxDailyWage() {
        return maxDailyWage;
    }

    public void setMaxDailyWage(double maxDailyWage) {
        this.maxDailyWage = maxDailyWage;
    }
}
