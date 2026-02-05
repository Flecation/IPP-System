package IPPSystem.Services;

import IPPSystem.DAO.databaseConnection;
import IPPSystem.Models.projects;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardService {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public static List<projects> getAllProjects() {
        List<projects> projectList = new ArrayList<>();
        String sql = "SELECT * FROM assignProjects p " +
                "LEFT JOIN projectStatus ps ON p.projectStatusId = ps.statusId " +
                "WHERE p.projectStatusId != 5"; // 5 is for deleted projects

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                projects project = new projects();
                project.setAssignProjectId(rs.getInt("assignProjectId"));
                project.setProjectInstanceName(rs.getString("projectInstanceName"));
                project.setProjectLocation(rs.getString("projectLocation"));
                project.setProjectCost(rs.getDouble("projectCost"));
                project.setProjectLaborQty(rs.getDouble("projectLaborQty"));
                project.setStartDate(rs.getDate("startDate"));
                project.setEndDate(rs.getDate("endDate"));
                project.setProjectStatus(rs.getString("statusName"));

                projectList.add(project);
            }
        } catch (SQLException e) {
            System.err.println("Error getting projects: " + e.getMessage());
            e.printStackTrace();
        }
        return projectList;
    }

    public static projects getProjectById(int projectId) {
        String sql = "SELECT * FROM assignProjects p " +
                "LEFT JOIN projectStatus ps ON p.projectStatusId = ps.statusId " +
                "WHERE p.assignProjectId = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    projects project = new projects();
                    project.setAssignProjectId(rs.getInt("assignProjectId"));
                    project.setProjectInstanceName(rs.getString("projectInstanceName"));
                    project.setProjectLocation(rs.getString("projectLocation"));
                    project.setProjectCost(rs.getDouble("projectCost"));
                    project.setProjectLaborQty(rs.getDouble("projectLaborQty"));
                    project.setStartDate(rs.getDate("startDate"));
                    project.setEndDate(rs.getDate("endDate"));
                    project.setProjectStatus(rs.getString("statusName"));
                    return project;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting project by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}