package IPPSystem.Models;

public class labors extends skills{
    protected int laborId;
    protected String laborName;

    public labors(){}

    //labors details
    public labors(int skillId, int laborId, String laborName) {
        super(skillId);
        this.laborId = laborId;
        this.laborName = laborName;
    }

    //labors assign in work items
    public labors(int assignProjectId, int workItemId, int skillId, int laborId, String laborName) {
        super(assignProjectId, workItemId, skillId);
        this.laborId = laborId;
        this.laborName = laborName;
    }
}
