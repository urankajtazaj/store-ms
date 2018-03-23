package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-14.
 */
public class ShtoKonsumatoret implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    Notification ntf = new Notification();

    @FXML private TextField emri, adresa, qyteti, shteti, telefoni, email, fiskal;

    int id = 0;
    private BorderPane root;

    public void setRoot (BorderPane root){
        this.root = root;
    }
    public void setId (int id) {
        this.id = id;
    }

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        if (id > 0) mbushTeDhenat();
    }

    @FXML
    private void ruajExit (){
        try {
            PreparedStatement pstmt = null;

            if (id > 0) {
                pstmt = con.prepareStatement("update konsumatoret set " +
                        "emri = ?, email = ?, qyteti = ?, shteti = ?, adresa = ?, telefoni = ?, nr_fiskal = ? " +
                        "where id = ?");
                pstmt.setString(1, emri.getText());
                pstmt.setString(2, email.getText());
                pstmt.setString(3, qyteti.getText());
                pstmt.setString(4, shteti.getText());
                pstmt.setString(5, adresa.getText());
                pstmt.setString(6, telefoni.getText());
                pstmt.setString(7, fiskal.getText());
                pstmt.setInt(8, id);
            }else {
                pstmt = con.prepareStatement("insert into konsumatoret values (" +
                        "null, ?,?,?,?,1, current_date(), current_date(),?,?,?)");
                pstmt.setString(1, emri.getText());
                pstmt.setString(2, adresa.getText());
                pstmt.setString(3, telefoni.getText());
                pstmt.setString(4, email.getText());
                pstmt.setString(5, qyteti.getText());
                pstmt.setString(6, shteti.getText());
                pstmt.setString(7, fiskal.getText());
            }
            pstmt.execute();

            pstmt.close();
            pastro();
            ntf.setMessage(rb.getString("shk_sukses"));
            ntf.show();

        }catch (Exception e) {e.printStackTrace();}
    }

    private void pastro (){
        emri.setText("");
        email.setText("");
        telefoni.setText("");
        adresa.setText("");
        qyteti.setText("");
        shteti.setText("");
        fiskal.setText("");
    }

    private void mbushTeDhenat (){
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from konsumatoret where id = "+id+" limit 1");

            rs.next();
            emri.setText(rs.getString("emri"));
            email.setText(rs.getString("email"));
            telefoni.setText(rs.getString("telefoni"));
            qyteti.setText(rs.getString("qyteti"));
            shteti.setText(rs.getString("shteti"));
            adresa.setText(rs.getString("adresa"));
            fiskal.setText(rs.getString("nr_fiskal"));

        }catch (Exception e) { e.printStackTrace(); }
    }

}
