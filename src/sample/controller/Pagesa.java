package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
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
    private BigDecimal total, pagesa, ksr;

    public void setKsr (BigDecimal ksr) {
        this.ksr = ksr;
    }

    public void setlKusuri (Label lKusuri) {
        this.lKusuri = lKusuri;
    }

    public void setPagesa (BigDecimal pagesa) {
        this.pagesa = pagesa;
    }

    public BigDecimal getPagesa (){
        return this.pagesa;
    }

    public void setTotal (BigDecimal total) {
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
            BigDecimal ttl = new BigDecimal(total+"");
            BigDecimal pag = new BigDecimal(txtPagesa.getText());
            if (ttl.compareTo(pag) <= 0) {
                lPagesa.setText(VariablatPublike.decimalFormat.format(pag.doubleValue()));
                pagesa = pag;
                lKusuri.setText(VariablatPublike.decimalFormat.format(pagesa.subtract(ttl)));
                stage.close();
            }else {
                System.err.println(ttl.toString() + ", " + pag.toString());
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
