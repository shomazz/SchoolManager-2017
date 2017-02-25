package com.school438.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.school438.myapplication.SchoolManager.Lesson;
import com.school438.myapplication.SchoolManager.News;
import com.school438.myapplication.SchoolManager.AdapterCustomLessons;
import com.school438.myapplication.SchoolManager.AdapterCustomLessonsEdit;
import com.school438.myapplication.SchoolManager.AdapterCustomNews;
import com.school438.myapplication.SchoolManager.DBManager;
import com.school438.myapplication.Utils.FloatingActionButton;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.RestClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity {

    public static final String siteTitle = "http://sch438uv.mskobr.ru/";
    public static final String user = "shamilm10031";
    public static final String token = "AQAAAAARB6_JAAQMUP88TmC_30mvkdcBetDgwWA";
    public static final String dbAdress = "http://cloud.w3bs.ru/download/allShedules.db";
    public static final String downloadedDBName = "allShedules.db";
    public static String dbPath;
    private TabHost tabHost;
    private ListView newsListView;
    private ListView lessonsListView;
    private ListView lessonsEditListView;
    private DBManager dbManager;
    private AdapterCustomNews adapterNews;
    private AdapterCustomLessons adapter;
    private AdapterCustomLessonsEdit adapterEdit;
    private ArrayList<Lesson> lessonArrayList;
    private ArrayList<News> newsArrayList;
    private Document siteDoc;
    private RestClient restClient;
    private Credentials credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dbManager = DBManager.getInstance(this);
        dbManager.copyDBFromAssets(this);
        if (dbManager.getAllLessonsFromDB() == null)
            dbManager.copyDBFromAssets(this);
        lessonArrayList = dbManager.getAllLessonsFromDB();
        setUpTabHosts();
        addFloatButton();
        createAdapters();
        initialisateListViews();
        fillListViews();
        credentials = new Credentials(user, token);
        restClient = new RestClient(credentials);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(newsArrayList.get(position).getLink()));
                startActivity(intent);
            }
        });
        dbPath = Environment.getExternalStorageDirectory().toString();
    }

    private void fillListViews(){
        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_button, null, false);
        lessonsListView.setAdapter(adapterEdit);
        lessonsListView.addFooterView(footerView);
        footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_editor_button, null, false);
        //lessonsEditListView.addFooterView(footerView);
        //lessonsEditListView.setAdapter(adapterEdit);
        LoadNewsTask l = new LoadNewsTask();
        l.execute();
    }

    private void initialisateListViews(){
        newsListView = (ListView) findViewById(R.id.list_view_news);
        lessonsListView = (ListView) findViewById(R.id.list_view_lessons);
        //lessonsEditListView = (ListView) findViewById(R.id.list_view_lessons_edit);
    }

    private void createAdapters(){
       // adapter = new AdapterCustomLessons(lessonArrayList, this);
        if (lessonArrayList == null) {
            adapterEdit = new AdapterCustomLessonsEdit(Lesson.getEmptyShedule(), this);
            System.out.println("EMPTY SHEDULE USED");
        } else {
            adapterEdit = new AdapterCustomLessonsEdit (
                    AdapterCustomLessonsEdit.makeSheduleForMainListView(lessonArrayList), this);
            //(Lesson.getEmptyShedule(), this);
            //(AdapterCustomLessonsEdit.makeSheduleForEditor(lessonArrayList), this);
            System.out.println("SHEDULE FROM DB USED");
        }
    }

    private void setUpTabHosts(){
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("1");
        tabSpec.setContent(R.id.tab_layout_news);
        tabSpec.setIndicator("Новости");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("2");
        tabSpec.setContent(R.id.tab_layout_shedule);
        tabSpec.setIndicator("Расписание");
        tabHost.addTab(tabSpec);

        /*tabSpec = tabHost.newTabSpec("3");
        tabSpec.setContent(R.id.tab_layout_edit_shedule);
        tabSpec.setIndicator("Редактор");
        tabHost.addTab(tabSpec);*/
        tabHost.setCurrentTab(0);
    }

    private void addFloatButton(){
        final FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withButtonColor(Color.WHITE)
                .withDrawable(getDrawable(R.drawable.ic_cloud_download_white_24dp))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    fabButton.onTouchDown();
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    fabButton.onTouchUp();
                    LoadDBFromDiskTask l = new LoadDBFromDiskTask();
                    l.execute();
                    Toast t = Toast.makeText(MainActivity.this, "Start downloading DB...", Toast.LENGTH_SHORT);
                    t.show();
                }
                return false;
            }
        });
    }

    public void onLinkButton(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/school438"));
        startActivity(browserIntent);
    }

    public void onEditorButton(View v) {
        PutSheduleToDBTask task = new PutSheduleToDBTask();
        task.execute();
    }

    public void onClearButton(View v){
        adapterEdit = new AdapterCustomLessonsEdit(Lesson.getEmptyShedule(), this);
        lessonsEditListView.setAdapter(adapterEdit);
        lessonsEditListView.deferNotifyDataSetChanged();
    }

    public class LoadDBFromDiskTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Гружу расписание...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                File file = new File(dbPath, downloadedDBName);
                URL url = new URL(dbAdress);
                System.out.println("Start Downloading database...");
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = 500000;
                InputStream input = new BufferedInputStream(url.openStream(), lenghtOfFile);
                OutputStream output = new FileOutputStream(file);
                System.out.println("File's absolute path : \""+ file.getAbsolutePath() + "\";");
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
            System.out.println("DB Downloaded path : \"" + dbPath + "/" + downloadedDBName + "\";");
        }
    }

    public class PutSheduleToDBTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Сохраняю расписание...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                dbManager.putSheduleToDB(adapterEdit.getLessons());
                lessonArrayList = dbManager.getAllLessonsFromDB();
                adapter = new AdapterCustomLessons(lessonArrayList, MainActivity.this);
                adapterEdit = new AdapterCustomLessonsEdit
                        (AdapterCustomLessonsEdit.makeSheduleForEditor(lessonArrayList), MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            lessonsListView.setAdapter(adapter);
            lessonsEditListView.setAdapter(adapterEdit);
            lessonsListView.deferNotifyDataSetChanged();
            lessonsEditListView.deferNotifyDataSetChanged();
            tabHost.setCurrentTab(0);
            pDialog.dismiss();
        }
    }

    public class LoadNewsTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        Element mainDiv;
        Elements days;
        Elements titles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Гружу новости...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                newsArrayList = new ArrayList<>();
                siteDoc = Jsoup.connect(siteTitle).get();
                mainDiv = siteDoc.select("div#allnews-on-main").first();
                titles = mainDiv.getElementsByClass("link");
                days = mainDiv.getElementsByClass("data");
                for (int i = 0; i < 6; i++) {
                    String title = titles.get(i).text().toString();
                    String day = days.get(i).text().toString();
                    String link = titles.get(i).attr("href");
                    News news = new News(title, day, link);
                    newsArrayList.add(news);
                    System.out.println(news.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            adapterNews = new AdapterCustomNews(newsArrayList, MainActivity.this);
            newsListView.setAdapter(adapterNews);
        }
    }
}
