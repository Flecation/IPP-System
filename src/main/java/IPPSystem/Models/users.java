package IPPSystem.Models;

import java.util.Date;

public class users {
    private String userName,userEmail,userPhone,userPassword,userRole;
    private int userId;
    private boolean isActive;
    private Date userDOB,userStartDate,userEndDate;

    public users(){}

    // to use when the users input
    public users(String userName, String userEmail, String userPhone, String userPassword, String userRole, Date userDOB, Date userStartDate) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.userRole = userRole;
        this.userDOB = userDOB;
        this.userStartDate = userStartDate;
    }


    public users(String userName, String userEmail, String userPhone, String userRole, Date userDOB, Date userStartDate, Date userEndDate, boolean isActive) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userRole = userRole;
        this.userDOB = userDOB;
        this.userStartDate = userStartDate;
        this.userEndDate = userEndDate;
        this.isActive = isActive;
    }

    // to get all users
    public users(int userId, String userName, String userEmail, String userPhone, String userRole, Date userDOB, Date userStartDate, Date userEndDate, boolean isActive, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userRole = userRole;
        this.userDOB = userDOB;
        this.userStartDate = userStartDate;
        this.userEndDate = userEndDate;
        this.isActive = isActive;
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Date getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(Date userDOB) {
        this.userDOB = userDOB;
    }

    public Date getUserStartDate() {
        return userStartDate;
    }

    public void setUserStartDate(Date userStartDate) {
        this.userStartDate = userStartDate;
    }

    public Date getUserEndDate() {
        return userEndDate;
    }

    public void setUserEndDate(Date userEndDate) {
        this.userEndDate = userEndDate;
    }
}