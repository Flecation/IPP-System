package IPPSystem.Utils;

import java.time.LocalDate;

public class createProjectDraft {
    private static final createProjectDraft instance = new createProjectDraft();
    public static createProjectDraft getInstance() { return instance; }

    public String instanceName;
    public String supervisorName;   // site engineer / supervisor
    public String projectTypeName;
    public String buildingName;
    public String levelName;
    public String address;
    public Double contractValue;

    public LocalDate startDate;
    public LocalDate endDate;
    public Double duration;

    public void clear() {
        instanceName = null;
        supervisorName = null;
        projectTypeName = null;
        buildingName = null;
        levelName = null;
        address = null;
        contractValue = null;
        startDate = null;
        endDate = null;
        duration = null;
    }
}
