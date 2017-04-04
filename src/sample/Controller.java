package sample;

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
import javafx.scene.layout.VBox;
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

import javax.tools.Tool;

public class Controller implements Initializable {

    @FXML Button btnHome, btnPunetoret, btnStat, btnKons, btnPaisjet, btnLogout, btnProfile, btnSettings, closeBtn, minimizeBtn;
    @FXML BorderPane root;
    @FXML VBox btnVb;

    ScrollPane scrollPane = new ScrollPane();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            scrollPane.setContent(FXMLLoader.load(getClass().getResource("dashboard.fxml")));
            root.setCenter(scrollPane);
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnHome.setOnMouseEntered(e -> {onHover(e);});
        btnPunetoret.setOnMouseEntered(e -> {onHover(e);});

        btnHome.setOnMouseExited(e -> {onLeave(e);});
        btnPunetoret.setOnMouseExited(e -> {onLeave(e);});

        btnStat.setOnMouseEntered(e -> {onHover(e);});
        btnStat.setOnMouseExited(e -> {onLeave(e);});

        btnKons.setOnMouseEntered(e -> {onHover(e);});
        btnKons.setOnMouseExited(e -> {onLeave(e);});

        btnPaisjet.setOnMouseEntered(e -> {onHover(e);});
        btnPaisjet.setOnMouseExited(e -> {onLeave(e);});

        btnLogout.setOnMouseEntered(e -> {onHover(e);});
        btnLogout.setOnMouseExited(e -> {onLeave(e);});

        btnProfile.setOnMouseEntered(e -> {onHover(e);});
        btnProfile.setOnMouseExited(e -> {onLeave(e);});

//        btnSettings.setOnMouseEntered(e -> {onHover(e);});
//        btnSettings.setOnMouseExited(e -> {onLeave(e);});
    }

    private void onHover(MouseEvent e) {
        ((Button) e.getSource()).setStyle("-fx-background-color: #e0e0e0");
    }

    private void onLeave(MouseEvent e) {
        ((Button) e.getSource()).setStyle("-fx-opacity: .7;");
    }

    private String cap(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length()).toLowerCase();
    }

    @FXML
    private void changeScene(ActionEvent e) throws Exception {
        Parent pntRoot = null;

        if (((Button) e.getSource()).getText().equals("PANELI KRYESOR")) {
            pntRoot = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        }
        else if (((Button) e.getSource()).getText().equals("PUNETORET")) {
            pntRoot = FXMLLoader.load(getClass().getResource("punetoret.fxml"));
        }

        scrollPane.setContent(pntRoot);
        root.setCenter(scrollPane);
    }

}
