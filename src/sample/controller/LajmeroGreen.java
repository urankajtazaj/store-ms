package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-05-02.
 */
public class LajmeroGreen implements Initializable {

    @FXML public Button btnOk;
    @FXML private Label mesazhi;

    private boolean delete;
    private Stage stage;

    boolean getDelete (){
        return delete;
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public void setMesazhi(String msg){
        this.mesazhi.setText(msg);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void closeStage (){
        stage.close();
    }

    @FXML
    private void retTrue (){
        delete = true;
        stage.close();
    }

    @FXML
    private void retFalse (){
        delete = false;
        stage.close();
    }

}
