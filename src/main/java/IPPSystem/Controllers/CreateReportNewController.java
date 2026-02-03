package IPPSystem.Controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CreateReportNewController {

    @FXML
    private Button addCommentsBtn;

    @FXML
    private Button addIssuesBtn;

    @FXML
    private Button addProgressBtn;

    @FXML
    private Button btnAddLabor;

    @FXML
    private TableColumn<?, ?> colHours;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colRemarks;

    @FXML
    private TableColumn<?, ?> colSkill;

    @FXML
    private TableColumn<?, ?> colWage;

    @FXML
    private VBox commentsContainer;

    @FXML
    private VBox issuesContainer;

    @FXML
    private TableView<?> laborTable;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private VBox progressContainer;

    @FXML
    private TextField projectNameField1;

    @FXML
    private DatePicker reportDatePicker;

    @FXML
    private Button submitReportBtn;

    @FXML
    private ComboBox<?> weatherTypeComboBox;

    private int progressCount = 0;
    private int issuesCount = 0;


    @FXML
    private void initialize() {
        // Initialize code here
    }

    // ========== Progress Description ==========
    @FXML
    private void handleAddProgress() {
        progressCount++;

        HBox rowContainer = new HBox(10);
        rowContainer.setStyle("-fx-alignment: CENTER_LEFT; -fx-spacing: 10px;");

        TextField textField = new TextField();
        textField.setPromptText("Enter progress description " + progressCount + "...");
        textField.setPrefWidth(400);
        textField.setMinWidth(400);
        textField.setMaxWidth(400);
        textField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        Button cancelBtn = new Button("✕");
        cancelBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: red; -fx-border-color: black; -fx-border-radius: 10px;"
        );
        cancelBtn.setPrefWidth(100);

        // ✅ SOLUTION: ဒီမှာ height ကို 0 လုပ်ပြီးမှ remove လုပ်မယ်
        cancelBtn.setOnAction(e -> {
            // ပထမဆုံး height ကို 0 လုပ်မယ် (layout မပြောင်းအောင်)
            rowContainer.setMinHeight(0);
            rowContainer.setPrefHeight(0);
            rowContainer.setMaxHeight(0);
            rowContainer.setVisible(false);
            rowContainer.setManaged(false);

            // နည်းနည်းစောင့်ပြီးမှ remove လုပ်မယ်
            PauseTransition pause = new PauseTransition(Duration.millis(50));
            pause.setOnFinished(event -> {
                progressContainer.getChildren().remove(rowContainer);
            });
            pause.play();
        });

        rowContainer.getChildren().addAll(textField, cancelBtn);
        progressContainer.getChildren().add(rowContainer);
    }

    // ========== Issues & Risks ==========
    @FXML
    private void handleAddIssue() {
        issuesCount++;

        HBox rowContainer = new HBox(10);
        rowContainer.setStyle("-fx-alignment: CENTER_LEFT;");

        TextField textField = new TextField();
        textField.setPromptText("Describe issue/risk " + issuesCount + "...");
        textField.setPrefWidth(400);
        textField.setMinWidth(400);
        textField.setMaxWidth(400);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-border-color: black; -fx-border-radius: 10px;");
        cancelBtn.setOnAction(e -> {
            issuesContainer.getChildren().remove(rowContainer);
        });

        rowContainer.getChildren().addAll(textField, cancelBtn);
        issuesContainer.getChildren().add(rowContainer);
    }



}