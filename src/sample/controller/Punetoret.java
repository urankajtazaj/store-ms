package sample.controller;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.constructors.Punetori;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    DB db = new DB();
    Connection con = db.connect();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    BorderPane stage;

    public void setStage(BorderPane stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
                        if (getText().equals("1")) {
                            setStyle("-fx-alignment: CENTER; -fx-background-color: -fx-green; -fx-font-size: 14; -fx-text-fill: #fff; -fx-font-weight: bold;");
                            setText("Aktiv");
                        }
                        else {
                            setStyle("-fx-alignment: CENTER; -fx-text-fill: #fff; -fx-font-size: 14; -fx-background-color: -fx-red; -fx-font-weight: bold;");
                            setText("Jo aktiv");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/konfirmo.fxml"));
        Parent parent = loader.load();
        Konfirmo konfirmo = loader.getController();

        konfirmo.setId(id);
        konfirmo.setTabela(1); /*1 = PUNETORET, 2 = KONSUMATORET, 3 = PRODUKTET*/
        konfirmo.setMessage("Konfirmo fshirjen e '" + emri + "' nga lista e punetoreve.");

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

    private void fillTable(){

        double paga = 0;
        int totalPnt = tbl.getItems().size();
        lblTotalPnt.setText(totalPnt+"");

        int psh = 0;

        for (int i = 0; i < tbl.getItems().size(); i++) {
            paga += Double.parseDouble(""+tbl.getColumns().get(5).getCellData(i));
            psh += tbl.getColumns().get(7).getCellData(i).equals("1") ? 0 : 1;
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
            excelFile(stage, "xls");
        });

        export.btnCsv.setOnAction(e -> {
            createFile(stage, "csv");
        });

        Scene scene = new Scene(bpExport, 400, 165);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/sample/style/style.css").toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    //    CSV FILE
    public void createFile(Stage stage, String extension) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ruaj");
        fileChooser.setInitialFileName(new Date()+"");

//        EMRI I FAJLLIT
        String fileName = fileChooser.showSaveDialog(stage).getAbsolutePath();

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
            stringBuilder.append(p.getEmri() + ", " + p.getDepartamenti() + ", " + p.getPuna() + ", " + p.getPergjigjet() + ", "
                    + p.getDtl() + ", " + p.getTel() + ", " + p.getPaga() + "\n");
        }

//        VENDOSI NE FILE
        try {
            bw.write(stringBuilder.toString());
            bw.close();
            MesazhetPublike.suksesDritarja("Fajlli u krijua me sukses dhe mund te gjinded ne\n" + file.getAbsolutePath());
            stage.close();
        } catch (IOException e) {
            System.out.println("Fajlli nuk mund te mbyllet: " + file.getName());
        }
    }

    //    EXCEL FILE
    public void excelFile(Stage stage, String extension){

//        KRIJOHET NJE FILE I RI I EXCEL-IT
        System.out.print("Duke krijuar excel file te ri");
        XSSFWorkbook workbook = new XSSFWorkbook();
        System.out.println("..OK");

//        KRIJOHET FLETA E RE E EXCELIT
        System.out.print("Duke krijuar fleten");
        XSSFSheet sheet = workbook.createSheet();
        System.out.println("..OK");

//        MERREN TE DHENAT DHE VENDOSEN NE MAP
        System.out.print("Duke nxjerre te dhenat nga programi");
        Map<String, Object[]> xlsData = new TreeMap<>();
        int i = 1;
        for (Punetori p : tbl.getItems()) {
            xlsData.put((i++)+"", new Object[] {p.getId(), p.getEmri(), p.getPuna(), p.getPaga(), p.getDepartamenti()});
        }
        System.out.println("..OK");

        System.out.print("Duke shkruajtur ne excel file");
        Set<String> keySet = xlsData.keySet();
        int r = 0;
        for (String s : keySet) {
            Row row = sheet.createRow(r++);
            Object[] obj = xlsData.get(s);
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
        System.out.println("..OK");


        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(new Date()+"");
        fileChooser.setTitle("Ruaj");

        String path = fileChooser.showSaveDialog(stage).getAbsolutePath();

        File file = new File(fileNaming(path, extension));

        System.out.println("File u krijua: " + file.getAbsolutePath());
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            MesazhetPublike.suksesDritarja("Fajlli u krijua me sukses dhe mund te gjinded ne\n" + file.getAbsolutePath());
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //    EMERTIMI I RREGULLT I FILE
    public static String fileNaming(String path, String extension){
        if (!path.substring(path.length()-extension.length()-1, path.length()).equals("."+extension)) {
            return path.substring(0, path.length())+"."+extension;
        }
        return path;
    }

    private void lidhuDb(){
        try {
            PreparedStatement ps = con.prepareStatement("select * from merrpunetoret order by id");
            ResultSet rs = ps.executeQuery();

            ObservableList<Punetori> data = FXCollections.observableArrayList();
            while (rs.next()) {
                String d = format.format(rs.getDate("ditelindja"));
                String d2 = format.format(rs.getDate("data_punesimit"));

                data.add(new Punetori(rs.getInt("id"), rs.getString("emri"),
                        rs.getString("titulli"), d, rs.getInt("statusi")+"", rs.getString("telefoni"),
                        rs.getString("departamenti"), d2, rs.getInt("paga")+""));
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
}
