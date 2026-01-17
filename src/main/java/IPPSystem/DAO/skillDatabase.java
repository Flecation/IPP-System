package IPPSystem.DAO;

import IPPSystem.Models.skills;

import java.sql.*;
import java.util.ArrayList;

public class skillDatabase  {

    private static Connection con;
    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<skills> getAllSkills(){
        ArrayList<skills> skill = new ArrayList<>();


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

    public static ArrayList<skills> getSkillByWorkItemId(int workItemId){
        ArrayList<skills> skill = new ArrayList<>();
        try(CallableStatement cs = con.prepareCall("{CALL getSkillByWorkItemId(?);")){
            cs.setInt(1,workItemId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()){
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


}
