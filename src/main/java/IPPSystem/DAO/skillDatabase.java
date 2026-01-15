package IPPSystem.DAO;

import IPPSystem.Models.skills;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class skillDatabase {

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
}
