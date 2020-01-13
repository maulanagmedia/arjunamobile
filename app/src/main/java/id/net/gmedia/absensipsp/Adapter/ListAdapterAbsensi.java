package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import id.net.gmedia.absensipsp.Model.Custom;
import id.net.gmedia.absensipsp.R;

import java.util.List;

/**
 * Created by Bayu on 08/12/2017.
 */

public class ListAdapterAbsensi extends ArrayAdapter {
    private Context context;
    private List<Custom> absensi;


    public ListAdapterAbsensi(Context context, List<Custom> absensi) {
        super(context, R.layout.lv_data_rekap_absensi, absensi);
        this.context = context;
        this.absensi = absensi;
    }

    private static class ViewHolder {
        private TextView tanggal, jammasuk, jamkeluar, telat;
        private String flag_libur;
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
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        int tipeViewList = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater  inflater = ((Activity) context).getLayoutInflater();

            convertView = inflater.inflate(R.layout.lv_data_rekap_absensi, null);
            holder.tanggal = convertView.findViewById(R.id.tanggalabsen);
            holder.jammasuk = convertView.findViewById(R.id.jammasukabsen);
            holder.jamkeluar = convertView.findViewById(R.id.jamkeluarabsen);
            holder.telat = convertView.findViewById(R.id.telat);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Custom absen = absensi.get(position);
        holder.tanggal.setText(absen.getTanggal());
        holder.jammasuk.setText(absen.getJammasuk());
        holder.jamkeluar.setText(absen.getJamkeluar());
        holder.telat.setText(absen.getTelat());
        holder.flag_libur = absen.getFlag_libur();
        if (tipeViewList == 0) {
            RelativeLayout a = convertView.findViewById(R.id.layouttanggalabsen);
            RelativeLayout b = convertView.findViewById(R.id.layoutjammasukabsen);
            RelativeLayout c = convertView.findViewById(R.id.layoutjamkeluarabsen);
            RelativeLayout d = convertView.findViewById(R.id.layoutjamtelat);
            a.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            b.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            c.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            d.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
        }
        TextView texta = holder.tanggal;
        if (holder.flag_libur.equals("1")) {
            texta.setTextColor(Color.parseColor("#FF0000"));
        } else {
            texta.setTextColor(context.getResources().getColor(R.color.textbiasa));
        }

        return convertView;
    }
}
