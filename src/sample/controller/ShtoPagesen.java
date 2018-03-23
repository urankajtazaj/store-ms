package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ShtoPagesen implements Initializable {

    @FXML TextField txtPagesa;
    @FXML Button btnOk, btnCancel;

    private int id;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setId(int id) {
        this.id = id;
    }

    ResourceBundle rb;

    DB db = new DB();
    Connection con = db.connect();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;

        btnOk.setOnAction(e -> ok(id));

        txtPagesa.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                ok(id);
            }
        });

        btnCancel.setOnAction(e -> stage.close());

    }

    private void ok(int id) {
        if (!txtPagesa.getText().isEmpty()) {
            try {
                if (Integer.parseInt(txtPagesa.getText()) > 0) {
                    shtoPagesen(id);
                    stage.close();
                } else {
                    txtPagesa.clear();
                }
            } catch (NumberFormatException nfe) {
                Notification ntf = new Notification(rb.getString("rep_pagesa_format"));
                ntf.setType(NotificationType.ERROR);
                ntf.setButton(ButtonType.NO_BUTTON);
                ntf.show();
            }
        } else {
            stage.close();
        }
    }

    private void shtoPagesen(int id) {
        try {
            Statement st = con.createStatement();
            st.addBatch("update shitjet set shitjet.cash = shitjet.cash + " + Double.parseDouble(txtPagesa.getText().trim()) +
                    " where shitjet.rec_id = " + id);
            st.addBatch("update rec set rec.statusi = (case when (select shitjet.cash from shitjet where shitjet.rec_id = "+id+" limit 1) " +
                    ">= (select sum(shitjet.qmimi) from shitjet where shitjet.rec_id = "+id+") then rec.statusi = 0 else 1 end) where rec.rec_id = " + id);
            st.executeBatch();
            st.close();

            Notification ntf = new Notification(rb.getString("rep_pagesa_sukses"));
            ntf.setType(NotificationType.SUCCESS);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();
            txtPagesa.clear();

        } catch (NumberFormatException nfe) {
            Notification ntf = new Notification(rb.getString("rep_pagesa_format"));
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.show();
            txtPagesa.clear();
        } catch (Exception e) { e.printStackTrace(); }
    }

}
