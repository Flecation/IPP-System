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



    //::::: Labor Section

    // 1. Total Labors Number
    public static int getTotalLaborsCount() {
        String sql = "SELECT COUNT(*) FROM labors";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 2. New Hires this month
    public static int getNewHiresThisMonth() {
        // Query filters by current Year and current Month
        String sql = "SELECT COUNT(*) FROM labors WHERE YEAR(laborStartDate) = YEAR(CURDATE()) " +
                "AND MONTH(laborStartDate) = MONTH(CURDATE())";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 3. Active Labor Number
    public static int getActiveLaborsCount() {
        String sql = "SELECT COUNT(*) FROM labors WHERE isActive = true";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 4. Resigned Labor Number
    public static int getResignedLaborsCount() {
        // We look for isActive = false (or 0) to find resigned staff
        String sql = "SELECT COUNT(*) FROM labors WHERE isActive = false";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }




    // 1. Total Engineers
    public static int getTotalEngineersCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE userRole = 'supervisor'";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }


    // 2. New Engineers This Month
    public static int getNewEngineersThisMonth() {
        String sql = "SELECT COUNT(*) FROM users WHERE userRole = 'supervisor' " +
                "AND YEAR(userStartDate) = YEAR(CURDATE()) " +
                "AND MONTH(userStartDate) = MONTH(CURDATE())";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }


    // 3. Active Engineers
    public static int getActiveEngineersCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE userRole = 'supervisor' AND isActive = true";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }


    // 4. Resigned Engineers
    public static int getResignedEngineersCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE userRole = 'supervisor' AND isActive = false";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

}