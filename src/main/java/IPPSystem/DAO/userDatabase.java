package IPPSystem.DAO;

import IPPSystem.Models.users;
import IPPSystem.Utils.passwordCrafting;
import IPPSystem.Utils.utils;

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
            PreparedStatement cstmt = con.prepareStatement("SELECT * FROM users WHERE userName = ?");
            cstmt.setString(1,userName);
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
            }else {
                return null;
            }
            if(utils.checkPassword(userPassword,users.getUserPassword())) return users;
            else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void addUser(users user){

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "INSERT INTO users (userName,userRole,userPhone," +
                    "userEmail,userDOB,userPassword,userStartDate)" +
                    " values (?,?,?,?,?,?,?)"
            );
            pstmt.setString(1,user.getUserName());
            pstmt.setString(2,user.getUserRole());
            pstmt.setString(3,user.getUserPhone());
            pstmt.setString(4,user.getUserEmail());
            pstmt.setDate(5, (Date) user.getUserDOB());
            pstmt.setString(6,user.getUserPassword());
            pstmt.setDate(7, (Date) user.getUserStartDate());
            pstmt.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
