package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;
import sample.constructors.ShpenzimetConstr;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class Shpenzimet implements Initializable {

    Connection con = new DB().connect();

    @FXML
    private TableView<ShpenzimetConstr> tblShpenz;
    @FXML private TableColumn colDel, colEdit;
    @FXML private Label lblTotal;

    @FXML
    private ComboBox<String> cbPnt, cbMuaji, cbViti;

    Notification ntf = new Notification();

    ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tblShpenz.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        rb = resources;

        cbPnt.getItems().clear();
        cbPnt.getItems().add(rb.getString("te_gjitha"));
        cbPnt.getSelectionModel().select(0);

        colDel.setCellFactory(e -> {
            return new TableCell<ShpenzimetConstr, ShpenzimetConstr>() {
                @Override
                protected void updateItem(ShpenzimetConstr item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        Hyperlink hl = new Hyperlink("Del");
                        ShpenzimetConstr shp = tblShpenz.getItems().get(getIndex());
                        hl.setOnAction(e -> {
                            deleteShpenzim(shp.getId());
                        });
                        setGraphic(hl);
                    } else {
                        setText(null);
                        setGraphic(null);
                    }

                }
            };
        });

        colEdit.setCellFactory(e -> {
            return new TableCell<ShpenzimetConstr, ShpenzimetConstr>() {
                @Override
                protected void updateItem(ShpenzimetConstr item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        Hyperlink hl = new Hyperlink("Edit");
                        hl.setOnAction(e -> {
                            shtoShpenzim(((ShpenzimetConstr) tblShpenz.getItems().get(getIndex())).getId());
                        });
                        setGraphic(hl);
                    } else {
                        setGraphic(null);
                    }

                }
            };
        });

        cbMuaji.getItems().addAll(
                rb.getString("te_gjitha"),
                rb.getString("jan"),
                rb.getString("shk"),
                rb.getString("mrs"),
                rb.getString("prl"),
                rb.getString("maj"),
                rb.getString("qer"),
                rb.getString("korr"),
                rb.getString("gsht"),
                rb.getString("sht"),
                rb.getString("tet"),
                rb.getString("nen"),
                rb.getString("dhj")
            );

        cbMuaji.getSelectionModel().select(Integer.parseInt(new SimpleDateFormat("M").format(new Date())));
        merrPunetoret();
        getViti();

        merrShpenzimet();

    }

    @FXML
    private void raportoShpenzim() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JasperReport jr = JasperCompileManager.compileReport(System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/Shpenzimet.jrxml");
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("personi", cbPnt.getSelectionModel().getSelectedItem());
                    params.put("muaji", cbMuaji.getSelectionModel().getSelectedIndex());
                    params.put("viti", Integer.parseInt(cbViti.getSelectionModel().getSelectedItem()));

                    JasperPrint print = JasperFillManager.fillReport(jr, params, con);
                    String filename = System.getProperty("user.home") + "/store-ms-files/Raportet/PDF/Shpenzimet-" +
                            new SimpleDateFormat("dd-MM-yy h-m").format(new Date()) + ".pdf";
                    JasperExportManager.exportReportToPdfFile(print, filename);

                    Desktop.getDesktop().open(new File(filename));

                } catch (JRException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void raportoFitim() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JasperReport jr = JasperCompileManager.compileReport(System.getProperty("user.home") + "/store-ms-files/Raportet/raportet/Shitjet.jrxml");
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("pnt", cbPnt.getSelectionModel().getSelectedItem());
                    params.put("muaji", cbMuaji.getSelectionModel().getSelectedIndex());
                    params.put("viti", Integer.parseInt(cbViti.getSelectionModel().getSelectedItem()));

                    JasperPrint print = JasperFillManager.fillReport(jr, params, con);
                    String filename = System.getProperty("user.home") + "/store-ms-files/Raportet/PDF/Fitim-" +
                            new SimpleDateFormat("dd-MM-yy h-m").format(new Date()) + ".pdf";
                    JasperExportManager.exportReportToPdfFile(print, filename);

                    Desktop.getDesktop().open(new File(filename));

                } catch (JRException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void deleteShpenzim(int id) {
        try {
            Statement st = con.createStatement();
            ntf.setMessage("A jeni te sigurt qe deshironi ta fshini kete shpenzim?");
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.YES_NO);
            ntf.showAndWait();

            if (ntf.getDelete()) {
                st.execute("delete from shpenzimet where id = " + id);
                ntf.setMessage("Sukses");
                ntf.setType(NotificationType.SUCCESS);
                ntf.setButton(ButtonType.NO_BUTTON);
                ntf.show();
                merrShpenzimet();
            }
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void getViti() {
        try {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select distinct formatdatetime(data, 'yyyy') as viti from shpenzimet order by viti desc");

            cbViti.getItems().clear();
            int rows = 0;
            while (rs.next()) {
                rows++;
                cbViti.getItems().add(rs.getString("viti"));
            }
            if (rows > 0) {
                cbViti.getSelectionModel().select(0);
            } else {
                cbViti.getItems().add(new SimpleDateFormat("yyyy").format(new Date()));
                cbViti.getSelectionModel().select(0);
            }

            st.close();
            rs.close();

        }catch (Exception e ) { e.printStackTrace(); }
    }

    @FXML
    private void merrShpenzimet() {
        try {
            double total = 0;
            Statement st = con.createStatement();
            String sql = "select * from shpenzimet";

            if (cbPnt.getSelectionModel().getSelectedIndex() > 0) {
                sql += " where lower(personi) = lower('" + cbPnt.getSelectionModel().getSelectedItem() + "')";
            }

            if (cbMuaji.getSelectionModel().getSelectedIndex() > 0) {
                sql += (cbPnt.getSelectionModel().getSelectedIndex() > 0 ? " and " : " where ") + " month(data) = " + cbMuaji.getSelectionModel().getSelectedIndex();
            }

            ResultSet rs = st.executeQuery(sql);

            ObservableList<ShpenzimetConstr> data = FXCollections.observableArrayList();
            tblShpenz.getItems().clear();
            while (rs.next()) {
                data.add(new ShpenzimetConstr(rs.getInt("id"), rs.getString("personi"), rs.getString("arsyeja"), VariablatPublike.sdf.format(rs.getDate("data")),
                        VariablatPublike.toMoney(rs.getDouble("shuma"))));
                total += rs.getDouble("shuma");
            }

            cbPnt.getSelectionModel().select(0);
            tblShpenz.getItems().addAll(data);
            st.close();
            rs.close();
            lblTotal.setText(VariablatPublike.toMoney(total));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void merrPunetoret() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select distinct personi from shpenzimet order by personi");

            cbPnt.getItems().clear();
            cbPnt.getItems().add(rb.getString("te_gjitha"));
            while (rs.next()) {
                cbPnt.getItems().add(rs.getString("personi"));
            }

            cbPnt.getSelectionModel().select(0);

        }catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void shtoShpenzim() {
        Stage stage = new Stage();
        ShtoShpenzim shp = new ShtoShpenzim();
        shp.setStage(stage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoShpenzim.fxml"), rb);
        loader.setController(shp);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        shp.btnRuaj.setOnAction(e -> {
            if (!shp.txtArsyeja.getText().isEmpty() || !shp.txtShuma.getText().isEmpty() || !cbPnt.getEditor().getText().isEmpty() || !shp.dpData.getEditor().getText().isEmpty()) {
                double shuma;
                try {
                    shuma = Double.parseDouble(shp.txtShuma.getText());
                    PreparedStatement pst = con.prepareStatement("insert into shpenzimet values(null, ?, ?, ?, ?)");
                    pst.setDouble(1, shuma);
                    pst.setString(2, shp.txtArsyeja.getText());
                    pst.setDate(3, java.sql.Date.valueOf(shp.dpData.getValue()));
                    pst.setString(4, shp.cbPnt.getEditor().getText());
                    pst.execute();
                    stage.close();
                    merrShpenzimet();
                    getViti();
                    ntf.setMessage("Te dhenat u shtuan me sukses");
                    ntf.setType(NotificationType.SUCCESS);
                    ntf.setButton(ButtonType.NO_BUTTON);
                    ntf.show();
                } catch (NumberFormatException nfe) {
                    ntf.setMessage("Shuma duhet te jete numer");
                    ntf.setType(NotificationType.ERROR);
                    ntf.setButton(ButtonType.NO_BUTTON);
                    ntf.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(root, 300, 300);
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setIconified(false);
        stage.setScene(scene);
        stage.show();
    }

    private void shtoShpenzim(int id) {
        Stage stage = new Stage();
        ShtoShpenzim shp = new ShtoShpenzim();
        shp.setStage(stage);
        shp.setId(id);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gui/ShtoShpenzim.fxml"), rb);
        loader.setController(shp);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        shp.btnRuaj.setOnAction(e -> {
            if (!shp.txtArsyeja.getText().isEmpty() || !shp.txtShuma.getText().isEmpty() || !cbPnt.getEditor().getText().isEmpty() || !shp.dpData.getEditor().getText().isEmpty()) {
                double shuma;
                try {
                    shuma = Double.parseDouble(shp.txtShuma.getText());
                    String sql = null;
                    if (id > 0) {
                        sql = "update shpenzimet set shuma = ?, arsyeja = ?, data = ?, personi = ? where id = " + id;
                    } else {
                        sql = "insert into shpenzimet values(null, ?, ?, ?, ?)";
                    }
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setDouble(1, shuma);
                    pst.setString(2, shp.txtArsyeja.getText());
                    pst.setDate(3, java.sql.Date.valueOf(shp.dpData.getValue()));
                    pst.setString(4, shp.cbPnt.getEditor().getText());
                    pst.execute();
                    stage.close();
                    merrShpenzimet();
                    getViti();
                    ntf.setMessage("Te dhenat u shtuan me sukses");
                    ntf.setType(NotificationType.SUCCESS);
                    ntf.setButton(ButtonType.NO_BUTTON);
                    ntf.show();
                } catch (NumberFormatException nfe) {
                    ntf.setMessage("Shuma duhet te jete numer");
                    ntf.setType(NotificationType.ERROR);
                    ntf.setButton(ButtonType.NO_BUTTON);
                    ntf.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(root, 300, 300);
        scene.getStylesheets().add(getClass().getResource(VariablatPublike.styleSheet).toExternalForm());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setIconified(false);
        stage.setScene(scene);
        stage.show();
    }

}