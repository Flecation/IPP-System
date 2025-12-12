module projectgroup1.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens projectgroup1.controllers to javafx.fxml;
    opens projectgroup1.demo to javafx.fxml;
    opens projectgroup1.DAO to javafx.fxml;
    opens projectgroup1.utils to javafx.fxml;
    exports projectgroup1.demo;

}