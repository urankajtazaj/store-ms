package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-02.
 */
public class LajmeroRed implements Initializable {

    @FXML
    public Button btnOk;
    @FXML private Label mesazhi;

    public void setMesazhi(String msg){
        this.mesazhi.setText(msg);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
