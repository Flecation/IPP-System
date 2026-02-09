package IPPSystem.Services;

import java.sql.*;
import java.time.LocalDate;

public class DailyReportService {

    public int upsertDailyReportHeader(Connection con,
                                       int assignProjectId,
                                       int assignWorkItemId,
                                       LocalDate reportDate,
                                       int supervisorId,
                                       String weather,
                                       String generalRemark,
                                       String issue) throws SQLException {

        Integer existingId = null;

        String find = """
                SELECT dailyReportId
                FROM dailyReports
                WHERE assignProjectId=? AND assignWorkItemId=? AND reportDate=?
                LIMIT 1
                """;

        try (PreparedStatement ps = con.prepareStatement(find)) {
            ps.setInt(1, assignProjectId);
            ps.setInt(2, assignWorkItemId);
            ps.setDate(3, Date.valueOf(reportDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) existingId = rs.getInt("dailyReportId");
            }
        }

        if (existingId != null) {
            String upd = """
                    UPDATE dailyReports
                    SET supervisorId=?, weather=?, generalRemark=?, issue=?
                    WHERE dailyReportId=?
                    """;
            try (PreparedStatement ps = con.prepareStatement(upd)) {
                ps.setInt(1, supervisorId);
                ps.setString(2, weather);
                ps.setString(3, generalRemark);
                ps.setString(4, issue);
                ps.setInt(5, existingId);
                ps.executeUpdate();
            }
            return existingId;
        }

        // create new (you can call your stored proc or just insert directly)
        String ins = """
                INSERT INTO dailyReports(assignProjectId, assignWorkItemId, reportDate, supervisorId, weather, generalRemark, issue)
                VALUES(?,?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = con.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, assignProjectId);
            ps.setInt(2, assignWorkItemId);
            ps.setDate(3, Date.valueOf(reportDate));
            ps.setInt(4, supervisorId);
            ps.setString(5, weather);
            ps.setString(6, generalRemark);
            ps.setString(7, issue);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }

        throw new SQLException("Failed to create daily report header.");
    }

    public void deleteDailyReportTasks(Connection con, int dailyReportId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM dailyReportTasks WHERE dailyReportId=?")) {
            ps.setInt(1, dailyReportId);
            ps.executeUpdate();
        }
    }

    public void deleteDailyReportLabors(Connection con, int dailyReportId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM dailyReportLabors WHERE dailyReportId=?")) {
            ps.setInt(1, dailyReportId);
            ps.executeUpdate();
        }
    }

}
