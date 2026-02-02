package IPPSystem.DAO;

import IPPSystem.Models.skills;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

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


}
