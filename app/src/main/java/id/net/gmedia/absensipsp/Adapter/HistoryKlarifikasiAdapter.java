package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.net.gmedia.absensipsp.Model.CustomHistoryIjin;
import id.net.gmedia.absensipsp.R;

public class HistoryKlarifikasiAdapter extends RecyclerView.Adapter<HistoryKlarifikasiAdapter.HistoryKlarifikasiViewHolder> {

    private final int ODD_FLAG = 101;
    private Activity activity;
    private List<CustomHistoryIjin> listKlarifikasi;

    public HistoryKlarifikasiAdapter(Activity activity, List<CustomHistoryIjin> listKlarifikasi){
        this.activity = activity;
        this.listKlarifikasi = listKlarifikasi;
    }

    @Override
    public int getItemViewType(int position) {
        if(position % 2 == 1){
            return ODD_FLAG;
        }
        else{
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public HistoryKlarifikasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryKlarifikasiViewHolder(LayoutInflater.from(activity).
                inflate(R.layout.item_history_klarifikasi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryKlarifikasiViewHolder holder, int position) {
        if(holder.getAdapterPosition() % 2 == 1){
            holder.layout_parent.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
        }
        holder.bind(listKlarifikasi.get(position));
    }

    @Override
    public int getItemCount() {
        return listKlarifikasi.size();
    }

    class HistoryKlarifikasiViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout_parent;
        TextView txt_tanggal, txt_keterangan, txt_status;

        HistoryKlarifikasiViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            txt_status = itemView.findViewById(R.id.txt_status);
        }

        void bind(CustomHistoryIjin c){
            txt_tanggal.setText(c.getTanggal());
            txt_keterangan.setText(c.getAlasan());
            txt_status.setText(c.getStatus());
        }
    }
}
