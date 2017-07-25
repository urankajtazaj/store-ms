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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

    private String path = System.getProperty("user.home") + "/store-ms-files/";

    @FXML private TextField tMujor, txtTvsh;
    @FXML private ComboBox<String> cbTipi, cbValuta;
    @FXML private VBox vbPunet;
    @FXML private Button shtoPune, btnImport;
    @FXML private VBox vbPnt;

    private RotateTransition transition;
    private ImageView iv;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getTarget();
        merrTvsh();
        getJobs();
        merrProdKats();

        cbValuta.getSelectionModel().select(VariablatPublike.valuta);

        File file = new File(path + "Backup/backup.sql");
        if (!file.exists()) btnImport.setDisable(true);

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
                tMujor.setText(rs.getString(VariablatPublike.valuta.equals("EURO") ? "qmimi" : "qmimi_lek"));
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

    @FXML
    private void pastroDb() {
        try {
            ntf.setMessage("Te gjitha te dhenat do te fshihen duke perfshire punetoret, konsumatoret shitjet etj. Deshironi te vazhdoni?");
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.YES_NO);
            ntf.showAndWait();

            if (ntf.getDelete()) {
                String query =
                        "delete from punetoret;" +
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
                        "alter table punetoret alter column id restart with 1;" +
                        "alter table konsumatoret alter column id restart with 1;" +
                        "alter table produktet alter column id restart with 1;" +
                        "alter table rec alter column rec_id restart with 1;" +
                        "alter table shitjet alter column id restart with 1;" +
                        "alter table departamenti alter column id restart with 1;" +
                        "alter table perdoruesi alter column id restart with 1;" +
                        "update tvsh set tvsh.tvsh = 0;" +
                        "update target set qmimi = 1000;" +
                        "insert into punetoret values (null, 1, 'Admin', '', 0, current_timestamp(), 0, current_timestamp(), current_timestamp(), 1, current_timestamp(), '','','','','','');" +
                        "insert into perdoruesi values (null, 1, 'admin', 123, 1);" +
                        "insert into konsumatoret values (null, 'Publik', '','','',1, current_timestamp(), current_timestamp(), '','Kosove');" +
                        "insert into departamenti values (null, 'Menagjer');" +
                        "insert into priv values (1, 1, 1,1,1,1,1,1)";

                String[] cmds = query.split(";");

                Statement stmt = con.createStatement();
                for (String s : cmds) {
                    stmt.addBatch(s);
                }

                stmt.executeBatch();
                ntf.setMessage("Te gjitha te dhenat u fshine. Programi do te mbyllet.");
                ntf.setButton(ButtonType.OK);
                ntf.setType(NotificationType.SUCCESS);
                ntf.showAndWait();
                if (ntf.getDelete()) {
                    Server.stopServer();
                    Platform.exit();
                }
            }

        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void importDb() {
        ntf.setMessage("Te gjitha te dhenat qe ndodhet tani do te zevendesohen me te dhenat tjera. Vazhdoni?");
        ntf.setButton(ButtonType.YES_NO);
        ntf.setType(NotificationType.ERROR);
        ntf.showAndWait();

        if (ntf.getDelete()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try (Statement st = con.createStatement()) {
                        iv.setImage(VariablatPublike.spinning);
                        transition.play();
                        st.addBatch("drop all objects");
                        st.addBatch("runscript from '" + path + "Backup/backup.sql'");
                        st.executeBatch();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            VariablatPublike.stopSpinning(transition, iv);
                            ntf.setMessage("Te dhenat u shtuan, programi do te mbyllet.");
                            ntf.setButton(ButtonType.OK);
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.showAndWait();
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
                        ntf.setMessage("Te dhenat u eksportuan me sukses.");
                        ntf.setButton(ButtonType.NO_BUTTON);
                        ntf.setType(NotificationType.SUCCESS);
                        ntf.show();
                    }
                });
            }
        }).start();
    }

    private void exportDataFunc() {
        File file = new File(System.getProperty("user.home") + "/store-ms-files/Backup/backup.sql");
        try (Statement st = con.createStatement()) {

            st.execute("script to '" + file.getAbsolutePath() + "' table produktet,punetoret,perdoruesi,konsumatoret," +
                    "shitjet,rec,kat_prod,departamenti,priv,pushimet,tvsh,target,shteti,qytetet");

            String view =
                    "CREATE FORCE VIEW PUBLIC.HYRAT_JAVA(FITIMI) AS SELECT SUM((PRODUKTET.QMIMI_SHITJES - PRODUKTET.QMIMI_STD) * SHITJET.SASIA) AS FITIMI FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.SHITJET ON 1=1 WHERE (PRODUKTET.ID = SHITJET.PROD_ID) AND (WEEK(SHITJET.KOHA_SHITJES) = WEEK(CURRENT_DATE()));\n" +
                    "CREATE FORCE VIEW PUBLIC.HYRAT_SOT(FITIMI) AS SELECT SUM((PRODUKTET.QMIMI_SHITJES - PRODUKTET.QMIMI_STD) * SHITJET.SASIA) AS FITIMI FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.SHITJET ON 1=1 WHERE (PRODUKTET.ID = SHITJET.PROD_ID) AND (CAST(SHITJET.KOHA_SHITJES AS DATE) = CURRENT_DATE());\n" +
                    "CREATE FORCE VIEW PUBLIC.HYRAT_MUAJ(FITIMI) AS SELECT SUM((PRODUKTET.QMIMI_SHITJES - PRODUKTET.QMIMI_STD) * SHITJET.SASIA) AS FITIMI FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.SHITJET ON 1=1 WHERE (PRODUKTET.ID = SHITJET.PROD_ID) AND (MONTH(SHITJET.KOHA_SHITJES) = MONTH(CURRENT_DATE()));\n" +
                    "CREATE FORCE VIEW PUBLIC.MERREMRINPNT(ID, PNT_ID, EMRI, USR, PW) AS SELECT PERDORUESI.ID, PERDORUESI.PNT_ID, ((PUNETORET.EMRI || ' ') || PUNETORET.MBIEMRI) AS EMRI, PERDORUESI.USR, PERDORUESI.PW FROM PUBLIC.PERDORUESI INNER JOIN PUBLIC.PUNETORET ON 1=1 WHERE PUNETORET.ID = PERDORUESI.PNT_ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.MERRPUNETORET(ID, EMRI, DITELINDJA, DEPARTAMENTI, DATA_PUNESIMIT, PAGA, TELEFONI, ADRESA, QYTETI, SHTETI, STATUSI, FOTO, EMAIL, GJINIA, HYRAT) AS SELECT PUNETORET.ID, ((PUNETORET.EMRI || ' ') || PUNETORET.MBIEMRI) AS EMRI, PUNETORET.DITELINDJA, DEPARTAMENTI.DEPARTAMENTI, PUNETORET.DATA_PUNESIMIT, PUNETORET.PAGA, PUNETORET.TELEFONI, PUNETORET.ADRESA, PUNETORET.QYTETI, PUNETORET.SHTETI, PUNETORET.STATUSI, PUNETORET.FOTO, PUNETORET.EMAIL, PUNETORET.GJINIA, (SELECT SUM(VSHITJET.TOTALNETO) FROM PUBLIC.VSHITJET WHERE VSHITJET.PNT_ID = PUNETORET.ID) AS HYRAT FROM PUBLIC.PUNETORET INNER JOIN PUBLIC.DEPARTAMENTI ON 1=1 WHERE PUNETORET.DEP_ID = DEPARTAMENTI.ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.PNT(NR, A, P) AS SELECT COUNT(PUNETORET.ID) AS NR, (SELECT COUNT(ID) FROM PUBLIC.PUNETORET WHERE STATUSI = 1) AS A, (SELECT COUNT(ID) FROM PUBLIC.PUNETORET WHERE STATUSI = 0) AS P FROM PUBLIC.PUNETORET;\n" +
                    "CREATE FORCE VIEW PUBLIC.PROD_DETAIL(ID, BARCODE, EMRI, SASIA, NJESIA, ZBRITJE, KATEGORIA, QMIMI_SHITJES) AS SELECT PRODUKTET.ID, PRODUKTET.BARCODE, PRODUKTET.EMRI, PRODUKTET.SASIA, PRODUKTET.NJESIA, PRODUKTET.ZBRITJE, KAT_PROD.KATEGORIA, PRODUKTET.QMIMI_SHITJES FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.KAT_PROD ON 1=1 WHERE PRODUKTET.KATEGORIA_ID = KAT_PROD.ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.TE_HYRAT(MUAJ, JAVA, SOT) AS SELECT HYRAT_MUAJ.FITIMI AS MUAJ, HYRAT_JAVA.FITIMI AS JAVA, HYRAT_SOT.FITIMI AS SOT FROM PUBLIC.HYRAT_MUAJ INNER JOIN PUBLIC.HYRAT_JAVA ON 1=1 INNER JOIN PUBLIC.HYRAT_SOT ON 1=1 WHERE TRUE;\n" +
                    "CREATE FORCE VIEW PUBLIC.TOPKATEGORIA(SASIA, KATEGORIA) AS SELECT SUM(VSHITJET.SASIA) AS SASIA, VSHITJET.KATEGORIA FROM PUBLIC.VSHITJET GROUP BY KATEGORIA;\n" +
                    "CREATE FORCE VIEW PUBLIC.TOPPRODUKTET(SASIA, PRODUKTI) AS SELECT SUM(VSHITJET.SASIA) AS SASIA, VSHITJET.PRODUKTI FROM PUBLIC.VSHITJET GROUP BY PRODUKTI;\n" +
                    "CREATE FORCE VIEW PUBLIC.VFATURA(REC_ID, USR, KOHA, CASH, KONSUMATORI) AS SELECT REC.REC_ID, PERDORUESI.USR, PARSEDATETIME(SHITJET.KOHA_SHITJES, 'yyyy-MM-dd H:m:ss') AS KOHA, SHITJET.CASH, KONSUMATORET.EMRI AS KONSUMATORI FROM PUBLIC.REC INNER JOIN PUBLIC.PRODUKTET ON 1=1 INNER JOIN PUBLIC.SHITJET ON 1=1 INNER JOIN PUBLIC.KONSUMATORET ON 1=1 INNER JOIN PUBLIC.PERDORUESI ON 1=1 WHERE (PERDORUESI.ID = SHITJET.USR) AND ((SHITJET.KONS_ID = KONSUMATORET.ID) AND ((SHITJET.REC_ID = REC.REC_ID) AND (SHITJET.PROD_ID = PRODUKTET.ID)));\n" +
                    "CREATE FORCE VIEW PUBLIC.VMES(SHUMA) AS SELECT SUM(TOTALNETO) AS SHUMA FROM PUBLIC.VSHITJET GROUP BY RED_ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.VPROD(BARCODE, ID, EMRI, QMIMI_SHITJES, BG, FG, NJESIA, KATEGORIA_ID, SASIA, ZBRITJE) AS SELECT PRODUKTET.BARCODE, PRODUKTET.ID, PRODUKTET.EMRI, PRODUKTET.QMIMI_SHITJES, KAT_PROD.BG, KAT_PROD.FG, PRODUKTET.NJESIA, KAT_PROD.ID AS KATEGORIA_ID, PRODUKTET.SASIA, PRODUKTET.ZBRITJE FROM PUBLIC.PRODUKTET INNER JOIN PUBLIC.KAT_PROD ON 1=1 WHERE (PRODUKTET.SASIA > 0) AND (PRODUKTET.KATEGORIA_ID = KAT_PROD.ID);\n" +
                    "CREATE FORCE VIEW PUBLIC.VREC(TOTAL, REC_ID, KOHA_KRIJIMIT) AS SELECT SUM(VSHITJET.TOTAL) AS TOTAL, REC.REC_ID, REC.KOHA_KRIJIMIT FROM PUBLIC.VSHITJET INNER JOIN PUBLIC.REC ON 1=1 WHERE VSHITJET.RED_ID = REC.REC_ID GROUP BY VSHITJET.RED_ID;\n" +
                    "CREATE FORCE VIEW PUBLIC.VSHITJET(KONS_ID, PRODUKTI, KATEGORIA, QMIMI_SHITJES, FITIMI, KOHA_SHITJES, CASH, KUSURI, SASIA, TOTALNETO, TOTAL, PUNETORI, RED_ID, P_ID, TVSH, PNT_ID, PROD_ZBRITJE) AS SELECT KONSUMATORET.ID AS KONS_ID, PRODUKTET.EMRI AS PRODUKTI, KAT_PROD.KATEGORIA, PRODUKTET.QMIMI_SHITJES, (PRODUKTET.QMIMI_SHITJES - PRODUKTET.QMIMI_STD) AS FITIMI, SHITJET.KOHA_SHITJES, SHITJET.CASH, (SHITJET.CASH - (PRODUKTET.QMIMI_SHITJES * SHITJET.SASIA)) AS KUSURI, SHITJET.SASIA AS SASIA, (SHITJET.SASIA * (PRODUKTET.QMIMI_SHITJES - PRODUKTET.QMIMI_STD)) AS TOTALNETO, (SHITJET.SASIA * ((PRODUKTET.QMIMI_SHITJES - ((PRODUKTET.QMIMI_SHITJES * PRODUKTET.ZBRITJE) / 100)) + (((PRODUKTET.QMIMI_SHITJES - ((PRODUKTET.QMIMI_SHITJES * PRODUKTET.ZBRITJE) / 100)) * REC.TVSH) / 100))) AS TOTAL, PERDORUESI.USR AS PUNETORI, SHITJET.REC_ID AS RED_ID, PRODUKTET.ID AS P_ID, REC.TVSH, PERDORUESI.PNT_ID, PRODUKTET.ZBRITJE AS PROD_ZBRITJE FROM PUBLIC.SHITJET INNER JOIN PUBLIC.KONSUMATORET ON 1=1 INNER JOIN PUBLIC.PRODUKTET ON 1=1 INNER JOIN PUBLIC.KAT_PROD ON 1=1 INNER JOIN PUBLIC.PERDORUESI ON 1=1 INNER JOIN PUBLIC.REC ON 1=1 WHERE (SHITJET.REC_ID = REC.REC_ID) AND ((PERDORUESI.ID = SHITJET.USR) AND ((KONSUMATORET.ID = SHITJET.KONS_ID) AND ((SHITJET.PROD_ID = PRODUKTET.ID) AND (PRODUKTET.KATEGORIA_ID = KAT_PROD.ID))));";
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

//    @FXML
//    private void ruajValuten() {
//        try (PreparedStatement ps = con.prepareStatement("update valuta set valuta.valuta = ?")) {
//            ps.setString(1, cbValuta.getSelectionModel().getSelectedItem());
//            ps.execute();
//            ntf.setMessage("Valuta u ndryshua me sukses");
//            ntf.setType(NotificationType.SUCCESS);
//            ntf.setButton(ButtonType.NO_BUTTON);
//            ntf.show();
//        }catch (Exception e) { e.printStackTrace(); }
//    }
}
