package IPPSystem.Utils;

import IPPSystem.DAO.database;
import IPPSystem.Models.labors;
import IPPSystem.Models.projects;
import IPPSystem.Models.skills;
import IPPSystem.Models.users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class storage {
    private static final   storage instance = new storage();
    private final ObservableList<users> allUsers = FXCollections.observableArrayList();
    private final ObservableList<projects> allProjects = FXCollections.observableArrayList();
    private final HashMap<Integer,String> projectTypes = new HashMap<>();
    private final ObservableList<labors> allLabors = FXCollections.observableArrayList();
    private final  ObservableList<skills> allSkills = FXCollections.observableArrayList();
    private final ObservableList<projects> allProjectsById = FXCollections.observableArrayList();
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
        allUsers.clear();
        allProjects.clear();
        projectTypes.clear();
        allLabors.clear();
        allSkills.clear();
        allUsers.setAll(database.getAllUsers());
        allProjects.setAll(database.getAllProjects());
        projectTypes.putAll(database.getAllProjectTypes());
        allLabors.setAll(database.getAllLabors());
        allSkills.setAll(database.getAllSkill());
    }

    public ObservableList<projects> getProjectsByUserId(int id){
        allProjectsById.clear();
        for (projects p : getAllProjects()){
            if (p.getUserId()== id){
                allProjectsById.add(p);
            }
        }
        return allProjectsById;
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

    private static final String PROFILE_DIR = "/Photos/";


    public static String saveProfileImage(File source) {

        try {
            Files.createDirectories(Path.of(PROFILE_DIR));

            String fileName = "profile_" + System.currentTimeMillis() + ".png";
            Path target = Path.of(PROFILE_DIR + fileName);

            Files.copy(
                    source.toPath(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return target.toString(); // save this to DB

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
