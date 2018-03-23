package sample.controller;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML ToggleButton btnHome, btnPunetoret, btnStat, btnKons, btnPaisjet, btnProfile, btnSettings, btnLogout, btnShit;
    @FXML BorderPane root;
    @FXML VBox btnVb;
    @FXML private Label user;
    @FXML private ImageView spinner;
    private String usr = "null";
    private int pntId = 0;

    ResourceBundle bundle = null;

    private Stage stage;

    DB db = new DB();
    Connection con = null;

    public void setPntId(int id) {
        this.pntId = id;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        con = db.connect(resources);

        stage.setOnCloseRequest(e -> {
            Server.stopServer();
            Platform.exit();
        });

//        System.out.println(VariablatPublike.IBAN);

        stage.setTitle("Sistemi per Menaxhim te Shitjeve");
        if (pntId > 0)
            setPrivileges();

        merrTvsh();
        disableButtons(VariablatPublike.shtepi, btnHome);
        disableButtons(VariablatPublike.punetoret, btnPunetoret);
        disableButtons(VariablatPublike.konsumatoret, btnKons);
        disableButtons(VariablatPublike.settings, btnSettings);
        disableButtons(VariablatPublike.shitjeBool, btnShit);
        disableButtons(VariablatPublike.raportet, btnStat);
        disableButtons(VariablatPublike.produktet, btnPaisjet);

        try {
            if (pntId > 0) root.setCenter(openFirst().load());
            else root.setCenter(FXMLLoader.load(getClass().getResource("/sample/gui/dashboard.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FXMLLoader openFirst(){
        FXMLLoader loader = null;
        ResourceBundle bundle = null;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (properties.getProperty("lang").equals("English")) {
            bundle = ResourceBundle.getBundle("resources.Language_en", new Locale("en", "EN"));
        } else if (properties.getProperty("lang").equals("German")) {
            bundle = ResourceBundle.getBundle("resources.Language_de", new Locale("de", "DE"));
        } else {
            bundle = ResourceBundle.getBundle("resources.Language_sq", new Locale("sq", "SQ"));
        }
        if (VariablatPublike.shtepi) {
            loader = new FXMLLoader(getClass().getResource("/sample/gui/dashboard.fxml"), bundle);
            btnHome.setSelected(true);
        }
        else if (VariablatPublike.punetoret) {
            loader = new FXMLLoader(getClass().getResource("/sample/gui/punetoret.fxml"), bundle);
            btnPunetoret.setSelected(true);
        }
        else if (VariablatPublike.konsumatoret) {
            loader = new FXMLLoader(getClass().getResource("/sample/gui/konsumatoret.fxml"), bundle);
            btnKons.setSelected(true);
        }
        else if (VariablatPublike.produktet) {
            loader = new FXMLLoader(getClass().getResource("/sample/gui/produktet.fxml"), bundle);
            btnPaisjet.setSelected(true);
        }
        else if (VariablatPublike.settings) {
            loader = new FXMLLoader(getClass().getResource("/sample/gui/Settings.fxml"), bundle);
            btnSettings.setSelected(true);
        }
        else if (VariablatPublike.raportet) {
            loader = new FXMLLoader(getClass().getResource("/sample/gui/Raportet.fxml"), bundle);
            btnStat.setSelected(true);
        }
        else if (VariablatPublike.shitjeBool) {
            loader = new FXMLLoader(getClass().getResource("/sample/gui/Shitjet.fxml"), bundle);
            btnShit.setSelected(true);
        }
        return loader;
    }

    private void disableButtons(boolean bool, ToggleButton btn){
        if (!bool && pntId > 0) btn.setDisable(true);
    }

    private void addTooltip(Tooltip tp, ToggleButton btn, String text){
        btn.setOnMouseEntered(e -> {
            tp.setText(text);
            tp.show(stage, btn.getBoundsInLocal().getMaxX(), btn.getBoundsInParent().getMaxY()-3-tp.getMaxHeight()/2);
        });

        btn.setOnMouseExited(e -> tp.hide());
    }

    private void setPrivileges (){
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("select * from priv where dep_id = " + pntId);
             Statement st2 = con.createStatement(); ResultSet rs2 = st2.executeQuery("select * from mundesite where dep_id = " + pntId)) {

            rs.next();
            VariablatPublike.shtepi = rs.getBoolean("shtepi");
            VariablatPublike.punetoret = rs.getBoolean("punetoret");
            VariablatPublike.konsumatoret = rs.getBoolean("konsumatoret");
            VariablatPublike.raportet = rs.getBoolean("raportet");
            VariablatPublike.shitjeBool = rs.getBoolean("shitje");
            VariablatPublike.produktet = rs.getBoolean("produktet");
            VariablatPublike.settings = rs.getBoolean("settings");

            rs2.next();
            VariablatPublike.prod_add = rs2.getBoolean("prod_add");
            VariablatPublike.prod_edit = rs2.getBoolean("prod_edit");
            VariablatPublike.prod_del = rs2.getBoolean("prod_del");
            VariablatPublike.pnt_add = rs2.getBoolean("pnt_add");
            VariablatPublike.pnt_edit = rs2.getBoolean("pnt_edit");
            VariablatPublike.pnt_del = rs2.getBoolean("pnt_del");
            VariablatPublike.kons_add = rs2.getBoolean("kons_add");
            VariablatPublike.kons_edit = rs2.getBoolean("kons_edit");
            VariablatPublike.kons_del = rs2.getBoolean("kons_del");
            VariablatPublike.rep_edit = rs2.getBoolean("rep_edit");
            VariablatPublike.rep_del = rs2.getBoolean("rep_del");

        }catch (Exception e){ e. printStackTrace(); }
    }

    private String cap(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length()).toLowerCase();
    }

    public void merrTvsh(){
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("select * from tvsh")) {
            while (rs.next()) {
                if (rs.getDouble("tvsh") > 0) {
                    VariablatPublike.tvsh = Double.parseDouble("1." + (rs.getInt("tvsh")));
                } else {
                    VariablatPublike.tvsh = 0;
                }
            }
        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void changeScene(ActionEvent e) throws Exception {
        Parent pntRoot = null;

        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(System.getProperty("user.home") + "/store-ms-files/config/config.properties");
        try {
            properties.load(in);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        if (properties.getProperty("lang").equals("English")) {
            bundle = ResourceBundle.getBundle("resources.Language_en", new Locale("en", "EN"));
        } else if (properties.getProperty("lang").equals("German")) {
            bundle = ResourceBundle.getBundle("resources.Language_de", new Locale("de", "DE"));
        } else {
            bundle = ResourceBundle.getBundle("resources.Language_sq", new Locale("sq", "SQ"));
        }

        if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_HOME"))) {
            pntRoot = FXMLLoader.load(getClass().getResource("/sample/gui/dashboard.fxml"), bundle);
        } else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_SHPENZIM"))) {
            pntRoot = FXMLLoader.load(getClass().getResource("/sample/gui/Shpenzimet.fxml"), bundle);
        } else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_PUNETORET"))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/punetoret.fxml"), bundle);
            pntRoot = loader.load();

            Punetoret punetoret = loader.getController();
            punetoret.setStage(stage);
            punetoret.setTransition(startSpinning(spinner));
            punetoret.setIv(spinner);
            punetoret.setBorderPane(root);

            punetoret.btnShtoPnt.setOnAction(a -> {
                try {
                    FXMLLoader l = new FXMLLoader(getClass().getResource("/sample/gui/ShtoPunetoret.fxml"), bundle);
                    ShtoPunetoret shp = new ShtoPunetoret();
                    l.setController(shp);
                    root.setCenter(l.load());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

        }else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_PRODUKTET"))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/produktet.fxml"), bundle);
            pntRoot = loader.load();
            Produktet p = loader.getController();

            p.setStage(stage);
            p.setTransition(startSpinning(spinner));
            p.setIv(spinner);
            p.setBp(root);
        }else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_KONSUMATORET"))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/konsumatoret.fxml"), bundle);
            pntRoot = loader.load();
            Konsumatoret konsumatoret = loader.getController();
            konsumatoret.setIv(spinner);
            konsumatoret.setTransition(startSpinning(spinner));
            konsumatoret.setRoot(root);
        }else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_RAPORTET"))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Raportet.fxml"), bundle);
            pntRoot = loader.load();
        }else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_SHITJE"))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Shitjet.fxml"), bundle);
            pntRoot = loader.load();
        }else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_FINANC"))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Financat.fxml"), bundle);
            pntRoot = loader.load();
        }else if (((ToggleButton) e.getSource()).getText().equals(bundle.getString("M_SETTINGS"))) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Settings.fxml"), bundle);
            pntRoot = loader.load();
            Settings settings = loader.getController();
            settings.setIv(spinner);
            settings.setTransition(startSpinning(spinner));
        }

        in.close();
        root.setCenter(pntRoot);
    }

    @FXML
    private void exit(){
        Server.stopServer();
        Platform.exit();
    }

    public RotateTransition startSpinning(ImageView iv){
        RotateTransition transition = new RotateTransition(Duration.millis(700), iv);
        transition.setByAngle(360);
        transition.setCycleCount(-1);
        return transition;
    }

}
