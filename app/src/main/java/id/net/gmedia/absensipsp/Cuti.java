package id.net.gmedia.absensipsp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Bayu on 05/12/2017.
 */

public class Cuti extends Fragment {
    private Context context;
    private TextView tglmulai, tglselesai, sisacuti;
    private EditText keterangan;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.cuti, viewGroup, false);
        context = getContext();
        RelativeLayout tombolmulai = view.findViewById(id.net.gmedia.absensipsp.R.id.layouttglmulai);
        RelativeLayout tombolselesai = view.findViewById(id.net.gmedia.absensipsp.R.id.layouttglselesai);
        RelativeLayout tombolkirim = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutkirim);
        tglmulai = view.findViewById(id.net.gmedia.absensipsp.R.id.texttglmulai);
        tglselesai = view.findViewById(id.net.gmedia.absensipsp.R.id.texttglselesai);
        keterangan = view.findViewById(id.net.gmedia.absensipsp.R.id.edit_text_keterangan);

        RelativeLayout layoutcuti = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutcuti);
        layoutcuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard((Activity) context);
            }
        });
        sisacuti = view.findViewById(id.net.gmedia.absensipsp.R.id.sisacuti);
        sisaCuti();
        tombolmulai.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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
                        tglmulai.setText(sdFormat.format(customDate.getTime()));
                        tglmulai.setAlpha(1);

                    }
                };
                new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR),
                        customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
                /*Intent i = new Intent(context, Calendar.class);
                i.putExtra("tombol","dari");
                i.putExtra("flag","cuti");
                startActivity(i);*/
            }
        });
        tombolselesai.setOnClickListener(new View.OnClickListener() {
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
                        tglselesai.setText(sdFormat.format(customDate.getTime()));
                        tglselesai.setAlpha(1);

                    }
                };
                new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR),
                        customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
                /*Intent i = new Intent(context, Calendar.class);
                i.putExtra("tombol","sampai");
                i.putExtra("flag","cuti");
                startActivity(i);*/
            }
        });

        //loadApproval();
        tombolkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tglmulai.getText().toString().equals("Tanggal Mulai")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal Mulai", Toast.LENGTH_LONG).show();
                } else if (tglselesai.getText().toString().equals("Tanggal Selesai")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal Selesai", Toast.LENGTH_LONG).show();
                } else if (keterangan.getText().toString().equals("")) {
                    Toast.makeText(context, "Silahkan Isi Keterangan Anda", Toast.LENGTH_LONG).show();
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.popup_konfirmasi);

                    TextView txt_konfirmasi = dialog.findViewById(R.id.txt_konfirmasi);
                    txt_konfirmasi.setText("Yakin ingin mengajukan cuti?");

                    Button ya = dialog.findViewById(id.net.gmedia.absensipsp.R.id.yacheckout);
                    ya.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            prepareData();
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
        TextView history = view.findViewById(id.net.gmedia.absensipsp.R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, HistoryCuti.class);
                context.startActivity(i);
            }
        });
        return view;
    }


    private void sisaCuti() {
        AppLoading.getInstance().showLoading(context);
        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlSisaCuti, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONObject isi = object.getJSONObject("response");
                                sisacuti.setText(isi.getString("sisa_cuti"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
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
        body.add("startdate", tglmulai.getText().toString());
        body.add("enddate", tglselesai.getText().toString());
        body.add("keterangan", keterangan.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlCuti, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            int status = object.getJSONObject("metadata").getInt("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                            if(status == 1){
                                tglmulai.setText("Tanggal Mulai");
                                tglselesai.setText("Tanggal Selesai");
                                keterangan.setText("");

                                sisaCuti();
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
