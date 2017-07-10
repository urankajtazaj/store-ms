package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-04-30.
 */
public class ShtoPunetoret implements Initializable {

    DB db = new DB();
    private Connection con = db.connect();

    Notification ntf = new Notification();

    @FXML private BorderPane shtoBp, childBp;
    @FXML Button btnRuaj;
    @FXML private Hyperlink addDp;
    @FXML private TextField emri, mbiemri, paga, titulli, telefoni, email, adresa, txtQyteti, txtShteti, txtUser;
    @FXML private PasswordField txtPw;
    @FXML private ComboBox<String> departamenti, gjinia, cbStatusi, cbShteti, cbQyteti;
    @FXML private DatePicker punesuar, ditelindja;
    @FXML private Label lblFoto;
    @FXML private Button btnFotoHape;

    int id = -1;
    char mode = 'i';

    public void setId (int id)
    {
        this.id = id;
    }
    public void setMode (char mode) {
        this.mode = mode;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        punesuar.setValue(LocalDate.now());
        cbStatusi.getSelectionModel().select(1);

        emri.focusedProperty().addListener((o, ov, nv) -> {
            if (nv.booleanValue() == false && !mbiemri.getText().isEmpty()) {
                txtUser.setText(emri.getText().toLowerCase() + "." + mbiemri.getText().toLowerCase());
            }
        });

        mbiemri.focusedProperty().addListener((o, ov, nv) -> {
            if (nv.booleanValue() == false && !emri.getText().isEmpty()) {
                txtUser.setText(emri.getText().toLowerCase() + "." + mbiemri.getText().toLowerCase());
            }
        });

        ditelindja.setConverter(VariablatPublike.converter);
        punesuar.setConverter(VariablatPublike.converter);

        btnRuaj.setOnAction(e -> {
            shtoPnt(mode, id);
        });

        merrShtetet();
        cbShteti.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv.equals("Tjeter")) {
                txtQyteti.setDisable(false);
                txtShteti.setDisable(false);
                cbQyteti.setDisable(true);
            }else {
                merrQytetet(VariablatPublike.shteti.get(nv));
            }
        });
        cbShteti.getSelectionModel().select(0);

        try {
            departamenti.getItems().clear();
            merrDeps("Select * from departamenti", departamenti);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rregulloPnt(id);
    }

    public void merrDeps(String q, ComboBox<String> cb) throws Exception{
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(q);

        while (rs.next()) {
            if (cb != null) cb.getItems().add(rs.getString("departamenti"));
            VariablatPublike.dep.put(rs.getString("departamenti"), rs.getInt("id"));
            VariablatPublike.revDep.put(rs.getInt("id"), rs.getString("departamenti"));
        }
        if (cb != null) cb.getSelectionModel().select(0);

        for (Map.Entry<String, Integer> m : VariablatPublike.dep.entrySet()) {
            VariablatPublike.revDep.put(m.getValue(), m.getKey());
        }

    }

    private void rregulloPnt (int id) {
        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from punetoret where id = " + id + " limit 1");

            Statement stmt2 = null;
            ResultSet rs2 = null;

            if (id > 0) {
                stmt2 = con.createStatement();
                rs2 = stmt2.executeQuery("select usr, pw from perdoruesi where pnt_id = " + id + " limit 1");
            }

            while (rs.next()) {
                emri.setText(rs.getString("emri"));
                mbiemri.setText(rs.getString("mbiemri"));
                paga.setText(rs.getDouble("paga") + "");
                telefoni.setText(rs.getString("telefoni"));
                email.setText(rs.getString("email"));
                adresa.setText(rs.getString("adresa"));
                cbQyteti.getSelectionModel().select(rs.getString("qyteti"));
                cbShteti.getSelectionModel().select(rs.getString("shteti"));
                lblFoto.setText(rs.getString("foto"));
                cbStatusi.getSelectionModel().select(rs.getInt("statusi"));
                departamenti.getSelectionModel().select(VariablatPublike.revDep.get(rs.getInt("dep_id")));
                gjinia.getSelectionModel().select(rs.getInt("gjinia"));
                ditelindja.setValue(rs.getDate("ditelindja").toLocalDate());
                punesuar.setValue(LocalDate.parse(rs.getString("data_punesimit"), VariablatPublike.dtf));
            }

            if (id > 0) {
                rs2.next();
                txtUser.setText(rs2.getString("usr"));
                txtPw.setText(rs2.getString("pw"));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shtoPnt(int mode, int id){
        try {
            PreparedStatement pstmt = null;
            PreparedStatement pstmt2 = null;
            if (mode == 'i') {
                pstmt2 = con.prepareStatement("insert into perdoruesi values (null, (select max(id) from punetoret), ?, ?, ?)");
                pstmt = con.prepareStatement("INSERT INTO punetoret" +
                "(id, dep_id, emri, mbiemri, gjinia, ditelindja, paga, data_punesimit, statusi, " +
                        "telefoni, modifikuar, adresa, qyteti, shteti, email, data_krijimit, foto)" +
                "VALUES" +
                "(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_date(), ?)");
            }else if (mode == 'u' && id > 0) {
                pstmt2 = con.prepareStatement("update perdoruesi set usr = ?, pw = ?, dep_id = ? where pnt_id = " + id);
                pstmt = con.prepareStatement("update punetoret set " +
                "dep_id = ?, emri = ?, mbiemri = ?, gjinia = ?, ditelindja = ?, paga = ?, " +
                "data_punesimit = ?, statusi = ?, telefoni = ?, modifikuar = ?, adresa = ?, qyteti=?," +
                        "shteti=?,email=?,foto = ? " +
                "where id = " + id);
            }

            if (!emri.getText().equals("") && !paga.getText().equals("") && !punesuar.getEditor().getText().equals("")) {
                if (!paga.getText().trim().matches("^\\d+(\\.\\d)*$") || !telefoni.getText().trim().matches("|^[0-9 ]+")) {
                    ntf.setMessage("Gabim ne konvertim te te dhenave, kontrolloni fushat ku kerkohen numra");
                    ntf.setType(NotificationType.ERROR);
                    ntf.show();
                }else {
                    pstmt.setInt(1, VariablatPublike.dep.get(departamenti.getSelectionModel().getSelectedItem()));
                    pstmt.setString(2, emri.getText());
                    pstmt.setString(3, mbiemri.getText());
                    pstmt.setInt(4, gjinia.getSelectionModel().getSelectedItem() == "Mashkull" ? 1 : 0);
                    pstmt.setDate(5, java.sql.Date.valueOf(formatter.format(ditelindja.getValue())));
                    pstmt.setDouble(6, Double.parseDouble(paga.getText()));
                    pstmt.setDate(7, java.sql.Date.valueOf(formatter.format(punesuar.getValue())));
                    pstmt.setInt(8, cbStatusi.getSelectionModel().getSelectedIndex());
                    pstmt.setString(9, telefoni.getText());
                    pstmt.setString(10, formatterTime.format(LocalDateTime.now()));
                    pstmt.setString(11, adresa.getText());
                    pstmt.setString(12, cbQyteti.getSelectionModel().getSelectedItem());
                    pstmt.setString(13, cbShteti.getSelectionModel().getSelectedItem());
                    pstmt.setString(14, email.getText());
                    pstmt.setString(15, lblFoto.getText());
                    pstmt.execute();

                    pstmt2.setString(1, txtUser.getText());
                    pstmt2.setString(2, txtPw.getText());
                    pstmt2.setInt(3, VariablatPublike.dep.get(departamenti.getSelectionModel().getSelectedItem()));
                    pstmt2.execute();

                    pastro();
                    ntf.setMessage("Te dhenat u ruajten me sukses");
                    ntf.setType(NotificationType.SUCCESS);
                    ntf.setButton(ButtonType.NO_BUTTON);
                    ntf.show();
                }
            }else {
                ntf.setMessage("Fushat e kerkuara duhet te permbajne te dhena");
                ntf.setType(NotificationType.ERROR);
                ntf.setButton(ButtonType.NO_BUTTON);
                ntf.show();
            }
        }catch (Exception ex) {ex.printStackTrace();}

    }

    private void pastro (){
        email.setText("");
        emri.setText("");
        mbiemri.setText("");
        paga.setText("");
        adresa.setText("");
        txtQyteti.setText("");
        txtShteti.setText("");
        cbShteti.getSelectionModel().select(0);
        cbQyteti.getSelectionModel().select(0);
        telefoni.setText("");
        punesuar.setValue(LocalDate.now());
        ditelindja.getEditor().clear();
        departamenti.getSelectionModel().select(0);
        gjinia.getSelectionModel().select(0);
    }

    private void merrShtetet(){
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("select * from shteti")) {
            cbShteti.getItems().clear();
            while (rs.next()) {
                cbShteti.getItems().add(rs.getString("shteti"));
                VariablatPublike.revShteti.put(rs.getInt("id"), rs.getString("shteti"));
                VariablatPublike.shteti.put(rs.getString("shteti"), rs.getInt("id"));
            }
            cbShteti.getItems().add("Tjeter");
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void merrQytetet(int id){
        try (PreparedStatement stmt = con.prepareStatement("select * from qytetet where shteti_id = ?")) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            cbQyteti.getItems().clear();
            while (rs.next()) {
                cbQyteti.getItems().add(rs.getString("qyteti"));
                VariablatPublike.qyteti.put(rs.getString("qyteti"), rs.getInt("id"));
                VariablatPublike.revQyteti.put(rs.getInt("id"), rs.getString("qyteti"));
            }
            cbQyteti.getSelectionModel().select(0);
        }catch (Exception e) {e.printStackTrace();}
    }

}
