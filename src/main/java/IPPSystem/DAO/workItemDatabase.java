package IPPSystem.DAO;

import IPPSystem.Models.workItems;

import java.sql.*;
import java.util.ArrayList;

public class workItemDatabase {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //for the assign work items(all)
    public static ArrayList<workItems> getAllWorkItemByAssignProjectId(int assignProjectId){
        ArrayList<workItems> workItems = new ArrayList<>();

        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                if(rs.getBoolean("isCustomize")) {
                    workItems.add(new workItems(
                            rs.getInt("assignProjectId"),
                            rs.getInt("projectWorkItemId"),
                            rs.getString("projectWorkItemName"),
                            rs.getDouble("customDuration"),
                            rs.getDouble("customCost"),
                            rs.getDouble("customLaborQty"),
                            rs.getDate("customStartDate"),
                            rs.getDate("customEndDate")
                    ));
                }else{
                    workItems.add(new workItems(
                            rs.getInt("projectTypeId"),
                            rs.getInt("workItemId"),
                            rs.getString("workItemName"),
                            rs.getDouble("autoDuration"),
                            rs.getDouble("autoCost"),
                            rs.getDouble("autoLaborQty"),
                            rs.getDate("autoStartDate"),
                            rs.getDate("autoEndDate")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return workItems;
    }

    //for the workItems detail (all)
    public static ArrayList<workItems> getAllWorkItemDetailsByAssignProjectId(String projectType){
        ArrayList<workItems> workItems = new ArrayList<>();
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setString(1,projectType);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                workItems.add(new workItems(
                        rs.getInt("projectTypeId"),
                        rs.getInt("projectWorkItemId"),
                        rs.getString("projectWorkItemName"),
                        rs.getDouble("minDuration"),
                        rs.getDouble("maxDuration"),
                        rs.getDouble("minCost"),
                        rs.getDouble("maxCost"),
                        rs.getDouble("minLaborQty"),
                        rs.getDouble("maxLaborQty")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return workItems;
    }

    //to make the changes in the work item

    //for the cost
    public static boolean changeTheCostOfWorkItem(int assignProjectId, int projectWorkItemId, double changeCost){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            cstmt.setInt(2, projectWorkItemId);
            cstmt.setDouble(3,changeCost);
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //for the labors qty
    public static boolean changeTheLaborQtyOfWorkItem(int assignProjectId, int projectWorkItemId, double changeLaborQty){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            cstmt.setInt(2, projectWorkItemId);
            cstmt.setDouble(3,changeLaborQty);
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //for the duration
    public static boolean changeTheDurationOfWorkItem(int assignProjectId,int workItemId,double changeDuration){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            cstmt.setInt(2,workItemId);
            cstmt.setDouble(3,changeDuration);
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    //to assign the workItems
    public static boolean assignWorkItems(workItems assign,boolean isCustomize){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assign.getProjectTypeId());
            cstmt.setInt(2,assign.getWorkItemId());
            cstmt.setDouble(3,assign.getProjectDuration());
            cstmt.setDouble(4,assign.getProjectCost());
            cstmt.setDouble(5,assign.getProjectLaborQty());
            cstmt.setDate(6,assign.getStartDate());
            cstmt.setDate(7,assign.getEndDate());
            cstmt.setBoolean(8,isCustomize);
            ResultSet rs = cstmt.executeQuery();
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
