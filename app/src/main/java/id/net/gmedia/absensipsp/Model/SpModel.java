package id.net.gmedia.absensipsp.Model;

import java.util.Date;

public class SpModel {
    private Date date_terbit;
    private Date date_berlaku;
    private String status;
    private String jenis;
    private String terbit;
    private String alasan;

    public SpModel(Date date_terbit, Date date_berlaku, String status, String jenis, String terbit, String alasan){
        this.date_berlaku = date_berlaku;
        this.date_terbit = date_terbit;
        this.status = status;
        this.jenis = jenis;
        this.terbit = terbit;
        this.alasan = alasan;
    }

    public String getJenis() {
        return jenis;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate_berlaku() {
        return date_berlaku;
    }

    public Date getDate_terbit() {
        return date_terbit;
    }

    public String getAlasan() {
        return alasan;
    }

    public String getTerbit() {
        return terbit;
    }
}
