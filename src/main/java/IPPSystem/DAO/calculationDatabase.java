package IPPSystem.DAO;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    // Existing methods (getWorkload, getHistoryPerformance, etc.) ...

    // --- REVENUE & EXPENSE METHODS ---

    /**
     * Calculates total revenue by summing projectCost from all assigned projects.
     */
    public static double getTotalRevenue() {
        String sql = "SELECT SUM(projectCost) as totalRevenue FROM assignProjects";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("totalRevenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Calculates total expenses by summing projectCost and projectOverheadCost.
     */
    public static double getTotalExpenses() {
        String sql = "SELECT SUM(projectCost + projectOverheadCost) as totalExpense FROM assignProjects";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("totalExpense");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // --- CHART DATA METHODS ---

    /**
     * Extracts the distribution of project statuses (Completed, In Progress, Planning).
     */
    public static Map<String, Integer> getProjectStatusDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("Completed", 0);
        distribution.put("In Progress", 0);
        distribution.put("Planning", 0);

        String sql = "SELECT ps.projectStatusName, COUNT(ap.assignProjectId) as count " +
                "FROM assignProjects ap " +
                "JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId " +
                "GROUP BY ps.projectStatusName";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String status = rs.getString("projectStatusName");
                int count = rs.getInt("count");

                if (status.equalsIgnoreCase("finished")) {
                    distribution.put("Completed", count);
                } else if (status.equalsIgnoreCase("inProgress") || status.equalsIgnoreCase("inProgressing")) {
                    distribution.put("In Progress", count);
                } else if (status.equalsIgnoreCase("planning")) {
                    distribution.put("Planning", count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }

    /**
     * Compares project end dates with the current date to determine On Time vs. Delayed status.
     */
    public static Map<String, Integer> getProjectTimingStatus() {
        Map<String, Integer> timingData = new HashMap<>();
        timingData.put("On Time", 0);
        timingData.put("Delayed", 0);

        String sql = "SELECT " +
                "SUM(CASE WHEN projectEndDate < CURDATE() AND ps.projectStatusName != 'finished' THEN 1 ELSE 0 END) as delayedCount, " +
                "SUM(CASE WHEN projectEndDate >= CURDATE() OR ps.projectStatusName = 'finished' THEN 1 ELSE 0 END) as onTimeCount " +
                "FROM assignProjects ap " +
                "JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                timingData.put("Delayed", rs.getInt("delayedCount"));
                timingData.put("On Time", rs.getInt("onTimeCount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timingData;
    }


    /**
     * Retrieves the three projects with the highest costs for the dashboard progress bars.
     */
    public static Map<String, Double> getTopThreeMostExpensiveProjects() {
        Map<String, Double> topProjects = new LinkedHashMap<>();
        String sql = "SELECT projectInstanceName, projectCost " +
                "FROM assignProjects " +
                "ORDER BY projectCost DESC " +
                "LIMIT 3";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                topProjects.put(rs.getString("projectInstanceName"), rs.getDouble("projectCost"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topProjects;
    }
//    Projectt Status
    public static Map<String, Integer> getProjectStatusMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("Completed", 0);
        metrics.put("Delayed", 0);
        metrics.put("Ahead/On Time", 0);

        String sql = "SELECT " +
                "SUM(CASE WHEN ps.projectStatusName = 'finished' THEN 1 ELSE 0 END) as completedCount, " +
                "SUM(CASE WHEN ps.projectStatusName != 'finished' AND ap.endDate < CURDATE() THEN 1 ELSE 0 END) as delayedCount, " +
                "SUM(CASE WHEN ps.projectStatusName != 'finished' AND ap.endDate >= CURDATE() THEN 1 ELSE 0 END) as aheadCount " +
                "FROM assignProjects ap " +
                "JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                metrics.put("Completed", rs.getInt("completedCount"));
                metrics.put("Delayed", rs.getInt("delayedCount"));
                metrics.put("Ahead/On Time", rs.getInt("aheadCount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metrics;
    }
    // Monthly Project Cost Trend (လအလိုက် ကုန်ကျစရိတ်)
    public static Map<String, Double> getMonthlyCostTrend() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(startDate, '%b') as month, SUM(projectCost) as total " +
                "FROM assignProjects " +
                "WHERE YEAR(startDate) = YEAR(CURDATE()) " +
                "GROUP BY MONTH(startDate) " +
                "ORDER BY MONTH(startDate)";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("month"), rs.getDouble("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // Active Project Trend (လအလိုက် Active ဖြစ်နေသော Project အရေအတွက်)
    public static Map<String, Integer> getActiveProjectTrend() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(startDate, '%b') as month, COUNT(*) as count " +
                "FROM assignProjects " +
                "WHERE projectStatus != (SELECT projectStatusId FROM projectStatus WHERE projectStatusName = 'finished') " +
                "AND YEAR(startDate) = YEAR(CURDATE()) " +
                "GROUP BY MONTH(startDate) " +
                "ORDER BY MONTH(startDate)";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("month"), rs.getInt("count"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }
}