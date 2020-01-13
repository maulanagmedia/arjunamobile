package id.net.gmedia.absensipsp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.Converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.absensipsp.Adapter.ListAdapterMangkir;
import id.net.gmedia.absensipsp.Model.MangkirModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewMangkir extends Fragment {

    private Context context;

    private ListView lv_mangkir;
    private TextView txt_periode;
    private View layout_kosong;

    private List<MangkirModel> listMangkir = new ArrayList<>();

    public ViewMangkir() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_view_mangkir, container, false);

        txt_periode = v.findViewById(R.id.txt_periode);
        lv_mangkir = v.findViewById(R.id.lv_mangkir);
        layout_kosong = v.findViewById(R.id.layout_kosong);

        lv_mangkir.setDivider(null);

        loadMangkir();
        return v;
    }

    private void loadMangkir(){
        lv_mangkir.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);

        AppLoading.getInstance().showLoading(context);
        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlMangkir,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(context),
                new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            listMangkir.clear();

                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 1){
                                if(response.get("response") instanceof JSONArray){
                                    JSONArray list_mangkir = response.getJSONArray("response");
                                    for(int i = 0; i < list_mangkir.length(); i++){
                                        JSONObject mangkir = list_mangkir.getJSONObject(i);
                                        if(i == 0){
                                            txt_periode.setText(mangkir.getString("range"));
                                        }

                                        listMangkir.add(new MangkirModel(mangkir.getString("hari"),
                                                Converter.stringDToDate(mangkir.getString("tanggal")),
                                                mangkir.getString("jam_masuk"),
                                                mangkir.getString("jam_pulang"),
                                                mangkir.getString("keterangan")));
                                    }

                                    lv_mangkir.setAdapter(null);
                                    ListAdapterMangkir adapter = new ListAdapterMangkir(context, listMangkir);
                                    lv_mangkir.setAdapter(adapter);
                                    lv_mangkir.setVisibility(View.VISIBLE);
                                }
                                else if(response.get("response") instanceof JSONObject){
                                    txt_periode.setText(response.getJSONObject("response").getString("periode"));
                                    layout_kosong.setVisibility(View.VISIBLE);
                                }
                            }
                            else{
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(context, "Terjadi kesalahan data", Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Terjadi Kesalahan koneksi", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }
}
