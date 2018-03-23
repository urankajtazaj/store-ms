package sample.controller;

import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

/**
 * Created by uran on 17-06-05.
 */
public class DB {
    private String db = "store-ms-files/Sistem Informacioni/databaza/db";
    private String conStr = "jdbc:h2:tcp://"+VariablatPublike.server+"/~/" + db;
    private String usr = "OOFJMPDRITAFRAETNFCSYWQUPXRRHQ";
    private String pw = "DfVxCqdKyMPz3yzYzQMHFCybn46q1iYS";

//    TODO: error_db
    Notification ntf = new Notification();

    public Connection connect () {
        Connection con = null;
        try {
            con = DriverManager.getConnection(conStr, usr, pw);
        }catch (Exception e) {
            ntf.setType(NotificationType.ERROR);
            ntf.setDuration(5);
            ntf.show();
        }
        return con;
    }

    public Connection connect (ResourceBundle rb) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(conStr, usr, pw);
        }catch (Exception e) {
            ntf.setType(NotificationType.ERROR);
            ntf.setMessage(rb.getString("error_db"));
            ntf.setDuration(5);
            ntf.show();
        }
        return con;
    }

}
