package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Enums.*;
import sample.Enums.ButtonType;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by uran on 17-04-30.
 */
public class ShtoPunetoret implements Initializable {

    DB db = new DB();
    private Connection con = db.connect();

    Notification ntf = new Notification();

    @FXML private BorderPane shtoBp, childBp;
    @FXML Button btnRuaj;
    @FXML private Hyperlink addDp;
    @FXML private TextField emri, mbiemri, paga, titulli, telefoni, email, adresa, qyteti, shteti, txtUser;
    @FXML private PasswordField txtPw;
    @FXML private ComboBox departamenti, gjinia, cbStatusi;
    @FXML private DatePicker punesuar, ditelindja;
    @FXML private Label lblFoto;
    @FXML private Button btnFotoHape;

    int id = -1;
    char mode = 'i';

    public void setId (int id)
    {
        this.id = id;
    }
    public void setMode (char mode) {
        this.mode = mode;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        punesuar.setValue(LocalDate.now());
        cbStatusi.getSelectionModel().select(1);

        emri.focusedProperty().addListener((o, ov, nv) -> {
            if (nv.booleanValue() == false && !mbiemri.getText().isEmpty()) {
                txtUser.setText(emri.getText().toLowerCase() + "." + mbiemri.getText().toLowerCase());
            }
        });

        mbiemri.focusedProperty().addListener((o, ov, nv) -> {
            if (nv.booleanValue() == false && !emri.getText().isEmpty()) {
                txtUser.setText(emri.getText().toLowerCase() + "." + mbiemri.getText().toLowerCase());
            }
        });

        ditelindja.setConverter(VariablatPublike.converter);
        punesuar.setConverter(VariablatPublike.converter);

        btnRuaj.setOnAction(e -> {
            shtoPnt(mode, id);
        });

        try {
            departamenti.getItems().clear();
            merrDeps("Select * from departamenti", departamenti);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rregulloPnt(id);
    }

    public void merrDeps(String q, ComboBox<String> cb) throws Exception{
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(q);

        while (rs.next()) {
            if (cb != null) cb.getItems().add(rs.getString("departamenti"));
            VariablatPublike.dep.put(rs.getString("departamenti"), rs.getInt("id"));
            VariablatPublike.revDep.put(rs.getInt("id"), rs.getString("departamenti"));
        }
        if (cb != null) cb.getSelectionModel().select(0);

        for (Map.Entry<String, Integer> m : VariablatPublike.dep.entrySet()) {
            VariablatPublike.revDep.put(m.getValue(), m.getKey());
        }

    }

    private void rregulloPnt (int id) {
        try {

            Statement stmt = con.createStatement();
            Statement stmt2 = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from punetoret where id = " + id + " limit 1");
            ResultSet rs2 = stmt2.executeQuery("select usr, pw from perdoruesi where pnt_id = " + id + " limit 1");

            while (rs.next()) {
                emri.setText(rs.getString("emri"));
                mbiemri.setText(rs.getString("mbiemri"));
                paga.setText(rs.getDouble("paga") + "");
                telefoni.setText(rs.getString("telefoni"));
                email.setText(rs.getString("email"));
                adresa.setText(rs.getString("adresa"));
                qyteti.setText(rs.getString("qyteti"));
                shteti.setText(rs.getString("shteti"));
                lblFoto.setText(rs.getString("foto"));
                cbStatusi.getSelectionModel().select(rs.getInt("statusi"));
                departamenti.getSelectionModel().select(VariablatPublike.revDep.get(rs.getInt("dep_id")));
                gjinia.getSelectionModel().select(rs.getInt("gjinia"));
                ditelindja.setValue(rs.getDate("ditelindja").toLocalDate());
                punesuar.setValue(LocalDate.parse(rs.getString("data_punesimit"), VariablatPublike.dtf));
            }

            rs2.next();
            txtUser.setText(rs2.getString("usr"));
            txtPw.setText(rs2.getString("pw"));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shtoPnt(int mode, int id){
        try {
            PreparedStatement pstmt = null;
            PreparedStatement pstmt2 = null;
            if (mode == 'i') {
                pstmt2 = con.prepareStatement("insert into perdoruesi values (null, (select max(id) from punetoret), ?, ?, ?)");
                pstmt = con.prepareStatement("INSERT INTO punetoret" +
                "(id, dep_id, emri, mbiemri, gjinia, ditelindja, paga, data_punesimit, statusi, " +
                        "telefoni, modifikuar, adresa, qyteti, shteti, email, data_krijimit, foto)" +
                "VALUES" +
                "(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_date(), ?)");
            }else if (mode == 'u' && id > 0) {
                pstmt2 = con.prepareStatement("update perdoruesi set usr = ?, pw = ?, dep_id = ? where pnt_id = " + id);
                pstmt = con.prepareStatement("update punetoret set " +
                "dep_id = ?, emri = ?, mbiemri = ?, gjinia = ?, ditelindja = ?, paga = ?, " +
                "data_punesimit = ?, statusi = ?, telefoni = ?, modifikuar = ?, adresa = ?, qyteti=?," +
                        "shteti=?,email=?,foto = ? " +
                "where id = " + id);
            }

            if (!emri.getText().equals("") && !mbiemri.getText().equals("")
                    && !paga.getText().equals("") && !punesuar.getEditor().getText().equals("")
                    && !telefoni.getText().equals("") && !email.getText().equals("")) {
                if (!paga.getText().trim().matches("^\\d+(\\.\\d)*$") || !telefoni.getText().trim().matches("[0-9 ]+")) {
                    ntf.setMessage("Fushat 'Paga' dhe 'Telefoni' duhet te permajne vetem numra");
                    ntf.setType(NotificationType.ERROR);
                    ntf.show();
                }else {
                    pstmt.setInt(1, VariablatPublike.dep.get(departamenti.getSelectionModel().getSelectedItem()));
                    pstmt.setString(2, emri.getText());
                    pstmt.setString(3, mbiemri.getText());
                    pstmt.setInt(4, gjinia.getSelectionModel().getSelectedItem() == "Mashkull" ? 1 : 0);
                    pstmt.setDate(5, java.sql.Date.valueOf(formatter.format(ditelindja.getValue())));
                    pstmt.setDouble(6, Double.parseDouble(paga.getText()));
                    pstmt.setDate(7, java.sql.Date.valueOf(formatter.format(punesuar.getValue())));
                    pstmt.setInt(8, cbStatusi.getSelectionModel().getSelectedIndex());
                    pstmt.setString(9, telefoni.getText());
                    pstmt.setString(10, formatterTime.format(LocalDateTime.now()));
                    pstmt.setString(11, adresa.getText());
                    pstmt.setString(12, qyteti.getText());
                    pstmt.setString(13, shteti.getText());
                    pstmt.setString(14, email.getText());
                    pstmt.setString(15, lblFoto.getText());
                    pstmt.execute();

                    pstmt2.setString(1, txtUser.getText());
                    pstmt2.setString(2, txtPw.getText());
                    pstmt2.setInt(3, VariablatPublike.dep.get(departamenti.getSelectionModel().getSelectedItem()));
                    pstmt2.execute();

                    pastro();
                    ntf.setMessage("Te dhenat u ruajten me sukses");
                    ntf.setType(NotificationType.SUCCESS);
                    ntf.show();
                }
            }else
                ntf.setMessage("Fushat e kerkuara duhet te permbajne te dhena");
                ntf.setType(NotificationType.ERROR);
                ntf.show();

        }catch (Exception ex) {ex.printStackTrace();}

    }

    private void pastro (){
        email.setText("");
        emri.setText("");
        mbiemri.setText("");
        paga.setText("");
        adresa.setText("");
        qyteti.setText("");
        shteti.setText("");
        telefoni.setText("");
        punesuar.setValue(LocalDate.now());
        ditelindja.getEditor().clear();
        departamenti.getSelectionModel().select(0);
        gjinia.getSelectionModel().select(0);
    }

}
