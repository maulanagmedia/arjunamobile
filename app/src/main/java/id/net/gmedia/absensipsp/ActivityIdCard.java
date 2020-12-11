package id.net.gmedia.absensipsp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class ActivityIdCard extends AppCompatActivity {

    private String link = "https://office.putmasaripratama.co.id/arjuna/ad?sis=MA==&woyo=MTgwMzYyOA==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("ID Card");


        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        //Zoom
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        //Load
        webView.loadUrl(link);

    }
}