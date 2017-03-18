package com.school438.myapplication.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.school438.myapplication.MainActivity;
import com.school438.myapplication.R;
import com.school438.myapplication.SchoolManager.AdapterCustomLessonsEdit;
import com.school438.myapplication.SchoolManager.DBManager;
import com.school438.myapplication.SchoolManager.Lesson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class SheduleFragment extends Fragment {

    private DBManager dbManager;
    private ArrayList<Lesson> lessonsArray;
    private ListView lessonsListView;
    private AdapterCustomLessonsEdit adapterLessons;
    private View v;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void saveShedule() {
        PutSheduleToDBTask p = new PutSheduleToDBTask(MainActivity.CURRENT_TABLE_NAME);
        p.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_shedule, container, false);
        context = v.getContext();
        lessonsListView = (ListView) v.findViewById(R.id.list_view_lessons);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_shedule);
        dbManager = DBManager.getInstance(v.getContext());
        lessonsArray = dbManager.getAllLessonsFromLocalDB(MainActivity.CURRENT_TABLE_NAME);
        if (lessonsArray == null) {
            adapterLessons = new AdapterCustomLessonsEdit(Lesson.getEmptyShedule(), v.getContext());
            System.out.println("EMPTY SHEDULE USED");
        } else {
            adapterLessons = new AdapterCustomLessonsEdit(
                    AdapterCustomLessonsEdit.makeSheduleForMainListView(lessonsArray), v.getContext());
            System.out.println("SHEDULE FROM DB USED");
        }
        lessonsListView.setAdapter(adapterLessons);
        lessonsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (lessonsListView != null && lessonsListView.getChildCount() > 0) {
                    boolean firstItemVisible = lessonsListView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = lessonsListView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dbManager = DBManager.getInstance(v.getContext());
                lessonsArray = dbManager.getAllLessonsFromLocalDB(MainActivity.CURRENT_TABLE_NAME);
                if (lessonsArray == null) {
                    adapterLessons = new AdapterCustomLessonsEdit(Lesson.getEmptyShedule(), v.getContext());
                    System.out.println("EMPTY SHEDULE USED");
                } else {
                    adapterLessons = new AdapterCustomLessonsEdit(
                            AdapterCustomLessonsEdit.makeSheduleForMainListView(lessonsArray), v.getContext());
                    System.out.println("SHEDULE FROM DB USED");
                }
                lessonsListView.setAdapter(adapterLessons);
                lessonsListView.deferNotifyDataSetChanged();
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        return v;
    }

    public void refreshListView() {
        try {
            lessonsArray = dbManager.getAllLessonsFromLocalDB(MainActivity.CURRENT_TABLE_NAME);
            adapterLessons = new AdapterCustomLessonsEdit(
                    AdapterCustomLessonsEdit.makeSheduleForMainListView(lessonsArray), v.getContext());
            lessonsListView.setAdapter(adapterLessons);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class PutSheduleToDBTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String tableName;

        public PutSheduleToDBTask(String tableName) {
            this.tableName = tableName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Сохраняю расписание...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                dbManager.putSheduleToDB(adapterLessons.getLessonsForDB(), tableName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            lessonsArray = dbManager.getAllLessonsFromLocalDB(tableName);
            adapterLessons = new AdapterCustomLessonsEdit
                    (AdapterCustomLessonsEdit.makeSheduleForMainListView(lessonsArray), context);
            lessonsListView.setAdapter(adapterLessons);
            lessonsListView.deferNotifyDataSetChanged();
            if (pDialog != null)
                pDialog.dismiss();
        }
    }
}