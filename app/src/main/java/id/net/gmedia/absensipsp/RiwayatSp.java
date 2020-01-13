package id.net.gmedia.absensipsp;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.Converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.absensipsp.Adapter.SpAdapter;
import id.net.gmedia.absensipsp.Model.SpModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class RiwayatSp extends Fragment {

    private Activity activity;

    private View layout_kosong, layout_sp;

    private SpAdapter adapter;
    private List<SpModel> listSp = new ArrayList<>();

    public RiwayatSp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_riwayat_sp, container, false);

        layout_kosong = v.findViewById(R.id.layout_kosong);
        layout_sp = v.findViewById(R.id.layout_sp);

        RecyclerView rv_sp = v.findViewById(R.id.rv_sp);
        rv_sp.setItemAnimator(new DefaultItemAnimator());
        adapter = new SpAdapter(activity, listSp);
        rv_sp.setAdapter(adapter);
        rv_sp.setLayoutManager(new LinearLayoutManager(activity));

        loadSp();

        return v;
    }

    private void loadSp(){
        AppLoading.getInstance().showLoading(activity);

        layout_sp.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlSp, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(activity), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 1){
                                JSONArray list_sp = response.getJSONArray("response");
                                if(list_sp.length() > 0){
                                    listSp.clear();
                                    for(int i = 0; i < list_sp.length(); i++){
                                        JSONObject sp = list_sp.getJSONObject(i);
                                        listSp.add(new SpModel(Converter.stringDToDateReversed(sp.getString("tgl_sp_awal")),
                                                Converter.stringDToDateReversed(sp.getString("tgl_sp_akhir")),
                                                sp.getString("status_sp"), sp.getString("level_sp"),
                                                sp.getString("status_terbit"), sp.getString("alasan")));
                                    }

                                    adapter.notifyDataSetChanged();
                                    layout_sp.setVisibility(View.VISIBLE);
                                }
                                else{
                                    layout_kosong.setVisibility(View.VISIBLE);
                                }
                            }
                            else{
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, "Terjadi kesalahan koneksi", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }
}
