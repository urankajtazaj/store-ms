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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.*;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by uran on 17-04-20.
 */
public class Konsumatoret implements Initializable {

    SimpleDateFormat tf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-s");
    SimpleDateFormat sqlDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    DB db = new DB();
    Connection con = db.connect();
    private BorderPane root;

    Notification ntf = new Notification();

    Punetoret pnt = new Punetoret();

    private RotateTransition transition;
    public void setTransition(RotateTransition transition) {
        this.transition = transition;
    }

    private ImageView iv;
    public void setIv (ImageView iv) {
        this.iv = iv;
    }

    public void setRoot (BorderPane root){
        this.root = root;
    }

    @FXML private TableView<sample.constructors.Konsumatoret> tbl;
    @FXML private TableColumn colAction;
    @FXML private Label lblTotalKon, lblTotalP, lblPs, lblPd;
    @FXML private TextField txtEmri, txtQyteti, txtAdr;

    @FXML private Button btnAdd;

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;

        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        txtQyteti.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) fillTableWithData();
        });

        txtEmri.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) fillTableWithData();
        });

        txtAdr.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) fillTableWithData();
        });

        if (!VariablatPublike.kons_add) btnAdd.setVisible(false);

        fillTableWithData();
        addButtonToCell();

    }

    @FXML private void eksporto() throws Exception {
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
                    pnt.excelFile("Konsumatoret", "xlsx", keySet());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            VariablatPublike.stopSpinning(transition, iv);
                            ntf.setMessage(rb.getString("kons_doc_sukses"));
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
                            ntf.setMessage("Dokumenti u eksportua me sukses!");
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
                    toCsv();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ntf.setMessage(rb.getString("kons_doc_sukses"));
                            ntf.setButton(ButtonType.NO_BUTTON);
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.show();
                        }
                    });
                }
            }).start();
        });

        export.btnSql.setOnAction(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    toSql();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ntf.setMessage(rb.getString("kons_doc_sukses"));
                            ntf.setButton(ButtonType.NO_BUTTON);
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.show();
                        }
                    });
                }
            }).start();
        });

        export.btnAnulo.setOnAction(e -> {
            stage.close();
        });

        Scene scene = new Scene(bpExport, 520, 214);
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

    private void toCsv (){
        String path = System.getProperty("user.home") + "/store-ms-files/Raportet/CSV/";
        File file = new File(path + "Konsumatoret " + tf.format(new Date()) + ".csv");
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {

            for (sample.constructors.Konsumatoret k : tbl.getItems()) {
                bw.write(k.getId() + "," + k.getEmri() + "," + k.getEmail() + "," + k.getTel() + "," + k.getAdresa() + "," + k.getQyteti() + "," +
                k.getShteti() + "," + (k.getStatusi() == 1 ? "Aktiv," : "Joaktiv,") + k.getKrijuar());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toSql() {

        String path = System.getProperty("user.home") + "/store-ms-files/Raportet/SQL/";
        File file = new File(path + "Konsumatoret " + tf.format(new Date()) + ".sql");

        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
            StringBuilder sb = new StringBuilder();

            sb.append("-- id, emri, adresa, telefoni, email, statusi, krijuar, modifikuar, qyteti, shteti\n");
            for (sample.constructors.Konsumatoret k : tbl.getItems()) {
                sb.append("insert into konsumatoret values(" + k.getId() + ",'" + k.getEmri() + "','" + k.getAdresa()+ "','" +
                        k.getTel() + "','" + k.getEmail() + "'," + k.getStatusi() + ",'" + k.getKrijuar() + "','"+sqlDf.format(new Date())+"','" + k.getQyteti() + "','" +
                        k.getShteti() + "')\n");
            }
            bw.write(sb.toString());
        }catch (Exception e){ e.printStackTrace(); }
    }

    private void toPdf() {
        try {
            String path = System.getProperty("user.home") + "/store-ms-files/Raportet/";
            JasperReport jasperReport = JasperCompileManager.compileReport(path + "raportet/Konsumatoret.jrxml");
            Map<String, Object> params = new HashMap();
            params.put("punetori", VariablatPublike.uemri);

            JasperPrint jprint = JasperFillManager.fillReport(jasperReport, params, con);

            JasperExportManager.exportReportToPdfFile(jprint, path + "PDF/Konsumatoret " + tf.format(new Date()) + ".pdf");
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void addButtonToCell(){
        colAction.setCellFactory(e -> {
            return new TableCell<String, sample.constructors.Konsumatoret>() {
                @Override
                protected void updateItem(sample.constructors.Konsumatoret item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        Button btnDel = new Button();
                        Button btnEd = new Button();
                        HBox hb = new HBox();

                        ImageView btIvEd = new ImageView(new Image("/sample/photo/setting.png"));
                        btIvEd.setFitWidth(15);
                        btIvEd.setPreserveRatio(true);
                        btnEd.setGraphic(btIvEd);
                        ImageView btIvDel = new ImageView(new Image("/sample/photo/trash.png"));
                        btIvDel.setFitWidth(15);
                        btIvDel.setPreserveRatio(true);
                        btnDel.setGraphic(btIvDel);

                        hb.setSpacing(7);
                        hb.setAlignment(Pos.CENTER);

                        sample.constructors.Konsumatoret konsumatoret = tbl.getItems().get(getIndex());

                        if (VariablatPublike.kons_edit) hb.getChildren().add(btnEd);
                        if (VariablatPublike.kons_del) hb.getChildren().add(btnDel);

                        btnDel.setOnAction(e -> {
                            try {
                                dritarjaKonfirmo(konsumatoret.getEmri(), konsumatoret.getId(), getIndex());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        });

                        btnEd.setOnAction(e -> {
                            try {
                                rregulloKons(konsumatoret.getId());
                            }catch (IOException ex) {ex.printStackTrace();}
                        });

                        setGraphic(hb);

                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

    }

    private void dritarjaKonfirmo(String emri, int id, int index) throws Exception{
        ntf.setType(NotificationType.ERROR);
        ntf.setMessage(MessageFormat.format(rb.getString("kons_sigurt_delete"), emri));
        ntf.setButton(ButtonType.YES_NO);
        ntf.showAndWait();

        if (ntf.getDelete()) {
            fshiPunetorin(id);
            tbl.getItems().remove(index);
        }
    }

    private void fshiPunetorin(int id) {
        try (PreparedStatement ps = con.prepareStatement("delete from konsumatoret where id = ?")) {
            ps.setInt(1, id);
            ps.execute();
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setMessage(rb.getString("kons_del_sukses"));
            ntf.show();
        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void fillTableWithData() {
        tbl.setItems(fillData());
        lblTotalKon.setText(tbl.getItems().size()+"");
    }

    private ObservableList<sample.constructors.Konsumatoret> fillData (){
        ObservableList<sample.constructors.Konsumatoret> data = FXCollections.observableArrayList();
        try {

            String sql = "select * from konsumatoret";

            if (!txtAdr.getText().isEmpty() || !txtEmri.getText().isEmpty() || !txtQyteti.getText().isEmpty()) {
                sql += " where lower(emri) like lower(?) and lower(adresa) like lower(?) and lower(qyteti) like lower(?)";
            }

            PreparedStatement ps = con.prepareStatement(sql);

            System.out.println(sql);

            if (!txtAdr.getText().isEmpty() || !txtEmri.getText().isEmpty() || !txtQyteti.getText().isEmpty()) {
                ps.setString(1, "%" + txtEmri.getText() + "%");
                ps.setString(2, "%" + txtAdr.getText() + "%");
                ps.setString(3, "%" + txtQyteti.getText() + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.add(new sample.constructors.Konsumatoret(rs.getInt("id"), rs.getString("emri"), rs.getString("email"),
                        rs.getString("telefoni"), rs.getString("adresa"), rs.getString("qyteti"), rs.getString("shteti"), rs.getInt("aktiv"),
                        rs.getString("data_krijimit"), rs.getString("nr_fiskal")));
            }

        }catch (Exception ex ) {ex.printStackTrace();}

        return data;

    }

    @FXML
    private void shtoKons(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoKonsumatoret.fxml"), rb);
        ShtoKonsumatoret sk = new ShtoKonsumatoret();
        loader.setController(sk);
        Parent parent = null;
        try {
            parent = loader.load();
            sk.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }

        root.setCenter(parent);
    }

    private void rregulloKons(int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoKonsumatoret.fxml"), rb);
        ShtoKonsumatoret sk = new ShtoKonsumatoret();
        sk.setRoot(root);
        sk.setId(id);

        loader.setController(sk);

        root.setCenter(loader.load());

    }

    private Map<String, Object[]> keySet (){
        Map<String, Object[]> xlsData = new TreeMap<>();
        int i = 1;
        xlsData.put((i++)+"", new Object[] {"ID", "EMRI", "ADRESA", "TELEFONI", "EMAIL", "QYTETI", "SHTETI", "KRIJUAR"});
        for (sample.constructors.Konsumatoret k : tbl.getItems()) {
            xlsData.put((i++)+"", new Object[] {k.getId(), k.getEmri(), k.getAdresa(), k.getTel(), k.getEmail(), k.getQyteti(), k.getShteti()});
        }
        xlsData.put((i++) + "", new Object[] {});
        xlsData.put((i++) + "", new Object[] {"Konsumatorë", i-4});
        return xlsData;
    }
}
