package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-04.
 */
public class Raportet implements Initializable {

    @FXML private DatePicker dpPrej, dpDeri;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpPrej.setConverter(VariablatPublike.converter);
        dpDeri.setConverter(VariablatPublike.converter);

        dpPrej.setValue(LocalDate.now());
        dpDeri.setValue(LocalDate.now());
    }
}
