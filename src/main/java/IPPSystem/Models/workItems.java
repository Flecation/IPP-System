package IPPSystem.Models;

public class workItems extends projects {

    protected int workItemId;
    protected String workItemName;
    protected double duration,cost,laborQty;

    public workItems(){}

    //for the child class
    public workItems(int assignProjectId, int workItemId, double duration, double cost, double laborQty) {
        super(assignProjectId);
        this.workItemId = workItemId;
        this.duration = duration;
        this.cost = cost;
        this.laborQty = laborQty;
    }

    //for the child class


    public workItems(int projectTypeId, int workItemId) {
        super(projectTypeId);
        this.workItemId = workItemId;
    }

    //for the child class
    public workItems(int assignProjectId, int workItemId, double minDuration, double maxDuration, double minCost, double maxCost, double minLaborQty, double maxLaborQty) {
        super(assignProjectId,minDuration, maxDuration, minCost, maxCost, minLaborQty, maxLaborQty);
        this.workItemId = workItemId;
    }

    //for the child class
    public workItems(int assignProjectId, int workItemId, Double minDuration, Double maxDuration) {
        super(assignProjectId,minDuration,maxDuration);
        this.workItemId = workItemId;
    }

    //for the workItem details
    public workItems(int projectTypeId,int workItemId, String workItemName, double minDuration, double maxDuration, double minCost, double maxCost, double minLaborQty, double maxLaborQty) {
        super(projectTypeId,minDuration, maxDuration, minCost, maxCost, minLaborQty, maxLaborQty);
        this.workItemId = workItemId;
        this.workItemName = workItemName;
    }

    //for the assign workItems ( auto assign or custom assign)
    public workItems(int assignProjectId, int workItemId, String workItemName, double duration, double cost, double laborQty) {
        super(assignProjectId);
        this.workItemId = workItemId;
        this.workItemName = workItemName;
        this.duration = duration;
        this.cost = cost;
        this.laborQty = laborQty;
    }

    public int getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(int workItemId) {
        this.workItemId = workItemId;
    }

    public String getWorkItemName() {
        return workItemName;
    }

    public void setWorkItemName(String workItemName) {
        this.workItemName = workItemName;
    }

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getLaborQty() {
        return laborQty;
    }

    public void setLaborQty(double laborQty) {
        this.laborQty = laborQty;
    }
}
