package id.net.gmedia.absensipsp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.net.gmedia.absensipsp.Model.Child;
import id.net.gmedia.absensipsp.Model.Parent;
import id.net.gmedia.absensipsp.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Bayu on 30/11/2017.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Parent> parentList;
    private HashMap<String, List<Child>> childList;

    public ExpandableListViewAdapter(Context context, List<Parent> parentList, HashMap<String, List<Child>> childList) {
        this.context = context;
        this.parentList = parentList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return this.parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (this.childList.get(this.parentList.get(groupPosition).getMinggu()) != null) ?
                this.childList.get(this.parentList.get(groupPosition).getMinggu()).size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.parentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childList.get(this.parentList.get(groupPosition).getMinggu()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getGroupTypeCount() {
        return 2;
    }

    @Override
    public int getGroupType(int groupPosition) {
        int hasil;
        if(groupPosition % 2 == 1){
            hasil = 0;
        }else{
            hasil = 1;
        }
        return hasil;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Parent parent1 = (Parent) getGroup(groupPosition);
        int tipeViewList = getGroupType(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_expand, null);
        }

        TextView minggu = convertView.findViewById(R.id.minggu1uanglembur);
        TextView tanggal = convertView.findViewById(R.id.tanggal1uanglembur);
        TextView jumlah = convertView.findViewById(R.id.jumlaharga1);
        minggu.setText(parent1.getMinggu());
        minggu.setTypeface(null, Typeface.BOLD);
        String periode = parent1.getTanggaldari() + "-" + parent1.getTanggalsampai();
        tanggal.setText(periode);
        jumlah.setText(parent1.getJumlah());
       /* holder.minggu = (TextView) convertView.findViewById(R.id.minggu1uanglembur);
        holder.tanggal = (TextView) convertView.findViewById(R.id.tanggal1uanglembur);
        holder.jumlah = (TextView) convertView.findViewById(R.id.jumlaharga1);
        holder.minggu.setTypeface(null, Typeface.BOLD);
        holder.minggu.setText(parent1.getMinggu());
        holder.tanggal.setText(parent1.getTanggaldari() + "-" + parent1.getTanggalsampai());
        holder.jumlah.setText(parent1.getJumlah());*/
        if (tipeViewList==0) {
            RelativeLayout a = convertView.findViewById(R.id.layoutparent);
            a.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
        }
        ImageView dropdown = convertView.findViewById(R.id.dropdown);
        int img = isExpanded? R.drawable.panah_keatas : R.drawable.panah_kebawah;
        dropdown.setImageResource(img);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Child child = (Child) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_expand, null);
        }
        TextView tanggalchild = convertView.findViewById(R.id.tanggalchild);
        TextView jumlahchild = convertView.findViewById(R.id.jumlahchild);
        tanggalchild.setTextColor(Color.parseColor("#FFFFFF"));
        tanggalchild.setText(child.getTanggalchild());
        jumlahchild.setTextColor(Color.parseColor("#FFFFFF"));
        jumlahchild.setText(child.getJumlahexpand());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
