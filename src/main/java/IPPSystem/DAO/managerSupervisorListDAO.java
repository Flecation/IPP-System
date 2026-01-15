package IPPSystem.DAO;

import IPPSystem.Models.managerSupervisorListModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class managerSupervisorListDAO {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<managerSupervisorListModel> getAllSupervisors(){
        List<managerSupervisorListModel> supervisorList = new ArrayList<>();
        String sql = "select userName,userRole,isActive from users";
        try(
                Connection con = databaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()

        ){
            while (rs.next()){
                managerSupervisorListModel supervisors = new managerSupervisorListModel(
                        rs.getString("userName"),
                        rs.getString("userRole"),
                        rs.getBoolean("isActive")
                );
                supervisorList.add(supervisors);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return supervisorList;
    }
}
