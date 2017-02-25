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

public class AdapterCustomRings extends BaseAdapter{

    private ArrayList <Lesson> lessons;
    private LayoutInflater inflater;
    private Context context;

    public AdapterCustomRings(ArrayList <Lesson> lessons, Context context){
        this.lessons = lessons;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0; i<lessons.size(); i++) {
            System.out.println("SHEDULE" + lessons.get(i).toString());
        }
    }

    @Override
    public int getCount() {
        return lessons.size();
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
        View view;
        if (!lessons.get(position).isDay()) {
            view = inflater.inflate(R.layout.list_item_lesson, null);
        } else {
            view = inflater.inflate(R.layout.list_item_day, null);
        }
        holder.weekDay = (TextView)view.findViewById(R.id.week_day);
        holder.lessonNumber = (TextView)view.findViewById(R.id.lesson_number_text_view);
        holder.lesson = (TextView)view.findViewById(R.id.lesson_text_view);
        holder.classNumber = (TextView)view.findViewById(R.id.class_number_text_view);
        if(lessons.get(position).getLessonTitle() != null && !lessons.get(position).isDay()){
            holder.lessonNumber.setText(""+lessons.get(position).getLessonNumber()+" |");
            holder.lesson.setText(lessons.get(position).getLessonTitle());
            holder.classNumber.setText(""+lessons.get(position).getClassNumber());
        } else if (lessons.get(position).isDay()){
            holder.weekDay.setText(lessons.get(position).getWeekDay());
        }
        return view;
    }

    public  class Holder {
        TextView lessonNumber;
        TextView lesson;
        TextView classNumber;
        TextView weekDay;
    }

}