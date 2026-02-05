package IPPSystem.DAO;

import IPPSystem.Models.labors;
import IPPSystem.Utils.dateFormatter;
import com.mysql.cj.protocol.Resultset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class laborDatabase {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObservableList<labors> getAllLabors(){
        ObservableList<labors> labors = FXCollections.observableArrayList();
        try{
            CallableStatement cs = con.prepareCall("{CALL getAllLabors()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()){
                labors.add(
                        new labors(
                                rs.getInt("laborId"),
                                rs.getString("skillName"),
                                rs.getString("laborName"),
                                rs.getString("laborNRC"),
                                rs.getString("laborPhone"),
                                rs.getDate("laborStartDate"),
                                rs.getDate("laborEndDate")
                        )
                );
            }
            return labors;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean addLabor(labors labors){
        String sql = "INSERT INTO labors " +
                "(skillId, laborName, laborNRC, laborPhone, laborStartDate) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement cs = con.prepareCall(sql)){
            cs.setInt(1,labors.getSkillId());
            cs.setString(2,labors.getLaborName());
            cs.setString(3,labors.getLaborNRC());
            cs.setString(4,labors.getLaborPhone());
            cs.setDate(5,(Date) labors.getLaborStartDate());
            int rowsAffected = cs.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean deleteLabor(int laborId)  {
        String sql = "UPDATE labors SET isActive = false," +
                "laborEndDate = ?  WHERE laborId = ? ";
        try(PreparedStatement ps = con.prepareCall(sql)){
            ps.setDate(1, dateFormatter.today());
            ps.setInt(2,laborId);
            return ps.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public static ObservableList<labors> getAllLaborsWithinProject(int assignProjectId){
        ObservableList<labors> labors = FXCollections.observableArrayList();
        try{
            CallableStatement cs = con.prepareCall("{CALL getAllLaborsByProjectId(?);}");
            cs.setInt(1,assignProjectId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()){
                labors.add(
                        new labors(
                                rs.getInt("laborId"),
                                rs.getString("skillName"),
                                rs.getString("laborName"),
                                rs.getString("laborNRC"),
                                rs.getString("laborPhone"),
                                rs.getDate("laborStartDate"),
                                rs.getDate("laborEndDate")
                        )
                );
            }
            return labors;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ObservableList<labors> getAllLaborsBySkill(int skillId){
        ObservableList<labors> labor = FXCollections.observableArrayList();
        String sql = "SELECT *" +
                "FROM labors l WHERE l.skillId = ?";
        try(PreparedStatement ps = con.prepareCall(sql)){
            ps.setInt(1,skillId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                labor.add(new labors(
                        rs.getInt("laborId"),
                        rs.getString("skillName"),
                        rs.getString("laborName"),
                        rs.getString("laborNRC"),
                        rs.getString("laborPhone"),
                        rs.getDate("laborStartDate"),
                        rs.getDate("laborEndDate")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return labor;
    }




    public static String getAssignedProjectName(int laborId) {
        String sql = "SELECT ap.projectInstanceName FROM assignProjects ap " +
                "JOIN assignWorkers aw ON ap.assignProjectId = aw.assignProjectId " +
                "WHERE aw.workerId = ? AND aw.isCancel = false LIMIT 1";

        String projectName = "Not Assigned"; // Default value if no project is found

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, laborId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    projectName = rs.getString("projectInstanceName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projectName;
    }

    public static int getSkillIdByName(String skillName) {
        String sql = "SELECT skillId FROM skills WHERE skillName = ?";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, skillName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("skillId");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

//    public static List<labors> getAllLaborsSortedByAssignment() {
//        List<labors> list = new ArrayList<>();
//
//        String sql = "SELECT l.*, s.skillName, ap.projectInstanceName " +
//                "FROM labors l " +
//                "LEFT JOIN skills s ON l.skillId = s.skillId " +
//                "LEFT JOIN assignWorkers aw ON l.laborId = aw.workerId AND aw.isCancel = false " +
//                "LEFT JOIN assignProjects ap ON aw.assignProjectId = ap.assignProjectId " +
//                "ORDER BY (ap.projectInstanceName IS NOT NULL) DESC, l.laborName ASC";
//
//        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
//            while (rs.next()) {
//                labors labor = new labors();
//                labor.setLaborId(rs.getInt("laborId"));
//                labor.setLaborName(rs.getString("laborName"));
//                labor.setLaborNRC(rs.getString("laborNRC"));
//                labor.setLaborPhone(rs.getString("laborPhone"));
//                labor.setLaborStartDate(rs.getDate("laborStartDate"));
//                labor.setLaborEndDate(rs.getDate("laborEndDate"));
//                labor.setActive(rs.getBoolean("isActive"));
//
//                // MUST set skill
//                labor.setSkillId(rs.getInt("skillId"));
//                labor.setSkillName(rs.getString("skillName")); // important!
//
//                list.add(labor);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }


    public static boolean resignLabor(int laborId) {
        String sql = "UPDATE labors SET isActive = 0, laborEndDate = CURDATE() WHERE laborId = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, laborId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    public static List<String> getAllSkills() {
        List<String> list = new ArrayList<>();
        // Added a WHERE clause to avoid adding "null" to your filter list
        String sql = "SELECT DISTINCT s.skillName " +
                "FROM skills s " +
                "JOIN labors l ON s.skillId = l.skillId " +
                "WHERE s.skillName IS NOT NULL";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("skillName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<labors> getAllLaborsSortedByAssignment() {
        List<labors> list = new ArrayList<>();
        String sql = "SELECT l.*, s.skillName, ap.projectInstanceName " +
                "FROM labors l " +
                "LEFT JOIN skills s ON l.skillId = s.skillId " +
                "LEFT JOIN assignWorkers aw ON l.laborId = aw.workerId AND aw.isCancel = false " +
                "LEFT JOIN assignProjects ap ON aw.assignProjectId = ap.assignProjectId " +
                "ORDER BY " +
                "  (ap.projectInstanceName IS NOT NULL) DESC, " +
                "  l.isActive DESC, " +
                "  l.laborName ASC";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                labors labor = new labors();
                labor.setLaborId(rs.getInt("laborId"));
                labor.setLaborName(rs.getString("laborName"));
                labor.setLaborNRC(rs.getString("laborNRC"));
                labor.setLaborPhone(rs.getString("laborPhone"));
                labor.setLaborStartDate(rs.getDate("laborStartDate"));
                labor.setLaborEndDate(rs.getDate("laborEndDate"));
                labor.setActive(rs.getBoolean("isActive"));
                labor.setSkillId(rs.getInt("skillId"));
                labor.setSkillName(rs.getString("skillName"));
                list.add(labor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



//    public static List<labors> getAllLaborsSortedByAssignment() {
//        List<labors> list = new ArrayList<>();
//        String sql = "SELECT l.*, s.skillName, ap.projectInstanceName " +
//                "FROM labors l " +
//                "LEFT JOIN skills s ON l.skillId = s.skillId " +
//                "LEFT JOIN assignWorkers aw ON l.laborId = aw.workerId AND aw.isCancel = false " +
//                "LEFT JOIN assignProjects ap ON aw.assignProjectId = ap.assignProjectId " +
//                "ORDER BY (ap.projectInstanceName IS NOT NULL) DESC, l.isActive DESC, l.laborName ASC";
//
//        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
//            while (rs.next()) {
//                labors labor = new labors();
//                labor.setLaborId(rs.getInt("laborId"));
//                labor.setLaborName(rs.getString("laborName"));
//                labor.setActive(rs.getBoolean("isActive"));
//                labor.setSkillName(rs.getString("skillName"));
//                // Ensure you are using java.sql.Date for this to work with .toLocalDate()
//                labor.setLaborStartDate(rs.getDate("laborStartDate"));
//                list.add(labor);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }


}
