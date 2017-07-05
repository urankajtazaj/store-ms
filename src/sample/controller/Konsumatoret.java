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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by uran on 17-04-20.
 */
public class Konsumatoret implements Initializable {

    SimpleDateFormat tf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-s");

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        fillTableWithData();
        addButtonToCell();

    }

    @FXML private void eksporto() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/export.fxml"));
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
                            ntf.setMessage("Dokumenti u eksportua me sukses");
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
                            ntf.setMessage("Dokumenti u eksportua me sukses");
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

        Scene scene = new Scene(bpExport, 520, 175);
        scene.setFill(Color.TRANSPARENT);
        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ESCAPE)) stage.close();
        });
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
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

            for (sample.constructors.Konsumatoret k : tbl.getItems()) {
                sb.append("merge into konsumatoret key(id) values(" + k.getId() + ",'" + k.getEmri() + "','" + k.getAdresa()+ "','" +
                        k.getTel() + "','" + k.getEmail() + "'," + k.getStatusi() + ",'" + k.getKrijuar() + "',current_timestamp(),'" + k.getQyteti() + "','" +
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
                Button btnDel = new Button();
                Button btnEd = new Button();
                HBox hb = new HBox(btnDel, btnEd);

                @Override
                protected void updateItem(sample.constructors.Konsumatoret item, boolean empty) {

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

                    super.updateItem(item, empty);
                    if (!empty) {
                        setGraphic(hb);
                        sample.constructors.Konsumatoret konsumatoret = tbl.getItems().get(getIndex());
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

                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

    }

    private void dritarjaKonfirmo(String emri, int id, int index) throws Exception{
        ntf.setType(NotificationType.ERROR);
        ntf.setMessage("A jeni te sigurte qe deshironi ta fshini " + emri + " nga lista e konsumatoreve?");
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
            ntf.setMessage("Konsumatori u fshi me sukses");
            ntf.show();
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void fillTableWithData() {
        tbl.setItems(fillData());
        lblTotalKon.setText(tbl.getItems().size()+"");
    }

    private ObservableList<sample.constructors.Konsumatoret> fillData (){
        ObservableList<sample.constructors.Konsumatoret> data = FXCollections.observableArrayList();
        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from konsumatoret");

            while (rs.next()) {
                data.add(new sample.constructors.Konsumatoret(rs.getInt("id"), rs.getString("emri"), rs.getString("email"),
                        rs.getString("telefoni"), rs.getString("adresa"), rs.getString("qyteti"), rs.getString("shteti"), rs.getInt("aktiv"),
                        rs.getString("data_krijimit")));
            }

        }catch (Exception ex ) {ex.printStackTrace();}

        return data;

    }

    @FXML
    private void shtoKons(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoKonsumatoret.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoKonsumatoret.fxml"));
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
