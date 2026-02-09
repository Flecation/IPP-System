package IPPSystem.Utils;

import IPPSystem.Controllers.*;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Main.HelloApplication;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Models.workItems;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import IPPSystem.Controllers.sideBarPaneController;
import IPPSystem.Controllers.projectCardController;


import java.io.IOException;

public class switchPage extends utils {

    private static final java.util.IdentityHashMap<StackPane, switchPage> INSTANCES = new java.util.IdentityHashMap<>();


    protected StackPane loadPane;

    private switchPage(){}

    public static switchPage getInstance(StackPane pane) {
        if (pane == null) {
            throw new IllegalArgumentException("switchPage.getInstance(...) requires a non-null StackPane.");
        }
        return INSTANCES.computeIfAbsent(pane, p -> {
            switchPage sp = new switchPage();
            sp.loadPane = p;
            return sp;
        });
    }


    // Dashboard page switch animation
    public void setSwitchPane(
            StackPane basePane,
            String toPane,
            Button titleUrlButton,
            String titleUrlName

    ) {

        // Loading spinner
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(14, 14);
        if (titleUrlButton != null) {
            titleUrlButton.setGraphic(spinner);
            titleUrlButton.setText("Loading...");
        }

        // Load next pane safely
        Parent nextPane;
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(toPane)
            );
            nextPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            if (titleUrlButton != null) {
                titleUrlButton.setGraphic(null);
                titleUrlButton.setText("Error");
            }
            return;
        }

        // Overlay region
        Region region = new Region();
        region.prefWidthProperty().bind(basePane.widthProperty());
        region.prefHeightProperty().bind(basePane.heightProperty());
        region.setManaged(false);
        region.setMouseTransparent(true);
        region.setOpacity(0);

        // Blur effect
        GaussianBlur blur = new GaussianBlur(0);
        basePane.setEffect(blur);

        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(), 0),
                        new KeyValue(region.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(blur.radiusProperty(), 5),
                        new KeyValue(region.opacityProperty(), 0.5)
                )
        );

        blurIn.setOnFinished(event -> {

            // Update title button
            if (titleUrlButton != null) {
                titleUrlButton.setGraphic(null);
                setToolTip(titleUrlButton, titleUrlName);
                titleUrlButton.setText(titleUrlName);
            }

            // Switch panes
            basePane.getChildren().setAll(region, nextPane);

            Timeline blurOut = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(blur.radiusProperty(), 5),
                            new KeyValue(region.opacityProperty(), 0.5)
                    ),
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(blur.radiusProperty(), 0),
                            new KeyValue(region.opacityProperty(), 0)
                    )
            );

            blurOut.setOnFinished(e -> {
                nextPane.setEffect(null);
                basePane.getChildren().remove(region);
            });

            blurOut.play();
        });

        blurIn.play();
    }

    // Simple FXML opener utility (cached per tab)
    @SuppressWarnings("unchecked")
    public void openFxml(String fxmlFile) {
        if (fxmlFile == null || fxmlFile.isBlank()) return;

        try {
            // Per-tab cache (each tab has its own loadPane instance)
            java.util.Map<String, Parent> cache =
                    (java.util.Map<String, Parent>) loadPane.getProperties()
                            .computeIfAbsent("FXML_CACHE", k -> new java.util.HashMap<String, Parent>());

            Parent resolved = cache.get(fxmlFile);

            // Load if not cached
            if (resolved == null) {
                FXMLLoader loader = new FXMLLoader(utils.class.getResource("/View/" + fxmlFile));
                resolved = loader.load();

                // Inject this tab's loadPane into controller (if supported)
                Object controller = loader.getController();
                if (controller instanceof loadPaneAware aware) {
                    aware.setLoadPane(loadPane);
                }

                cache.put(fxmlFile, resolved);
            }

            final Parent newContent = resolved;

            StackPane.setAlignment(newContent, Pos.CENTER);
            StackPane.setMargin(newContent, javafx.geometry.Insets.EMPTY);


            if (newContent instanceof Region region) {
                // Bind only once (re-using cached nodes)
                if (!region.prefWidthProperty().isBound()) {
                    region.prefWidthProperty().bind(loadPane.widthProperty());
                }
                if (!region.prefHeightProperty().isBound()) {
                    region.prefHeightProperty().bind(loadPane.heightProperty());
                }
                region.setMaxWidth(Double.MAX_VALUE);
                region.setMaxHeight(Double.MAX_VALUE);
            }

            if (loadPane.getChildren().isEmpty()) {
                loadPane.getChildren().setAll(newContent);
                return;
            }

            Node oldContent = loadPane.getChildren().get(0);
            newContent.setOpacity(0);

            loadPane.getChildren().setAll(oldContent, newContent);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(160), oldContent);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            ParallelTransition transition = new ParallelTransition(fadeOut, fadeIn);
            transition.setOnFinished(e -> loadPane.getChildren().setAll(newContent));
            transition.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //from the login controller to the dashboard with animation
    public static  void switchScene(Button button, String fxmlPath) {

        String fxml = "/View/" + fxmlPath;


        String originalText = button.getText();
        Node originalGraphic = button.getGraphic();

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(16, 16);

        button.setText("Loading...");
        button.setGraphic(spinner);
        button.setDisable(true);

        Stage stage = (Stage) button.getScene().getWindow();
        Parent oldRoot = stage.getScene().getRoot();

        GaussianBlur blur = new GaussianBlur(0);
        oldRoot.setEffect(blur);

        // Allow UI to render spinner
        PauseTransition pause = new PauseTransition(Duration.millis(80));
        pause.setOnFinished(p -> {

            Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(oldRoot.opacityProperty(), 1),
                            new KeyValue(blur.radiusProperty(), 0)
                    ),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(oldRoot.opacityProperty(), 0),
                            new KeyValue(blur.radiusProperty(), 6)
                    )
            );

            fadeOut.setOnFinished(event -> {

                Parent newRoot;
                try {
                    newRoot = FXMLLoader.load(
                            HelloApplication.class.getResource(fxml)
                    );
                } catch (IOException e) {
                    button.setText(originalText);
                    button.setGraphic(originalGraphic);
                    button.setDisable(false);
                    oldRoot.setEffect(null);
                    e.printStackTrace();
                    return;
                }

                newRoot.setOpacity(0);
                GaussianBlur newBlur = new GaussianBlur(6);
                newRoot.setEffect(newBlur);

                Scene newScene = new Scene(newRoot);
                newScene.setFill(Color.TRANSPARENT);

                stage.setScene(newScene);
                stage.setMaximized(true);

                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(newRoot.opacityProperty(), 0),
                                new KeyValue(newBlur.radiusProperty(), 6)
                        ),
                        new KeyFrame(Duration.millis(300),
                                new KeyValue(newRoot.opacityProperty(), 1),
                                new KeyValue(newBlur.radiusProperty(), 0)
                        )
                );

                fadeIn.setOnFinished(e -> newRoot.setEffect(null));
                fadeIn.play();
            });

            fadeOut.play();
        });

        pause.play();
    }

    public void loadProjects(ObservableList<projects> projectsList, VBox projectContainer) {
        projectContainer.getChildren().clear();
        HBox row = null;
        int count = 0;

        // ✅ get the sidebar controller for THIS TAB
        sideBarPaneController nav =
                (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");

        for (projects p : projectsList) {
            if (count % 3 == 0) {
                row = new HBox(20);
                projectContainer.getChildren().add(row);
                projectContainer.setAlignment(Pos.CENTER);
            }

            try {
                FXMLLoader loader = new FXMLLoader(utils.class.getResource("/View/projectCard.fxml"));
                Parent card = loader.load();

                projectCardController controller = loader.getController();

                // keep your existing pattern
                controller.setData(p, loadPane);

                // ✅ IMPORTANT: inject nav so card can open projectDetails safely
                if (nav != null) {
                    controller.setNav(nav);
                }

                if (row != null) row.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }

            count++;
        }
    }


    public void openWorkItemDetails(workItems item,projects project){
        if (item == null || loadPane == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/workItemDetails.fxml"));
            Parent root = loader.load();
            workItemDetailsController controller = loader.getController();
            controller.setWorkItem(item,project/* tasks list if you want later */);
            loadPane.getChildren().setAll(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void OpenLogout(){

    }

    public void viewUsersInfo(users user){
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/View/mgSEPersonalDetail.fxml"));

            Parent page = loader.load();

            mgSEPersonalDetailController controller =
                    loader.getController();
            controller.setEngineer(user);

            if (controller instanceof IPPSystem.Interfaces.loadPaneAware aware) {
                aware.setLoadPane(loadPane);
            }


            loadPane.getChildren().setAll(page);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void openProjectDetails(projects project) {
        if (project == null) return;

        sideBarPaneController nav =
                (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        if (nav == null) return;

        nav.openInnerView("projectDetails.fxml", ctrl -> {
            if (ctrl instanceof projectDetailsController c) {
                c.setProjectData(project);
            }
        });
    }

}
