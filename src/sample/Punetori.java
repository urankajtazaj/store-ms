package sample;

/**
 * Created by Uran on 18/03/2017.
 */
public class Punetori {
    private String emri, mbiemri, roli, dtl, statusi;

    public Punetori() {
        this("", "", "", "", "");
    }

    public Punetori(String emri, String mbiemri, String roli, String dtl, String statusi){
        this.dtl = dtl;
        this.emri = emri;
        this.mbiemri = mbiemri;
        this.statusi = statusi;
        this.roli = roli;
    }

    public String getEmri() {
        return emri;
    }

    public void setEmri(String emri) {
        this.emri = emri;
    }

    public String getMbiemri() {
        return mbiemri;
    }

    public void setMbiemri(String mbiemri) {
        this.mbiemri = mbiemri;
    }

    public String getRoli() {
        return roli;
    }

    public void setRoli(String roli) {
        this.roli = roli;
    }

    public String getDtl() {
        return dtl;
    }

    public void setDtl(String dtl) {
        this.dtl = dtl;
    }

    public String getStatusi() {
        return statusi;
    }

    public void setStatusi(String statusi) {
        this.statusi = statusi;
    }
}
