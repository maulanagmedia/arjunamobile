package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.absensipsp.Model.SpModel;
import id.net.gmedia.absensipsp.R;

public class SpAdapter extends RecyclerView.Adapter<SpAdapter.SpViewHolder> {

    private Activity activity;
    private List<SpModel> listSp;

    public SpAdapter(Activity activity, List<SpModel> listSp){
        this.activity = activity;
        this.listSp = listSp;
    }

    @NonNull
    @Override
    public SpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_sp, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpViewHolder h, int position) {
        SpModel s = listSp.get(position);

        h.txt_alasan.setText(s.getAlasan());
        h.txt_date_berlaku.setText(Converter.DToString(s.getDate_berlaku()));
        h.txt_date_terbit.setText(Converter.DToString(s.getDate_terbit()));
        h.txt_jenis.setText(s.getJenis());
        h.txt_terbit.setText(s.getTerbit());
        h.txt_status.setText(s.getStatus());
    }

    @Override
    public int getItemCount() {
        return listSp.size();
    }

    class SpViewHolder extends RecyclerView.ViewHolder {

        private boolean show_detail = false;
        ImageView img_expand;
        TextView txt_date_terbit, txt_date_berlaku, txt_status, txt_jenis, txt_terbit, txt_alasan;
        View layout_detail, layout_header;

        SpViewHolder(@NonNull View i) {
            super(i);
            txt_date_terbit = i.findViewById(R.id.txt_date_terbit);
            txt_date_berlaku = i.findViewById(R.id.txt_date_berlaku);
            txt_alasan = i.findViewById(R.id.txt_alasan);
            txt_jenis = i.findViewById(R.id.txt_jenis);
            txt_terbit = i.findViewById(R.id.txt_terbit);
            txt_status = i.findViewById(R.id.txt_status);
            img_expand = i.findViewById(R.id.img_expand);
            layout_detail = i.findViewById(R.id.layout_detail);
            layout_header = i.findViewById(R.id.layout_header);

            switchLayout();

            layout_header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_detail = !show_detail;
                    switchLayout();
                }
            });
        }

        private void switchLayout(){
            if(show_detail){
                img_expand.setImageResource(R.drawable.minus);
                layout_detail.setVisibility(View.VISIBLE);
            }
            else{
                img_expand.setImageResource(R.drawable.plus);
                layout_detail.setVisibility(View.GONE);
            }
        }
    }
}
