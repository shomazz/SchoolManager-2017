package com.school438.myapplication.SchoolManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.school438.myapplication.Utils.WeekDays;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {

    private SQLiteDatabase database;
    private Context context;
    public static final String DB_NAME = "data.db";
    public static final String SHEDULE_TABLE = "SHEDULE";
    public static int version = 1;
    public static DBManager dbManager;


    public DBManager(Context context, int version) {
        super(context, DB_NAME, null, version);
        this.context = context;
        this.version = version;
        this.database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context, version);
        }
        return dbManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void copyDBFromAssets(Context context) {
        if (//!new File("/data/data/" + context.getPackageName() + "/databases/data.db").exists()){
                true) {
            try {
                String destPath = "/data/data/" + context.getPackageName() + "/databases/data.db";
                File f = new File(destPath);
                InputStream in = context.getAssets().open("old.db");
                OutputStream out = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.v("TAG", "ioexeption");
                e.printStackTrace();
            }
        }
        System.out.println("COPIED!!!");
    }

    public ArrayList<Lesson> getAllLessonsFromDB() {
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        Lesson l = new Lesson(0, 0, null, WeekDays.MONDAY);
        lessons.add(l.makeItDay());
        System.out.println("From getAllLessonsFromDB()");
        try {
            Cursor cursor = database.query(SHEDULE_TABLE, null, null, null, null, null, null);
            boolean hasMoreData = cursor.moveToFirst();
            while (hasMoreData) {
                int lessonNumber = cursor.getInt(cursor.getColumnIndex("LESSONNUMBER"));
                int classNumber = cursor.getInt(cursor.getColumnIndex("CLASSNUMBER"));
                String lessonTitle = cursor.getString(cursor.getColumnIndex("LESSON"));
                String weekDay = cursor.getString(cursor.getColumnIndex("WEEKDAY"));
                Lesson lesson = new Lesson(lessonNumber, classNumber, lessonTitle, weekDay);
                if ((!(weekDay).equals(lessons.get(lessons.size() - 1).getWeekDay()) && !lessons.get(lessons.size() - 1).isDay()))
                    lessons.add(new Lesson(0, 0, null, weekDay).makeItDay());
                lessons.add(lesson);
                hasMoreData = cursor.moveToNext();
                System.out.println(lesson.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            lessons = null;
        }
        return lessons;
    }

    public void clearTable(String tableName){
        try{
            Cursor cursor = database.query(SHEDULE_TABLE, null, null, null, null, null, null);
            System.out.println("Clearing table " + tableName + "...");
            database.execSQL("DELETE FROM " + tableName);
            System.out.println(tableName + " cleared!");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadDBFromWeb(Context context, String site){

    }

    public void putSheduleToDB(ArrayList<Lesson> lessons) {
        clearTable(SHEDULE_TABLE);
        for (int i = 0; i<lessons.size(); i++){
            System.out.println(lessons.get(i).toString());
        }
        System.out.println("Putting lessons to db...");
        try {
            for (int i = 0; i < lessons.size(); i++) {
                int numberOfLesson = lessons.get(i).getLessonNumber();
                int numberOfClass = lessons.get(i).getClassNumber();
                String lesson = lessons.get(i).getLessonTitle();
                String weekDay = lessons.get(i).getWeekDay();
                if (lessons.get(i).getLessonNumber() != 9)
                database.execSQL("INSERT INTO " + SHEDULE_TABLE + " VALUES (" + numberOfLesson
                        + " , '" + lesson + "', " + numberOfClass + ", '" + weekDay + "');");
                System.out.println("Put to db lesson (" + numberOfLesson + ", "
                            + lesson + ", " + numberOfClass + ", " + weekDay + ").");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Shedule putted!");
    }
}
