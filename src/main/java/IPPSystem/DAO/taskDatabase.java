package IPPSystem.DAO;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.projectStatus;
import IPPSystem.Models.tasks;
import IPPSystem.Utils.dateFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

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
    public static ObservableList<tasks> getAllTasksByAssignWorkItem(int assignWorkItemId){
        ObservableList<tasks> ls = FXCollections.observableArrayList();

        try {
            CallableStatement cstmt = con.prepareCall("{CALL getAllTasksByAssignWorkItem(?)}");
            cstmt.setInt(1,assignWorkItemId);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){

                ls.add(new tasks(
                        rs.getInt("assignTaskId"),
                        rs.getString("taskName"),
                        rs.getString("taskStatus"),
                        rs.getString("assignStatus"),
                        rs.getDouble("duration"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getDouble("plannedQty"),
                        rs.getString("unitOfMeasure")
                ));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;

    }

    //get all task details by project of work item
    public static ObservableList<tasks> getAllTasksDetailsByWorkItem(int projectTypeId,int workItemId,int buildingId, int levelId){
        ObservableList<tasks> ls =  FXCollections.observableArrayList();

        try {
            CallableStatement cstmt = con.prepareCall("{CALL getAllTasksDetailsByWorkItem(?,?,?,?)}");
            cstmt.setInt(1,projectTypeId);
            cstmt.setInt(2,workItemId);
            cstmt.setInt(3,buildingId);
            cstmt.setInt(4,levelId);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                ls.add(new tasks(
                        rs.getInt("projectTypeId"),
                        rs.getInt("projectWorkItemId"),
                        rs.getInt("projectTaskId"),
                        rs.getString("projectTaskName"),
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
    public static boolean addTaskDetailRecord(tasks changeTask,assignStatus status){
        try {
            CallableStatement cstmt = con.prepareCall("{CALL addTaskDetailRecord(?,?,?,?,?)}");
            cstmt.setInt(1,changeTask.getAssignTaskId());
            cstmt.setDouble(2,changeTask.getProjectDuration());
            cstmt.setDate(3,changeTask.getStartDate());
            cstmt.setDate(4,changeTask.getEndDate());
            cstmt.setString(5,status.toString());
            ResultSet rs = cstmt.executeQuery();
            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //assign the task
    public static boolean assignTasks(tasks assign, projectStatus projectStatus, assignStatus assignStatus){
        try {
            CallableStatement cstmt = con.prepareCall("{CALL assignTaskToWorkItem(?,?,?,?,?,?,?,?,?,?)}");
            cstmt.setInt(1,assign.getAssignProjectId());
            cstmt.setInt(2,assign.getWorkItemId());
            cstmt.setInt(3,assign.getTaskId());
            cstmt.setDouble(4,assign.getProjectDuration());
            cstmt.setDate(5,assign.getStartDate());
            cstmt.setDate(6,assign.getEndDate());
            cstmt.setDouble(7,assign.getPlannedQty());
            cstmt.setString(8,assign.getUnitOfMeasure() == null ? "" : assign.getUnitOfMeasure());
            cstmt.setString(9,projectStatus.toString());
            cstmt.setString(10,assignStatus.toString());
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteTask(int assignTaskId){
        String sql = "UPDATE assignTasks " +
                "SET taskStatus = 5" +
                "WHERE assignTaskId = ?;";
        try {
            PreparedStatement pstmt = con.prepareCall(sql);
            pstmt.setInt(1,assignTaskId);
            return  pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
