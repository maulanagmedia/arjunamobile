package id.net.gmedia.absensipsp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leonardus.irfan.SimpleObjectModel;

import java.util.HashMap;
import java.util.List;

import id.net.gmedia.absensipsp.R;

public class SlipGajiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_FOOTER = 999;

    private Context context;
    private List<String> listHeader;
    private HashMap<String, List<SimpleObjectModel>> detailGaji;
    private String total = "0";

    public SlipGajiAdapter(Context context, List<String> listHeader, HashMap<String,
            List<SimpleObjectModel>> detailGaji){
        this.context = context;
        this.listHeader = listHeader;
        this.detailGaji = detailGaji;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == listHeader.size()){
            return TYPE_FOOTER;
        }
        else{
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER){
            return new SlipGajiHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gaji_table, parent, false));
        }
        else{
            return new SlipGajiViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gaji_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SlipGajiViewHolder){
            String h = listHeader.get(position);
            ((SlipGajiViewHolder)holder).txt_header.setText(h);

            ((SlipGajiViewHolder)holder).rv_detail.setItemAnimator(new DefaultItemAnimator());
            ((SlipGajiViewHolder)holder).rv_detail.setLayoutManager(new LinearLayoutManager(context));
            ((SlipGajiViewHolder)holder).rv_detail.setAdapter(new SlipGajiChildAdapter(detailGaji.get(h)));
        }
        else if(holder instanceof SlipGajiHeaderViewHolder){
            ((SlipGajiHeaderViewHolder)holder).txt_total.setText(total);
        }
    }

    @Override
    public int getItemCount() {
        return detailGaji.size() + 1;
    }

    class SlipGajiViewHolder extends RecyclerView.ViewHolder{

        TextView txt_header;
        RecyclerView rv_detail;

        SlipGajiViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_header = itemView.findViewById(R.id.txt_header);
            rv_detail = itemView.findViewById(R.id.rv_detail);
        }
    }

    class SlipGajiHeaderViewHolder extends RecyclerView.ViewHolder{

        TextView txt_total;

        SlipGajiHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_total = itemView.findViewById(R.id.txt_total);
        }
    }

    class SlipGajiChildAdapter extends RecyclerView.Adapter<SlipGajiChildAdapter.SlipGajiChildViewHolder>{

        private List<SimpleObjectModel> listDetail;

        SlipGajiChildAdapter(List<SimpleObjectModel> listDetail){
            this.listDetail = listDetail;
        }

        @NonNull
        @Override
        public SlipGajiChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SlipGajiChildViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gaji, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SlipGajiChildViewHolder holder, int position) {
            SimpleObjectModel s = listDetail.get(position);

            if(position < listDetail.size() - 1){
                holder.txt_nomor.setText(String.valueOf(position + 1));
            }
            holder.txt_keterangan.setText(s.getId());
            holder.txt_nominal.setText(s.getValue());
        }

        @Override
        public int getItemCount() {
            return listDetail.size();
        }

        class SlipGajiChildViewHolder extends RecyclerView.ViewHolder{

            TextView txt_nomor, txt_keterangan, txt_nominal;

            SlipGajiChildViewHolder(@NonNull View itemView) {
                super(itemView);
                txt_nomor = itemView.findViewById(R.id.txt_nomor);
                txt_nominal = itemView.findViewById(R.id.txt_nominal);
                txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            }
        }
    }
}
