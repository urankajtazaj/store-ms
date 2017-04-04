package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.net.URL;
import java.util.Calendar;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by Uran on 14/03/2017.
 */
public class Dashboard implements Initializable{

    @FXML VBox chartVb;
    @FXML private TableView tbl;
    @FXML private TableColumn sts, colPuna, colMng;
    @FXML private ComboBox cb;
    @FXML NumberAxis xAxis1, yAxis1;
    @FXML LineChart line1;
    @FXML PieChart pieChart1, pieChart2, pieChart3, pieChart4;
    @FXML Label lbPerc1, lbPerc2, lbPerc3, lbPerc4;
    @FXML ComboBox cbMuaji, cbDita, cbDitaDeri, cbViti, cbPamja;
    @FXML Button btFiltro;

    NumberAxis y = new NumberAxis();
    NumberAxis x = new NumberAxis();
    LineChart lineChart = new LineChart(x, y);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Random random = new Random();
        btFiltro.setOnAction(e -> {
            fillPieCharts(random);
        });

        mbushDitetCb(leapYear(Integer.parseInt(cbViti.getSelectionModel().getSelectedItem().toString())), Calendar.getInstance().get(Calendar.MONTH)+1);
        cbDita.getSelectionModel().select(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        x.setLabel("Dita e muajit");
        displayChart();
        tbl.setPlaceholder(new Label("Nuk ka te dhena"));
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        sts.setCellFactory(e -> {
            return new TableCell<Punet, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    ImageView iv = new ImageView();
                    iv.setFitHeight(16);
                    iv.setPreserveRatio(true);

                    setAlignment(Pos.CENTER);
                    if (!empty) {
                        if (item.toLowerCase().equals("aktiv")) {
                            iv.setImage(new Image("/sample/photo/a.png"));
                            setGraphic(iv);
                        } else if (item.toLowerCase().equals("pritje")){
                            iv.setImage(new Image("/sample/photo/prt.png"));
                            setGraphic(iv);
                        }
                        else if (item.toLowerCase().equals("perfunduar")) {
                            iv.setImage(new Image("/sample/photo/prf.png"));
                            setGraphic(iv);
                        }else {
                            iv.setImage(new Image("/sample/photo/ndl.png"));
                            setGraphic(iv);
                        }
                    }

                }
            };
        });

        colProp();

        fillTable();
        fillLineChart();
        ditaDisableCb();

        cbDita.setDisable(true);
        cbDitaDeri.setDisable(true);
        cbMuaji.setDisable(true);
        cbViti.setDisable(false);

        cbMuaji.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> {
            if (nv.intValue() == 0)
                mbushDitetCb(leapYear(Integer.parseInt(cbViti.getSelectionModel().getSelectedItem().toString())), Calendar.getInstance().get(Calendar.MONTH)+1);
            else
                mbushDitetCb(leapYear(Integer.parseInt(cbViti.getSelectionModel().getSelectedItem().toString())), nv.intValue());
        });

    }

    private void displayChart(){

        lineChart.setAnimated(false);

        Random rand = new Random();

        XYChart.Series series = new XYChart.Series();
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        for (int i = 1; i <= 20; i++) {
            series.getData().add(new XYChart.Data<>(i, rand.nextInt(100)));
            series1.getData().add(new XYChart.Data<>(i, rand.nextInt(100)));
            series2.getData().add(new XYChart.Data<>(i, rand.nextInt(100)));
            series3.getData().add(new XYChart.Data<>(i, rand.nextInt(100)));
        }

        lineChart.getData().addAll(series, series1, series2, series3);

        chartVb.getChildren().add(0, lineChart);

        Axis<Double> xa = lineChart.getXAxis();

        Tooltip tp = new Tooltip("");
        tp.getStyleClass().add("chart-tooltip");
        lineChart.lookup(".chart-plot-background").setOnMouseMoved(event -> {
            Node n = ((XYChart.Data)series.getData().get((int)xa.toNumericValue(xa.getValueForDisplay(event.getX())))).getNode();
            tp.setText(xa.getValueForDisplay(event.getX()).intValue()+"\n$"+
                    ((XYChart.Data)series.getData().get((int)xa.toNumericValue(xa.getValueForDisplay(event.getX())))).getYValue());
            tp.show(lineChart.lookup(".chart-plot-background"),
                    n.localToScene(n.getBoundsInLocal()).getMinX()-tp.getWidth()/2,
                    n.localToScene(n.getBoundsInLocal()).getMinY()-tp.getHeight()/2-2);
        });

        series.getNode().setOnMouseExited(e -> {
            series.getNode().setStyle("-fx-background-color: transparent");
        });

        lineChart.setOnMouseExited(e -> {
            tp.hide();
        });

        lineChart.setVerticalGridLinesVisible(false);
        x.setAutoRanging(false);
        x.setUpperBound(20);
        x.setTickUnit(1);
        x.setMinorTickVisible(false);
        x.setStyle("-fx-opacity: .7");

        lineChart.setLegendVisible(false);
    }

    private String cap(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length()).toLowerCase();
    }

    private void fillTable(){
        ObservableList<Punet> data = FXCollections.observableArrayList(
                new Punet(1, "Douchebag", "Uran Kajtazaj", "Sektori", "07-12-2017", "15-12-2017", "AKTIV"),
                new Punet(2, "Lorem ipsum dolor sit", "Ardei Imeraj", "Sektori", "15-12-2017", "15-12-2017", "PERFUNDUAR"),
                new Punet(3, "Amet hello world", "Driton Berisha", "Sektori", "15-12-2017", "15-12-2017", "PRITJE"),
                new Punet(4, "World lorem hello", "Auron Duraku", "Sektori", "15-12-2017", "15-12-2017", "AKTIV"),
                new Punet(5, "Lorem ipsum dolor hello", "Shkumbin Sadikaj", "Sektori", "15-12-2017", "15-12-2017", "PERFUNDUAR"),
                new Punet(5, "Lorem dolor hello", "Alban Sadikaj", "Sektori", "15-12-2017", "15-12-2017", "NDALUAR")
        );
        tbl.setItems(data);
    }

    private void colProp(){
        colPuna.setCellFactory(e -> {
            return new TableCell<Punet, Hyperlink>(){
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);

                    setGraphic(item);
                    if (!empty) {
                        item.setOnAction(e -> {
                            System.out.println(  );
                        });
                    }

                }
            };
        });
    }

    private void fillLineChart(){

        pieChart1.setBorder(Border.EMPTY);
        pieChart1.setClockwise(true);
        pieChart1.setStartAngle(90);
        pieChart1.getData().add(new PieChart.Data("", 87));
        pieChart1.getData().add(new PieChart.Data("", 100-pieChart1.getData().get(0).getPieValue()));
        lbPerc1.setText((int)pieChart1.getData().get(0).getPieValue()+"%");

        pieChart2.setBorder(Border.EMPTY);
        pieChart2.setClockwise(true);
        pieChart2.setStartAngle(90);
        pieChart2.getData().add(new PieChart.Data("", 17));
        pieChart2.getData().add(new PieChart.Data("", 100-pieChart2.getData().get(0).getPieValue()));
        lbPerc2.setText((int)pieChart2.getData().get(0).getPieValue()+"%");

        pieChart3.setBorder(Border.EMPTY);
        pieChart3.setClockwise(true);
        pieChart3.setStartAngle(90);
        pieChart3.getData().add(new PieChart.Data("", 47));
        pieChart3.getData().add(new PieChart.Data("", 100-pieChart3.getData().get(0).getPieValue()));
        lbPerc3.setText((int)pieChart3.getData().get(0).getPieValue()+"%");

        pieChart4.setBorder(Border.EMPTY);
        pieChart4.setClockwise(true);
        pieChart4.setStartAngle(90);
        pieChart4.getData().add(new PieChart.Data("", 29));
        pieChart4.getData().add(new PieChart.Data("", 100-pieChart4.getData().get(0).getPieValue()));
        lbPerc4.setText((int)pieChart4.getData().get(0).getPieValue()+"%");

    }

    private void mbushDitetCb(boolean leap, int muaji) {
        int ditet = (muaji%2==1?(muaji>=7?30:31) : (muaji>=8?31:30));
        if (muaji==2 && leap)
            ditet = 29;
        else if (muaji==2 & !leap)
            ditet = 28;
        cbDita.getItems().clear();
        for (int i = 1; i <= ditet; i++) {
            cbDita.getItems().add(i);
        }
        cbDita.getSelectionModel().select(0);
    }

    private boolean leapYear(int viti) {
        if (viti % 4 == 0) {
            if (viti % 200 == 0)
                return false;
            else if (viti % 400 == 0)
                return true;
            return true;
        }
        return false;
    }

    private void ditaDisableCb(){
        cbPamja.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> {
            if (nv.intValue() == 0) {
                cbDita.setDisable(true);
                cbDitaDeri.setDisable(true);
                cbMuaji.setDisable(true);
                cbViti.setDisable(false);
            }else if (nv.intValue() == 1) {
                cbDita.setDisable(true);
                cbDitaDeri.setDisable(true);
                cbMuaji.setDisable(false);
                cbViti.setDisable(false);
            }else if (nv.intValue() == 2) {
                cbDita.setDisable(true);
                cbDitaDeri.setDisable(true);
                cbMuaji.setDisable(false);
                cbViti.setDisable(false);
            }else if (nv.intValue() == 3) {
                cbDita.setDisable(false);
                cbDitaDeri.setDisable(true);
                cbMuaji.setDisable(false);
                cbViti.setDisable(false);
            }else if (nv.intValue() == 4) {
                cbDita.setDisable(false);
                cbDitaDeri.setDisable(false);
                cbMuaji.setDisable(false);
                cbViti.setDisable(false);
            }
        });
    }

    private void fillPieCharts(Random random){

        pieChart1.getData().clear();
        pieChart2.getData().clear();
        pieChart3.getData().clear();
        pieChart4.getData().clear();

        pieChart1.getData().add(new PieChart.Data("", random.nextInt(100)));
        pieChart1.getData().add(new PieChart.Data("", 100-pieChart1.getData().get(0).getPieValue()));
        lbPerc1.setText((int)pieChart1.getData().get(0).getPieValue()+"%");

        pieChart2.getData().add(new PieChart.Data("", random.nextInt(100)));
        pieChart2.getData().add(new PieChart.Data("", 100-pieChart2.getData().get(0).getPieValue()));
        lbPerc2.setText((int)pieChart2.getData().get(0).getPieValue()+"%");

        pieChart3.getData().add(new PieChart.Data("", random.nextInt(100)));
        pieChart3.getData().add(new PieChart.Data("", 100-pieChart3.getData().get(0).getPieValue()));
        lbPerc3.setText((int)pieChart3.getData().get(0).getPieValue()+"%");

        pieChart4.getData().add(new PieChart.Data("", random.nextInt(100)));
        pieChart4.getData().add(new PieChart.Data("", 100-pieChart4.getData().get(0).getPieValue()));
        lbPerc4.setText((int)pieChart4.getData().get(0).getPieValue()+"%");

    }
}
