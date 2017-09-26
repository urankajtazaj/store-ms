package sample.constructors;

/**
 * Created by Uran on 18/03/2017.
 */
public class Punetori {
    private int id;
    private String emri, roli, dtl, statusi, tel, departamenti, puna, punesimi, pergjigjet, paga, hyrat, adresa, qyteti, shteti, email, gjinia;

    public Punetori(int id, String emri, String departamenti, String paga, String dpns, String hyrat, String statusi) {
        this.id = id;
        this.emri = emri;
        this.departamenti = departamenti;
        this.paga = paga;
        this.punesimi = dpns;
        this.hyrat = hyrat;
        this.statusi = statusi;
    }

    public Punetori(int id, String emri, String departamenti, String paga, String hyrat, String statusi, String dtl, String tel, String punesimi, String adresa,
                    String qyteti, String shteti, String email, String gjinia){
        this.id = id;
        this.dtl = dtl;
        this.emri = emri;
        this.hyrat = hyrat;
        this.statusi = statusi;
        this.departamenti = departamenti;
        this.tel = tel;
        this.punesimi = punesimi;
        this.paga = paga;
        this.adresa = adresa;
        this.qyteti = qyteti;
        this.shteti = shteti;
        this.email = email;
        this.gjinia = gjinia;
    }

    public String getGjinia() {
        return gjinia;
    }

    public void setGjinia(String gjinia) {
        this.gjinia = gjinia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getQyteti() {
        return qyteti;
    }

    public void setQyteti(String qyteti) {
        this.qyteti = qyteti;
    }

    public String getShteti() {
        return shteti;
    }

    public void setShteti(String shteti) {
        this.shteti = shteti;
    }

    public String getHyrat() {
        return hyrat;
    }

    public void setHyrat(String hyrat) {
        this.hyrat = hyrat;
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
