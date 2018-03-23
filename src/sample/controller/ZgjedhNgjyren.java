package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-23.
 */
public class ZgjedhNgjyren implements Initializable {

    @FXML private ColorPicker cpBg;

    private Button button;
    private Stage stage;
    private int id = 0;
    private String background;

    public void setBg (String background) {
        this.background = background;
    }

    public void setButton (Button button) {
        this.button = button;
    }
    public void setId (int id) {
        this.id = id;
    }
    public void setStage (Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cpBg.setValue(Color.valueOf(button.getId().split("/")[1]));
        cpBg.setOnAction(e -> {
            background = toRGB(cpBg.getValue());
            System.out.println(background);
        });
    }

    private String toRGB(Color c) {
        return String.format("#%02X%02X%02X", (int)(c.getRed() * 255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }

    @FXML
    private void ruajNgjyren(ActionEvent actionEvent) {
        button.setStyle("-fx-min-width: 20; -fx-max-width: 20; -fx-min-height: 20; -fx-max-height: 20; -fx-border-radius: 50; -fx-background-radius: 50; -fx-background-color: " + background);
        button.setId(id + "/" + background);
        stage.close();
    }

}
