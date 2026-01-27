package IPPSystem.Utils;

import IPPSystem.DAO.database;
import IPPSystem.Models.labors;
import IPPSystem.Models.projects;
import IPPSystem.Models.skills;
import IPPSystem.Models.users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class storage {
    private static final   storage instance = new storage();
    private final ObservableList<users> allUsers = FXCollections.observableArrayList();
    private final ObservableList<projects> allProjects = FXCollections.observableArrayList();
    private final HashMap<Integer,String> projectTypes = new HashMap<>();
    private final ObservableList<labors> allLabors = FXCollections.observableArrayList();
    private final  ObservableList<skills> allSkills = FXCollections.observableArrayList();
//    private final
    private storage(){
        allUsers.setAll(database.getAllUsers());
        allProjects.setAll(database.getAllProjects());
        projectTypes.putAll(database.getAllProjectTypes());
        allLabors.setAll(database.getAllLabors());
        allSkills.setAll(database.getAllSkill());
    }
    public static storage getInstance(){
        return instance;
    }

    public void reload(){
        allUsers.removeAll();
        allProjects.removeAll();
        projectTypes.clear();
        allLabors.removeAll();
        allSkills.removeAll();
        allUsers.setAll(database.getAllUsers());
        allProjects.setAll(database.getAllProjects());
        projectTypes.putAll(database.getAllProjectTypes());
        allLabors.setAll(database.getAllLabors());
        allSkills.setAll(database.getAllSkill());
    }

    public ObservableList<users> getAllUsers() {
        return allUsers;
    }

    public ObservableList<projects> getAllProjects() {
        return allProjects;
    }

    public HashMap<Integer, String> getProjectTypes() {
        return projectTypes;
    }

    public ObservableList<skills> getAllSkills() {
        return allSkills;
    }

    public ObservableList<labors> getAllLabors() {
        return allLabors;
    }
}
