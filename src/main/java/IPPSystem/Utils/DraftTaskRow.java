package IPPSystem.Utils;

/**
 * A lightweight row model for the Create Project preview.
 * Stored inside {@link createProjectDraft} so CreateViewProject can show
 * tasks even if navigation causes controller fields to become null.
 */
public class DraftTaskRow {
    public int workItemId;
    public int taskId;
    public String taskName;
    public double plannedQty;
    public String unit;
    public double durationDays;
    public String startDate; // yyyy-MM-dd
    public String endDate;   // yyyy-MM-dd

    public DraftTaskRow() {}

    public DraftTaskRow(int workItemId, int taskId, String taskName,
                        double plannedQty, String unit, double durationDays,
                        String startDate, String endDate) {
        this.workItemId = workItemId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.plannedQty = plannedQty;
        this.unit = unit;
        this.durationDays = durationDays;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
