package id.net.gmedia.absensipsp;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class KlarifikasiAbsensi extends Fragment {

    private Activity activity;

    private List<SimpleObjectModel> listTipe = new ArrayList<>();
    private ArrayAdapter<SimpleObjectModel> adapter;

    private Spinner spn_jenis;
    private View layout_jam_masuk, layout_jam_pulang, layout_upload;
    private TextView txt_tanggal, txt_keterangan, txt_file, txt_jam_masuk, txt_jam_pulang;
    private String tanggal = "", jam_masuk = "", jam_pulang = "", file_upload = "";

    public KlarifikasiAbsensi() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_klarifikasi_absensi, container, false);

        txt_tanggal = v.findViewById(R.id.txt_tanggal);
        spn_jenis = v.findViewById(R.id.spn_jenis);
        txt_keterangan = v.findViewById(R.id.txt_keterangan);
        txt_file = v.findViewById(R.id.txt_file);
        txt_jam_masuk = v.findViewById(R.id.txt_jam_masuk);
        txt_jam_pulang = v.findViewById(R.id.txt_jam_pulang);
        layout_jam_masuk = v.findViewById(R.id.layout_jam_masuk);
        layout_jam_pulang = v.findViewById(R.id.layout_jam_pulang);
        layout_upload = v.findViewById(R.id.layout_upload);

        Bundle args = getArguments();
        if(args != null){
            String t = args.getString(Constant.EXTRA_TANGGAL);
            if(t != null && !t.isEmpty()){
                tanggal = t;
                txt_tanggal.setText(t);
            }
        }

        adapter = new ArrayAdapter<SimpleObjectModel>
                (activity, android.R.layout.simple_spinner_item, listTipe) {
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
                if(position == 0){
                    layout_jam_masuk.setVisibility(View.GONE);
                    layout_jam_pulang.setVisibility(View.GONE);
                    layout_upload.setVisibility(View.GONE);
                }
                else{
                    layout_jam_masuk.setVisibility(View.VISIBLE);
                    layout_jam_pulang.setVisibility(View.VISIBLE);
                    layout_upload.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        v.findViewById(R.id.btn_riwayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, HistoryKlarifikasi.class));
            }
        });

        v.findViewById(R.id.layout_tanggal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(activity, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        String date = Converter.DToStringReversed(Converter.stringDToDate(dateString));
                        txt_tanggal.setText(date);
                        tanggal = date;
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

        v.findViewById(R.id.layout_jam_masuk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectTime(activity, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_jam_masuk.setText(dateString);
                        jam_masuk = dateString;
                    }
                });
            }
        });

        v.findViewById(R.id.layout_jam_pulang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectTime(activity, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_jam_pulang.setText(dateString);
                        jam_pulang = dateString;
                    }
                });
            }
        });

        v.findViewById(R.id.btn_kirim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn_jenis.getSelectedItemPosition() < 1){
                    Toast.makeText(activity, "Pilih tipe klarifikasi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(tanggal.isEmpty()){
                    Toast.makeText(activity, "Input tanggal klarifikasi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    ajukanKlarifikasi();
                }
            }
        });

        loadTipe();
        return v;
    }

    private void ajukanKlarifikasi(){
        AppLoading.getInstance().showLoading(activity);
        JSONBuilder body = new JSONBuilder();

        body.add("date", tanggal);
        body.add("tipe_klarifikasi", listTipe.get(spn_jenis.getSelectedItemPosition()).getId());
        body.add("scan_masuk", jam_masuk);
        body.add("scan_pulang", jam_pulang);
        body.add("klarifikasi", txt_keterangan.getText().toString());
        body.add("file", file_upload);

        Log.d(Constant.TAG, "Body " + body.create());

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlKlarifikasiAbsensi,
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
                                txt_tanggal.setText("Tanggal");
                                txt_jam_masuk.setText("Jam Masuk");
                                txt_jam_pulang.setText("Jam Pulang");
                                txt_file.setText("Upload File");
                                txt_keterangan.setText("");

                                tanggal = "";
                                jam_masuk = "";
                                jam_pulang = "";
                                file_upload = "";
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

    private void initTipe(){
        listTipe.clear();
        listTipe.add(new SimpleObjectModel("0", "Jenis Klarifikasi Absensi : "));
    }

    void prepareBitmap(String path, Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        txt_file.setText(path);
        file_upload = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void loadTipe(){
        AppLoading.getInstance().showLoading(activity);
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlMasterKlarifikasi,
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
                                    JSONObject t = response.getJSONObject(i);
                                    listTipe.add(new SimpleObjectModel(t.getString("id"), t.getString("value")));
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
