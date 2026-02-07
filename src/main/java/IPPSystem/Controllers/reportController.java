package IPPSystem.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class reportController {

    @FXML
    private Button btnAddReport;

    @FXML
    private Button btnResetFilters;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private ComboBox<?> projectSidebarFilter;

    @FXML
    private VBox reportContainer;

    @FXML
    private Label reportCountLabel;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox sidebarProjectList;

    @FXML
    private ScrollPane sidebarScrollPane;

    @FXML
    private DatePicker startDatePicker;

}
