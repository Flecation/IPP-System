package IPPSystem.DAO;

import IPPSystem.Constants.assignStatus;
import IPPSystem.Constants.role;
import IPPSystem.Models.labors;
import IPPSystem.Models.skills;
import IPPSystem.Models.tasks;
import IPPSystem.Models.users;

import java.util.ArrayList;
import java.util.Collection;
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

    public static ArrayList<skills> getSkillByWorkItemId(int workItemId){return skillDatabase.getSkillByWorkItemId(workItemId);}

//    For the project tasks

    public static boolean editAssignTasks(tasks task, assignStatus status){return taskDatabase.addTaskDetailRecord(task,status);}

    public static boolean createAssignTaskToWorkItem(tasks assign,assignStatus status){return taskDatabase.assignTasks(assign,status);}

    public static ArrayList<tasks> getAllTasksByAssignWorkItem(int assignWorkItemId){return taskDatabase.getAllTasksByAssignWorkItem(assignWorkItemId);}

    public static ArrayList<tasks> getAllTasksForAutoGeneration(int projectTypeId,int workItemId){return taskDatabase.getAllTasksDetailsByWorkItem(projectTypeId,workItemId);}

    public static boolean cancelAssignTask(int assignTaskId){return taskDatabase.deleteTask(assignTaskId);}

//    For the Projects functions

//    For the WorkItems functions

//    For the Status Require functions
    public static HashMap<Integer,String> getAllAssignStatus(){return otherRequireDatabase.getAssignStatus();}

    public static HashMap<Integer,String> getAllProjectStatus(){return otherRequireDatabase.getProjectStatus();}


}
