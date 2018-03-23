package sample.constructors;

/**
 * Created by uran on 17-06-02.
 */
public class Faturat {
    private int id;
    private String punetori, konsumatori, koha, data, qmimi;
    private boolean statusi, lloji, anuluar;

    public Faturat (int id, String punetori, String konsumatori, boolean lloji, boolean statusi, String data, String qmimi) {
        this.statusi = statusi;
        this.id = id;
        this.punetori = punetori;
        this.konsumatori = konsumatori;
        this.lloji = lloji;
        this.data = data;
        this.qmimi = qmimi;
    }

    public Faturat (int id, String punetori, String konsumatori, boolean lloji, boolean statusi, String data, String qmimi, boolean anuluar) {
        this.statusi = statusi;
        this.id = id;
        this.punetori = punetori;
        this.konsumatori = konsumatori;
        this.lloji = lloji;
        this.data = data;
        this.qmimi = qmimi;
        this.anuluar = anuluar;
    }

    public boolean isAnuluar() {
        return anuluar;
    }

    public void setAnuluar(boolean anuluar) {
        this.anuluar = anuluar;
    }

    public String getQmimi() {
        return qmimi;
    }

    public void setQmimi(String qmimi) {
        this.qmimi = qmimi;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isLloji() {
        return lloji;
    }

    public void setLloji(boolean lloji) {
        this.lloji = lloji;
    }

    public boolean getStatusi() {return statusi; };

    public void setStatusi(boolean statusi) {
        this.statusi = statusi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
