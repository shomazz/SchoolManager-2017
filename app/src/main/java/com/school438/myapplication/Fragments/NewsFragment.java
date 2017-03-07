package com.school438.myapplication.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
    private View v;
    private ListView newsListView;
    private ArrayList<News> newsArrayList;
    private AdapterCustomNews adapterNews;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toast errorToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_news, container, false);
        errorToast = Toast.makeText(v.getContext(), "Отсутствует подключение к интернету :(", Toast.LENGTH_SHORT);
        newsListView = (ListView) v.findViewById(R.id.list_view_news);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        loadNews();
        newsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (newsListView != null && newsListView.getChildCount() > 0) {
                    boolean firstItemVisible = newsListView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = newsListView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNews();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        View view = ((LayoutInflater) v.getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_telegram, null, false);
        newsListView.addHeaderView(view);

        return v;
    }

    public void loadNews() {
        LoadNewsTask l = new LoadNewsTask();
        l.execute();
    }

    public class LoadNewsTask extends AsyncTask<Void, Void, Void> {

        Element mainDiv;
        Elements days;
        Elements titles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                errorToast.show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapterNews = new AdapterCustomNews(newsArrayList, getActivity());
            newsListView.setAdapter(adapterNews);
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);
        }
    }
}
