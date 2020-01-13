package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.FileDownloadManager;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import id.net.gmedia.absensipsp.Approval;
import id.net.gmedia.absensipsp.R;
import id.net.gmedia.absensipsp.Model.CustomApproval;
import id.net.gmedia.absensipsp.Constant;

public class ListAdapterApproval extends ArrayAdapter {
    private Context context;
    private List<CustomApproval> approval;

    public ListAdapterApproval(Context context, List<CustomApproval> approval) {
        super(context, R.layout.lv_approval, approval);
        this.context = context;
        this.approval = approval;
    }

    private static class ViewHolder {
        private TextView alasan, nama, jam, tanggal, keterangan;
        private RelativeLayout layoutJam;
        private LinearLayout background;
        private Button btn_download;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.lv_approval, null);
            holder.nama = convertView.findViewById(R.id.tv_nama);
            holder.tanggal = convertView.findViewById(R.id.tv_tanggal);
            holder.keterangan = convertView.findViewById(R.id.tv_keterangan);
            holder.jam = convertView.findViewById(R.id.tv_jam);
            holder.alasan = convertView.findViewById(R.id.tv_alasan);
            holder.background = convertView.findViewById(R.id.background);
            holder.layoutJam = convertView.findViewById(R.id.layoutJamApproval);
            holder.btn_download = convertView.findViewById(R.id.btn_download);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomApproval custom = approval.get(position);
        holder.nama.setText(custom.getNama());
        holder.tanggal.setText(custom.getTanggal());
        holder.keterangan.setText(custom.getKeterangan());
        holder.alasan.setText(custom.getAlasan());
        String waktu = custom.getJam() + " menit";
        holder.jam.setText(waktu);

        /*Animation animation = AnimationUtils.loadAnimation(context, R.anim.animasi);
        cv.startAnimation(animation);*/

        switch (Approval.tipe) {
            case "cuti":
                holder.layoutJam.setVisibility(View.GONE);
                break;
            case "pulang":
                holder.layoutJam.setVisibility(View.VISIBLE);
                break;
            case "ijin_keluar":
                holder.layoutJam.setVisibility(View.VISIBLE);
                break;
            case "cuti_khusus" :
                holder.layoutJam.setVisibility(View.GONE);
                break;
            case "klarifikasi_absensi":
                holder.layoutJam.setVisibility(View.GONE);
                break;
        }

        if(!custom.getUrl().isEmpty()){
            holder.btn_download.setVisibility(View.VISIBLE);
        }
        else{
            holder.btn_download.setVisibility(View.GONE);
        }

        holder.btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!custom.getUrl().isEmpty() && context instanceof Activity){
                    FileDownloadManager manager = new FileDownloadManager((Activity)context);
                    manager.download(custom.getUrl());
                }
            }
        });

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogYesNO = new Dialog(context);
                dialogYesNO.setContentView(R.layout.popup_approval);
                dialogYesNO.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                View cancel = dialogYesNO.findViewById(R.id.cancelPopupApproval);
                Button Accept = dialogYesNO.findViewById(R.id.btnAcc);
                Button Reject = dialogYesNO.findViewById(R.id.btnRjj);
                TextView jenisApproval = dialogYesNO.findViewById(R.id.txtJenisApproval);
                switch (Approval.tipe) {
                    case "cuti":
                        jenisApproval.setText("Cuti?");
                        break;
                    case "pulang":
                        jenisApproval.setText("Ijin Pulang Awal?");
                        break;
                    case "ijin_keluar":
                        jenisApproval.setText("Ijin Keluar Kantor?");
                        break;
                    case "cuti_khusus":
                        jenisApproval.setText("Cuti Khusus?");
                        break;
                    case "klarifikasi_absensi":
                        jenisApproval.setText("Klarifikasi Absensi?");
                        break;
                }

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogYesNO.dismiss();
                    }
                });

                Accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                        JSONBuilder body = new JSONBuilder();
                        body.add("tipe", Approval.tipe);
                        body.add("approval", "1");
                        body.add("id", approval.get(position).getId());

                        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlKonfirmasiApproval,
                                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(context),
                                body.create(), new ApiVolleyManager.RequestCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        dialog.dismiss();
                                        dialogYesNO.dismiss();
                                        try {
                                            JSONObject object = new JSONObject(result);
                                            String status = object.getJSONObject("metadata").getString("status");
                                            String message = object.getJSONObject("metadata").getString("message");
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                            if (status.equals("1")) {
                                                approval.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.e(Constant.TAG, e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onError(String result) {
                                        dialog.dismiss();

                                        dialogYesNO.dismiss();
                                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                                        Log.e(Constant.TAG, result);
                                    }
                                });
                    }
                });

                Reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
                        if(dialog.getWindow() != null){
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }

                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                        JSONBuilder body = new JSONBuilder();
                        body.add("tipe", Approval.tipe);
                        body.add("approval", "0");
                        body.add("id", approval.get(position).getId());

                        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlKonfirmasiApproval,
                                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(context),
                                body.create(), new ApiVolleyManager.RequestCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        dialog.dismiss();
                                        dialogYesNO.dismiss();
                                        try {
                                            JSONObject object = new JSONObject(result);
                                            String status = object.getJSONObject("metadata").getString("status");
                                            String message = object.getJSONObject("metadata").getString("message");
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                            if (status.equals("1")) {
                                                approval.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.e(Constant.TAG, e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onError(String result) {
                                        dialog.dismiss();
                                        dialogYesNO.dismiss();

                                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                                        Log.e(Constant.TAG, result);
                                    }
                                });
                    }
                });
                dialogYesNO.setCancelable(false);
                dialogYesNO.show();
            }
        });
        return convertView;
    }
}
