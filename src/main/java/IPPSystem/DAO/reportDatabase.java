package IPPSystem.DAO;

import IPPSystem.Models.DailyReport;
import IPPSystem.Models.ReportLabor;
import IPPSystem.Models.projects;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class reportDatabase {

    private static Connection con;
    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static int getCompletedDaysByAssignProject(int assignProjectId) {
        String sql =
                "SELECT COUNT(DISTINCT r.reportDate) AS completedDays " +
                        "FROM dailyReports r " +
                        "JOIN dailyReportTasks rd ON rd.dailyReportId = r.dailyReportId " +
                        "WHERE r.assignProjectId = ? " +
                        "AND rd.isCompleted = true";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignProjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("completedDays");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<DailyReport> getAllReports(Integer engineerId) {
        List<DailyReport> list = new ArrayList<>();

        String sql = "SELECT dr.dailyReportId, dr.assignProjectId, ap.projectInstanceName, dr.reportDate, dr.issue, u.userName AS supervisorName " +
                "FROM dailyReports dr " +
                "JOIN assignProjects ap ON dr.assignProjectId = ap.assignProjectId " +
                "JOIN users u ON dr.supervisorId = u.userId ";


        if (engineerId != null) {
            sql += "WHERE ap.supervisorId = ? ";
        }

        sql += "ORDER BY dr.reportDate DESC"; // Most recent first

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (engineerId != null) {
                ps.setInt(1, engineerId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DailyReport(
                        rs.getInt("dailyReportId"),
                        rs.getInt("assignProjectId"),
                        rs.getString("projectInstanceName"),
                        rs.getDate("reportDate").toLocalDate(),
                        null, null, null,
                        rs.getString("issue"),
                        null,
                        rs.getString("supervisorName")
                ));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }



    public static List<DailyReport> getReportsByProjectId(int assignProjectId) {
        List<DailyReport> reports = new ArrayList<>();
        String sql = """
            SELECT 
                dr.dailyReportId AS reportId,
                dr.assignProjectId,
                dr.reportDate,
                dr.issue,
                dr.weather AS weatherType,
                dr.generalRemark AS comments,
                p.projectInstanceName AS projectName,
                ps.projectStatusName AS projectStatus,
                u.userName AS supervisorName
            FROM dailyReports dr
            JOIN assignProjects p ON dr.assignProjectId = p.assignProjectId
            LEFT JOIN users u ON dr.supervisorId = u.userId
            LEFT JOIN projectStatus ps ON p.projectStatus = ps.projectStatusId
            WHERE dr.assignProjectId = ?
            ORDER BY dr.reportDate DESC
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignProjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DailyReport report = new DailyReport();
                report.setReportId(rs.getInt("reportId"));
                report.setAssignProjectId(rs.getInt("assignProjectId"));
                report.setReportDate(rs.getDate("reportDate").toLocalDate());
                report.setIssues(rs.getString("issue"));
                report.setWeatherType(rs.getString("weatherType"));
                report.setComments(rs.getString("comments"));
                report.setProjectName(rs.getString("projectName"));
                report.setProjectStatus(rs.getString("projectStatus"));
                report.setSupervisorName(rs.getString("supervisorName"));

                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

}
