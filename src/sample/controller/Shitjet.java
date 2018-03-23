package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;
import sample.constructors.ShitjetProd;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    @FXML private TableColumn colAct, colSasia, colQmimi, colNjesia;
    @FXML private TextField txtProd;
    @FXML private ComboBox<String> cbCat, cbKons;
    @FXML private CheckBox cbShtypPagesen;
    @FXML private RadioButton rbPakice, rbShumic;
    NumberFormat format = NumberFormat.getInstance(Locale.US);
    DecimalFormat pct = new DecimalFormat("00");

    BigDecimal qmimi = BigDecimal.ZERO, pgs = BigDecimal.ZERO, qmShum = BigDecimal.ZERO;

    Receta receta = new Receta();

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rb = resources;

        if (!PrintReceipt.availablePrinter())
            cbShtypPagesen.setDisable(true);

        merrKons();
        cbKons.getItems().clear();
        Iterator<String> itK = VariablatPublike.konsEmri.iterator();
        while (itK.hasNext()) {
            cbKons.getItems().add(itK.next());
        }
        cbKons.getSelectionModel().select(0);

        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lTotal.setText(VariablatPublike.toMoney(0));
        cbCat.getItems().clear();
        cbCat.getItems().add(rb.getString("te_gjitha"));
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
                firstButton((HBox) flow.getChildren().get(0));
                txtProd.setText("");
            }
        });

        lTvsh.setText(VariablatPublike.tvsh > 0 ? ("" + VariablatPublike.tvsh).split("\\.")[1] + "%" : 0 + "%");

        colNjesia.setCellFactory(e -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        if (item.equals("0")) setText(rb.getString("cope"));
                        else if (item.equals("1")) setText(rb.getString("paketim"));
                        else if (item.equals("2")) setText(rb.getString("Liter"));
                        else if (item.equals("3")) setText(rb.getString("Kg"));
                    } else {
                        setText("");
                        setGraphic(null);
                    }

                }
            };
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
                            try {
                                qmimi = qmimi.subtract(new BigDecimal((format.parse(sp.getQm().getText()).doubleValue() *
                                        Double.parseDouble(sp.getSasia().getText()))+""));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            tbl.getItems().remove(getIndex());
                            lSubTtl.setText(VariablatPublike.toMoney(qmimi.doubleValue()/VariablatPublike.tvsh));
                            lTotal.setText(VariablatPublike.toMoney(qmimi.doubleValue()));
//                            lShumic.setText(VariablatPublike.toMoney(qmShum.doubleValue() + (qmShum.doubleValue()*VariablatPublike.tvsh/100)));
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
                                double ttl = merrQmimet(((TextField) colQmimi.getCellData(getIndex())).getText(), tf.getText(), getIndex(), rbShumic.isSelected()).doubleValue();
                                lSubTtl.setText(VariablatPublike.toMoney(ttl/VariablatPublike.tvsh));
                                lTotal.setText(VariablatPublike.toMoney(ttl));
                            }else if (e.getCode().equals(KeyCode.ENTER)) {
                                txtProd.requestFocus();
                            }
                        });

                        setGraphic(tf);
                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

        colQmimi.setCellFactory(e -> {
            return new TableCell<String, TextField>() {
                @Override
                protected void updateItem(TextField item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        ShitjetProd sp = tbl.getItems().get(getIndex());
                        if (rbShumic.isSelected()) {
                            if (!item.getText().isEmpty()) {
                                item.setText(item.getText());
                            } else {
                                item.setText(sp.getQmimiShum());
                            }
                        } else {
                            if (!item.getText().isEmpty()) {
                                item.setText(item.getText());
                            } else {
                                item.setText(sp.getQmimi());
                            }
                        }

                        setOnKeyPressed(e -> {
                            if (e.getCode().equals(KeyCode.ENTER)) {
                                double ttl = merrQmimet(item.getText(), ((TextField) colSasia.getCellData(getIndex())).getText(), getIndex(), rbShumic.isSelected()).doubleValue();
                                lSubTtl.setText(VariablatPublike.toMoney(ttl/VariablatPublike.tvsh));
                                lTotal.setText(VariablatPublike.toMoney(ttl));
                            }
                        });

                        setGraphic(item);
                    } else {
                        setText("");
                        setGraphic(null);
                    }
                }
            };
        });

        getProducts("", "");

        rbPakice.setOnAction(e -> updateQmimet(1));
        rbShumic.setOnAction(e -> updateQmimet(2));
    }

    private void updateQmimet(int id) {
//        1 = pakica
//        2 = shumic

        for (ShitjetProd p : tbl.getItems()) {
            if (id == 1) {
                p.getQm().setText(p.getQmimi());
            } else {
                p.getQm().setText(p.getQmimiShum());
            }

            double ttl = merrQmimet(p.getQm().getText(), p.getSasia().getText(), -2, rbShumic.isSelected()).doubleValue();
            lSubTtl.setText(VariablatPublike.toMoney(ttl/VariablatPublike.tvsh));
            lTotal.setText(VariablatPublike.toMoney(ttl));

        }

    }

//    LEXO TE GJITH TABELEN PER SHITJE DHE KTHE QMIMIN PAS NDRYSHIMIT TE SASISE
    private BigDecimal merrQmimet (String s, String ss, int index, boolean nqmimi){
        BigDecimal t = BigDecimal.ZERO;

        for (int i = 0; i < tbl.getItems().size(); i++) {
            ShitjetProd sp = tbl.getItems().get(i);

            if (index != -2) {
                if (s.equals("0") || s.isEmpty()) {
                    t = t.add(new BigDecimal(Double.parseDouble(((TextField) colQmimi.getCellData(i)).getText())));
                    continue;
                }
            }

            if (index == -1 || i == index) {
                sp.getSasia().setText(ss);
                sp.getQm().setText(s);
            }

            double qm = Double.parseDouble(((TextField)colQmimi.getCellData(i)).getText());
            t = t.add(new BigDecimal(qm).multiply(new BigDecimal(sp.getSasia().getText())));
        }

        qmimi = t;
        return qmimi;
    }

//    MERR PRODUKTET DHE KRIJO BUTONAT PER SHITJE
    private void getProducts (String q, String kat) {

        try {
            Statement stmt = con.createStatement();

            StringBuilder sb = new StringBuilder("select * from vprod where (barcode = '" + q + "' or lower(emri) like " +
                    "lower('%" + q + "%'))");

            if (!kat.isEmpty() && !kat.equals(rb.getString("te_gjitha"))) {
                sb.append(" and kategoria_id = " + VariablatPublike.revProdKat.get(kat));
            }

            sb.append(" and sasia > 0 order by kategoria_id limit 40");
            ResultSet rs = stmt.executeQuery(sb.toString());

            flow.getChildren().clear();
            while (rs.next()) {
                Circle circle = new Circle(5, Paint.valueOf(rs.getString("bg")));
                HBox hbox1 = new HBox();
                HBox hbox2 = new HBox();
                Button button = new Button((rs.getString("emri").length() >= 60 ? rs.getString("emri").substring(0, 60) : rs.getString("emri")));
                button.setTextAlignment(TextAlignment.CENTER);
                button.setWrapText(true);

                hbox2.getChildren().add(circle);
                hbox1.getChildren().addAll(button, hbox2);
                hbox1.setHgrow(hbox2, Priority.ALWAYS);
                hbox1.setAlignment(Pos.CENTER_LEFT);
                hbox2.setAlignment(Pos.CENTER_RIGHT);

                hbox1.getStyleClass().addAll("btn", "bigBtn");
                button.getStyleClass().addAll("sellBtn", "bigBtn");

                ImageView ivButton = null;
                if (rs.getString("foto") != null) {
                    if (!rs.getString("foto").isEmpty()) {
                        ivButton = new ImageView(new Image("file:///" + rs.getString("foto")));
                        ivButton.setPreserveRatio(true);
                        ivButton.setFitHeight(80);
                        ivButton.setSmooth(true);

                        hbox1.setPrefHeight(85);
                        hbox1.setMinHeight(85);
                        hbox1.setMaxHeight(85);
                    }
                }

                button.setTextAlignment(TextAlignment.LEFT);
                button.setGraphic(ivButton);
//                circle.setStyle("-fx-fill: " + rs.getString("bg") + "; -fx-text-fill: " + rs.getString("fg"));
                button.setId(rs.getString("id") + " " + rs.getDouble("qmimi_shitjes") + " " + rs.getInt("njesia") + " " + rs.getDouble("zbritje")
                        + " " + rs.getDouble("qmimi_shumic"));

                hbox1.setMinWidth(flow.getWidth()/3-10);
                hbox1.setMaxWidth(flow.getWidth()/3-10);
                hbox1.setPrefWidth(flow.getWidth()/3-10);

//                button.setMinSize(hbox1.getMinWidth(), hbox2.getMinHeight());

                flow.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        hbox1.setMinWidth(newValue.doubleValue()/3-10);
                        hbox1.setPrefWidth(newValue.doubleValue()/3-10);
                        hbox1.setMaxWidth(newValue.doubleValue()/3-10);
                    }
                });

                button.setOnAction(e -> {
                    firstButton(hbox1);
                });
                hbox1.setOnMousePressed(e -> {
                    firstButton(hbox1);
                });
                flow.getChildren().add(hbox1);
            }

        }catch (Exception e) { e.printStackTrace();}

    }

    private void firstButton (HBox button){
        try {
            String[] dt = ((Button) button.getChildren().get(0)).getId().split(" ");
            double qm = Double.parseDouble(dt[1]);
            double zbr = Double.parseDouble(dt[3]);
            double zbrttl = qm - (qm * zbr/100);
            double shumic = Double.parseDouble(dt[4]);

            ShitjetProd sp = new ShitjetProd(Integer.parseInt(dt[0]),
                    ((Button) button.getChildren().get(0)).getText().split("\n")[0], qm + "", dt[2],
                    VariablatPublike.decimal.format(zbr) + "%", shumic+"");

            tbl.getItems().add(sp);

            qmimi = qmimi.add(new BigDecimal((rbPakice.isSelected() ? qm : shumic) - ((rbPakice.isSelected() ? qm : shumic) * zbr/100)));
//            qmShum = qmShum.add(new BigDecimal(shumic - (shumic * zbr/100)));

            lSubTtl.setText(VariablatPublike.toMoney(qmimi.doubleValue()/VariablatPublike.tvsh));
            lTotal.setText(VariablatPublike.toMoney(qmimi.doubleValue()));

//            lShumic.setText(VariablatPublike.toMoney(qmShum.doubleValue() + (qmShum.doubleValue() * VariablatPublike.tvsh/100)));
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
        ps.addBatch("update produktet set sasia = sasia - " + sasia + " where id = " + id);
    }

    @FXML
    private void perfundoPagesen (){
        try {
            if (tbl.getItems().size() > 0) {
//                if ((rbPakice.isSelected() && qmimi.compareTo(pgs) <= 0) || (rbShumic.isSelected() && qmShum.compareTo(pgs) <= 0)) {
                    Statement stmt = con.createStatement();
                    int lloji = rbPakice.isSelected() ? 0 : 1;

                    if (qmimi.compareTo(pgs) <= 0) {
                        stmt.addBatch("insert into rec values (null, current_date(), " +
                                (VariablatPublike.tvsh > 0 ? ("" + VariablatPublike.tvsh).split("\\.")[1] : 0) +
                                ", " + lloji + ", 0, 0)");
                    } else {
                        stmt.addBatch("insert into rec values (null, current_date(), " +
                                (VariablatPublike.tvsh > 0 ? ("" + VariablatPublike.tvsh).split("\\.")[1] : 0)
                                + ", " + lloji + ", 1, 0)");
                    }

                    int i = 0;
                    double qm = 0;
                    for (ShitjetProd sp : tbl.getItems()) {
                        qm = format.parse(sp.getQm().getText()).doubleValue();
                        batch(stmt, sp.getId(), qm, pgs.compareTo(BigDecimal.ZERO) <= 0 ? new BigDecimal(0 + "") : pgs, Double.parseDouble(sp.getSasia().getText()));
                    }
                    stmt.executeBatch();
                    updateProd();
                    ntf.setButton(ButtonType.NO_BUTTON);
                    ntf.setType(NotificationType.SUCCESS);
                    ntf.setDuration(2);
                    ntf.setMessage(rb.getString("shit_sukses"));
                    ntf.show();

                    rbPakice.setSelected(true);

                    receta.setTvsh((int) VariablatPublike.tvsh);
                    receta.setPagesa(pgs.doubleValue());

                    pastro();

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                        new PrintReceipt(receta.krijoFaturen());

                            try {
                                JasperReport jr = JasperCompileManager.compileReport(System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/Faktura.jrxml");
                                HashMap<String, Object> params = new HashMap<>();
                                params.put("anuluar", false);
                                params.put("gjuha", VariablatPublike.LANG);
                                params.put("date", new java.util.Date());
                                params.put("pnt", VariablatPublike.pntEmri);
                                params.put("email", VariablatPublike.pntEmail);
                                params.put("adresa", VariablatPublike.ADRESA);
                                params.put("nrFiskal", VariablatPublike.nrFiskal);
                                params.put("foto", System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/2.jpg");
                                params.put("Tel", VariablatPublike.pntTel);
                                params.put("Emri", VariablatPublike.emriShitores);
                                params.put("Faktura", getRecId());
                                params.put("iban", VariablatPublike.IBAN);
                                params.put("banka", VariablatPublike.BANKA);
                                params.put("swift", VariablatPublike.SWIFT);
                                params.put("konto", VariablatPublike.KONTO);
                                params.put("konsumatori", VariablatPublike.konsShitur.get(VariablatPublike.revKons.get(cbKons.getSelectionModel().getSelectedItem()))[0]);
                                params.put("konsAdresa", VariablatPublike.konsShitur.get(VariablatPublike.revKons.get(cbKons.getSelectionModel().getSelectedItem()))[1]);
                                params.put("konsQyteti", VariablatPublike.konsShitur.get(VariablatPublike.revKons.get(cbKons.getSelectionModel().getSelectedItem()))[2]);

                                JasperPrint print = JasperFillManager.fillReport(jr, params, con);
                                String filename = System.getProperty("user.home") + "/store-ms-files/Raportet/PDF/Faktura-" +
                                        new SimpleDateFormat("dd-MM-yy h-m").format(new Date()) + ".pdf";
                                JasperExportManager.exportReportToPdfFile(print, filename);

                                Desktop.getDesktop().open(new File(filename));

                            } catch (JRException ex) {
                                ex.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                            $V{tvsh}.compareTo(BigDecimal.ZERO) > 0 ?
//$V{total}.divide(new BigDecimal(Double.parseDouble(("1." + ("" + $V{tvsh}).split("\\.")[1])))) :
//$V{total}

                        }
                    });
                    t.setDaemon(true);

                    if (cbShtypPagesen.isSelected() && !cbShtypPagesen.isDisable()) {
                        t.start();
                    }
//                } else {
//                    ntf.setType(NotificationType.ERROR);
//                    ntf.setMessage("Nuk eshte procesuar pagesa.");
//                    ntf.show();
//                }
            }else {
                ntf.setType(NotificationType.ERROR);
                ntf.setMessage(rb.getString("shit_error_nr_prod"));
                ntf.show();
            }
        }catch (Exception e) {
            e.printStackTrace();}
    }

    private void pastro (){
        qmimi = BigDecimal.ZERO;
        pgs = BigDecimal.ZERO;
        lTotal.setText(VariablatPublike.toMoney(qmimi.doubleValue()));
        lKusuri.setText("0");
        lPagesa.setText("0");
        tbl.getItems().clear();
    }

    private void batch (Statement ps, int id, double qmimi, BigDecimal pagesa, double sasia) throws Exception {
        ps.addBatch("insert into shitjet values (null, "+id+", "+VariablatPublike.revKons.get(cbKons.getSelectionModel().getSelectedItem())+
                ", current_date(), "+pagesa+", "+VariablatPublike.uid+", "+sasia+", (select max(rec_id) from rec limit 1), "+
                VariablatPublike.uid2+", 0, " + qmimi + ")");
    }

    private int getRecId() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select max(rec_id) as nr from rec limit 1");

            rs.next();
            return rs.getInt("nr");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void merrKons(){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select emri, id, adresa, qyteti from konsumatoret order by id")) {
            while (rs.next()) {
                VariablatPublike.konsEmri.add(rs.getString("emri"));
                VariablatPublike.revKons.put(rs.getString("emri"), rs.getInt("id"));
                VariablatPublike.konsShitur.put(rs.getInt("id"), new String[] {rs.getString("emri"), rs.getString("adresa"), rs.getString("qyteti")});
            }
        }catch (Exception e ){ e.printStackTrace(); }
    }

    @FXML
    private void pagesa (){
        try {
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/Pagesa.fxml"), rb);
            Pagesa pagesa = new Pagesa();
            pagesa.setlPagesa(lPagesa);
            pagesa.setlKusuri(lKusuri);
            pagesa.setPagesa(pgs);
            pagesa.setStage(stage);
            pagesa.setTotal(qmimi);
            loader.setController(pagesa);
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 340, 150);
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
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            pgs = pagesa.getPagesa();

        }catch (Exception e) {e.printStackTrace();}
    }

}
