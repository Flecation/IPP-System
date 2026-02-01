package IPPSystem.DAO;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.projectStatus;
import IPPSystem.Constants.role;
import IPPSystem.Models.*;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;

public class database {

//    for the users database ( manager / supervisors ) of useful get function

    public static ObservableList<users> getAllUsers(){return  userDatabase.getAllUser();}

    public static ObservableList<users> getAllSupervisors(){return userDatabase.getUserByRole(role.SUPERVISOR.toString());}

    public static users loginUser(String userName){return userDatabase.login(userName);}

    public static users getUserById(int userId){return userDatabase.getUserByUserId(userId);}

    public static boolean deleteUser(int userId){return userDatabase.delete(userId);}

    public static boolean createUser(users users){return userDatabase.addUser(users);}


//    For the labors functions

    public static ObservableList<labors> getAllLabors(){return laborDatabase.getAllLabors();}

    public static ObservableList<labors> getAllLaborsByProjectId(int assignProjectId){return laborDatabase.getAllLaborsWithinProject(assignProjectId);}

    public static boolean createLabor(labors labor){return laborDatabase.addLabor(labor);}

    public static boolean deleteLabor(int laborId){return laborDatabase.deleteLabor(laborId);}

    public static ObservableList<labors> getAllLaborsBySkillId(int skillId){return laborDatabase.getAllLaborsBySkill(skillId);}

    public static int getTotalLabors() {return calculationDatabase.getTotalLaborsCount();}

    public static int getNewHires() {return calculationDatabase.getNewHiresThisMonth();}

    public static int getActiveLabors() {return calculationDatabase.getActiveLaborsCount();}

    public static int getResignedLaborsCount(){return calculationDatabase.getResignedLaborsCount();}

    public static String getAssignedProjectName(int laborId){return laborDatabase.getAssignedProjectName(laborId);}

    public  static List<labors> getAllLaborsSortedByAssignment(){return laborDatabase.getAllLaborsSortedByAssignment();}

    public static boolean resignLabor(int laborId){return  laborDatabase.resignLabor(laborId);}
    public  static  List<String> getAllSkills(){return laborDatabase.getAllSkills();}
    
    
//    For the project types
    public static HashMap<Integer,String> getAllProjectTypes(){return otherRequireDatabase.getAllProjectType();}

//    For the project building
    public static HashMap<Integer,String> getAllBuildings(){return otherRequireDatabase.getAllBuilding();}

    public static HashMap<Integer,String> getAllBuildingByProjectTypeId(int projectTypeId){return otherRequireDatabase.getBuildingNameByProjectId(projectTypeId);}

//    For the project levels
    public static HashMap<Integer,String> getAllLevels(){return otherRequireDatabase.getAllLevel();}

    public static HashMap<Integer,String> getAllLevelByProjectTypeId(int projectTypeId){return otherRequireDatabase.getLevelByProjectId(projectTypeId);}

//    For the Project skills

    public static ObservableList<skills> getAllSkill(){return skillDatabase.getAllSkills();}

    public static ObservableList<skills> getSkillDetails(int projectTypeId,int workItemId){return skillDatabase.getSkillByWorkItem(projectTypeId,workItemId);}

//    For the project tasks

    public static boolean editAssignTasks(tasks task, assignStatus status){return taskDatabase.addTaskDetailRecord(task,status);}

    public static boolean setAssignTaskToWorkItem(tasks assign, projectStatus projectStatus, assignStatus assignStatus){return taskDatabase.assignTasks(assign,projectStatus,assignStatus);}

    public static ObservableList<tasks> getAllTasksByAssignWorkItem(int assignWorkItemId){return taskDatabase.getAllTasksByAssignWorkItem(assignWorkItemId);}

    public static ObservableList<tasks> getAllTasksForAutoGeneration(int projectTypeId,int workItemId,int buildingId,int levelId){return taskDatabase.getAllTasksDetailsByWorkItem(projectTypeId,workItemId,buildingId,levelId);}

    public static boolean cancelAssignTask(int assignTaskId){return taskDatabase.deleteTask(assignTaskId);}

//    For the WorkItems functions

    public static ObservableList<workItems> getAllWorkItemsForAutoGeneration(int projectTypeId,int buildingId, int levelId){return workItemDatabase.getAllWorkItemDetails(projectTypeId,buildingId,levelId);}

    public static ObservableList<workItems> getAllWorkItemsByAssignProject(int assignProjectId){return workItemDatabase.getAllWorkItemByAssignProjectId(assignProjectId);}

    public static boolean setAssignWorkItems(workItems assignWorkItem,projectStatus projectStatus,assignStatus assignStatus){return workItemDatabase.assignWorkItems(assignWorkItem,projectStatus,assignStatus);}

    public static boolean deleteWorkItem(int assignWorkItemId){return workItemDatabase.deleteWorkItem(assignWorkItemId);}

    public static boolean deleteSkillFromWorkItem(int assignWorkItemId,int skillId){return workItemDatabase.deleteSkillFromWorkItem(assignWorkItemId,skillId);}

    public static ObservableList<skills> getAllSkillByAssignWorkItemDetails(int assignWorkItemId){return workItemDatabase.getAllSkillDetailsByAssignWorkItem(assignWorkItemId);}

    public static boolean setSkillsToWorkItem(skills skill,assignStatus status){return workItemDatabase.addSkillToWorkItem(skill,status);}

//    For the project functions

    public static boolean deleteAssignProject(int assignProjectId){return projectDatabase.deleteAssignProject(assignProjectId);}

    public static boolean setAssignProject(projects assignProject,projectStatus projectStatus,assignStatus assignStatus){return projectDatabase.assignProjects(assignProject,projectStatus,assignStatus);}

    public static boolean updateAssignProject(projects updateAssignProject,assignStatus status){return projectDatabase.updateAssignProject(updateAssignProject,status);}

    public static ObservableList<projects> getAllProjectDetails(int projectTypeId){return projectDatabase.getProjectDetails(projectTypeId);}

    public static ObservableList<projects> getAllProjects(){return projectDatabase.getAllProjects();}

    public  static  String currentAssignProject(int userId){return  projectDatabase.currentAssignProject(userId);}

    public  static List<projects> getProjectsByEngineer(int engineerId){return projectDatabase.getProjectsByEngineer(engineerId     );}


//    For the Status Require functions
    public static HashMap<Integer,String> getAllAssignStatus(){return otherRequireDatabase.getAssignStatus();}

    public static HashMap<Integer,String> getAllProjectStatus(){return otherRequireDatabase.getProjectStatus();}


    //    For project detail
    
    // Current Project Workload
    public static double getWorkload(int userId) {
        return calculationDatabase.getWorkload(userId);
    }

        // Historical Performance
    public static double getPerformance(int userId) {
        return calculationDatabase.getHistoryPerformance(userId);
    }
}
