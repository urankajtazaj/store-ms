package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.constructors.ShitjetProd;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-19.
 */
public class Shitjet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    @FXML private TableView<ShitjetProd> tbl;
    @FXML private FlowPane flow;
    @FXML private ScrollPane scroll;
    @FXML private Label lTotal, lPagesa, lKusuri;
    @FXML private TableColumn colAct, colSasia;
    @FXML private TextField txtProd;
    @FXML private ComboBox<String> cbCat, cbKons;

    BigDecimal qmimi = BigDecimal.ZERO, pgs = BigDecimal.ZERO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        merrKons();
        cbKons.getItems().clear();
        Iterator<String> itK = VariablatPublike.konsEmri.iterator();
        while (itK.hasNext()) {
            cbKons.getItems().add(itK.next());
        }
        cbKons.getSelectionModel().select(0);

        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lTotal.setText(VariablatPublike.decimalFormat.format(0));
        cbCat.getItems().clear();
        cbCat.getItems().add("Te gjitha");
        Iterator<String> it = VariablatPublike.prodKat.iterator();
        while (it.hasNext()) {
            cbCat.getItems().add(it.next());
        }
        cbCat.getSelectionModel().select(0);

        cbCat.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            getProducts(txtProd.getText(), nv.toString());
        });

        txtProd.setOnKeyReleased(e -> {
            getProducts(txtProd.getText(), cbCat.getSelectionModel().getSelectedItem());
        });

        txtProd.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                firstButton((Button) flow.getChildren().get(0));
                txtProd.setText("");
            }
        });

        colAct.setCellFactory(e -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        Button btn = new Button();
                        ImageView iv = new ImageView(new Image("/sample/photo/trash.png"));
                        iv.setPreserveRatio(true);
                        iv.setFitWidth(14);
                        btn.setGraphic(iv);
                        btn.setOnAction(e -> {
                            ShitjetProd sp = tbl.getItems().get(getIndex());
                            qmimi = qmimi.subtract(new BigDecimal((sp.getQmimi() * Double.parseDouble(sp.getSasia().getText()))+""));
                            tbl.getItems().remove(getIndex());
                            lTotal.setText(VariablatPublike.decimalFormat.format(qmimi.doubleValue()));
                        });
                        setGraphic(btn);
                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

        colSasia.setCellFactory(e -> {
            return new TableCell<String, TextField>() {
                @Override
                protected void updateItem(TextField item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        ShitjetProd sp = tbl.getItems().get(getIndex());
                        TextField tf = new TextField(sp.getSasia().getText());
                        setOnKeyReleased(e -> {
                            if (Pattern.compile("[0-9.]+").matcher(e.getText()).matches()) {
                                lTotal.setText(VariablatPublike.decimalFormat.format(
                                        merrQmimet(tf.getText(), getIndex()).doubleValue()));
                            }
                        });

                        setGraphic(tf);
                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

        getProducts("", "");
    }

//    LEXO TE GJITH TABELEN PER SHITJE DHE KTHE QMIMIN PAS NDRYSHIMIT TE SASISE
    private BigDecimal merrQmimet (String s, int index){
        BigDecimal t = BigDecimal.ZERO;

        for (int i = 0; i < tbl.getItems().size(); i++) {
            ShitjetProd sp = tbl.getItems().get(i);

            if (s.equals("0") || s.isEmpty()) {
                t = t.add(new BigDecimal(sp.getQmimi()));
                continue;
            }

            if (i == index)
                sp.getSasia().setText(s);

            t = t.add(new BigDecimal((sp.getQmimi() * Double.parseDouble(sp.getSasia().getText()))+""));
        }

        qmimi = t;
        return t;
    }

//    MERR PRODUKTET DHE KRIJO BUTONAT PER SHITJE
    private void getProducts (String q, String kat) {

        try {
            Statement stmt = con.createStatement();

            StringBuilder sb = new StringBuilder("select * from vprod where (barcode like lower('%" + q + "%') or lower(emri) like " +
                    "lower('%" + q + "%'))");

            if (!kat.isEmpty() && !kat.equals("Te gjitha")) {
                sb.append(" and kategoria_id = " + VariablatPublike.revProdKat.get(kat));
            }

            ResultSet rs = stmt.executeQuery(sb.toString());

            flow.getChildren().clear();
            while (rs.next()) {
                Button button = new Button(rs.getString("emri") + "\n(" + VariablatPublike.decimalFormat.format(rs.getDouble("qmimi_shitjes")) + ")");
                button.setTextAlignment(TextAlignment.CENTER);
                button.setWrapText(true);
                button.getStyleClass().addAll("btn", "bigBtn");
                button.setStyle("-fx-background-color: " + rs.getString("bg") + "; -fx-text-fill: " + rs.getString("fg"));
                button.setId(rs.getString("id") + " " + rs.getDouble("qmimi_shitjes") + " " + rs.getString("njesia"));

                button.setOnAction(e -> {
                    firstButton(button);
                });

                flow.getChildren().add(button);
            }

        }catch (Exception e) { e.printStackTrace();}

    }

    private void firstButton (Button button){
        try {
            tbl.getItems().add(new ShitjetProd(Integer.parseInt(button.getId().split(" ")[0]),
                    button.getText().split("\n")[0], Double.parseDouble(button.getId().split(" ")[1]), button.getId().split(" ")[2]));
            qmimi = qmimi.add(new BigDecimal(button.getId().split(" ")[1]));
            lTotal.setText(VariablatPublike.decimalFormat.format(qmimi.doubleValue()));
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    private void perfundoPagesen (){
        try {
            if (tbl.getItems().size() > 0 && pgs.compareTo(BigDecimal.ZERO) > 0 && qmimi.compareTo(BigDecimal.ZERO) > 0) {
                Statement stmt = con.createStatement();
                stmt.addBatch("insert into rec values (null, current_timestamp())");
                for (ShitjetProd sp : tbl.getItems()) {
                    batch(stmt, sp.getId(), sp.getQmimi(), pgs, Double.parseDouble(sp.getSasia().getText()));
                }
                stmt.executeBatch();
                MesazhetPublike.Lajmerim("Shitja u krye me sukses", MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.SUCCESS, 5);
                pastro();
            }else {
                MesazhetPublike.Lajmerim(tbl.getItems().size() == 0 ? "Duhet te zgjedhet se paku nje produkt" : "Nuk eshte procesuar pagesa",
                        MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.ERROR, 5);
            }
        }catch (Exception e) {
            MesazhetPublike.Lajmerim("Shitja nuk u krye me sukses.\nNje gabim ka ndodhur.", MesazhetPublike.ButtonType.OK_BUTTON, MesazhetPublike.NotificationType.ERROR, 0);
            e.printStackTrace();}
    }

    private void pastro (){
        qmimi = BigDecimal.ZERO;
        pgs = BigDecimal.ZERO;
        lTotal.setText(VariablatPublike.decimalFormat.format(qmimi.doubleValue()));
        lKusuri.setText("0");
        lPagesa.setText("0");
        tbl.getItems().clear();
    }

    private void batch (Statement ps, int id, double qmimi, BigDecimal pagesa, double sasia) throws Exception {
        ps.addBatch("insert into shitjet values (null, "+id+", "+VariablatPublike.revKons.get(cbKons.getSelectionModel().getSelectedItem())+
                ", current_timestamp(), "+pagesa+", "+VariablatPublike.uid+", "+sasia+", (select max(rec_id) from rec limit 1))");
    }

    private void merrKons(){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select emri, id from konsumatoret order by id")) {
            while (rs.next()) {
                VariablatPublike.konsEmri.add(rs.getString("emri"));
                VariablatPublike.revKons.put(rs.getString("emri"), rs.getInt("id"));
            }
        }catch (Exception e ){ e.printStackTrace(); }
    }

    @FXML
    private void pagesa (){
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Pagesa.fxml"));
            Pagesa pagesa = new Pagesa();
            pagesa.setlPagesa(lPagesa);
            pagesa.setlKusuri(lKusuri);
            pagesa.setPagesa(pgs);
            pagesa.setStage(stage);
            pagesa.setTotal(qmimi);
            loader.setController(pagesa);
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 300, 100);
            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    pagesa.shtoPagesen();
                }else if (e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
            pgs = pagesa.getPagesa();

        }catch (Exception e) {e.printStackTrace();}
    }

}
