package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import sample.controller.Login;
import sample.controller.Server;
import sample.controller.VariablatPublike;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Main extends Application {

    Server server = new Server();

    @Override
    public void start(Stage primaryStage) throws Exception{
        server.startServer();
        primaryStage.setOnCloseRequest(e -> {
            Server.stopServer();
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                lexoServerin();
            }
        }).start();

        ResourceBundle bundle = null;
        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties"));
        if (properties.getProperty("lang").equals("English")) {
            bundle = ResourceBundle.getBundle("resources.Language_en", new Locale("en", "EN"));
        } else if (properties.getProperty("lang").equals("German")) {
            bundle = ResourceBundle.getBundle("resources.Language_de", new Locale("de", "DE"));
        } else {
            bundle = ResourceBundle.getBundle("resources.Language_sq", new Locale("sq", "SQ"));
        }

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        FXMLLoader login = new FXMLLoader(getClass().getResource("/sample/gui/Login.fxml"), bundle);
        Parent root = login.load();

        Scene scene = new Scene(root, 400, 450);

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
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties"));

            VariablatPublike.server = props.getProperty("server");
            VariablatPublike.emriShitores = props.getProperty("emri");
            VariablatPublike.styleSheet = props.getProperty("theme").equals("Dark") ? "/sample/style/style.css" : "/sample/style/styleLight.css";
            VariablatPublike.nrFiskal = props.getProperty("nr_fiskal");
            VariablatPublike.BANKA = props.getProperty("banka");
            VariablatPublike.KONTO = props.getProperty("konto");
            VariablatPublike.IBAN = props.getProperty("iban");
            VariablatPublike.SWIFT = props.getProperty("swift");
            VariablatPublike.ADRESA = props.getProperty("adresa");
            VariablatPublike.LANG = props.getProperty("lang");
        }catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        launch(args);
    }
}