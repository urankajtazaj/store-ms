package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import sample.constructors.FaturaDhenat;
import sample.constructors.Faturat;

import javax.xml.crypto.Data;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-04.
 */
public class Raportet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    @FXML private DatePicker dpPrej, dpDeri;
    @FXML private TableView tblFatura, tblDhenat;
    @FXML private Label lTotal;
    private double total;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tblFatura.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblDhenat.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dpPrej.setConverter(VariablatPublike.converter);
        dpDeri.setConverter(VariablatPublike.converter);

        dpPrej.setValue(LocalDate.now());
        dpDeri.setValue(LocalDate.now());

        tblFatura.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) merrTeDhenat(((Faturat)nv).getId());
        });

        merrFaturat();

    }

    @FXML
    private void merrFaturat (){
        tblDhenat.getItems().clear();
        try (PreparedStatement pstmt = con.prepareStatement("select distinct * from vfatura where formatdatetime(koha, 'yyyy-MM-dd') >= ? " +
                "and formatdatetime(koha, 'yyyy-MM-dd') <= ? order by koha desc")) {
            pstmt.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            pstmt.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));

            ResultSet rs = pstmt.executeQuery();
            ObservableList<Faturat> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Faturat(rs.getInt("rec_id"), rs.getString("punetori"), rs.getString("emri"), rs.getString("koha"),
                        VariablatPublike.decimalFormat.format(rs.getDouble("cash"))));
            }
            tblFatura.setItems(data);

        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrTeDhenat(int id) {
        try (PreparedStatement pstmt = con.prepareStatement("select p_id, produkti, qmimi_shitjes, sasia from vshitjet where red_id = ?")) {
            pstmt.setInt(1, id);
            total = 0;
            ResultSet rs = pstmt.executeQuery();

            ObservableList<FaturaDhenat> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new FaturaDhenat(rs.getString("p_id"), rs.getString("produkti"), VariablatPublike.decimalFormat.format(rs.getDouble("qmimi_shitjes")),
                        VariablatPublike.decimal.format(rs.getDouble("sasia"))));
                total += rs.getDouble("qmimi_shitjes") * rs.getDouble("sasia");
            }
            tblDhenat.setItems(data);
            lTotal.setText(VariablatPublike.decimalFormat.format(total));

        }catch (Exception e) {e.printStackTrace();}
    }

}
