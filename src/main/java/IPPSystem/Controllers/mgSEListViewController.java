package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mgSEListViewController {

    @FXML
    private Button managerSpCreateBtn;

    @FXML
    private ComboBox<?> managerSpProjectTypeCombo;

    @FXML
    private ComboBox<?> managerSpStatusCombo;

    @FXML
    private VBox managerSupervisorListPane;

    @FXML
    private HBox paginationBox;

    @FXML
    private Button paginationBtn1;

    @FXML
    private Button paginationBtn2;

    @FXML
    private Button paginationBtn3;

    @FXML
    private Button paginationNextBtn;

    @FXML
    private Button paginationPrevBtn;

    @FXML
    void clickon1(ActionEvent event) {

    }

    @FXML
    void clickon2(ActionEvent event) {

    }

    @FXML
    void clickon3(ActionEvent event) {

    }

    @FXML
    void clickonnext(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
            loadPage(currentPage);
        }
    }

    @FXML
    void clickonprev(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            loadPage(currentPage);
        }
    }

    public List<users> allEngineers = new ArrayList<>();

    private int currentPage = 1;
    private final int pageSize = 5;
    private int totalPages;


    @FXML
    public void initialize() {

        allEngineers =
                userDatabase.getUserByRole(role.SUPERVISOR.toString());

        totalPages = (int) Math.ceil(
                (double) allEngineers.size() / pageSize
        );

        loadPage(currentPage);
        buildPaginationButtons();
    }


    private void loadPage(int page) {

        managerSupervisorListPane.getChildren().clear();

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allEngineers.size());

        List<users> pageData =
                allEngineers.subList(start, end);

        for (users engineer : pageData) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/View/mgSERow.fxml")
                );

                Parent row = loader.load();

                mgSERowController controller =
                        loader.getController();

                controller.setData(engineer);

                controller.setOnDelete(this::refreshUI);



                managerSupervisorListPane.getChildren().add(row);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updatePaginationStyle();
    }

    private void refreshUI() {
        System.out.println("REFRESH UI CALLED");

        allEngineers.sort(
                (a, b) -> Boolean.compare(b.isActive(), a.isActive())
        );

        loadPage(currentPage);
        buildPaginationButtons();
    }




    private void buildPaginationButtons() {

        paginationBox.getChildren().clear();

        Button prev = new Button("Prev");
        prev.getStyleClass().add("page-btn");
        prev.setDisable(currentPage == 1);
        prev.setOnAction(e -> {
            currentPage--;
            loadPage(currentPage);
            buildPaginationButtons();
        });

        paginationBox.getChildren().add(prev);

        for (int i = 1; i <= totalPages; i++) {

            final int page = i;
            Button btn = new Button(String.valueOf(i));
            btn.getStyleClass().add("page-btn");

            if (page == currentPage)
                btn.getStyleClass().add("page-btn-active");

            btn.setOnAction(e -> {
                currentPage = page;
                loadPage(page);
                buildPaginationButtons();
            });

            paginationBox.getChildren().add(btn);
        }

        Button next = new Button("Next");
        next.getStyleClass().add("page-btn");
        next.setDisable(currentPage == totalPages);
        next.setOnAction(e -> {
            currentPage++;
            loadPage(currentPage);
            buildPaginationButtons();
        });

        paginationBox.getChildren().add(next);
    }



    private void updatePaginationStyle() {

        for (Node node : paginationBox.getChildren()) {

            Button btn = (Button) node;

            btn.getStyleClass().remove("page-btn-active");

            if (btn.getText().equals(String.valueOf(currentPage))) {
                btn.getStyleClass().add("page-btn-active");
            }

        }

        updatePrevNextState();
    }


    private void updatePrevNextState() {

        paginationPrevBtn.setDisable(currentPage == 1);
        paginationNextBtn.setDisable(currentPage == totalPages);
    }





}

