package id.net.gmedia.absensipsp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Bayu on 28/11/2017.
 */

public class Checkin extends Fragment {

    private Context context;
    private SessionManager session;
    private boolean posisi = true;
    private WifiManager manager;
    private String infoSSID = "", infoBSSID = "", infoIpPublic = "";
    private Dialog dialogCheckIn;

    private TextView dinotanggal, waktuJam, waktuMenit;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, final Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.chekin, viewGroup, false);
        context = getContext();
        session = new SessionManager(context);

        //Inisialisasi Variabel
        dinotanggal = view.findViewById(id.net.gmedia.absensipsp.R.id.dinotanggal);
        waktuJam = view.findViewById(id.net.gmedia.absensipsp.R.id.waktuJam);
        waktuMenit = view.findViewById(id.net.gmedia.absensipsp.R.id.waktuMenit);
        RelativeLayout btnLogin = view.findViewById(R.id.tombolCheckIn);

        LinearLayout layoutcheckin = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutcheckin);
        TextView checkin = view.findViewById(id.net.gmedia.absensipsp.R.id.checkin);
        if (DashboardBaru.tombol) {
            posisi = true;
            layoutcheckin.setBackground(getResources().getDrawable(R.drawable.background));
            checkin.setText("CHECK IN");
            checkin.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica-Bold.otf"));
            btnLogin.setBackground(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.bundertombolin));
        } else {
            posisi = false;
            layoutcheckin.setBackground(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.baground_chek_out));
            checkin.setText("CHECK OUT");
            checkin.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica-Bold.otf"));
            btnLogin.setBackground(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.bundertombolout));
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = Checkin.this.getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(context.getResources().getColor(id.net.gmedia.absensipsp.R.color.statusbarcheckout));
            }
        }

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("EE, dd/MMM/yyy", Locale.US);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.getDefault());
                                SimpleDateFormat sdf3 = new SimpleDateFormat("mm", Locale.getDefault());
                                String dateString = sdf.format(date);
                                String dateString2 = sdf2.format(date);
                                String dateString3 = sdf3.format(date);
                                dinotanggal.setText(dateString);
                                waktuJam.setText(dateString2);
                                waktuMenit.setText(dateString3);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        waktuJam.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/DS-DIGI.TTF"));
        waktuMenit.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/DS-DIGI.TTF"));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bundle = getArguments().getString("Check");
                if (bundle != null) {
                    if (bundle.equals("in")) {
                        Log.d("Check", "in");
                        getIpPublic();
                    } else if (bundle.equals("out")) {
                        Log.d("Check", "out");
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.popupcheckout);
                        Button ya = dialog.findViewById(id.net.gmedia.absensipsp.R.id.yacheckout);
                        ya.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                getIpPublic();
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
            }
        });
        return view;
    }

    private void getIpPublic() {
        dialogCheckIn = new Dialog(context);
        dialogCheckIn.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialogCheckIn.getWindow() != null){
            dialogCheckIn.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialogCheckIn.setCanceledOnTouchOutside(false);
        dialogCheckIn.show();

        ApiVolleyManager.getInstance().addRequest(context, Constant.urlIpPublic, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(context), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialogCheckIn.dismiss();
                        infoIpPublic = Jsoup.parse(result).text();
                        manager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                        WifiInfo info = manager.getConnectionInfo();
                        infoSSID = info.getSSID();
                        infoBSSID = info.getBSSID();
                        checkin();
                    }

                    @Override
                    public void onError(String result) {
                        dialogCheckIn.dismiss();

                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private void checkin() {
        if (posisi) {
            final Dialog dialogLoading = new Dialog(context);
            dialogLoading.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
            if(dialogLoading.getWindow() != null){
                dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialogLoading.setCanceledOnTouchOutside(false);
            dialogLoading.show();

            JSONBuilder body = new JSONBuilder();
            //body.add("imei", imei);
            body.add("ssid", infoSSID);
            body.add("macaddress", infoBSSID);
            body.add("ippublic", infoIpPublic);

            ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlCheckIn, ApiVolleyManager.METHOD_POST,
                    Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                        @Override
                        public void onSuccess(String result) {
                            dialogLoading.dismiss();
                            try {
                                JSONObject object = new JSONObject(result);
                                String status = object.getJSONObject("metadata").getString("status");
                                if (status.equals("1")) {
                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(id.net.gmedia.absensipsp.R.layout.popupsuksescheckin);
                                    RelativeLayout close = dialog.findViewById(id.net.gmedia.absensipsp.R.id.closesukseslogin);
                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    if(dialog.getWindow() != null){
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    }
                                    dialog.show();
                                    final Handler handler = new Handler();
                                    final Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    };

                                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            handler.removeCallbacks(runnable);
                                            Intent intent = new Intent(context, MainActivity.class);
                                            startActivity(intent);
                                            ((Activity) context).finish();
                                        }
                                    });

                                    handler.postDelayed(runnable, 2000);

                                } else {
                                    String warning = object.getJSONObject("metadata").getString("message");
                                    Toast.makeText(context, warning, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(Constant.TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onError(String result) {
                            dialogLoading.dismiss();

                            Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                            Log.e(Constant.TAG, result);
                        }
                    });
        } else {
            final Dialog dialogLoading = new Dialog(context);
            dialogLoading.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
            if(dialogLoading.getWindow() != null){
                dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialogLoading.setCanceledOnTouchOutside(false);
            dialogLoading.show();

            JSONBuilder body = new JSONBuilder();
            //body.add("imei", imei);
            body.add("flag", "1");
            body.add("ssid", infoSSID);
            body.add("macaddress", infoBSSID);
            body.add("ippublic", infoIpPublic);

            ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlCheckIn, ApiVolleyManager.METHOD_POST,
                    Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                        @Override
                        public void onSuccess(String result) {
                            dialogLoading.dismiss();
                            try {
                                JSONObject object = new JSONObject(result);
                                String status = object.getJSONObject("metadata").getString("status");
                                if (status.equals("1")) {
                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.popupsuksescheckout);
                                    RelativeLayout close = dialog.findViewById(id.net.gmedia.absensipsp.R.id.closesukseslogin);
                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    if(dialog.getWindow() != null){
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    }
                                    dialog.show();
                                    final Handler handler = new Handler();
                                    final Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    };

                                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            handler.removeCallbacks(runnable);
                                            Intent intent = new Intent(context, MainActivity.class);
                                            startActivity(intent);
                                            ((Activity) context).finish();
                                        }
                                    });

                                    handler.postDelayed(runnable, 2000);
                                } else {
                                    String warning = object.getJSONObject("metadata").getString("message");
                                    Toast.makeText(context, warning, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(Constant.TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onError(String result) {
                            dialogLoading.dismiss();
                            Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                            Log.e(Constant.TAG, result);
                        }
                    });
        }
    }
}