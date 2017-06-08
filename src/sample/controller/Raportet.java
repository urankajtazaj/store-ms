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
    @FXML private Label lTotal, lPagesa, lKusuri, lCurrTtl, lLastTtl, lKons, lKonsL;
    private double total, totalLast;

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
            merrTotal();
            merrKons();
        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrTotal () {
        String q = "select sum(total) as ttl from vshitjet where formatdatetime(koha_shitjes, 'yyyy-MM-dd') >= ? and " +
                "formatdatetime(koha_shitjes, 'yyyy-MM-dd') <= ?";
        String q2 = "select sum(total) as ttl from vshitjet where formatdatetime(koha_shitjes, 'yyyy-MM-dd') < ? and " +
                "koha_shitjes > " +
                "dateadd('day', -datediff('day',  ?, ?)-1, ?);";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareStatement(q2)) {

            ps.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));

            ps2.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(2, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(3, java.sql.Date.valueOf(dpDeri.getValue()));
            ps2.setDate(4, java.sql.Date.valueOf(dpPrej.getValue()));

            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            double last = 0;

            while (rs.next()) {
                totalLast = rs.getDouble("ttl");
            }

            while (rs2.next()) {
                last = rs2.getDouble("ttl");
            }

            double perc = ((totalLast-last)/last)*100;

            lCurrTtl.setText(VariablatPublike.decimalFormat.format(totalLast));
            if (perc < 0) {
                lLastTtl.setStyle("-fx-text-fill: #e74c3c");
            }else {
                lLastTtl.setStyle("-fx-text-fill: -fx-btn");
            }
            lLastTtl.setText(perc > 100 ? "+100%" : perc < -100 ? "-100%" : VariablatPublike.decimal.format(perc) + "%");
        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrKons (){
        String q = "select count(*) as c from konsumatoret where formatdatetime(data_krijimit, 'yyyy-MM-dd') >= ? and " +
                "formatdatetime(data_krijimit, 'yyyy-MM-dd') <= ?";
        String q2 = "select count(*) as c from konsumatoret where formatdatetime(data_krijimit, 'yyyy-MM-dd') < ? and " +
                "data_krijimit > " +
                "dateadd('day', -datediff('day',  ?, ?)-1, ?);";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareStatement(q2)) {
            ps.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));

            ps2.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(2, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(3, java.sql.Date.valueOf(dpDeri.getValue()));
            ps2.setDate(4, java.sql.Date.valueOf(dpPrej.getValue()));

            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            int kons = 0;
            int konsL = 0;
            while (rs.next()) {
                kons = rs.getInt("c");
            }

            while (rs2.next()) {
                konsL = rs2.getInt("c");
            }

            double perc = ((kons - konsL)/konsL)*100;

            if (perc < 0) {
                lKonsL.setStyle("-fx-text-fill: #e74c3c");
            }else {
                lKonsL.setStyle("-fx-text-fill: -fx-btn");
            }

            lKons.setText(kons+"");
            lKonsL.setText(perc > 100 ? "+100%" : perc < -100 ? "-100%" : VariablatPublike.decimal.format(perc) + "%");

        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrTeDhenat(int id) {
        try (PreparedStatement pstmt = con.prepareStatement("select p_id, produkti, qmimi_shitjes, cash, total, sasia from vshitjet where red_id = ?")) {
            pstmt.setInt(1, id);
            total = 0;
            double pag = 0;
            ResultSet rs = pstmt.executeQuery();

            ObservableList<FaturaDhenat> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new FaturaDhenat(rs.getString("p_id"), rs.getString("produkti"), VariablatPublike.decimalFormat.format(rs.getDouble("qmimi_shitjes")),
                        VariablatPublike.decimal.format(rs.getDouble("sasia"))));
                total += rs.getDouble("qmimi_shitjes") * rs.getDouble("sasia");
                pag = rs.getDouble("cash");
            }
            tblDhenat.setItems(data);
            lTotal.setText(VariablatPublike.decimalFormat.format(total));
            lPagesa.setText(VariablatPublike.decimalFormat.format(pag));
            lKusuri.setText(VariablatPublike.decimalFormat.format(pag-total));

        }catch (Exception e) {e.printStackTrace();}
    }

}
