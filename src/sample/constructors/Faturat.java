package sample.constructors;

/**
 * Created by uran on 17-06-02.
 */
public class Faturat {
    private int id;
    private String punetori, konsumatori, koha, cash;

    public Faturat (int id, String punetori, String konsumatori, String koha, String cash) {
        this.id = id;
        this.punetori = punetori;
        this.konsumatori = konsumatori;
        this.koha = koha;
        this.cash = cash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getPunetori() {
        return punetori;
    }

    public void setPunetori(String punetori) {
        this.punetori = punetori;
    }

    public String getKonsumatori() {
        return konsumatori;
    }

    public void setKonsumatori(String konsumatori) {
        this.konsumatori = konsumatori;
    }

    public String getKoha() {
        return koha;
    }

    public void setKoha(String koha) {
        this.koha = koha;
    }
}
