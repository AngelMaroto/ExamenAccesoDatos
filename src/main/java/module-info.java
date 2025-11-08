module com.example.examenaccesodatos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires com.sun.xml.bind;
    requires java.naming;
    requires com.fasterxml.jackson.databind;
    requires java.sql;


    opens com.example.examenaccesodatos to javafx.fxml;
    exports com.example.examenaccesodatos;

    exports com.example.examenaccesodatos.model;
    opens com.example.examenaccesodatos.model to com.fasterxml.jackson.databind, org.hibernate.orm.core;

    exports com.example.examenaccesodatos.Controller;
    opens com.example.examenaccesodatos.Controller to javafx.fxml;
}