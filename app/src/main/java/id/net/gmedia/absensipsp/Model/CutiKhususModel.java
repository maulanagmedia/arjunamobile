package id.net.gmedia.absensipsp.Model;

import androidx.annotation.NonNull;

public class CutiKhususModel {
    private String id;
    private String nama;
    private boolean need_upload;

    public CutiKhususModel(String id, String nama, boolean need_upload){
        this.id = id;
        this.nama = nama;
        this.need_upload = need_upload;
    }

    public String getNama() {
        return nama;
    }

    public String getId() {
        return id;
    }

    public boolean isNeed_upload() {
        return need_upload;
    }

    @NonNull
    @Override
    public String toString() {
        return nama;
    }
}
