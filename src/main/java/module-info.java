module com.example.gambarucmsui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens com.example.gambarucmsui to javafx.fxml;
    opens com.example.gambarucmsui.adapter.out.persistence.entity to org.hibernate.orm.core;
    opens com.example.gambarucmsui.adapter.out.persistence.entity.user to org.hibernate.orm.core;
    opens com.example.gambarucmsui.common to javafx.controls;
    exports com.example.gambarucmsui;
}