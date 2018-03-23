package sample.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-02.
 */
public class Notification {

    private int seconds = 3;
    private String message = "";
    private ButtonType btnType = ButtonType.NO_BUTTON;
    private NotificationType ntfType = NotificationType.SUCCESS;
    private boolean delete;
    ResourceBundle bundle;

    boolean getDelete (){
        return delete;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDuration(int seconds) {
        this.seconds = seconds;
    }

    public void setType(NotificationType nt) {
        this.ntfType = nt;
    }

    public void setButton(ButtonType bt) {
        this.btnType = bt;
    }

    public Notification (){}

    public void setAll(String message, ButtonType buttonType, NotificationType notificationType, int seconds){
        this.message = message;
        this.btnType = buttonType;
        this.ntfType = notificationType;
        this.seconds = seconds;
    }

    public Notification (String message){
        this.message = message;
    }

    public void showAndWait() {
        Properties props = new Properties();
        try {
            Rectangle2D rect = Screen.getPrimary().getBounds();
            String fxml = "";

            switch (ntfType) {
                case SUCCESS: {
                    switch (btnType) {
                        case NO_BUTTON: fxml = "/sample/gui/LajmeroGreen.fxml"; break;
                        case OK: fxml = "/sample/gui/LajmeroGreenBtn.fxml"; break;
                    }
                    break;
                }
                case ERROR: {
                    switch (btnType) {
                        case NO_BUTTON: fxml = "/sample/gui/LajmeroRed.fxml"; break;
                        case OK: fxml = "/sample/gui/LajmeroRedBtn.fxml"; break;
                        case YES_NO: fxml = "/sample/gui/LajmeroRedBtnYN.fxml"; break;
                    }
                    break;
                }
            }

            props.load(new FileInputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties"));
            if (props.getProperty("lang").equals("English")) {
                bundle = ResourceBundle.getBundle("resources.Language_en", new Locale("en", "EN"));
            } else if (props.getProperty("lang").equals("German")) {
                bundle = ResourceBundle.getBundle("resources.Language_de", new Locale("de", "DE"));
            } else {
                bundle = ResourceBundle.getBundle("resources.Language_sq", new Locale("sq", "SQ"));
            }

            FXMLLoader loader = new FXMLLoader(Notification.class.getResource(fxml), bundle);
            Parent parent = loader.load();
            LajmeroGreen lajmero = loader.getController();
            lajmero.setMesazhi(message);
            Stage stage = new Stage();
            Scene scene = new Scene(parent, 420, 165);

            lajmero.setStage(stage);

            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(Notification.class.getResource(VariablatPublike.styleSheet).toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            stage.setX(rect.getMinX() + rect.getWidth() - 380);
            stage.setY(rect.getMinY());

            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
            delete = lajmero.getDelete();
        }catch (Exception e) {e.printStackTrace();}
    }

    public void show () {
        try {
            Rectangle2D rect = Screen.getPrimary().getBounds();
            String fxml = "";

            switch (ntfType) {
                case SUCCESS: {
                    switch (btnType) {
                        case NO_BUTTON: fxml = "/sample/gui/LajmeroGreen.fxml"; break;
                        case OK: fxml = "/sample/gui/LajmeroGreenBtn.fxml"; break;
                    }
                    break;
                }
                case ERROR: {
                    switch (btnType) {
                        case NO_BUTTON: fxml = "/sample/gui/LajmeroRed.fxml"; break;
                        case OK: fxml = "/sample/gui/LajmeroRedBtn.fxml"; break;
                    }
                    break;
                }
            }

            FXMLLoader loader = new FXMLLoader(Notification.class.getResource(fxml));
            Parent parent = loader.load();
            LajmeroGreen lajmero = loader.getController();
            lajmero.setMesazhi(message);
            Stage stage = new Stage();
            Scene scene = new Scene(parent, 400, 150);

            lajmero.setStage(stage);

            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(Notification.class.getResource(VariablatPublike.styleSheet).toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            stage.setX(rect.getMinX() + rect.getWidth() - 380);
            stage.setY(rect.getMinY());

            PauseTransition pause = new PauseTransition(Duration.seconds(seconds < 1 ? 1 : seconds));
            pause.setOnFinished(e -> stage.close());

            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setResizable(false);
            if (seconds > 0) pause.play();
            stage.show();
        }catch (Exception e) {e.printStackTrace();}
    }

}
