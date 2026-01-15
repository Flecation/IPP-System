package IPPSystem.DAO;

import IPPSystem.Constants.role;
import IPPSystem.Models.labors;
import IPPSystem.Models.skills;
import IPPSystem.Models.users;

import java.util.ArrayList;
import java.util.Collection;

public class database {

//    for the users database ( manager / supervisors ) of useful get function

    public static ArrayList<users> getAllUsers(){return  userDatabase.getAllUser();}

    public static ArrayList<users> getAllSupervisors(){return userDatabase.getUserByRole(role.SUPERVISOR.toString());}

    public static users loginUser(String userName , String password){return userDatabase.login(userName,password);}

    public static users getUserById(int userId){return userDatabase.getUserByUserId(userId);}

    public static void deleteUser(int userId){

    }
//    For the labors functions

    public static ArrayList<labors> getAllLabors(){return laborDatabase.getAllLabors();}

    public static ArrayList<labors> getAllLaborsByProject(int assignProjectId){return laborDatabase.getAllLaborsWithinProject(assignProjectId);}

//    For the Projects functions

//    For the WorkItems functions

//    For the Tasks functions

//    For the

//    For the skill  functions

    public static ArrayList<skills> getAllSkill(){return skillDatabase.getAllSkills();}

//    public static ArrayList<>


}
