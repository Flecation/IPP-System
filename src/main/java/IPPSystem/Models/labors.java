package IPPSystem.Models;

import java.util.Date;

public class labors extends skills{
    protected int laborId;
    protected String laborName;
    protected String laborNRC;
    protected Date laborStartDate,laborEndDate;

    public labors(){}

    //labors details
    public labors( int laborId,String skillName, String laborName,String laborNRC,Date laborStartDate,Date laborEndDate) {
        super(skillName);
        this.laborId = laborId;
        this.laborName = laborName;
        this.laborNRC = laborNRC;
        this.laborStartDate = laborStartDate;
        this.laborEndDate = laborEndDate;
    }

    //labors assign in work items
    public labors(int assignProjectId, int workItemId, int skillId, int laborId, String laborName) {
        super(assignProjectId, workItemId, skillId);
        this.laborId = laborId;
        this.laborName = laborName;
    }

    public String getLaborNRC() {
        return laborNRC;
    }

    public void setLaborNRC(String laborNRC) {
        this.laborNRC = laborNRC;
    }

    public int getLaborId() {
        return laborId;
    }

    public void setLaborId(int laborId) {
        this.laborId = laborId;
    }

    public String getLaborName() {
        return laborName;
    }

    public void setLaborName(String laborName) {
        this.laborName = laborName;
    }
}
