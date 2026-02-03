package IPPSystem.Utils;

import IPPSystem.DAO.databaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Data + calculation helper for Earned Value (BAC/PV/EV/AC) and performance indexes (CPI/SPI).
 *
 * - DB-side numbers come from stored procedures:
 *      getProjectDashboard(projectId, asOfDate)
 *      getProjectWorkItemsDashboard(projectId, asOfDate)
 *      getWorkItemDashboard(assignWorkItemId, asOfDate)
 *
 * - This class also provides small "utils-like" pure functions for CPI/SPI and day calculations.
 */
public final class calculationHelper {

    private static final calculationHelper INSTANCE = new calculationHelper();

    private calculationHelper() {}

    public static calculationHelper getInstance() {
        return INSTANCE;
    }

    /**
     * Aggregated dashboard for a project.
     */
    public record ProjectDashboard(
            double bac, double pv, double ev, double ac,
            Double cpi, Double spi,
            double progressRatio,
            Date baselineStart, Date baselineEnd,
            int elapsedDays, int totalDays, int reportedDays,
            int completedWorkItems, int totalWorkItems
    ) {}

    /**
     * Per work-item dashboard row inside a project.
     */
    public record WorkItemDashboard(
            int assignWorkItemId,
            String workItemName,
            String workItemStatus,
            double bac, double pv, double ev, double ac,
            Double cpi, Double spi,
            double progressRatio
    ) {}

    // ---------------------------------------------------------------------
    // Pure calculations ("utils-like")
    // ---------------------------------------------------------------------

    /** CPI = EV / AC. Returns null when AC <= 0. */
    public static Double calcCpi(double ev, double ac) {
        return (ac <= 0) ? null : (ev / ac);
    }

    /** SPI = EV / PV. Returns null when PV <= 0. */
    public static Double calcSpi(double ev, double pv) {
        return (pv <= 0) ? null : (ev / pv);
    }

    /** Total baseline days (inclusive). Returns 0 when dates are invalid. */
    public static int computeTotalDays(Date start, Date end) {
        if (start == null || end == null) return 0;
        LocalDate s = start.toLocalDate();
        LocalDate e = end.toLocalDate();
        if (e.isBefore(s)) return 0;
        long days = ChronoUnit.DAYS.between(s, e) + 1;
        if (days <= 0) return 0;
        return (days > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) days;
    }

    /**
     * Elapsed days from baseline start to asOf (inclusive), clamped to 0..totalDays.
     * Returns 0 if baseline dates are invalid.
     */
    public static int computeElapsedDays(Date start, Date end, LocalDate asOf) {
        if (start == null || end == null || asOf == null) return 0;
        int total = computeTotalDays(start, end);
        if (total <= 0) return 0;

        LocalDate s = start.toLocalDate();
        long raw = ChronoUnit.DAYS.between(s, asOf) + 1;
        if (raw < 0) return 0;
        if (raw > total) return total;
        return (raw > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) raw;
    }

    /**
     * Shared status text for CPI/SPI.
     *
     * schedule=true  -> SPI meaning: Ahead/On/Behind schedule
     * schedule=false -> CPI meaning: Under/On/Over budget
     */
    public static String statusTextForIndex(Double idx, boolean schedule) {
        if (idx == null) return "No Data";

        if (schedule) {
            if (idx >= 1.05) return "Ahead of Schedule";
            if (idx >= 0.95) return "On Schedule";
            return "Behind Schedule";
        } else {
            if (idx >= 1.05) return "Under Budget";
            if (idx >= 0.95) return "On Budget";
            return "Over Budget";
        }
    }


    // ---------------------------------------------------------------------
    // Stored procedure calls
    // ---------------------------------------------------------------------

    public ProjectDashboard getProjectDashboard(int projectId, LocalDate asOf) {
        final String sql = "{CALL getProjectDashboard(?,?)}";
        LocalDate effectiveAsOf = (asOf == null) ? LocalDate.now() : asOf;

        try (Connection con = databaseConnection.getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, projectId);
            stmt.setDate(2, Date.valueOf(effectiveAsOf));

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                double bac = rs.getDouble("BAC");
                double pv  = rs.getDouble("PV");
                double ev  = rs.getDouble("EV");
                double ac  = rs.getDouble("AC");

                Double cpi = getNullableDouble(rs, "CPI");
                if (cpi == null) cpi = calcCpi(ev, ac);

                Double spi = getNullableDouble(rs, "SPI");
                if (spi == null) spi = calcSpi(ev, pv);

                double progressRatio = clamp01(rs.getDouble("progressRatio"));

                Date baselineStart = rs.getDate("baselineStart");
                Date baselineEnd   = rs.getDate("baselineEnd");

                // NOTE: your current SQL returns a wrong "elapsedDays" (it outputs latestReportDate).
                // So we recompute days safely from baselineStart/baselineEnd + asOf.
                int totalDays   = computeTotalDays(baselineStart, baselineEnd);
                int elapsedDays = computeElapsedDays(baselineStart, baselineEnd, effectiveAsOf);

                // If baseline dates are missing (shouldn't happen), fall back to DB fields.
                if (totalDays <= 0) totalDays = rs.getInt("totalDays");
                if (elapsedDays <= 0) elapsedDays = rs.getInt("elapsedDays");

                return new ProjectDashboard(
                        bac, pv, ev, ac,
                        cpi, spi,
                        progressRatio,
                        baselineStart, baselineEnd,
                        elapsedDays,
                        totalDays,
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
        LocalDate effectiveAsOf = (asOf == null) ? LocalDate.now() : asOf;

        ObservableList<WorkItemDashboard> list = FXCollections.observableArrayList();

        try (Connection con = databaseConnection.getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, projectId);
            stmt.setDate(2, Date.valueOf(effectiveAsOf));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double bac = rs.getDouble("BAC");
                    double pv  = rs.getDouble("PV");
                    double ev  = rs.getDouble("EV");
                    double ac  = rs.getDouble("AC");

                    Double cpi = getNullableDouble(rs, "CPI");
                    if (cpi == null) cpi = calcCpi(ev, ac);

                    Double spi = getNullableDouble(rs, "SPI");
                    if (spi == null) spi = calcSpi(ev, pv);

                    list.add(new WorkItemDashboard(
                            rs.getInt("assignWorkItemId"),
                            rs.getString("workItemName"),
                            rs.getString("workItemStatus"),
                            bac, pv, ev, ac,
                            cpi, spi,
                            clamp01(rs.getDouble("progressRatio"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getProjectWorkItemsDashboard failed", e);
        }

        return list;
    }

    /**
     * For work item details screen we only need the numeric fields.
     * (We reuse ProjectDashboard record for convenience.)
     */
    public ProjectDashboard getWorkItemDashboardOnlyNumbers(int assignWorkItemId, LocalDate asOf) {
        final String sql = "{CALL getWorkItemDashboard(?,?)}";
        LocalDate effectiveAsOf = (asOf == null) ? LocalDate.now() : asOf;

        try (Connection con = databaseConnection.getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, assignWorkItemId);
            stmt.setDate(2, Date.valueOf(effectiveAsOf));

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                double bac = rs.getDouble("BAC");
                double pv  = rs.getDouble("PV");
                double ev  = rs.getDouble("EV");
                double ac  = rs.getDouble("AC");

                Double cpi = getNullableDouble(rs, "CPI");
                if (cpi == null) cpi = calcCpi(ev, ac);

                Double spi = getNullableDouble(rs, "SPI");
                if (spi == null) spi = calcSpi(ev, pv);

                double progressRatio = clamp01(rs.getDouble("progressRatio"));

                Date baselineStart = rs.getDate("baselineStart");
                Date baselineEnd   = rs.getDate("baselineEnd");

                int totalDays   = computeTotalDays(baselineStart, baselineEnd);
                int elapsedDays = computeElapsedDays(baselineStart, baselineEnd, effectiveAsOf);

                if (totalDays <= 0) totalDays = rs.getInt("totalDays");
                if (elapsedDays <= 0) elapsedDays = rs.getInt("elapsedDays");

                return new ProjectDashboard(
                        bac, pv, ev, ac,
                        cpi, spi,
                        progressRatio,
                        baselineStart, baselineEnd,
                        elapsedDays,
                        totalDays,
                        0, 0, 0
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("getWorkItemDashboard failed", e);
        }
    }


    // ---------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------

    private static Double getNullableDouble(ResultSet rs, String col) throws SQLException {
        Object obj = rs.getObject(col);
        return (obj == null) ? null : ((Number) obj).doubleValue();
    }

    private static double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }
}
