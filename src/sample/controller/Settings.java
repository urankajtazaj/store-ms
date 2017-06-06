package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-21.
 */
public class Settings implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    @FXML private TextField tMujor;
    @FXML private ComboBox cbTipi;
    @FXML private VBox vbPunet;
    @FXML private Button shtoPune;
    @FXML private VBox vbPnt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getTarget();
        getJobs();
        merrProdKats();
    }

    @FXML
    private void shtoPune (){
        addDeps("", 0);
    }

    private void rregulloPunen (TextField tf, int id) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoPune.fxml"));
            ShtoPune sp = new ShtoPune();
            sp.setTf(tf);
            sp.setId(id);
            sp.setStage(stage);
            loader.setController(sp);
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 350, 450);
            scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (Exception ex) {ex.printStackTrace();}
    }

    private void getTarget (){
        try {

            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("select * from target limit 1");

            while (rs.next()) {
                tMujor.setText(rs.getString("qmimi"));
                cbTipi.getSelectionModel().select(rs.getInt("tipi"));
            }

            rs.close();
            s.close();

        }catch (Exception e) { e.printStackTrace(); }
    }

    private void addDeps (String emri, int index) {
        HBox hb = new HBox(5);
        TextField tfEmri = new TextField(emri);
        tfEmri.setEditable(false);
        Button btnRregullo = new Button("Rregullo");
        btnRregullo.setId(index+"");

        btnRregullo.setOnAction(e -> {
            rregulloPunen(tfEmri, index);
        });

        hb.getChildren().addAll(tfEmri, btnRregullo);
        vbPunet.getChildren().add(0, hb);
    }

    private void hapNgjyren (Button btn, int id, String bg, String fg){
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ZgjedhNgjyren.fxml"));
            ZgjedhNgjyren zgj = new ZgjedhNgjyren();
            zgj.setId(id);
            zgj.setStage(stage);
            zgj.setButton(btn);
            zgj.setBg(bg);
            zgj.setFg(fg);
            loader.setController(zgj);
            Parent parent = loader.load();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(parent, 320, 210);
            stage.setScene(scene);
            scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
            stage.setResizable(false);
            stage.show();

        }catch (Exception e ) { e.printStackTrace(); }
    }

    private void merrProdKats(){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select * from kat_prod")) {
            while (rs.next()) {
                addKats(rs.getInt("id"), rs.getString("kategoria"), rs.getString("bg"), rs.getString("fg"));
            }
        }catch (Exception e){ e.printStackTrace(); }
    }

    @FXML
    private void shtoProdKat (){
        addKats(0, "", "rgba(0,0,0,0.085)", "rgba(0,0,0,0.9)");
    }

    @FXML
    private void ruajProdKat(){
        try (Statement s = con.createStatement()) {
            for (Node n : vbPnt.getChildren()) {
                if (n instanceof HBox) {
                    String tf = ((TextField)((HBox)n).getChildren().get(0)).getText();
                    int id = Integer.parseInt(((HBox)n).getChildren().get(1).getId().split("/")[0]);
                    String bg = ((HBox)n).getChildren().get(1).getId().split("/")[1];
                    String fg = ((HBox)n).getChildren().get(1).getId().split("/")[2];
                    if (id == 0)
                        s.addBatch("insert into kat_prod values (null, '"+tf+"', '" + bg + "', '"+ fg + "')");
                    else {
                        s.addBatch("update kat_prod set kategoria = '" + tf + "', bg = '" + bg + "', fg = '" + fg + "' where id = " + id);
                        VariablatPublike.mProdKat.put(id, tf);
                        VariablatPublike.revProdKat.put(tf, id);
                        VariablatPublike.prodKat.add(tf);
                    }
                }
            }
            s.executeBatch();
            MesazhetPublike.Lajmerim("Ndryshimet ne kategori u ruajten me sukses", MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.SUCCESS, 5);

        }catch (Exception e) {
            MesazhetPublike.Lajmerim("Diqka nuk ka shkuar siq duhet!", MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.ERROR, 5);
            e.printStackTrace(); }
    }

    private void addKats (int id, String emri, String bg, String fg) {
        HBox hb = new HBox(5);
        TextField tf = new TextField(emri);
        tf.setMaxWidth(120);
        Button btn = new Button("Ngjyra");
        Button btnDel = new Button();
        ImageView iv = new ImageView(new Image("/sample/photo/errormark.png"));
        iv.setFitWidth(14);
        iv.setPreserveRatio(true);
        btnDel.setGraphic(iv);
        btn.setStyle("-fx-background-color: "+bg+"; -fx-text-fill: " + fg);
        btnDel.setStyle("-fx-background-color: rgba(0,0,0,0.085); -fx-text-fill: rgba(0,0,0,0.9); -fx-padding: 3");
        btn.setId(id + "/" + bg + "/" + fg);

        btn.setOnAction(e -> {
            hapNgjyren((Button) e.getSource(), id, bg, fg);
        });

        btnDel.setOnAction(e -> {
            vbPnt.getChildren().remove(hb);
        });

        hb.getChildren().addAll(tf, btn, btnDel);
        hb.setAlignment(Pos.CENTER_RIGHT);
        vbPnt.getChildren().add(0, hb);
    }

    private void getJobs (){
        String q = "select * from departamenti";
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery(q)) {

            while (rs.next()) {
                addDeps(rs.getString("departamenti"), rs.getInt("id"));
            }

        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void ruajTarget(){
        try {
            if (Pattern.compile("[0-9.]+").matcher(tMujor.getText()).matches() && Double.parseDouble(tMujor.getText()) > -1) {
                PreparedStatement p = con.prepareStatement("update target set qmimi = ?, tipi = ?");
                p.setDouble(1, Double.parseDouble(tMujor.getText()));
                p.setInt(2, cbTipi.getSelectionModel().getSelectedIndex());
                p.execute();
                MesazhetPublike.Lajmerim("Targeti u azhurnua me sukses", MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.SUCCESS, 5);
            }else {
                MesazhetPublike.Lajmerim("Fusha e qmimit duhet te jete numer me i madh se 0, dhe nuk duhet te permbaje hapesira.",
                        MesazhetPublike.ButtonType.OK_BUTTON, MesazhetPublike.NotificationType.ERROR, 8);
            }
        }catch (Exception e) {e.printStackTrace();}
    }

}
