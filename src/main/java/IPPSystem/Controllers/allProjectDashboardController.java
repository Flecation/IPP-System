package IPPSystem.Controllers;

import IPPSystem.DAO.databaseConnection;
import IPPSystem.Utils.utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static IPPSystem.DAO.database.getProjectTimingStatus;


public class allProjectDashboardController {

    @FXML
    private Label projectName, lblProject, lblRevenue, lblExpense, lblWorkers;
    @FXML
    private ComboBox<String> comboProjectList, comboProjectList1, comboProjectList2;
    @FXML
    private PieChart pieProjectStatus, pieSchedule;

    @FXML
    private VBox vboxTtlProject, vboxTtlRevenue, vboxTtlExpense, vboxTtlWorkers, vboxProjectStatusDistribution, vboxSchedulePerformance, vboxTop3Intensive, vboxMonthlyProject, vboxActiveProject;
    @FXML
    private LineChart<String, Number> linechartMonthlyCost, linechartActiveProject;

    @FXML
    private Label progressCostTyp1, progressCost1, progressCostTyp2, progressCost2, progressCostTyp3, progressCost3;
    @FXML
    private ProgressBar progressbarTyp1, progressbarTyp2, progressbarTyp3;

    private Connection con;

    @FXML
    public void initialize() {
        try {
            con = databaseConnection.getConnection();
            if (con == null) {
                System.out.println("Database Connection Failed!");
                return;
            }
            loadDashboardData();
            setupFilters();
            addHoverEffect(vboxTtlProject);
            addHoverEffect(vboxTtlExpense);
            addHoverEffect(vboxTtlRevenue);
            addHoverEffect(vboxTtlWorkers);
//            addHoverEffect(vboxProjectStatusDistribution);
//            addHoverEffect(vboxSchedulePerformance);
//            addHoverEffect(vboxTop3Intensive);
//            addHoverEffect(vboxMonthlyProject);
//            addHoverEffect(vboxActiveProject);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    //  >>>>>>>>>>>>>>>>>>>>>
//    Design
//    >>>>>>>>>>>>>>>>>>
    private void addHoverEffect(VBox box) {
        box.setOnMouseEntered(e -> {
            box.setScaleX(1.05);
            box.setScaleY(1.05);
            box.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");
        });

        box.setOnMouseExited(e -> {
            box.setScaleX(1.0);
            box.setScaleY(1.0);
            box.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        });
    }
// addHoverEffect(vboxTotalProject);

    private void loadDashboardData() {
        // IFNULL(..., 0) ထည့်လိုက်ခြင်းဖြင့် Null Pointer Error မှ ကာကွယ်နိုင်ပါသည်
        double revenue = fetchSingleValue("SELECT IFNULL(SUM(projectCost), 0) FROM assignprojectdetails");
        double overhead = fetchSingleValue("SELECT IFNULL(SUM(projectOverHeadCost), 0) FROM assignprojects");

        // ကျန်တဲ့ data ယူနစ်များ
        int workers = (int) fetchSingleValue("SELECT COUNT(*) FROM labors");
        int projects = (int) fetchSingleValue("SELECT COUNT(*) FROM assignprojects");

        // UI ပေါ်တင်ခြင်း
        lblRevenue.setText(String.format("$%,.2f", revenue));
        lblExpense.setText(String.format("$%,.2f", revenue + overhead));
        lblWorkers.setText(String.valueOf(workers));
        lblProject.setText(String.valueOf(projects));

        // ကျန်သည့် Chart updates များ
        updatePieChart();
        updatePieChart1();
        updateLineCharts();
        setupMostCostProjects();
        applyCustomColors();

    }

    private double fetchSingleValue(String sql) {
        // IFNULL သုံးခြင်းဖြင့် data မရှိပါက 0 ပြန်ပေးမည်
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Database Error on Query: " + sql + " | Message: " + e.getMessage());
        }
        return 0;
    }

    private void updatePieChart() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        String sql = "SELECT ps.projectStatusName, COUNT(ap.assignProjectId) \n" +
                "FROM projectstatus ps\n" +
                "LEFT JOIN assignprojects ap ON ps.projectStatusId = ap.projectStatus\n" +
                "WHERE ps.projectStatusName IN ('In Progress', 'Finished', 'Delay')\n" +
                "GROUP BY ps.projectStatusName;";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new PieChart.Data(
                        rs.getString(1),
                        rs.getInt(2)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pieProjectStatus != null) {
            pieProjectStatus.setData(data);

            // >>> Percentage ထည့်သွင်းပေးမည့်အပိုင်း <<<
            // ၁။ စုစုပေါင်း Value ကို အရင်တွက်မည်
            double total = data.stream()
                    .mapToDouble(PieChart.Data::getPieValue)
                    .sum();

            // ၂။ Data တစ်ခုချင်းစီကို loop ပတ်ပြီး Name ကို Percentage နှင့်တွဲမည်
            for (PieChart.Data d : data) {
                if (total > 0) {
                    double percentage = (d.getPieValue() / total) * 100;
                    // နာမည်ကို "Finished (45.5%)" ပုံစံမျိုး ပြောင်းလဲပေးမည်
                    d.setName(String.format("%s (%.1f%%)", d.getName(), percentage));
                }
            }
        }
    }

    public Map<String, Integer> getProjectTimingStatus() {
        Map<String, Integer> timingData = new HashMap<>();

        // Default values
        timingData.put("On Time", 0);
        timingData.put("Delayed", 0);

        // Query ရှင်းလင်းချက်-
        // ၁။ သတ်မှတ်ရက်ကျော်ပြီး Finished မဖြစ်သေးရင် = Delayed
        // ၂။ သတ်မှတ်ရက် မကျော်သေးရင် သို့မဟုတ် Finished ဖြစ်သွားရင် = On Time
        String sql = "SELECT " +
                "SUM(CASE WHEN apd.endDate < CURDATE() AND ps.projectStatusName != 'Finished' THEN 1 ELSE 0 END) as delayedCount, " +
                "SUM(CASE WHEN apd.endDate >= CURDATE() OR ps.projectStatusName = 'Finished' THEN 1 ELSE 0 END) as onTimeCount " +
                "FROM assignprojects ap " +
                "JOIN projectstatus ps ON ap.projectStatus = ps.projectStatusId " +
                "JOIN assignprojectdetails apd ON ap.assignProjectId = apd.assignProjectId";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                timingData.put("Delayed", rs.getInt("delayedCount"));
                timingData.put("On Time", rs.getInt("onTimeCount"));
            }
        } catch (SQLException e) {
            System.out.println("Timing Query Error: " + e.getMessage());
        }
        return timingData;
    }

    private void updatePieChart1() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        // Map ထဲက Data တွေကို ဆွဲယူမယ်
        Map<String, Integer> timingStatus = getProjectTimingStatus();

        // Map ထဲမှာရှိတဲ့ "On Time" နဲ့ "Delayed" ကို loop ပတ်ပြီး Chart ထဲထည့်မယ်
        timingStatus.forEach((status, count) -> {
            if (count >= 0) { // ဒေတာရှိမှ ထည့်မယ်
                pieData.add(new PieChart.Data(status + " (" + count + ")", count));
            }
        });

        if (pieSchedule != null) {
            pieSchedule.setData(pieData);

            // အရောင်သတ်မှတ်ခြင်း (On Time = အစိမ်း၊ Delayed = အနီ)
            for (PieChart.Data data : pieSchedule.getData()) {
                if (data.getName().contains("On Time")) {
                    data.getNode().setStyle("-fx-pie-color: #06bf00;"); // Green
                } else if (data.getName().contains("Delayed")) {
                    data.getNode().setStyle("-fx-pie-color: #db361a;"); // Red
                }
            }
        }
    }

    // Status အလိုက် သတ်မှတ်အရောင်များ ပေါ်စေရန်
    private void applyCustomColors() {
        for (PieChart.Data data : pieProjectStatus.getData()) {
            if (data.getName().contains("In Progress")) {
                data.getNode().setStyle("-fx-pie-color: red;"); // အနီရောင် (FXML အရ)
            } else if (data.getName().contains("Finished")) {
                data.getNode().setStyle("-fx-pie-color: blue;"); // အပြာရောင်
            } else if (data.getName().contains("Delay")) {
                data.getNode().setStyle("-fx-pie-color: yellow;"); // အဝါရောင်
            }
        }
    }

    //    Top3 Rank Cost
    private void setupMostCostProjects() {
        // အကုန်အကျအများဆုံး ၃ ခုကို ကြီးစဉ်ငယ်လိုက် ဆွဲထုတ်သည့် Query
        String sql = "SELECT ap.projectInstanceName, apd.projectCost " +
                "FROM assignprojects ap " +
                "JOIN assignprojectdetails apd ON ap.assignProjectId = apd.assignProjectId " +
                "ORDER BY apd.projectCost DESC LIMIT 3";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int i = 1;
            while (rs.next()) {
                String projectName = rs.getString("projectInstanceName");
                double cost = rs.getDouble("projectCost");

                // UI Labels များထဲသို့ ထည့်ခြင်း
                if (i == 1) {
                    progressCostTyp1.setText(projectName); // ဘယ်ဘက် Label (နာမည်)
                    progressCost1.setText(String.format("$%,.0f", cost)); // ညာဘက် Label (ဈေးနှုန်း)
                    progressbarTyp1.setProgress(1.0); // ပထမဆုံးတစ်ခုကို အပြည့်ထားပါ
                    progressbarTyp1.setStyle("-fx-accent: #02006b;");
                } else if (i == 2) {
                    progressCostTyp2.setText(projectName);
                    progressCost2.setText(String.format("$%,.0f", cost));
                    progressbarTyp2.setProgress(0.7); // နှိုင်းယှဉ်ချက်အရ လျှော့ပြပါ
                    progressbarTyp2.setStyle("-fx-accent: #02006b;");
                } else if (i == 3) {
                    progressCostTyp3.setText(projectName);
                    progressCost3.setText(String.format("$%,.0f", cost));
                    progressbarTyp3.setProgress(0.4);
                    progressbarTyp3.setStyle("-fx-accent: #02006b;");
                }
                i++;
            }
        } catch (SQLException e) {
            System.out.println("Cost Progress Bar Error: " + e.getMessage());
        }

    }

    private void updateLineCharts() {
        XYChart.Series<String, Number> costSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> projSeries = new XYChart.Series<>();

        // DATE_FORMAT တွင် စာလုံးအကြီးအသေး သတိထားရန်
        String sql = "SELECT DATE_FORMAT(startDate, '%b') as month, SUM(projectCost) as cost, COUNT(*) as qty " +
                "FROM assignprojectdetails GROUP BY MONTH(startDate) ORDER BY MONTH(startDate)";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String m = rs.getString("month");
                costSeries.getData().add(new XYChart.Data<>(m, rs.getDouble("cost")));
                projSeries.getData().add(new XYChart.Data<>(m, rs.getInt("qty")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (linechartMonthlyCost != null) {
            linechartMonthlyCost.getData().clear();
            linechartMonthlyCost.getData().add(costSeries);
        }
        if (linechartActiveProject != null) {
            linechartActiveProject.getData().clear();
            linechartActiveProject.getData().add(projSeries);
        }
    }

    private void updateProgressBars() {
        String sql = "SELECT ap.projectInstanceName, apd.projectCost FROM assignprojects ap " +
                "JOIN assignprojectdetails apd ON ap.assignProjectId = apd.assignProjectId " +
                "ORDER BY apd.projectCost DESC LIMIT 3";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                progressCostTyp1.setText(rs.getString(1));
                progressCost1.setText("$" + rs.getInt(2));
                progressbarTyp1.setProgress(1.0);
            }
            if (rs.next()) {
                progressCostTyp2.setText(rs.getString(1));
                progressCost2.setText("$" + rs.getInt(2));
                progressbarTyp2.setProgress(0.7);
            }
            if (rs.next()) {
                progressCostTyp3.setText(rs.getString(1));
                progressCost3.setText("$" + rs.getInt(2));
                progressbarTyp3.setProgress(0.4);
            }
        } catch (Exception e) {
        }
    }

    private void setupFilters() {
        comboProjectList.setItems(FXCollections.observableArrayList("All Projects", "Active Project"));
        comboProjectList1.setItems(FXCollections.observableArrayList("2025", "2026"));
        comboProjectList2.setItems(FXCollections.observableArrayList("Jan", "Feb", "Mar"));
    }

    @FXML
    void clickProjectSelect(ActionEvent event) {

            String selected = comboProjectList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (selected.equals("Active Project")){
                utils.openFxml("currentProjectDashboard.fxml",null);
            }

    }
}

