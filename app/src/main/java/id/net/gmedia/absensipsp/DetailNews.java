package id.net.gmedia.absensipsp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.FileDownloadManager;

import id.net.gmedia.absensipsp.Model.BeritaModel;

public class DetailNews extends AppCompatActivity {

    private BeritaModel berita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_news);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(id.net.gmedia.absensipsp.R.color.statusbar));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            berita = gson.fromJson(bundle.getString(Constant.EXTRA_BERITA,
                    ""), BeritaModel.class);
        }

        initUI();
    }

    private void initUI() {
        TextView txtJudul = findViewById(R.id.txtJudul);
        TextView txtTanggal = findViewById(R.id.txtTanggal);
        TextView txtBerita = findViewById(R.id.txtBerita);
        ImageView gambar = findViewById(R.id.picDetailNews);
        Button btn_download = findViewById(R.id.btn_download);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Berita");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtJudul.setText(berita.getJudul());
        txtTanggal.setText(berita.getTanggal());
        txtBerita.setText(berita.getBerita());
        if(!berita.getFile().isEmpty()){
            btn_download.setVisibility(View.VISIBLE);
            btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(berita.getFile());
                }
            });
        }
    }

    private void downloadFile(String url){
        FileDownloadManager manager = new FileDownloadManager(this);
        manager.download(url);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
