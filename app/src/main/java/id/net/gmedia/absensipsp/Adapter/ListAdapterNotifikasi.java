package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.absensipsp.Model.CustomChangeLog;
import id.net.gmedia.absensipsp.Model.CustomItem;
import id.net.gmedia.absensipsp.Model.ModelNotif;
import id.net.gmedia.absensipsp.R;

public class ListAdapterNotifikasi extends ArrayAdapter {
    private Context context;
    private List<ModelNotif> changeLog;

    public ListAdapterNotifikasi(Context context, List<ModelNotif> changeLog) {
        super(context, R.layout.adapter_notif, changeLog);
        this.context = context;
        this.changeLog = changeLog;
    }

    private static class ViewHolder {
        private LinearLayout tv_status;
        private ImageView imageView;
        private TextView notifikasi, tanggal;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        convertView = null;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_notif, null);
            holder.notifikasi = convertView.findViewById(R.id.text_notif);
            holder.tanggal = convertView.findViewById(R.id.text_tglTime);
            holder.tv_status = convertView.findViewById(R.id.iv_status);
            holder.imageView = convertView.findViewById(R.id.img_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ModelNotif itemSelected = changeLog.get(position);
        LinearLayout background = convertView.findViewById(R.id.ll_notif);
        ModelNotif custom = changeLog.get(position);
        holder.notifikasi.setText(custom.getItem3());
        holder.tanggal.setText(custom.getItem4());
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.animasi);
        background.startAnimation(animation);

        if(itemSelected.getItem5().toUpperCase().trim().equals("0")){
            holder.imageView.setBackground(context.getResources().getDrawable(R.drawable.status_circle_pengajuan));
        }
        else if(itemSelected.getItem5().toUpperCase().trim().equals("1")){
            holder.imageView.setBackground(context.getResources().getDrawable(R.drawable.status_circle_stuju));
        }

        return convertView;
    }
}
