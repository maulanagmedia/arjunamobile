package id.net.gmedia.absensipsp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import id.net.gmedia.absensipsp.Adapter.ListAdapterJadwal;
import id.net.gmedia.absensipsp.Model.CustomJadwal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Bayu on 27/02/2018.
 */

public class Jadwal extends Fragment {
    private Context context;

    private ListView listView;
    private List<CustomJadwal> jadwal;
    private ListAdapterJadwal adapter;
    private TextView tglAwal, tglAkhir;
    private View dataJadwal, layout_kosong;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.jadwal, viewGroup, false);
        context = getContext();

        RelativeLayout proses;
        RelativeLayout tombolAwal = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutAwalJadwal);
        RelativeLayout tombolAkhir = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutAkhirJadwal);
        tglAwal = view.findViewById(id.net.gmedia.absensipsp.R.id.tglAwalJadwal);
        tglAkhir = view.findViewById(id.net.gmedia.absensipsp.R.id.tglAkhirJadwal);
        dataJadwal = view.findViewById(id.net.gmedia.absensipsp.R.id.dataJadwal);
        layout_kosong = view.findViewById(R.id.layout_kosong);
        listView = view.findViewById(id.net.gmedia.absensipsp.R.id.listJadwalExpand);
        proses = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutProsesJadwal);
        tombolAwal.setOnClickListener(new View.OnClickListener() {
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
                        tglAwal.setText(sdFormat.format(customDate.getTime()));
                        tglAwal.setAlpha(1);
                    }
                };
                new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        tombolAkhir.setOnClickListener(new View.OnClickListener() {
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
                        tglAkhir.setText(sdFormat.format(customDate.getTime()));
                        tglAkhir.setAlpha(1);

                    }
                };
                new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR),
                        customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tglAwal.getText().toString().equals("Tanggal Mulai")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal Mulai", Toast.LENGTH_LONG).show();
                } else if (tglAkhir.getText().toString().equals("Tanggal Selesai")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal Akhir", Toast.LENGTH_LONG).show();
                } else {
                    prepareData();
                }
            }
        });
        return view;
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
        body.add("startdate", tglAwal.getText().toString());
        body.add("enddate", tglAkhir.getText().toString());

        dataJadwal.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlJadwal, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        jadwal = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                //Jika data ada isinya
                                if(object.get("response") instanceof JSONObject){
                                    JSONObject response = object.getJSONObject("response");
                                    JSONArray detail = response.getJSONArray("jadwal");
                                    for (int i = 0; i < detail.length(); i++) {
                                        JSONObject isiDetail = detail.getJSONObject(i);
                                        jadwal.add(new CustomJadwal(
                                                isiDetail.getString("tanggal"),
                                                isiDetail.getString("shift"),
                                                isiDetail.getString("jam_masuk"),
                                                isiDetail.getString("jam_pulang"),
                                                isiDetail.getString("flag_libur")
                                        ));
                                    }
                                    listView.setAdapter(null);
                                    adapter = new ListAdapterJadwal(context, jadwal);
                                    listView.setAdapter(adapter);

                                    dataJadwal.setVisibility(View.VISIBLE);
                                }
                                //Jika data kosong
                                else{
                                    layout_kosong.setVisibility(View.VISIBLE);
                                }
                            }
                            else if (status.equals("0")){
                                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.d(Constant.TAG, result);
                    }
                });
    }
}