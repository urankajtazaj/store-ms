package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-27.
 */
public class ShtoProdukte implements Initializable {

    @FXML private TextField bc, emri, qmimiStd, qmimiShitjes, zbritje, stok, stokCrit;
    @FXML private ComboBox<String> cbKategoria, cbNjesia;

    private String patternDot = "^$|[0-9.]+";
    private String pattern = "[0-9]+";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cbKategoria.getItems().clear();
        Iterator<String> it = VariablatPublike.prodKat.iterator();
        while (it.hasNext()) {
            cbKategoria.getItems().add(it.next());
        }
        cbKategoria.getSelectionModel().select(0);
    }

    @FXML
    private void shtoProdukt(){
        if (!bc.getText().isEmpty() && !emri.getText().isEmpty() && !qmimiShitjes.getText().isEmpty() && !stok.getText().isEmpty()) {
            if (Pattern.compile(patternDot).matcher(qmimiStd.getText()).matches() && Pattern.compile(patternDot).matcher(qmimiShitjes.getText()).matches() &&
                    Pattern.compile(pattern).matcher(zbritje.getText()).matches() && Pattern.compile(pattern).matcher(stok.getText()).matches() &&
                    Pattern.compile(pattern).matcher(stokCrit.getText()).matches() && Pattern.compile(pattern).matcher(bc.getText()).matches()) {

            }
        }
    }

    @FXML
    private void pastroProdukt(){
        bc.clear();
        emri.clear();
        qmimiStd.clear();
        qmimiShitjes.clear();
        zbritje.clear();
        stok.clear();
        stokCrit.clear();
        cbKategoria.getSelectionModel().select(0);
        cbNjesia.getSelectionModel().select(0);
    }

}
