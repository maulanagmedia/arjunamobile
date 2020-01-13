package id.net.gmedia.absensipsp.Model;

import java.util.Date;

public class MangkirModel {
    private String hari;
    private Date tanggal;
    private String jam_masuk;
    private String jam_pulang;
    private String keterangan;

    public MangkirModel(String hari, Date tanggal, String jam_masuk, String jam_pulang, String keterangan){
        this.hari = hari;
        this.tanggal = tanggal;
        this.jam_masuk = jam_masuk;
        this.jam_pulang = jam_pulang;
        this.keterangan = keterangan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public String getHari() {
        return hari;
    }

    public String getJam_masuk() {
        return jam_masuk;
    }

    public String getJam_pulang() {
        return jam_pulang;
    }
}
