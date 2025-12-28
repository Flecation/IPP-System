package IPPSystem.Models;

import java.sql.Date;

public class projects {
    private int assignProjectId,projectTypeId, projectBuildingId, projectLevelId,userId;
    private String projectTypeName;
    private String projectInstanceName;
    private String projectBuildingName;
    private String projectLevelName;
    private String projectLocation;
    private String projectStatus;
    private String userName;
    private double projectArea;
    private double totalStories;
    private double totalUnits;
    private double projectDuration;
    private double projectCost;
    private double projectLaborQty;
    private double projectHeight;
    private double minOverHeadCost;
    private double maxOverHeadCost;
    private double projectOverHeadCost;
    private double minDuration;
    private double maxDuration;
    private double minCost;
    private double maxCost;
    private double minLaborQty;
    private double maxLaborQty;
    private Date startDate,endDate;

    public projects() {
    }

    //for the child class
    public projects(int assignProjectId) {
        this.assignProjectId = assignProjectId;
    }

    public projects(int assignProjectId, Date startDate, Date endDate,double projectDuration) {
        this.assignProjectId = assignProjectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectDuration = projectDuration;
    }

    public projects(int projectTypeId, double minDuration, double maxDuration, double minCost, double maxCost, double minLaborQty, double maxLaborQty) {
        this.projectTypeId = projectTypeId;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.minLaborQty = minLaborQty;
        this.maxLaborQty = maxLaborQty;
    }

    public projects(int projectTypeId, double minDuration, double maxDuration) {
        this.projectTypeId = projectTypeId;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    public projects(int assignProjectId, double projectDuration, double projectCost, double projectLaborQty, Date startDate, Date endDate) {
        this.assignProjectId = assignProjectId;
        this.projectDuration = projectDuration;
        this.projectCost = projectCost;
        this.projectLaborQty = projectLaborQty;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public projects(String projectInstanceName, double projectDuration, double projectCost, double projectLaborQty, Date startDate, Date endDate) {
        this.projectInstanceName = projectInstanceName;
        this.projectDuration = projectDuration;
        this.projectCost = projectCost;
        this.projectLaborQty = projectLaborQty;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public projects(String projectInstanceName, double projectDuration) {
        this.projectInstanceName = projectInstanceName;
        this.projectDuration = projectDuration;
    }

    //for the project details
    public projects(int projectTypeId, String projectTypeName, int projectLevelId, String projectLevelName, int projectBuildingId, String projectBuildingName, double minOverHeadCost, double maxOverHeadCost) {
        this.projectTypeId = projectTypeId;
        this.projectTypeName = projectTypeName;
        this.projectLevelId = projectLevelId;
        this.projectLevelName = projectLevelName;
        this.projectBuildingId = projectBuildingId;
        this.projectBuildingName = projectBuildingName;
        this.minOverHeadCost = minOverHeadCost;
        this.maxOverHeadCost = maxOverHeadCost;
    }


    //for the assign project (view)
    public projects(int assignProjectId, String projectInstanceName, String projectTypeName, String projectBuildingName, String projectLevelName, String userName, double projectArea, double projectHeight, double totalStories, double totalUnits, double projectCost, double projectOverHeadCost, double projectLaborQty, double projectDuration, Date startDate, Date endDate, String projectLocation, String projectStatus) {
        this.assignProjectId = assignProjectId;
        this.projectInstanceName = projectInstanceName;
        this.projectTypeName = projectTypeName;
        this.projectBuildingName = projectBuildingName;
        this.projectLevelName = projectLevelName;
        this.userName = userName;
        this.projectArea = projectArea;
        this.projectHeight = projectHeight;
        this.totalStories = totalStories;
        this.totalUnits = totalUnits;
        this.projectCost = projectCost;
        this.projectOverHeadCost = projectOverHeadCost;
        this.projectLaborQty = projectLaborQty;
        this.projectDuration = projectDuration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectLocation = projectLocation;
        this.projectStatus = projectStatus;
    }

    //for the assign project insert
    public projects(String projectInstanceName, String projectTypeName, String projectBuildingName, String projectLevelName, String userName, double projectArea, double projectHeight, double totalStories, double totalUnits, double projectDuration, double projectCost, double projectLaborQty, double projectOverHeadCost, Date startDate, Date endDate, String projectLocation, String projectStatus) {
        this.projectInstanceName = projectInstanceName;
        this.projectTypeName = projectTypeName;
        this.projectBuildingName = projectBuildingName;
        this.projectLevelName = projectLevelName;
        this.userName = userName;
        this.projectArea = projectArea;
        this.projectHeight = projectHeight;
        this.totalStories = totalStories;
        this.totalUnits = totalUnits;
        this.projectDuration = projectDuration;
        this.projectCost = projectCost;
        this.projectLaborQty = projectLaborQty;
        this.projectOverHeadCost = projectOverHeadCost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectLocation = projectLocation;
        this.projectStatus = projectStatus;
    }

    public double getMaxLaborQty() {
        return maxLaborQty;
    }

    public void setMaxLaborQty(double maxLaborQty) {
        this.maxLaborQty = maxLaborQty;
    }

    public double getMinLaborQty() {
        return minLaborQty;
    }

    public void setMinLaborQty(double minLaborQty) {
        this.minLaborQty = minLaborQty;
    }

    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public double getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(double maxDuration) {
        this.maxDuration = maxDuration;
    }

    public double getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(double minDuration) {
        this.minDuration = minDuration;
    }

    public double getProjectOverHeadCost() {
        return projectOverHeadCost;
    }

    public void setProjectOverHeadCost(double projectOverHeadCost) {
        this.projectOverHeadCost = projectOverHeadCost;
    }
    public double getProjectHeight() {
        return projectHeight;
    }

    public void setProjectHeight(double projectHeight) {
        this.projectHeight = projectHeight;
    }

    public double getMinOverHeadCost() {
        return minOverHeadCost;
    }

    public void setMinOverHeadCost(double minOverHeadCost) {
        this.minOverHeadCost = minOverHeadCost;
    }

    public double getMaxOverHeadCost() {
        return maxOverHeadCost;
    }

    public void setMaxOverHeadCost(double maxOverHeadCost) {
        this.maxOverHeadCost = maxOverHeadCost;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAssignProjectId() {
        return assignProjectId;
    }

    public void setAssignProjectId(int assignProjectId) {
        this.assignProjectId = assignProjectId;
    }

    public int getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(int projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public int getProjectBuildingId() {
        return projectBuildingId;
    }

    public void setProjectBuildingId(int projectBuildingId) {
        this.projectBuildingId = projectBuildingId;
    }

    public int getProjectLevelId() {
        return projectLevelId;
    }

    public void setProjectLevelId(int projectLevelId) {
        this.projectLevelId = projectLevelId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProjectTypeName() {
        return projectTypeName;
    }

    public void setProjectTypeName(String projectTypeName) {
        this.projectTypeName = projectTypeName;
    }

    public String getProjectInstanceName() {
        return projectInstanceName;
    }

    public void setProjectInstanceName(String projectInstanceName) {
        this.projectInstanceName = projectInstanceName;
    }

    public String getProjectBuildingName() {
        return projectBuildingName;
    }

    public void setProjectBuildingName(String projectBuildingName) {
        this.projectBuildingName = projectBuildingName;
    }

    public String getProjectLevelName() {
        return projectLevelName;
    }

    public void setProjectLevelName(String projectLevelName) {
        this.projectLevelName = projectLevelName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public double getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(double projectArea) {
        this.projectArea = projectArea;
    }

    public double getTotalStories() {
        return totalStories;
    }

    public void setTotalStories(double totalStories) {
        this.totalStories = totalStories;
    }

    public double getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(double totalUnits) {
        this.totalUnits = totalUnits;
    }

    public double getProjectDuration() {
        return projectDuration;
    }

    public void setProjectDuration(double projectDuration) {
        this.projectDuration = projectDuration;
    }

    public double getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(double projectCost) {
        this.projectCost = projectCost;
    }

    public double getProjectLaborQty() {
        return projectLaborQty;
    }

    public void setProjectLaborQty(double projectLaborQty) {
        this.projectLaborQty = projectLaborQty;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
