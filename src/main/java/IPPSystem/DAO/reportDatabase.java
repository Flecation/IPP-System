package IPPSystem.DAO;

import IPPSystem.Models.DailyReport;
import IPPSystem.Models.ReportLabor;
import IPPSystem.Models.projects;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // ======================== EXISTING METHODS ========================

    // createReport Method (ရှိပြီးသား - မဖျက်ပါနဲ့)
    public static boolean createReport(DailyReport report, List<ReportLabor> laborList) {
        // ... (မင်းရဲ့ create logic အဟောင်း) ...
        return true; // placeholder
    }

    // ✅ Supervisor အတွက် Report အားလုံးဆွဲထုတ်ခြင်း
    public static ArrayList<DailyReport> getAllReports(int supervisorId) {
        ArrayList<DailyReport> list = new ArrayList<>();
        String sql = "SELECT r.reportId, r.reportDate, r.issues, p.projectInstanceName, u.userName " +
                "FROM dailyReports r " +
                "JOIN assignProjects ap ON r.assignProjectId = ap.assignProjectId " +
                "JOIN projects p ON ap.assignProjectId = p.assignProjectId " +
                "JOIN users u ON r.supervisorId = u.userId " +
                "WHERE r.supervisorId = ? ORDER BY r.reportDate DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supervisorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DailyReport(
                        rs.getInt("reportId"),
                        rs.getString("projectInstanceName"),
                        rs.getDate("reportDate").toLocalDate(),
                        rs.getString("issues"),
                        rs.getString("userName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Manager အတွက် Report အားလုံးဆွဲထုတ်ခြင်း (အသစ်ထပ်ထည့်ရန်)
    public static ArrayList<DailyReport> getAllReportsForManager() {
        ArrayList<DailyReport> list = new ArrayList<>();
        String sql = "SELECT r.reportId, r.reportDate, r.issues, p.projectInstanceName, u.userName " +
                "FROM dailyReports r " +
                "JOIN assignProjects ap ON r.assignProjectId = ap.assignProjectId " +
                "JOIN projects p ON ap.assignProjectId = p.assignProjectId " +
                "JOIN users u ON r.supervisorId = u.userId " +
                "ORDER BY r.reportDate DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DailyReport(
                        rs.getInt("reportId"),
                        rs.getString("projectInstanceName"),
                        rs.getDate("reportDate").toLocalDate(),
                        rs.getString("issues"),
                        rs.getString("userName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Detail ကြည့်ဖို့ Report တစ်ခုတည်းဆွဲထုတ်ခြင်း
    public static DailyReport getReportById(int reportId) {
        String sql = "SELECT r.*, p.projectInstanceName, u.userName FROM dailyReports r " +
                "JOIN assignProjects ap ON r.assignProjectId = ap.assignProjectId " +
                "JOIN projects p ON ap.assignProjectId = p.assignProjectId " +
                "JOIN users u ON r.supervisorId = u.userId " +
                "WHERE r.reportId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DailyReport(
                        rs.getInt("reportId"),
                        rs.getInt("assignProjectId"),
                        rs.getString("projectInstanceName"),
                        rs.getDate("reportDate").toLocalDate(),
                        rs.getString("weatherType"),
                        rs.getString("workAffect"),
                        rs.getString("weatherNote"),
                        rs.getString("issues"),
                        rs.getString("comments"),
                        rs.getString("userName")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Project List ကို ဆွဲထုတ်ခြင်း (Create Report အတွက်)
    public static ArrayList<String> getProjectListForSupervisor(int supervisorId) {
        ArrayList<String> projectList = new ArrayList<>();
        String sql = "SELECT p.projectInstanceName FROM assignProjects ap " +
                "JOIN projects p ON ap.assignProjectId = p.assignProjectId " +
                "WHERE ap.supervisorId = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supervisorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                projectList.add(rs.getString("projectInstanceName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projectList;
    }

    // ✅ Project ID ရှာဖို့
    public static int getProjectIdByName(String projectName, int supervisorId) {
        String sql = "SELECT ap.assignProjectId FROM assignProjects ap " +
                "JOIN projects p ON ap.assignProjectId = p.assignProjectId " +
                "WHERE p.projectInstanceName = ? AND ap.supervisorId = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, projectName);
            ps.setInt(2, supervisorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("assignProjectId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ======================== NEW METHODS FOR SUPERVISOR REPORT ========================

    // ✅ Supervisor အတွက် Filter စစ်ထားသော Reports များဆွဲထုတ်ခြင်း
    public static ArrayList<DailyReport> getFilteredReports(int supervisorId, int projectId, Date startDate, Date endDate) {
        ArrayList<DailyReport> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT r.reportId, r.reportDate, r.issues, p.projectInstanceName, u.userName " +
                        "FROM dailyReports r " +
                        "JOIN assignProjects ap ON r.assignProjectId = ap.assignProjectId " +
                        "JOIN projects p ON ap.assignProjectId = p.assignProjectId " +
                        "JOIN users u ON r.supervisorId = u.userId " +
                        "WHERE r.supervisorId = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(supervisorId);

        if (projectId > 0) {
            sql.append("AND ap.assignProjectId = ? ");
            params.add(projectId);
        }

        if (startDate != null) {
            sql.append("AND r.reportDate >= ? ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append("AND r.reportDate <= ? ");
            params.add(endDate);
        }

        sql.append("ORDER BY r.reportDate DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DailyReport(
                        rs.getInt("reportId"),
                        rs.getString("projectInstanceName"),
                        rs.getDate("reportDate").toLocalDate(),
                        rs.getString("issues"),
                        rs.getString("userName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Project Status အလိုက် Project List ဆွဲထုတ်ခြင်း
    public static ArrayList<projects> getProjectsBySupervisorAndStatus(int supervisorId, String status) {
        ArrayList<projects> list = new ArrayList<>();

        String sql = "SELECT ap.assignProjectId, p.projectInstanceName, pt.typeName, " +
                "ps.projectStatusName, ap.startDate, ap.endDate " +
                "FROM assignProjects ap " +
                "JOIN projects p ON ap.assignProjectId = p.assignProjectId " +
                "JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId " +
                "JOIN projectTypes pt ON ap.projectTypeId = pt.projectTypeId " +
                "WHERE ap.supervisorId = ? ";

        if (!status.equals("All Assigned")) {
            sql += "AND ps.projectStatusName = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supervisorId);

            if (!status.equals("All Assigned")) {
                ps.setString(2, status);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                projects p = new projects();
                p.setAssignProjectId(rs.getInt("assignProjectId"));
                p.setProjectInstanceName(rs.getString("projectInstanceName"));
                p.setProjectTypeName(rs.getString("typeName"));
                p.setProjectStatus(rs.getString("projectStatusName"));
                p.setStartDate(rs.getDate("startDate"));
                p.setEndDate(rs.getDate("endDate"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Report အရေအတွက်တွက်ချက်ခြင်း
    public static Map<String, Integer> getReportStatistics(int supervisorId, int projectId, Date startDate, Date endDate) {
        Map<String, Integer> stats = new HashMap<>();

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) as total, " +
                        "SUM(CASE WHEN r.issues IS NOT NULL AND r.issues != '' THEN 1 ELSE 0 END) as withIssues " +
                        "FROM dailyReports r " +
                        "WHERE r.supervisorId = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(supervisorId);

        if (projectId > 0) {
            sql.append("AND r.assignProjectId = ? ");
            params.add(projectId);
        }

        if (startDate != null) {
            sql.append("AND r.reportDate >= ? ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append("AND r.reportDate <= ? ");
            params.add(endDate);
        }

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("withIssues", rs.getInt("withIssues"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}