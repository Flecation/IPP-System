package IPPSystem.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class linkButton {

    private static final int TAB_LIMIT = 12;

    private final Map<Button, Parent> linkMap = new HashMap<>();
    private Button activeTab;
    private Button activeCloseBtn;
    private HBox activeTabBox;

    // ✅ host (navigationPane) references
    private HBox hostTabBar;
    private StackPane hostLoadPane;

    private static linkButton instance;

    private linkButton(){}

    public static linkButton getInstance(){
        if (instance == null) instance = new linkButton();
        return instance;
    }

    // ✅ bind once from navigationPaneController
    public void bindHost(HBox tabBar, StackPane loadPane) {
        this.hostTabBar = tabBar;
        this.hostLoadPane = loadPane;
    }

    public void createTab(HBox tabBar, StackPane loadPane, String fxmlFile, String title) {
        createTabInternal(tabBar, loadPane, fxmlFile, title, null, null);
    }

    public void createTabWithInitialInner(String fxmlFile, String title, String initialInnerFxml) {
        createTabWithInitialInner(fxmlFile, title, initialInnerFxml, null);
    }



    // ✅ create new tab and load same inner page (duplicate tab feature)
    public void createTabWithInitialInner(
            String fxmlFile,
            String title,
            String initialInnerFxml,
            java.util.function.Consumer<IPPSystem.Controllers.sideBarPaneController> afterOpen
    ) {
        if (hostTabBar == null || hostLoadPane == null) {
            throw new IllegalStateException("Host not bound. Call bindHost(...) first.");
        }
        createTabInternal(hostTabBar, hostLoadPane, fxmlFile, title, initialInnerFxml, afterOpen);
    }

    private void createTabInternal(
            HBox tabBar,
            StackPane loadPane,
            String fxmlFile,
            String title,
            String initialInnerFxml,
            java.util.function.Consumer<IPPSystem.Controllers.sideBarPaneController> afterOpen
    ) {
        if (tabBar == null || loadPane == null) {
            throw new IllegalArgumentException("tabBar/loadPane cannot be null");
        }

        // ---------- Load FXML with controller access ----------
        Parent content;
        Object controller;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/" + fxmlFile));
            content = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlFile, e);
        }

        // Keep a reference to the controller on the root node, so we can
        // re-sync per-tab UI state when switching tabs.
        content.getProperties().put("TAB_CONTROLLER", controller);

        content.setVisible(false);
        content.setManaged(false);
        content.setMouseTransparent(true);
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
        pageBtn.setOnAction(e -> switchTab(tabBar, tabBox, pageBtn, closeBtn));
        tabBox.setOnMouseClicked(e -> switchTab(tabBar, tabBox, pageBtn, closeBtn));

        // ---------- Close tab ----------
        closeBtn.setOnAction(e -> closeTab(tabBar, loadPane, tabBox, pageBtn, content));

        // ---------- Add before + button ----------
        tabBar.getChildren().add(tabBar.getChildren().size() - 1, tabBox);

        // ---------- Activate ----------
        switchTab(tabBar, tabBox, pageBtn, closeBtn);

        // ✅ After tab created, load same inner view if this is a sideBarPane tab
        if (initialInnerFxml != null && controller instanceof IPPSystem.Controllers.sideBarPaneController sb) {
            sb.openInnerView(initialInnerFxml);

            if (afterOpen != null) {
                afterOpen.accept(sb); // ✅ give the caller access to the new tab sidebar controller
            }
        }

    }

    private void switchTab(HBox tabBar, HBox tabBox, Button selectedTab, Button closeBtn) {

        // Hide all content completely (no layout + no mouse blocking)
        linkMap.values().forEach(node -> {
            node.setVisible(false);
            node.setManaged(false);
            node.setMouseTransparent(true);
        });

        // Show selected content
        Parent content = linkMap.get(selectedTab);
        if (content != null) {
            content.setVisible(true);
            content.setManaged(true);
            content.setMouseTransparent(false);
            content.toFront(); // ✅ important in StackPane

            // ✅ If this tab content is a sidebar, re-sync its toggle UI with the current theme.
            Object ctrl = content.getProperties().get("TAB_CONTROLLER");
            if (ctrl instanceof IPPSystem.Controllers.sideBarPaneController sb) {
                sb.syncThemeToggleUI();
            }
        }

        setActiveTabStyles(tabBox, selectedTab, closeBtn);
        updateTabCloseButtons(tabBar);
    }

    private void setActiveTabStyles(HBox tabBox, Button pageBtn, Button closeBtn) {
        if (activeTab != null) activeTab.getStyleClass().remove("active-tab");
        if (activeCloseBtn != null) activeCloseBtn.getStyleClass().remove("active-tab");
        if (activeTabBox != null) activeTabBox.getStyleClass().remove("active-tab");

        tabBox.getStyleClass().add("active-tab");
        pageBtn.getStyleClass().add("active-tab");
        closeBtn.getStyleClass().add("active-tab");

        activeTab = pageBtn;
        activeCloseBtn = closeBtn;
        activeTabBox = tabBox;
    }

    private void closeTab(HBox tabBar, StackPane loadPane, HBox tabBox, Button pageBtn, Parent content) {
        int closedIndex = tabBar.getChildren().indexOf(tabBox);

        loadPane.getChildren().remove(content);
        tabBar.getChildren().remove(tabBox);
        linkMap.remove(pageBtn);

        if (pageBtn == activeTab) {
            activeTab = null;
            activeCloseBtn = null;
            activeTabBox = null;
            int tabCount = tabBar.getChildren().size() - 1;

            if (tabCount > 0) {
                int nextIndex = Math.min(closedIndex, tabCount - 1);
                HBox nextTabBox = (HBox) tabBar.getChildren().get(nextIndex);
                Button nextPageBtn = (Button) nextTabBox.getChildren().get(0);
                Button nextCloseBtn = (Button) nextTabBox.getChildren().get(1);
                switchTab(tabBar, nextTabBox, nextPageBtn, nextCloseBtn);
            } else {
                System.exit(0);
            }
        } else {
            updateTabCloseButtons(tabBar);
        }
    }

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

    public Button getTabButton() {
        for (Button btn : linkMap.keySet()) {
            if (btn.getStyleClass().contains("active-tab")) return btn;
        }
        return null;
    }

    public void setTabButtonName(String text){
        Button b = getTabButton();
        if (b != null) b.setText(text);
    }
}
