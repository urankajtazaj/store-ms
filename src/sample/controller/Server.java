package sample.controller;

import sample.Enums.ButtonType;
import sample.Enums.NotificationType;

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
        } catch (Exception ex) {
            Notification ntf = new Notification("Ju lutem mbyllini te gjitha dritaret tjera te ketij programi dhe provoni perseri.");
            ntf.setType(NotificationType.ERROR);
            ntf.setButton(ButtonType.NO_BUTTON);
            ntf.setDuration(4);
            ntf.show();
        }
    }
}
