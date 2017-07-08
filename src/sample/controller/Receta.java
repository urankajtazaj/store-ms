package sample.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by uran on 17-06-15.
 */
public class Receta {
    private String produkti;
    private double qmimi, sasia, pagesa, total = 0, zbritje = 0;
    private int tvsh = 0, len = 0;

    String[][] rec = new String[100][3];

    public void setPagesa(double pagesa) {
        this.pagesa = pagesa;
    }

    public void setTvsh(int tvsh) {
        this.tvsh = tvsh;
    }

    public void setData(String produkti, double sasia, double qmimi, double zbritje, int i) {
        this.produkti = produkti;
        this.qmimi = qmimi - (qmimi * zbritje);
        this.sasia = sasia;
        this.zbritje = zbritje;
        this.total += (qmimi - (qmimi * zbritje)) * sasia;
        rec[i][0] = produkti;
        rec[i][1] = VariablatPublike.decimal.format(sasia);
        rec[i][2] = (qmimi - (qmimi * zbritje))+"";
        len = i;
    }

    StringBuilder sb = new StringBuilder();

    public void krijoFaturen() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        sb.append(String.format("\n\t\t%s\n\n\t%s\n\n", VariablatPublike.emriShitores, sdf.format(date)));
        sb.append(String.format("%-22s%-6s %-9s\n------------------------------------\n", "Produkti", "Qmimi", "Total"));
        for (int i = 0; i <= len; i++) {
            sb.append(String.format("%-22s%-6.2f%.2f€\n", rec[i][0] + " x" + rec[i][1], Double.parseDouble(rec[i][2]), (Double.parseDouble(rec[i][1]) *
            Double.parseDouble(rec[i][2]))));
        }
        sb.append("\n------------------------------------\n");
        sb.append(String.format("%-22s%10.2f€\n%-18s%10d%%\n%-18s%10.2f€\n\n","Total pa tvsh:", total, "TVSH:", tvsh, "TOTAL:", total + (total * tvsh/100)));
        sb.append(String.format("%-22s%10.2f€\n%-18s%10.2f€", "Pagesa:", pagesa, "Kusuri:", pagesa - (total + (total * tvsh/100))));
        sb.append(String.format("\n\nPunetori: %s", VariablatPublike.uemri));
        sb.append("\n\n\t\tJU FALEMINDERIT");
        krijoFile(date);
    }

    public void krijoFile(Date date) {
        File smsf = new File(System.getProperty("user.home") + "/store-ms-files");
        smsf.mkdir();
        File fat = new File(smsf.getAbsolutePath() + "/Faturat");
        fat.mkdir();
        File file = new File(fat.getAbsolutePath() + "/" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(date)+".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        }catch (IOException io) {io.printStackTrace(); }
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(sb.toString());
        }catch (Exception e ) {e.printStackTrace(); }
    }

}
