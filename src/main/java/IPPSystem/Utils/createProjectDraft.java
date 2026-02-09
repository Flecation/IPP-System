package IPPSystem.Utils;

import IPPSystem.Models.skills;
import IPPSystem.Models.tasks;
import IPPSystem.Models.workItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    public Integer projectTypeId;
    public Integer buildingId;
    public Integer levelId;
    public Double area;
    public Double units;
    public Double stories;
    public Double height;

    // ===== Preview cache (optional) =====
    // Carry the auto-generated preview lists from CreateProject -> CreateViewProject
    // without relying on DB calls at the moment of switching controllers.
    public final ObservableList<workItems> previewWorkItems = FXCollections.observableArrayList();
    public final ObservableList<skills> previewSkills = FXCollections.observableArrayList();
    public final ObservableList<tasks> previewTasks  = FXCollections.observableArrayList();

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
        area = null;
        units = null;
        stories = null;
        height = null;

        previewWorkItems.clear();
        previewSkills.clear();
        previewTasks.clear();
    }
}
