package sample.constructors;

/**
 * Created by uran on 17-06-02.
 */
public class FaturaDhenat {
    private String emri, qmimi, sasia, bc;

    public FaturaDhenat (String bc, String emri, String qmimi, String sasia) {
        this.bc = bc;
        this.emri = emri;
        this.qmimi = qmimi;
        this.sasia = sasia;
    }

    public String getBc() {
        return bc;
    }

    public void setBc(String bc) {
        this.bc = bc;
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

    public String getSasia() {
        return sasia;
    }

    public void setSasia(String sasia) {
        this.sasia = sasia;
    }
}
