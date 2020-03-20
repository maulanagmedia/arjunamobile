package id.net.gmedia.absensipsp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import id.net.gmedia.absensipsp.Adapter.ExpandableListViewAdapter;
import id.net.gmedia.absensipsp.Adapter.ListAdapterFilterLembur;
import id.net.gmedia.absensipsp.Adapter.ListAdapterScanLog;
import id.net.gmedia.absensipsp.Adapter.SlipGajiAdapter;
import id.net.gmedia.absensipsp.Model.Child;
import id.net.gmedia.absensipsp.Model.CustomScanLog;
import id.net.gmedia.absensipsp.Model.ModelFilterLembur;
import id.net.gmedia.absensipsp.Model.Parent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UangLembur extends Fragment {
    private ExpandableListViewAdapter listViewAdapter;
    private ListAdapterFilterLembur lvLembur;
    ExpandableListView listView;

    private ListView Rvlmbr;

    private View dataAda, layout_kosong;

    private AppCompatSpinner spn_bulan, spn_tahun;
    private String[] bulan = new String[]{"Pilih Bulan : ", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

    private String[] tahun = new String[]
            {
                    "Pilih Tahun : ",
                    String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - 1),
                    String.valueOf(java.util.Calendar.getInstance().get(Calendar.YEAR)),
            };

    private List<Parent> header;
    private List <ModelFilterLembur> listlembur= new ArrayList<>();
    private List<ModelFilterLembur> lembur;
    private HashMap<String, List<Child>> child;
    private Context context;
    private TextView jumlah;
    private String totalan;
    private TextView tgl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.uang_lembur, viewGroup, false);
        context = getContext();
        listView = view.findViewById(id.net.gmedia.absensipsp.R.id.expanded_menu);
        jumlah = view.findViewById(id.net.gmedia.absensipsp.R.id.text80);
        spn_bulan =  view.findViewById(R.id.spn_bulan);
        spn_tahun = view.findViewById(R.id.spn_tahun);
        Rvlmbr = view.findViewById(R.id.rv_lembur);
        dataAda = view.findViewById(R.id.layout_detail);
        layout_kosong = view.findViewById(R.id.layout_kosong);
        initSpinner();
        parsingdata();

        /*RecyclerView rv_lembur = view.findViewById(R.id.rv_lembur);
        rv_lembur.setItemAnimator(new DefaultItemAnimator());
        rv_lembur.setLayoutManager(new LinearLayoutManager(context));
        lvLembur = new ListAdapterFilterLembur(context, listlembur);
        rv_lembur.setAdapter(lvLembur);*/


        view.findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn_bulan.getSelectedItemPosition() == 0){
                    Toast.makeText(context, "Pilih bulan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(spn_tahun.getSelectedItemPosition() == 0){
                    Toast.makeText(context, "Pilih tahun terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    filter();
                }
            }
        });


       /* proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tgl.getText().toString().equals("Silahkan Isi Tanggal")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal", Toast.LENGTH_LONG).show();
                    return;
                }

                listView.setAdapter(lvLembur);
                filter();
            }
        });
*/
        return view;


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


    private void parsingdata() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("type", "lembur");

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlUangLembur, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        header = new ArrayList<>();
                        child = new HashMap<>();

                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                double totalnominal = 0;
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject parent = array.getJSONObject(i);
                                    header.add(new Parent(
                                            parent.getString("minggu"),
                                            parent.getString("tgl_awal"),
                                            parent.getString("tgl_akhir"),
                                            parent.getString("nominal")
                                    ));

                                    totalnominal+= parseNullDouble(parent.getString("nominal"));
                                    JSONArray cilikan = parent.getJSONArray("detail");
                                    if (cilikan.length() > 0) {
                                        List<Child> childminggu1 = new ArrayList<>();
                                        for (int a = 0; a < cilikan.length(); a++) {
                                            JSONObject isicilikan = cilikan.getJSONObject(a);
                                            childminggu1.add(new Child(
                                                    isicilikan.getString("hari"),
                                                    isicilikan.getString("nominal")
                                            ));
                                        }
                                        child.put(header.get(i).getMinggu(), childminggu1);
                                    }
                                }

                                totalan = doubleToStringFull(totalnominal);
                                jumlah.setText(ChangeToRupiahFormat(totalan));
                                listViewAdapter = new ExpandableListViewAdapter(context, header, child);
                                listView.setAdapter(listViewAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }


    /*private void prepareDataScanLog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dataScanLog.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);

        JSONBuilder body = new JSONBuilder();
        body.add("date", tgl.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlScanLog, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        scanLog = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isi = response.getJSONObject(i);
                                    scanLog.add(new CustomScanLog(
                                            isi.getString("tanggal"),
                                            isi.getString("jam")
                                    ));
                                }

                                if(response.length() > 0){
                                    listView.setAdapter(null);
                                    adapter = new ListAdapterScanLog(context, scanLog);
                                    listView.setAdapter(adapter);
                                    dataScanLog.setVisibility(View.VISIBLE);
                                }
                                else{
                                    layout_kosong.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }*/



    private void filter() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("bulan", spn_bulan.getSelectedItemPosition());
        body.add("tahun", (String)spn_tahun.getSelectedItem());

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlFilterLembur, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {

                    @Override
                    public void onSuccess(String result) {
                        child = new HashMap<>();

                        try {
                            JSONObject object = new JSONObject(result);
                            int status = object.getJSONObject("metadata").getInt("status");
                            if (status == 1) {
                                JSONObject objt = object.getJSONObject("response");
                                objt.getString("total_nominal");
                                objt.getString("range");
                                objt.getString("tgl_awal");
                                objt.getString("tgl_akhir");

                                    JSONArray cilikan = objt.getJSONArray("detail");
                                    if (cilikan.length() > 0) {
                                        for (int a = 0; a < cilikan.length(); a++) {
                                            JSONObject isicilikan = cilikan.getJSONObject(a);
                                            listlembur.add(new ModelFilterLembur(
                                                    isicilikan.getString("hari"),
                                                    isicilikan.getString("nominal")
                                            ));

                                            Rvlmbr.setAdapter(null);
                                            lvLembur = new ListAdapterFilterLembur(context, lembur);
                                            Rvlmbr.setAdapter(lvLembur);
                                            lvLembur.notifyDataSetChanged();
                                            dataAda.setVisibility(View.VISIBLE);
                                        }

                                    } else{
                                        layout_kosong.setVisibility(View.VISIBLE);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }


    private Double parseNullDouble(String s){
        double result = 0;
        if(s != null && s.length() > 0){
            try {
                result = Double.parseDouble(s);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(Constant.TAG, e.getMessage());
            }
        }
        return result;
    }
    private String doubleToStringFull(Double number){
        return String.format("%s", number).replace(",",".");
    }

    private String ChangeToRupiahFormat(String number){
        double value = parseNullDouble(number);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        symbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(0);

        return String.valueOf(format.format(value));
    }

}