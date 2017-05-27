package sample.constructors;

import javafx.scene.control.TextField;

/**
 * Created by uran on 17-05-19.
 */
public class ShitjetProd {
    private String emri;
    private int id;
    private TextField sasia = new TextField("1");
    private double qmimi;

    public ShitjetProd (){}

    public ShitjetProd (int id, String emri, double qmimi) {
        this.emri = emri;
        this.id = id;
        this.qmimi = qmimi;
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

    public double getQmimi() {
        return qmimi;
    }

    public void setQmimi(double qmimi) {
        this.qmimi = qmimi;
    }

    public TextField getSasia() {
        return sasia;
    }

    public void setSasia(TextField sasia) {
        this.sasia = sasia;
    }
}
