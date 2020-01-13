package id.net.gmedia.absensipsp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leonardus.irfan.ApiVolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.absensipsp.Adapter.ListAdapterChangeLog;
import id.net.gmedia.absensipsp.Adapter.ListAdapterNotif;
import id.net.gmedia.absensipsp.Adapter.ListAdapterNotifikasi;
import id.net.gmedia.absensipsp.Model.CustomChangeLog;
import id.net.gmedia.absensipsp.Model.ModelNotif;

public class ActifityNotfif extends AppCompatActivity {

    private List<ModelNotif> listNotif = new ArrayList<>();
    private ListAdapterNotifikasi adapterNf;
    private Activity activity;
    private ListView listView;
    private static final String TAG ="Notifikasi";

    @Override
    protected void onCreate (Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.notify_activity);
    activity = this;

        listView = findViewById(R.id.lv_notif);
//        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Notifikasi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        LoadMenu();

    }

    private void LoadMenu() {
        final Dialog dialog = new Dialog(ActifityNotfif.this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlNotifikasi, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(activity), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        listNotif = new ArrayList<>();
                        listView.setAdapter(null);
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isi = response.getJSONObject(i);
                                    listNotif.add(new ModelNotif(
                                            isi.getString("id"),
                                            isi.getString("title"),
                                            isi.getString("notification"),
                                            isi.getString("timestamp"),
                                            isi.getString("flag_notif")
                                    ));
                                }
                                adapterNf = new ListAdapterNotifikasi(ActifityNotfif.this,listNotif);
                                listView.setAdapter(adapterNf);
                            } else {
                                Toast.makeText(ActifityNotfif.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(ActifityNotfif.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
