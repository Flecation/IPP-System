package IPPSystem.Controllers;

import IPPSystem.Models.projects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class viewProjectsController {

    @FXML
    private Button activeBtn;

    @FXML
    private Button addBtn;

    @FXML
    private Button allBtn;

    @FXML
    private Button completedBtn;

    @FXML
    private Button planningBtn;

    @FXML
    private VBox projectContainer;

    @FXML
    public void initialize(){
        allBtn.setOnAction(e -> loadProjects(getDummyProjects()));
        completedBtn.setOnAction(e -> loadProjectsByStatusDummy("Completed"));
        activeBtn.setOnAction(e -> loadProjectsByStatusDummy("Active"));
        planningBtn.setOnAction( e -> loadProjectsByStatusDummy("Planning"));

        loadProjects(getDummyProjects());


    }

    private ArrayList<projects> getDummyProjects(){
        ArrayList<projects> list = new ArrayList<>();

        list.add(new projects("Project A", "Office", "Building 1", "Level 2", "John Doe", 1200, 15, 5, 50, 24, 500000, 100, 20000, java.sql.Date.valueOf("2025-01-01"), java.sql.Date.valueOf("2026-01-01"), "Yangon", "Active"));
        list.add(new projects("Project B", "Residential", "Building 2", "Level 5", "Jane Smith", 800, 10, 4, 30, 18, 300000, 60, 15000, java.sql.Date.valueOf("2024-06-01"), java.sql.Date.valueOf("2025-06-01"), "Mandalay", "Completed"));
        list.add(new projects("Project C", "Commercial", "Building 3", "Level 3", "Alice", 1500, 20, 6, 70, 36, 700000, 120, 30000, java.sql.Date.valueOf("2025-03-01"), java.sql.Date.valueOf("2026-03-01"), "Yangon", "Planning"));
        list.add(new projects("Project D", "Office", "Building 4", "Level 1", "Bob", 900, 12, 4, 40, 20, 350000, 70, 18000, java.sql.Date.valueOf("2024-08-01"), java.sql.Date.valueOf("2025-08-01"), "Mandalay", "Active"));

        return list;

    }

    private void loadProjectsByStatusDummy(String status) {
        ArrayList<projects> all = getDummyProjects();
        ArrayList<projects> filtered = new ArrayList<>();

        for (projects p : all) {
            if (p.getProjectStatus().equalsIgnoreCase(status)) {
                filtered.add(p);
            }
        }
        loadProjects(filtered);
    }

//    private void loadAllProjects(){
//        ArrayList<projects> list = projectDatabase.getAllProjects();
//        loadProjects(list);
//    }


//    private void loadProjectsByStatus(String status){
//        ArrayList<projects> all = projectDatabase.getAllProjects();
//        ArrayList<projects> filtered = new ArrayList<>();
//
//        for(projects p : all){
//            if(p.getProjectStatus().equalsIgnoreCase(status)){
//                filtered.add(p);
//            }
//        }
//        loadProjects(filtered);
//    }

    private void loadProjects(ArrayList<projects> projectsList){
        projectContainer.getChildren().clear();

        HBox row = null;
        int count =0;

        for(projects p : projectsList){

//            a new row every 2 cards
            if(count % 2 == 0){
                row = new HBox(30);//30 px spacing between cards
                projectContainer.getChildren().add(row);
            }



            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/View/projectCard.fxml")
                );

                Parent card = loader.load();


                projectCardController controller = loader.getController();
                controller.setData(p);

                row.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }

           count++;
        }
    }



}
