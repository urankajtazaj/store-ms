package sample.constructors;

/**
 * Created by uran on 17-04-20.
 */
public class Konsumatoret {

    private int id;
    private String emri, qyteti, shteti, adresa, tel, email;

    public Konsumatoret(int id, String emri, String email, String telefoni, String adresa, String qyteti, String shteti){
        this.id = id;
        this.email = email;
        this.emri = emri;
        this.qyteti = qyteti;
        this.adresa = adresa;
        this.tel = telefoni;
        this.shteti = shteti;
    }


    public String getShteti() {
        return shteti;
    }

    public void setShteti(String shteti) {
        this.shteti = shteti;
    }

    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}

    public String getEmri() {return this.emri;}
    public void setEmri(String emri) {this.emri = emri;}

    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}

    public String getQyteti() {return this.qyteti;}
    public void setQyteti(String qyteti) {this.qyteti = qyteti;}

    public String getAdresa() {return this.adresa;}
    public void setAdresa(String adresa) {this.adresa = adresa;}

    public String getTel() {return this.tel;}
    public void setTel(String tel) {this.tel = tel;}

}
