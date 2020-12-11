package id.net.gmedia.absensipsp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bayu on 27/02/2018.
 */

public class Profile extends Fragment {
    private Context context;
    private TextView nik, nama, noKTP, tempatLahir, tgl_lahir, agama, jenisKelamin, golonganDarah, telephone,
            email, alamat, status_menikah, bank, noRek, pendidikanTerakhir, namaSekolah, jurusan,
            thnLulus, noAbsen, cabang, divisi, bagian, jabatan, statusKaryawan, departmen, tgl_masuk, no_bpjsKS, no_bpjsTK;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.profile, viewGroup, false);
        context = getContext();

        AppLoading.getInstance().showLoading(context);

        nik = view.findViewById(id.net.gmedia.absensipsp.R.id.noNik);
        nama = view.findViewById(id.net.gmedia.absensipsp.R.id.namaProfile);
        noKTP = view.findViewById(id.net.gmedia.absensipsp.R.id.noKTP);
        tempatLahir = view.findViewById(id.net.gmedia.absensipsp.R.id.tempatLahir);
        tgl_lahir = view.findViewById(id.net.gmedia.absensipsp.R.id.tanggalLahir);
        agama = view.findViewById(id.net.gmedia.absensipsp.R.id.agama);
        jenisKelamin = view.findViewById(id.net.gmedia.absensipsp.R.id.jenisKelamin);
        golonganDarah = view.findViewById(id.net.gmedia.absensipsp.R.id.golDarah);
        telephone = view.findViewById(id.net.gmedia.absensipsp.R.id.noTelepon);
        email = view.findViewById(id.net.gmedia.absensipsp.R.id.email);
        alamat = view.findViewById(id.net.gmedia.absensipsp.R.id.alamatProfile);
        status_menikah = view.findViewById(id.net.gmedia.absensipsp.R.id.statusMenikah);
        bank = view.findViewById(id.net.gmedia.absensipsp.R.id.namaBank);
        noRek = view.findViewById(id.net.gmedia.absensipsp.R.id.noRekening);
        pendidikanTerakhir = view.findViewById(id.net.gmedia.absensipsp.R.id.pendTerakhir);
        namaSekolah = view.findViewById(id.net.gmedia.absensipsp.R.id.namaSekolah);
        jurusan = view.findViewById(id.net.gmedia.absensipsp.R.id.jurusan);
        thnLulus = view.findViewById(id.net.gmedia.absensipsp.R.id.thnLulus);
        noAbsen = view.findViewById(id.net.gmedia.absensipsp.R.id.noAbsen);
        cabang = view.findViewById(id.net.gmedia.absensipsp.R.id.cabang);
        divisi = view.findViewById(id.net.gmedia.absensipsp.R.id.divisi);
        bagian = view.findViewById(id.net.gmedia.absensipsp.R.id.bagian);
        jabatan = view.findViewById(id.net.gmedia.absensipsp.R.id.jabatan);
        statusKaryawan = view.findViewById(id.net.gmedia.absensipsp.R.id.statusKaryawan);
        departmen = view.findViewById(id.net.gmedia.absensipsp.R.id.departmen);
        tgl_masuk = view.findViewById(id.net.gmedia.absensipsp.R.id.tglMasuk);
        no_bpjsKS = view.findViewById(R.id.no_bpjsKS);
        no_bpjsTK = view.findViewById(R.id.no_bpjsTK);

        prepareDataProfile();
        return view;
    }

    private void prepareDataProfile() {
        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlProfile, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")){
                                JSONObject profile = object.getJSONObject("response");
                                nik.setText(profile.getString("nip"));
                                nama.setText(profile.getString("nama"));
                                noKTP.setText(profile.getString("no_ktp"));
                                tempatLahir.setText(profile.getString("tempat_lahir"));
                                tgl_lahir.setText(profile.getString("tgl_lahir"));
                                agama.setText(profile.getString("agama"));
                                jenisKelamin.setText(profile.getString("jenis_kelamin"));
                                golonganDarah.setText(profile.getString("golongan_darah"));
                                telephone.setText(profile.getString("telp"));
                                email.setText(profile.getString("email"));
                                alamat.setText(profile.getString("alamat"));
                                status_menikah.setText(profile.getString("status_menikah"));
                                bank.setText(profile.getString("nama_bank"));
                                noRek.setText(profile.getString("no_rekening"));
                                pendidikanTerakhir.setText(profile.getString("pendidikan"));
                                namaSekolah.setText(profile.getString("nama_sekolah"));
                                jurusan.setText(profile.getString("jurusan"));
                                thnLulus.setText(profile.getString("tahun_lulus"));
                                noAbsen.setText(profile.getString("pin"));
                                cabang.setText(profile.getString("cabang"));
                                divisi.setText(profile.getString("divisi"));
                                bagian.setText(profile.getString("bagian"));
                                jabatan.setText(profile.getString("jabatan"));
                                statusKaryawan.setText(profile.getString("status_karyawan"));
                                departmen.setText(profile.getString("departemen"));
                                tgl_masuk.setText(profile.getString("tgl_masuk"));
                                no_bpjsKS.setText(profile.getString("no_bpjsks"));
                                no_bpjsTK.setText(profile.getString("no_bpjstk"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Terjadi kesalahan koneksi", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }
}
