package IPPSystem.DAO;

import IPPSystem.Models.DailyReport;
import IPPSystem.Models.ReportLabor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class reportDatabase {

    private static Connection con;
    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
}