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
    requires liquibase.core;

    opens com.example.gambarucmsui to javafx.fxml;
    opens com.example.gambarucmsui.database.entity to org.hibernate.orm.core;
    opens com.example.gambarucmsui.common to javafx.controls;
    opens com.example.gambarucmsui.ui.form to javafx.fxml;
    opens com.example.gambarucmsui.ui.alert to javafx.fxml;
    opens com.example.gambarucmsui.ui to javafx.fxml;

    exports com.example.gambarucmsui;
    exports com.example.gambarucmsui.ui;
    exports com.example.gambarucmsui.ui.form;
    exports com.example.gambarucmsui.ui.dto.admin;
    opens com.example.gambarucmsui.ui.dto.admin to javafx.base, javafx.fxml;
    exports com.example.gambarucmsui.ui.dto.admin.subtables;
    opens com.example.gambarucmsui.ui.dto.admin.subtables to javafx.base, javafx.fxml;
    exports com.example.gambarucmsui.ui.dto.core;
    opens com.example.gambarucmsui.ui.dto.core to javafx.base, javafx.fxml;
    opens com.example.gambarucmsui.util to javafx.controls;
    exports com.example.gambarucmsui.ui.panel;
    opens com.example.gambarucmsui.ui.panel to javafx.fxml;
    opens com.example.gambarucmsui.ports.interfaces.user to org.hibernate.orm.core;
    opens com.example.gambarucmsui.ports.interfaces.team to org.hibernate.orm.core;
    opens com.example.gambarucmsui.ports.interfaces.barcode to org.hibernate.orm.core;
    opens com.example.gambarucmsui.ports.interfaces.attendance to org.hibernate.orm.core;
}