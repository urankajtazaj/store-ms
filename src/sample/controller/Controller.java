package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.EventListener;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.tools.Tool;

public class Controller implements Initializable {

    @FXML Button btnHome, btnPunetoret, btnStat, btnKons, btnPaisjet, btnLogout, btnProfile, btnSettings;
    @FXML BorderPane root;
    @FXML VBox btnVb;
    @FXML private Label user;
    private String usr = "null";

    public void setUser (String user) {
        this.usr = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            root.setCenter(FXMLLoader.load(getClass().getResource("/sample/gui/dashboard.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setText(usr);

    }

    private String cap(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length()).toLowerCase();
    }

    @FXML
    private void changeScene(ActionEvent e) throws Exception {
        Parent pntRoot = null;

        if (((Button) e.getSource()).getText().equals("SHTEPI")) {
            pntRoot = FXMLLoader.load(getClass().getResource("/sample/gui/dashboard.fxml"));
            root.setCenter(pntRoot);
        }
        else if (((Button) e.getSource()).getText().equals("PUNETORET")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/punetoret.fxml"));
            pntRoot = loader.load();

            Punetoret punetoret = loader.getController();

            punetoret.setStage(root);

            punetoret.btnShtoPnt.setOnAction(a -> {
                try {
                    FXMLLoader l = new FXMLLoader(getClass().getResource("/sample/gui/ShtoPunetoret.fxml"));
                    ShtoPunetoret sp = new ShtoPunetoret();
                    l.setController(sp);
                    root.setCenter(l.load());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            root.setCenter(pntRoot);
        }
        else if (((Button) e.getSource()).getText().equals("PRODUKTET")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/produktet.fxml"));
            pntRoot = loader.load();
            root.setCenter(pntRoot);
        }
        else if (((Button) e.getSource()).getText().equals("KONSUMATORET")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/konsumatoret.fxml"));
            pntRoot = loader.load();
            Konsumatoret konsumatoret = loader.getController();
            konsumatoret.setRoot(root);
            root.setCenter(pntRoot);
        }
        else if (((Button) e.getSource()).getText().equals("RAPORTET")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Raportet.fxml"));
            pntRoot = loader.load();
            root.setCenter(pntRoot);
        }else if (((Button) e.getSource()).getText().equals("SHITJE")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Shitjet.fxml"));
            pntRoot = loader.load();
            root.setCenter(pntRoot);
        }else if (((Button) e.getSource()).getText().equals("RREGULLO")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Settings.fxml"));
            pntRoot = loader.load();
            root.setCenter(pntRoot);
        }

//        scrollPane.setContent(pntRoot);
    }

}
