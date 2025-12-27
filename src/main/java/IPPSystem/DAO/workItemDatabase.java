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
                            rs.getInt("projectTypeId"),
                            rs.getInt("workItemId"),
                            rs.getString("workItemName"),
                            rs.getDouble("customDuration"),
                            rs.getDouble("customCost"),
                            rs.getDouble("customLaborQty")
                    ));
                }else{
                    workItems.add(new workItems(
                            rs.getInt("projectTypeId"),
                            rs.getInt("workItemId"),
                            rs.getString("workItemName"),
                            rs.getDouble("autoDuration"),
                            rs.getDouble("autoCost"),
                            rs.getDouble("autoLaborQty")
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
                        rs.getInt("workItemId"),
                        rs.getString("workItemName"),
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
    public static boolean changeTheCostOfWorkItem(int assignProjectId,int workItemId,double changeCost){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            cstmt.setInt(2,workItemId);
            cstmt.setDouble(3,changeCost);
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //for the labors qty
    public static boolean changeTheLaborQtyOfWorkItem(int assignProjectId,int workItemId,double changeLaborQty){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            cstmt.setInt(2,workItemId);
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
            cstmt.setDouble(3,assign.getDuration());
            cstmt.setDouble(4,assign.getCost());
            cstmt.setDouble(5,assign.getLaborQty());
            cstmt.setBoolean(6,isCustomize);
            ResultSet rs = cstmt.executeQuery();
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
