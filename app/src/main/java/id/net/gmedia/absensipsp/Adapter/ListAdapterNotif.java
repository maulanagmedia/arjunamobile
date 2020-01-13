package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.net.gmedia.absensipsp.Model.ModelNotif;
import id.net.gmedia.absensipsp.R;

//import co.id.gmedia.pullens.R;

public class ListAdapterNotif extends RecyclerView.Adapter<ListAdapterNotif.TemplateViewHolder> {

    private Activity activity;
    private List<ModelNotif> listNotif;

    public ListAdapterNotif(Activity activity, List<ModelNotif>listKopi){
        this.activity= activity;
        this.listNotif= listKopi;
    }
    @NonNull
    @Override
    public ListAdapterNotif.TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new TemplateViewHolder(LayoutInflater.from(activity)
                .inflate(R.layout.adapter_notif, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterNotif.TemplateViewHolder holder, int i) {
        final ModelNotif item = listNotif.get(i);
        final int final_position = i;
        holder.tv_notif.setText(listNotif.get(i).getItem3());
        holder.tv_tgl.setText(listNotif.get(i).getItem4());


    }

    @Override
    public int getItemCount() {
        return listNotif.size();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_notif;
        private TextView tv_tgl;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_notif = (TextView) itemView.findViewById(R.id.text_notif);
            tv_tgl = (TextView) itemView.findViewById(R.id.text_tglTime);
        }
    }
}
