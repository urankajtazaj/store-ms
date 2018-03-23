package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-03.
 */
public class Login implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    Notification ntf = new Notification();

    @FXML private Hyperlink hyperlink;
    @FXML
    public Button btnLogin, btnSetting;
    @FXML
    private TextField txtUser, txtPw;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rb = resources;

        String theme = VariablatPublike.styleSheet.substring(VariablatPublike.styleSheet.length()-9, VariablatPublike.styleSheet.length());
        if (!theme.equals("style.css"))
            hyperlink.setStyle("-fx-text-fill: rgba(0,0,0,0.7)");

        btnLogin.setOnAction(e -> {
            checkUser(stage);
        });
    }

    public void checkUser(Stage stage) {
        try {
            PreparedStatement stmt = con.prepareStatement("select perdoruesi.id, perdoruesi.pnt_id, perdoruesi.usr, perdoruesi.pw, perdoruesi.dep_id, punetoret.telefoni as tel, " +
                    "punetoret.email, (punetoret.emri || ' ' || punetoret.mbiemri) as pnt " +
                    "from perdoruesi, punetoret where " +
                    "lower(perdoruesi.usr) = lower(?) and perdoruesi.pw = ? and punetoret.id = perdoruesi.id limit 1");
            stmt.setString(1, txtUser.getText());
            stmt.setString(2, txtPw.getText());
            ResultSet rs = stmt.executeQuery();

            int r = 0;

            while (rs.next()) {
                r++;
                VariablatPublike.uid = rs.getInt("pnt_id");
                VariablatPublike.uid2 = rs.getInt("id");
                VariablatPublike.uemri = rs.getString("usr");
                VariablatPublike.pntTel = rs.getString("tel");
                VariablatPublike.pntEmail = rs.getString("email");
                VariablatPublike.pntEmri = rs.getString("pnt");
                hapDritarenKryesore(stage, rs.getString("usr"), rs.getInt("dep_id"));
            }

            if (r == 0) {
                txtUser.selectAll();
                txtPw.clear();
                ntf.setMessage(rb.getString("log_error"));
                ntf.setType(NotificationType.ERROR);
                ntf.show();
            }

        } catch (NullPointerException npe) {
            ntf.setMessage(rb.getString("log_error_server"));
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapDritarenKryesore(Stage login, String username, int dep_id) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        ResourceBundle bundle = null;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (properties.getProperty("lang").equals("English")) {
            bundle = ResourceBundle.getBundle("resources.Language_en", new Locale("en", "EN"));
        } else if (properties.getProperty("lang").equals("German")) {
            bundle = ResourceBundle.getBundle("resources.Language_de", new Locale("de", "DE"));
        } else {
            bundle = ResourceBundle.getBundle("resources.Language_sq", new Locale("sq", "SQ"));
        }
        FXMLLoader dashboard = new FXMLLoader(getClass().getResource("/sample/sample.fxml"), bundle);

        Controller con = new Controller();
        con.setStage(stage);
        con.setPntId(dep_id);

        dashboard.setController(con);
        Parent parent = null;
        try {
            parent = dashboard.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(parent, 1100, 600);
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(1100);
        stage.show();
        login.close();
    }

    @FXML
    private void openSettings() {
        try {
            Stage stage = new Stage();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ChangeServer.fxml"), bundle);
            ChangeServer cs = new ChangeServer();
            cs.setStage(stage);
            loader.setController(cs);
            Parent parent = loader.load();

            Scene scene = new Scene(parent, 300, 500);
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openGithub(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/About.fxml"), rb);
            Parent parent = loader.load();

            Scene scene = new Scene(parent, 300, 270);

            scene.setOnMouseClicked(e -> {
                stage.close();
            });

            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ESCAPE) || e.getCode().equals(KeyCode.ENTER)) {
                    stage.close();
                }
            });

            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
