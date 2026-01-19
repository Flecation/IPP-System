package IPPSystem.Utils;

import IPPSystem.DAO.databaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class linkButton {

    private static final int TAB_LIMIT = 12;

    private final Map<Button, Parent> linkMap = new HashMap<>();
    private Button activeTab;
    private HBox activeBox;

    /**
     * Create a new tab with FXML content
     */
    public void createTab(
            HBox tabBar,
            StackPane loadPane,
            String fxmlFile,
            String title
    ) {

        // ---------- Load FXML ----------
        Parent content;
        try {
            content = FXMLLoader.load(getClass().getResource("/View/" + fxmlFile));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlFile, e);
        }

        content.setVisible(false);
        loadPane.getChildren().add(content);

        // ---------- Tab buttons ----------
        Button pageBtn = new Button(title);
        Button closeBtn = new Button();
        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.TIMES);
        closeIcon.getStyleClass().add("closeIcon");
        closeBtn.setGraphic(closeIcon);

        pageBtn.setMinHeight(35);
        closeBtn.setMinHeight(35);
        pageBtn.setMinWidth(30);

        // ---------- Tab container ----------
        HBox tabBox = new HBox(pageBtn, closeBtn);
        tabBox.setAlignment(Pos.CENTER);
        tabBox.getStyleClass().add("linkButton");
        pageBtn.getStyleClass().add("pageBtn");
        closeBtn.getStyleClass().add("closeBtn");

        HBox.setHgrow(pageBtn, Priority.ALWAYS);
        HBox.setHgrow(closeBtn, Priority.NEVER);

        // ---------- Map ----------
        linkMap.put(pageBtn, content);

        // ---------- Switch tab ----------
        pageBtn.setOnAction(e -> switchTab(tabBar, pageBtn));

        // ---------- Close tab ----------
        closeBtn.setOnAction(e -> closeTab(tabBar, loadPane, tabBox, pageBtn, content));

        // ---------- Add before + button ----------
        tabBar.getChildren().add(tabBar.getChildren().size() - 1, tabBox);

        // ---------- Activate first tab ----------
            switchTab(tabBar, pageBtn);
    }

    // =========================================================
    // TAB SWITCHING
    // =========================================================
    private void switchTab(HBox tabBar, Button selectedTab) {

        // Hide all content
        linkMap.values().forEach(node -> node.setVisible(false));

        // Remove active style
        if (activeTab != null) {
            activeTab.getStyleClass().remove("active-tab");

        }
        if(activeBox != null){
            activeBox.getStyleClass().remove("active");
        }

        // Show selected content
        Parent content = linkMap.get(selectedTab);
        if (content != null) {
            content.setVisible(true);
        }

        selectedTab.getStyleClass().add("active-tab");
        tabBar.getStyleClass().add("active");
        activeBox = tabBar;
        activeTab = selectedTab;

        updateTabCloseButtons(tabBar);
    }

    // =========================================================
    // TAB CLOSE
    // =========================================================
    private void closeTab(
            HBox tabBar,
            StackPane loadPane,
            HBox tabBox,
            Button pageBtn,
            Parent content
    ) {

        int closedIndex = tabBar.getChildren().indexOf(tabBox);

        loadPane.getChildren().remove(content);
        tabBar.getChildren().remove(tabBox);
        linkMap.remove(pageBtn);

        if (activeBox == tabBar){
            activeBox = null;
        }

        if (pageBtn == activeTab) {
            activeTab = null;
            int tabCount = tabBar.getChildren().size() - 1;

            if (tabCount > 0) {
                int nextIndex = Math.min(closedIndex, tabCount - 1);
                HBox nextTabBox = (HBox) tabBar.getChildren().get(nextIndex);
                Button nextPageBtn = (Button) nextTabBox.getChildren().get(0);
                switchTab(tabBar, nextPageBtn);
            } else {
                databaseConnection.closeConnection();
                System.exit(0);
            }
        } else {
            updateTabCloseButtons(tabBar);
        }
    }

    // =========================================================
    // CLOSE BUTTON VISIBILITY (SINGLE SOURCE OF TRUTH)
    // =========================================================
    private void updateTabCloseButtons(HBox tabBar) {

        int tabCount = tabBar.getChildren().size() - 1;
        boolean overLimit = tabCount > TAB_LIMIT;

        for (Button pageBtn : linkMap.keySet()) {

            HBox tabBox = (HBox) pageBtn.getParent();
            Button closeBtn = (Button) tabBox.getChildren().get(1);

            if (!overLimit) {
                closeBtn.setVisible(true);
                closeBtn.setManaged(true);
            } else {
                boolean isActive = pageBtn == activeTab;
                closeBtn.setVisible(isActive);
                closeBtn.setManaged(isActive);
            }
        }
    }
}
