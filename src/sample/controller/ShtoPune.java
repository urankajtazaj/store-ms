package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-22.
 */
public class ShtoPune implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    private TextField tf;
    private Stage stage;
    private int id;

    @FXML private Button btnFshi;
    @FXML private TextField txtEmriPunes;
    @FXML private CheckBox cbShtepi, cbPunetoret, cbKonsumatoret, cbProduktet, cbShitjet, cbRaportet, cbRregullo, cbShikim, cbShtim, cbFshirje, cbRregullim;

    public void setTf (TextField tf) {
        this.tf = tf;
    }
    public void setId (int id) { this.id = id; }
    public void setStage (Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (id > 0) {
            selectCb();
            txtEmriPunes.setText(tf.getText());
        }else {
            btnFshi.setDisable(true);
        }
    }

    private void selectCb (){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select * from priv where dep_id = " + id)) {
            while (rs.next()) {
                cbShtepi.setSelected(rs.getBoolean("shtepi"));
                cbRregullo.setSelected(rs.getBoolean("settings"));
                cbProduktet.setSelected(rs.getBoolean("produktet"));
                cbRaportet.setSelected(rs.getBoolean("raportet"));
                cbKonsumatoret.setSelected(rs.getBoolean("konsumatoret"));
                cbShitjet.setSelected(rs.getBoolean("shitje"));
                cbPunetoret.setSelected(rs.getBoolean("punetoret"));

                cbShikim.setSelected(rs.getBoolean("lexo"));
                cbRregullim.setSelected(rs.getBoolean("rregullo"));
                cbShtim.setSelected(rs.getBoolean("shto"));
                cbFshirje.setSelected(rs.getBoolean("fshi"));
            }
        }catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void ruajPunen (){
        tf.setText(txtEmriPunes.getText());
        if (id == 0) {
            shtoDepartamentin();
        }else {
            rregulloDepartamentin(id);
        }
        stage.close();
    }

    private void rregulloDepartamentin(int id) {
        String q = "update priv set shto = ?, fshi = ?, lexo = ?, rregullo = ?, shtepi = ?, punetoret = ?, konsumatoret = ?, raportet = ?, shitje = ?," +
                "produktet = ?, settings = ? where dep_id = ?";
        String q2 = "update departamenti set departamenti = ? where id = ?";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareCall(q2)) {

            ps.setInt(1, cbShtim.isSelected() ? 1 : 0);
            ps.setInt(2, cbFshirje.isSelected() ? 1 : 0);
            ps.setInt(3, cbShikim.isSelected() ? 1 : 0);
            ps.setInt(4, cbRregullim.isSelected() ? 1 : 0);
            ps.setInt(5, cbShtepi.isSelected() ? 1 : 0);
            ps.setInt(6, cbPunetoret.isSelected() ? 1 : 0);
            ps.setInt(7, cbKonsumatoret.isSelected() ? 1 : 0);
            ps.setInt(8, cbRaportet.isSelected() ? 1 : 0);
            ps.setInt(9, cbShitjet.isSelected() ? 1 : 0);
            ps.setInt(10, cbProduktet.isSelected() ? 1 : 0);
            ps.setInt(11, cbRregullo.isSelected() ? 1 : 0);
            ps.setInt(12, id);
            ps.execute();

            ps2.setString(1, txtEmriPunes.getText());
            ps2.setInt(2, id);
            ps2.execute();

        }catch (Exception e) {e.printStackTrace();}
    }

    private void shtoDepartamentin () {
        try (PreparedStatement s = con.prepareStatement("insert into priv values (" +
                "(select max(id) from departamenti), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                ")"); PreparedStatement ps = con.prepareStatement("insert into departamenti values (null, ?)")) {

            ps.setString(1, txtEmriPunes.getText());
            ps.execute();
            
            s.setInt(1, cbShtim.isSelected() ? 1 : 0);
            s.setInt(2, cbFshirje.isSelected() ? 1 : 0);
            s.setInt(3, cbShikim.isSelected() ? 1 : 0);
            s.setInt(4, cbRregullim.isSelected() ? 1 : 0);
            s.setInt(5, cbShtepi.isSelected() ? 1 : 0);
            s.setInt(6, cbPunetoret.isSelected() ? 1 : 0);
            s.setInt(7, cbKonsumatoret.isSelected() ? 1 : 0);
            s.setInt(8, cbRaportet.isSelected() ? 1 : 0);
            s.setInt(9, cbShitjet.isSelected() ? 1 : 0);
            s.setInt(10, cbProduktet.isSelected() ? 1 : 0);
            s.setInt(11, cbRregullo.isSelected() ? 1 : 0);
            s.execute();

        }catch (Exception e) { e.printStackTrace(); }
    }
}