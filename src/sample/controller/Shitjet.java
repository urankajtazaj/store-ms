package sample.controller;

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
import sample.Enums.*;
import sample.Enums.ButtonType;
import sample.constructors.ShitjetProd;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-19.
 */
public class Shitjet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();
    Notification ntf = new Notification();

    @FXML private TableView<ShitjetProd> tbl;
    @FXML private FlowPane flow;
    @FXML private ScrollPane scroll;
    @FXML private Label lTotal, lPagesa, lKusuri, lTvsh, lSubTtl;
    @FXML private TableColumn colAct, colSasia;
    @FXML private TextField txtProd;
    @FXML private ComboBox<String> cbCat, cbKons;
    @FXML private CheckBox cbShtypPagesen;

    BigDecimal qmimi = BigDecimal.ZERO, pgs = BigDecimal.ZERO;

    Receta receta = new Receta();

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
            if (e.getCode().equals(KeyCode.ENTER) && flow.getChildren().size() > 0) {
                firstButton((Button) flow.getChildren().get(0));
                txtProd.setText("");
            }
        });

        lTvsh.setText(VariablatPublike.decimal.format(VariablatPublike.tvsh) + "%");

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
                            qmimi = qmimi.subtract(new BigDecimal((Double.parseDouble(sp.getQmimi().substring(0, sp.getQmimi().length()-1)) *
                                    Double.parseDouble(sp.getSasia().getText()))+""));
                            tbl.getItems().remove(getIndex());
                            lSubTtl.setText(VariablatPublike.decimalFormat.format(qmimi.doubleValue()));
                            lTotal.setText(VariablatPublike.decimalFormat.format(qmimi.doubleValue() + (qmimi.doubleValue()*VariablatPublike.tvsh/100)));
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
                                double ttl = merrQmimet(tf.getText(), getIndex()).doubleValue();
                                lSubTtl.setText(VariablatPublike.decimalFormat.format(ttl));
                                lTotal.setText(VariablatPublike.decimalFormat.format(
                                        ttl + ttl * VariablatPublike.tvsh/100));
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

            double qm = Double.parseDouble(sp.getQmimi().substring(0, sp.getQmimi().length()-1));
            t = t.add(new BigDecimal((((qm - (qm * Double.parseDouble(sp.getZbritje().substring(0, sp.getZbritje().length()-1))/100)) *
                    Double.parseDouble(sp.getSasia().getText()))+"")));
        }

        qmimi = t;
        return qmimi;
    }

//    MERR PRODUKTET DHE KRIJO BUTONAT PER SHITJE
    private void getProducts (String q, String kat) {

        try {
            Statement stmt = con.createStatement();

            StringBuilder sb = new StringBuilder("select * from vprod where (barcode like lower('" + q + "%') or lower(emri) like " +
                    "lower('%" + q + "%'))");

            if (!kat.isEmpty() && !kat.equals("Te gjitha")) {
                sb.append(" and kategoria_id = " + VariablatPublike.revProdKat.get(kat));
            }

            sb.append(" order by kategoria_id");
            ResultSet rs = stmt.executeQuery(sb.toString());

            flow.getChildren().clear();
            while (rs.next()) {
                Button button = new Button((rs.getString("emri").length() >= 30 ? rs.getString("emri").substring(0, 30) : rs.getString("emri")) +
                        "\n(" + VariablatPublike.decimalFormat.format(rs.getDouble("qmimi_shitjes")) + ")");
                button.setTextAlignment(TextAlignment.CENTER);
                button.setWrapText(true);
                button.getStyleClass().addAll("btn", "bigBtn");
                button.setStyle("-fx-background-color: " + rs.getString("bg") + "; -fx-text-fill: " + rs.getString("fg"));
                button.setId(rs.getString("id") + " " + rs.getDouble("qmimi_shitjes") + " " + rs.getString("njesia") + " " + rs.getDouble("zbritje"));

                button.setOnAction(e -> {
                    firstButton(button);
                });

                flow.getChildren().add(button);
            }

        }catch (Exception e) { e.printStackTrace();}

    }

    private void firstButton (Button button){
        try {
            String[] dt = button.getId().split(" ");
            double qm = Double.parseDouble(dt[1]);
            double zbr = Double.parseDouble(dt[3]);
            double zbrttl = qm - (qm * zbr/100);
            tbl.getItems().add(new ShitjetProd(Integer.parseInt(dt[0]),
                    button.getText().split("\n")[0], VariablatPublike.decimalFormat.format(zbrttl), dt[2],
                    VariablatPublike.decimal.format(zbr) + "%"));
            qmimi = qmimi.add(new BigDecimal(Double.parseDouble(dt[1]) - (Double.parseDouble(dt[1]) * Double.parseDouble(dt[3])/100)));
            lSubTtl.setText(VariablatPublike.decimalFormat.format(qmimi.doubleValue()));
            lTotal.setText(VariablatPublike.decimalFormat.format(qmimi.doubleValue() + (qmimi.doubleValue() * VariablatPublike.tvsh/100)));
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    private void updateProd (){
        try (Statement st = con.createStatement()){
            for (ShitjetProd data : tbl.getItems()) {
                upbatch(st, data.getId(), Double.parseDouble(data.getSasia().getText()));
            }
            st.executeBatch();
        }catch (Exception e) {e.printStackTrace();}
    }

    private void upbatch (Statement ps, int id, double sasia) throws Exception{
        ps.addBatch("update produktet set sasia = sasia - "+sasia+" where id = " + id);
    }

    @FXML
    private void perfundoPagesen (){
        try {
            if (tbl.getItems().size() > 0 && pgs.compareTo(BigDecimal.ZERO) > 0 && qmimi.compareTo(BigDecimal.ZERO) > 0 && qmimi.compareTo(pgs) <= 0) {
                Statement stmt = con.createStatement();
                stmt.addBatch("insert into rec values (null, current_timestamp(), "+VariablatPublike.tvsh+")");
                int i = 0;
                double qm = 0;
                for (ShitjetProd sp : tbl.getItems()) {
                    qm = Double.parseDouble(sp.getQmimi().substring(0, sp.getQmimi().length()-1));
                    if (cbShtypPagesen.isSelected()) {
                        receta.setData(sp.getEmri(), Double.parseDouble(sp.getSasia().getText()), qm,
                                Double.parseDouble(sp.getZbritje().substring(0, sp.getZbritje().length()-1)) / 100, i++);
                    }
                    batch(stmt, sp.getId(), qm, pgs.compareTo(BigDecimal.ZERO) <= 0 ? new BigDecimal(0+"") : pgs, Double.parseDouble(sp.getSasia().getText()));
                }
                stmt.executeBatch();
                updateProd();
                if (cbShtypPagesen.isSelected()) {
                    receta.setTvsh((int)VariablatPublike.tvsh);
                    receta.setPagesa(pgs.doubleValue());
                    receta.krijoFaturen();
                }
                ntf.setButton(ButtonType.NO_BUTTON);
                ntf.setType(NotificationType.SUCCESS);
                ntf.setDuration(3);
                ntf.setMessage("Shitja u krye me sukses");
                ntf.show();
                pastro();
            }else {
                ntf.setType(NotificationType.ERROR);
                ntf.setMessage(tbl.getItems().size() == 0 ? "Duhet te zgjedhet se paku nje produkt" : "Nuk eshte procesuar pagesa");
                ntf.show();
            }
        }catch (Exception e) {
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
                ", current_timestamp(), "+pagesa+", "+VariablatPublike.uid+", "+sasia+", (select max(rec_id) from rec limit 1), "+
                VariablatPublike.uid2+")");
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
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
            pgs = pagesa.getPagesa();

        }catch (Exception e) {e.printStackTrace();}
    }

}
