package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class ShtoStock implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    @FXML
    public ToggleGroup tgSasia, tgTipi;
    @FXML
    public Button btnOk;

    @FXML
    private ToggleButton tbAdd, tbRem;

    @FXML
    public TextField txtSasia;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String theme = VariablatPublike.styleSheet.substring(VariablatPublike.styleSheet.length()-9, VariablatPublike.styleSheet.length());

        if (theme.equals("Light.css")) {
            tbAdd.setGraphic(new ImageView(new Image("/sample/photo/add-black.png")));
            tbRem.setGraphic(new ImageView(new Image("/sample/photo/minus-black.png")));
        }else {
            tbAdd.setGraphic(new ImageView(new Image("/sample/photo/add-white.png")));
            tbRem.setGraphic(new ImageView(new Image("/sample/photo/minus-white.png")));
        }

        tgSasia.selectedToggleProperty().addListener((o, ov ,nv) -> {
            if (nv != null) txtSasia.setText(((ToggleButton)nv).getText());
        });
    }
}
