package IPPSystem.DAO;

import IPPSystem.Models.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userDatabase {

    private static Connection con;

        private static String userName,userEmail,userPhone,userPassword,userRole;
        private static int userId;
        private static boolean isActive;
        private static java.util.Date userDOB,userStartDate,userEndDate;

    static {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static user getUserByUserId(int id){
        user info = new user();
        try {
            PreparedStatement pstmt = con.prepareCall("SELECT * FROM users WHERE userId = ?");
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                userName = rs.getString("userName");
                userRole = rs.getString("userRole");
                userEmail = rs.getString("userEmail");
                userPhone = rs.getString("userPhone");
                userDOB = rs.getDate("userDOB");
                userStartDate = rs.getDate("userStartDate");
                userEndDate = rs.getDate("userEndDate");
                isActive = rs.getBoolean("isActive");
                userPassword = rs.getString("userPassword");
                userId = rs.getInt("userId");
                info = new user(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return info;
    }

    public static List<user> getAllUser(){
        List<user> ls = new ArrayList<user>();
        try {
            PreparedStatement pstmt = con.prepareCall("SELECT * FROM users");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                userName = rs.getString("userName");
                userRole = rs.getString("userRole");
                userEmail = rs.getString("userEmail");
                userPhone = rs.getString("userPhone");
                userDOB = rs.getDate("userDOB");
                userStartDate = rs.getDate("userStartDate");
                userEndDate = rs.getDate("userEndDate");
                isActive = rs.getBoolean("isActive");
                userPassword = rs.getString("userPassword");
                userId = rs.getInt("userId");
                user user = new user(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword);
                ls.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    public static user getUserByRole(String role){
        user info = new user();
        try {

            PreparedStatement pstmt = con.prepareCall("SELECT * FROM users WHERE userRole = ?");
            pstmt.setString(1,role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                userName = rs.getString("userName");
                userRole = rs.getString("userRole");
                userEmail = rs.getString("userEmail");
                userPhone = rs.getString("userPhone");
                userDOB = rs.getDate("userDOB");
                userStartDate = rs.getDate("userStartDate");
                userEndDate = rs.getDate("userEndDate");
                isActive = rs.getBoolean("isActive");
                userPassword = rs.getString("userPassword");
                userId = rs.getInt("userId");
                info = new user(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return info;
    }


}
