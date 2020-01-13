package id.net.gmedia.absensipsp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import id.net.gmedia.absensipsp.Adapter.ExpandableListViewAdapter;
import id.net.gmedia.absensipsp.Model.Child;
import id.net.gmedia.absensipsp.Model.Parent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UangLembur extends Fragment {
    private ExpandableListViewAdapter listViewAdapter;
    ExpandableListView listView;
    private List<Parent> header;
    private HashMap<String, List<Child>> child;
    private Context context;
    private TextView jumlah;
    private String totalan;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.uang_lembur, viewGroup, false);
        context = getContext();
        listView = view.findViewById(id.net.gmedia.absensipsp.R.id.expanded_menu);
        jumlah = view.findViewById(id.net.gmedia.absensipsp.R.id.text80);
        parsingdata();
        return view;
    }

    private void parsingdata() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("type", "lembur");

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlUangLembur, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        header = new ArrayList<>();
                        child = new HashMap<>();

                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                double totalnominal = 0;
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject parent = array.getJSONObject(i);
                                    header.add(new Parent(
                                            parent.getString("minggu"),
                                            parent.getString("tgl_awal"),
                                            parent.getString("tgl_akhir"),
                                            parent.getString("nominal")
                                    ));

                                    totalnominal+= parseNullDouble(parent.getString("nominal"));
                                    JSONArray cilikan = parent.getJSONArray("detail");
                                    if (cilikan.length() > 0) {
                                        List<Child> childminggu1 = new ArrayList<>();
                                        for (int a = 0; a < cilikan.length(); a++) {
                                            JSONObject isicilikan = cilikan.getJSONObject(a);
                                            childminggu1.add(new Child(
                                                    isicilikan.getString("hari"),
                                                    isicilikan.getString("nominal")
                                            ));
                                        }
                                        child.put(header.get(i).getMinggu(), childminggu1);
                                    }
                                }

                                totalan = doubleToStringFull(totalnominal);
                                jumlah.setText(ChangeToRupiahFormat(totalan));
                                listViewAdapter = new ExpandableListViewAdapter(context, header, child);
                                listView.setAdapter(listViewAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private Double parseNullDouble(String s){
        double result = 0;
        if(s != null && s.length() > 0){
            try {
                result = Double.parseDouble(s);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(Constant.TAG, e.getMessage());
            }
        }
        return result;
    }
    private String doubleToStringFull(Double number){
        return String.format("%s", number).replace(",",".");
    }

    private String ChangeToRupiahFormat(String number){
        double value = parseNullDouble(number);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        symbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(0);

        return String.valueOf(format.format(value));
    }
}