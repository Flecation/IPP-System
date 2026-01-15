package IPPSystem.Models;

import java.time.LocalDate;

public class managerSupervisorListModel {
    private Integer userId;
    private String userName;
    private String userRole;
    private String userPhone;
    private String userEmail;
    private LocalDate userDOB;
    private String userPassword;
    private LocalDate userStartDate;
    private LocalDate userEndDate;
    private boolean isActive;

    public managerSupervisorListModel(String name, String role, boolean isActive){
        this.userName = name;
        this.userRole = role;
        this.isActive = isActive;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(LocalDate userDOB) {
        this.userDOB = userDOB;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public LocalDate getUserStartDate() {
        return userStartDate;
    }

    public void setUserStartDate(LocalDate userStartDate) {
        this.userStartDate = userStartDate;
    }

    public LocalDate getUserEndDate() {
        return userEndDate;
    }

    public void setUserEndDate(LocalDate userEndDate) {
        this.userEndDate = userEndDate;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
