package sample.constructors;

/**
 * Created by Uran on 18/03/2017.
 */
public class Punetori {
    private int id;
    private String emri, mbiemri, roli, dtl, statusi, tel, departamenti, puna, punesimi, pergjigjet, paga;

    public Punetori(int id, String emri, String puna, String dtl, String statusi, String tel, String departamenti,
                    String punesimi, String paga){
        this.id = id;
        this.dtl = dtl;
        this.emri = emri;
        this.statusi = statusi;
        this.departamenti = departamenti;
        this.tel = tel;
        this.puna = puna;
        this.punesimi = punesimi;
        this.pergjigjet = pergjigjet;
        this.paga = paga;
    }

    public String getPuna() {
        return puna;
    }

    public void setPuna(String puna) {
        this.puna = puna;
    }

    public String getPunesimi() {
        return punesimi;
    }

    public void setPunesimi(String punesimi) {
        this.punesimi = punesimi;
    }

    public String getPergjigjet() {
        return pergjigjet;
    }

    public void setPergjigjet(String pergjigjet) {
        this.pergjigjet = pergjigjet;
    }

    public String getPaga() {
        return paga;
    }

    public void setPaga(String paga) {
        this.paga = paga;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDepartamenti() {
        return departamenti;
    }

    public void setDepartamenti(String departamenti) {
        this.departamenti = departamenti;
    }

    public String getEmri() {
        return emri;
    }

    public void setEmri(String emri) {
        this.emri = emri;
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
