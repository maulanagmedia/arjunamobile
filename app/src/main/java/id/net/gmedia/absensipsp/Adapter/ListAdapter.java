package id.net.gmedia.absensipsp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import id.net.gmedia.absensipsp.SessionManager;
import id.net.gmedia.absensipsp.Model.CustomItem;
import id.net.gmedia.absensipsp.R;

import java.util.List;

/**
 * Created by Bayu on 24/11/2017.
 */

public class ListAdapter extends ArrayAdapter {
    Activity context;
    private List<CustomItem> items;
    private SessionManager session;

    public ListAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.listview_drawer, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private ImageView icon;
        private TextView textIcon;
        private RelativeLayout menuApproval;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        session = new SessionManager(context);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.listview_drawer, null);
            holder.icon = convertView.findViewById(R.id.gambar);
            holder.textIcon = convertView.findViewById(R.id.textDrawer);
            holder.menuApproval = convertView.findViewById(R.id.layoutMenuUtama);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.icon.setImageDrawable(convertView.getResources().getDrawable(itemSelected.getIcon()));
        holder.textIcon.setText(itemSelected.getTextIcon());
        if (session.getApproval().equals("1")) {
            holder.menuApproval.setVisibility(View.VISIBLE);
        } else {
            if (position == 2) {
                holder.menuApproval.setVisibility(View.GONE);
            } else {
                holder.menuApproval.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }
}
