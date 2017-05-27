package sample.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by uran on 17-05-02.
 */
public class MesazhetPublike {

    static void suksesDritarja(String msg) {
        try {
            FXMLLoader loader = new FXMLLoader(MesazhetPublike.class.getResource("/sample/gui/LajmeroGreen.fxml"));
            Parent parent = loader.load();
            LajmeroGreen lajmero = loader.getController();
            lajmero.setMesazhi(msg);
            Stage stage = new Stage();
            lajmero.btnOk.setOnAction(e -> stage.close());
            Scene scene = new Scene(parent, 440, 200);
            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(MesazhetPublike.class.getResource("/sample/style/style.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (Exception e) {e.printStackTrace();}
    }

    static void suksesDritarjaRed(String msg) {
        try {
            FXMLLoader loader = new FXMLLoader(MesazhetPublike.class.getResource("/sample/gui/LajmeroRed.fxml"));
            Parent parent = loader.load();
            LajmeroRed lajmero = loader.getController();
            lajmero.setMesazhi(msg);
            Stage stage = new Stage();
            lajmero.btnOk.setOnAction(e -> stage.close());
            Scene scene = new Scene(parent, 440, 200);
            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(MesazhetPublike.class.getResource("/sample/style/style.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (Exception e) {e.printStackTrace();}
    }
}
