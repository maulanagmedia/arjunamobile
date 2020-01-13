package id.net.gmedia.absensipsp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.net.gmedia.absensipsp.Adapter.ListAdapterApproval;
import id.net.gmedia.absensipsp.Model.CustomApproval;

public class Approval extends Fragment {
    private Context context;
    private ListAdapterApproval adapterApproval;
    private ArrayList<CustomApproval> approval;
    private ListView listView;

    private Spinner dropdown;
    public static String tipe = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.approval, viewGroup, false);
        context = getContext();
        listView =  view.findViewById(R.id.listApprovalIjin);
        dropdown =  view.findViewById(R.id.dropdownApproval);
        String[] items = new String[]{"Jenis Approval: ", "Cuti", "Ijin Pulang Awal", "Ijin Meninggalkan Kerja", "Cuti Khusus", "Klarifikasi Absensi"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    dropdown.setAlpha(0.5f);
                    return false;
                } else {
                    dropdown.setAlpha(1);
                    return true;
                }
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.DKGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switchData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(getArguments() != null){
            String tipe = getArguments().getString(Constant.EXTRA_APPROVAL);
            if(tipe != null && !tipe.isEmpty()){
                switch (tipe){
                    case "cuti":switchData(1);break;
                    case "cuti_khusus":switchData(2);break;
                    case "pulang_awal":switchData(3);break;
                    case "meninggalkan_kerja":switchData(4);break;
                    case "klarifikasi":switchData(5);break;
                }
            }
        }

        return view;
    }

    private void switchData(int position){
        if(position != 0){
            if (position == 1) {
                tipe = "cuti";
            } else if (position == 2) {
                tipe = "pulang";
            } else if (position == 3) {
                tipe = "ijin_keluar";
            }
            else if(position == 4){
                tipe = "cuti_khusus";
            }
            else if(position == 5){
                tipe = "klarifikasi_absensi";
            }

            prepareData();
        }
    }

    private void prepareData() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("tipe", tipe);

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlApproval, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        listView.setAdapter(null);
                        dialog.dismiss();

                        approval = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isi = response.getJSONObject(i);
                                    CustomApproval apv;
                                    switch (tipe) {
                                        case "cuti":
                                            apv = new CustomApproval(
                                                    isi.getString("id"),
                                                    isi.getString("nama"),
                                                    isi.getString("tgl_awal") + " - "
                                                            + isi.getString("tgl_akhir"),
                                                    isi.getString("keterangan"),
                                                    isi.getString("alasan_cuti"), ""
                                            );
                                            approval.add(apv);
                                            break;
                                        case "pulang":
                                            apv = new CustomApproval(
                                                    isi.getString("id"),
                                                    isi.getString("nama"),
                                                    isi.getString("tgl"),
                                                    isi.getString("keterangan"),
                                                    isi.getString("alasan"),
                                                    isi.getString("jam")
                                            );
                                            apv.setUrl(isi.getString("file_bukti"));
                                            approval.add(apv);
                                            break;
                                        case "ijin_keluar":
                                            apv = new CustomApproval(
                                                    isi.getString("id"),
                                                    isi.getString("nama"),
                                                    isi.getString("tgl"),
                                                    isi.getString("keterangan"),
                                                    isi.getString("alasan"),
                                                    isi.getString("jmljam")
                                            );
                                            approval.add(apv);
                                            break;
                                        case "cuti_khusus":
                                            apv = new CustomApproval(
                                                    isi.getString("id"),
                                                    isi.getString("nama"),
                                                    isi.getString("tgl_awal") + " - "
                                                            + isi.getString("tgl_akhir"),
                                                    isi.getString("keterangan"),
                                                    isi.getString("alasan_cuti"), ""
                                            );
                                            apv.setUrl(isi.getString("file_bukti"));
                                            approval.add(apv);
                                            break;
                                        case "klarifikasi_absensi":
                                            apv = new CustomApproval(
                                                    isi.getString("id"),
                                                    isi.getString("nama"),
                                                    isi.getString("tgl"),
                                                    isi.getString("keterangan"),
                                                    isi.getString("alasan"), ""
                                            );
                                            apv.setUrl(isi.getString("file_bukti"));
                                            approval.add(apv);
                                            break;
                                    }
                                }

                                adapterApproval = new ListAdapterApproval(context, approval);
                                listView.setAdapter(adapterApproval);
                                listView.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }
}
