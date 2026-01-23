package IPPSystem.Utils;

import IPPSystem.Controllers.projectCardController;
import IPPSystem.Models.projects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class showProjectCard {

    public void loadProjects(ArrayList<projects> projectsList, VBox projectContainer){
        projectContainer.getChildren().clear();

        HBox row = null;
        int count =0;

        for(projects p : projectsList){

//            a new row every 2 cards
            if(count % 3 == 0){
                row = new HBox(15);//25 px spacing between cards
                projectContainer.getChildren().add(row);
            }



            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/View/projectCard.fxml")
                );

                Parent card = loader.load();


                projectCardController controller = loader.getController();
                controller.setData(p);
//                controller.setParentController(this);


                row.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }

            count++;
        }


    }

}
