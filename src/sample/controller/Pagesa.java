package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Enums.NotificationType;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-06-07.
 */
public class Pagesa implements Initializable {

    @FXML private TextField txtPagesa;

    Notification ntf = new Notification();

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

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
    }

    @FXML
    public void shtoPagesen(){
        if (Pattern.compile("[0-9.]+").matcher(txtPagesa.getText()).matches()) {
            BigDecimal ttl = new BigDecimal(total+"");
//            ttl = ttl.add(ttl.multiply(new BigDecimal(VariablatPublike.tvsh/100)));
            ttl = ttl.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal pag = new BigDecimal(txtPagesa.getText()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
//            if (ttl.compareTo(pag) <= 0) {
                lPagesa.setText(VariablatPublike.toMoney(pag.doubleValue()));
                pagesa = pag;
                lKusuri.setText(VariablatPublike.toMoney(pagesa.subtract(ttl)));
                stage.close();
//            }else {
//                ntf.setMessage("Pagesa nuk mund te jete me e vogel se shuma qe duhet paguar");
//                ntf.setType(NotificationType.ERROR);
//                ntf.show();
//                txtPagesa.clear();
//            }
        }else {
            ntf.setMessage(rb.getString("pagesa_msg"));
            ntf.setType(NotificationType.ERROR);
            ntf.show();
            txtPagesa.clear();
        }
    }

}
