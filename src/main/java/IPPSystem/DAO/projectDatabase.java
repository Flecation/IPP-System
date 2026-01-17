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

    //for the all assign projects that we have(all)
    public static ArrayList<projects> getAllProjects(){
        ArrayList<projects> ls = new ArrayList<>();
        try {
            CallableStatement cstmt = con.prepareCall("");
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                ls.add(new projects(
                        rs.getString("projectInstanceName"),
                        rs.getString("typeName"),
                        rs.getString("buildingName"),
                        rs.getString("levelName"),
                        rs.getString("userName"),
                        rs.getDouble("projectArea"),
                        rs.getDouble("projectHeight"),
                        rs.getDouble("totalStories"),
                        rs.getDouble("totalUnits"),
                        rs.getDouble("projectDuration"),
                        rs.getDouble("projectCost"),
                        rs.getDouble("projectLaborQty"),
                        rs.getDouble("projectOverHeadCost"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getString("projectLocation"),
                        rs.getString("projectStatus")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    //to assign the project
    public static int assignProjects(projects assign){
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setInt(1,assign.getProjectTypeId());
            cstmt.setString(2,assign.getProjectInstanceName());
            cstmt.setString(3,assign.getProjectLevelName());
            cstmt.setString(4,assign.getProjectBuildingName());
            cstmt.setDouble(5,assign.getProjectArea());
            cstmt.setDouble(6,assign.getProjectHeight());
            cstmt.setDouble(7,assign.getTotalStories());
            cstmt.setDouble(8,assign.getTotalUnits());
            cstmt.setString(9,assign.getUserName());
            cstmt.setString(10,assign.getProjectLocation());
            cstmt.setDate(11,assign.getStartDate());
            cstmt.setDouble(12,assign.getProjectDuration());
            cstmt.setString(13,assign.getProjectStatus());
            ResultSet rs = cstmt.executeQuery();

            return rs.getInt(1);


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
                        rs.getString("projectInstanceName"),
                        rs.getString("typeName"),
                        rs.getString("buildingName"),
                        rs.getString("levelName"),
                        rs.getString("userName"),
                        rs.getDouble("projectArea"),
                        rs.getDouble("projectHeight"),
                        rs.getDouble("totalStories"),
                        rs.getDouble("totalUnits"),
                        rs.getDouble("projectDuration"),
                        rs.getDouble("projectCost"),
                        rs.getDouble("projectLaborQty"),
                        rs.getDouble("projectOverHeadCost"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getString("projectLocation"),
                        rs.getString("projectStatus")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    //for the projects details
    public static projects getProjectDetails(String projectTypeName){
        projects projects = new projects();
        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setString(1,projectTypeName);
            ResultSet rs = cstmt.executeQuery();
            if(rs.next()){
                projects = new projects(
                        rs.getInt("projectTypeId"),
                        rs.getString("projectTypeName"),
                        rs.getInt("projectLevelId"),
                        rs.getString("projectLevelName"),
                        rs.getInt("projectBuildingId"),
                        rs.getString("projectBuildingName"),
                        rs.getDouble("minOverHeadCost"),
                        rs.getDouble("maxOverHeadCost")

                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projects;
    }

}
