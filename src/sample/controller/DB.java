package sample.controller;

import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by uran on 17-06-05.
 */
public class DB {
    private String db = "Sistem Informacioni/databaza/db";
    private String conStr = "jdbc:h2:tcp://"+VariablatPublike.server+"/~/" + db;
    private String usr = "OOFJMPDRITAFRAETNFCSYWQUPXRRHQ";
    private String pw = "DfVxCqdKyMPz3yzYzQMHFCybn46q1iYS";

    Notification ntf = new Notification("Nuk mund te behet lidhja me Databaze, kontrollo serverin.");

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

}
