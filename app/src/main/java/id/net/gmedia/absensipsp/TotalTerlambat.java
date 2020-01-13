package id.net.gmedia.absensipsp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;

import id.net.gmedia.absensipsp.Adapter.ListAdapterTotalTerlambat;
import id.net.gmedia.absensipsp.Model.CustomTotalTerlambat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Bayu on 15/02/2018.
 */

public class TotalTerlambat extends AppCompatActivity {
    private ListView listView;
    private ListAdapterTotalTerlambat adapter;
    private TextView tanggalPeriode, hari, menit;

    private View layout_detail, layout_kosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(id.net.gmedia.absensipsp.R.layout.total_terlambat);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(id.net.gmedia.absensipsp.R.color.statusbar));
        }

        layout_detail = findViewById(R.id.layout_detail);
        layout_kosong = findViewById(R.id.layout_kosong);
        tanggalPeriode = findViewById(id.net.gmedia.absensipsp.R.id.tanggalPeriode);
        hari = findViewById(id.net.gmedia.absensipsp.R.id.totalHari);
        menit = findViewById(id.net.gmedia.absensipsp.R.id.totalMenit);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Total Terlambat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AppLoading.getInstance().showLoading(this);
        prepareDataTotalTerlambat();
        prepareDataDetailTotalTerlambat();
        listView = findViewById(id.net.gmedia.absensipsp.R.id.listTotalTerlambat);
    }

    private void prepareDataTotalTerlambat() {
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlTotalTerlambat,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONObject totalTerlambat = object.getJSONObject("response");
                                tanggalPeriode.setText(totalTerlambat.getString("periode"));
                                String total_hari = totalTerlambat.getString("total_hari") + " hari";
                                hari.setText(total_hari);
                                String total_menit = totalTerlambat.getString("total_menit") + " menit";
                                menit.setText(total_menit);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private void prepareDataDetailTotalTerlambat() {
        layout_detail.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlTotalTerlambat,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        ArrayList<CustomTotalTerlambat>totalTerlambat = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONObject response = object.getJSONObject("response");
                                JSONArray detail = response.getJSONArray("detail");
                                for (int i=0;i<detail.length();i++){
                                    JSONObject onProgress = detail.getJSONObject(i);
                                    totalTerlambat.add(new CustomTotalTerlambat(
                                            onProgress.getString("tanggal"),
                                            onProgress.getString("jam_masuk"),
                                            onProgress.getString("scan_masuk"),
                                            onProgress.getString("total_menit")
                                    ));
                                }

                                listView.setAdapter(null);
                                adapter = new ListAdapterTotalTerlambat(TotalTerlambat.this, totalTerlambat);
                                listView.setAdapter(adapter);

                                if(detail.length() > 0){
                                    layout_detail.setVisibility(View.VISIBLE);
                                }
                                else{
                                    layout_kosong.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
