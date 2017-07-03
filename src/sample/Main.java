package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.controller.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main extends Application {

    Server server = new Server();

    @Override
    public void start(Stage primaryStage) throws Exception{
        server.startServer();
        primaryStage.setOnCloseRequest(e -> {
            Server.stopServer();
        });
        lexoServerin();

        FXMLLoader login = new FXMLLoader(getClass().getResource("/sample/gui/Login.fxml"));
        Parent root = login.load();

        Scene scene = new Scene(root, 700, 400);

        Login loginController = login.getController();
        loginController.setStage(primaryStage);

        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                loginController.checkUser(primaryStage);
            }
        });

        primaryStage.setTitle("Sistemi per Menaxhimin te Shitjeve");
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void lexoServerin(){
        File file = new File(System.getProperty("user.home") + "/store-ms-files/config/config.txt");
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            VariablatPublike.server = br.readLine().split(":")[1];
            VariablatPublike.emriShitores = br.readLine().split(":")[1];
            VariablatPublike.styleSheet = br.readLine().split(":")[1].equals("Dark") ? "/sample/style/style.css" : "/sample/style/styleLight.css";
        }catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        launch(args);
    }
}