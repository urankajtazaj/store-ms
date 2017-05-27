package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-04-19.
 */
public class PunetoretRregullo implements Initializable {

    @FXML public TextField txtEmriR, txtMbiemriR, txtQytetiR, txtDepR, txtRoliR;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setData(String emri, String mbiemri, String departamenti, String roli) {
        txtRoliR.setText(roli);
        txtEmriR.setText(emri);
        txtDepR.setText(departamenti);
        txtMbiemriR.setText(mbiemri);
    }

}
