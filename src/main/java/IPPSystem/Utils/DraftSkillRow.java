package IPPSystem.Utils;

/**
 * A lightweight row model for the Create Project preview.
 * Stored inside {@link createProjectDraft} so CreateViewProject can show
 * skills even if navigation causes controller fields to become null.
 */
public class DraftSkillRow {
    public int workItemId;
    public int skillId;
    public String skillName;
    public double laborQty;
    public double dailyWage;

    public DraftSkillRow() {}

    public DraftSkillRow(int workItemId, int skillId, String skillName, double laborQty, double dailyWage) {
        this.workItemId = workItemId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.laborQty = laborQty;
        this.dailyWage = dailyWage;
    }
}
