package id.net.gmedia.absensipsp.Model;

/**
 * Created by Bayu on 21/02/2018.
 */

public class CustomHistoryIjinKeluarKantor {
    private String tanggal, alasan, status;
    private int durasi;

    public CustomHistoryIjinKeluarKantor(String tanggal, int durasi, String alasan, String status) {
        this.tanggal = tanggal;
        this.durasi = durasi;
        this.alasan = alasan;
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public int getDurasi() {
        return durasi;
    }



    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
