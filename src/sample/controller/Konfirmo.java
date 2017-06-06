package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-04-20.
 */
public class Konfirmo implements Initializable {

    @FXML private Label mesazhi, titulli;
    @FXML public Button btnOk, btnAnulo;

    DB db = new DB();
    Connection con = db.connect();
    private Stage stage;
    private int index;
    private TableView tableView;

    int tabela = 0;

    private int id = -1;

    public void setIndex(int index){
        this.index = index;
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
    }

    public void setStage(Stage root){
        this.stage = root;
    }

    private Stage getStage(){
        return this.stage;
    }

    public void setTitle(String title){
        titulli.setText(title);
    }

    public void setMessage (String message) {
        mesazhi.setText(message);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setTabela (int tabela) {
        this.tabela = tabela;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void mbyllDritaren(){
        getStage().close();
    }

    @FXML
    private void egzekuto(){
        tableView.getItems().remove(index);
        try {
            fshiNgaDb(tabela);
        }catch (SQLException e) {e.printStackTrace();}
        mbyllDritaren();
    }

    private void fshiNgaDb (int t) throws SQLException {
//        1 = Punetoret
//        2 = Konsumatoret
//        3 = Produktet
        String emri = "";
        String query = "";
        if (t == 1) {
            emri = "Punetori";
            query = "delete from punetoret where id = " + getId();
        }else if (t == 2) {
            emri = "Konsumatori";
            query = "delete from konsumatoret where id = " + getId();
        }else if (t == 3) {
            emri = "Produkti";
            query = "delete from produktet where id = " + getId();
        }

        Statement stmt = con.createStatement();
        stmt.execute(query);
        MesazhetPublike.Lajmerim(emri + " u fshi me sukses", MesazhetPublike.ButtonType.NO_BUTTON, MesazhetPublike.NotificationType.SUCCESS, 5);
    }

}
