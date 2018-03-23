package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ShtoShpenzim implements Initializable {

    @FXML public DatePicker dpData;
    @FXML public TextField txtArsyeja, txtShuma;
    @FXML public ComboBox<String> cbPnt;
    @FXML public Button btnRuaj;

    Notification ntf = new Notification();
    Connection con = new DB().connect();

    private int id = 0;

    public void setId(int id) {
        this.id = id;
    }

    Stage stage;

    ResourceBundle rb;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        merrPersonin();
        dpData.setConverter(VariablatPublike.converter);
        dpData.setValue(LocalDate.now());

        if (id > 0) {
            merrTeDhenat(id);
        }

    }

    private void merrTeDhenat(int id) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from shpenzimet where id = " + id + " limit 1");

            while (rs.next()) {
                txtArsyeja.setText(rs.getString("arsyeja"));
                txtShuma.setText(rs.getDouble("shuma") + "");
                cbPnt.getEditor().setText(rs.getString("personi"));
                dpData.setValue(LocalDate.parse(rs.getString("data")));
            }
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    private void merrPersonin() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select distinct personi from shpenzimet");

            cbPnt.getItems().clear();
            while (rs.next()) {
                cbPnt.getItems().add(rs.getString("personi"));
            }

            rs.close();

        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void shtoShpenzim() {
        if (!txtArsyeja.getText().isEmpty() || !txtShuma.getText().isEmpty() || !cbPnt.getEditor().getText().isEmpty() || !dpData.getEditor().getText().isEmpty()) {
            double shuma;
            try {
                shuma = Double.parseDouble(txtShuma.getText());
                PreparedStatement pst = null;
                if (id == 0) {
                    pst = con.prepareStatement("insert into shpenzimet values(null, ?, ?, ?, ?)");
                } else {
                    pst = con.prepareStatement("update shpenzimet set shuma = ?, arsyeja = ?, data = ?, personi = ? where id = " + id);
                }
                pst.setDouble(1, shuma);
                pst.setString(2, txtArsyeja.getText());
                pst.setDate(3, java.sql.Date.valueOf(dpData.getValue()));
                pst.setString(4, cbPnt.getEditor().getText());
                pst.execute();
                stage.close();
                ntf.setMessage(rb.getString("shpun_add_sukses"));
                ntf.setType(NotificationType.SUCCESS);
                ntf.setButton(ButtonType.NO_BUTTON);
                ntf.show();
            } catch (NumberFormatException nfe) {
                ntf.setMessage(rb.getString("set_error_nr_format"));
                ntf.setType(NotificationType.ERROR);
                ntf.setButton(ButtonType.NO_BUTTON);
                ntf.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void closeShpenzim() {
        stage.close();
    }

}
