package id.net.gmedia.absensipsp;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.leonardus.irfan.AppLoading;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserGuide extends Fragment {

    public UserGuide() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View v = inflater.inflate(R.layout.fragment_user_guide, container, false);

        WebView webView = v.findViewById(R.id.web_userguide);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        AppLoading.getInstance().showLoading(context);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                AppLoading.getInstance().stopLoading();
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl("https://office.putmasaripratama.co.id/arjuna/web_view/user_guide_absensi");

        return v;
    }

}
