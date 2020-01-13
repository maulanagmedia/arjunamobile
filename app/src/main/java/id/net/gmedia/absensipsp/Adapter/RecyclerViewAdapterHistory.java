package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.JSONBuilder;

import id.net.gmedia.absensipsp.EditCuti;
import id.net.gmedia.absensipsp.HistoryCuti;
import id.net.gmedia.absensipsp.Model.CustomRecycler;
import id.net.gmedia.absensipsp.R;
import id.net.gmedia.absensipsp.Constant;
import id.net.gmedia.absensipsp.Volley.ApiVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Bayu on 14/12/2017.
 */

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.ViewHolder> {
    private ArrayList<CustomRecycler> rvData;
    private Context context;

    public RecyclerViewAdapterHistory(Context context, ArrayList<CustomRecycler> rvData) {
        this.context = context;
        this.rvData = rvData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mulai, sampai, keterangan;
        private Button cancelHistory, editHistory;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mulai = itemView.findViewById(R.id.textmulaicuti);
            sampai = itemView.findViewById(R.id.textsampaicuti);
            keterangan = itemView.findViewById(R.id.textketeranganhistory);
            cancelHistory = itemView.findViewById(R.id.cancelHistory);
            editHistory = itemView.findViewById(R.id.editHistory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_history_cuti, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mulai.setText(rvData.get(position).getMulai());
        holder.sampai.setText(rvData.get(position).getSampai());
        holder.keterangan.setText(rvData.get(position).getKeterangan());
        setAnimation(holder.itemView, position);
        holder.cancelHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLoading.getInstance().showLoading(context);
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popupcancelhistory);
                Button yacancel = dialog.findViewById(R.id.yacancel);
                yacancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONBuilder body = new JSONBuilder();
                        body.add("id_cuti", rvData.get(position).getId());

                        String url;
                        if(context instanceof HistoryCuti && ((HistoryCuti)context).cuti_khusus){
                            url = Constant.urlBatalCutiKhusus;
                        }
                        else{
                            url = Constant.urlBatalCuti;
                        }

                        ApiVolleyManager.getInstance().addSecureRequest(context, url, ApiVolleyManager.METHOD_POST,
                                Constant.getTokenHeader(context), body.create(), 20000,
                                new ApiVolleyManager.RequestCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            JSONObject object = new JSONObject(result);
                                            String status = object.getJSONObject("metadata").getString("status");
                                            String message = object.getJSONObject("metadata").getString("message");

                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                            if (status.equals("1")) {
                                                rvData.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.e(Constant.TAG, e.getMessage());
                                        }

                                        AppLoading.getInstance().stopLoading();
                                    }

                                    @Override
                                    public void onError(String result) {
                                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                                        Log.e(Constant.TAG, result);

                                        AppLoading.getInstance().stopLoading();
                                    }
                                });

                        dialog.dismiss();
                    }
                });
                Button tidakcancel = dialog.findViewById(R.id.tidakcancel);
                tidakcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        holder.editHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject jBody = new JSONObject();
                try {
                    jBody.put("flag", "1");
                    jBody.put("id", rvData.get(position).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiVolley request = new ApiVolley(context, jBody, "POST", Constant.urlHistory,
                        "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            final String status = object.getJSONObject("metadata").getString("status");
//                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isi = response.getJSONObject(i);
                                    Intent intent = new Intent(context, EditCuti.class);
                                    intent.putExtra("id",isi.getString("id_cuti"));
                                    intent.putExtra("awal",isi.getString("tgl_mulai"));
                                    intent.putExtra("akhir",isi.getString("tgl_selesai"));
                                    intent.putExtra("alasan",isi.getString("alasan"));
                                    intent.putExtra("approval",isi.getString("approval"));
                                    ((Activity) context).startActivity(intent);
                                }
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void setAnimation(View viewToAnimate, int position) {
            /*Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.right_from_left
                    : R.anim.left_form_right);*/
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.animasi);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }


}
