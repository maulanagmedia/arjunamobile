package id.net.gmedia.absensipsp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.absensipsp.Adapter.ListAdapterChangeLog;
import id.net.gmedia.absensipsp.Model.CustomChangeLog;

public class ChangeLog extends AppCompatActivity {
    private ListView listView;
    private ListAdapterChangeLog adapter;
    private List<CustomChangeLog> changeLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_log);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(id.net.gmedia.absensipsp.R.color.statusbar));
        }

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Change Log");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.listChangeLog);

        prepareDataChangeLog();
    }

    private void prepareDataChangeLog() {
        final Dialog dialog = new Dialog(ChangeLog.this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlChangeLog, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        changeLog = new ArrayList<>();
                        listView.setAdapter(null);
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isi = response.getJSONObject(i);
                                    changeLog.add(new CustomChangeLog(
                                            isi.getString("version"),
                                            isi.getString("timestamp"),
                                            isi.getString("changelog")
                                    ));
                                }
                                adapter = new ListAdapterChangeLog(ChangeLog.this,changeLog);
                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(ChangeLog.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(ChangeLog.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
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
        MainActivity.changelog = true;
        Intent intent = new Intent(ChangeLog.this, MainActivity.class);
        intent.putExtra("About", "data");
        startActivity(intent);
        finish();
    }
}
