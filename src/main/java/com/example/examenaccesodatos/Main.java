package com.example.examenaccesodatos;

import com.example.examenaccesodatos.Connection.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/citas.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Inicio de Sesión");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() throws Exception {
        DBConnection.desconectar();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



