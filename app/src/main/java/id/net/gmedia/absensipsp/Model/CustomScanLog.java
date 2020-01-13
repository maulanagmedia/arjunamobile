package id.net.gmedia.absensipsp.Model;

public class CustomScanLog {
    private String tanggal, jam;
    private String type = "";

    public CustomScanLog(String tanggal, String jam) {
        this.tanggal=tanggal;
        this.jam=jam;
    }

    public CustomScanLog(String tanggal, String jam, String type) {
        this.tanggal=tanggal;
        this.jam=jam;
        this.type=type;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
