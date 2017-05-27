package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by uran on 17-05-01.
 */
public class ShtoDepartamente implements Initializable {

    DB db = new DB();
    Connection con = db.connect();
    @FXML public Button btnKthehu, btnRuaj, btnShto;
    @FXML private VBox vb;

    Set<String> stringSet = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnShto.setOnAction(e -> {
            shtoOpsion("", false);
        });

        getDeps();

        btnRuaj.setOnAction(e -> {
            try {
                getTxt(vb);
            }catch (Exception ex) {ex.printStackTrace();}
        });

    }

    private void getDeps(){
        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select distinct departamenti.departamenti as emri from departamenti");

            while (rs.next()) {
                stringSet.add(rs.getString("emri"));
                shtoOpsion(rs.getString("emri"), true);
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getTxt(VBox vbox) throws Exception {
        Statement stmt = con.createStatement();

        for (Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                if (!((TextField) node).getText().equals("")) {
                    if (!stringSet.contains(((TextField) node).getText())) {
                        stmt.addBatch("insert into departamenti (departamenti) values ('" + ((TextField) node).getText() + "')");
                        stringSet.add(((TextField) node).getText());
                    }
                }
            }
        }
        stmt.executeBatch();
        MesazhetPublike.suksesDritarja("Te dhenat u ruajten me sukses.");
    }

    private void shtoOpsion(String t, boolean disabled){
        TextField tf = new TextField(t);
        tf.setDisable(disabled);
        vb.getChildren().add(0, tf);
    }

}
