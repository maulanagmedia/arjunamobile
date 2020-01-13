package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import id.net.gmedia.absensipsp.R;
import id.net.gmedia.absensipsp.Model.CustomScanLog;

public class ListAdapterScanLog extends ArrayAdapter {
    private Context context;
    private List<CustomScanLog> scanLog;

    public ListAdapterScanLog(Context context, List<CustomScanLog> scanLog) {
        super(context, R.layout.listview_scan_log, scanLog);
        this.context = context;
        this.scanLog = scanLog;
    }

    private static class ViewHolder {
        private TextView tanggal, jam;
        private View layout_parent;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        int hasil;
        if (position % 2 == 1) {
            hasil = 0;
        } else {
            hasil = 1;
        }
        return hasil;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        int tipeViewList = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.listview_scan_log, null);
            holder.tanggal = convertView.findViewById(R.id.tanggalScanLog);
            holder.jam = convertView.findViewById(R.id.jamScanLog);
            holder.layout_parent = convertView.findViewById(R.id.layout_parent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CustomScanLog custom = scanLog.get(position);
        holder.tanggal.setText(custom.getTanggal());
        holder.jam.setText(custom.getJam());

        if(tipeViewList == 1){
            holder.layout_parent.setBackground(new ColorDrawable(context.getResources().getColor(R.color.white)));
        }
        else{
            holder.layout_parent.setBackground(new ColorDrawable(context.getResources().getColor(R.color.grey)));
        }

        return convertView;
    }
}
