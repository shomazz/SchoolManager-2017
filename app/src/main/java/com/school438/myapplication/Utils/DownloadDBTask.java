package com.school438.myapplication.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.school438.myapplication.MainActivity;
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

public class DownloadDBTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog pDialog;
    private Context context;
    private String dbPath;


    public DownloadDBTask (String dbPath, Context context){
        this.context = context;
        this.dbPath = dbPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Гружу расписание классов...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            File file = new File(dbPath, DBManager.LOCAL_DB_NAME);
            URL url = new URL(DBManager.DB_ADRESS + "/" + DBManager.LOCAL_DB_NAME);
            System.out.println("Start Downloading database...");
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = 500000;
            InputStream input = new BufferedInputStream(url.openStream(), lenghtOfFile);
            OutputStream output = new FileOutputStream(file);
            System.out.println("File's absolute path : \"" + file.getAbsolutePath() + "\";");
            byte data[] = new byte[lenghtOfFile];
            long total = 0;
            int count = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pDialog.dismiss();
        System.out.println("DB Downloaded path : \"" + dbPath + DBManager.LOCAL_DB_NAME + "\";");
    }
}
