package IPPSystem.Utils;

import IPPSystem.DAO.databaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public final class calculationHelper {

    private static calculationHelper instance;

    private calculationHelper() {}

    public static calculationHelper getInstance() {
        if (instance == null) instance = new calculationHelper();
        return instance;
    }

    public record ProjectDashboard(
            double bac, double pv, double ev, double ac,
            Double cpi, Double spi,
            double progressRatio,
            Date baselineStart, Date baselineEnd,
            int elapsedDays, int totalDays, int reportedDays,
            int completedWorkItems, int totalWorkItems
    ) {}

    public record WorkItemDashboard(
            int assignWorkItemId,
            String workItemName,
            String workItemStatus,
            double bac, double pv, double ev, double ac,
            Double cpi, Double spi,
            double progressRatio
    ) {}

    public ProjectDashboard getProjectDashboard(int projectId, LocalDate asOf) {
        final String sql = "{CALL getProjectDashboard(?,?)}";
        Date asOfDate = Date.valueOf(asOf);

        try (Connection con = databaseConnection.getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, projectId);
            stmt.setDate(2, asOfDate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                return new ProjectDashboard(
                        rs.getDouble("BAC"),
                        rs.getDouble("PV"),
                        rs.getDouble("EV"),
                        rs.getDouble("AC"),
                        getNullableDouble(rs, "CPI"),
                        getNullableDouble(rs, "SPI"),
                        rs.getDouble("progressRatio"),
                        rs.getDate("baselineStart"),
                        rs.getDate("baselineEnd"),
                        rs.getInt("elapsedDays"),
                        rs.getInt("totalDays"),
                        rs.getInt("reportedDays"),
                        rs.getInt("completedWorkItems"),
                        rs.getInt("totalWorkItems")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("getProjectDashboard failed", e);
        }
    }

    public ObservableList<WorkItemDashboard> getProjectWorkItemsDashboard(int projectId, LocalDate asOf) {
        final String sql = "{CALL getProjectWorkItemsDashboard(?,?)}";
        Date asOfDate = Date.valueOf(asOf);
        ObservableList<WorkItemDashboard> list = FXCollections.observableArrayList();

        try (Connection con = databaseConnection.getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, projectId);
            stmt.setDate(2, asOfDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new WorkItemDashboard(
                            rs.getInt("assignWorkItemId"),
                            rs.getString("workItemName"),
                            rs.getString("workItemStatus"),
                            rs.getDouble("BAC"),
                            rs.getDouble("PV"),
                            rs.getDouble("EV"),
                            rs.getDouble("AC"),
                            getNullableDouble(rs, "CPI"),
                            getNullableDouble(rs, "SPI"),
                            rs.getDouble("progressRatio")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getProjectWorkItemsDashboard failed", e);
        }

        return list;
    }

    public ProjectDashboard getWorkItemDashboardOnlyNumbers(int assignWorkItemId, LocalDate asOf) {
        final String sql = "{CALL getWorkItemDashboard(?,?)}";
        Date asOfDate = Date.valueOf(asOf);

        try (Connection con = databaseConnection.getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, assignWorkItemId);
            stmt.setDate(2, asOfDate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                // reuse ProjectDashboard shape for numeric fields (simple)
                return new ProjectDashboard(
                        rs.getDouble("BAC"),
                        rs.getDouble("PV"),
                        rs.getDouble("EV"),
                        rs.getDouble("AC"),
                        getNullableDouble(rs, "CPI"),
                        getNullableDouble(rs, "SPI"),
                        rs.getDouble("progressRatio"),
                        rs.getDate("baselineStart"),
                        rs.getDate("baselineEnd"),
                        rs.getInt("elapsedDays"),
                        rs.getInt("totalDays"),
                        0, 0, 0
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("getWorkItemDashboard failed", e);
        }
    }

    private static Double getNullableDouble(ResultSet rs, String col) throws SQLException {
        Object obj = rs.getObject(col);
        return (obj == null) ? null : ((Number) obj).doubleValue();
    }
}
