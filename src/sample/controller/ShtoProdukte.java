package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
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
    @FXML private TextField bc, emri, qmimiShitjes, zbritje, stok, qmimiStd, qmimiShumice, txtNjesia;
    @FXML private ComboBox<String> cbKategoria;
    @FXML private ChoiceBox<String> cbNjesia;
    @FXML private Button shtoFoto, pastroFoto;
    @FXML private CheckBox cbRifillo;

    private String vbc = "", vemri = "", cbCat, cbNjs, fotoPath, fotoLink, nrNjesia;
    private double vqstd = 0, vqs = 0, vz = 0, vqsh = 0;
    private int vs = 0, vsc = 0, id = 0;

    private String patternDot = "[0-9.]+";
    private String patternEmpty = "^$|[0-9.]+";
    private String pattern = "[0-9]+";

    public void setNrNjesia (String njesia) {
        this.nrNjesia = njesia;
    }

    public void setQmimiShumice (Double qmimiShumice) {
        this.vqsh = qmimiShumice;
    }

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

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rb = resources;

        zgjedhFoton();

        cbNjesia.getItems().clear();
        cbNjesia.getItems().add(rb.getString("cope"));
        cbNjesia.getItems().add(rb.getString("paketim"));
        cbNjesia.getItems().add(rb.getString("Liter"));
        cbNjesia.getItems().add(rb.getString("Kg"));
//        cbNjesia.getSelectionModel().select(0);

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
            qmimiShumice.setText(vqsh + "");
            cbNjesia.getSelectionModel().select(Integer.parseInt(cbNjs));
            cbKategoria.getSelectionModel().select(cbCat);
            txtNjesia.setText(nrNjesia);

            if (fotoPath != null) {
                ivProdFoto.setImage(new Image("file:///" + fotoPath));
                pastroFoto.setDisable(false);
            } else {
                ivProdFoto.setImage(new Image(""));
                pastroFoto.setDisable(true);
            }
        }

//        if (!ivProdFoto.getImage().getUrl().isEmpty()) {
//            pastroFoto.setDisable(false);
//        }

    }

    private void addToDatabase(){
        String q = "insert into produktet values (null, ?, ?, ?, ?, ?, ?, current_timestamp(), ?, 0, ?, 0, ?, ?, ?, ?, current_date(), ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, VariablatPublike.revProdKat.get(cbKategoria.getSelectionModel().getSelectedItem()));
            ps.setString(2, emri.getText());
            ps.setInt(3, Integer.parseInt(stok.getText()));
            ps.setDouble(4, qmimiStd.getText().isEmpty() ? 0 : Double.parseDouble(qmimiStd.getText()));
            ps.setDouble(5, Double.parseDouble(qmimiShitjes.getText()));
            ps.setInt(6, cbNjesia.getSelectionModel().getSelectedIndex());
            ps.setString(7, bc.getText());
            ps.setInt(8, zbritje.getText().isEmpty() ? 0 : Integer.parseInt(zbritje.getText()));
            ps.setDouble(9, Double.parseDouble(qmimiShitjes.getText()));
            ps.setString(10, ivProdFoto.getImage() != null ? ivProdFoto.getImage().getUrl().substring(6, ivProdFoto.getImage().getUrl().length()) : "");
            ps.setInt(11, Integer.parseInt(stok.getText()));
            ps.setInt(12, Integer.parseInt(stok.getText()));
            ps.setDouble(13, qmimiShumice.getText().isEmpty() ? 0 : Double.parseDouble(qmimiShumice.getText()));
            ps.setString(14, txtNjesia.getText());
            ps.execute();
            ntf.setMessage(MessageFormat.format(rb.getString("shp_shto_sukses"), emri.getText()));
            ntf.setType(NotificationType.SUCCESS);
            ntf.show();
        }catch (NumberFormatException nfe) {
            ntf.setMessage(rb.getString("shp_format_error"));
            ntf.setType(NotificationType.ERROR);
            ntf.show();
        }catch (Exception e){ e.printStackTrace(); }
    }

    @FXML
    private void shtoProdukt(){
        if (!emri.getText().isEmpty() && !qmimiShitjes.getText().isEmpty() && !stok.getText().isEmpty()) {
            if (Pattern.compile(patternDot).matcher(qmimiShitjes.getText()).matches() && Pattern.compile(patternEmpty).matcher(qmimiStd.getText()).matches() &&
                    Pattern.compile(patternEmpty).matcher(qmimiShumice.getText()).matches() &&
                    Pattern.compile(patternEmpty).matcher(zbritje.getText()).matches() && Pattern.compile(pattern).matcher(stok.getText()).matches()) {
                if (id == 0)
                    addToDatabase();
                else
                    updateProduct();
            }else {
                ntf.setType(NotificationType.ERROR);
                ntf.setMessage(rb.getString("shp_format_error"));
                ntf.show();
            }
        }else {
            ntf.setType(NotificationType.ERROR);
            ntf.setMessage(rb.getString("shp_field_error"));
            ntf.show();
        }
    }

    private void updateProduct() {
        String q = "update produktet set barcode = ?, emri = ?, kategoria_id = ?, " +
                "qmimi_shitjes = ?, njesia = ?, modifikuar = current_timestamp(), " +
                "zbritje = ?, foto = ?, qmimi_std = ?, qmimi_shumic = ?, nr_njesia = ?, sasia = ? " +
                "where id = ?";
        try (PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, bc.getText());
            ps.setString(2, emri.getText());
            ps.setInt(3, VariablatPublike.revProdKat.get(cbKategoria.getSelectionModel().getSelectedItem()));
//            ps.setInt(4, Integer.parseInt(stok.getText()));
//            ps.setInt(4, stokCrit.getText().isEmpty() ? 0 : Integer.parseInt(stokCrit.getText()));
//            ps.setDouble(6, qmimiStd.getText().isEmpty() ? 0 : Double.parseDouble(qmimiStd.getText()));
            ps.setDouble(4, Double.parseDouble(qmimiShitjes.getText()));
            ps.setInt(5, cbNjesia.getSelectionModel().getSelectedIndex());
            ps.setDouble(6, zbritje.getText().isEmpty() ? 0 : Double.parseDouble(zbritje.getText()));
//            ps.setDouble(10, qmimiStd.getText().isEmpty() ? 0 : Double.parseDouble(qmimiStd.getText()));
            ps.setString(7, ivProdFoto.getImage() != null ? ivProdFoto.getImage().getUrl().substring(6, ivProdFoto.getImage().getUrl().length()) : "");
            ps.setDouble(8, qmimiStd.getText().isEmpty() ? 0 : Double.parseDouble(qmimiStd.getText()));
            ps.setDouble(9, qmimiShumice.getText().isEmpty() ? 0 : Double.parseDouble(qmimiShumice.getText()));
            ps.setString(10, txtNjesia.getText());
            ps.setInt(11, Integer.parseInt(stok.getText()));
            ps.setInt(12, id);

            ps.execute();

            ntf.setMessage(MessageFormat.format(rb.getString("shp_update_sukses"), emri.getText()));
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();

        }catch (NumberFormatException nfe) {
            ntf.setMessage(rb.getString("shp_format_error"));
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
//        stokCrit.clear();
        cbKategoria.getSelectionModel().select(0);
        cbNjesia.getSelectionModel().select(0);
        txtNjesia.clear();
    }

}
