package IPPSystem.DAO;

import IPPSystem.Models.projects;

import java.sql.*;
import java.util.ArrayList;

public class projectDatabase {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //for the all projects that we have(all)
    public static ArrayList<projects> getAllProjects(){
        ArrayList<projects> ls = new ArrayList<>();
        try {
            CallableStatement cstmt = con.prepareCall("");
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                ls.add(new projects(
                        rs.getInt("projectId"),
                        rs.getString("projectInstanceName"),
                        rs.getString("typeName"),
                        rs.getString("buildingName"),
                        rs.getString("levelName"),
                        rs.getInt("totalStories"),
                        rs.getInt("totalUnits"),
                        rs.getDouble("projectArea"),
                        rs.getDouble("projectHeight"),
                        rs.getString("projectLocation"),
                        rs.getDate("startDate"),
                        rs.getDouble("duration"),
                        rs.getDouble("totalCost"),
                        rs.getString("userName"),
                        rs.getString("projectStatus")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    //to assign the project
    public static boolean assignProjects(projects assign){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assign.getProjectTypeId());
            cstmt.setString(2,assign.getProjectInstanceName());
            cstmt.setString(3,assign.getLevel());
            cstmt.setString(4,assign.getBuildingName());
            cstmt.setDouble(5,assign.getProjectArea());
            cstmt.setDouble(6,assign.getProjectHeight());
            cstmt.setInt(7,assign.getTotalStories());
            cstmt.setInt(8,assign.getTotalUnits());
            cstmt.setString(9,assign.getSupervisor());
            cstmt.setString(10,assign.getProjectLocation());
            cstmt.setDate(11,assign.getStartDate());
            cstmt.setDouble(12,assign.getDuration());
            cstmt.setString(13,assign.getProjectStatus());
            ResultSet rs = cstmt.executeQuery();

            return rs.getBoolean(1);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //for the projects from the supervisor
    public static ArrayList<projects> getAllProjectsBySupervisor(int supervisorId){
        ArrayList<projects> ls = new ArrayList<>();

        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,supervisorId);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                ls.add(new projects(
                        rs.getInt("projectId"),
                        rs.getString("projectInstanceName"),
                        rs.getString("typeName"),
                        rs.getString("buildingName"),
                        rs.getString("levelName"),
                        rs.getInt("totalStories"),
                        rs.getInt("totalUnits"),
                        rs.getDouble("projectArea"),
                        rs.getDouble("projectHeight"),
                        rs.getString("projectLocation"),
                        rs.getDate("startDate"),
                        rs.getDouble("duration"),
                        rs.getDouble("totalCost"),
                        rs.getString("userName"),
                        rs.getString("projectStatus")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;
    }



}
