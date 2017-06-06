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

    static void Lajmerim (String msg, ButtonType btns, NotificationType ntf, double duration) {
        try {
            Rectangle2D rect = Screen.getPrimary().getBounds();
            String fxml = "";

            switch (ntf) {
                case SUCCESS: {
                    switch (btns) {
                        case NO_BUTTON: fxml = "/sample/gui/LajmeroGreen.fxml"; break;
                        case OK_BUTTON: fxml = "/sample/gui/LajmeroGreenBtn.fxml"; break;
                    }
                    break;
                }
                case ERROR: {
                    switch (btns) {
                        case NO_BUTTON: fxml = "/sample/gui/LajmeroRed.fxml"; break;
                        case OK_BUTTON: fxml = "/sample/gui/LajmeroRedBtn.fxml"; break;
                    }
                    break;
                }
            }

            FXMLLoader loader = new FXMLLoader(MesazhetPublike.class.getResource(fxml));
            Parent parent = loader.load();
            LajmeroGreen lajmero = loader.getController();
            lajmero.setMesazhi(msg);
            Stage stage = new Stage();
            Scene scene = new Scene(parent, 400, 150);

            lajmero.setStage(stage);

            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(MesazhetPublike.class.getResource("/sample/style/style.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            stage.setX(rect.getMinX() + rect.getWidth() - 380);
            stage.setY(rect.getMinY());

            PauseTransition pause = new PauseTransition(Duration.seconds(duration < 1 ? 1 : duration));
            pause.setOnFinished(e -> stage.hide());

            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            if (duration > 0) pause.play();
            stage.show();
        }catch (Exception e) {e.printStackTrace();}
    }

    public enum ButtonType {
        NO_BUTTON,
        OK_BUTTON,
    }

    public enum NotificationType {
        ERROR,
        SUCCESS
    }

}
