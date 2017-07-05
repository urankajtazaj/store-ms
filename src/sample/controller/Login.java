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
import javafx.stage.Stage;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String theme = VariablatPublike.styleSheet.substring(VariablatPublike.styleSheet.length()-9, VariablatPublike.styleSheet.length());
        if (!theme.equals("style.css"))
            hyperlink.setStyle("-fx-text-fill: rgba(0,0,0,0.7)");

        btnLogin.setOnAction(e -> {
            checkUser(stage);
        });
    }

    public void checkUser(Stage stage) {
        try {
            PreparedStatement stmt = con.prepareStatement("select id, pnt_id, usr, pw, dep_id from perdoruesi where " +
                    "lower(usr) = lower(?) and pw = ? limit 1");
            stmt.setString(1, txtUser.getText());
            stmt.setString(2, txtPw.getText());
            ResultSet rs = stmt.executeQuery();

            int r = 0;

            while (rs.next()) {
                r++;
                VariablatPublike.uid = rs.getInt("pnt_id");
                VariablatPublike.uid2 = rs.getInt("id");
                VariablatPublike.uemri = rs.getString("usr");
                hapDritarenKryesore(stage, rs.getString("usr"), rs.getInt("dep_id"));
            }

            if (r == 0) {
                System.out.println(r);
                txtUser.selectAll();
                txtPw.clear();
                ntf.setMessage("Emri ose fjalekalimi jane gabim");
                ntf.setType(NotificationType.ERROR);
                ntf.show();
            }

        } catch (NullPointerException npe) {
            ntf.setMessage("Ju lutem kontrolloni serverin");
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapDritarenKryesore(Stage login, String username, int dep_id) {
        Stage stage = new Stage();
        FXMLLoader dashboard = new FXMLLoader(getClass().getResource("/sample/sample.fxml"));

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ChangeServer.fxml"));
            ChangeServer cs = new ChangeServer();
            cs.setStage(stage);
            loader.setController(cs);
            Parent parent = loader.load();

            Scene scene = new Scene(parent, 300, 250);
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openGithub(ActionEvent actionEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.github.com/urankajtazaj/store-ms"));
                } catch (Exception e) {

                }
            }
        }).start();
    }
}
