package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-04-20.
 */
public class Konsumatoret implements Initializable {

    DB db = new DB();
    Connection con = db.connect();

    private BorderPane root;

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

        export.btnAnulo.setOnAction(e -> {
            stage.close();
        });

        Scene scene = new Scene(bpExport, 520, 205);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
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

                    }

                }
            };
        });

    }

    private void dritarjaKonfirmo(String emri, int id, int index) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/konfirmo.fxml"));
        Parent parent = loader.load();
        Konfirmo konfirmo = loader.getController();

        konfirmo.setId(id);
        konfirmo.setTabela(2); /* 1 = PUNETORET, 2 = KONSUMATORET, 3 = PRODUKTET */
        konfirmo.setMessage("Konfirmo fshirjen e '" + emri + "' nga lista e konsumatoreve.");

        Scene scene = new Scene(parent, 460, 200);
        Stage stage = new Stage();

        konfirmo.setStage(stage);
        konfirmo.setTableView(tbl);
        konfirmo.setIndex(index);

        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
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
                        rs.getString("telefoni"), rs.getString("adresa"), rs.getString("qyteti"), rs.getString("shteti")));
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
}
