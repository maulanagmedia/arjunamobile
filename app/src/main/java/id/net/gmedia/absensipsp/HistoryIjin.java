package id.net.gmedia.absensipsp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import id.net.gmedia.absensipsp.Adapter.ListAdapterHistoryIjinKeluarKantor;
import id.net.gmedia.absensipsp.Adapter.ListAdapterHistoryIjinPulangAwal;
import id.net.gmedia.absensipsp.Model.CustomHistoryIjinKeluarKantor;
import id.net.gmedia.absensipsp.Model.CustomHistoryIjinPulangAwal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Bayu on 21/02/2018.
 */

public class HistoryIjin extends AppCompatActivity {
    private ListView listViewHistoryIjinPulangAwal, listViewHistoryIjinKeluarKantor;
    private ListAdapterHistoryIjinPulangAwal adapterHistoryIjinPulangAwal;
    private ListAdapterHistoryIjinKeluarKantor adapterHistoryIjinKeluarKantor;

    private Spinner dropdownMenu;
    private View pulangAwal, keluarKantor;
    private TextView textMulai, textSelesai;
    private int posisi;
    private List<CustomHistoryIjinPulangAwal> historyIjinPulangAwal;
    private List<CustomHistoryIjinKeluarKantor> historyIjinKeluarKantor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(id.net.gmedia.absensipsp.R.layout.history_ijin);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(id.net.gmedia.absensipsp.R.color.statusbar));
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Riwayat Ijin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RelativeLayout tglMulai, tglSelesai, kirim;
        tglMulai = findViewById(id.net.gmedia.absensipsp.R.id.layoutTglMulaiHistoryIjin);
        tglSelesai = findViewById(id.net.gmedia.absensipsp.R.id.layoutTglSelesaiHistoryIjin);
        kirim = findViewById(id.net.gmedia.absensipsp.R.id.layoutkirimhistoryijin);
        textMulai = findViewById(id.net.gmedia.absensipsp.R.id.textTglMulaiHistoryIjin);
        textSelesai = findViewById(id.net.gmedia.absensipsp.R.id.textTglSelesaiHistoryIjin);
        listViewHistoryIjinPulangAwal = findViewById(id.net.gmedia.absensipsp.R.id.listHistoryIjinPulangAwal);
        listViewHistoryIjinKeluarKantor = findViewById(id.net.gmedia.absensipsp.R.id.listHistoryIjinKeluarKantor);
        pulangAwal = findViewById(id.net.gmedia.absensipsp.R.id.layoutListPulangAwal);
        keluarKantor = findViewById(id.net.gmedia.absensipsp.R.id.layoutListKeluarKantor);
        dropdownMenu = findViewById(id.net.gmedia.absensipsp.R.id.menudropdownHistoryIjin);
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posisi = parent.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String[] items = new String[]{"Jenis Ijin ", "Ijin Pulang Awal", "Ijin Meninggalkan Kerja"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryIjin.this, android.R.layout.simple_spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    dropdownMenu.setAlpha(0.5f);
                    return false;
                } else {
                    dropdownMenu.setAlpha(1);
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
                    // Set the hint text color gray
                    tv.setTextColor(Color.DKGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(adapter);
        tglMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar customDate = java.util.Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        customDate.set(java.util.Calendar.YEAR, year);
                        customDate.set(java.util.Calendar.MONTH, month);
                        customDate.set(java.util.Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        textMulai.setText(sdFormat.format(customDate.getTime()));
                        textMulai.setAlpha(1);

                    }
                };
                new DatePickerDialog(HistoryIjin.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        tglSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar customDate = java.util.Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        customDate.set(java.util.Calendar.YEAR, year);
                        customDate.set(java.util.Calendar.MONTH, month);
                        customDate.set(java.util.Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        textSelesai.setText(sdFormat.format(customDate.getTime()));
                        textSelesai.setAlpha(1);

                    }
                };
                new DatePickerDialog(HistoryIjin.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posisi == 0) {
                    Toast.makeText(getApplicationContext(), "Silahkan Pilih Jenis Ijin Anda", Toast.LENGTH_LONG).show();
                }
                if (posisi == 1) {
                    if (textMulai.getText().toString().equals("Tanggal Mulai")) {
                        Toast.makeText(getApplicationContext(), "Silahkan Isi Tanggal Mulai", Toast.LENGTH_LONG).show();
                    } else if (textSelesai.getText().toString().equals("Tanggal Selesai")) {
                        Toast.makeText(getApplicationContext(), "Silahkan Isi Tanggal Selesai", Toast.LENGTH_LONG).show();
                    } else {
                        prepareViewIjinPulang();
                    }
                } else if (posisi == 2) {
                    if (textMulai.getText().toString().equals("Tanggal Mulai")) {
                        Toast.makeText(getApplicationContext(), "Silahkan Isi Tanggal Mulai", Toast.LENGTH_LONG).show();
                    } else if (textSelesai.getText().toString().equals("Tanggal Selesai")) {
                        Toast.makeText(getApplicationContext(), "Silahkan Isi Tanggal Selesai", Toast.LENGTH_LONG).show();
                    } else {
                        prepareViewKeluarKantor();
                    }
                }
            }
        });
    }

    private void prepareViewIjinPulang() {
        final Dialog dialog = new Dialog(HistoryIjin.this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("startdate", textMulai.getText().toString());
        body.add("enddate", textSelesai.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlViewPulangAwal, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        keluarKantor.setVisibility(View.GONE);
                        historyIjinPulangAwal = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("0")){
                                Toast.makeText(getApplicationContext(),"Tidak Ada Data",Toast.LENGTH_LONG).show();
                            }
                            else if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject isi = array.getJSONObject(i);
                                    historyIjinPulangAwal.add(new CustomHistoryIjinPulangAwal(
                                            isi.getString("tgl"),
                                            isi.getString("jam"),
                                            isi.getString("alasan"),
                                            isi.getString("status")
                                    ));
                                }

                                listViewHistoryIjinPulangAwal.setAdapter(null);
                                adapterHistoryIjinPulangAwal = new ListAdapterHistoryIjinPulangAwal(HistoryIjin.this, historyIjinPulangAwal);
                                listViewHistoryIjinPulangAwal.setAdapter(adapterHistoryIjinPulangAwal);
                                pulangAwal.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private void prepareViewKeluarKantor() {
        final Dialog dialog = new Dialog(HistoryIjin.this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("startdate", textMulai.getText().toString());
        body.add("enddate", textSelesai.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlViewKeluarKantor, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        pulangAwal.setVisibility(View.GONE);
                        historyIjinKeluarKantor = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("0")){
                                Toast.makeText(getApplicationContext(),"Tidak Ada Data",Toast.LENGTH_LONG).show();
                            }
                            else if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject isi = array.getJSONObject(i);
                                    historyIjinKeluarKantor.add(new CustomHistoryIjinKeluarKantor(
                                            isi.getString("tgl"),
                                            isi.getInt("jmljam"),
                                            isi.getString("alasan"),
                                            isi.getString("status")
                                    ));
                                }

                                listViewHistoryIjinKeluarKantor.setAdapter(null);
                                adapterHistoryIjinKeluarKantor = new ListAdapterHistoryIjinKeluarKantor(HistoryIjin.this, historyIjinKeluarKantor);
                                listViewHistoryIjinKeluarKantor.setAdapter(adapterHistoryIjinKeluarKantor);
                                keluarKantor.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
