package sample.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by uran on 17-04-28.
 */
public class DB {
    private static String db = "/~/Sistem Informacioni/databaza/db", server = "localhost";
    private static String url = "jdbc:h2:tcp://" + server + db;
    private static String usr = "HIDDEN";
    private static String pw = "HIDDEN";

    public Connection connect(){

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, usr, pw);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return con;
    }

}
