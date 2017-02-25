package com.school438.myapplication.SchoolManager;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.school438.myapplication.R;
import com.school438.myapplication.SchoolManager.Lesson;

import org.w3c.dom.Text;

import java.security.Key;
import java.util.ArrayList;

public class AdapterCustomLessonsEdit extends BaseAdapter {

    private ArrayList<Lesson> lessons;
    private LayoutInflater inflater;
    private Context context;

    public AdapterCustomLessonsEdit(ArrayList<Lesson> lessons, Context context) {
        this.lessons = lessons;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < lessons.size(); i++) {
            System.out.println("EDIT" + lessons.get(i).toString());
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
        final Holder holder = new Holder();
        //initialisateViews(position, holder, convertView);
        convertView = getInflatedView(position);
        if(lessons.get(position).isDay())
            holder.weekDay = (TextView) convertView.findViewById(R.id.week_day);
        else {
            holder.lessonNumber = (TextView) convertView.findViewById(R.id.lesson_number_text_view);
            holder.lesson = (EditText) convertView.findViewById(R.id.lesson_edit_text);
            holder.classNumber = (EditText) convertView.findViewById(R.id.class_number_edit_text);
        } if (lessons.get(position).isAddNewLesson())
            holder.addLesson = (EditText) convertView.findViewById(R.id.add_lesson_edittext);
        convertView.setTag(holder);
        holder.ref = position;
        if (lessons.get(position).getLessonTitle() != null && !lessons.get(position).isDay()
                && !lessons.get(position).isAddNewLesson()) {
            holder.lessonNumber.setText("" + lessons.get(position).getLessonNumber() + " |");
            if (!lessons.get(position).getLessonTitle().equals(Lesson.EMPTY_LESSON))
                holder.lesson.setText(lessons.get(position).getLessonTitle().toString(), TextView.BufferType.EDITABLE);
            else
                holder.lesson.setHint(Lesson.EMPTY_LESSON);
            if (lessons.get(position).getClassNumber() > Lesson.EMPTY_CLASS_NUMBER)
                holder.classNumber.setText("" + lessons.get(position).getClassNumber(), TextView.BufferType.EDITABLE);
            else
                holder.classNumber.setHint("Каб.");
            addTextWatchers(holder);
        } else {
            if (lessons.get(position).isDay())
                holder.weekDay.setText(lessons.get(position).getWeekDay());
            else if (lessons.get(position).isAddNewLesson())
                holder.addLesson.setHint(Lesson.EMPTY_LESSON);
        }
        return convertView;
    }

    private void addTextWatchers(final Holder holder){
        holder.lesson.addTextChangedListener(new TextWatcher() {

            String word;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                word = holder.lesson.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                //   if (holder.lesson.getText().toString().charAt(holder.lesson.getText().toString().length()-1) == )
                if (holder.lesson.getText().toString().equals(""))
                    holder.lesson.setHint(Lesson.EMPTY_LESSON);
                lessons.get(holder.ref).setLessonTitle(holder.lesson.getText().toString());
            }
        });
        holder.classNumber.addTextChangedListener(new TextWatcher() {

            String word;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                word = holder.classNumber.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                try {
                    if (holder.classNumber.getText().toString().equals("")) {
                        lessons.get(holder.ref).setClassNumber(Lesson.EMPTY_CLASS_NUMBER);
                        holder.classNumber.setHint("Каб.");
                    } else {
                        lessons.get(holder.ref).setClassNumber(Integer.parseInt(arg0.toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Введите номер кабинета!", Toast.LENGTH_SHORT).show();
                    System.out.println(word);
                    holder.classNumber.setText(word);
                }
            }
        });
    }

    public static ArrayList<Lesson> makeSheduleForEditor(ArrayList<Lesson> dbArr) {
        ArrayList<Lesson> mainArr = Lesson.getEmptyShedule();
        try {
            int mainArrIndex = 1;
            int dbArrIndex = 1;
            System.out.println("mainArr Size == " + mainArr.size() + " ; dbArr Size == " + dbArr.size() + " ;");
            for (; dbArrIndex < dbArr.size(); ) {
                if (dbArr.get(dbArrIndex).getLessonTitle() != Lesson.EMPTY_LESSON &&
                        !dbArr.get(dbArrIndex).isDay()) {
                    mainArr.get(mainArrIndex).changeLesson(dbArr.get(dbArrIndex));
                    dbArrIndex++;
                    mainArrIndex++;
                } else if (dbArr.get(dbArrIndex).isDay()) {
                    mainArrIndex += Lesson.LESSONS_IN_DAY - dbArr.get(dbArrIndex - 1).getLessonNumber();
                    mainArrIndex++;
                    dbArrIndex++;
                    System.out.println("mainArrindex == " + mainArrIndex + " ; dbArrIndex == " + dbArrIndex + " ;");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainArr;
    }

    public static ArrayList<Lesson> makeSheduleForMainListView(ArrayList<Lesson> dbArr){
        System.out.println("FROM makeSheduleForMainListView()");
        ArrayList<Lesson> mainArr = new ArrayList<Lesson>();
        for(int i = 0; i < dbArr.size(); i++){
            mainArr.add(dbArr.get(i));
            if((i != dbArr.size() - 1 && !dbArr.get(i).isDay() && dbArr.get(i+1).isDay()) || i == dbArr.size()-1)
                mainArr.add(new Lesson(0, 0, Lesson.ADD_NEW_LESSON, dbArr.get(i).getWeekDay()));
        }
        return mainArr;
    }

    public ArrayList<Lesson> getLessons() {
        ArrayList<Lesson> lessons = new ArrayList<>();
        System.out.println("From get Lessons: ");
        for (int i = 1; i < this.lessons.size(); i++) {
            System.out.println(this.lessons.get(i).toString());
            if (this.lessons.get(i).isDay()) {
                System.out.println("it is day!");
                if (!(this.lessons.get(i + 1).isEmpty() && !this.lessons.get(i + 2).isEmpty()))
                    lessons.add(this.lessons.get(i));
            } else if (!this.lessons.get(i).isEmpty()) {
                lessons.add(this.lessons.get(i));
                System.out.println("its not empty and not day");
            }
        }
        return lessons;
    }

    public View getInflatedView(int position) {
        if (!lessons.get(position).isAddNewLesson()) {
            if (!lessons.get(position).isDay()) {
                return inflater.inflate(R.layout.list_item_lesson_edit, null);
            } else {
                return inflater.inflate(R.layout.list_item_day, null);
            }
        } else {
            System.out.println(lessons.get(position).toString() + "     Lesson is addnewleson!");
            return inflater.inflate(R.layout.list_item_add_lesson, null);
        }
    }

    public class Holder {
        TextView lessonNumber;
        EditText lesson;
        EditText classNumber;
        TextView weekDay;
        EditText addLesson;
        int ref;
    }

}