package sample.controller;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.*;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;
import sample.constructors.ProduktetClass;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by uran on 17-04-07.
 */
public class Produktet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    Notification ntf = new Notification();

    Punetoret pnt = new Punetoret();

    @FXML private TextField txtId, txtProd, txtProdh, txtQmimi, txtFurn;
    @FXML private ComboBox<String> cbCat, cbOp;
    @FXML private TableView<ProduktetClass> tblProduktet;
    @FXML private TableColumn colStatusi, colAksion, colSasia, colNjesia;
    @FXML private BarChart<String, Number> barChart, barChart2;
    @FXML private Button btnPakice, btnAdd;

    private Stage stage;
    SimpleDateFormat tf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-s");
    SimpleDateFormat sqlDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BorderPane bp;

    public void setBp (BorderPane bp) {
        this.bp = bp;
    }

    Random rand = new Random();

    ResourceBundle rb;

    private RotateTransition transition;
    private ImageView iv;

    public void setTransition(RotateTransition transition) {
        this.transition = transition;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rb = resources;

        if (!VariablatPublike.prod_add) btnAdd.setVisible(false);

        Thread produktet = new Thread(new Runnable() {
            @Override
            public void run() {
                fillTable();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fillBarChart();
                    }
                });
            }
        });

        produktet.setDaemon(true);
        produktet.start();

        btnPakice.setOnAction(e -> nxjerrRaport(0));
//        btnShumic.setOnAction(e -> nxjerrRaport(1));

        merrProduktetKat();

        cbCat.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> filterTable());
        cbOp.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> filterTable());
        txtId.setOnKeyPressed(e -> { if (e.getCode().equals(KeyCode.ENTER)) filterTable(); });
        txtProd.setOnKeyPressed(e -> { if (e.getCode().equals(KeyCode.ENTER)) filterTable(); });
        txtQmimi.setOnKeyPressed(e -> { if (e.getCode().equals(KeyCode.ENTER)) filterTable(); });

        cbCat.getItems().clear();
        cbCat.getItems().add(rb.getString("te_gjitha"));
        Iterator<String> it = VariablatPublike.prodKat.iterator();
        while (it.hasNext()) {
            cbCat.getItems().add(it.next());
        }
        cbCat.getSelectionModel().select(0);

        colAksion.setCellFactory(e -> {
            return new TableCell<String, HBox>() {

                @Override
                protected void updateItem(HBox item, boolean empty) {

                    super.updateItem(item, empty);
                    if (!empty) {

                        Button btnDel = new Button();
                        Button btnEd = new Button();
                        Button btnStock = new Button("Stock");
                        HBox hbox = new HBox();

                        if (VariablatPublike.prod_edit) hbox.getChildren().add(btnEd);
                        if (VariablatPublike.prod_del) hbox.getChildren().add(btnDel);

                        ImageView deliv = new ImageView(new Image("/sample/photo/trash.png"));
                        ImageView ediv = new ImageView(new Image("/sample/photo/settings.png"));
                        ImageView stiv = new ImageView(new Image("/sample/photo/add-blue.png"));

                        btnDel.setTooltip(new Tooltip("Fshi produktin"));

                        deliv.setPreserveRatio(true);
                        ediv.setPreserveRatio(true);
                        stiv.setPreserveRatio(true);

                        deliv.setFitWidth(15);
                        ediv.setFitWidth(15);
                        stiv.setFitWidth(15);

                        btnEd.setGraphic(ediv);
                        btnDel.setGraphic(deliv);
                        btnStock.setGraphic(stiv);

                        hbox.setAlignment(Pos.CENTER);
                        hbox.setSpacing(7);

                        ProduktetClass p = tblProduktet.getItems().get(getIndex());

                        btnStock.setOnAction(e -> {
                            hapShtoStock(p.getId());
                        });

                        btnEd.setOnAction(e -> {
                            try {
                                String njesia = null;
                                rregulloProd(p.getId(), p.getNrNjesia(), p.getQmimiShumice(), p.getEmri(), p.getBc(), p.getQmimiStd(), p.getQmimi().substring(0, p.getQmimi().length()-1), p.getSasia(), p.getSasiaKrit(), p.getZbritje(),
                                        p.getKategoria(), p.getNjesia(), p.getFoto());
                            }catch (Exception ex) {ex.printStackTrace(); }
                        });

                        btnDel.setOnAction(e -> {
                            ntf.setMessage(rb.getString("prod_del_q"));
                            ntf.setType(NotificationType.ERROR);
                            ntf.setButton(ButtonType.YES_NO);
                            ntf.showAndWait();
                            fshiProd(p.getId(), ntf.getDelete());
                        });
                        setGraphic(hbox);
                    }

                }
            };
        });

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

        tblProduktet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void hapShtoStock(int id) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoStock.fxml"));
            ShtoStock sk = new ShtoStock();
            loader.setController(sk);
            Parent root = loader.load();
            Scene scene = new Scene(root, 520, 280);

            sk.btnOk.setOnAction(e -> {
                updateStock(id, sk.txtSasia.getText(), ((ToggleButton) sk.tgTipi.getSelectedToggle()).getText().equals("+") ? '+' : '-');
                stage.close();
                filterTable();
            });

            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    updateStock(id, sk.txtSasia.getText(), ((ToggleButton) sk.tgTipi.getSelectedToggle()).getText().equals("+") ? '+' : '-');
                    stage.close();
                    filterTable();
                }else if (e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.setFill(Color.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setResizable(false);
            stage.setMaxWidth(520);
            stage.setMinWidth(520);
            stage.setMaxHeight(280);
            stage.setMinHeight(280);
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            stage.setScene(scene);
            stage.show();
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void updateStock(int id, String sasia, char op) {

        try (PreparedStatement ps = con.prepareStatement("update produktet set sasia = sasia " + op + " ?, aktual = aktual " + op + " ?, " +
                "hyrje_sot = hyrje_sot " + op + " ?, hyrje_date = current_date() where id = ?")) {
            ps.setDouble(1, Double.parseDouble(sasia));
            ps.setDouble(2, Double.parseDouble(sasia));
            ps.setInt(3, Integer.parseInt(sasia));
            ps.setInt(4, id);
            ps.execute();
        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void filterTable(){
        String q = "select * from produktet where id " + (!txtId.getText().isEmpty() ? "= ?" : "> ?") + " and " +
                "lower(emri) like lower(?) and kategoria_id " + (cbCat.getSelectionModel().getSelectedIndex()==0 ? "> ?" : "= ?") +
                " and qmimi_shitjes " + (txtQmimi.getText().isEmpty() ? "> ?" : cbOp.getSelectionModel().getSelectedItem() + " ?") + " order by sasia";
        try (PreparedStatement ps = con.prepareStatement(q)) {

            ps.setInt(1, txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()));
            ps.setString(2, "%" + txtProd.getText() + "%");
            ps.setInt(3, cbCat.getSelectionModel().getSelectedIndex() == 0 ? 0 : VariablatPublike.revProdKat.get(cbCat.getSelectionModel().getSelectedItem()));
            ps.setDouble(4, txtQmimi.getText().isEmpty() ? 0.0 : Double.parseDouble(txtQmimi.getText()));

            ResultSet rs = ps.executeQuery();

            tblProduktet.getItems().clear();
            while (rs.next()) {
                tblProduktet.getItems().add(new ProduktetClass(rs.getString("barcode"), rs.getInt("id"), rs.getString("emri"),
                        VariablatPublike.mProdKat.get(rs.getInt("kategoria_id")),
                        rs.getString("qmimi_shitjes"),
                        rs.getDouble("qmimi_std"), rs.getInt("sasia"), rs.getInt("stokCrit"),
                        rs.getString("zbritje") + "%", rs.getString("njesia"), rs.getString("foto"), rs.getDouble("qmimi_shumic"), rs.getString("nr_njesia")));
            }

        }catch (NumberFormatException nfe) {
            txtQmimi.clear();
            txtId.clear();
        }
        catch (Exception e) {
            ntf.setMessage(rb.getString("prod_gabim"));
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();
            e.printStackTrace();
        }
    }

    @FXML private void export() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/export.fxml"), rb);
        Parent bpExport = loader.load();
        Export export = loader.getController();
        Stage stage = new Stage();

        export.btnExcel.setOnAction(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    iv.setImage(VariablatPublike.spinning);
                    transition.play();
                    pnt.excelFile("Produktet", "xlsx", keySet());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            VariablatPublike.stopSpinning(transition, iv);
                            ntf.setMessage(rb.getString("prod_doc_sukses"));
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.setButton(ButtonType.NO_BUTTON);
                            ntf.show();
                        }
                    });
                }
            }).start();
            stage.close();
        });

        export.btnCsv.setOnAction(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    toCSV();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ntf.setMessage(rb.getString("prod_doc_sukses"));
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.setButton(ButtonType.NO_BUTTON);
                            ntf.show();
                        }
                    });
                }
            }).start();
            stage.close();
        });

        export.btnPdf.setOnAction(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    iv.setImage(VariablatPublike.spinning);
                    transition.play();
                    toPdf();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            VariablatPublike.stopSpinning(transition, iv);
                            ntf.setMessage(rb.getString("prod_doc_sukses"));
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.setButton(ButtonType.NO_BUTTON);
                            ntf.show();
                        }
                    });
                }
            }).start();
            stage.close();
        });

        export.btnAnulo.setOnAction(e -> {
            stage.close();
        });

        export.btnSql.setOnAction(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    toSql();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ntf.setMessage(rb.getString("prod_doc_sukses"));
                            ntf.setButton(ButtonType.NO_BUTTON);
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.show();
                        }
                    });
                }
            }).start();
            stage.close();
        });

        Scene scene = new Scene(bpExport, 520, 214); // 175
        scene.setFill(Color.TRANSPARENT);
        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ESCAPE)) stage.close();
        });
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setMaxWidth(520);
        stage.setMinWidth(520);
        stage.setMaxHeight(200);
        stage.setMinHeight(200);
        stage.setScene(scene);
        stage.show();
    }

    private void toSql() {
        String path = System.getProperty("user.home") + "/store-ms-files/Raportet/SQL/";
        File file = new File(path + "Produktet_" + tf.format(new Date()) + ".sql");

        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
            StringBuilder sb = new StringBuilder();

            Set<String> kategoria = new HashSet<>();

            sb.append("-- id, kategoria, emri, sasia, qmimi_blerjes, qmimi, njesia, modifikuar, barkodi, zbritje, sasia_minimale\n");
            for (ProduktetClass p : tblProduktet.getItems()) {
                sb.append("insert into produktet values (" + p.getId() + ",'" + p.getKategoria() + "','" + p.getEmri() + "'," +
                p.getSasia() + "," + p.getQmimiStd() + "," + p.getQmimi() + ",'" + p.getNjesia() + "'"+sqlDf.format(new Date())+"'" + p.getBc() + "'," +
                p.getZbritje().substring(0, p.getZbritje().length()-1) + "," + p.getSasiaKrit() + ");\n");
            }
            bw.write(sb.toString());

        }catch (Exception e){ e.printStackTrace(); }
    }

    private void toPdf() {
        try {
            String path = System.getProperty("user.home") + "/store-ms-files/Raportet/";
            JasperReport jasperReport = JasperCompileManager.compileReport(path + "raportet/Produktet.jrxml");
            Map<String, Object> params = new HashMap();
            params.put("Punetori", VariablatPublike.uemri);

            JasperPrint jprint = JasperFillManager.fillReport(jasperReport, params, con);

            JasperExportManager.exportReportToPdfFile(jprint, path + "PDF/Produktet " + tf.format(new Date()) + ".pdf");
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void rregulloProd (int id, String txtNjesia, double qshum, String emri, String bc, double qstd, String qs, int stok, int stokk, String zbritje, String catIndex, String njesia, String foto) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoProdukte.fxml"), rb);
        ShtoProdukte sp = new ShtoProdukte();
        sp.setId(id);
        sp.setEmri(emri);
        sp.setQmimiShumice(qshum);
        sp.setBc(bc);
        sp.setQmimiStd(qstd);
        sp.setQmimiShitjes(Double.parseDouble(qs));
        sp.setStok(stok);
        sp.setStokCrit(stokk);
        sp.setZbritje(Double.parseDouble(zbritje.substring(0, zbritje.length()-1)));
        sp.setCbKategoria(catIndex);
        sp.setCbNjesia(njesia);
        sp.setIvProdFoto(foto);
        sp.setNrNjesia(txtNjesia);
        loader.setController(sp);
        bp.setCenter(loader.load());
    }

    private void fillTable(){
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("select * from produktet order by sasia limit 100")){
            tblProduktet.getItems().clear();
            ObservableList<ProduktetClass> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new ProduktetClass(rs.getString("barcode"), rs.getInt("id"), rs.getString("emri"),
                        VariablatPublike.mProdKat.get(rs.getInt("kategoria_id")),
                        VariablatPublike.toMoney(rs.getDouble("qmimi_shitjes")),
                        rs.getDouble("qmimi_std"), rs.getInt("aktual"), rs.getInt("stokcrit"),
                        rs.getDouble("zbritje") + "%", rs.getString("njesia"), rs.getString("foto"), rs.getDouble("qmimi_shumic"), rs.getString("nr_njesia")));
            }
            tblProduktet.setItems(data);
        }catch (Exception e) {e.printStackTrace();}
    }

    private void fillBarChart(){

        List<String> prodE = new ArrayList<>(), katE = new ArrayList<>();
        List<Integer> prodN = new ArrayList<>(), katN = new ArrayList<>();

        getTopProds(prodE, prodN, katE, katN);

        XYChart.Series series1 = new XYChart.Series();
        for (int i = 0; i < katE.size(); i++) {
            if (i == 5) break;
            series1.getData().add(new XYChart.Data(katE.get(i), katN.get(i)));
        }
        barChart.getData().add(series1);

        XYChart.Series series2 = new XYChart.Series();
        for (int i = 0; i < prodE.size(); i++) {
            if (i == 5) break;
            series2.getData().add(new XYChart.Data(prodE.get(i), prodN.get(i)));
        }
        barChart2.getData().add(series2);

        barChart.setLegendVisible(false);
        barChart2.setLegendVisible(false);

        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data data : series.getData()) {
                StackPane bar = (StackPane) data.getNode();

                Text dt = new Text(data.getYValue() + "");
                dt.getStyleClass().add("bar-text");
                bar.getChildren().add(dt);
            }
        }

        for (XYChart.Series<String, Number> series : barChart2.getData()) {
            for (XYChart.Data data : series.getData()) {
                StackPane bar = (StackPane) data.getNode();

                Text dt = new Text(data.getYValue() + "");
                dt.getStyleClass().add("bar-text");
                bar.getChildren().add(dt);
            }
        }

    }

    private void getTopProds (List<String> prodE, List<Integer> prodN, List<String> katE, List<Integer> katN){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select * from topproduktet order by sasia desc");
             Statement s2 = con.createStatement(); ResultSet rs2 = s2.executeQuery("select * from topkategoria order by sasia desc")) {

            int i = 0;
            while (rs.next()) {
                prodE.add(rs.getString(2));
                prodN.add(rs.getInt(1));
                i++;
            }

            i = 0;
            while (rs2.next()) {
                katE.add(rs2.getString(2));
                katN.add(rs2.getInt(1));
                i++;
            }

        }catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void shtoProdukt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoProdukte.fxml"), rb);
            ShtoProdukte sp = new ShtoProdukte();
            loader.setController(sp);
            Parent parent = loader.load();
            bp.setCenter(parent);
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void fshiProd(int id, boolean delete) {
        if (delete) {
            try (PreparedStatement ps = con.prepareStatement("delete from produktet where id = ?")) {
                ps.setInt(1, id);
                ps.execute();
                fillTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Object[]> keySet (){
        Map<String, Object[]> xlsData = new TreeMap<>();
        int i = 1;
        xlsData.put((i++)+"", new Object[] {"BARKODI", "ID", "PRODUKTI", "KATEGORIA", "SASIA", "NJESIA", "QMIMI", "ZBRITJE"});
        for (ProduktetClass p : tblProduktet.getItems()) {
            xlsData.put((i++)+"", new Object[] {p.getBc(), p.getId(), p.getEmri(), p.getKategoria(), p.getSasia(), "",
                    p.getQmimi(), p.getZbritje()});
        }
        return xlsData;
    }

    private void toCSV(){
        File file = new File(System.getProperty("user.home") + "/store-ms-files/Raportet/CSV/Produktet " + tf.format(new Date()) + ".csv");
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
            file.createNewFile();
            StringBuilder sb = new StringBuilder();

            sb.append("BARKODI,ID,PRODUKTI,KATEGORIA,SASIA,QMIMI,ZBRITJE\n\n");
            for (ProduktetClass p : tblProduktet.getItems()) {
                sb.append(p.getBc() + "," + p.getId() + "," + p.getEmri() + "," + p.getKategoria() + "," + p.getSasia() + "," +
                        p.getQmimi() + "," + p.getZbritje() + "\n\r");
            }

            bw.write(sb.toString());

        }catch (Exception e) { e.printStackTrace(); }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void merrProduktetKat() {
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("select kategoria as k, id from kat_prod")) {
            while (rs.next()) {
                VariablatPublike.prodKat.add(rs.getString("k"));
                VariablatPublike.mProdKat.put(rs.getInt("id"), rs.getString("k"));
                VariablatPublike.revProdKat.put(rs.getString("k"), rs.getInt("id"));
            }
        }catch (Exception e) { e.printStackTrace(); }
    }

    private String[] getRaportinLloji() {
        String[] arr = new String[2];
        try {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from raportimet");

            rs.next();

            arr[0] = rs.getString("lloji");
            arr[1] = rs.getString("last_report");

        }catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    private void nxjerrRaport(int lloji) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                iv.setImage(VariablatPublike.spinning);
                transition.play();
//                if (!getRaportinLloji()[0].equals("0")) {
//                    updateHyrja();
////                    updateSasia();
//                }
                try {
                    JasperReport jr = JasperCompileManager.compileReport(System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/RaportiShitjesShumice.jrxml");

                    updateHyrja();

                    Map<String, Object> map = new HashMap<>();
                    map.put("lloji", lloji);

                    JasperPrint print = JasperFillManager.fillReport(jr, map, con);
                    String filename = System.getProperty("user.home") + "/Desktop/Raporti-"+
                            new SimpleDateFormat("dd-MM-yy h-m").format(new Date())+".pdf";
                    JasperExportManager.exportReportToPdfFile(print, filename);

                    updateSasia();

                    Desktop.getDesktop().open(new File(filename));

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (getRaportinLloji()[0].equals("0")) {
//                    updateHyrja();
//                    updateSasia();
//                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        VariablatPublike.stopSpinning(transition, iv);
                        ntf.setMessage(rb.getString("prod_report_sukses"));
                        ntf.setType(NotificationType.SUCCESS);
                        ntf.setButton(ButtonType.NO_BUTTON);
                        ntf.show();
                    }
                });
            }
        });

        thread.setDaemon(true);
        thread.start();

    }

    private void updateSasia() {
        try {
            String[] id = getRaportinLloji();
            Statement st = con.createStatement();
            if (id[0].equals("0")) {
                st.addBatch("update produktet set sasia = aktual;");
                st.addBatch("update raportimet set last_report = current_date();");
            } else if (id[0].equals("1")) {
                Date last = new SimpleDateFormat("YYYY-MM-dd").parse(id[1]);
                Date curr = new Date();

                long diff = curr.getTime() - last.getTime();
                long days = TimeUnit.MILLISECONDS.toDays(diff);

                if (days != 0) {
                    st.addBatch("update produktet set sasia = aktual;");
                    st.addBatch("update raportimet set last_report = current_date();");
                }
            } else {
                Date last = new SimpleDateFormat("YYYY-MM-dd").parse(id[1]);
                Date curr = new Date();

                long diff = curr.getTime() - last.getTime();
                long days = TimeUnit.MILLISECONDS.toDays(diff);

                if (days >= 30) {
                    st.addBatch("update produktet set sasia = aktual;");
                    st.addBatch("update raportimet set last_report = current_date();");
                }
            }
            st.executeBatch();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateHyrja() {
        try {
            Statement st = con.createStatement();
            String sql =
                    "update produktet set hyrje_sot = (case when produktet.hyrje_date != current_date() then 0 else hyrje_sot end)," +
                            "hyrje_date = (case when produktet.hyrje_date != current_date() then current_date() else hyrje_date end)";

            st.execute(sql);
        }catch (Exception e) { e.printStackTrace(); }
    }
}
