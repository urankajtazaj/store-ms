package sample.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.constructors.Punetori;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by uran on 17-04-27.
 */
public class HapeExporto {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Punetoret.fxml"));
    Punetoret punetoret = loader.getController();

    public HapeExporto() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/export.fxml"));
        Parent bpExport = null;
        try {
            bpExport = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Export export = loader.getController();
        Stage stage = new Stage();

        export.btnAnulo.setOnAction(e -> {
            stage.close();
        });

        export.btnExcel.setOnAction(e -> {

        });

        export.btnCsv.setOnAction(e -> {
//            createFile(stage, "csv");
        });

        Scene scene = new Scene(bpExport, 520, 205);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}
