package id.net.gmedia.absensipsp.Model;

public class ModelFilterLembur {
    private String hari, nominal;
    private String type = "";

    public ModelFilterLembur(String hari, String nominal) {
        this.hari=hari;
        this.nominal=nominal;
    }

    public ModelFilterLembur(String hari, String nominal, String type) {
        this.hari=hari;
        this.nominal=nominal;
        this.type=type;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
