module com.example.gambarucmsui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.desktop;
    requires com.google.zxing;
    requires itextpdf;

    opens com.example.gambarucmsui to javafx.fxml;
    opens com.example.gambarucmsui.adapter.out.persistence.entity to org.hibernate.orm.core;
    opens com.example.gambarucmsui.common to javafx.controls;
    opens com.example.gambarucmsui.ui.form to javafx.fxml;

    exports com.example.gambarucmsui;
    exports com.example.gambarucmsui.ui;
    opens com.example.gambarucmsui.ui to javafx.fxml;
    opens com.example.gambarucmsui.ui.dto to javafx.base, javafx.fxml;
    exports com.example.gambarucmsui.ui.dto;
}