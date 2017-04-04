package sample;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;

/**
 * Created by Uran on 18/03/2017.
 */
public class Punet {
    private int id;
    private String sektori, fillimi, mbarimi, statusi;
    private Hyperlink menaxheri, puna;

    public Punet(){
        this(0, "", "", "", "", "", "");
    }

    public Punet(int id, String puna, String menaxheri, String sektori, String fillimi, String mbarimi, String statusi) {
        this.id = id;
        this.puna = new Hyperlink(puna);
        this.menaxheri = new Hyperlink(menaxheri);
        this.sektori = sektori;
        this.fillimi = fillimi;
        this.mbarimi = mbarimi;
        this.statusi = statusi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusi() {
        return statusi;
    }

    public void setStatusi(String statusi) {
        this.statusi = statusi;
    }

    public Hyperlink getPuna() {
        return puna;
    }

    public void setPuna(Hyperlink puna) {
        this.puna = puna;
    }

    public Hyperlink getMenaxheri() {
        return menaxheri;
    }

    public void setMenaxheri(Hyperlink menaxheri) {
        this.menaxheri = menaxheri;
    }

    public String getSektori() {
        return sektori;
    }

    public void setSektori(String sektori) {
        this.sektori = sektori;
    }

    public String getFillimi() {
        return fillimi;
    }

    public void setFillimi(String fillimi) {
        this.fillimi = fillimi;
    }

    public String getMbarimi() {
        return mbarimi;
    }

    public void setMbarimi(String mbarimi) {
        this.mbarimi = mbarimi;
    }
}
