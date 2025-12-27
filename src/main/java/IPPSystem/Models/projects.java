package IPPSystem.Models;

import java.sql.Date;

public class projects {
    protected String projectType,projectInstanceName,BuildingName,level, supervisor,projectStatus,projectLocation;
    protected int projectTypeId,totalStories,totalUnits;
    protected double projectArea,projectHeight,totalCost, duration,minDuration,maxDuration,minCost,maxCost,minLaborQty,maxLaborQty;
    protected Date startDate;


    public projects(){};

    // for the child class
    public projects(int projectTypeId){
        this.projectTypeId = projectTypeId;
    }

    //for the child class
    public projects(int projectTypeId, double minDuration, double maxDuration) {
        this.projectTypeId = projectTypeId;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    // for the child class
    public projects(int projectTypeId, String projectInstanceName, String projectType, String buildingName, String level) {
        this.projectTypeId = projectTypeId;
        this.projectInstanceName = projectInstanceName;
        this.projectType = projectType;
        BuildingName = buildingName;
        this.level = level;
    }

    //for the child class
    public projects(int projectTypeId, double minDuration, double maxDuration, double minCost, double maxCost, double minLaborQty, double maxLaborQty) {
        this.projectTypeId = projectTypeId;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.minLaborQty = minLaborQty;
        this.maxLaborQty = maxLaborQty;
    }

    //for the assign project ( input details)
    public projects(int projectTypeId, String projectInstanceName, String projectType, String buildingName, String level, int totalStories, int totalUnits, double projectArea, double projectHeight, String projectLocation, Date startDate, Double duration, double totalCost, String supervisor, String projectStatus) {
        this.projectTypeId = projectTypeId;
        this.projectInstanceName = projectInstanceName;
        this.projectType = projectType;
        this.BuildingName = buildingName;
        this.level = level;
        this.totalStories = totalStories;
        this.totalUnits = totalUnits;
        this.projectArea = projectArea;
        this.projectHeight = projectHeight;
        this.projectLocation = projectLocation;
        this.startDate = startDate;
        this.duration = duration;
        this.totalCost = totalCost;
        this.supervisor = supervisor;
        this.projectStatus = projectStatus;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public double getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(double minDuration) {
        this.minDuration = minDuration;
    }

    public double getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(double maxDuration) {
        this.maxDuration = maxDuration;
    }

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public double getMinLaborQty() {
        return minLaborQty;
    }

    public void setMinLaborQty(double minLaborQty) {
        this.minLaborQty = minLaborQty;
    }

    public double getMaxLaborQty() {
        return maxLaborQty;
    }

    public void setMaxLaborQty(double maxLaborQty) {
        this.maxLaborQty = maxLaborQty;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectInstanceName() {
        return projectInstanceName;
    }

    public void setProjectInstanceName(String projectInstanceName) {
        this.projectInstanceName = projectInstanceName;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public int getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(int projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public int getTotalStories() {
        return totalStories;
    }

    public void setTotalStories(int totalStories) {
        this.totalStories = totalStories;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public double getProjectArea() {
        return projectArea;
    }

    public void setProjectArea(double projectArea) {
        this.projectArea = projectArea;
    }

    public double getProjectHeight() {
        return projectHeight;
    }

    public void setProjectHeight(double projectHeight) {
        this.projectHeight = projectHeight;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
