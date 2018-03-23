package sample.constructors;

public class ShpenzimetConstr {
    private int id;
    private String emri, arsyeja, data, shuma;

    public ShpenzimetConstr (int id, String emri, String arsyeja, String data, String shuma) {
        this.id = id;
        this.emri = emri;
        this.arsyeja = arsyeja;
        this.data = data;
        this.shuma = shuma;
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

    public String getArsyeja() {
        return arsyeja;
    }

    public void setArsyeja(String arsyeja) {
        this.arsyeja = arsyeja;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getShuma() {
        return shuma;
    }

    public void setShuma(String shuma) {
        this.shuma = shuma;
    }
}
