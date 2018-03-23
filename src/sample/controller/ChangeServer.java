package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.io.*;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-06-14.
 */
public class ChangeServer implements Initializable {

    @FXML private TextField txtSrv, txtEmri, txtFiskal, txtBanka, txtKonto, txtIban, txtSwift, txtAdresa;
    @FXML private ComboBox<String> cbTheme, cbLang;

    private Stage stage;

    Notification ntf = new Notification();

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Properties props = new Properties();

        try {

            FileInputStream in = new FileInputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties");
            props.load(in);

            System.out.println(props.getProperty("emri"));

            txtSrv.setText(props.getProperty("server"));
            txtEmri.setText(props.getProperty("emri"));
            cbTheme.getSelectionModel().select(props.getProperty("theme"));
            txtFiskal.setText(props.getProperty("nr_fiskal"));
            txtBanka.setText(props.getProperty("banka"));
            txtKonto.setText(props.getProperty("konto"));
            txtIban.setText(props.getProperty("iban"));
            txtSwift.setText(props.getProperty("swift"));
            txtAdresa.setText(props.getProperty("adresa"));
            cbLang.getSelectionModel().select(props.getProperty("lang"));

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        File dir = new File(System.getProperty("user.home") + "/store-ms-files/config");
        dir.mkdir();

        File file = new File(dir.getAbsolutePath() + "/config.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void ruajSrv(){
        Properties confProp = new Properties();

        try {
            Properties props = new Properties();
            FileOutputStream out = new FileOutputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties");

            props.setProperty("server", (txtSrv.getText().isEmpty() ? "localhost" : txtSrv.getText()));
            props.setProperty("emri", txtEmri.getText());
            props.setProperty("theme", cbTheme.getSelectionModel().getSelectedItem());
            props.setProperty("nr_fiskal", txtFiskal.getText());
            props.setProperty("banka", txtBanka.getText());
            props.setProperty("konto", txtKonto.getText());
            props.setProperty("iban", txtIban.getText());
            props.setProperty("swift", txtSwift.getText());
            props.setProperty("adresa", txtAdresa.getText());
            props.setProperty("lang", cbLang.getSelectionModel().getSelectedItem());

            props.store(out, null);
            out.close();

            VariablatPublike.server = txtSrv.getText().isEmpty() ? "localhost" : txtSrv.getText();
            VariablatPublike.nrFiskal = txtFiskal.getText();
            VariablatPublike.BANKA = txtBanka.getText();
            VariablatPublike.KONTO = txtKonto.getText();
            VariablatPublike.SWIFT = txtSwift.getText();
            VariablatPublike.IBAN = txtIban.getText();
            VariablatPublike.ADRESA = txtAdresa.getText();
            VariablatPublike.styleSheet = cbTheme.getSelectionModel().getSelectedItem().equals("Dark") ? "/sample/style/style.css" : "/sample/style/styleLight.css";
            VariablatPublike.LANG = cbLang.getSelectionModel().getSelectedItem();

            ResourceBundle resourceBundle = null;
            if (cbLang.getSelectionModel().getSelectedItem().equals("English")) {
                resourceBundle = ResourceBundle.getBundle("resources.Language_en", new Locale("en", "EN"));
            } else if (cbLang.getSelectionModel().getSelectedItem().equals("German")) {
                resourceBundle = ResourceBundle.getBundle("resources.Language_de", new Locale("de", "DE"));
            } else {
                resourceBundle = ResourceBundle.getBundle("resources.Language_sq", new Locale("sq", "SQ"));
            }
            stage.close();

            ntf.setMessage(resourceBundle.getString("sukses_change_server"));
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setType(NotificationType.SUCCESS);
            ntf.show();
        }catch (Exception e) {
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setMessage(e.getMessage());
            ntf.setType(NotificationType.ERROR);
            ntf.show();
            e.printStackTrace(); }
    }

}
