package IPPSystem.DAO;

import IPPSystem.Models.tasks;
import IPPSystem.Models.workItems;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class taskDatabase {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //get all task by the assign project of work item
    public static ArrayList<tasks> getAllTasksByAssignProjectOfWorkItem(int assignProjectId, int workItemId){
        ArrayList<tasks> ls =  new ArrayList<>();

        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            cstmt.setInt(2,workItemId);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                if(rs.getBoolean("isCustomize")) {
                    ls.add(new tasks(
                            rs.getInt("assignProjectId"),
                            rs.getInt("workItemId"),
                            rs.getInt("taskId"),
                            rs.getString("taskName"),
                            rs.getDouble("customDuration"),
                            rs.getDouble("customCost"),
                            rs.getDouble("customLaborQty")
                    ));
                }else{
                    ls.add(new tasks(
                            rs.getInt("assignProjectId"),
                            rs.getInt("workItemId"),
                            rs.getInt("taskId"),
                            rs.getString("taskName"),
                            rs.getDouble("autoDuration"),
                            rs.getDouble("autoCost"),
                            rs.getDouble("autoLaborQty")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;

    }

    //get all task details by project of work item
    public static ArrayList<tasks> getAllTasksDetailsByProjectOfWorkItem(String projectType,String workItemName){
        ArrayList<tasks> ls =  new ArrayList<>();

        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setString(1,projectType);
            cstmt.setString(2,workItemName);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                ls.add(new tasks(
                        rs.getInt("assignProjectId"),
                        rs.getInt("workItemId"),
                        rs.getInt("taskId"),
                        rs.getString("taskName"),
                        rs.getDouble("minDuration"),
                        rs.getDouble("maxDuration")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    //to make the changes in the tasks

    //for the duration
    public static boolean changeTheDurationOfTasks(int assignProjectId,int workItemId,int taskId,double changeDuration){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assignProjectId);
            cstmt.setInt(2,workItemId);
            cstmt.setInt(3,taskId);
            cstmt.setDouble(4,changeDuration);
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //assign the task
    public static boolean assignTasks(tasks assign, boolean isCustomize){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assign.getProjectTypeId());
            cstmt.setInt(2,assign.getWorkItemId());
            cstmt.setInt(3,assign.getTaskId());
            cstmt.setDouble(4,assign.getDuration());
            cstmt.setBoolean(5,isCustomize);
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
