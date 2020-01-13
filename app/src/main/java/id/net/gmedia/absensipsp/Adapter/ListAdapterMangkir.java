package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.absensipsp.Constant;
import id.net.gmedia.absensipsp.KlarifikasiAbsensi;
import id.net.gmedia.absensipsp.MainActivity;
import id.net.gmedia.absensipsp.Model.MangkirModel;
import id.net.gmedia.absensipsp.R;

public class ListAdapterMangkir extends ArrayAdapter {
    private Context context;
    private List<MangkirModel> listMangkir;

    public ListAdapterMangkir(Context context, List<MangkirModel> listMangkir) {
        super(context, R.layout.listview_mangkir, listMangkir);
        this.listMangkir = listMangkir;
        this.context=context;
    }

    private class MangkirHolder{
        private View layout_parent;
        private TextView txt_hari_tanggal, txt_jam_masuk, txt_jam_pulang, txt_keterangan;
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
        if(position % 2 == 1){
            hasil = 0;
        }else{
            hasil = 1;
        }
        return hasil;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        MangkirHolder holder = new MangkirHolder();
        //int tipeViewList = getItemViewType(position);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.listview_mangkir, null);
            holder.layout_parent = convertView.findViewById(R.id.layout_parent);
            holder.txt_hari_tanggal = convertView.findViewById(R.id.txt_hari_tanggal);
            holder.txt_jam_masuk = convertView.findViewById(R.id.txt_jam_masuk);
            holder.txt_jam_pulang = convertView.findViewById(R.id.txt_jam_pulang);
            holder.txt_keterangan = convertView.findViewById(R.id.txt_keterangan);
            convertView.setTag(holder);
        }
        else {
            holder = (MangkirHolder) convertView.getTag();
        }

        final MangkirModel mangkir = listMangkir.get(position);
        String hari = mangkir.getHari() + ", " + Converter.DToString(mangkir.getTanggal());
        holder.txt_hari_tanggal.setText(hari);
        holder.txt_jam_masuk.setText(mangkir.getJam_masuk());
        holder.txt_jam_pulang.setText(mangkir.getJam_pulang());
        holder.txt_keterangan.setText(mangkir.getKeterangan());

        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof MainActivity){
                    Fragment f = new KlarifikasiAbsensi();
                    Bundle b = new Bundle();
                    b.putString(Constant.EXTRA_TANGGAL,
                            Converter.DToStringReversed(mangkir.getTanggal()));
                    f.setArguments(b);

                    ((MainActivity)context).loadFragment(f,
                            "Klarifikasi Absensi");
                }
            }
        });

        /*if (tipeViewList==0) {
            RelativeLayout a = convertView.findViewById(R.id.layouttanggaltotalterlambat);
            RelativeLayout b = convertView.findViewById(R.id.layoutjammasuktotalterlambat);
            RelativeLayout c = convertView.findViewById(R.id.layoutscanmasuktotalterlambat);
            RelativeLayout d = convertView.findViewById(R.id.layoutjamtelattotalterlambat);
            a.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            b.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            c.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            d.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
        }*/

        TextView texta = holder.txt_hari_tanggal;
        texta.setTextColor(context.getResources().getColor(R.color.textbiasa));

        return convertView;
    }
}