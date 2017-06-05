package sample.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Created by uran on 17-05-02.
 */
public class MesazhetPublike {

    static void suksesDritarja(String msg) {
        try {
            Rectangle2D rect = Screen.getPrimary().getBounds();
            FXMLLoader loader = new FXMLLoader(MesazhetPublike.class.getResource("/sample/gui/LajmeroGreen.fxml"));
            Parent parent = loader.load();
            LajmeroGreen lajmero = loader.getController();
            lajmero.setMesazhi(msg);
            Stage stage = new Stage();
            Scene scene = new Scene(parent, 400, 130);
            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(MesazhetPublike.class.getResource("/sample/style/style.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            stage.setX(rect.getMinX() + rect.getWidth() - 380);
            stage.setY(rect.getMinY() + rect.getHeight() - 110);

            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(e -> stage.hide());

            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            pause.play();
            stage.show();
        }catch (Exception e) {e.printStackTrace();}
    }

    static void suksesDritarjaRed(String msg) {
        try {
            Rectangle2D rect = Screen.getPrimary().getBounds();
            FXMLLoader loader = new FXMLLoader(MesazhetPublike.class.getResource("/sample/gui/LajmeroRed.fxml"));
            Parent parent = loader.load();
            LajmeroRed lajmero = loader.getController();
            lajmero.setMesazhi(msg);
            Stage stage = new Stage();
            Scene scene = new Scene(parent, 400, 130);
            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(MesazhetPublike.class.getResource("/sample/style/style.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            PauseTransition pause = new PauseTransition(Duration.seconds(6));
            pause.setOnFinished(e -> stage.hide());

            stage.setX(rect.getMinX() + rect.getWidth() - 380);
            stage.setY(rect.getMinY());

            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            pause.play();
            stage.show();
        }catch (Exception e) {e.printStackTrace();}
    }
}
