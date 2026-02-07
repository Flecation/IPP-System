package IPPSystem.DAO;

import IPPSystem.Models.skills;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class skillDatabase  {

    private static Connection con;
    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObservableList<skills> getAllSkills(){
        ObservableList<skills> skill = FXCollections.observableArrayList();


        try {
            PreparedStatement pstmt = con.prepareCall("SELECT * FROM skills");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                skill.add(new skills(
                        rs.getInt("skillId"),
                        rs.getString("skillName")
                ));
            }
            return skill;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static ObservableList<skills> getSkillByWorkItem(int projectTypeId, int workItemId){
        ObservableList<skills> skill = FXCollections.observableArrayList();
        try(CallableStatement cs = con.prepareCall("{CALL getSkillByWorkItem(?,?)}")){
            cs.setInt(1,projectTypeId);
            cs.setInt(2,workItemId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()){
                skill.add(new skills(
                        rs.getInt("skillId"),
                        rs.getString("skillName"),
                        rs.getDouble("minRequireLabors"),
                        rs.getDouble("maxRequireLabors"),
                        rs.getDouble("minDailyWage"),
                        rs.getDouble("maxDailyWage")
                ));
            }
            return skill;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


//    public static List<skills> getSkillsByWorkItem(int workItemId) {
//
//        List<skills> list = new ArrayList<>();
//
//        String sql = """
//            SELECT DISTINCT s.skillId, s.skillName
//            FROM assignworkitemskilldetails aw
//            JOIN skills s ON aw.skillId = s.skillId
//            WHERE aw.workItemId = ?
//        """;
//
//        try (PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setInt(1, workItemId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new skills(
//                        rs.getInt("skillId"),
//                        rs.getString("skillName")
//                ));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }























    // Skills required by selected ASSIGN work item (from assignWorkItemSkills)
    public static List<skills> getSkillsByAssignWorkItem(int assignWorkItemId) {
        List<skills> list = new ArrayList<>();

        String sql = """
            SELECT s.skillId, s.skillName
            FROM assignWorkItemSkills aws
            JOIN skills s ON s.skillId = aws.skillId
            WHERE aws.assignWorkItemId = ?
              AND aws.isCancel = 0
            ORDER BY s.skillName
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, assignWorkItemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills sk = new skills();
                    sk.setSkillId(rs.getInt("skillId"));
                    sk.setSkillName(rs.getString("skillName"));
                    list.add(sk);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

//
//    public static List<String> getRequiredSkillsByAssignProject(int assignProjectId) {
//        List<String> skills = new ArrayList<>();
//
//        String sql =
//                "SELECT DISTINCT s.skillName " +
//                        "FROM skills s " +
//                        "JOIN assignWorkItemSkills awis ON s.skillId = awis.skillId " +
//                        "JOIN assignWorkItems awi ON awis.assignWorkItemId = awi.assignWorkItemId " +
//                        "WHERE awi.assignProjectId = ? " +
//                        "AND awis.isCancel = false";
//
//        try (PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setInt(1, assignProjectId);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    skills.add(rs.getString("skillName"));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return skills;
//    }



}
