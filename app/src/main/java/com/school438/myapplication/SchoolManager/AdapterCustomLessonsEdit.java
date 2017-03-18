package com.school438.myapplication.SchoolManager;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.school438.myapplication.R;

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
        convertView = getInflatedView(position);
        if (!lessons.get(position).isView()) {
            if (!lessons.get(position).isAddNewLesson()) {
                if (!lessons.get(position).isDay()) {
                    holder.lessonNumber = (TextView) convertView.findViewById(R.id.lesson_number_text_view);
                    holder.lesson = (EditText) convertView.findViewById(R.id.lesson_edit_text);
                    holder.classNumber = (EditText) convertView.findViewById(R.id.class_number_edit_text);
                    convertView.setTag(holder);
                    holder.ref = position;
                    if (lessons.get(position).getLessonTitle() != null) {
                        holder.lessonNumber.setText("" + lessons.get(position).getLessonNumber());
                        if (!lessons.get(position).isEmpty()) {
                            if (lessons.get(position).getClassNumber() == null || lessons.get(position).getClassNumber().equals("null"))
                                holder.classNumber.setHint("Каб.");
                            else
                                holder.classNumber.setText("" + lessons.get(position).getClassNumber(), TextView.BufferType.EDITABLE);
                            holder.lesson.setText(lessons.get(position).getLessonTitle().toString(), TextView.BufferType.EDITABLE);
                        } else {
                            holder.lesson.setHint(Lesson.EMPTY_LESSON);
                            holder.classNumber.setHint("каб.");
                        }
                        addTextWatchersToLessonsET(holder);
                    }
                } else {
                    holder.weekDay = (TextView) convertView.findViewById(R.id.week_day);
                    holder.weekDay.setText(lessons.get(position).getWeekDay());
                }
            } else {
                holder.lessonNumberEdit = (EditText) convertView.findViewById(R.id.add_lesson_number_edittext);
                holder.lessonNumberEdit.setVisibility(View.GONE);
                holder.lesson = (EditText) convertView.findViewById(R.id.add_lesson_edittext);
                holder.lesson.setHint(Lesson.EMPTY_LESSON);
                holder.classNumber = (EditText) convertView.findViewById(R.id.add_lesson_room_edittext);
                holder.button = (ImageButton) convertView.findViewById(R.id.add_lesson_button);
                addTextWatcherToAddLessonsET(holder);
                setCLickListennerToAddLesson(holder, position);
            }
        }
        return convertView;
    }

    private void setCLickListennerToAddLesson(final Holder holder, final int position) {
        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    int lessonNumber = Integer.parseInt(holder.lessonNumberEdit.getText().toString());
                    String title = holder.lesson.getText().toString();
                    String classNumber = holder.classNumber.getText().toString();
                    String weekDay = lessons.get(position).getWeekDay().toString();
                    Lesson lesson = new Lesson(lessonNumber, classNumber, title, weekDay);
                    addLessonToArray(lesson, weekDay, lessonNumber);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    Toast t = Toast.makeText(context, "Введите номер класса и название урока", Toast.LENGTH_SHORT);
                    t.show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void addLessonToArray(Lesson lesson, String weekDay, int lessonNumber) {
        boolean founded = false;
        int position = 0;
        for (int i = 0; !founded; i++) {
            if (lessons.get(i).getWeekDay() != null && lessons.get(i).getWeekDay().equals(weekDay)) {
                position = i;
                founded = true;
            }
        }
        this.lessons.add(position + lessonNumber, lesson);
        founded = false;
        for (int k = 1 + position + lessonNumber; !founded; k++) {
            if (!lessons.get(k).isAddNewLesson())
                lessons.get(k).incLessonNumber();
            else founded = true;
        }
    }

    private void addTextWatcherToAddLessonsET(final Holder holder) {
        holder.lesson.addTextChangedListener(new TextWatcher() {

            String word;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (holder.lesson.getText().toString().equals("")) {
                    holder.lesson.setHint(Lesson.EMPTY_LESSON);
                } else if (arg0.toString().length() > arg1) {
                    char c = holder.lesson.getText().toString().charAt(arg1);
                    System.out.println("" + c + "    " + arg0 + "   " + (int) c + "");
                    if (c == 0 || c == 10) {
                        holder.lesson.clearFocus();
                        holder.lesson.setText(word);
                    }
                    holder.lessonNumberEdit.setVisibility(View.VISIBLE);
                    holder.classNumber.setHint("Каб.");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                word = holder.lesson.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (holder.lesson.getText().toString().equals("")) {
                    holder.lesson.setHint(Lesson.EMPTY_LESSON);
                    holder.classNumber.setHint("");
                    holder.lessonNumberEdit.setVisibility(View.GONE);
                }

            }
        });
        holder.classNumber.addTextChangedListener(new TextWatcher() {

            String word;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (holder.classNumber.getText().toString().equals("")) {
                    lessons.get(holder.ref).setClassNumber(Lesson.EMPTY_CLASS_NUMBER);
                    holder.classNumber.setHint("");
                } else if (arg0.toString().length() > arg1) {
                    char c = holder.classNumber.getText().toString().charAt(arg1);
                    System.out.println("" + c + "    " + arg0 + "   " + (int) c + "");
                    if (c == 10) {
                        holder.classNumber.clearFocus();
                        holder.classNumber.setText(word);
                    } else if (c >= 48 && c <= 57) {

                    } else {
                        Toast t = Toast.makeText(context, "Введите номер кабинета", Toast.LENGTH_SHORT);
                        t.show();
                        holder.classNumber.setText(word);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                word = holder.classNumber.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (holder.classNumber.getText().toString().equals("")) {
                    lessons.get(holder.ref).setClassNumber(Lesson.EMPTY_CLASS_NUMBER);
                    holder.classNumber.setHint("");
                }
            }
        });
        holder.lessonNumberEdit.addTextChangedListener(new TextWatcher() {
            String word;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (holder.lessonNumberEdit.getText().toString().equals("")) {
                    holder.lessonNumberEdit.setHint("№");
                } else if (arg0.toString().length() > arg1) {
                    char c = holder.lessonNumberEdit.getText().toString().charAt(arg1);
                    System.out.println("" + c + "    " + arg0 + "   " + (int) c + "");
                    if (c == 0 || c == 10) {
                        holder.lessonNumberEdit.clearFocus();
                        holder.lessonNumberEdit.setText(word);
                    } else if (arg0.toString().length() <= 1 && c >= 48 && c <= 57) {

                    } else {
                        Toast t = Toast.makeText(context, "Введите номер урока", Toast.LENGTH_SHORT);
                        t.show();
                        holder.lessonNumberEdit.setText(word);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                word = holder.lessonNumberEdit.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (holder.lessonNumberEdit.getText().toString().equals(""))
                    holder.lessonNumberEdit.setHint("№");
            }
        });
    }

    private void addTextWatchersToLessonsET(final Holder holder) {

        holder.lesson.addTextChangedListener(new TextWatcher() {

            String word;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (holder.lesson.getText().toString().equals("")) {
                    holder.lesson.setHint(Lesson.EMPTY_LESSON);
                } else if (arg0.toString().length() > arg1) {
                    char c = holder.lesson.getText().toString().charAt(arg1);
                    System.out.println("" + c + "    " + arg0 + "   " + (int) c + "");
                    if (c == 0 || c == 10) {
                        holder.lesson.clearFocus();
                        holder.lesson.setText(word);
                    } else {
                        lessons.get(holder.ref).setLessonTitle(arg0.toString());
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                word = holder.lesson.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (holder.lesson.getText().toString().equals(""))
                    holder.lesson.setHint(Lesson.EMPTY_LESSON);
            }
        });
        holder.classNumber.addTextChangedListener(new TextWatcher() {

            String word;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (holder.classNumber.getText().toString().equals("")) {
                    lessons.get(holder.ref).setClassNumber(Lesson.EMPTY_CLASS_NUMBER);
                    holder.classNumber.setHint("Каб.");
                } else if (arg0.toString().length() > arg1) {
                    char c = holder.classNumber.getText().toString().charAt(arg1);
                    System.out.println("" + c + "    " + arg0 + "   " + (int) c + "");
                    if (c == 10) {
                        holder.classNumber.clearFocus();
                        holder.classNumber.setText(word);
                    } else if (c >= 48 && c <= 57) {
                        lessons.get(holder.ref).setClassNumber(arg0.toString());
                    } else {
                        Toast t = Toast.makeText(context, "Введите номер кабинета", Toast.LENGTH_SHORT);
                        t.show();
                        holder.classNumber.setText(word);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                word = holder.classNumber.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (holder.classNumber.getText().toString().equals(""))
                    holder.classNumber.setHint("Каб.");
            }
        });
    }


    public static ArrayList<Lesson> makeSheduleForMainListView(ArrayList<Lesson> dbArr) {
        System.out.println("FROM makeSheduleForMainListView()");
        ArrayList<Lesson> mainArr = new ArrayList<Lesson>();
        for (int i = 0; i < dbArr.size(); i++) {
            mainArr.add(dbArr.get(i));
            if ((i != dbArr.size() - 1 && !dbArr.get(i).isDay() && dbArr.get(i + 1).isDay()) || i == dbArr.size() - 1) {
                mainArr.add(new Lesson(0, "0", Lesson.ADD_NEW_LESSON, dbArr.get(i).getWeekDay()));
                mainArr.add(new Lesson(-2, "0", null, null).makeItView());
            }
        }
        return mainArr;
    }

    public ArrayList<Lesson> getLessonsForDB() {
        ArrayList<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < this.lessons.size(); i++) {
            if (!(this.lessons.get(i).isDay() || this.lessons.get(i).isEmpty()
                    || this.lessons.get(i).isAddNewLesson() || this.lessons.get(i).isView())) {
                lessons.add(this.lessons.get(i));
                System.out.println(this.lessons.get(i).toString());
            }
        }
        return lessons;
    }

    public View getInflatedView(int position) {
        if (!lessons.get(position).isView()) {
            if (!lessons.get(position).isAddNewLesson()) {
                if (!lessons.get(position).isDay()) {
                    return inflater.inflate(R.layout.list_item_lesson, null);
                } else {
                    return inflater.inflate(R.layout.list_item_day, null);
                }
            } else {
                return inflater.inflate(R.layout.list_item_add_lesson, null);
            }
        } else
            return inflater.inflate(R.layout.view_after_add_lesson, null);
    }

    public class Holder {
        TextView lessonNumber;
        EditText lesson;
        EditText classNumber;
        TextView weekDay;
        int ref;
        ImageButton button;
        EditText lessonNumberEdit;
    }

}