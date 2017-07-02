package sample.controller;

/**
 * Created by uran on 02/07/17.
 */
public class Server {
    public static void stopServer() {
        try {
            org.h2.tools.Server.shutdownTcpServer("tcp://127.0.1.1:9092", "", true, true);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public static void startServer() {
        try {
            org.h2.tools.Server server = org.h2.tools.Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
