package IPPSystem.DAO;

import IPPSystem.Models.project;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class projectDatabase {
    private static Connection con;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<project> getAllProjects(){
        ArrayList<project> ls = new ArrayList<>();

        return ls;
    }
}
