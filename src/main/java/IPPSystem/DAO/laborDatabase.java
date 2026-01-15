package IPPSystem.DAO;

import IPPSystem.Models.labors;
import com.mysql.cj.protocol.Resultset;

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

    public static ArrayList<labors> getAllLabors(){
        ArrayList<labors> labors = new ArrayList<>();
        try{
            CallableStatement cs = con.prepareCall("");
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
        try{
            CallableStatement cs = con.prepareCall("");
            cs.setInt(1,labors.getSkillId());
            cs.setString(2,labors.getLaborName());
            cs.setString(3,labors.getLaborNRC());
            cs.setString(4,labors.getLaborPhone());
            cs.setDate(5,(Date) labors.getLaborStartDate());
            int rowsAffected = cs.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<labors> getAllLaborsWithinProject(int assignProjectId){
        ArrayList<labors> labors = new ArrayList<>();
        try{
            CallableStatement cs = con.prepareCall("");
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



}
