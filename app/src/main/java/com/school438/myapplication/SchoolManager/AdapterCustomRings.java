package com.school438.myapplication.SchoolManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.school438.myapplication.R;
import com.school438.myapplication.SchoolManager.Lesson;

import java.util.ArrayList;

public class AdapterCustomRings extends BaseAdapter {

    private ArrayList<Ring> rings;
    private LayoutInflater inflater;
    private Context context;

    public AdapterCustomRings(ArrayList<Ring> rings, Context context) {
        this.rings = rings;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return rings.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View view = inflater.inflate(R.layout.list_item_rings, null);
        holder.ringNumber = (TextView) view.findViewById(R.id.rings_number);
        holder.ringTxt = (TextView) view.findViewById(R.id.rings_time);
        System.out.println(rings.get(position).getNumber() + "  " + rings.get(position).getTime());
        holder.ringNumber.setText("" + rings.get(position).getNumber());
        holder.ringTxt.setText(rings.get(position).getTime());
        return view;
    }

    public class Holder {
        TextView ringNumber;
        TextView ringTxt;
    }

}