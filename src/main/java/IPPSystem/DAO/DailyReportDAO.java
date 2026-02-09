package IPPSystem.DAO;

import IPPSystem.Controllers.CreateReportNewController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DailyReportDAO {

    public static class ProjectContext {
        public final Integer assignProjectId;
        public final String projectInstanceName;
        public ProjectContext(Integer id, String name) {
            this.assignProjectId = id;
            this.projectInstanceName = name;
        }
    }

    public ProjectContext findLatestInProgressProject(Connection con, int supervisorId, int statusInProgress) throws SQLException {
        String sql = "SELECT assignProjectId, projectInstanceName " +
                "FROM assignProjects " +
                "WHERE supervisorId=? AND projectStatus=? " +
                "ORDER BY assignProjectId DESC LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supervisorId);
            ps.setInt(2, statusInProgress);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProjectContext(rs.getInt("assignProjectId"), rs.getString("projectInstanceName"));
                }
            }
        }
        return new ProjectContext(null, null);
    }

    public List<CreateReportNewController.WorkItemOption> loadWorkItems(Connection con, int assignProjectId) throws SQLException {
        String sql =
                "SELECT awi.assignWorkItemId, wi.projectWorkItemName " +
                        "FROM assignWorkItems awi " +
                        "JOIN workItems wi ON wi.projectWorkItemId = awi.projectWorkItemId " +
                        "WHERE awi.assignProjectId = ? " +
                        "ORDER BY awi.assignWorkItemId";

        List<CreateReportNewController.WorkItemOption> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignProjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CreateReportNewController.WorkItemOption(
                            rs.getInt("assignWorkItemId"),
                            rs.getString("projectWorkItemName")
                    ));
                }
            }
        }
        return list;
    }

    public List<CreateReportNewController.TaskOption> loadTasks(Connection con, int assignWorkItemId) throws SQLException {
        String sql =
                "SELECT at.assignTaskId, t.projectTaskName " +
                        "FROM assignTasks at " +
                        "JOIN tasks t ON t.projectTaskId = at.projectTaskId " +
                        "WHERE at.assignWorkItemId = ? " +
                        "ORDER BY at.assignTaskId";

        List<CreateReportNewController.TaskOption> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignWorkItemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CreateReportNewController.TaskOption(
                            rs.getInt("assignTaskId"),
                            rs.getString("projectTaskName")
                    ));
                }
            }
        }
        return list;
    }

    public List<CreateReportNewController.SkillOption> loadSkills(Connection con, int assignWorkItemId) throws SQLException {
        String sql =
                "SELECT DISTINCT aws.skillId, s.skillName " +
                        "FROM assignWorkItemSkills aws " +
                        "JOIN skills s ON s.skillId = aws.skillId " +
                        "WHERE aws.assignWorkItemId = ? " +
                        "ORDER BY s.skillName";

        List<CreateReportNewController.SkillOption> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignWorkItemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CreateReportNewController.SkillOption(
                            rs.getInt("skillId"),
                            rs.getString("skillName")
                    ));
                }
            }
        }
        return list;
    }

    public double getRemainQty(Connection con, int assignTaskId) throws SQLException {
        String sql =
                "SELECT at.plannedQty - COALESCE((" +
                        "   SELECT SUM(drt.completedQty) " +
                        "   FROM dailyReportTasks drt " +
                        "   JOIN dailyReports dr ON dr.dailyReportId = drt.dailyReportId " +
                        "   WHERE drt.assignTaskId = at.assignTaskId" +
                        "), 0) AS remainQty " +
                        "FROM assignTasks at WHERE at.assignTaskId = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignTaskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("remainQty");
            }
        }
        return 0;
    }

    public double findAutoDailyWage(Connection con, int assignWorkItemId, int skillId, int assignStatusAuto) throws SQLException {
        String sql =
                "SELECT awsd.dailyWagePerLabor " +
                        "FROM assignWorkItemSkills aws " +
                        "JOIN assignWorkItemSkillDetails awsd ON awsd.assignWorkItemSkillId = aws.assignWorkItemSkillId " +
                        "WHERE aws.assignWorkItemId = ? AND aws.skillId = ? AND awsd.assignStatusId = ? " +
                        "ORDER BY awsd.assignWorkItemSkillDetailId DESC LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignWorkItemId);
            ps.setInt(2, skillId);
            ps.setInt(3, assignStatusAuto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("dailyWagePerLabor");
            }
        }
        return 0;
    }
}
