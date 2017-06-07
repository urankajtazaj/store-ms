package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-06-07.
 */
public class Pagesa implements Initializable {

    @FXML private TextField txtPagesa;

    private Label lPagesa, lKusuri;
    private Stage stage;
    private double total, pagesa, ksr;

    public void setKsr (double ksr) {
        this.ksr = ksr;
    }

    public void setlKusuri (Label lKusuri) {
        this.lKusuri = lKusuri;
    }

    public void setPagesa (double pagesa) {
        this.pagesa = pagesa;
    }

    public void setTotal (double total) {
        this.total = total;
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public void setlPagesa (Label pagesa) {
        this.lPagesa = pagesa;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void shtoPagesen(){
        if (Pattern.compile("[0-9.]+").matcher(txtPagesa.getText()).matches()) {
            System.out.println(total);
            if (total <= Double.parseDouble(txtPagesa.getText())) {
                lPagesa.setText(VariablatPublike.decimalFormat.format(Double.parseDouble(txtPagesa.getText())));
                pagesa = Double.parseDouble(txtPagesa.getText());
                lKusuri.setText(VariablatPublike.decimalFormat.format(pagesa-total));
                stage.close();
            }else {
                MesazhetPublike.Lajmerim("Pagesa nuk mund te jete me e vogel se shuma qe duhet paguar",
                        MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.ERROR, 5);
                txtPagesa.clear();
            }
        }else {
            MesazhetPublike.Lajmerim("Pagesa duhet te permbaje vetem numra", MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.ERROR, 4);
            txtPagesa.clear();
        }
    }

}
