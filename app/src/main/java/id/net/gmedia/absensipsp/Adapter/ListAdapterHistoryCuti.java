package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import id.net.gmedia.absensipsp.Model.CustomHistory;
import id.net.gmedia.absensipsp.R;

import java.util.List;

/**
 * Created by Bayu on 15/12/2017.
 */

public class ListAdapterHistoryCuti extends ArrayAdapter {
    private Context context;
    private List<CustomHistory> history;

    public ListAdapterHistoryCuti(Context context, List<CustomHistory>history) {
        super(context, R.layout.listview_history_cuti, history);
        this.context=context;
        this.history=history;
    }
    private static class ViewHolder {
        private TextView no, mulai, selesai, keterangan;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView (final int position, View convertView, @NonNull ViewGroup parent){
        ViewHolder holder = new ViewHolder();
        if (convertView==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.listview_history_cuti,null);
            holder.no = convertView.findViewById(R.id.nohistory);
            holder.mulai = convertView.findViewById(R.id.tanggalmulaihistory);
            holder.selesai = convertView.findViewById(R.id.tanggalselesaihistory);
            holder.keterangan = convertView.findViewById(R.id.keteranganhistory);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CustomHistory customHistory = history.get(position);
        holder.no.setText(customHistory.getNo());
        holder.mulai.setText(customHistory.getMulai());
        holder.selesai.setText(customHistory.getSelesai());
        holder.keterangan.setText(customHistory.getKeteranganHistory());

        if (position%2==1)
        {
            LinearLayout layout = convertView.findViewById(R.id.layouthistory);
            layout.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
            notifyDataSetChanged();
        }
        return convertView;
    }
}
