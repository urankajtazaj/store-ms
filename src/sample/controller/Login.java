package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.io.IOException;
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

    @FXML public Button btnLogin, btnSetting;
    @FXML private TextField txtUser, txtPw;

    private Stage stage;

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnLogin.setOnAction(e -> {
            checkUser(stage);
        });

    }

    public void checkUser (Stage stage){
        try {
            PreparedStatement stmt = con.prepareStatement("select id, pnt_id, usr, pw from perdoruesi where " +
                    "lower(usr) = lower(?) and pw = ? limit 1");
            stmt.setString(1, txtUser.getText());
            stmt.setString(2, txtPw.getText());
            ResultSet rs = stmt.executeQuery();

            int r = 0;

            while (rs.next()) {
                r++;
                VariablatPublike.uid = rs.getInt("pnt_id");
                VariablatPublike.uemri = rs.getString("usr");
                hapDritarenKryesore(stage, rs.getString("usr"), rs.getInt("pnt_id"));
            }

            if (r == 0) {
                System.out.println(r);
                txtUser.selectAll();
                txtPw.clear();

                txtUser.getStyleClass().add("wrong");
                txtPw.getStyleClass().add("wrong");
            }

        }catch (Exception e) { e.printStackTrace(); }
    }

    private void hapDritarenKryesore(Stage login, String username, int pntId) {
        Stage stage = new Stage();
        FXMLLoader dashboard = new FXMLLoader(getClass().getResource("/sample/sample.fxml"));

        Controller con = new Controller();
        con.setUser(username);

        dashboard.setController(con);
        Parent parent = null;
        try {
            parent = dashboard.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(parent, 1100, 600);
        scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        login.close();
    }

}
