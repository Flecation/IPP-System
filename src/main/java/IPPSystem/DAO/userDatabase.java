package IPPSystem.DAO;

import IPPSystem.Models.users;

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

    public static users getUserByUserId(int id){
        users info = new users();
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
                info = new users(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return info;
    }

    public static List<users> getAllUser(){
        List<users> ls = new ArrayList<users>();
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
                users users = new users(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword);
                ls.add(users);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    public static users getUserByRole(String role){
        users info = new users();
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
                info = new users(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return info;
    }

    public static users loginUser(String userName, String userPassword){
        users users = new users();

        try {
            CallableStatement cstmt = con.prepareCall("");
            cstmt.setString(1,userName);
            cstmt.setString(2,userPassword);
            ResultSet rs = cstmt.executeQuery();
            if(rs.next()){
                users = new users(
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("userEmail"),
                        rs.getString("userPhone"),
                        rs.getString("userRole"),
                        rs.getDate("userDOB"),
                        rs.getDate("userStartDate"),
                        rs.getDate("userEndDate"),
                        rs.getBoolean("isActive"),
                        rs.getString("userPassword")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;

    }

}
