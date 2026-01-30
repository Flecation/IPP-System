package IPPSystem.Utils;

import IPPSystem.DAO.databaseConnection;
import IPPSystem.Models.projects;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class calculationHelper {

    private static calculationHelper instance;
    private static Connection con;

    // ===== EVM VALUES =====
    private Double pv, ev, ac, cpi, spi;
    private String cpiStatus, spiStatus;

    private calculationHelper() {
        try {
            con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static calculationHelper getInstance() {
        if (instance == null) instance = new calculationHelper();
        return instance;
    }

    // ==============================
    // DB CALCULATION
    // ==============================
    public void calculate(projects project) {

        String sql = "{CALL calculateCpiSpi(?)}";

        try (CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, project.getAssignProjectId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                pv = rs.getDouble("PV");
                ev = rs.getDouble("EV");
                ac = rs.getDouble("AC");

                cpi = rs.getObject("CPI") != null ? rs.getDouble("CPI") : null;
                spi = rs.getObject("SPI") != null ? rs.getDouble("SPI") : null;

                cpiStatus = rs.getString("CPI_STATUS");
                spiStatus = rs.getString("SPI_STATUS");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // UNIVERSAL CIRCLE SETUP
    // ==============================
    public void setupCpiCircle(Circle circle) {
        if (cpi == null) return;
        setupCircle(circle, cpi, cpiStatus, "CPI");
    }

    public void setupSpiCircle(Circle circle) {
        if (spi == null) return;
        setupCircle(circle, spi, spiStatus, "SPI");
    }

    // ==============================
    // CORE CIRCLE LOGIC (REUSABLE)
    // ==============================
    private void setupCircle(Circle circle, double value, String status, String type) {

        double radius = circle.getRadius();
        double circumference = 2 * Math.PI * radius;
        double progress = normalize(value);

        circle.setFill(null);
        circle.setRotate(-90);

        // Color
        circle.setStroke(getColor(type, value));

        // Animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(circle.strokeDashOffsetProperty(), circumference)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(circle.strokeDashOffsetProperty(),
                                circumference * (1 - progress))
                )
        );
        timeline.play();
    }

    // ==============================
    // HELPERS
    // ==============================
    private double normalize(double value) {
        double min = 0.8;
        double max = 1.2;
        double progress = (value - min) / (max - min);
        return Math.max(0, Math.min(1, progress));
    }

    private Color getColor(String type, double value) {

        if ("CPI".equals(type)) {
            if (value > 1.05) return Color.web("#2ecc71"); // green
            if (value >= 0.95) return Color.web("#f1c40f"); // yellow
            return Color.web("#e74c3c"); // red
        }

        if ("SPI".equals(type)) {
            if (value > 1.05) return Color.web("#2ecc71");
            if (value >= 0.95) return Color.web("#f1c40f");
            return Color.web("#e74c3c");
        }

        return Color.GRAY;
    }

    // ==============================
    // GETTERS
    // ==============================
    public Double getCpi() { return cpi; }
    public Double getSpi() { return spi; }
    public String getCpiStatus() { return cpiStatus; }
    public String getSpiStatus() { return spiStatus; }
    public Double getPv() { return pv; }
    public Double getEv() { return ev; }
    public Double getAc() { return ac; }
}
