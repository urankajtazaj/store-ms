package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.controller.Controller;
import sample.controller.DB;
import sample.controller.Login;
import sample.controller.VariablatPublike;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader login = new FXMLLoader(getClass().getResource("/sample/gui/Login.fxml"));
        Parent root = login.load();

        System.err.println(VariablatPublike.javetNeMuaj(28));

        Scene scene = new Scene(root, 700, 450);

        Login loginController = login.getController();
        loginController.setStage(primaryStage);

        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                loginController.checkUser(primaryStage);
            }
        });

        primaryStage.setTitle("Information System");
        scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

//1100 x 600