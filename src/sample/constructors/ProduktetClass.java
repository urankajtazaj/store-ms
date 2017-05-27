package sample.constructors;

import javafx.scene.control.Label;

/**
 * Created by uran on 17-04-07.
 */
public class ProduktetClass {
    private String emri, prodhuesi, kategoria, qmimi, furnizuesi;
    private int id, sasia;

    public ProduktetClass(int id, String emri, String prodhuesi, String kategoria, String qmimi, int sasia) {
        this.emri = emri;
        this.id = id;
        this.prodhuesi = prodhuesi;
        this.kategoria = kategoria;
        this.qmimi = qmimi;
        this.sasia = sasia;
    }

    public ProduktetClass(int id, String emri, String prodhuesi, String kategoria, String furnizuesi, String qmimi, int sasia) {
        this.emri = emri;
        this.id = id;
        this.prodhuesi = prodhuesi;
        this.kategoria = kategoria;
        this.qmimi = qmimi;
        this.sasia = sasia;
        this.furnizuesi = furnizuesi;
    }

    public String getFurnizuesi() {
        return furnizuesi;
    }

    public void setFurnizuesi(String furnizuesi) {
        this.furnizuesi = furnizuesi;
    }

    public String getEmri() {
        return emri;
    }

    public void setEmri(String emri) {
        this.emri = emri;
    }

    public String getProdhuesi() {
        return prodhuesi;
    }

    public void setProdhuesi(String prodhuesi) {
        this.prodhuesi = prodhuesi;
    }

    public String getKategoria() {
        return kategoria;
    }

    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }

    public String getQmimi() {
        return qmimi;
    }

    public void setQmimi(String qmimi) {
        this.qmimi = qmimi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSasia() {
        return sasia;
    }

    public void setSasia(int sasia) {
        this.sasia = sasia;
    }
}
