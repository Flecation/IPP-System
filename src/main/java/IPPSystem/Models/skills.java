package IPPSystem.Models;

public class skills extends workItems{
    protected int skillId,assignWorkItemSkillId;
    protected String skillName;
    protected double minRequireLabors,maxRequireLabors, minDailyWage,maxDailyWage,dailyWagePerLabor;

    public skills (){}

    //for the child class
    public skills(int skillId) {
        this.skillId = skillId;
    }

    public skills(String skillName){
        this.skillName = skillName;
    }


    public skills(int skillId,String skillName){
        this.skillId = skillId;
        this.skillName = skillName;
    }

    public skills(int assignWorkItemId, int skillId, Double projectLaborQty, double dailyWagePerLabor) {
        super(assignWorkItemId, projectLaborQty);
        this.skillId = skillId;
        this.dailyWagePerLabor = dailyWagePerLabor;
    }

    public skills(int skillId, String skillName, double minRequireLabors, double maxRequireLabors, double minDailyWage, double maxDailyWage) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.minRequireLabors = minRequireLabors;
        this.maxRequireLabors = maxRequireLabors;
        this.minDailyWage = minDailyWage;
        this.maxDailyWage = maxDailyWage;
    }

    public skills(int assignWorkItemSkillId, String skillName, String assignStatus, Double projectLaborQty,Double dailyWagePerLabor, boolean isCancel){
        super(assignStatus,projectLaborQty,isCancel);
        this.assignWorkItemSkillId = assignWorkItemSkillId;
        this.skillName = skillName;
    }

    //for the child class
    public skills(int assignProjectId, int workItemId, int skillId) {
        super(assignProjectId, workItemId);
        this.skillId = skillId;
    }

    //for the skill details
    public skills(int assignProjectId, int workItemId, int skillId, String skillName, double minRequireLabors, double maxRequireLabors, double minDailyWage, double maxDailyWage) {
        super(assignProjectId, workItemId);
        this.skillId = skillId;
        this.skillName = skillName;
        this.minRequireLabors = minRequireLabors;
        this.maxRequireLabors = maxRequireLabors;
        this.minDailyWage = minDailyWage;
        this.maxDailyWage = maxDailyWage;
    }

    public int getAssignWorkItemSkillId() {
        return assignWorkItemSkillId;
    }

    public void setAssignWorkItemSkillId(int assignWorkItemSkillId) {
        this.assignWorkItemSkillId = assignWorkItemSkillId;
    }

    public double getDailyWagePerLabor() {
        return dailyWagePerLabor;
    }

    public void setDailyWagePerLabor(double dailyWagePerLabor) {
        this.dailyWagePerLabor = dailyWagePerLabor;
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

    public double getMinDailyWage() {
        return minDailyWage;
    }

    public void setMinDailyWage(double minDailyWage) {
        this.minDailyWage = minDailyWage;
    }

    public double getMaxDailyWage() {
        return maxDailyWage;
    }

    public void setMaxDailyWage(double maxDailyWage) {
        this.maxDailyWage = maxDailyWage;
    }
}
