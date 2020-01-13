package id.net.gmedia.absensipsp.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import id.net.gmedia.absensipsp.Constant;
import id.net.gmedia.absensipsp.DetailNews;
import id.net.gmedia.absensipsp.Model.BeritaModel;
import id.net.gmedia.absensipsp.R;

import java.util.ArrayList;

/**
 * Created by Bayu on 24/01/2018.
 */

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.ViewHolder> {
    private ArrayList<BeritaModel> rvData;
    private Context context;

    public BeritaAdapter(Context context, ArrayList<BeritaModel> rvData) {
        this.context = context;
        this.rvData = rvData;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tanggal, judul, berita;
        private RelativeLayout background;

        public ViewHolder(View itemView) {
            super(itemView);
            tanggal = itemView.findViewById(R.id.tanggalDashboardBaru);
            judul = itemView.findViewById(R.id.teks1DashboardBaru);
            berita = itemView.findViewById(R.id.teks2DashboardBaru);
            background = itemView.findViewById(R.id.onClickNews);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recyclerview_dashboard_baru, parent, false));
        return viewHolder;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tanggal.setText(rvData.get(position).getTanggal());
        holder.judul.setText(rvData.get(position).getJudul());
        holder.berita.setText(rvData.get(position).getBerita());
        final BeritaModel custom = rvData.get(position);
        //final String gambar = custom.getGambar();
        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent intent = new Intent(context, DetailNews.class);
                intent.putExtra(Constant.EXTRA_BERITA, gson.toJson(custom));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }
}
