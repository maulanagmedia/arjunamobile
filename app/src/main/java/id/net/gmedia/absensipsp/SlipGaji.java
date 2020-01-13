package id.net.gmedia.absensipsp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import id.net.gmedia.absensipsp.Adapter.SlipGajiAdapter;


public class SlipGaji extends Fragment {

    private Context context;

    private AppCompatSpinner spn_bulan, spn_tahun;
    private String[] bulan = new String[]{"Pilih Bulan : ", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    private String[] tahun = new String[]
            {
                    "Pilih Tahun : ",
                    String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - 1),
                    String.valueOf(java.util.Calendar.getInstance().get(Calendar.YEAR)),
            };

    private View layout_detail, layout_kosong;

    private HashMap<String, List<SimpleObjectModel>> detailGaji = new HashMap<>();
    private List<String> listHeader = new ArrayList<>();
    private SlipGajiAdapter adapter;

    public SlipGaji() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_slip_gaji, container, false);

        spn_bulan =  v.findViewById(R.id.spn_bulan);
        spn_tahun = v.findViewById(R.id.spn_tahun);
        layout_detail = v.findViewById(R.id.layout_detail);
        layout_kosong = v.findViewById(R.id.layout_kosong);
        RecyclerView rv_gaji = v.findViewById(R.id.rv_gaji);

        rv_gaji.setItemAnimator(new DefaultItemAnimator());
        rv_gaji.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SlipGajiAdapter(context, listHeader, detailGaji);
        rv_gaji.setAdapter(adapter);

        initSpinner();
        v.findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn_bulan.getSelectedItemPosition() == 0){
                    Toast.makeText(context, "Pilih bulan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(spn_tahun.getSelectedItemPosition() == 0){
                    Toast.makeText(context, "Pilih tahun terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadGaji();
                }
            }
        });

        return v;
    }

    private void loadGaji(){
        AppLoading.getInstance().showLoading(context);
        JSONBuilder body = new JSONBuilder();
        body.add("bulan", spn_bulan.getSelectedItemPosition());
        body.add("tahun", (String)spn_tahun.getSelectedItem());

        layout_kosong.setVisibility(View.GONE);
        layout_detail.setVisibility(View.GONE);

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlGaji, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 1){
                                listHeader.clear();
                                detailGaji.clear();

                                JSONArray slip = response.getJSONArray("response");
                                if(slip.length() > 0){
                                    JSONObject d = slip.getJSONObject(0);

                                    String header = "Keterangan";
                                    listHeader.add(header);
                                    JSONArray arr = d.getJSONArray("header_gaji");
                                    List<SimpleObjectModel> s = new ArrayList<>();
                                    for(int i = 0; i < arr.length(); i++){
                                        s.add(new SimpleObjectModel(arr.getJSONObject(i).getString("label"),
                                                arr.getJSONObject(i).getString("value")));
                                    }
                                    detailGaji.put(header, s);

                                    header = "Nominal";
                                    listHeader.add(header);
                                    arr = d.getJSONArray("nominal");
                                    s = new ArrayList<>();
                                    for(int i = 0; i < arr.length(); i++){
                                        s.add(new SimpleObjectModel(arr.getJSONObject(i).getString("label"),
                                                arr.getJSONObject(i).getString("value")));
                                    }
                                    s.add(new SimpleObjectModel("Total Nominal",
                                            d.getJSONArray("total_nominal").getJSONObject(0).getString("value")));
                                    detailGaji.put(header, s);

                                    header = "Potongan";
                                    listHeader.add(header);
                                    arr = d.getJSONArray("potongan");
                                    s = new ArrayList<>();
                                    for(int i = 0; i < arr.length(); i++){
                                        s.add(new SimpleObjectModel(arr.getJSONObject(i).getString("label"),
                                                arr.getJSONObject(i).getString("value")));
                                    }
                                    s.add(new SimpleObjectModel("Total Potongan",
                                            d.getJSONArray("total_potongan").getJSONObject(0).getString("value")));
                                    detailGaji.put(header, s);

                                    String total = d.getJSONArray("total").getJSONObject(0).getString("value");
                                    adapter.setTotal(total);

                                    adapter.notifyDataSetChanged();
                                    layout_detail.setVisibility(View.VISIBLE);
                                }
                                else{
                                    layout_kosong.setVisibility(View.VISIBLE);
                                }
                            }
                            else{
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }

    private void initSpinner(){
        ArrayAdapter<String> adapter_bulan = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, bulan) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    spn_bulan.setAlpha(0.5f);
                    return false;
                } else {
                    spn_bulan.setAlpha(1);
                    return true;
                }
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
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

        ArrayAdapter<String> adapter_tahun = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, tahun) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    spn_bulan.setAlpha(0.5f);
                    return false;
                } else {
                    spn_bulan.setAlpha(1);
                    return true;
                }
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
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

        adapter_bulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_tahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_bulan.setAdapter(adapter_bulan);
        spn_tahun.setAdapter(adapter_tahun);

        spn_bulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
