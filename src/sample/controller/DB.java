package sample.controller;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by uran on 17-06-05.
 */
public class DB {
    private String server = "localhost", db = "Sistem Informacioni/databaza/db";
    private String conStr = "jdbc:h2:tcp://"+server+"/~/" + db;
    private String usr = "OOFJMPDRITAFRAETNFCSYWQUPXRRHQ";
    private String pw = "DfVxCqdKyMPz3yzYzQMHFCybn46q1iYS";

    public Connection connect () {
        Connection con = null;
        try {
            con = DriverManager.getConnection(conStr, usr, pw);
        }catch (Exception e) { e.printStackTrace(); }
        return con;
    }

}
