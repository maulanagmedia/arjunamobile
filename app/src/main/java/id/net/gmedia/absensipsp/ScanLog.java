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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.absensipsp.Adapter.ListAdapterScanLog;
import id.net.gmedia.absensipsp.Model.CustomScanLog;

public class ScanLog extends Fragment {
    private Context context;
    private ListAdapterScanLog adapter;
    private List<CustomScanLog> scanLog;
    private ListView listView;

    private View dataScanLog, layout_kosong;
    private TextView tgl;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scan_log, viewGroup, false);
        context = getContext();

        //Inisialisasi Variabel
        listView = view.findViewById(R.id.listScanLog);
        tgl = view.findViewById(R.id.tglScanLog);
        dataScanLog = view.findViewById(R.id.dataScanLog);
        layout_kosong = view.findViewById(R.id.layout_kosong);
        RelativeLayout btnTgl = view.findViewById(R.id.btnTglScanLog);
        RelativeLayout proses = view.findViewById(R.id.btnProsesScanLog);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        tgl.setText(formattedDate);
        btnTgl.setOnClickListener(new View.OnClickListener() {
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
                        tgl.setText(sdFormat.format(customDate.getTime()));
                        tgl.setAlpha(1);
                    }
                };
                new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH),
                        customDate.get(java.util.Calendar.DATE)).show();
            }
        });

        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tgl.getText().toString().equals("Silahkan Isi Tanggal")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal", Toast.LENGTH_LONG).show();
                    return;
                }

                listView.setAdapter(null);
                prepareDataScanLog();
            }
        });

        return view;
    }

    private void prepareDataScanLog() {
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
    }
}
