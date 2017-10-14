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
import java.util.ResourceBundle;

/**
 * Created by uran on 17-06-14.
 */
public class ChangeServer implements Initializable {

    @FXML private TextField txtSrv, txtEmri, txtFiskal, txtBanka, txtKonto, txtIban, txtSwift;
    @FXML private ComboBox<String> cbTheme;

    private Stage stage;

    Notification ntf = new Notification();

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        String ip = null, emri = null, theme = null, fiskal = null, banka = null, konto = null, iban = null, swift = null;
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            ip = br.readLine();
            emri = br.readLine();
            theme = br.readLine();
            fiskal = br.readLine();
            banka = br.readLine();
            konto = br.readLine();
            iban = br.readLine();
            swift = br.readLine();
        }catch (Exception e) { e.printStackTrace(); }

        txtSrv.setText(ip != null ? ip.split(":")[1] : "");
        txtEmri.setText(emri != null ? emri.split(":")[1] : "");
        cbTheme.getSelectionModel().select(theme != null ? theme.split(":")[1] : "Dark");
        txtFiskal.setText(fiskal != null ? fiskal.split(":")[1] : "");
        txtBanka.setText(banka != null ? banka.split(":")[1] : "");
        txtKonto.setText(konto != null ? konto.split(":")[1] : "");
        txtIban.setText(iban != null ? iban.split(":")[1] : "");
        txtSwift.setText(swift != null ? swift.split(":")[1] : "");
    }

    @FXML
    private void ruajSrv(){
        File dir = new File(System.getProperty("user.home") + "/store-ms-files/config");
        dir.mkdir();

        File file = new File(dir.getAbsolutePath() + "/config.txt");

        try(FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("server:" + (txtSrv.getText().isEmpty() ? "localhost" : txtSrv.getText()) + "\n");
            bw.write("emri:" + txtEmri.getText() + "\n");
            bw.write("theme:" + cbTheme.getSelectionModel().getSelectedItem() + "\n");
            bw.write("nr.fiskal:" + txtFiskal.getText() + "\n");
            bw.write("banka:" + txtBanka.getText() + "\n");
            bw.write("konto:" + txtKonto.getText() + "\n");
            bw.write("iban:" + txtIban.getText() + "\n");
            bw.write("swift:" + txtSwift.getText() + "\n");
            VariablatPublike.server = txtSrv.getText().isEmpty() ? "localhost" : txtSrv.getText();
            VariablatPublike.nrFiskal = txtFiskal.getText();
            VariablatPublike.BANKA = txtBanka.getText();
            VariablatPublike.KONTO = txtKonto.getText();
            VariablatPublike.SWIFT = txtSwift.getText();
            VariablatPublike.IBAN = txtIban.getText();
            stage.close();
            ntf.setMessage("Te dhenat u ruajten me sukses dhe do te aplikohen pas ristartimit te aplikacionit");
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
