package id.net.gmedia.absensipsp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import id.net.gmedia.absensipsp.Adapter.ListAdapterHistoryCuti;
import id.net.gmedia.absensipsp.Adapter.RecyclerViewAdapterHistory;
import id.net.gmedia.absensipsp.Model.CustomHistory;
import id.net.gmedia.absensipsp.Model.CustomRecycler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryCuti extends AppCompatActivity {
    public boolean cuti_khusus = false;

    private ListView listView;
    private ListAdapterHistoryCuti adapterHistory;
    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private ArrayList<CustomRecycler> dataSet;
    private ArrayList<CustomHistory> history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(id.net.gmedia.absensipsp.R.layout.historycuti);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(id.net.gmedia.absensipsp.R.color.statusbar));
        }

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Riwayat Cuti");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //initToolbar();
        if(getIntent().hasExtra(Constant.EXTRA_FLAG_CUTI_KHUSUS)){
            cuti_khusus = getIntent().getBooleanExtra(Constant.EXTRA_FLAG_CUTI_KHUSUS, false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvView = findViewById(id.net.gmedia.absensipsp.R.id.rv_main);
        rvView.setLayoutManager(layoutManager);

        listView = findViewById(id.net.gmedia.absensipsp.R.id.lvCuti);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }

        parsingDataCutiHistory();
        parsingDataOnProgress();
    }

    private void parsingDataOnProgress() {
        String url = cuti_khusus ? Constant.urlHistoryCutiKhusus : Constant.urlHistory;
        JSONBuilder body = new JSONBuilder();
        body.add("flag", "1");

        ApiVolleyManager.getInstance().addSecureRequest(this, url, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(),
                20000, new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dataSet = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject onProgress = array.getJSONObject(i);
                                    dataSet.add(new CustomRecycler(
                                            onProgress.getString("id_cuti"),
                                            onProgress.getString("mulai"),
                                            onProgress.getString("selesai"),
                                            onProgress.getString("alasan")
                                    ));
                                }
                                rvView.setAdapter(null);
                                adapter = new RecyclerViewAdapterHistory(HistoryCuti.this, dataSet);
                                rvView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private void parsingDataCutiHistory() {
        String url = cuti_khusus ? Constant.urlHistoryCutiKhusus : Constant.urlHistory;
        JSONBuilder body = new JSONBuilder();
        body.add("flag", "0");

        ApiVolleyManager.getInstance().addSecureRequest(this, url, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        history = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject cutiHistory = array.getJSONObject(i);
                                    history.add(new CustomHistory(
                                            cutiHistory.getString("status"),
                                            cutiHistory.getString("mulai"),
                                            cutiHistory.getString("selesai"),
                                            cutiHistory.getString("alasan")
                                    ));
                                }
                                listView.setAdapter(null);
                                adapterHistory = new ListAdapterHistoryCuti(HistoryCuti.this, history);
                                listView.setAdapter(adapterHistory);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
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

    /*private void initToolbar(){
        //Inisialisasi Toolbar
        collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        collapsing_toolbar.setTitle("");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (appBarLayout.getTotalScrollRange() + verticalOffset <= getActionBarHeight()) {
                    txt_title.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                    isShow = true;
                    //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                } else if (isShow) {
                    txt_title.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    isShow = false;
                    //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }
        });
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        if(getSupportActionBar() != null){
            actionBarHeight = getSupportActionBar().getHeight();
            if (actionBarHeight != 0)
                return actionBarHeight;
            final TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }*/
}
