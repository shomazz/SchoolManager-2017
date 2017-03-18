package com.school438.myapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.school438.myapplication.R;
import com.school438.myapplication.Utils.DownloadDBTask;

public class SettingsFragment extends Fragment {

    private DownloadDBTask task;
    private View v;
    private Button downloadDB;
    private Context context;
    private String dbPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        context = v.getContext();
        downloadDB = (Button) v.findViewById(R.id.download_button);
        dbPath =  "/data/data/" + context.getPackageName() + "/databases/";
        task = new DownloadDBTask(dbPath, context);
        downloadDB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                task.execute();
                Toast t = Toast.makeText(context,"Расписание обновлено!", Toast.LENGTH_SHORT);
                t.show();
            }

        });
        return v;
    }
}
