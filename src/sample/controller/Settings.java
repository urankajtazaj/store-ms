package sample.controller;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-21.
 */
public class Settings implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    Notification ntf = new Notification();

    private String path = System.getProperty("user.home") + "/store-ms-files/";

    @FXML private TextField tMujor, txtTvsh;
    @FXML private ComboBox<String> cbTipi, cbValuta;
    @FXML private VBox vbPunet;
    @FXML private Button shtoPune, btnImport;
    @FXML private VBox vbPnt;
    @FXML private RadioButton rbQdoRaport, rbQdoDite, rbQdoMuaj;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH-mm");

    DecimalFormat pct = new DecimalFormat("00");

    private RotateTransition transition;
    private ImageView iv;

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rb = resources;

        cbTipi.getItems().addAll(rb.getString("sett_mujore"), rb.getString("sett_javore"), rb.getString("sett_ditore"));

        Thread dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        getTarget();
                        merrTvsh();
                        getJobs();
                        merrProdKats();
                        getRaportimet();
                    }
                });
            }
        });

        dataThread.setDaemon(true);
        dataThread.start();

        cbValuta.getSelectionModel().select(getIndex());

//        File file = new File(path + "Backup/backup.sql");
//        if (!file.exists()) btnImport.setDisable(true);

    }

    private int getIndex() {
        char c = VariablatPublike.valuta;
        if (c == 'L') {
            return 1;
        }else if (c == 'D') {
            return 2;
        }else if (c == 'M') {
            return 3;
        }else {
            return 0;
        }
    }

    @FXML
    private void shtoPune(){
        addDeps("", 0);
    }

    private void rregulloPunen(TextField tf, int id, HBox hb) {
        try {
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoPune.fxml"), rb);
            ShtoPune sp = new ShtoPune();
            sp.setTf(tf);
            sp.setId(id);
            sp.setHBox(hb);
            sp.setVbox(vbPunet);
            sp.setStage(stage);
            loader.setController(sp);
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 450, 630);
            scene.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ESCAPE)) {
                    stage.close();
                }
            });
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
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

    private void getRaportimet(){
        try {

            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("select * from raportimet limit 1");

            rs.next();

            int id = rs.getInt("lloji");

            if (id == 0) rbQdoRaport.setSelected(true);
            else if (id == 1) rbQdoDite.setSelected(true);
            else if (id == 2) rbQdoMuaj.setSelected(true);

            rs.close();
            s.close();

        }catch (Exception e) { e.printStackTrace(); }
    }

    private void addDeps (String emri, int index) {
        HBox hb = new HBox(5);
        TextField tfEmri = new TextField(emri);
        tfEmri.setEditable(false);
        Button btnRregullo = new Button(rb.getString("rregullo"));
        btnRregullo.setId(index+"");

        btnRregullo.setOnAction(e -> {
            rregulloPunen(tfEmri, index, hb);
        });

        hb.getChildren().addAll(tfEmri, btnRregullo);
        vbPunet.getChildren().add(0, hb);
    }

    private void hapNgjyren (Button btn, int id, String bg){
        try {
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ZgjedhNgjyren.fxml"), rb);
            ZgjedhNgjyren zgj = new ZgjedhNgjyren();
            zgj.setId(id);
            zgj.setStage(stage);
            zgj.setButton(btn);
            zgj.setBg(bg);
            loader.setController(zgj);
            Parent parent = loader.load();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(parent, 280, 200);
            stage.setScene(scene);
            scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
            stage.setResizable(false);
            stage.show();

        }catch (Exception e ) { e.printStackTrace(); }
    }

    private void merrProdKats(){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select * from kat_prod")) {
            while (rs.next()) {
                addKats(rs.getInt("id"), rs.getString("kategoria"), rs.getString("bg"));
            }
        }catch (Exception e){ e.printStackTrace(); }
    }

    @FXML
    private void shtoProdKat(){
        addKats(0, "", "rgba(0,0,0,0.085)");
    }

    @FXML
    private void ruajProdKat() throws Exception {
        Statement s = con.createStatement();
        for (Node n : vbPnt.getChildren()) {
            if (n instanceof HBox) {
                String tf = ((TextField)((HBox)n).getChildren().get(0)).getText();
                int id = Integer.parseInt(((HBox)n).getChildren().get(1).getId().split("/")[0]);
                String bg = ((HBox)n).getChildren().get(1).getId().split("/")[1];
                if (id == 0)
                    s.addBatch("insert into kat_prod values (null, '"+tf+"', '" + bg + "', '#000')");
                else {
                    s.addBatch("update kat_prod set kategoria = '" + tf + "', bg = '" + bg + "' where id = " + id);
                    VariablatPublike.mProdKat.put(id, tf);
                    VariablatPublike.revProdKat.put(tf, id);
                    VariablatPublike.prodKat.add(tf);
                }
            }
        }
        s.executeBatch();
    }

    private void addKats(int id, String emri, String bg) {
        HBox hb = new HBox(5);
        TextField tf = new TextField(emri);
        tf.setMaxWidth(160);
        Button btn = new Button();
        Button btnDel = new Button("X");
        btn.setStyle("-fx-min-width: 20; -fx-max-width: 20; -fx-min-height: 20; -fx-max-height: 20; -fx-border-radius: 50; -fx-background-radius: 50; -fx-border-width: 1; " +
                "-fx-border-color: rgba(255,255,255,.2); -fx-background-color: "+bg);
        btnDel.getStyleClass().addAll("btnRed", "btnRound");
        btn.setId(id + "/" + bg);

        btn.setOnAction(e -> {
            hapNgjyren((Button) e.getSource(), id, bg);
        });

        btnDel.setOnAction(e -> {
            ntf.setMessage(rb.getString("set_del_kat_q"));
            ntf.setType(NotificationType.ERROR);
            ntf.setDuration(0);
            ntf.setButton(ButtonType.YES_NO);
            ntf.showAndWait();
            if (id > 0 && ntf.getDelete()) {
                vbPnt.getChildren().remove(hb);
                fshiKat(id);
            }
        });

        hb.setAlignment(Pos.CENTER_LEFT);
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

            VariablatPublike.revProdKat.remove(VariablatPublike.mProdKat.get(id));
            VariablatPublike.prodKat.remove(VariablatPublike.mProdKat.get(id));
            VariablatPublike.mProdKat.remove(id);

            ntf.setMessage(rb.getString("set_del_kat_sukses"));
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
            }else {
                throw new Exception();
            }
        }catch (Exception e) {e.printStackTrace();}
    }

    private void ruajRaportet() {
        try {
            Statement st = con.createStatement();
            int id = rbQdoRaport.isSelected() ? 0 : (rbQdoDite.isSelected() ? 1 : 2);
            st.executeUpdate("update raportimet set last_report = current_date(), lloji = " + id);
            st.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void merrTvsh(){
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("select * from tvsh")) {
            while (rs.next()) {
                txtTvsh.setText(VariablatPublike.decimal.format(rs.getDouble("tvsh")));
                VariablatPublike.tvsh = Double.parseDouble("1." + (rs.getInt("tvsh")));
            }
        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void ruajTvsh() throws NumberFormatException, SQLException {
        PreparedStatement ps = con.prepareStatement("update tvsh set tvsh = ?");
        ps.setDouble(1, Double.parseDouble(txtTvsh.getText()));
        ps.execute();

        if (Integer.parseInt(txtTvsh.getText()) > 0) {
            VariablatPublike.tvsh = (Double.parseDouble("1." + (Integer.parseInt(txtTvsh.getText()))));
            System.out.println(VariablatPublike.tvsh);
        } else {
            VariablatPublike.tvsh = 0;
        }
        ps.close();
    }

    @FXML
    private void pastroDb() {
        try {
            ntf.setMessage(rb.getString("set_del_data_q"));
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.YES_NO);
            ntf.showAndWait();

            if (ntf.getDelete()) {
                String query =
                        "delete from mundesite;" +
                        "delete from shpenzimet;" +
                        "update raportimet set lloji = 0, last_report = current_date();" +
                        "delete from punetoret;" +
                        "update valuta set valuta = 'EUR';" +
                        "delete from shteti where id > 2;" +
                        "alter table shteti alter column id restart with 3;" +
                        "delete from qytetet where id > 102;" +
                        "alter table qytetet alter id restart with 103;" +
                        "delete from konsumatoret;" +
                        "delete from produktet;" +
                        "delete from departamenti;" +
                        "delete from kat_prod;" +
                        "delete from priv;" +
                        "delete from perdoruesi;" +
                        "delete from shitjet;" +
                        "delete from rec;" +
                        "delete from pushimet;" +
                        "alter table mundesite alter column id restart with 1;" +
                        "alter table shpenzimet alter column id restart with 1;" +
                        "alter table punetoret alter column id restart with 1;" +
                        "alter table konsumatoret alter column id restart with 1;" +
                        "alter table produktet alter column id restart with 1;" +
                        "alter table rec alter column rec_id restart with 1;" +
                        "alter table shitjet alter column id restart with 1;" +
                        "alter table departamenti alter column id restart with 1;" +
                        "alter table perdoruesi alter column id restart with 1;" +
                        "alter table kat_prod alter column id restart with 1;" +
                        "update tvsh set tvsh.tvsh = 0;" +
                        "update target set qmimi = 1000;" +
                        "insert into punetoret values (null, 1, 'Admin', '', 0, current_timestamp(), 0, current_timestamp(), current_timestamp(), 1, current_timestamp(), '','','','','','');" +
                        "insert into perdoruesi values (null, 1, 'admin', 123, 1);" +
                        "insert into konsumatoret values (null, 'Publik', '','','',1, current_timestamp(), current_timestamp(), '','Kosove', '');" +
                        "insert into departamenti values (null, 'Administrator');" +
                        "insert into priv values (1, 1, 1,1,1,1,1,1);" +
                        "insert into mundesite values (null, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);";

                String[] cmds = query.split(";");

                Statement stmt = con.createStatement();
                for (String s : cmds) {
                    stmt.addBatch(s);
                }

                stmt.executeBatch();
                ntf.setMessage(rb.getString("set_del_data_sukses"));
                ntf.setButton(ButtonType.OK);
                ntf.setType(NotificationType.SUCCESS);
                ntf.showAndWait();
//                if (ntf.getDelete()) {
                    Server.stopServer();
                    Platform.exit();
//                }
            }

        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void importDb() {

        FileChooser file = new FileChooser();
        file.setInitialDirectory(new File(path + "/Backup"));

        file.setTitle("Choose file");
        file.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));

        File filename = file.showOpenDialog(null);

        if (filename != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try (Statement st = con.createStatement()) {
                        iv.setImage(VariablatPublike.spinning);
                        transition.play();
                        st.addBatch("drop all objects");
                        st.addBatch("runscript from '" + filename.getAbsolutePath() + "'");
                        st.executeBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            VariablatPublike.stopSpinning(transition, iv);
                            if (filename != null) {
                                ntf.setMessage(rb.getString("set_import_sukses"));
                                ntf.setButton(ButtonType.OK);
                                ntf.setType(NotificationType.SUCCESS);
                                ntf.showAndWait();
                            }
                            Server.stopServer();
                            Platform.exit();
                        }
                    });
                }
            }).start();
        }
    }

    @FXML
    private void exportDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                iv.setImage(VariablatPublike.spinning);
                transition.play();
                exportDataFunc();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        VariablatPublike.stopSpinning(transition, iv);
                        ntf.setMessage(rb.getString("set_export_sukses"));
                        ntf.setButton(ButtonType.NO_BUTTON);
                        ntf.setType(NotificationType.SUCCESS);
                        ntf.show();
                    }
                });
            }
        }).start();
    }

    private void exportDataFunc() {
        File file = new File(System.getProperty("user.home") + "/store-ms-files/Backup/backup_" + sdf.format(new Date()) + ".sql");
        try (Statement st = con.createStatement()) {

            st.execute("script to '" + file.getAbsolutePath() + "' table produktet,punetoret,perdoruesi,konsumatoret," +
                    "shitjet,rec,kat_prod,departamenti,priv,pushimet,tvsh,target,shteti,raportimet,qytetet,valuta,shpenzimet,mundesite");

            String view =
                    "create table if not exists shpenzimet (id identity, shuma decimal(10,2), arsyeja varchar(50), data date, personi varchar(50));\n" +
                    "alter table shitjet alter column koha_shitjes date not null;\n" +
                    "alter table shitjet add column if not exists qmimi decimal(10,2) default 0;\n" +
                    "alter table produktet add column if not exists nr_njesia decimal(10,2) default 1;\n" +
                    "alter table rec add column if not exists lloji int(1) default 0;\n" +
                    "alter table rec add column if not exists statusi int(1) default 0;\n" +
                    "alter table rec add column if not exists anuluar int(1) default 0;\n" +
//                    "alter table shitjet add column if not exists anuluar int(1) default 0;\n" +
                    "create table IF NOT EXISTS raportimet (lloji int(1) default 0, last_report date default current_date());\n" +
                    "alter table produktet add column if not exists hyrje_sot int default 0;\n" +
                    "alter table produktet add column if not exists hyrje_date date default current_date();\n" +
                    "alter table produktet add column if not exists qmimi_shumic decimal(10,2) default 0;\n" +
                    "alter table konsumatoret add column if not exists nr_fiskal varchar(20) default '';\n" +
                    "CREATE FORCE VIEW PUBLIC.HYRAT_JAVA(FITIMI) AS SELECT SUM(shitjet.qmimi) AS FITIMI FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.SHITJET ON 1=1 WHERE (CAST(SHITJET.KOHA_SHITJES AS DATE) >= DATEADD('week', -1, CURRENT_DATE())) AND ((PRODUKTET.ID = SHITJET.PROD_ID) AND (CAST(SHITJET.KOHA_SHITJES AS DATE) <= CURRENT_DATE()));\n" +
                    "CREATE FORCE VIEW PUBLIC.HYRAT_SOT(FITIMI) AS SELECT SUM(shitjet.qmimi) AS FITIMI FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.SHITJET ON 1=1 WHERE (CAST(SHITJET.KOHA_SHITJES AS DATE) >= DATEADD('day', 0, CURRENT_DATE())) AND ((PRODUKTET.ID = SHITJET.PROD_ID) AND (CAST(SHITJET.KOHA_SHITJES AS DATE) <= CURRENT_DATE()));\n" +
                    "CREATE FORCE VIEW PUBLIC.HYRAT_MUAJ(FITIMI) AS SELECT SUM(shitjet.qmimi) AS FITIMI FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.SHITJET ON 1=1 WHERE (CAST(SHITJET.KOHA_SHITJES AS DATE) >= DATEADD('month', -1, CURRENT_DATE())) AND ((PRODUKTET.ID = SHITJET.PROD_ID) AND (CAST(SHITJET.KOHA_SHITJES AS DATE) <= CURRENT_DATE()));\n" +
                    "CREATE FORCE VIEW PUBLIC.MERREMRINPNT(ID, PNT_ID, EMRI, USR, PW) AS SELECT PERDORUESI.ID, PERDORUESI.PNT_ID, ((PUNETORET.EMRI || ' ') || PUNETORET.MBIEMRI) AS EMRI, PERDORUESI.USR, PERDORUESI.PW FROM PUBLIC.PERDORUESI INNER JOIN PUBLIC.PUNETORET ON 1=1 WHERE PUNETORET.ID = PERDORUESI.PNT_ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.MERRPUNETORET(ID, EMRI, DITELINDJA, DEPARTAMENTI, DATA_PUNESIMIT, PAGA, TELEFONI, ADRESA, QYTETI, SHTETI, STATUSI, FOTO, EMAIL, GJINIA, HYRAT) AS SELECT PUNETORET.ID, ((PUNETORET.EMRI || ' ') || PUNETORET.MBIEMRI) AS EMRI, PUNETORET.DITELINDJA, DEPARTAMENTI.DEPARTAMENTI, PUNETORET.DATA_PUNESIMIT, PUNETORET.PAGA, PUNETORET.TELEFONI, PUNETORET.ADRESA, PUNETORET.QYTETI, PUNETORET.SHTETI, PUNETORET.STATUSI, PUNETORET.FOTO, PUNETORET.EMAIL, PUNETORET.GJINIA, (SELECT SUM(VSHITJET.TOTAL) FROM PUBLIC.VSHITJET WHERE VSHITJET.PNT_ID = PUNETORET.ID) AS HYRAT FROM PUBLIC.PUNETORET INNER JOIN PUBLIC.DEPARTAMENTI ON 1=1 WHERE PUNETORET.DEP_ID = DEPARTAMENTI.ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.PNT(NR, A, P) AS SELECT COUNT(PUNETORET.ID) AS NR, (SELECT COUNT(ID) FROM PUBLIC.PUNETORET WHERE STATUSI = 1) AS A, (SELECT COUNT(ID) FROM PUBLIC.PUNETORET WHERE STATUSI = 0) AS P FROM PUBLIC.PUNETORET;\n" +
                    "CREATE FORCE VIEW PUBLIC.PROD_DETAIL(ID, BARCODE, EMRI, SASIA, QMIMI_SHITJES, QMIMI_SHUMIC) AS SELECT PRODUKTET.ID, PRODUKTET.BARCODE, PRODUKTET.EMRI, PRODUKTET.SASIA AS SASIA, PRODUKTET.QMIMI_SHITJES, PRODUKTET.QMIMI_SHUMIC FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.KAT_PROD ON 1=1 WHERE PRODUKTET.KATEGORIA_ID = KAT_PROD.ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.TE_HYRAT(MUAJI, JAVA, SOT) AS SELECT (SELECT SUM(VSHITJET.TOTAL) FROM PUBLIC.VSHITJET WHERE DATEDIFF('month', CAST(VSHITJET.KOHA_SHITJES AS DATE), CURRENT_DATE()) = 0) AS MUAJI, (SELECT SUM(VSHITJET.TOTAL) FROM PUBLIC.VSHITJET WHERE DATEDIFF('week', CAST(VSHITJET.KOHA_SHITJES AS DATE), CURRENT_DATE()) = 0) AS JAVA, (SELECT SUM(VSHITJET.TOTAL) FROM PUBLIC.VSHITJET WHERE DATEDIFF('day', CAST(VSHITJET.KOHA_SHITJES AS DATE), CURRENT_DATE()) = 0) AS SOT FROM PUBLIC.SHITJET INNER JOIN PUBLIC.PRODUKTET ON 1=1 WHERE SHITJET.PROD_ID = PRODUKTET.ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.TOPKATEGORIA(SASIA, KATEGORIA) AS SELECT SUM(VSHITJET.SASIA) AS SASIA, VSHITJET.KATEGORIA FROM PUBLIC.VSHITJET GROUP BY KATEGORIA;\n" +
                    "CREATE FORCE VIEW PUBLIC.TOPPRODUKTET(SASIA, PRODUKTI) AS SELECT SUM(VSHITJET.SASIA) AS SASIA, VSHITJET.PRODUKTI FROM PUBLIC.VSHITJET GROUP BY PRODUKTI;\n" +
                    "CREATE FORCE VIEW PUBLIC.VFATURA(KONS_ID, REC_ID, USR, KOHA_SHITJES, SHITJE, CASH, KONSUMATORI, LLOJI, STATUSI, KOHA, ANULUAR) AS SELECT KONSUMATORET.ID, REC.REC_ID, PUNETORET.EMRI AS USR, SHITJET.KOHA_SHITJES, SUM(SHITJET.QMIMI * SHITJET.SASIA) AS SHITJE, SHITJET.CASH, KONSUMATORET.EMRI AS KONSUMATORI, REC.LLOJI, REC.STATUSI AS STATUSI, REC.KOHA_KRIJIMIT AS KOHA, REC.ANULUAR FROM REC, PUNETORET, SHITJET, KONSUMATORET WHERE (SHITJET.KONS_ID = KONSUMATORET.ID) AND ((SHITJET.REC_ID = REC.REC_ID) AND (PUNETORET.ID = SHITJET.PNT_ID)) GROUP BY REC.REC_ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.VMES(SHUMA) AS SELECT SUM(TOTALNETO) AS SHUMA FROM PUBLIC.VSHITJET GROUP BY RED_ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.VPROD(BARCODE, ID, EMRI, QMIMI_SHITJES, BG, FG, NJESIA, KATEGORIA_ID, SASIA, ZBRITJE, QMIMI_SHITJES_LEK, FOTO, QMIMI_SHUMIC) AS SELECT PRODUKTET.BARCODE, PRODUKTET.ID, PRODUKTET.EMRI, PRODUKTET.QMIMI_SHITJES, KAT_PROD.BG, KAT_PROD.FG, PRODUKTET.NJESIA, KAT_PROD.ID AS KATEGORIA_ID, PRODUKTET.SASIA, PRODUKTET.ZBRITJE, PRODUKTET.QMIMI_SHITJES_LEK, PRODUKTET.FOTO, PRODUKTET.QMIMI_SHUMIC FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.KAT_PROD ON 1=1 WHERE (PRODUKTET.SASIA > 0) AND (PRODUKTET.KATEGORIA_ID = KAT_PROD.ID);\n" +
                    "CREATE FORCE VIEW PUBLIC.VREC(TOTAL, REC_ID, KOHA_KRIJIMIT) AS SELECT SUM(VSHITJET.TOTAL) AS TOTAL, REC.REC_ID, REC.KOHA_KRIJIMIT FROM PUBLIC.VSHITJET INNER JOIN PUBLIC.REC ON 1=1 WHERE (VSHITJET.REC_STATUSI = 0) AND (VSHITJET.RED_ID = REC.REC_ID) GROUP BY VSHITJET.RED_ID;\n" +
                    "CREATE or replace VIEW PUBLIC.VSHITJET(KONS_ID, PRODUKTI, KATEGORIA, QMIMI_SHITJES, FITIMI, KOHA_SHITJES, CASH, KUSURI, SASIA, TOTALNETO, TOTAL, PUNETORI, RED_ID, P_ID, TVSH, PNT_ID, PROD_ZBRITJE, SHITJA_LLOJI, QMIMI_SHUMIC, BARCODE, PNTNAME, NR_FISKAL, EMAIL, REC_STATUSI, KONS_EMRI, ADRESA, QYTETI, ANULUAR) AS SELECT KONSUMATORET.ID AS KONS_ID, PRODUKTET.EMRI AS PRODUKTI, KAT_PROD.KATEGORIA, SHITJET.QMIMI AS QMIMI_SHITJES, (SHITJET.QMIMI - PRODUKTET.QMIMI_STD) AS FITIMI, SHITJET.KOHA_SHITJES, SHITJET.CASH, (SHITJET.CASH - (SHITJET.QMIMI * SHITJET.SASIA)) AS KUSURI, SHITJET.SASIA AS SASIA, (SHITJET.SASIA * (SHITJET.QMIMI - PRODUKTET.QMIMI_STD)) AS TOTALNETO, (SHITJET.SASIA * ((SHITJET.QMIMI - ((SHITJET.QMIMI * PRODUKTET.ZBRITJE) / 100)))) AS TOTAL, PERDORUESI.USR AS PUNETORI, SHITJET.REC_ID AS RED_ID, PRODUKTET.ID AS P_ID, REC.TVSH, PERDORUESI.PNT_ID, PRODUKTET.ZBRITJE AS PROD_ZBRITJE, REC.LLOJI AS SHITJA_LLOJI, PRODUKTET.QMIMI_SHUMIC, PRODUKTET.BARCODE, ((PUNETORET.EMRI || ' ') || PUNETORET.MBIEMRI) AS PNTNAME, KONSUMATORET.NR_FISKAL, PUNETORET.EMAIL, REC.STATUSI AS REC_STATUSI, KONSUMATORET.EMRI AS KONS_EMRI, KONSUMATORET.ADRESA, KONSUMATORET.QYTETI, REC.ANULUAR FROM PUBLIC.SHITJET INNER JOIN PUBLIC.KONSUMATORET ON 1=1 INNER JOIN PUBLIC.PRODUKTET ON 1=1 INNER JOIN PUBLIC.KAT_PROD ON 1=1 INNER JOIN PUBLIC.PERDORUESI ON 1=1 INNER JOIN PUBLIC.REC ON 1=1 INNER JOIN PUBLIC.PUNETORET ON 1=1 WHERE (PUNETORET.ID = PERDORUESI.ID) AND ((PRODUKTET.KATEGORIA_ID = KAT_PROD.ID) AND ((SHITJET.PROD_ID = PRODUKTET.ID) AND ((KONSUMATORET.ID = SHITJET.KONS_ID) AND ((SHITJET.REC_ID = REC.REC_ID) AND (PERDORUESI.ID = SHITJET.USR)))));\n" +
                    "CREATE FORCE VIEW PUBLIC.RAPORTI_SHITJES(ID, EMRI, STOKU, SASIA_SHITUR, MBETUR, QMIMI_NJESOR, SHUMA, HYRJE_SOT) AS SELECT PRODUKTET.ID, PRODUKTET.EMRI, PRODUKTET.SASIA AS STOKU, (SASIA - AKTUAL) AS SASIA_SHITUR, PRODUKTET.AKTUAL AS MBETUR, PRODUKTET.QMIMI_SHITJES AS QMIMI_NJESOR, ((SASIA - AKTUAL) * PRODUKTET.QMIMI_SHITJES) AS SHUMA, PRODUKTET.HYRJE_SOT FROM PUBLIC.PRODUKTET GROUP BY PRODUKTET.ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.SHITJET_SHUMIC AS ((PRODUKTET.QMIMI_SHUMIC-PRODUKTET.QMIMI_STD) * SHITJET.SASIA - (((PRODUKTET.QMIMI_SHUMIC-PRODUKTET.QMIMI_STD) * SHITJET.SASIA)*REC.TVSH/100)) AS TOTALNETO FROM PRODUKTET, SHITJET, REC WHERE PRODUKTET.ID = SHITJET.PROD_ID AND SHITJET.REC_ID = REC.REC_ID;";

            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);) {
                bw.write(view);
            }catch (Exception ex) {ex.printStackTrace();}
        }catch (Exception e) { e.printStackTrace(); }
    }

    public void setTransition(RotateTransition transition) {
        this.transition = transition;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }

    @FXML
    private void oneClickSaveAll() {
        try {
            ruajValuten();
            ruajProdKat();
            ruajTarget();
            ruajTvsh();
            ruajRaportet();
            ntf.setType(NotificationType.SUCCESS);
            ntf.setMessage(rb.getString("set_save_all_sukses"));
            ntf.show();
        } catch (NumberFormatException nfe) {
            ntf.setType(NotificationType.ERROR);
            ntf.setMessage(rb.getString("set_error_nr_format"));
            ntf.show();
        } catch (Exception ex) {
            ntf.setType(NotificationType.ERROR);
            ntf.setMessage(rb.getString("set_error_unknown"));
            ntf.show();
        }
    }

    @FXML
    private void ruajValuten() throws Exception {
        Statement stmt = con.createStatement();
        int index = cbValuta.getSelectionModel().getSelectedIndex();
        VariablatPublike.valuta = (index == 0 ? '€' : index == 1 ? 'L' : index == 2 ? 'D' : 'M');
        stmt.executeUpdate("update valuta set valuta = '" + cbValuta.getSelectionModel().getSelectedItem() + "'");
    }
}
