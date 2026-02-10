package IPPSystem.DAO;

import IPPSystem.Models.users;
import IPPSystem.Utils.dateFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//import static IPPSystem.Controllers.loginController.user;


public class userDatabase {

    private static Connection con;

    private static String userName,userEmail,userPhone,userPassword,userRole,userPhoto,userAddress;
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
                userPhoto = rs.getString("userPhoto");
                userAddress = rs.getString("userAddress");

                info = new users(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword,userPhoto,userAddress);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return info;
    }

    public static ObservableList<users> getAllUser(){
        ObservableList<users> ls = FXCollections.observableArrayList();
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
                userPhoto = rs.getString("userPhoto");
                //  userAddress = rs.getString("userAddress");

                users users = new users(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword,userPhoto,userAddress);
                ls.add(users);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ls;
    }

    public static ObservableList<users> getUserByRole(String role){
        ObservableList<users> info = FXCollections.observableArrayList();
        try {

            PreparedStatement pstmt = con.prepareCall("SELECT * FROM users WHERE userRole = ? ORDER BY isActive DESC, userEndDate DESC ");
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
                userPhoto = rs.getString("userPhoto");
                userAddress = rs.getString("userAddress");

                info.add( new users(userId,userName,userEmail,userPhone,userRole,userDOB,userStartDate,userEndDate,isActive,userPassword,userPhoto,userAddress));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return info;
    }

    public static users login(String userEmail){
        users users = new users();

        try {
            PreparedStatement cstmt = con.prepareStatement("SELECT * FROM users WHERE userEmail = ?");
            cstmt.setString(1, userEmail);
            ResultSet rs = cstmt.executeQuery();

            if(rs.next()){
                users = new users(
                        rs.getInt("userId"),
                        rs.getString("userEmail"),
                        rs.getString("userPhone"),
                        rs.getString("userRole"),
                        rs.getDate("userDOB"),
                        rs.getDate("userStartDate"),
                        rs.getDate("userEndDate"),
                        rs.getBoolean("isActive"),
                        rs.getString("userPassword"),
                        rs.getString("userPhoto"),
                        rs.getString("userAddress")

                );
            }else {
                return null;
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean addUser(users user){

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "INSERT INTO users (userName,userRole,userPhone," +
                            "userEmail,userDOB,userPassword,userStartDate,userPhoto)" +
                            " values (?,?,?,?,?,?,?,?)"
            );
            pstmt.setString(1,user.getUserName());
            pstmt.setString(2,user.getUserRole());
            pstmt.setString(3,user.getUserPhone());
            pstmt.setString(4,user.getUserEmail());
            pstmt.setDate(5, (Date) user.getUserDOB());
            pstmt.setString(6,user.getUserPassword());
            pstmt.setDate(7, (Date) user.getUserStartDate());
            pstmt.setString(8,user.getUserPhoto());
            boolean rs = pstmt.execute();
            return rs;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean delete(int id){
        try {
            PreparedStatement pstmt = con.prepareCall("UPDATE users " +
                    "SET isActive = FALSE, " +
                    " userEndDate = ? " +
                    "WHERE userId = ? ;");
            pstmt.setDate(1, dateFormatter.today());
            pstmt.setInt(2,id);
            return  pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    
public static boolean updatePasswordByEmail(String email, String hashedPassword) {
    try {
        PreparedStatement pstmt = con.prepareStatement(
                "UPDATE users SET userPassword = ? WHERE userEmail = ?"
        );
        pstmt.setString(1, hashedPassword);
        pstmt.setString(2, email);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}


/**
 * Update basic profile fields for a user.
 * Used by the sidebar "Edit Profile" feature.
 */
public static boolean updateProfileByUserId(int userId, String email, String phone) {
    try {
        PreparedStatement pstmt = con.prepareStatement(
                "UPDATE users SET userEmail = ?, userPhone = ? WHERE userId = ?"
        );
        pstmt.setString(1, email);
        pstmt.setString(2, phone);
        pstmt.setInt(3, userId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}


public static boolean createSupervisor(users u) {
        String sql = """
            INSERT INTO users
            (userName, userRole, userPhone, userEmail, userDOB, userPassword, userPhoto, userStartDate)
            VALUES (?, 'supervisor', ?, ?, ?, ?, ?, CURDATE())
        """;


        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUserName());
            ps.setString(2, u.getUserPhone());
            ps.setString(3, u.getUserEmail());
            ps.setDate(4, (Date) u.getUserDOB());
            ps.setString(5, u.getUserPassword()); // default password
            ps.setString(6, u.getUserPhoto());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }






    /**
     * UnResign / Reactivate user (set isActive = TRUE and clear userEndDate)
     */
    public static boolean reactivate(int id){
        try {
            PreparedStatement pstmt = con.prepareCall(
                    "UPDATE users SET isActive = TRUE, userEndDate = NULL WHERE userId = ?;"
            );
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
