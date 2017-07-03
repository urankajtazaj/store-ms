package sample.controller;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Enums.*;
import sample.Enums.ButtonType;
import sample.constructors.Punetori;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/**
 * Created by Uran on 14/03/2017.
 */
public class Punetoret implements Initializable {
    @FXML private TableView<Punetori> tbl;
    @FXML private TableColumn colAct, sts;
    @FXML public GridPane gp;
    @FXML private Label lblTotalPnt, lblTotalM, lblMes, lblPntPsh, lblPntAktiv;
    @FXML public Button btnShtoPnt;
    @FXML private TextField txtId, txtEmri;
    @FXML private ComboBox<String> cbDep, cbStat;
    private ImageView iv;
    private RotateTransition transition;

    private String path = System.getProperty("user.home") + "/store-ms-files/Raportet/";

    public void setTransition (RotateTransition transition) {
        this.transition = transition;
    }

    DB db = new DB();
    Connection con = db.connect();

    Notification ntf = new Notification();

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat tf = new SimpleDateFormat("dd-MM-yyyy HH-mm-s");
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    BorderPane stage;

    public void setStage(BorderPane stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ShtoPunetoret sp = new ShtoPunetoret();
        cbDep.getItems().clear();
        cbDep.getItems().add("Te gjithe");
        try {
            sp.merrDeps("select * from departamenti", cbDep);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cbDep.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> filtroTabelen());
        cbStat.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> filtroTabelen());
        txtId.setOnKeyPressed(e -> {if (e.getCode().equals(KeyCode.ENTER)) filtroTabelen();});
        txtEmri.setOnKeyPressed(e -> {if (e.getCode().equals(KeyCode.ENTER)) filtroTabelen();});

        lidhuDb();
        colAct.setStyle("-fx-alignment: CENTER-RIGHT");

        colAct.setCellFactory(e -> {
            return new TableCell<Punetori, Punetori>() {

                Button btnDel = new Button();
                Button btnEd = new Button();
                HBox hBox = new HBox(btnDel, btnEd);

                @Override
                protected void updateItem(Punetori item, boolean empty) {
                    super.updateItem(item, empty);

                    hBox.setSpacing(7);
                    hBox.setAlignment(Pos.CENTER);
                    ImageView btIvDel = new ImageView(new Image("/sample/photo/trash.png"));
                    btIvDel.setFitWidth(15);
                    btIvDel.setPreserveRatio(true);
                    ImageView btIvEd = new ImageView(new Image("/sample/photo/settings.png"));
                    btIvEd.setFitWidth(15);
                    btIvEd.setPreserveRatio(true);
                    btnDel.setGraphic(btIvDel);
                    btnEd.setGraphic(btIvEd);

                    if (!empty) {
                    Punetori punetori = tbl.getItems().get(getIndex());

                        btnDel.setOnAction(e -> {
                            try {
                                dritarjaKonfirmo(punetori.getEmri(), punetori.getId(), getIndex());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        });

                        btnEd.setOnAction(e -> {
                            hapeRregullo(punetori.getId());
                        });

                        setGraphic(hBox);
                    }else {
                        setGraphic(null);
                    }

                }
            };
        });

        sts.setCellFactory(e -> {
            return new TableCell<String, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(item);
                    if (item != null && !empty) {
                        ImageView iv = new ImageView();
                        iv.setPreserveRatio(true);
                        iv.setFitWidth(15);
                        if (getText().equals("1")) {
                            iv.setImage(new Image("/sample/photo/green-flag.png"));
                            setGraphic(iv);
                            setText("");
                        }
                        else {
                            iv.setImage(new Image("/sample/photo/red-flag.png"));
                            setGraphic(iv);
                            setText("");
                        }

                    }else {
                        setGraphic(null);
                        setText(null);
                        setStyle(null);
                    }

                }
            };
        });

        tbl.setPlaceholder(new Label("Nuk ka te dhena"));
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        fillTable();
    }

    private void dritarjaKonfirmo(String emri, int id, int index) throws Exception{
        ntf.setType(NotificationType.ERROR);
        ntf.setMessage("A jeni te sigurte qe deshironi ta fshini " + emri + " nga lista e punetoreve?\nKy veprim nuk mund te kthehet!");
        ntf.setButton(ButtonType.YES_NO);
        ntf.showAndWait();

        if (ntf.getDelete()) {
            fshiPunetorin(id);
            tbl.getItems().remove(index);
        }
    }

    private void fshiPunetorin(int id) {
        try (Statement st = con.createStatement()) {
            st.addBatch("delete from punetoret where id = " + id);
            st.addBatch("delete from perdoruesi where pnt_id = " + id);
            st.addBatch("delete from pushimet where pnt_id = " + id);
            st.executeBatch();
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setMessage("Punetori u fshi me sukses");
            ntf.show();
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void fillTable(){

        double paga = 0;
        int totalPnt = tbl.getItems().size();
        lblTotalPnt.setText(totalPnt+"");

        int psh = 0;

        for (int i = 0; i < tbl.getItems().size(); i++) {
            String s = ""+tbl.getColumns().get(4).getCellData(i);
            paga += Double.parseDouble(s.substring(0, s.length()-1));
            psh += tbl.getColumns().get(6).getCellData(i).equals("1") ? 0 : 1;
        }

        if (totalPnt > 0) {
            lblTotalM.setText(VariablatPublike.decimalFormat.format(paga));
            lblMes.setText(VariablatPublike.decimalFormat.format(paga / totalPnt));
            lblPntAktiv.setText((totalPnt - psh) + "");
            lblPntPsh.setText(psh + "");
        }
    }

    private void hapeRregullo(int index){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/PunetoretView.fxml"));

            PunetoretView pv = new PunetoretView();
            pv.setId(index);
            pv.setRoot(stage);

            loader.setController(pv);
            Parent parent = loader.load();
            ScrollPane sp = new ScrollPane(parent);
            sp.getStyleClass().add("edge-to-edge");
            stage.setCenter(sp);

        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void eksporto(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/export.fxml"));
        Parent bpExport = null;
        try {
            bpExport = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Export export = loader.getController();
        Stage stage = new Stage();

        export.btnAnulo.setOnAction(e -> {
            stage.close();
        });

        export.btnExcel.setOnAction(e -> {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    iv.setImage(VariablatPublike.spinning);
                    transition.play();
                    excelFile("Punëtorët", "xlsx", keySet());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            VariablatPublike.stopSpinning(transition, iv);
                            ntf.setMessage("Fajlli u krijua me sukses!");
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.show();
                        }
                    });
                }
            });
            t.setDaemon(true);
            t.start();
            stage.close();
        });

        export.btnSql.setOnAction(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    toSql();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ntf.setMessage("Dokumenti u eksportua me sukses");
                            ntf.setButton(ButtonType.NO_BUTTON);
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.show();
                        }
                    });
                }
            }).start();
            stage.close();
        });

        export.btnCsv.setOnAction(e -> {
            iv.setImage(VariablatPublike.spinning);
            transition.play();
            createFile(stage, "csv");
            VariablatPublike.stopSpinning(transition, iv);
        });

        export.btnPdf.setOnAction(e -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    iv.setImage(VariablatPublike.spinning);
                    transition.play();
                    jasperFile();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            VariablatPublike.stopSpinning(transition, iv);
                            ntf.setMessage("Fajli u exportua me sukses");
                            ntf.setType(NotificationType.SUCCESS);
                            ntf.show();
                        }
                    });
                }
            }).start();
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

    private void toSql(){
        File file = new File(path + "SQL/Punetoret_" + tf.format(new Date()) + ".sql");
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {

            for (Punetori p : tbl.getItems()){
                bw.write("merge punetoret key(id) values (" + p.getId() + "," + VariablatPublike.dep.get(p.getDepartamenti()) + ",'" +
                p.getEmri().split(" ")[0] + "','" + p.getEmri().split(" ")[1] + "'," + (p.getGjinia().equals("Femer") ? 1 : 0) + ",'" + p.getDtl() + "'," +
                p.getPaga().substring(0, p.getPaga().length()-1) + ",'" + p.getPunesimi() + "',''," + (p.getStatusi().equals("Aktiv") ? 1 : 0) + ",'current_timestamp()','" +
                p.getTel() + "','" + p.getEmail() + "','" + p.getQyteti() + "','" + p.getShteti() + "', '')");
            }

        }catch (Exception e) {e.printStackTrace();}
    }

    //    CSV FILE
    public void createFile(Stage stage, String extension) {
//        EMRI I FAJLLIT
        String path = System.getProperty("user.home") + "/store-ms-files/Raportet/CSV/";
        String fileName = fileNaming(path + "Punetoret " + tf.format(new Date()), extension);

//        KRIJO FILE TE RI
        File file = new File(fileNaming(fileName, extension));

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            System.out.println("Fajlli nuk u gjet: " + file.getName());
        }

//        SHKRUAJ TE DHENAT
        StringBuilder stringBuilder = new StringBuilder("");
        for (Punetori p : tbl.getItems()) {
            stringBuilder.append(p.getId() + ", " + p.getEmri() + ", " + p.getDepartamenti() + ", " + p.getPaga() + ", " +
                    p.getHyrat() + ", " + p.getStatusi() + "\n\r");
        }

//        VENDOSI NE FILE
        try {
            bw.write(stringBuilder.toString());
            bw.close();
            ntf.setType(NotificationType.SUCCESS);
            ntf.setMessage("Fajlli u krijua me sukses dhe mund te gjinded ne\n" + file.getAbsolutePath());
            ntf.show();
            stage.close();
        } catch (IOException e) {
            System.out.println("Fajlli nuk mund te mbyllet: " + file.getName());
        }
    }

    //    EXCEL FILE
    public void excelFile(String fileName, String extension, Map<String, Object[]> map){

//        KRIJOHET NJE FILE I RI I EXCEL-IT
        XSSFWorkbook workbook = new XSSFWorkbook();

//        KRIJOHET FLETA E RE E EXCELIT
        XSSFSheet sheet = workbook.createSheet();

        Set<String> keySet = map.keySet();
        int r = 0;
        for (String s : keySet) {
            Row row = sheet.createRow(r++);
            Object[] obj = map.get(s);
            int c = 0;

            for (Object object : obj) {
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(c++);

                if (object instanceof String) {
                    cell.setCellValue((String) object);
                } else if (object instanceof Integer) {
                    cell.setCellValue((Integer) object);
                }
            }
        }

        String path = System.getProperty("user.home") + "/store-ms-files/Raportet/EXCEL/";

        File file = new File(fileNaming(path + fileName + " " + tf.format(new Date()), extension));

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Map<String, Object[]> keySet (){
        Map<String, Object[]> xlsData = new TreeMap<>();
        int i = 1;
        double total = 0;
        xlsData.put((i++)+"", new Object[] {"ID", "EMRI", "GJINIA", "PUNA", "PAGA", "TË HYRAT", "DITËLINDJA", "STATUSI", "ADRESA", "QYTETI", "SHTETI", "EMAIL"});
        for (Punetori p : tbl.getItems()) {
            xlsData.put((i++)+"", new Object[] {p.getId(), p.getEmri(), p.getGjinia(), p.getDepartamenti(), p.getPaga(), p.getHyrat(),
                    p.getDtl(), p.getStatusi().equals("1") ? "Aktiv" : "Jo aktiv", p.getAdresa(), p.getQyteti(), p.getShteti(), p.getEmail()});
            total += Double.parseDouble(p.getPaga().substring(0, p.getPaga().length()-1));
        }
        xlsData.put((i++) + "", new Object[] {}); //RRESHT I ZBRAZET
        xlsData.put((i++) + "", new Object[] {"Punëtorë", i-4});
        xlsData.put((i) + "", new Object[] {"Shpenzime", total + ""});
        return xlsData;
    }

    //    EMERTIMI I RREGULLT I FILE
    public static String fileNaming(String path, String extension){
        if (!path.substring(path.length()-extension.length()-1, path.length()).equals("."+extension)) {
            return path.substring(0, path.length())+"."+extension;
        }
        return path;
    }

    // PDF FILE
    private void jasperFile(){
        try {
            String path = System.getProperty("user.home") + "/store-ms-files/Raportet/";
            JasperReport jasperReport = JasperCompileManager.compileReport(path + "raportet/Punetoret.jrxml");
            Map<String, Object> params = new HashMap();
            params.put("Punetori", VariablatPublike.uemri);

            JasperPrint jprint = JasperFillManager.fillReport(jasperReport, params, con);

            JasperExportManager.exportReportToPdfFile(jprint, path + "PDF/" + tf.format(new Date()) + ".pdf");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lidhuDb(){
        try {
            PreparedStatement ps = con.prepareStatement("select * from merrpunetoret order by id");
            ResultSet rs = ps.executeQuery();

            ObservableList<Punetori> data = FXCollections.observableArrayList();
            while (rs.next()) {

                data.add(new Punetori(rs.getInt("id"), rs.getString("emri"), rs.getString("departamenti"), VariablatPublike.decimalFormat.format(rs.getDouble("paga")),
                        VariablatPublike.decimalFormat.format(rs.getDouble("hyrat")), rs.getString("statusi"),
                        VariablatPublike.sdf.format(rs.getDate("ditelindja")), rs.getString("telefoni"), VariablatPublike.sdf.format(rs.getDate("data_punesimit")),
                        rs.getString("adresa"), rs.getString("qyteti"), rs.getString("shteti"), rs.getString("email"), rs.getInt("gjinia") == 0 ? "Mashkull" : "Femer"));

            }
            tbl.getItems().addAll(data);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<String> merrTeDhenatPerRregullim(int id){
        List<String> list = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from punetoret where id = " + id + " limit 1");

            while (rs.next()) {
                list.add(rs.getString("emri"));
                list.add(rs.getString("mbiemri"));
                list.add(rs.getString("dep_id"));
                list.add(rs.getString("gjinia"));
                list.add(rs.getString("statusi"));
                list.add(rs.getString("data_punesimit"));
                list.add(rs.getString("titulli"));
                list.add(rs.getString("ditelindja"));
                list.add(""+rs.getInt("paga"));
                list.add(rs.getString("telefoni"));
                list.add(""+rs.getInt("id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @FXML
    private void filtroTabelen(){
        String q = "select * from merrpunetoret where id "+ (txtId.getText().isEmpty() ? "> 0" : "= " + txtId.getText()) + " and lower(emri) like " +
                "lower('%"+txtEmri.getText()+"%') and departamenti " + (cbDep.getSelectionModel().getSelectedIndex() == 0 ? "like '%%'" : "= '" +
                cbDep.getSelectionModel().getSelectedItem() + "'") +
                " and statusi " +
                (cbStat.getSelectionModel().getSelectedIndex() == 0 ? ">= 0" : "= " + (cbStat.getSelectionModel().getSelectedIndex() == 1 ? "1" : "0"));
        try (PreparedStatement ps = con.prepareStatement(q)) {

            ResultSet rs = ps.executeQuery();

            tbl.getItems().clear();
            while (rs.next()) {
                tbl.getItems().add(new Punetori(rs.getInt("id"), rs.getString("emri"), rs.getString("departamenti"), VariablatPublike.decimalFormat.format(rs.getDouble("paga")),
                        VariablatPublike.sdf.format(rs.getDate("data_punesimit")), VariablatPublike.decimalFormat.format(rs.getDouble("hyrat")), rs.getString("statusi")));
            }
        }catch (Exception e) { e.printStackTrace(); }
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }
}
