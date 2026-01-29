package IPPSystem.DAO;

import java.sql.*;

public class calculationDatabase {
    private static Connection con;
    private static final double MAX_CAPACITY = 100.0; // Normalization constant

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // WORKLOAD: Sum of Duration + Labor for CURRENT project tasks only
    public static double getWorkload(int userId) {
        String sql = "SELECT SUM(awid.workItemDuration + awid.workItemLaborQty) as currentLoad " +
                "FROM assignWorkItemDetails awid " +
                "JOIN assignWorkItems awi ON awid.assignWorkItemId = awi.assignWorkItemId " +
                "JOIN dailyReports dr ON awi.assignProjectId = dr.assignProjectId " +
                "JOIN projectStatus ps ON awi.workItemStatus = ps.projectStatusId " +
                "WHERE dr.supervisorId = ? AND ps.projectStatusName = 'inProgress'";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double load = rs.getDouble("currentLoad");
                // Divide by your MAX_CAPACITY (e.g., 100.0)
                return Math.min(load / 100.0, 1.0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // PERFORMANCE: (Finished Projects / Total Assigned Projects) in history
    public static double getHistoryPerformance(int userId) {
        String sql = "SELECT " +
                "COUNT(DISTINCT CASE WHEN ps.projectStatusName = 'finished' THEN ap.assignProjectId END) as finished, " +
                "COUNT(DISTINCT ap.assignProjectId) as total " +
                "FROM assignProjects ap " +
                "JOIN dailyReports dr ON ap.assignProjectId = dr.assignProjectId " +
                "JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId " +
                "WHERE dr.supervisorId = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int finished = rs.getInt("finished");
                return (total == 0) ? 0.0 : (double) finished / total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}