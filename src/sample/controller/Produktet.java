package sample.controller;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Enums.*;
import sample.Enums.ButtonType;
import sample.constructors.ProduktetClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by uran on 17-04-07.
 */
public class Produktet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    Notification ntf = new Notification();

    Punetoret pnt = new Punetoret();

    @FXML private TableView<ProduktetClass> tblProduktet;
    @FXML private TableColumn colStatusi, colAksion, colSasia, colZbritje;
    @FXML private BarChart<String, Number> barChart, barChart2;

    SimpleDateFormat tf = new SimpleDateFormat("dd-MM-yyyy HH-mm-s");

    private BorderPane bp;

    public void setBp (BorderPane bp) {
        this.bp = bp;
    }

    Random rand = new Random();

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
        fillBarChart();
        fillTable();

        colAksion.setCellFactory(e -> {
            return new TableCell<String, HBox>() {

                Button btnDel = new Button();
                Button btnEd = new Button();
                HBox hbox = new HBox(btnDel, btnEd);
                ImageView deliv = new ImageView(new Image("/sample/photo/trash.png"));
                ImageView ediv = new ImageView(new Image("/sample/photo/setting.png"));

                @Override
                protected void updateItem(HBox item, boolean empty) {

                    btnDel.setTooltip(new Tooltip("Fshi produktin"));

                    deliv.setPreserveRatio(true);
                    ediv.setPreserveRatio(true);

                    deliv.setFitWidth(15);
                    ediv.setFitWidth(15);

                    btnEd.setGraphic(ediv);
                    btnDel.setGraphic(deliv);

                    hbox.setAlignment(Pos.CENTER);
                    hbox.setSpacing(7);

                    super.updateItem(item, empty);
                    if (!empty) {
                        ProduktetClass p = tblProduktet.getItems().get(getIndex());

                        btnEd.setOnAction(e -> {
                            try {
                                ntf.setMessage(p.getEmri() + ", " + p.getKategoria());
                                ntf.setType(NotificationType.SUCCESS);
                                ntf.setButton(ButtonType.NO_BUTTON);
                                ntf.show();
                                rregulloProd(p.getId(), p.getEmri(), p.getBc(), p.getQmimiStd(), p.getQmimi().substring(0, p.getQmimi().length()-1), p.getSasia(), p.getSasiaKrit(), p.getZbritje(),
                                        p.getKategoria());
                            }catch (Exception ex) {ex.printStackTrace(); }
                        });

                        btnDel.setOnAction(e -> {
                            ntf.setMessage("A jeni te sigurte qe deshironi ta fshini kete produkt?");
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

        tblProduktet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colSasia.setCellFactory(e -> {
            return new TableCell<ProduktetClass, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);

                    TableRow<ProduktetClass> row = getTableRow();

                    if (!empty && row != null) {
                        ProduktetClass pc = tblProduktet.getItems().get(getIndex());
                        setText(item + "");
                        if (item <= pc.getSasiaKrit() || item == 0) {
                            row.getStyleClass().add("redRow");
                        }
                    }

                }
            };
        });

    }

    @FXML private void export() throws Exception {
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
                    pnt.excelFile("Produktet", "xlsx", keySet());
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
                    toCSV();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
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

        export.btnAnulo.setOnAction(e -> {
            stage.close();
        });

        Scene scene = new Scene(bpExport, 400, 165);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void rregulloProd (int id, String emri, String bc, double qstd, String qs, int stok, int stokk, String zbritje, String catIndex) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoProdukte.fxml"));
        ShtoProdukte sp = new ShtoProdukte();
        sp.setId(id);
        sp.setEmri(emri);
        sp.setBc(bc);
        sp.setQmimiStd(qstd);
        sp.setQmimiShitjes(Double.parseDouble(qs));
        sp.setStok(stok);
        sp.setStokCrit(stokk);
        sp.setZbritje(Double.parseDouble(zbritje.substring(0, zbritje.length()-1)));
        sp.setCbKategoria(catIndex);
        loader.setController(sp);
        bp.setCenter(loader.load());
    }

    private void fillTable(){
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("select * from produktet order by sasia")){
            tblProduktet.getItems().clear();
            ObservableList<ProduktetClass> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new ProduktetClass(rs.getString("barcode"), rs.getInt("id"), rs.getString("emri"),
                        VariablatPublike.mProdKat.get(rs.getInt("kategoria_id")),
                        VariablatPublike.decimalFormat.format(rs.getDouble("qmimi_shitjes")), rs.getDouble("qmimi_std"), rs.getInt("sasia"), rs.getInt("stokcrit"),
                        rs.getDouble("zbritje") + "%"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoProdukte.fxml"));
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
                sb.append(p.getBc() + "," + p.getId() + "," + p.getEmri() + "," + p.getKategoria() + "," + p.getSasia() + "," + p.getQmimi() + "," + p.getZbritje() + "\n");
            }

            bw.write(sb.toString());

        }catch (Exception e) { e.printStackTrace(); }
    }
}
