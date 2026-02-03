package IPPSystem.DAO;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.projectStatus;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public static ObservableList<projects> getAllProjects(){
        ObservableList<projects> ls = FXCollections.observableArrayList();
        try {
            CallableStatement cstmt = con.prepareCall("{CALL getAllProjects()}");
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                ls.add(new projects(
                        rs.getInt("assignProjectId"),
                        rs.getString("projectInstanceName"),
                        rs.getInt("projectTypeId"),
                        rs.getString("projectTypeName"),
                        rs.getInt("levelId"),
                        rs.getString("levelName"),
                        rs.getInt("buildingId"),
                        rs.getString("buildingName"),
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getDouble("projectArea"),
                        rs.getDouble("projectHeight"),
                        rs.getDouble("totalStories"),
                        rs.getDouble("totalUnits"),
                        rs.getDouble("projectCost"),
                        rs.getDouble("projectLaborQty"),
                        rs.getDouble("projectOverHeadCost"),
                        rs.getDouble("projectDuration"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getString("projectLocation"),
                        rs.getString("projectStatus"),
                        rs.getString("assignStatus")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    //for the projects details
    public static ObservableList<projects> getProjectDetails(int projectTypeId){
        ObservableList<projects> projects = FXCollections.observableArrayList();
        try {
            CallableStatement cstmt = con.prepareCall("{CALL getProjectDetails(?)}");
            cstmt.setInt(1,projectTypeId);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()){
                projects.add(
                    new projects(
                            rs.getInt("projectTypeId"),
                            rs.getString("projectTypeName"),
                            rs.getInt("projectLevelId"),
                            rs.getString("projectLevelName"),
                            rs.getInt("projectBuildingId"),
                            rs.getString("projectBuildingName"),
                            rs.getDouble("minOverHeadCost"),
                            rs.getDouble("maxOverHeadCost")
                    ));
            }
            return projects;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //to assign the project
    public static boolean assignProjects(projects assign, projectStatus projectStatus, assignStatus assignStatus){
        try {
            CallableStatement cstmt = con.prepareCall("{CALL assignFullProject(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cstmt.setInt(1,assign.getProjectTypeId());
            cstmt.setString(2,assign.getProjectInstanceName());
            cstmt.setInt(3,assign.getProjectBuildingId());
            cstmt.setInt(4,assign.getProjectLevelId());
            cstmt.setDouble(5,assign.getProjectArea());
            cstmt.setDouble(6,assign.getProjectHeight());
            cstmt.setDouble(7,assign.getTotalStories());
            cstmt.setDouble(8,assign.getTotalUnits());
            cstmt.setInt(9,assign.getUserId());
            cstmt.setString(10,assign.getProjectLocation());
            cstmt.setDouble(11,assign.getProjectOverHeadCost());
            cstmt.setString(12, projectStatus.toString());
            cstmt.setString(13,assignStatus.toString());
            cstmt.setDate(14,assign.getStartDate());
            cstmt.setDate(15,assign.getEndDate());
            ResultSet rs = cstmt.executeQuery();
            return rs.next() && rs.getBoolean(1);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean updateAssignProject(projects assign,assignStatus assignStatus){
        try(CallableStatement cs = con.prepareCall("{CALL updateAssignProject(?,?,?,?,?,?,?)}")){
            cs.setInt(1,assign.getAssignProjectId());
            cs.setString(2,assignStatus.toString());
            cs.setDouble(3,assign.getProjectCost());
            cs.setDouble(4,assign.getProjectLaborQty());
            cs.setDouble(5,assign.getProjectDuration());
            cs.setDate(6,assign.getStartDate());
            cs.setDate(7,assign.getEndDate());
            ResultSet rs = cs.executeQuery();
            return rs.next() && rs.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean deleteAssignProject(int assignProjectId){
        String sql = "UPDATE assignProjects " +
                "SET projectStatus = 5" +
                "WHERE assignProjectId = ?;";
        try {
            PreparedStatement pstmt = con.prepareCall(sql);
            pstmt.setInt(1,assignProjectId);
            return  pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static String currentAssignProject(int userId) {
        try {
            String sql = "SELECT projectInstanceName FROM assignProjects WHERE supervisorId = ? AND projectStatus = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, 2); // 2 = inProgressing;

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String projectTypeName = rs.getString("projectInstanceName");
                rs.close();
                pstmt.close();
                return projectTypeName;
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // return null if no project found
    }


    public static List<projects> getProjectsByEngineer(int engineerId) {

        System.out.println("Engineer ID: " + engineerId);

        List<projects> list = new ArrayList<>();

        String sql = "SELECT ap.*, ps.projectStatusName AS projectStatusName, pt.typeName AS projectTypeName " +
                "FROM assignProjects ap " +
                "LEFT JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId " +
                "LEFT JOIN projectTypes pt ON ap.projectTypeId = pt.projectTypeId " +
                "WHERE ap.supervisorId = ?";


        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, engineerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                projects p = new projects();
                p.setAssignProjectId(rs.getInt("assignProjectId"));
                p.setProjectInstanceName(rs.getString("projectInstanceName"));
                p.setProjectTypeName(rs.getString("projectTypeName"));
                p.setProjectLocation(rs.getString("projectLocation"));
                p.setProjectArea(rs.getDouble("projectArea"));
                p.setProjectHeight(rs.getDouble("projectHeight"));
                p.setTotalStories(rs.getDouble("totalStories"));
                p.setTotalUnits(rs.getDouble("totalUnits"));
                p.setProjectOverHeadCost(rs.getDouble("projectOverHeadCost"));
                p.setProjectStatus(rs.getString("projectStatusName"));

                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void callUpdateProjectBaseline(int projectId, double cost,
                                                 Date start, Date end,
                                                 double duration) {
        String sql = "{CALL updateProjectBaseline(?,?,?,?,?)}";

        try (Connection con = databaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, projectId);
            cs.setDouble(2, cost);
            cs.setDate(3, start);
            cs.setDate(4, end);
            cs.setDouble(5, duration);

            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
//            aa
        }
    }


}
