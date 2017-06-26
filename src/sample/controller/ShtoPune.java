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
            }
        }catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void ruajPunen (){
        tf.setText(txtEmriPunes.getText());
        if (id == 0) {
            askQuestion();
        }else {
            askQuestion(id);
        }
        stage.close();
    }

    @FXML
    private void fshiPunen(){
        ntf.setMessage("Te gjithe anetaret qe jane ne kete pune do te fshihen.\nA jeni te sigurte?");
        ntf.setButton(ButtonType.YES_NO);
        ntf.setType(NotificationType.ERROR);
        ntf.showAndWait();
        if (ntf.getDelete()) {
            fshi(id);
            vbox.getChildren().remove(hbox);
        }
    }

    private void fshi(int id){
        try(Statement st = con.createStatement()) {
            st.addBatch("delete from departamenti where id = " + id);
            st.addBatch("delete from punetoret where dep_id = " + id);
            st.addBatch("delete from perdoruesi where dep_id = " + id);
            st.executeBatch();
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setMessage("Puna e fshi me sukses");
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
            ntf.setMessage("Nese nuk zgjedhet te pakten njera prej menyve, anetari nuk mund te kyqet ne aplikacion. Vazhdoni?");
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
            ntf.setMessage("Nese nuk zgjedhet te pakten njera prej menyve, anetari nuk mund te kyqet ne aplikacion. Vazhdoni?");
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
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareCall(q2)) {

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

            ntf.setMessage("Puna u rregullua me sukses");
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();

        }catch (Exception e) {e.printStackTrace();}
    }

    private void shtoDepartamentin () {
        try (PreparedStatement s = con.prepareStatement("insert into priv values (" +
                "(select max(id) from departamenti), ?, ?, ?, ?, ?, ?, ?" +
                ")"); PreparedStatement ps = con.prepareStatement("insert into departamenti values (null, ?)")) {

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

            ntf.setMessage("Puna u shtua me sukses");
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();

        }catch (Exception e) { e.printStackTrace(); }
    }
}