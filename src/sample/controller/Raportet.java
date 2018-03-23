package sample.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.*;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;
import sample.constructors.FaturaDhenat;
import sample.constructors.Faturat;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Created by uran on 17-05-04.
 */
public class Raportet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    //    @FXML private Button btnExportRaport;
    @FXML private ComboBox<String> cbKons;
    @FXML private Button btnAnulo, btnPagesa, btnFaturo;
    @FXML private DatePicker dpPrej, dpDeri;
    @FXML private TableView tblFatura, tblDhenat;
    @FXML private Label lTotal, lPagesa, lKusuri, lCurrTtl, lLastTtl, lKons, lKonsL, lPagMes, lPagMesL, lTvsh, lShitje, lShitjeL;
    private double total, totalLast;

    private double totali = 0, totaliLast = 0, totaliShpenz = 0, totaliShpenzLast = 0;

    @FXML private TableColumn colLloji, colStatusi, colAnuluar;

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        tblFatura.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblDhenat.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        cbKons.getItems().clear();
        cbKons.getItems().add(rb.getString("te_gjitha"));
        cbKons.getSelectionModel().select(0);

        merrKonsList();
        Iterator<String> itK = VariablatPublike.konsEmri.iterator();
        while (itK.hasNext()) {
            cbKons.getItems().add(itK.next());
        }

        cbKons.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (!nv.equals(rb.getString("te_gjitha"))) {
                merrFaturat(nv);
            } else {
                merrFaturat();
            }
        });

        dpPrej.setConverter(VariablatPublike.converter);
        dpDeri.setConverter(VariablatPublike.converter);

        colLloji.setCellFactory(e -> {
            return new TableCell<String, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        if (!item) {
                            setText(rb.getString("pakic"));
                        } else {
                            setText(rb.getString("shumic"));
                        }
                    } else {
                        setText("");
                        setGraphic(null);
                    }
                }
            };
        });

        colStatusi.setCellFactory(e -> {
            return new TableCell<String, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        Circle c = new Circle(6);
                        setAlignment(Pos.CENTER);
                        if (!item) {
                            c.setFill(Paint.valueOf("#2ecc71"));
                        } else {
                            c.setFill(Paint.valueOf("#e74c3c"));
                        }
                        setGraphic(c);
                    } else {
                        setText(null);
                        setGraphic(null);
                    }

                }
            };
        });

        colAnuluar.setCellFactory(e -> {
            return new TableCell<String, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        Circle c = new Circle(6);
                        setAlignment(Pos.CENTER);
                        if (!item) {
                            c.setFill(Paint.valueOf("#2ecc71"));
                        } else {
                            c.setFill(Paint.valueOf("#e74c3c"));
                        }
                        setGraphic(c);
                    } else {
                        setText(null);
                        setGraphic(null);
                    }

                }
            };
        });

//        dpPrej.setValue(LocalDate.now());
//        dpDeri.setValue(LocalDate.now());
        dpPrej.setValue(LocalDate.now().withDayOfMonth(1));
        dpDeri.setValue(LocalDate.now());

        tblFatura.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                merrTeDhenat(((Faturat) nv).getId(), ((Faturat) nv).getKonsumatori(), ((Faturat) nv).isLloji(), ((Faturat) nv).isAnuluar());
            }
        });
        merrFaturat();
    }

    private void merrKonsList(){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select emri, id, adresa, qyteti from konsumatoret order by id")) {
            while (rs.next()) {
                VariablatPublike.konsEmri.add(rs.getString("emri"));
                VariablatPublike.revKons.put(rs.getString("emri"), rs.getInt("id"));
                VariablatPublike.konsShitur.put(rs.getInt("id"), new String[] {rs.getString("emri"), rs.getString("adresa"), rs.getString("qyteti")});
            }
        }catch (Exception e ){ e.printStackTrace(); }
    }

    private void merrFaturat (String kons){
        totali = 0;
        totaliLast = 0;
        totaliShpenz = 0;
        totaliShpenzLast = 0;
        total = 0;
        totalLast = 0;
        btnAnulo.setDisable(true);
        tblDhenat.getItems().clear();
        lTotal.setText("0");
        lPagesa.setText("0");
        lTvsh.setText("0");
        lKusuri.setText("0");
        try (PreparedStatement pstmt = con.prepareStatement("select kons_id, rec_id, usr, konsumatori, shitje, lloji, statusi, koha_shitjes, anuluar from vfatura " +
                "where kons_id = " + VariablatPublike.revKons.get(kons) +
                " order by rec_id desc")) {
//            pstmt.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
//            pstmt.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));

            ResultSet rs = pstmt.executeQuery();
            ObservableList<Faturat> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Faturat(rs.getInt("rec_id"), rs.getString("usr"), rs.getString("konsumatori"),
                        rs.getBoolean("lloji"), rs.getBoolean("statusi"),
                        VariablatPublike.sdf.format(rs.getDate("koha_shitjes")), VariablatPublike.toMoney(rs.getDouble("shitje")), rs.getBoolean("anuluar")));
            }
            tblFatura.setItems(data);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            merrTotal(kons);
                            merrMes();
                            merrKons();
                            merrShitje();
                        }
                    });
                }
            });
            t.setDaemon(true);
            t.start();

        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrTotal (String kons) {
        String q = "select (case when sum(total) > cash then cash else sum(total) end) as ttl from vshitjet " +
                "where kons_id = " + VariablatPublike.revKons.get(kons) +
                " and anuluar = 0 group by red_id " +
                "order by koha_shitjes desc";
        String q2 = "select (case when sum(total) > cash then cash else sum(total) end) as ttl from vshitjet " +
                "where kons_id = " + VariablatPublike.revKons.get(kons) +
                " and anuluar = 0 group by red_id " +
                "order by koha_shitjes desc";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareStatement(q2)) {

//            ps.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
//            ps.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));
//
//            ps2.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
//            ps2.setDate(2, java.sql.Date.valueOf(dpPrej.getValue()));
//            ps2.setDate(3, java.sql.Date.valueOf(dpDeri.getValue()));
//            ps2.setDate(4, java.sql.Date.valueOf(dpPrej.getValue()));

            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            double last = 0;

            totaliLast = 0;
            while (rs.next()) {
                totalLast += rs.getDouble("ttl");
                totali = totalLast;
            }

            while (rs2.next()) {
                last += rs2.getDouble("ttl");
                totaliLast = last;
            }

            double perc = 0;

            if (last != 0) {
                perc = ((totalLast-last)/last)*100;
            }else if (totalLast > 0) {
                perc = 100;
            }else if (totalLast < 0) {
                perc = -100;
            }

            lCurrTtl.setText(VariablatPublike.toMoney(totalLast));
            if (perc < 0) {
                lLastTtl.setStyle("-fx-text-fill: -fx-second");
            }else {
                lLastTtl.setStyle("-fx-text-fill: -fx-green");
            }
            lLastTtl.setText(perc > 100 ? "999+%" : perc < -100 ? "-999+%" : VariablatPublike.decimal.format(perc) + "%");
        }catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void merrFaturat (){
        totali = 0;
        totaliLast = 0;
        totaliShpenz = 0;
        totaliShpenzLast = 0;
        total = 0;
        totalLast = 0;
        btnAnulo.setDisable(true);
        btnFaturo.setDisable(true);
        tblDhenat.getItems().clear();
        lTotal.setText("0");
        lPagesa.setText("0");
        lTvsh.setText("0");
        lKusuri.setText("0");
        try (PreparedStatement pstmt = con.prepareStatement("select rec_id, usr, konsumatori, shitje, lloji, statusi, koha_shitjes, anuluar from vfatura " +
                "where formatdatetime(koha_shitjes, 'yyyy-MM-dd') >= ? " +
                "and formatdatetime(koha_shitjes, 'yyyy-MM-dd') <= ? order by rec_id desc")) {
            pstmt.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            pstmt.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));

            ResultSet rs = pstmt.executeQuery();
            ObservableList<Faturat> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Faturat(rs.getInt("rec_id"), rs.getString("usr"), rs.getString("konsumatori"), rs.getBoolean("lloji"), rs.getBoolean("statusi"),
                        VariablatPublike.sdf.format(rs.getDate("koha_shitjes")), VariablatPublike.toMoney(rs.getDouble("shitje")), rs.getBoolean("anuluar")));
            }
            tblFatura.setItems(data);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            merrTotal();
                            merrMes();
                            merrKons();
                            merrShitje();
                        }
                    });
                }
            });
            t.setDaemon(true);
            t.start();

        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrTotal () {
        String q = "select (case when cash < sum(total) then cash else sum(total) end) as ttl from vshitjet " +
                "where formatdatetime(koha_shitjes, 'yyyy-MM-dd') >= ? and " +
                "formatdatetime(koha_shitjes, 'yyyy-MM-dd') <= ? and anuluar = 0 " +
                "group by red_id " +
                "order by koha_shitjes desc";
        String q2 = "select (case when cash < sum(total) then cash else sum(total) end) as ttl from vshitjet " +
                "where formatdatetime(koha_shitjes, 'yyyy-MM-dd') < ? and " +
                "koha_shitjes > dateadd('day', -datediff('day',  ?, ?)-1, ?) and anuluar = 0 " +
                "group by red_id " +
                "order by koha_shitjes desc";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareStatement(q2)) {

            ps.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));

            ps2.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(2, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(3, java.sql.Date.valueOf(dpDeri.getValue()));
            ps2.setDate(4, java.sql.Date.valueOf(dpPrej.getValue()));

            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            double last = 0;

            totaliLast = 0;
            while (rs.next()) {
                totalLast += rs.getDouble("ttl");
                totali = totalLast;
            }

            while (rs2.next()) {
                last += rs2.getDouble("ttl");
                totaliLast = last;
            }

            double perc = 0;

            if (last != 0) {
                perc = ((totalLast-last)/last)*100;
            }else if (totalLast > 0) {
                perc = 100;
            }else if (totalLast < 0) {
                perc = -100;
            }

            lCurrTtl.setText(VariablatPublike.toMoney(totalLast));
            if (perc < 0) {
                lLastTtl.setStyle("-fx-text-fill: -fx-second");
            }else {
                lLastTtl.setStyle("-fx-text-fill: -fx-green");
            }
            lLastTtl.setText(perc > 100 ? "999+%" : perc < -100 ? "-999+%" : VariablatPublike.decimal.format(perc) + "%");
        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrMes (){
        String q = "select sum(shuma) as shuma from shpenzimet where formatdatetime(data, 'yyyy-MM-dd') >= ? " +
                "and formatdatetime(data, 'yyyy-MM-dd') <= ?;";
        String q2 = "select sum(shuma) as shuma from shpenzimet where formatdatetime(data, 'yyyy-MM-dd') < ? and " +
                "data > " +
                "dateadd('day', -datediff('day',  ?, ?)-1, ?);";
        try (PreparedStatement ps2 = con.prepareStatement(q2); PreparedStatement ps = con.prepareStatement(q)) {
            ps2.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(2, java.sql.Date.valueOf(dpPrej.getValue()));
            ps2.setDate(3, java.sql.Date.valueOf(dpDeri.getValue()));
            ps2.setDate(4, java.sql.Date.valueOf(dpPrej.getValue()));

            ps.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
            ps.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));

            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            double mes = 0, mesL = 0;
            while (rs.next()) {
                mes = rs.getDouble("shuma");
                totaliShpenz = mes;
            }

            while (rs2.next()) {
                mesL = rs2.getDouble("shuma");
                totaliShpenzLast = mesL;
            }

            double perc = 0;

            if (mesL != 0) perc = ((mes-mesL)/mesL)*100;
            else if (mes > 0) perc = 100;
            else if (mes < 0) perc = -100;

            lPagMesL.setStyle(perc <= 0 ? "-fx-text-fill: -fx-green" : "-fx-text-fill: -fx-second");

            lPagMes.setText(VariablatPublike.toMoney(mes));
            lPagMesL.setText(perc > 100 ? "999%" : perc < -100 ? "-999%" : VariablatPublike.decimal.format(perc) + "%");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void merrKons (){
//        String q = "select count(*) as c from konsumatoret where formatdatetime(data_krijimit, 'yyyy-MM-dd') >= ? and " +
//                "formatdatetime(data_krijimit, 'yyyy-MM-dd') <= ?";
//        String q2 = "select count(*) as c from konsumatoret where formatdatetime(data_krijimit, 'yyyy-MM-dd') < ? and " +
//                "data_krijimit > " +
//                "dateadd('day', -datediff('day',  ?, ?)-1, ?);";
//        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareStatement(q2)) {
//            ps.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
//            ps.setDate(2, java.sql.Date.valueOf(dpDeri.getValue()));
//
//            ps2.setDate(1, java.sql.Date.valueOf(dpPrej.getValue()));
//            ps2.setDate(2, java.sql.Date.valueOf(dpPrej.getValue()));
//            ps2.setDate(3, java.sql.Date.valueOf(dpDeri.getValue()));
//            ps2.setDate(4, java.sql.Date.valueOf(dpPrej.getValue()));
//
//            ResultSet rs = ps.executeQuery();
//            ResultSet rs2 = ps2.executeQuery();

            double kons = 0;
            double konsL = 0;
//            while (rs.next()) {
                kons = totali - totaliShpenz;
//            }

//            while (rs2.next()) {
                konsL = totaliLast - totaliShpenzLast;
//            }

            double perc = 0;

            if (konsL != 0) {
                perc = ((kons - konsL)/konsL)*100;
            }else if (kons > 0) {
                perc = 100;
            }else if (kons < 0) {
                perc = -100;
            }

            if (perc < 0) {
                lKonsL.setStyle("-fx-text-fill: -fx-second");
            }else {
                lKonsL.setStyle("-fx-text-fill: -fx-green");
            }

            lKons.setText(VariablatPublike.toMoney(kons));
            lKonsL.setText(perc > 100 ? "999%" : perc < -100 ? "-999%" : VariablatPublike.decimal.format(perc) + "%");

//        }catch (Exception e) {e.printStackTrace();}
    }

    private void merrShitje(){
        String q = "select count(*) as ttl from rec where formatdatetime(koha_krijimit, 'yyyy-MM-dd') >= ? and " +
                "formatdatetime(koha_krijimit, 'yyyy-MM-dd') <= ?";
        String q2 = "select count(*) as ttl from rec where formatdatetime(koha_krijimit, 'yyyy-MM-dd') < ? and " +
                "koha_krijimit > " +
                "dateadd('day', -datediff('day',  ?, ?)-1, ?)";
        try (PreparedStatement ps = con.prepareStatement(q); PreparedStatement ps2 = con.prepareStatement(q2)) {
            ps.setDate(1, Date.valueOf(dpPrej.getValue()));
            ps.setDate(2, Date.valueOf(dpDeri.getValue()));

            ps2.setDate(1, Date.valueOf(dpPrej.getValue()));
            ps2.setDate(2, Date.valueOf(dpPrej.getValue()));
            ps2.setDate(3, Date.valueOf(dpDeri.getValue()));
            ps2.setDate(4, Date.valueOf(dpPrej.getValue()));

            double shitje = 0, shitjeL = 0;
            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            rs.next(); shitje = rs.getDouble("ttl");
            rs2.next(); shitjeL = rs2.getDouble("ttl");

            double perc = 0;

            if (shitjeL != 0) perc = ((shitje-shitjeL)/shitjeL)*100;
            else if (shitje > 0) perc = 100;
            else if (shitje < 0) perc = -100;

            lShitjeL.setStyle(perc < 0 ? "-fx-text-fill: -fx-second" : "-fx-text-fill: -fx-green");

            lShitje.setText(Math.round(shitje)+"");
            lShitjeL.setText(perc > 999 ? "999+%" : perc < -999 ? "-999+%" : VariablatPublike.decimal.format(perc)+"%");

        }catch (Exception e) { e.printStackTrace(); }
    }

    private void merrTeDhenat(int id, String konsumatori, boolean lloji, boolean anuluar) {
        if (VariablatPublike.rep_del) {
            btnAnulo.setDisable(false);
        }
        btnFaturo.setDisable(false);

        try (PreparedStatement pstmt = con.prepareStatement("select p_id, produkti, qmimi_shitjes, qmimi_shumic, tvsh, prod_zbritje, cash, total, sasia, rec_statusi, red_id, " +
                "kons_emri, adresa, qyteti, anuluar from vshitjet where red_id = ?")) {
            pstmt.setInt(1, id);
            total = 0;
            double tvsh = 0;
            double pag = 0;
            ResultSet rs = pstmt.executeQuery();

            List<Integer> prod_ids = new ArrayList<>();
            List<Integer> prod_count = new ArrayList<>();

            ObservableList<FaturaDhenat> data = FXCollections.observableArrayList();
            double zbritje = 0.0;
            int index = 0;
            while (rs.next()) {

                String[] kons = {rs.getString("kons_emri"), rs.getString("adresa"), rs.getString("qyteti")};
                int rec_id = rs.getInt("red_id");

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JasperReport jr = JasperCompileManager.compileReport(System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/Faktura.jrxml");
                            HashMap<String, Object> params = new HashMap<>();
                            params.put("anuluar", anuluar);
                            params.put("gjuha", VariablatPublike.LANG);
                            params.put("date", null);
                            params.put("pnt", VariablatPublike.pntEmri);
                            params.put("email", VariablatPublike.pntEmail);
                            params.put("adresa", VariablatPublike.ADRESA);
                            params.put("nrFiskal", VariablatPublike.nrFiskal);
                            params.put("foto", System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/2.jpg");
                            params.put("Tel", VariablatPublike.pntTel);
                            params.put("Emri", VariablatPublike.emriShitores);
                            params.put("Faktura", rec_id);
                            params.put("iban", VariablatPublike.IBAN);
                            params.put("banka", VariablatPublike.BANKA);
                            params.put("swift", VariablatPublike.SWIFT);
                            params.put("konto", VariablatPublike.KONTO);
                            params.put("konsumatori", kons[0]);
                            params.put("konsAdresa", kons[1]);
                            params.put("konsQyteti", kons[2]);

                            JasperPrint print = JasperFillManager.fillReport(jr, params, con);
                            String filename = System.getProperty("user.home") + "/store-ms-files/Raportet/PDF/Faktura-" +
                                    new SimpleDateFormat("dd-MM-yy h-m").format(new java.util.Date()) + ".pdf";
                            JasperExportManager.exportReportToPdfFile(print, filename);

                            Desktop.getDesktop().open(new File(filename));

                        } catch (JRException ex) {
                            ex.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                t.setDaemon(true);

                btnFaturo.setOnAction(e -> {
                    t.start();
                });

                if (rs.getBoolean("rec_statusi") && VariablatPublike.rep_edit) {
                    int red = rs.getInt("red_id");
                    btnPagesa.setOnAction(e -> {
                        hapShtoPagesen(red);
                        merrFaturat();
//                        shtoPagesen(red);
                    });
//                    txtPagesa.setOnKeyReleased(e -> {
//                        if (e.getCode() == KeyCode.ENTER)
//                            shtoPagesen(red);
//                    });
                    btnPagesa.setVisible(true);
                } else {
                    btnPagesa.setVisible(false);
                }
                zbritje = rs.getDouble("sasia") * rs.getDouble("qmimi_shitjes") - ((rs.getDouble("sasia") * rs.getDouble("qmimi_shitjes")) * rs.getDouble("prod_zbritje") / 100);

                data.add(new FaturaDhenat(rs.getString("p_id"), rs.getString("produkti"), VariablatPublike.toMoney(rs.getDouble((!lloji ? "qmimi_shitjes" : "qmimi_shumic"))),
                        VariablatPublike.decimal.format(rs.getDouble("sasia")), VariablatPublike.toMoney(zbritje) +
                        " (" + VariablatPublike.decimal.format(rs.getDouble("prod_zbritje")) + "%)"));
                total += zbritje;
                pag = rs.getDouble("cash");
                tvsh = rs.getDouble("tvsh");
                prod_ids.add(rs.getInt("p_id"));
                prod_count.add(rs.getInt("sasia"));
            }

            if (prod_ids != null && VariablatPublike.rep_del) {
//                if (VariablatPublike.uemri.toLowerCase().equals("admin")) {
                    btnAnulo.setDisable(false);

                    if (anuluar) {
                        btnAnulo.setText(rb.getString("btn_aktivizo"));
                        btnAnulo.getStyleClass().removeAll("btnRed");
                        btnAnulo.getStyleClass().add("btnGreen");
                    }
                    else {
                        btnAnulo.setText(rb.getString("btn_anulo"));
                        btnAnulo.getStyleClass().removeAll("btnGreen");
                        btnAnulo.getStyleClass().add("btnRed");
                    }

                    btnAnulo.setOnAction(e -> {
                        if (!anuluar)
                            anuloFakturen(id, prod_ids, prod_count);
                        else
                            aktivizoFakturen(id, prod_ids, prod_count);
                    });
//                }
            } else {
                btnAnulo.setDisable(true);
            }

            lTvsh.setText(VariablatPublike.decimal.format(tvsh) + "%");
//            total -= total*(tvsh/100);
            tblDhenat.setItems(data);
            lTotal.setText(VariablatPublike.toMoney(total));
            lPagesa.setText(VariablatPublike.toMoney(pag));
            lKusuri.setText(VariablatPublike.toMoney(pag-total));

        }catch (Exception e) {e.printStackTrace();}
    }

    private void hapShtoPagesen(int id) {
        Stage stage = new Stage();
        ShtoPagesen shp = new ShtoPagesen();
        shp.setId(id);
        shp.setStage(stage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoPagesen.fxml"), rb);
        loader.setController(shp);
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(parent, 400, 170);
        scene.setFill(Color.TRANSPARENT);
        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ESCAPE)) stage.close();
        });
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();

    }

    private void perfundo(int red_id) {
        try {
            Statement st = con.createStatement();
            st.addBatch("update rec set statusi = 0 where rec_id = " + red_id);
            st.addBatch("update shitjet update cash = ");
            st.close();
//            btnPerfundo.setDisable(true);
//            btnPerfundo.setStyle("-fx-background-color: -fx-green;");
//            btnPerfundo.setText("Ne rregull");
            Notification ntf = new Notification();
            ntf.setMessage(rb.getString("rep_pagesa"));
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void ExportRaport (int rec_id, String konsumatori) {

        try {
            JasperReport jr = JasperCompileManager.compileReport(System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/Faktura.jrxml");
            HashMap<String, Object> params = new HashMap<>();
            params.put("Emri", VariablatPublike.emriShitores);
            params.put("Faktura", rec_id);
            params.put("iban", VariablatPublike.IBAN);
            params.put("banka", VariablatPublike.BANKA);
            params.put("swift", VariablatPublike.SWIFT);
            params.put("konto", VariablatPublike.KONTO);
            params.put("konsumatori", VariablatPublike.konsShitur.get(VariablatPublike.revKons.get(konsumatori))[0]);
            params.put("konsAdresa", VariablatPublike.konsShitur.get(VariablatPublike.revKons.get(konsumatori))[1]);
            params.put("konsQyteti", VariablatPublike.konsShitur.get(VariablatPublike.revKons.get(konsumatori))[2]);

            JasperPrint print = JasperFillManager.fillReport(jr, params, con);
            String filename = System.getProperty("user.home") + "/store-ms-files/Raportet/PDF/Faktura-"+
                    new SimpleDateFormat("dd-MM-yy h-m").format(new java.util.Date())+".pdf";
            JasperExportManager.exportReportToPdfFile(print, filename);

            Desktop.getDesktop().open(new File(filename));

        } catch (JRException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void anuloFakturen(int rec_id, List<Integer> prods, List<Integer> counts) {
        try {
            Notification ntf = new Notification();
            ntf.setMessage(rb.getString("rep_anulo"));
            ntf.setButton(ButtonType.YES_NO);
            ntf.setType(NotificationType.ERROR);
            ntf.showAndWait();

            if (ntf.getDelete()) {
                Statement st = con.createStatement();
                st.addBatch("update rec set anuluar = 1 where rec_id = " + rec_id);

                for (int i = 0; i < prods.size(); i++) {
                    st.addBatch("update produktet set aktual = aktual + " + counts.get(i) + ", sasia = sasia + " + counts.get(i) + " where id = " + prods.get(i));
                }
                st.executeBatch();
                merrFaturat();
            }

        }catch (Exception e) { e.printStackTrace(); }
    }

    private void aktivizoFakturen(int rec_id, List<Integer> prods, List<Integer> counts) {
        try {
            Notification ntf = new Notification();
            ntf.setMessage(rb.getString("rep_anulo"));
            ntf.setButton(ButtonType.YES_NO);
            ntf.setType(NotificationType.ERROR);
            ntf.showAndWait();

            if (ntf.getDelete()) {
                Statement st = con.createStatement();
                st.addBatch("update rec set anuluar = 0 where rec_id = " + rec_id);

                for (int i = 0; i < prods.size(); i++) {
                    st.addBatch("update produktet set aktual = aktual - " + counts.get(i) + ", sasia = sasia - " + counts.get(i) + " where id = " + prods.get(i));
                }
                st.executeBatch();
                merrFaturat();
            }

        }catch (Exception e) { e.printStackTrace(); }
    }

}
