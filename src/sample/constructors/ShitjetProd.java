package sample.constructors;

import javafx.scene.control.TextField;

/**
 * Created by uran on 17-05-19.
 */
public class ShitjetProd {
    private String emri, njesia, zbritje, qmimi, qmimiShum;
    private int id, shitja;
    private TextField sasia = new TextField("1"), qm = new TextField("");

    public ShitjetProd (){}

    public ShitjetProd (int id, String emri, String qmimi, String njesia, String zbritje, String qmimiShum) {
        this.emri = emri;
        this.id = id;
        this.qmimi = qmimi;
        this.njesia = njesia;
        this.zbritje = zbritje;
        this.qmimiShum = qmimiShum;
    }

    public int getShitja() {
        return shitja;
    }

    public void setShitja(int shitja) {
        this.shitja = shitja;
    }

    public String getQmimiShum() {
        return qmimiShum;
    }

    public void setQmimiShum(String qmimiShum) {
        this.qmimiShum = qmimiShum;
    }

    public String getZbritje() {
        return zbritje;
    }

    public void setZbritje(String zbritje) {
        this.zbritje = zbritje;
    }

    public String getNjesia() {
        return njesia;
    }

    public void setNjesia(String njesia) {
        this.njesia = njesia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmri() {
        return emri;
    }

    public void setEmri(String emri) {
        this.emri = emri;
    }

    public String getQmimi() {
        return qmimi;
    }

    public void setQmimi(String qmimi) {
        this.qmimi = qmimi;
    }

    public TextField getSasia() {
        return sasia;
    }

    public void setSasia(TextField sasia) {
        this.sasia = sasia;
    }

    public TextField getQm() {
        return qm;
    }

    public void setQm(TextField qm) {
        this.qm = qm;
    }
}
