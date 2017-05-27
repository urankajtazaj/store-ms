package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sample.constructors.ProduktetClass;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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

        ObservableList<ProduktetClass> data = FXCollections.observableArrayList(
                new ProduktetClass(1, "Playstation", "Sony", "Teknologji", "Furnizuesi", "100", 23),
                new ProduktetClass(2, "Xbox", "Microsoft", "Teknologji", "Furnizuesi", "95", 0),
                new ProduktetClass(3, "Thinkpad t410", "Lenovo", "Teknologji", "Furnizuesi", "150", 11),
                new ProduktetClass(4, "Compaq 221", "HP", "Teknologji", "Furnizuesi", "75", 3),
                new ProduktetClass(4, "Java Runtime", "Oracle", "Teknologji", "Furnizuesi", "0", 0),
                new ProduktetClass(5, "Shoes", "Nike", "Teknologji", "Furnizuesi", "25", 12),
                new ProduktetClass(6, "Drive", "Google", "Teknologji", "Furnizuesi", "10", 42),
                new ProduktetClass(7, "IntelliJ IDEA", "Intellij", "Teknologji", "Furnizuesi", "5", 10)
        );

        tblProduktet.setItems(data);
    }

    private void fillBarChart(){

        String[] prodE = new String[5], katE = new String[5];
        int[] prodN = new int[5], katN = new int[5];

        getTopProds(prodE, prodN, katE, katN);

        if (prodN[0] > 0) {
            XYChart.Series series1 = new XYChart.Series();
            for (int i = 0; i < 5; i++) {
                series1.getData().add(new XYChart.Data(prodE[i], prodN[i]));
            }
            barChart.getData().add(series1);
        }

        if (katN[0] > 0) {
            XYChart.Series series2 = new XYChart.Series();
            for (int i = 0; i < 5; i++) {
                series2.getData().add(new XYChart.Data(katE[i], katN[i]));
            }
            barChart2.getData().add(series2);
        }

        barChart.setLegendVisible(false);
        barChart2.setLegendVisible(false);

    }

    private void getTopProds (String[] prodE, int[] prodN, String[] katE, int[] katN){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select * from topproduktet");
             Statement s2 = con.createStatement(); ResultSet rs2 = s2.executeQuery("select * from topkategoria")) {

            int i = 0;
            while (rs.next()) {
                prodE[i] = rs.getString(2);
                prodN[i] = rs.getInt(1);
                i++;
            }

            i = 0;
            while (rs2.next()) {
                katE[i] = rs2.getString(2);
                katN[i] = rs2.getInt(1);
                i++;
            }

        }catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void shtoProdukt(){

    }

}
