package IPPSystem.DAO;

import IPPSystem.Models.DailyReport;
import IPPSystem.Models.DailyReportLaborView;
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

    public static List<DailyReport> getAllReports(Integer supervisorId) {
        List<DailyReport> list = new ArrayList<>();
        String sql = """
        SELECT dr.dailyReportId, dr.assignProjectId, ap.projectInstanceName, pt.typeName AS projectTypeName,
               dr.reportDate, dr.issue, u.userName AS supervisorName
        FROM dailyReports dr
        JOIN assignProjects ap ON dr.assignProjectId = ap.assignProjectId
        LEFT JOIN projectTypes pt ON ap.projectTypeId = pt.projectTypeId
        LEFT JOIN users u ON dr.supervisorId = u.userId
    """;

        if (supervisorId != null) {
            sql += " WHERE dr.supervisorId = ?";
        }

        sql += " ORDER BY dr.reportDate DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (supervisorId != null) ps.setInt(1, supervisorId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DailyReport(
                        rs.getInt("dailyReportId"),
                        rs.getInt("assignProjectId"),
                        rs.getString("projectInstanceName"),
                        rs.getString("projectTypeName"),
                        rs.getDate("reportDate").toLocalDate(),
                        rs.getString("issue"),
                        null,
                        null,
                        rs.getString("supervisorName")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


//    public static List<DailyReport> getAllReports(Integer engineerId) {
//        List<DailyReport> list = new ArrayList<>();
//
//        String sql = "SELECT dr.dailyReportId, dr.assignProjectId, ap.projectInstanceName, dr.reportDate, dr.issue, u.userName AS supervisorName " +
//                "FROM dailyReports dr " +
//                "JOIN assignProjects ap ON dr.assignProjectId = ap.assignProjectId " +
//                "JOIN users u ON dr.supervisorId = u.userId ";
//
//
//        if (engineerId != null) {
//            sql += "WHERE ap.supervisorId = ? ";
//        }
//
//        sql += "ORDER BY dr.reportDate DESC"; // Most recent first
//
//        try (PreparedStatement ps = con.prepareStatement(sql)) {
//            if (engineerId != null) {
//                ps.setInt(1, engineerId);
//            }
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                list.add(new DailyReport(
//                        rs.getInt("dailyReportId"),
//                        rs.getInt("assignProjectId"),
//                        rs.getString("projectInstanceName"),
//                        rs.getDate("reportDate").toLocalDate(),
//                        null, null, null,
//                        rs.getString("issue"),
//                        null,
//                        rs.getString("supervisorName")
//                ));
//
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }



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



    public static List<DailyReportLaborView> getDailyReportLabors(int dailyReportId) {

        List<DailyReportLaborView> list = new ArrayList<>();

        String sql = """
        SELECT 
            l.laborName,
            s.skillName,
            drl.dailyWage,
            drl.workHours,
            drl.remark
        FROM dailyReportLabors drl
        JOIN labors l ON drl.laborId = l.laborId
        LEFT JOIN skills s ON l.skillId = s.skillId
        WHERE drl.dailyReportId = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new DailyReportLaborView(
                        rs.getString("laborName"),
                        rs.getString("skillName"),
                        rs.getDouble("dailyWage"),
                        rs.getDouble("workHours"),
                        rs.getString("remark")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }



    public static double getTotalWorkedHours(int dailyReportId) {

        String sql = """
        SELECT COALESCE(SUM(workHours),0)
        FROM dailyReportLabors
        WHERE dailyReportId = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static double getTotalDailyLaborCost(int dailyReportId) {

        String sql = """
        SELECT COALESCE(SUM(dailyWage),0)
        FROM dailyReportLabors
        WHERE dailyReportId = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static double getCompletedQty(int dailyReportId) {

        String sql = """
        SELECT COALESCE(SUM(completedQty),0)
        FROM dailyReportTasks
        WHERE dailyReportId = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dailyReportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static double getRemainQty(int assignWorkItemId) {

        String sql = """
        SELECT 
            COALESCE(SUM(at.plannedQty),0) 
            - COALESCE(SUM(drt.completedQty),0) AS remainQty
        FROM assignTasks at
        LEFT JOIN dailyReportTasks drt 
            ON at.assignTaskId = drt.assignTaskId
        WHERE at.assignWorkItemId = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignWorkItemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("remainQty");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
