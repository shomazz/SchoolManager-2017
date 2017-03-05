package com.school438.myapplication.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.school438.myapplication.MainActivity;
import com.school438.myapplication.R;
import com.school438.myapplication.SchoolManager.AdapterCustomNews;
import com.school438.myapplication.SchoolManager.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class NewsFragment extends Fragment {

    public static final String siteTitle = "http://sch438uv.mskobr.ru/";
    private Document siteDoc;

    private ListView newsListView;
    private ArrayList <News> newsArrayList;
    private AdapterCustomNews adapterNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        newsListView = (ListView)v.findViewById(R.id.list_view_news);
        loadNews();
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(newsArrayList.get(position).getLink()));
                startActivity(intent);
            }
        });
        return v;
    }

    public void loadNews(){
        LoadNewsTask l = new LoadNewsTask();
        l.execute();
    }

    public class LoadNewsTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        Element mainDiv;
        Elements days;
        Elements titles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
            adapterNews = new AdapterCustomNews(newsArrayList, getActivity());
            newsListView.setAdapter(adapterNews);
        }
    }
}
