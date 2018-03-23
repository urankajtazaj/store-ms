package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

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
    private int id = 0;
    private VBox vbox;
    private HBox hbox;

    Notification ntf = new Notification();

    @FXML private Button btnFshi;
    @FXML private TextField txtEmriPunes;
    @FXML private CheckBox cbShtepi, cbPunetoret, cbKonsumatoret, cbProduktet, cbShitjet, cbRaportet, cbRregullo;
    @FXML private CheckBox prod_add, prod_edit, prod_del, pnt_add, pnt_edit, pnt_del, kons_add, kons_edit, kons_del, rep_edit, rep_del;
    @FXML private VBox cbVb1, cbVb2;

    public void setTf (TextField tf) {
        this.tf = tf;
    }
    public void setId (int id) { this.id = id; }
    public void setStage (Stage stage) {
        this.stage = stage;
    }
    public void setVbox (VBox vbox) {
        this.vbox = vbox;
    }
    public void setHBox(HBox HBox) {
        this.hbox = HBox;
    }

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rb = resources;

        cbProduktet.setOnAction(e -> {
            if (cbProduktet.isSelected()) {
                prod_add.setDisable(false);
                prod_edit.setDisable(false);
                prod_del.setDisable(false);
            } else {
                prod_del.setSelected(false);
                prod_edit.setSelected(false);
                prod_add.setSelected(false);
                prod_add.setDisable(true);
                prod_edit.setDisable(true);
                prod_del.setDisable(true);
            }
        });

        cbPunetoret.setOnAction(e -> {
            if (cbPunetoret.isSelected()) {
                pnt_add.setDisable(false);
                pnt_edit.setDisable(false);
                pnt_del.setDisable(false);
            } else {
                pnt_del.setSelected(false);
                pnt_edit.setSelected(false);
                pnt_add.setSelected(false);
                pnt_add.setDisable(true);
                pnt_edit.setDisable(true);
                pnt_del.setDisable(true);
            }
        });

        cbKonsumatoret.setOnAction(e -> {
            if (cbKonsumatoret.isSelected()) {
                kons_add.setDisable(false);
                kons_edit.setDisable(false);
                kons_del.setDisable(false);
            } else {
                kons_del.setSelected(false);
                kons_edit.setSelected(false);
                kons_add.setSelected(false);
                kons_add.setDisable(true);
                kons_edit.setDisable(true);
                kons_del.setDisable(true);
            }
        });

        cbRaportet.setOnAction(e -> {
            if (cbRaportet.isSelected()) {
                rep_edit.setDisable(false);
                rep_del.setDisable(false);
            } else {
                rep_del.setSelected(false);
                rep_edit.setSelected(false);
                rep_edit.setDisable(true);
                rep_del.setDisable(true);
            }
        });

        if (id > 0) {
            selectCb();
            txtEmriPunes.setText(tf.getText());
        }else {
            btnFshi.setDisable(true);
        }
    }

    private void selectCb (){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select * from priv where dep_id = " + id);
             Statement s2 = con.createStatement(); ResultSet rs2 = s2.executeQuery("select * from mundesite where dep_id = " + id)) {
            while (rs.next()) {
                cbShtepi.setSelected(rs.getBoolean("shtepi"));
                cbRregullo.setSelected(rs.getBoolean("settings"));
                cbProduktet.setSelected(rs.getBoolean("produktet"));
                cbRaportet.setSelected(rs.getBoolean("raportet"));
                cbKonsumatoret.setSelected(rs.getBoolean("konsumatoret"));
                cbShitjet.setSelected(rs.getBoolean("shitje"));
                cbPunetoret.setSelected(rs.getBoolean("punetoret"));
            }

            while (rs2.next()) {
                prod_add.setSelected(rs2.getBoolean("prod_add"));
                prod_edit.setSelected(rs2.getBoolean("prod_edit"));
                prod_del.setSelected(rs2.getBoolean("prod_del"));
                pnt_add.setSelected(rs2.getBoolean("pnt_add"));
                pnt_edit.setSelected(rs2.getBoolean("pnt_edit"));
                pnt_del.setSelected(rs2.getBoolean("pnt_del"));
                kons_add.setSelected(rs2.getBoolean("kons_add"));
                kons_edit.setSelected(rs2.getBoolean("kons_edit"));
                kons_del.setSelected(rs2.getBoolean("kons_del"));
                rep_edit.setSelected(rs2.getBoolean("rep_edit"));
                rep_del.setSelected(rs2.getBoolean("rep_del"));
            }
        }catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void ruajPunen (){
        tf.setText(txtEmriPunes.getText());
        if (!tf.getText().isEmpty()) {
            if (id == 0) {
                askQuestion();
            } else {
                askQuestion(id);
            }
            stage.close();
        }
    }

    @FXML
    private void fshiPunen(){
        ntf.setMessage(rb.getString("shp_del_q"));
        ntf.setButton(ButtonType.YES_NO);
        ntf.setType(NotificationType.ERROR);
        ntf.showAndWait();
        if (ntf.getDelete()) {
            fshi(id);
            vbox.getChildren().remove(hbox);
        }
    }

    @FXML
    private void mbyllDritaren() {
        stage.close();
    }

    private void fshi(int id){
        try(Statement st = con.createStatement()) {
            st.addBatch("delete from departamenti where id = " + id);
            st.addBatch("delete from punetoret where dep_id = " + id);
            st.addBatch("delete from perdoruesi where dep_id = " + id);
            st.executeBatch();
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setMessage(rb.getString("shp_del_sukses"));
            ntf.show();
            stage.close();
        }catch (Exception e) {e.printStackTrace();}
    }


    private boolean checkingBoxes(VBox vb1, VBox vb2){
        for (Node n : vb1.getChildren()) {
            if (n instanceof CheckBox)
                if (((CheckBox) n).isSelected()) return true;
        }

        for (Node n : vb2.getChildren()) {
            if (n instanceof CheckBox)
                if (((CheckBox) n).isSelected()) return true;
        }

        return false;
    }


    private void askQuestion(int id){
        if (checkingBoxes(cbVb1, cbVb2)) {
            rregulloDepartamentin(id);
        }else {
            ntf.setMessage(rb.getString("shp_zgjedh_nje"));
            ntf.setButton(ButtonType.YES_NO);
            ntf.setType(NotificationType.ERROR);
            ntf.showAndWait();
            if (ntf.getDelete()) rregulloDepartamentin(id);
        }
    }

    private void askQuestion(){
        if (checkingBoxes(cbVb1, cbVb2)) {
            shtoDepartamentin();
        }else {
            ntf.setMessage(rb.getString("shp_zgjedh_nje"));
            ntf.setButton(ButtonType.YES_NO);
            ntf.setType(NotificationType.ERROR);
            ntf.showAndWait();
            if (ntf.getDelete()) shtoDepartamentin();
        }
    }


    private void rregulloDepartamentin(int id) {
        String q = "update priv set shtepi = ?, punetoret = ?, konsumatoret = ?, raportet = ?, shitje = ?," +
                "produktet = ?, settings = ? where dep_id = ?";
        String q2 = "update departamenti set departamenti = ? where id = ?";
        String q3 = "update mundesite set prod_add = ?, prod_edit = ?, prod_del = ?, pnt_add = ?, pnt_edit = ?, pnt_del = ?, kons_add = ?, kons_edit = ?, kons_del = ?, rep_edit = ?, rep_del = ? " +
                "where dep_id = ?";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareCall(q2); PreparedStatement psm = con.prepareCall(q3)) {

            ps.setInt(1, cbShtepi.isSelected() ? 1 : 0);
            ps.setInt(2, cbPunetoret.isSelected() ? 1 : 0);
            ps.setInt(3, cbKonsumatoret.isSelected() ? 1 : 0);
            ps.setInt(4, cbRaportet.isSelected() ? 1 : 0);
            ps.setInt(5, cbShitjet.isSelected() ? 1 : 0);
            ps.setInt(6, cbProduktet.isSelected() ? 1 : 0);
            ps.setInt(7, cbRregullo.isSelected() ? 1 : 0);
            ps.setInt(8, id);
            ps.execute();

            ps2.setString(1, txtEmriPunes.getText());
            ps2.setInt(2, id);
            ps2.execute();

            psm.setInt(1, prod_add.isSelected() ? 1 : 0);
            psm.setInt(2, prod_edit.isSelected() ? 1 : 0);
            psm.setInt(3, prod_del.isSelected() ? 1 : 0);
            psm.setInt(4, pnt_add.isSelected() ? 1 : 0);
            psm.setInt(5, pnt_edit.isSelected() ? 1 : 0);
            psm.setInt(6, pnt_del.isSelected() ? 1 : 0);
            psm.setInt(7, kons_add.isSelected() ? 1 : 0);
            psm.setInt(8, kons_edit.isSelected() ? 1 : 0);
            psm.setInt(9, kons_del.isSelected() ? 1 : 0);
            psm.setInt(10, rep_edit.isSelected() ? 1 : 0);
            psm.setInt(11, rep_del.isSelected() ? 1 : 0);
            psm.setInt(12, id);
            psm.execute();

            ntf.setMessage(rb.getString("shpun_update_sukses"));
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();

        }catch (Exception e) {e.printStackTrace();}
    }

    private void shtoDepartamentin () {
        try (PreparedStatement s = con.prepareStatement("insert into priv values (" +
                "(select max(id) from departamenti), ?, ?, ?, ?, ?, ?, ?" +
                ")"); PreparedStatement ps = con.prepareStatement("insert into departamenti values (null, ?)");
            PreparedStatement psm = con.prepareStatement("insert into mundesite values (null, (select max(id) from departamenti), ?,?,?,?,?,?,?,?,?,?,?)")
        ) {

            ps.setString(1, txtEmriPunes.getText());
            ps.execute();

            s.setInt(1, cbShtepi.isSelected() ? 1 : 0);
            s.setInt(2, cbPunetoret.isSelected() ? 1 : 0);
            s.setInt(3, cbKonsumatoret.isSelected() ? 1 : 0);
            s.setInt(4, cbRaportet.isSelected() ? 1 : 0);
            s.setInt(5, cbShitjet.isSelected() ? 1 : 0);
            s.setInt(6, cbProduktet.isSelected() ? 1 : 0);
            s.setInt(7, cbRregullo.isSelected() ? 1 : 0);
            s.execute();

            psm.setInt(1, prod_add.isSelected() ? 1 : 0);
            psm.setInt(2, prod_edit.isSelected() ? 1 : 0);
            psm.setInt(3, prod_del.isSelected() ? 1 : 0);
            psm.setInt(4, pnt_add.isSelected() ? 1 : 0);
            psm.setInt(5, pnt_edit.isSelected() ? 1 : 0);
            psm.setInt(6, pnt_del.isSelected() ? 1 : 0);
            psm.setInt(7, kons_add.isSelected() ? 1 : 0);
            psm.setInt(8, kons_edit.isSelected() ? 1 : 0);
            psm.setInt(9, kons_del.isSelected() ? 1 : 0);
            psm.setInt(10, rep_edit.isSelected() ? 1 : 0);
            psm.setInt(11, rep_del.isSelected() ? 1 : 0);
            psm.execute();

            ntf.setMessage(rb.getString("shpun_add_sukses"));
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();

        }catch (Exception e) { e.printStackTrace(); }
    }
}