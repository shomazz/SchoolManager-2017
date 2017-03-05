package com.school438.myapplication.SchoolManager;

import com.school438.myapplication.Fragments.NewsFragment;
import com.school438.myapplication.MainActivity;

public class News {

    private String title;
    private String day;
    private String link = NewsFragment.siteTitle;

    public News (String title, String day, String link){
        this.title = title;
        this.day = day;
        this.link += link.substring(1);
    }

    public String getTitle(){
        return title;
    }

    public String getDay(){
        return day;
    }

    @Override
    public String toString(){
        return day + "   " + title;
    }

    public String getLink(){
        return link;
    }

}
