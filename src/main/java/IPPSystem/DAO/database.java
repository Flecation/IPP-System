package IPPSystem.DAO;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.projectStatus;
import IPPSystem.Constants.role;
import IPPSystem.Models.*;

import java.util.ArrayList;
import java.util.HashMap;

public class database {

//    for the users database ( manager / supervisors ) of useful get function

    public static ArrayList<users> getAllUsers(){return  userDatabase.getAllUser();}

    public static ArrayList<users> getAllSupervisors(){return userDatabase.getUserByRole(role.SUPERVISOR.toString());}

    public static users loginUser(String userName , String password){return userDatabase.login(userName,password);}

    public static users getUserById(int userId){return userDatabase.getUserByUserId(userId);}

    public static boolean deleteUser(int userId){return userDatabase.delete(userId);}

    public static boolean createUser(users users){return userDatabase.addUser(users);}

//    public static users getUserByProjectId(int projectId){return }

//    For the labors functions

    public static ArrayList<labors> getAllLabors(){return laborDatabase.getAllLabors();}

    public static ArrayList<labors> getAllLaborsByProjectId(int assignProjectId){return laborDatabase.getAllLaborsWithinProject(assignProjectId);}

    public static boolean createLabor(labors labor){return laborDatabase.addLabor(labor);}

    public static boolean deleteLabor(int laborId){return laborDatabase.deleteLabor(laborId);}

    public static ArrayList<labors> getAllLaborsBySkillId(int skillId){return laborDatabase.getAllLaborsBySkill(skillId);}

//    For the project types
    public static HashMap<Integer,String> getAllProjectTypes(){return otherRequireDatabase.getAllProjectType();}

//    For the project building
    public static HashMap<Integer,String> getAllBuildings(){return otherRequireDatabase.getAllBuilding();}

    public static HashMap<Integer,String> getAllBuildingByProjectTypeId(int projectTypeId){return otherRequireDatabase.getBuildingNameByProjectId(projectTypeId);}

//    For the project levels
    public static HashMap<Integer,String> getAllLevels(){return otherRequireDatabase.getAllLevel();}

    public static HashMap<Integer,String> getAllLevelByProjectTypeId(int projectTypeId){return otherRequireDatabase.getLevelByProjectId(projectTypeId);}

//    For the Project skills

    public static ArrayList<skills> getAllSkill(){return skillDatabase.getAllSkills();}

    public static ArrayList<skills> getSkillDetails(int projectTypeId,int workItemId){return skillDatabase.getSkillByWorkItem(projectTypeId,workItemId);}

//    For the project tasks

    public static boolean editAssignTasks(tasks task, assignStatus status){return taskDatabase.addTaskDetailRecord(task,status);}

    public static boolean setAssignTaskToWorkItem(tasks assign, projectStatus projectStatus, assignStatus assignStatus){return taskDatabase.assignTasks(assign,projectStatus,assignStatus);}

    public static ArrayList<tasks> getAllTasksByAssignWorkItem(int assignWorkItemId){return taskDatabase.getAllTasksByAssignWorkItem(assignWorkItemId);}

    public static ArrayList<tasks> getAllTasksForAutoGeneration(int projectTypeId,int workItemId){return taskDatabase.getAllTasksDetailsByWorkItem(projectTypeId,workItemId);}

    public static boolean cancelAssignTask(int assignTaskId){return taskDatabase.deleteTask(assignTaskId);}

//    For the WorkItems functions

    public static ArrayList<workItems> getAllWorkItemsForAutoGeneration(int projectTypeId,int buildingId, int levelId){return workItemDatabase.getAllWorkItemDetails(projectTypeId,buildingId,levelId);}

    public static ArrayList<workItems> getAllWorkItemsByAssignProject(int assignProjectId){return workItemDatabase.getAllWorkItemByAssignProjectId(assignProjectId);}

    public static boolean setAssignWorkItems(workItems assignWorkItem,projectStatus projectStatus,assignStatus assignStatus){return workItemDatabase.assignWorkItems(assignWorkItem,projectStatus,assignStatus);}

    public static boolean deleteWorkItem(int assignWorkItemId){return workItemDatabase.deleteWorkItem(assignWorkItemId);}

    public static boolean deleteSkillFromWorkItem(int assignWorkItemId,int skillId){return workItemDatabase.deleteSkillFromWorkItem(assignWorkItemId,skillId);}

    public static ArrayList<skills> getAllAssignWorkItemDetails(int assignWorkItemId){return workItemDatabase.getAllSkillDetailsByAssignWorkItem(assignWorkItemId);}

    public static boolean setSkillsToWorkItem(skills skill,assignStatus status){return workItemDatabase.addSkillToWorkItem(skill,status);}

//    For the project functions

    public static boolean deleteAssignProject(int assignProjectId){return projectDatabase.deleteAssignProject(assignProjectId);}



//    For the Status Require functions
    public static HashMap<Integer,String> getAllAssignStatus(){return otherRequireDatabase.getAssignStatus();}

    public static HashMap<Integer,String> getAllProjectStatus(){return otherRequireDatabase.getProjectStatus();}


}
