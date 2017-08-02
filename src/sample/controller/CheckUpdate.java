package sample.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckUpdate {
    private String url = "https://api.github.com/repos/urankajtazaj/store-ms/releases/latest";

    public CheckUpdate() throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        for (String line; (line = br.readLine()) != null; ) {
            System.out.println(line.split(":")[0]);
        }

        br.close();

    }
}
