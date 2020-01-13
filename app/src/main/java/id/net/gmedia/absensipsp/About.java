package id.net.gmedia.absensipsp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leonardus.irfan.ApiVolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class About extends Fragment {
    private Context context;
    private RelativeLayout btnChangeLog;
    private TextView version;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about, viewGroup, false);
        context = getContext();
        initUI();
        initAction();

        return view;
    }

    private void initUI() {
        btnChangeLog = view.findViewById(R.id.btnChangeLog);
        version = view.findViewById(R.id.version);
    }

    private void initAction() {
        prepareDataVersion();
        btnChangeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChangeLog.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
    }

    private void prepareDataVersion() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlChangeLog, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray response = object.getJSONArray("response");
                                if (response.length() > 0) {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject isi = response.getJSONObject(i);
                                        if (i == 0) {
                                            String versi = "Versi "+isi.getString("version");
                                            version.setText(versi);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Log.e(Constant.TAG, result);
                    }
                });
    }
}
