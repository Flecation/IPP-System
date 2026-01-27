package IPPSystem.DAO;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.projectStatus;
import IPPSystem.Models.skills;
import IPPSystem.Models.workItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;

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
    public static ObservableList<workItems> getAllWorkItemByAssignProjectId(int assignProjectId){
        ObservableList<workItems> workItems = FXCollections.observableArrayList();

        try {
            CallableStatement cstmt = con.prepareCall("{CALL getAllWorkItemByAssignProjectId(?)}");
            cstmt.setInt(1,assignProjectId);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                    workItems.add(new workItems(
                            rs.getInt("assignWorkItemId"),
                            rs.getString("workItemName"),
                            rs.getString("workItemStatus"),
                            rs.getString("assignStatus"),
                            rs.getDouble("cost"),
                            rs.getDouble("laborQty"),
                            rs.getDouble("duration"),
                            rs.getDate("startDate"),
                            rs.getDate("endDate")
                    ));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return workItems;
    }

    //for the workItems detail (all)
    public static ObservableList<workItems> getAllWorkItemDetails(int projectTypeId, int buildingId, int levelId){
        ObservableList<workItems> workItems = FXCollections.observableArrayList();
        try {
            CallableStatement cstmt = con.prepareCall("{CALL getAllWorkItemDetails(?,?,?)}");
            cstmt.setInt(1,projectTypeId);
            cstmt.setInt(2,buildingId);
            cstmt.setInt(3,levelId);
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

    //to assign the workItems
    public static boolean assignWorkItems(workItems assign, projectStatus projectStatus, assignStatus assignStatus){
        try {
            CallableStatement cstmt = con.prepareCall("{CALL assignWorkItems(?,?,?,?,?,?,?,?,?)}");
            cstmt.setInt(1,assign.getAssignProjectId());
            cstmt.setInt(2,assign.getWorkItemId());
            cstmt.setString(3,projectStatus.toString());
            cstmt.setString(4,assignStatus.toString());
            cstmt.setDouble(5,assign.getProjectCost());
            cstmt.setDouble(6,assign.getProjectLaborQty());
            cstmt.setDouble(7,assign.getProjectDuration());
            cstmt.setDate(8,assign.getStartDate());
            cstmt.setDate(9,assign.getEndDate());
            ResultSet rs = cstmt.executeQuery();
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteWorkItem(int assignWorkItemId){
        String sql = "UPDATE assignWorkItems " +
                "SET workItemStatus = 5" +
                "WHERE assignWorkItemId = ?;";
        try {
            PreparedStatement pstmt = con.prepareCall(sql);
            pstmt.setInt(1,assignWorkItemId);
            return  pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean deleteSkillFromWorkItem(int assignWorkItemId,int skillId){
        String sql = "UPDATE assignWorkItemSkills " +
                "SET isCancel = true" +
                "WHERE assignWorkItemId = ?" +
                "AND skillId = ?;";
        try {
            PreparedStatement pstmt = con.prepareCall(sql);
            pstmt.setInt(1,assignWorkItemId);
            pstmt.setInt(2,skillId);
            return  pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObservableList<skills> getAllSkillDetailsByAssignWorkItem(int assignWorkItemId){
        ObservableList<skills> skill = FXCollections.observableArrayList();
        try(CallableStatement cs = con.prepareCall("{CALL getAllSkillDetailsByAssignWorkItem(?)}")){
            cs.setInt(1,assignWorkItemId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()){
                skill.add(new skills(
                        rs.getInt("assignWorkItemSkillId"),
                        rs.getString("skillName"),
                        rs.getString("assignStatus"),
                        rs.getDouble("laborQty"),
                        rs.getDouble("dailyWagePerLabor"),
                        rs.getBoolean("isCancel")
                ));
            }
            return skill;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean addSkillToWorkItem(skills assign, assignStatus assignStatus){
        try (CallableStatement cs = con.prepareCall("")){
            cs.setInt(1,assign.getAssignWorkItemId());
            cs.setInt(2,assign.getSkillId());
            cs.setString(3,assignStatus.toString());
            cs.setDouble(4,assign.getProjectLaborQty());
            cs.setDouble(5,assign.getDailyWagePerLabor());
            return cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
