package IPPSystem.DAO;

import IPPSystem.Models.labors;
import IPPSystem.Utils.dateFormatter;

import java.sql.*;
import java.util.ArrayList;

public class laborDatabase {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // FIXED: Changed from stored procedure to direct SQL query
    public static ArrayList<labors> getAllLabors(){
        ArrayList<labors> labors = new ArrayList<>();
        try{
            // Direct query instead of stored procedure to avoid error
            String sql = "SELECT l.laborId, l.laborName, l.laborNRC, l.laborPhone, " +
                    "l.laborStartDate, l.laborEndDate, s.skillName " +
                    "FROM labors l " +
                    "LEFT JOIN skills s ON l.skillId = s.skillId " +
                    "WHERE l.isActive = 1";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
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
            }
            return labors;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ADDED: New method for direct labor query without stored procedure
    public static ArrayList<labors> getAllLaborsDirect(){
        return getAllLabors(); // Use the fixed method above
    }

    // UNCHANGED: Original method
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

    // UNCHANGED: Original method
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

    // UNCHANGED: Original method (uses stored procedure that works)
    public static ArrayList<labors> getAllLaborsWithinProject(int assignProjectId){
        ArrayList<labors> labors = new ArrayList<>();
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

    // UNCHANGED: Original method (fixed to include skillName join)
    public static ArrayList<labors> getAllLaborsBySkill(int skillId){
        ArrayList<labors> labor = new ArrayList<>();
        String sql = "SELECT l.*, s.skillName " +
                "FROM labors l " +
                "LEFT JOIN skills s ON l.skillId = s.skillId " +
                "WHERE l.skillId = ? AND l.isActive = 1";
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
}