package id.net.gmedia.absensipsp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.absensipsp.Adapter.HistoryKlarifikasiAdapter;
import id.net.gmedia.absensipsp.Model.CustomHistoryIjin;

public class HistoryKlarifikasi extends AppCompatActivity {

    private List<CustomHistoryIjin> listKlarifikasi = new ArrayList<>();
    private HistoryKlarifikasiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_klarifikasi);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Riwayat Klarifikasi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_riwayat = findViewById(R.id.rv_riwayat);
        rv_riwayat.setItemAnimator(new DefaultItemAnimator());
        rv_riwayat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryKlarifikasiAdapter(this, listKlarifikasi);
        rv_riwayat.setAdapter(adapter);

        loadRiwayat();
    }

    private void loadRiwayat(){
        AppLoading.getInstance().showLoading(this);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlHistoryKlarifikasi,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject obj = new JSONObject(result);
                            int status = obj.getJSONObject("metadata").getInt("status");
                            String message = obj.getJSONObject("metadata").getString("message");

                            if(status == 1){
                                listKlarifikasi.clear();
                                JSONArray response = obj.getJSONArray("response");
                                for(int i = 0; i < response.length(); i++){
                                    JSONObject riwayat = response.getJSONObject(i);
                                    CustomHistoryIjin r = new CustomHistoryIjin();

                                    r.setNo(riwayat.getString("no"));
                                    r.setTanggal(riwayat.getString("tgl"));
                                    r.setAlasan(riwayat.getString("alasan"));
                                    r.setStatus(riwayat.getString("status"));

                                    listKlarifikasi.add(r);
                                }

                                adapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(HistoryKlarifikasi.this, message, Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        AppLoading.getInstance().stopLoading();

                        Toast.makeText(HistoryKlarifikasi.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
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
