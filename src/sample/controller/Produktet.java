package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import sample.constructors.ProduktetClass;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-04-07.
 */
public class Produktet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    @FXML private TableView tblProduktet;
    @FXML private TableColumn colStatusi, colAksion, colSasia;
    @FXML private BarChart<String, Number> barChart, barChart2;

    private BorderPane bp;

    public void setBp (BorderPane bp) {
        this.bp = bp;
    }

    Random rand = new Random();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fillTable();
        fillBarChart();

        colAksion.setCellFactory(e -> {
            return new TableCell<String, HBox>() {

                Button btnDel = new Button();
                Button btnEd = new Button();
                HBox hbox = new HBox(btnDel, btnEd);
                ImageView deliv = new ImageView(new Image("/sample/photo/trash.png"));
                ImageView ediv = new ImageView(new Image("/sample/photo/settings.png"));

                @Override
                protected void updateItem(HBox item, boolean empty) {

                    btnDel.setTooltip(new Tooltip("Fshi produktin"));

                    deliv.setPreserveRatio(true);
                    ediv.setPreserveRatio(true);

                    deliv.setFitWidth(15);
                    ediv.setFitWidth(15);

                    btnEd.setGraphic(ediv);
                    btnDel.setGraphic(deliv);

                    hbox.setSpacing(7);

                    super.updateItem(item, empty);
                    if (!empty) {
                        setGraphic(hbox);
                    }

                }
            };
        });

        tblProduktet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tblProduktet.setRowFactory(e -> {
            return new TableRow<ProduktetClass>(){
                @Override
                protected void updateItem(ProduktetClass item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty && item != null) {
                        if (item.getSasia() == 0) {
                            getStyleClass().add("redRow");
                        }
                    }

                }
            };
        });
    }

    private void fillTable(){
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("select * from produktet")){
            tblProduktet.getItems().clear();
            ObservableList<ProduktetClass> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new ProduktetClass(rs.getInt("id"), rs.getString("emri"), VariablatPublike.mProdKat.get(rs.getInt("kategoria_id")),
                        VariablatPublike.decimalFormat.format(rs.getDouble("qmimi_shitjes")), rs.getInt("sasia"), rs.getDouble("zbritje")));
            }
            tblProduktet.setItems(data);
        }catch (Exception e) {e.printStackTrace();}
    }

    private void fillBarChart(){

        List<String> prodE = new ArrayList<>(), katE = new ArrayList<>();
        List<Integer> prodN = new ArrayList<>(), katN = new ArrayList<>();

        getTopProds(prodE, prodN, katE, katN);

        if (prodN.size() == 5) {
            XYChart.Series series1 = new XYChart.Series();
            for (int i = 0; i < 5; i++) {
                series1.getData().add(new XYChart.Data(prodE.get(i), prodN.get(i)));
            }
            barChart.getData().add(series1);
        }

        if (katN.size() == 5) {
            XYChart.Series series2 = new XYChart.Series();
            for (int i = 0; i < 5; i++) {
                series2.getData().add(new XYChart.Data(katE.get(i), katN.get(i)));
            }
            barChart2.getData().add(series2);
        }

        barChart.setLegendVisible(false);
        barChart2.setLegendVisible(false);

    }

    private void getTopProds (List<String> prodE, List<Integer> prodN, List<String> katE, List<Integer> katN){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select * from topproduktet");
             Statement s2 = con.createStatement(); ResultSet rs2 = s2.executeQuery("select * from topkategoria")) {

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
            Parent parent = loader.load();
            bp.setCenter(parent);
        }catch (Exception e) { e.printStackTrace(); }
    }

}
