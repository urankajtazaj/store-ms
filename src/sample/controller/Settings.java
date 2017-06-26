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
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

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

    Notification ntf = new Notification();

    @FXML private TextField tMujor, txtTvsh;
    @FXML private ComboBox cbTipi;
    @FXML private VBox vbPunet;
    @FXML private Button shtoPune;
    @FXML private VBox vbPnt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getTarget();
        merrTvsh();
        getJobs();
        merrProdKats();
    }

    @FXML
    private void shtoPune(){
        addDeps("", 0);
    }

    private void rregulloPunen(TextField tf, int id, HBox hb) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoPune.fxml"));
            ShtoPune sp = new ShtoPune();
            sp.setTf(tf);
            sp.setId(id);
            sp.setHBox(hb);
            sp.setVbox(vbPunet);
            sp.setStage(stage);
            loader.setController(sp);
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 345, 335);
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);
            stage.show();
        }catch (Exception ex) {ex.printStackTrace();}
    }

    private void getTarget(){
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
            rregulloPunen(tfEmri, index, hb);
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
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
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
    private void shtoProdKat(){
        addKats(0, "", "rgba(0,0,0,0.085)", "rgba(255,255,255,0.9)");
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
            ntf.setMessage("Ndryshimet ne kategori u ruajten me sukses");
            ntf.show();

        }catch (Exception e) {
            ntf.setMessage("Diqka nuk ka shkuar siq duhet!");
            ntf.setType(NotificationType.ERROR);
            ntf.show();
            e.printStackTrace(); }
    }

    private void addKats(int id, String emri, String bg, String fg) {
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
        btnDel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-padding: 3");
        btn.setId(id + "/" + bg + "/" + fg);

        btn.setOnAction(e -> {
            hapNgjyren((Button) e.getSource(), id, bg, fg);
        });

        btnDel.setOnAction(e -> {
            ntf.setMessage("Nese e fshini kete kategori, te gjitha produktet qe jane ne kate kategoria do te fshihen.\nA deshironi te vazhdoni?");
            ntf.setType(NotificationType.ERROR);
            ntf.setDuration(0);
            ntf.setButton(ButtonType.YES_NO);
            ntf.showAndWait();
            if (id > 0 && ntf.getDelete()) {
                vbPnt.getChildren().remove(hb);
                fshiKat(id);
            }
        });

        hb.getChildren().addAll(tf, btn, btnDel);
        hb.setAlignment(Pos.CENTER_RIGHT);
        vbPnt.getChildren().add(0, hb);
    }

    private void fshiKat(int id){
        String q = "delete from kat_prod where id = ?";
        String q2 = "delete from produktet where kategoria_id = ?";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareStatement(q2)) {
            ps.setInt(1, id);
            ps.execute();
            ps2.setInt(1, id);
            ps2.execute();
            ntf.setMessage("Kategoria u fshi me sukses, produktet qe kane qene ne kete kategoria jane fshire gjithashtu.");
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setDuration(3);
            ntf.show();
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void getJobs(){
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
                ntf.setMessage("Targeti u azhurnua me sukses");
                ntf.setType(NotificationType.SUCCESS);
                ntf.show();
            }else {
                ntf.setType(NotificationType.ERROR);
                ntf.setMessage("Fusha e qmimit duhet te jete numer me i madh se 0, dhe nuk duhet te permbaje hapesira");
                ntf.show();
            }
        }catch (Exception e) {e.printStackTrace();}
    }

    public void merrTvsh(){
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("select * from tvsh")) {
            while (rs.next()) {
                txtTvsh.setText(VariablatPublike.decimal.format(rs.getDouble("tvsh")));
                VariablatPublike.tvsh = rs.getDouble("tvsh");
            }
        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void ruajTvsh(){
        try (PreparedStatement ps = con.prepareStatement("update tvsh set tvsh = ?")) {
            ps.setDouble(1, Double.parseDouble(txtTvsh.getText()));
            ps.execute();
            VariablatPublike.tvsh = Double.parseDouble(txtTvsh.getText());
            ntf.setMessage("TVSH u ndryshua me sukses");
            ntf.setType(NotificationType.SUCCESS);
            ntf.show();
        }catch (NumberFormatException nfe) {
            ntf.setType(NotificationType.ERROR);
            ntf.setMessage("TVSH duhet te permbaje vetem numer");
            ntf.show();
            txtTvsh.clear();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

}
