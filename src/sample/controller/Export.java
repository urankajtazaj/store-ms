package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-04-05.
 */
public class Export implements Initializable {
    @FXML Button btnAnulo, btnCsv, btnExcel, btnSql, btnPdf;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String theme = VariablatPublike.styleSheet.substring(VariablatPublike.styleSheet.length()-9, VariablatPublike.styleSheet.length());
        if (!theme.equals("style.css")) {
            btnSql.setGraphic(new ImageView(new Image("/sample/photo/sql-dark.png")));
            btnExcel.setGraphic(new ImageView(new Image("/sample/photo/xls-dark.png")));
            btnCsv.setGraphic(new ImageView(new Image("/sample/photo/csv-dark.png")));
            btnPdf.setGraphic(new ImageView(new Image("/sample/photo/pdf-dark.png")));
        }
    }
}
