package IPPSystem.Controllers;

import IPPSystem.DAO.reportDatabase;
import IPPSystem.Models.DailyReport;
import IPPSystem.Utils.session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class SupervisorReportController {

    @FXML private Pagination reportPagination;
    @FXML private Button btnAddReport;

    private ArrayList<DailyReport> allReports;
    private String currentUserRole;
    private int currentUserId;

    @FXML
    public void initialize() {
        System.out.println("Supervisor Report Page Initialized...");

        // 1. Login User စစ်ဆေးခြင်း
        if (session.getInstance().getUser() == null) {
            System.out.println("User not logged in!");
            return;
        }

        // 2. User Role နဲ့ ID ကို သိမ်းထားမယ်
        currentUserRole = session.getInstance().getUser().getUserRole();
        currentUserId = session.getInstance().getUser().getUserId();

        System.out.println("Current User Role: " + currentUserRole);
        System.out.println("Current User ID: " + currentUserId);

        // 3. User Role အလိုက် Create Button ကို စစ်ဆေးမယ်
        if (btnAddReport != null) {
            if ("manager".equalsIgnoreCase(currentUserRole)) {
                // Manager ဆို Create button ကို ဖျောက်မယ်
                btnAddReport.setVisible(false);
                btnAddReport.setManaged(false);
                System.out.println("Manager logged in - Hide Create Report button");
            } else if ("supervisor".equalsIgnoreCase(currentUserRole)) {
                // Supervisor ဆို Create button ကို ပြမယ်
                btnAddReport.setVisible(true);
                btnAddReport.setManaged(true);
                btnAddReport.setOnAction(e -> goToCreatePage());
                System.out.println("Supervisor logged in - Show Create Report button");
            }
        }

        // 4. Data Load လုပ်ခြင်း
        try {
            if ("supervisor".equalsIgnoreCase(currentUserRole)) {
                // Supervisor အတွက် သူ့ report တွေပဲ ဆွဲမယ်
                allReports = reportDatabase.getAllReports(currentUserId);
                System.out.println("Loading supervisor-specific reports for user ID: " + currentUserId);
            } else if ("manager".equalsIgnoreCase(currentUserRole)) {
                // Manager အတွက် report အားလုံးကို ဆွဲမယ်
                allReports = reportDatabase.getAllReportsForManager();
                System.out.println("Loading all reports for manager view");
            }
        } catch (Exception e) {
            e.printStackTrace();
            allReports = new ArrayList<>();
        }

        setupPagination();
    }

    // Create Report Page ကို သွားမည့် Function
    private void goToCreatePage() {
        try {
            // FXML ကို load လုပ်မယ်
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CreateReportNew.fxml"));
            Parent root = loader.load();

            // CreateReportNewController ကိုယူပြီး data တွေ pass လုပ်မယ်
            CreateReportNewController controller = loader.getController();
            controller.setSupervisorId(currentUserId);
            controller.setCurrentUserRole(currentUserRole);

            // လက်ရှိ loadPane ထဲမှာ CreateReportNew ကို ပြမယ်
            Stage stage = (Stage) btnAddReport.getScene().getWindow();
            Scene scene = stage.getScene();

            // BorderPane ရှာမယ်
            BorderPane borderPane = (BorderPane) scene.lookup("#basePane");
            if (borderPane != null) {
                StackPane loadPane = (StackPane) borderPane.lookup("#loadPane");
                if (loadPane != null) {
                    loadPane.getChildren().setAll(root);
                }
            }

            System.out.println("Loaded CreateReportNew.fxml in current view");

        } catch (IOException ex) {
            System.out.println("Error loading CreateReportNew.fxml");
            ex.printStackTrace();
        }
    }

    private void setupPagination() {
        int itemsPerPage = 5;
        int size = (allReports != null) ? allReports.size() : 0;
        int pageCount = (int) Math.ceil((double) size / itemsPerPage);
        reportPagination.setPageCount(pageCount > 0 ? pageCount : 1);
        reportPagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        VBox pageBox = new VBox(10);
        if (allReports == null || allReports.isEmpty()) {
            return pageBox;
        }

        int itemsPerPage = 5;
        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allReports.size());

        for (int i = start; i < end; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/TableInCard.fxml"));
                Node card = loader.load();

                // TableInCardController ကို ခေါ်ပြီး data set လုပ်မယ်
                TableInCardController controller = loader.getController();
                controller.setReportData(allReports.get(i));

                pageBox.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pageBox;
    }
}