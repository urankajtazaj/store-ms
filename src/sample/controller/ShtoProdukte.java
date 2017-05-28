package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-27.
 */
public class ShtoProdukte implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    @FXML private TextField bc, emri, qmimiStd, qmimiShitjes, zbritje, stok, stokCrit;
    @FXML private ComboBox<String> cbKategoria, cbNjesia;

    private String patternDot = "[0-9.]+";
    private String patternEmpty = "^$|[0-9]+";
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

    private void addToDatabase(){
        String q = "insert into produktet values (null, ?, ?, ?, ?, ?, ?, current_timestamp(), ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, VariablatPublike.revProdKat.get(cbKategoria.getSelectionModel().getSelectedItem()));
            ps.setString(2, emri.getText());
            ps.setInt(3, Integer.parseInt(stok.getText()));
            ps.setDouble(4, Double.parseDouble(qmimiStd.getText()));
            ps.setDouble(5, Double.parseDouble(qmimiShitjes.getText()));
            ps.setString(6, cbNjesia.getSelectionModel().getSelectedItem());
            ps.setString(7, bc.getText());
            ps.setInt(8, zbritje.getText().isEmpty() ? 0 : Integer.parseInt(zbritje.getText()));
            ps.execute();
            MesazhetPublike.suksesDritarja("Produkti " + emri.getText() + ", u shtua me sukses.");
        }catch (NumberFormatException nfe) {
            MesazhetPublike.suksesDritarjaRed("Fushat ku kerkohen vetem numra nuk duhet te permbajne shkronja ose karaktere tjera.");
        }catch (Exception e){ e.printStackTrace(); }
    }

    @FXML
    private void shtoProdukt(){
        if (!bc.getText().isEmpty() && !emri.getText().isEmpty() && !qmimiShitjes.getText().isEmpty() && !stok.getText().isEmpty()) {
            if (Pattern.compile(patternDot).matcher(qmimiStd.getText()).matches() && Pattern.compile(patternDot).matcher(qmimiShitjes.getText()).matches() &&
                    Pattern.compile(patternEmpty).matcher(zbritje.getText()).matches() && Pattern.compile(pattern).matcher(stok.getText()).matches() &&
                    Pattern.compile(patternEmpty).matcher(stokCrit.getText()).matches() && Pattern.compile(pattern).matcher(bc.getText()).matches()) {
                addToDatabase();
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
