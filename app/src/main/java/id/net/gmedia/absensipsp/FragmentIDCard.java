package id.net.gmedia.absensipsp;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class FragmentIDCard extends Fragment {
    private String link = "https://office.putmasaripratama.co.id/arjuna/ad?sis=MA==&woyo=";
    private View view;
    private Activity activity;
    private Base64 base64;
    private SessionManager sessionManager;

    public FragmentIDCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_i_d_card, container, false);

        sessionManager = new SessionManager(activity);

        WebView webView = view.findViewById(R.id.web_view);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        //Zoom
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        //Convert NIP to
        String np =sessionManager.getNip();
        String Sbase64 = np;
        byte[] bytes = Sbase64.getBytes();
        Sbase64 = new String(android.util.Base64.encode(bytes, Base64.DEFAULT));
        Log.d(Sbase64, "Testing");

        //Load
        webView.loadUrl(link+Sbase64);

        return view;
    }
}