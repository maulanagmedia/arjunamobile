package id.net.gmedia.absensipsp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.github.barteksc.pdfviewer.PDFView;

public class ActivityPDFLink extends AppCompatActivity {

    private WebView webView;
    private String link="";
    private ProgressBar progressBar;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_link);



        Bundle bundle = getIntent().getExtras();

        if (bundle !=null){
            link = bundle.getString("web");
        }

        //view
       // webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.loading);

        pdfView.fromAsset("namafile.pdf")
                .enableSwipe(true)
                .load();


        /*WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url="+ link);

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                progressBar.setVisibility(View.GONE);
            }
        });*/
    }
}