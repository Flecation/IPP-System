package IPPSystem.Models;

import java.util.ArrayList;
import java.util.Date;

public class assignProject {
    private String projectType,projectInstanceName,BuildingName,level,manager,projectStatus,unitDuration;
    private int assignProjectId,totalStories,totalUnits;
    private ArrayList<String> workItems,tasks;
    private double projectArea,projectHeight,totalCost;
    private Date startDate;

    public assignProject(){};

    public assignProject(String projectInstanceName, String projectType, String buildingName, String level, int totalStories, int totalUnits, ArrayList<String> workItems, ArrayList<String> tasks, double projectArea, double projectHeight, String manager, Date startDate, double totalCost, String unitDuration, String projectStatus) {
        this.projectInstanceName = projectInstanceName;
        this.projectType = projectType;
        BuildingName = buildingName;
        this.level = level;
        this.totalStories = totalStories;
        this.totalUnits = totalUnits;
        this.workItems = workItems;
        this.tasks = tasks;
        this.projectArea = projectArea;
        this.projectHeight = projectHeight;
        this.manager = manager;
        this.startDate = startDate;
        this.totalCost = totalCost;
        this.unitDuration = unitDuration;
        this.projectStatus = projectStatus;
    }

    public assignProject(String projectInstanceName, String projectType, String buildingName, int totalStories, int totalUnits, ArrayList<String> workItems, ArrayList<String> tasks, double projectArea, double projectHeight, String manager, Date startDate, double totalCost, String unitDuration, String projectStatus) {
        this.projectInstanceName = projectInstanceName;
        this.projectType = projectType;
        BuildingName = buildingName;
        this.totalStories = totalStories;
        this.totalUnits = totalUnits;
        this.workItems = workItems;
        this.tasks = tasks;
        this.projectArea = projectArea;
        this.projectHeight = projectHeight;
        this.manager = manager;
        this.startDate = startDate;
        this.totalCost = totalCost;
        this.unitDuration = unitDuration;
        this.projectStatus = projectStatus;
    }

    public assignProject(String projectInstanceName, String projectType, int totalStories, int totalUnits, ArrayList<String> workItems, ArrayList<String> tasks, double projectArea, double projectHeight, String manager, Date startDate, double totalCost, String unitDuration, String projectStatus) {
        this.projectInstanceName = projectInstanceName;
        this.projectType = projectType;
        this.level = level;
        this.totalStories = totalStories;
        this.totalUnits = totalUnits;
        this.workItems = workItems;
        this.tasks = tasks;
        this.projectArea = projectArea;
        this.projectHeight = projectHeight;
        this.manager = manager;
        this.startDate = startDate;
        this.totalCost = totalCost;
        this.unitDuration = unitDuration;
        this.projectStatus = projectStatus;
    }

    public assignProject(String projectType, String projectInstanceName, String buildingName, String level, String manager, String projectStatus, String unitDuration, int assignProjectId, int totalStories, int totalUnits, ArrayList<String> workItems, ArrayList<String> tasks, double projectArea, double projectHeight, double totalCost, Date startDate) {
        this.projectType = projectType;
        this.projectInstanceName = projectInstanceName;
        BuildingName = buildingName;
        this.level = level;
        this.manager = manager;
        this.projectStatus = projectStatus;
        this.unitDuration = unitDuration;
        this.assignProjectId = assignProjectId;
        this.totalStories = totalStories;
        this.totalUnits = totalUnits;
        this.workItems = workItems;
        this.tasks = tasks;
        this.projectArea = projectArea;
        this.projectHeight = projectHeight;
        this.totalCost = totalCost;
        this.startDate = startDate;
    }
}
