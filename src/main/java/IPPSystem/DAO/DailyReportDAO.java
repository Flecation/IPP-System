package IPPSystem.DAO;

import IPPSystem.Models.DailyReportModel;
import IPPSystem.Models.DailyReportLaborModel;
import IPPSystem.Models.DailyReportTaskModel;

import java.sql.*;

public class DailyReportDAO {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int saveReport(DailyReportModel report) {
        String sql = "INSERT INTO dailyreports (assignProjectId, reportDate, supervisorId, weather, generalRemark, issue) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, report.getAssignProjectId());
            ps.setDate(2, report.getReportDate());
            ps.setInt(3, report.getSupervisorId());
            ps.setString(4, report.getWeather());
            ps.setString(5, report.getGeneralRemark());
            ps.setString(6, report.getIssue());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Return generated dailyReportId
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean saveLaborReport(DailyReportLaborModel laborReport) {
        String sql = "INSERT INTO dailyreportlabors (dailyReportId, laborId, workHours, dailyWage, remark) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, laborReport.getDailyReportId());
            ps.setInt(2, laborReport.getLaborId());
            ps.setDouble(3, laborReport.getWorkHours());
            ps.setDouble(4, laborReport.getDailyWage());
            ps.setString(5, laborReport.getRemark());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveTaskReport(DailyReportTaskModel taskReport) {
        String sql = "INSERT INTO dailyreporttasks (dailyReportId, assignTaskId, progressDescription, workHours, completedQty, isCompleted) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskReport.getDailyReportId());
            ps.setInt(2, taskReport.getAssignTaskId());
            ps.setString(3, taskReport.getProgressDescription());
            ps.setDouble(4, taskReport.getWorkHours());
            ps.setDouble(5, taskReport.getCompletedQty());
            ps.setBoolean(6, taskReport.isCompleted());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}