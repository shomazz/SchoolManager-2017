package com.school438.myapplication.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.school438.myapplication.R;
import com.school438.myapplication.SchoolManager.AdapterCustomRings;
import com.school438.myapplication.SchoolManager.DBManager;
import com.school438.myapplication.SchoolManager.Ring;

import java.util.ArrayList;

public class RingsFragment extends Fragment {

    private ListView ringsListView;
    private View v;
    private ArrayList<Ring> ringsArr;
    private AdapterCustomRings adapterRings;
    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_rings, container, false);
        dbManager = DBManager.getInstance(v.getContext());
        ringsListView = (ListView) v.findViewById(R.id.list_view_rings);
        ringsArr = dbManager.getAllRings();
        adapterRings = new AdapterCustomRings(ringsArr, v.getContext());
        ringsListView.setAdapter(adapterRings);
        View vv = inflater.inflate(R.layout.view_after_add_lesson,null);
        ringsListView.addFooterView(vv);
        vv = inflater.inflate(R.layout.list_item_day, null);
        ringsListView.addHeaderView(vv);
        return v;
    }
}
