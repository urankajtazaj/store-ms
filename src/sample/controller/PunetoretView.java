package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

/**
 * Created by uran on 17-05-10.
 */
public class PunetoretView implements Initializable {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    GridPane grid = new GridPane();
    DB db = new DB();
    Connection con = db.connect();
    @FXML private Circle crcStatusi;
    @FXML private Button btnRregullo;
    @FXML private VBox hbGrid;
    @FXML private ComboBox<String> cbViti, cbArsyeja;
    @FXML private ImageView iv;
    @FXML private StackPane sp;
    private Circle circle = new Circle(95, 62, 62);
    @FXML private Label emri, gjinia, dtl, rruga, qyteti, shteti, dep, titulli, email, telefoni, pagesa, adresa;
    @FXML private DatePicker dpPrejPushim, dpDeriPushim;

    private BorderPane root;

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    private int id = 0;

    public void setId (int id) {
        this.id = id;
    }

    public int getId () {
        return this.id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grid.setHgap(1);
        grid.setVgap(1);
        grid.getStyleClass().add("shadow");
        grid.setStyle("-fx-border-width: 0; -fx-background-color: transparent");
        generateCalendar(LocalDate.now().getYear());
        fillCbViti();

        if (cbViti.getItems().size() == 0)
            cbViti.getSelectionModel().select(LocalDate.now().getYear());

        getData();
        hbGrid.getChildren().add(grid);

        dpPrejPushim.setConverter(VariablatPublike.converter);
        dpDeriPushim.setConverter(VariablatPublike.converter);

        dpPrejPushim.setValue(LocalDate.now());
        dpDeriPushim.setValue(LocalDate.now());

        dpPrejPushim.setOnAction(e -> {
            dpDeriPushim.setValue(dpPrejPushim.getValue());
        });

        btnRregullo.setOnAction(e -> {
            hapeRregullo(getId());
        });

        cbViti.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            generateCalendar(Integer.parseInt(nv));
            getData();
        });

        circle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0)");
        iv.setClip(circle);

    }

    private void fillCbViti (){
        try {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery("select distinct year(data_deri) as v from pushimet where pnt_id = " +  getId() + " order by v desc");

            while (rs.next()) {
                cbViti.getItems().add(rs.getString("v"));
            }

            cbViti.getSelectionModel().select(0);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapeRregullo(int index){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoPunetoret.fxml"));

            ShtoPunetoret sp = new ShtoPunetoret();
            sp.setId(index);
            sp.setMode('u');

            loader.setController(sp);
            Parent parent = loader.load();

            root.setCenter(parent);

        }catch (Exception e) { e.printStackTrace(); }
    }

    private void getData(){

        try {
            Statement stmt = con.createStatement();
            PreparedStatement pstmt = con.prepareStatement("select * from merrpunetoret where id = " + getId() + " limit 1");
            ResultSet rs = stmt.executeQuery("select data_prej as dp, data_deri as dd, arsyeja from pushimet where pnt_id = " +
                    getId());

            ResultSet r = pstmt.executeQuery();

            System.out.println(getId());

            iv.setImage(new Image("/sample/photo/punetoret/user.png"));

            r.next();
            emri.setText(r.getString("emri"));
            titulli.setText(r.getString("titulli") + ",");
            dep.setText(r.getString("departamenti"));
            pagesa.setText(r.getString("paga"));
            dtl.setText(VariablatPublike.sdf.format(r.getDate("ditelindja")));
            iv.setImage(new Image("file:" + r.getString("foto")));
            shteti.setText(r.getString("shteti"));
            qyteti.setText(r.getString("qyteti"));
            adresa.setText(r.getString("adresa"));
            gjinia.setText(r.getInt("gjinia") == 1 ? "Femer" : "Mashkull");
            telefoni.setText(r.getString("telefoni"));
            email.setText(r.getString("email"));
            crcStatusi.setStyle(r.getInt("statusi") == 1 ? "-fx-fill: -fx-green" : "-fx-fill: -fx-red");

            while (rs.next()) {
                Date d1 = sdf.parse(rs.getString("dp"));
                Date d2 = sdf.parse(rs.getString("dd"));
                getRect(d1, d2, rs.getString("arsyeja"));
            }
        }catch (Exception ex) { ex.printStackTrace(); }

    }

    private void getRect (Date d1, Date d2, String cls) throws ParseException {
        for (Node n : grid.getChildren()) {
            if (n instanceof Rectangle) {
                Date d = sdf.parse(n.getId());
                if (d.compareTo(d1) >= 0 && d2.compareTo(d) >= 0)
                    n.getStyleClass().add(cls);
            }
        }
    }

    private void generateCalendar(int viti) {
        grid.getChildren().clear();
        int dita = 0, muaji = 0, c = 0, d = -1;
        boolean leap = VariablatPublike.leapYear(viti);

        while (true) {
            while (dita < VariablatPublike.ditetMuajit(leap, muaji+1)) {
                dita++;
                d++;
                if (d % 7 == 0) {
                    c++;
                    d = 0;
                }

                Tooltip tooltip = new Tooltip((dita > 9 ? dita : "0" + dita) + "/" + (muaji + 1 < 10 ? "0" + (muaji + 1) : (muaji + 1)) + "/" + viti);
                Rectangle r = new Rectangle(15,15, Color.valueOf("rgba(255,255,255,.05)"));
                r.setId(viti+"-"+(muaji+1)+"-"+dita);
                r.getStyleClass().add("cal");

                r.setOnMouseEntered(e -> {
                    tooltip.show(r, e.getScreenX() - tooltip.getWidth() / 2, e.getScreenY() - 50);
                });

                r.setOnMouseExited(e -> {
                    tooltip.hide();
                });

                grid.add(r, c, d);
                if (muaji % 2 == 0) {
                    r.getStyleClass().add("darkerRect");
                }
            }
            if (muaji == 11) break;
            muaji++;
            dita = 0;
        }
    }

    @FXML
    private void shtoPushim (){
        try {
            if (!dpPrejPushim.getEditor().getText().isEmpty() && !dpDeriPushim.getEditor().getText().isEmpty() &&
                    dpPrejPushim.getValue().compareTo(dpDeriPushim.getValue()) <= 0) {
                String arsyeja = "";
                if (cbArsyeja.getSelectionModel().getSelectedItem().equals("Vjetor")) arsyeja = "vjetor";
                else if (cbArsyeja.getSelectionModel().getSelectedItem().equals("Semure")) arsyeja = "semure";
                else if (cbArsyeja.getSelectionModel().getSelectedItem().equals("Veqant")) arsyeja = "veqant";
                else if (cbArsyeja.getSelectionModel().getSelectedItem().equals("Pa arsye")) arsyeja = "parsye";
                else arsyeja = "ppages";
                PreparedStatement ps = con.prepareStatement("insert into pushimet values " +
                        "(?, ?, ?, ?)");

                ps.setInt(1, getId());
                ps.setDate(2, java.sql.Date.valueOf(dpPrejPushim.getValue()));
                ps.setDate(3, java.sql.Date.valueOf(dpDeriPushim.getValue()));
                ps.setString(4, arsyeja);
                ps.execute();
                MesazhetPublike.suksesDritarja("Pushimi u shtua me sukses");
                getData();
                fillCbViti();
            } else {
                Notifications.create().text("Data e fillimit duhet te jete me e vogel se ajo e perfundimit te pushimit").
                        position(Pos.TOP_CENTER).show();
            }
        }catch (Exception e) {e.printStackTrace();}
    }

}
