// Original Projects.java - Leader Version
package IPPSystem.Models;

import java.util.Date;

public class projects {
    private int assignProjectId;
    private String projectInstanceName;
    private String projectLocation;
    private String projectDescription;
    private Date startDate;
    private Date endDate;
    private double projectCost;
    private String projectStatus;

    // Constructor
    public projects(int assignProjectId, String projectInstanceName, String projectLocation, 
                   String projectDescription, Date startDate, Date endDate, 
                   double projectCost, String projectStatus) {
        this.assignProjectId = assignProjectId;
        this.projectInstanceName = projectInstanceName;
        this.projectLocation = projectLocation;
        this.projectDescription = projectDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectCost = projectCost;
        this.projectStatus = projectStatus;
    }

    // Getters and Setters
    public int getAssignProjectId() {
        return assignProjectId;
    }

    public void setAssignProjectId(int assignProjectId) {
        this.assignProjectId = assignProjectId;
    }

    public String getProjectInstanceName() {
        return projectInstanceName;
    }

    public void setProjectInstanceName(String projectInstanceName) {
        this.projectInstanceName = projectInstanceName;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
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

    public double getProjectCost() {
        return projectCost;
    }

    public void setProjectCost(double projectCost) {
        this.projectCost = projectCost;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    // Progress calculation methods - ORIGINAL VERSION
    public long getTotalDuration() {
        // Get duration from assignprojectdetails table
        try {
            Connection con = IPPSystem.DAO.databaseConnection.getConnection();
            String sql = "SELECT projectDuration FROM assignprojectdetails WHERE assignProjectId = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, assignProjectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return (long) rs.getDouble("projectDuration");
            }
            rs.close();
            pstmt.close();
            con.close();
        } catch (SQLException e) {
            System.err.println("Error getting total duration: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Fallback to date calculation if available
        if (startDate != null && endDate != null) {
            long diffInMillies = endDate.getTime() - startDate.getTime();
            return diffInMillies / (1000 * 60 * 60 * 24);
        }
        
        return 0;
    }

    public long getRemainingDays() {
        // Get remaining days from database dates
        if (startDate != null && endDate != null) {
            long diffInMillies = endDate.getTime() - System.currentTimeMillis();
            return Math.max(0, diffInMillies / (1000 * 60 * 60 * 24));
        }
        return 0;
    }

    public long getElapsedDays() {
        if (startDate == null) return 0;
        long diffInMillies = System.currentTimeMillis() - startDate.getTime();
        return Math.max(0, diffInMillies / (1000 * 60 * 60 * 24));
    }

    public int getTotalTasks() {
        // Get total tasks from database for this project
        try {
            Connection con = IPPSystem.DAO.databaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as totalTasks FROM assignTasks at " +
                        "INNER JOIN assignWorkItems awi ON at.assignWorkItemId = awi.assignWorkItemId " +
                        "WHERE awi.assignProjectId = ? AND at.isCancel = 0";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, assignProjectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalTasks");
            }
            rs.close();
            pstmt.close();
            con.close();
        } catch (SQLException e) {
            System.err.println("Error getting total tasks: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getDoneTasks() {
        // Get completed tasks from database (taskStatus = 4 = finished)
        try {
            Connection con = IPPSystem.DAO.databaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as doneTasks FROM assignTasks at " +
                        "INNER JOIN assignWorkItems awi ON at.assignWorkItemId = awi.assignWorkItemId " +
                        "WHERE awi.assignProjectId = ? AND at.taskStatus = 4 AND at.isCancel = 0";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, assignProjectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("doneTasks");
            }
            rs.close();
            pstmt.close();
            con.close();
        } catch (SQLException e) {
            System.err.println("Error getting done tasks: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public double getPlannedValue() {
        return projectCost; // Total cost is planned value
    }

    public double getEarnedValue() {
        // Calculate earned value based on completed work percentage
        try {
            Connection con = IPPSystem.DAO.databaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as doneTasks FROM assignTasks at " +
                        "INNER JOIN assignWorkItems awi ON at.assignWorkItemId = awi.assignWorkItemId " +
                        "WHERE awi.assignProjectId = ? AND at.taskStatus = 4 AND at.isCancel = 0";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, assignProjectId);
            ResultSet rs = pstmt.executeQuery();
            
            int doneTasks = 0;
            int totalTasks = getTotalTasks();
            
            if (rs.next()) {
                doneTasks = rs.getInt("doneTasks");
            }
            
            rs.close();
            pstmt.close();
            con.close();
            
            if (totalTasks > 0) {
                return projectCost * ((double) doneTasks / totalTasks);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating earned value: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
