package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

    @FXML private ToggleButton tb1, tb2, tb3, tb4, tb5, tb6, tb7, tb8, ftb1, ftb2;

    private Button button;
    private Stage stage;
    private int id = 0;

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

    }

    @FXML
    private void ruajNgjyren(ActionEvent actionEvent) {
        button.setStyle("-fx-background-color: " + background() + "; -fx-text-fill: " + foreground());
        button.setId(id + "/" + background() + "/" + foreground());
        stage.close();
    }

    private String background(){
        if (tb1.isSelected()) return tb1.getText();
        else if (tb2.isSelected()) return tb2.getText();
        else if (tb3.isSelected()) return tb3.getText();
        else if (tb4.isSelected()) return tb4.getText();
        else if (tb5.isSelected()) return tb5.getText();
        else if (tb6.isSelected()) return tb6.getText();
        else if (tb7.isSelected()) return tb7.getText();
        else if (tb8.isSelected()) return tb8.getText();
        else return "";
    }

    private String foreground(){
        if (ftb1.isSelected()) return ftb1.getText();
        else if (ftb2.isSelected()) return ftb2.getText();
        else return "";
    }

}
