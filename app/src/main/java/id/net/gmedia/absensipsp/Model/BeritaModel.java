package id.net.gmedia.absensipsp.Model;

/**
 * Created by Bayu on 24/01/2018.
 */

public class BeritaModel {
    private String id, judul, berita, tanggal, file;

    public BeritaModel(String id, String judul, String berita, String tanggal, String file) {
        this.id = id;
        this.judul = judul;
        this.berita = berita;
        this.tanggal = tanggal;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getBerita() {
        return berita;
    }

    public void setBerita(String berita) {
        this.berita = berita;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getFile() {
        return file;
    }
}
