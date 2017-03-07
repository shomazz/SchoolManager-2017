package com.school438.myapplication.SchoolManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.school438.myapplication.R;
import com.school438.myapplication.SchoolManager.Lesson;
import com.school438.myapplication.SchoolManager.News;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AdapterCustomNews extends BaseAdapter{

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<News> newsArr;

    public AdapterCustomNews(ArrayList<News> newsArr, Context context){
        this.newsArr = newsArr;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsArr.size();
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
        final int pos = position;
        View view = inflater.inflate(R.layout.list_item_news, null);
        holder.newsTitle = (TextView)view.findViewById(R.id.news_title);
        holder.day = (TextView)view.findViewById(R.id.news_day);
        holder.button = (TextView)view.findViewById(R.id.news_button);
        holder.newsTitle.setText(newsArr.get(position).getTitle());
        holder.day.setText(newsArr.get(position).getDay());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsArr.get(pos).getLink()));
                context.startActivity(intent);
            }
        });
        return view;
    }

    public class Holder {
        TextView newsTitle;
        TextView day;
        TextView button;
    }
}