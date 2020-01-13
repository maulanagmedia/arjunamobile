package id.net.gmedia.absensipsp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.absensipsp.Model.CutiKhususModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CutiKhusus extends Fragment {

    private Activity activity;
    private List<CutiKhususModel> listTipe = new ArrayList<>();
    private ArrayAdapter<CutiKhususModel> adapter;
    private Spinner spn_jenis;
    private View layout_upload;

    private TextView txt_tanggal_mulai, txt_tanggal_selesai,
            txt_file, txt_sisa_cuti;

    private String tanggal_mulai = "";
    private String tanggal_selesai = "";
    private String upload_file = "";

    public CutiKhusus() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_cuti_khusus, container, false);

        txt_file = v.findViewById(R.id.txt_file);
        txt_tanggal_mulai = v.findViewById(R.id.txt_tanggal_mulai);
        txt_tanggal_selesai = v.findViewById(R.id.txt_tanggal_selesai);
        layout_upload = v.findViewById(R.id.layout_upload);
        spn_jenis = v.findViewById(R.id.spn_jenis);
        txt_sisa_cuti = v.findViewById(R.id.txt_sisa_cuti);

        initTipe();
        adapter = new ArrayAdapter<CutiKhususModel>(activity, android.R.layout.simple_spinner_item, listTipe) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    spn_jenis.setAlpha(0.5f);
                    return false;
                } else {
                    spn_jenis.setAlpha(1);
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
        spn_jenis.setAdapter(adapter);
        spn_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listTipe.get(position).isNeed_upload()){
                    layout_upload.setVisibility(View.VISIBLE);
                }
                else{
                    layout_upload.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        v.findViewById(R.id.layout_tanggal_mulai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(activity, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        String date = Converter.DToStringReversed(Converter.stringDToDate(dateString));
                        txt_tanggal_mulai.setText(date);
                        tanggal_mulai = date;
                    }
                });
            }
        });

        v.findViewById(R.id.layout_tanggal_selesai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(activity, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        String date = Converter.DToStringReversed(Converter.stringDToDate(dateString));
                        txt_tanggal_selesai.setText(date);
                        tanggal_selesai = date;
                    }
                });
            }
        });

        v.findViewById(R.id.layout_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start((FragmentActivity) activity, ((MainActivity)activity).UPLOAD_CODE, 1);
            }
        });

        v.findViewById(R.id.btn_riwayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, HistoryCuti.class);
                i.putExtra(Constant.EXTRA_FLAG_CUTI_KHUSUS, true);
                activity.startActivity(i);
            }
        });

        v.findViewById(R.id.btn_kirim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn_jenis.getSelectedItemPosition() == 0){
                    Toast.makeText(activity, "Silahkan pilih jenis cuti anda", Toast.LENGTH_SHORT).show();
                }
                else if(tanggal_mulai.isEmpty()){
                    Toast.makeText(activity, "Input tanggal awal cuti anda", Toast.LENGTH_SHORT).show();
                }
                else if(tanggal_selesai.isEmpty()){
                    Toast.makeText(activity, "Input tanggal akhir cuti anda", Toast.LENGTH_SHORT).show();
                }
                else if(listTipe.get(spn_jenis.getSelectedItemPosition()).isNeed_upload() &&
                    upload_file.isEmpty()){
                    Toast.makeText(activity, "Upload file bukti cuti anda", Toast.LENGTH_SHORT).show();
                }
                else{
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.popup_konfirmasi);

                    TextView txt_konfirmasi = dialog.findViewById(R.id.txt_konfirmasi);
                    txt_konfirmasi.setText("Yakin ingin mengajukan cuti?");

                    Button ya = dialog.findViewById(id.net.gmedia.absensipsp.R.id.yacheckout);
                    ya.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            ajukanCuti();
                        }
                    });
                    Button tidak = dialog.findViewById(id.net.gmedia.absensipsp.R.id.tidakcheckout);
                    tidak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    if(dialog.getWindow() != null){
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });

        loadMasterCutiKhusus();
        sisaCuti();

        return v;
    }

    void prepareBitmap(String path, Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        txt_file.setText(path);
        upload_file = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void initTipe(){
        listTipe.clear();
        listTipe.add(new CutiKhususModel("0", "Jenis Cuti Khusus : ", false));
    }

    private void sisaCuti() {
        AppLoading.getInstance().showLoading(activity);
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlSisaCuti, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(activity), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONObject isi = object.getJSONObject("response");
                                txt_sisa_cuti.setText(isi.getString("sisa_cuti"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }

    private void ajukanCuti(){
        AppLoading.getInstance().showLoading(activity);
        JSONBuilder body = new JSONBuilder();
        body.add("tipe_cuti", listTipe.get(spn_jenis.getSelectedItemPosition()).getId());
        body.add("startdate", tanggal_mulai);
        body.add("enddate", tanggal_selesai);
        body.add("file", upload_file);
        Log.d(Constant.TAG, body.create().toString());

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlCutiKhusus,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity), body.create(),
                20000, new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            int status = object.getJSONObject("metadata").getInt("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

                            if(status == 1){
                                txt_tanggal_mulai.setText("Tanggal Mulai");
                                txt_tanggal_selesai.setText("Tanggal Selesai");
                                txt_file.setText("Upload File");

                                tanggal_mulai = "";
                                tanggal_selesai = "";
                                upload_file = "";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }

    private void loadMasterCutiKhusus(){
        AppLoading.getInstance().showLoading(activity);
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlMasterCutiKhusus,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(activity),
                new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject metadata = new JSONObject(result).getJSONObject("metadata");
                            int status = metadata.getInt("status");
                            String message = metadata.getString("message");

                            if(status == 1){
                                initTipe();
                                JSONArray response = new JSONObject(result).getJSONArray("response");
                                for(int i = 0; i < response.length(); i++){
                                    JSONObject tipe = response.getJSONObject(i);
                                    listTipe.add(new CutiKhususModel(tipe.getString("id"),
                                            tipe.getString("tipe"), tipe.getInt("flag_upload") == 1));
                                }

                                adapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }
}
