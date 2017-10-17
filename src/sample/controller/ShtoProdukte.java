package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-27.
 */
public class ShtoProdukte implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    Notification ntf = new Notification();

    @FXML private ImageView ivProdFoto;
    @FXML private TextField bc, emri, qmimiStd, qmimiShitjes, zbritje, stok, stokCrit;
    @FXML private ComboBox<String> cbKategoria;
    @FXML private ChoiceBox<String> cbNjesia;
    @FXML private Button shtoFoto, pastroFoto;

    private String vbc = "", vemri = "", cbCat, cbNjs, fotoPath, fotoLink;
    private double vqstd = 0, vqs = 0, vz = 0;
    private int vs = 0, vsc = 0, id = 0;

    private String patternDot = "[0-9.]+";
    private String patternEmpty = "^$|[0-9.]+";
    private String pattern = "[0-9]+";

    public void setCbKategoria (String index){
        this.cbCat = index;
    }

    public void setCbNjesia (String njesia) {
        this.cbNjs = njesia;
    }

    public void setBc (String bc) {
        this.vbc = bc;
    }

    public void setId (int id) {
        this.id = id;
    }

    public void setEmri (String emri) {
        this.vemri = emri;
    }

    public void setQmimiStd(double qmimiStd) {
        this.vqstd = qmimiStd;
    }

    public void setQmimiShitjes (double qmimiShitjes) {
        vqs = qmimiShitjes;
    }

    public void setZbritje (double zbritje) {
        this.vz = zbritje;
    }

    public void setStok (int stok) {
        this.vs = stok;
    }

    public void setStokCrit (int stokCrit) {
        this.vsc = stokCrit;
    }

    public void setIvProdFoto (String fotoPath) { this.fotoPath = fotoPath; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        zgjedhFoton();

        cbKategoria.getItems().clear();
        Iterator<String> it = VariablatPublike.prodKat.iterator();
        while (it.hasNext()) {
            cbKategoria.getItems().add(it.next());
        }
        cbKategoria.getSelectionModel().select(0);

        pastroFoto.setOnAction(e -> {
            ivProdFoto.setImage(null);
            pastroFoto.setDisable(true);
        });

        if (id > 0) {
            emri.setText(vemri);
            bc.setText(vbc);
            qmimiStd.setText(vqstd+"");
            qmimiShitjes.setText(vqs+"");
            zbritje.setText(vz+"");
            stok.setText(vs+"");
            stokCrit.setText(vsc+"");
            cbNjesia.getSelectionModel().select(cbNjs);
            cbKategoria.getSelectionModel().select(cbCat);

//            if (fotoPath != null) {
                ivProdFoto.setImage(new Image("file:///" + fotoPath));
//            }
        }

//        if (!ivProdFoto.getImage().getUrl().substring(ivProdFoto.getImage().getUrl().length()-10, ivProdFoto.getImage().getUrl().length()).equals("sample.png")) {
//            pastroFoto.setDisable(false);
//        }

    }

    private void addToDatabase(){
        String q = "insert into produktet values (null, ?, ?, ?, ?, ?, ?, current_timestamp(), ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, VariablatPublike.revProdKat.get(cbKategoria.getSelectionModel().getSelectedItem()));
            ps.setString(2, emri.getText());
            ps.setInt(3, Integer.parseInt(stok.getText()));
            ps.setDouble(4, qmimiStd.getText().isEmpty() ? 0 : Double.parseDouble(qmimiStd.getText()));
//            ps.setDouble(5, Double.parseDouble(qmimiShitjes.getText()));
            ps.setDouble(5, Double.parseDouble(qmimiShitjes.getText()));
            ps.setString(6, cbNjesia.getSelectionModel().getSelectedItem());
            ps.setString(7, bc.getText());
            ps.setInt(8, zbritje.getText().isEmpty() ? 0 : Integer.parseInt(zbritje.getText()));
            ps.setInt(9, stokCrit.getText().isEmpty() ? 0 : Integer.parseInt(stokCrit.getText()));
            ps.setDouble(10, Double.parseDouble(qmimiStd.getText()));
            ps.setDouble(11, Double.parseDouble(qmimiShitjes.getText()));
            ps.setString(12, ivProdFoto.getImage().getUrl().substring(6, ivProdFoto.getImage().getUrl().length()));
            ps.execute();
            ntf.setMessage("Produkti " + emri.getText() + ", u shtua me sukses");
            ntf.setType(NotificationType.SUCCESS);
            ntf.show();
        }catch (NumberFormatException nfe) {
            ntf.setMessage("Fushat ku kerkohen numra nuk duhet te permbajne shkronja ose karaktere tjera");
            ntf.setType(NotificationType.ERROR);
            ntf.show();
        }catch (Exception e){ e.printStackTrace(); }
    }

    @FXML
    private void shtoProdukt(){
        if (!emri.getText().isEmpty() && !qmimiShitjes.getText().isEmpty() && !stok.getText().isEmpty()) {
            if (Pattern.compile(patternDot).matcher(qmimiStd.getText()).matches() && Pattern.compile(patternDot).matcher(qmimiShitjes.getText()).matches() &&
                    Pattern.compile(patternEmpty).matcher(zbritje.getText()).matches() && Pattern.compile(pattern).matcher(stok.getText()).matches() &&
                    Pattern.compile(patternEmpty).matcher(stokCrit.getText()).matches()) {
                if (id == 0)
                    addToDatabase();
                else
                    updateProduct();
            }else {
                ntf.setType(NotificationType.ERROR);
                ntf.setMessage("Fushat ku kerkohen numra nuk mund te permbajne karaktere tjera");
                ntf.show();
            }
        }else {
            ntf.setType(NotificationType.ERROR);
            ntf.setMessage("Fushat me yll (*) nuk duhet te jene te zbrazeta");
            ntf.show();
        }
    }

    private void updateProduct() {
        String q = "update produktet set barcode = ?, emri = ?, kategoria_id = ?, sasia = ?, stokcrit = ?, " +
                "qmimi_std = ?, qmimi_shitjes = ?, njesia = ?, modifikuar = current_timestamp(), " +
                "zbritje = ?, qmimi_shitjes_lek = ?, foto = ? where id = ?";
        try (PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, bc.getText());
            ps.setString(2, emri.getText());
            ps.setInt(3, VariablatPublike.revProdKat.get(cbKategoria.getSelectionModel().getSelectedItem()));
            ps.setInt(4, Integer.parseInt(stok.getText()));
            ps.setInt(5, stokCrit.getText().isEmpty() ? 0 : Integer.parseInt(stokCrit.getText()));
            ps.setDouble(6, qmimiStd.getText().isEmpty() ? 0 : Double.parseDouble(qmimiStd.getText()));
            ps.setDouble(7, Double.parseDouble(qmimiShitjes.getText()));
            ps.setString(8, cbNjesia.getSelectionModel().getSelectedItem());
            ps.setDouble(9, zbritje.getText().isEmpty() ? 0 : Double.parseDouble(zbritje.getText()));
            ps.setDouble(10, qmimiStd.getText().isEmpty() ? 0 : Double.parseDouble(qmimiStd.getText()));
            ps.setString(11, ivProdFoto.getImage().getUrl().substring(6, ivProdFoto.getImage().getUrl().length()));
            ps.setInt(12, id);

            ps.execute();

            ntf.setMessage("Produkti u '"+emri.getText()+"' azhurnua me sukses");
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();

        }catch (NumberFormatException nfe) {
            ntf.setMessage("Fushat ku kerkohen numra nuk duhet te permabjne karaktere tjera");
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setType(NotificationType.ERROR);
            ntf.show();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void zgjedhFoton() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Zgjedh foton");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Foto", "*.jpg", "*.png", "*.jpeg"));

        shtoFoto.setOnAction(e -> {
            fotoLink = fc.showOpenDialog(null).getAbsolutePath();

            if (fotoLink != null) {
                if (fotoLink.substring(fotoLink.length()-3, fotoLink.length()).equals("jpg") || fotoLink.substring(fotoLink.length()-3, fotoLink.length()).equals("png")
                        || fotoLink.substring(fotoLink.length()-4, fotoLink.length()).equals("jpeg")) {
                    pastroFoto.setDisable(false);
                    ivProdFoto.setImage(new Image("file:///" + fotoLink));
                }
            }
        });
    }

    @FXML
    private void pastroProdukt(){
        bc.clear();
        emri.clear();
        qmimiStd.clear();
        qmimiShitjes.clear();
        zbritje.clear();
        stok.clear();
        stokCrit.clear();
        cbKategoria.getSelectionModel().select(0);
        cbNjesia.getSelectionModel().select(0);
    }

}
