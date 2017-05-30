package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextAlignment;
import sample.constructors.ShitjetProd;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by uran on 17-05-19.
 */
public class Shitjet implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    @FXML private TableView<ShitjetProd> tbl;
    @FXML private FlowPane flow;
    @FXML private ScrollPane scroll;
    @FXML private Label lTotal;
    @FXML private TableColumn colAct, colSasia;
    @FXML private TextField txtProd;
    @FXML private ComboBox<String> cbCat, cbKons;

    double qmimi = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        merrKons();
        cbKons.getItems().clear();
        Iterator<String> itK = VariablatPublike.konsEmri.iterator();
        while (itK.hasNext()) {
            cbKons.getItems().add(itK.next());
        }
        cbKons.getSelectionModel().select(0);

        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lTotal.setText(VariablatPublike.decimalFormat.format(0));
        cbCat.getItems().clear();
        cbCat.getItems().add("Te gjitha");
        Iterator<String> it = VariablatPublike.prodKat.iterator();
        while (it.hasNext()) {
            cbCat.getItems().add(it.next());
        }
        cbCat.getSelectionModel().select(0);

        cbCat.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            getProducts(txtProd.getText(), nv.toString());
        });

        txtProd.setOnKeyReleased(e -> {
            getProducts(txtProd.getText(), cbCat.getSelectionModel().getSelectedItem());
        });

        txtProd.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                firstButton((Button) flow.getChildren().get(0));
                txtProd.setText("");
            }
        });

        colAct.setCellFactory(e -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        Button btn = new Button();
                        ImageView iv = new ImageView(new Image("/sample/photo/trash.png"));
                        iv.setPreserveRatio(true);
                        iv.setFitWidth(14);
                        btn.setGraphic(iv);
                        btn.setOnAction(e -> {
                            ShitjetProd sp = tbl.getItems().get(getIndex());
                            qmimi -= sp.getQmimi() * Double.parseDouble(sp.getSasia().getText());
                            tbl.getItems().remove(getIndex());
                            lTotal.setText(VariablatPublike.decimalFormat.format(qmimi));
                        });
                        setGraphic(btn);
                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

        colSasia.setCellFactory(e -> {
            return new TableCell<String, TextField>() {
                @Override
                protected void updateItem(TextField item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        ShitjetProd sp = tbl.getItems().get(getIndex());
                        TextField tf = new TextField(sp.getSasia().getText());
                        setOnKeyReleased(e -> {
                            if (Pattern.compile("[0-9]+").matcher(e.getText()).matches()) {
                                lTotal.setText(VariablatPublike.decimalFormat.format(
                                        merrQmimet(tf.getText(), getIndex())));
                            }
                        });

                        setGraphic(tf);
                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

        getProducts("", "");
    }

//    LEXO TE GJITH TABELEN PER SHITJE DHE KTHE QMIMIN PAS NDRYSHIMIT TE SASISE
    private double merrQmimet (String s, int index){
        double t = 0;

        for (int i = 0; i < tbl.getItems().size(); i++) {
            ShitjetProd sp = tbl.getItems().get(i);

            if (s.equals("0") || s.isEmpty()) {
                t += sp.getQmimi();
                continue;
            }

            if (i == index)
                sp.getSasia().setText(s);

            t += sp.getQmimi() * Integer.parseInt(sp.getSasia().getText());
        }

        qmimi = t;
        return t;
    }

//    MERR PRODUKTET DHE KRIJO BUTONAT PER SHITJE
    private void getProducts (String q, String kat) {

        try {
            Statement stmt = con.createStatement();

            StringBuilder sb = new StringBuilder("select id, emri, qmimi_shitjes from produktet where (barcode like lower('%" + q + "%') or lower(emri) like " +
                    "lower('%" + q + "%'))");

            if (!kat.isEmpty() && !kat.equals("Te gjitha")) {
                sb.append(" and kategoria_id = " + VariablatPublike.revProdKat.get(kat));
            }

            ResultSet rs = stmt.executeQuery(sb.toString());

            flow.getChildren().clear();
            while (rs.next()) {
                Button button = new Button(rs.getString("emri") + "\n(" + VariablatPublike.decimalFormat.format(rs.getDouble("qmimi_shitjes")) + ")");
                button.setTextAlignment(TextAlignment.CENTER);
                button.setWrapText(true);
                button.getStyleClass().add("btn");
                button.getStyleClass().add("bigBtn");
                button.setId(rs.getString("id") + " " + rs.getString("qmimi_shitjes"));

                button.setOnAction(e -> {
                    firstButton(button);
                });

                flow.getChildren().add(button);
            }

        }catch (Exception e) { e.printStackTrace();}

    }

    private void firstButton (Button button){
        try {
            tbl.getItems().add(new ShitjetProd(Integer.parseInt(button.getId().split(" ")[0]),
                    button.getText().split("\n")[0], Double.parseDouble(button.getId().split(" ")[1])));
            qmimi += Double.parseDouble(button.getId().split(" ")[1]);
            lTotal.setText(VariablatPublike.decimalFormat.format(qmimi));
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML
    private void perfundoPagesen (){
        try {
            Statement stmt = con.createStatement();
            stmt.addBatch("insert into rec values (null, current_timestamp())");
            for (ShitjetProd sp : tbl.getItems()) {
                batch(stmt, sp.getId(), sp.getQmimi(), Integer.parseInt(sp.getSasia().getText()));
            }
            stmt.executeBatch();
            MesazhetPublike.suksesDritarja("Te dhenat u shtuan me sukses!");
        }catch (Exception e) {e.printStackTrace();}
    }

    private void batch (Statement ps, int id, double qmimi, int sasia) throws Exception {
        ps.addBatch("insert into shitjet values (null, "+id+", "+VariablatPublike.revKons.get(cbKons.getSelectionModel().getSelectedItem())+
                ", current_timestamp(), 120, "+VariablatPublike.uid+", "+sasia+", (select max(rec_id) from rec limit 1))");
        System.err.println("insert into shitjet values (null, "+id+", "+VariablatPublike.revKons.get(cbKons.getSelectionModel().getSelectedItem())+
                ", current_timestamp(), 120, "+VariablatPublike.uid+", "+sasia+", (select max(rec_id) from rec limit 1))");
    }

    @FXML
    private void historiku (){

    }

    private void merrKons(){
        try (Statement s = con.createStatement(); ResultSet rs = s.executeQuery("select emri, id from konsumatoret order by id")) {
            while (rs.next()) {
                VariablatPublike.konsEmri.add(rs.getString("emri"));
                VariablatPublike.revKons.put(rs.getString("emri"), rs.getInt("id"));
            }
        }catch (Exception e ){ e.printStackTrace(); }
    }

}
