package com.school438.myapplication.SchoolManager;

import com.school438.myapplication.Utils.WeekDays;

import java.util.ArrayList;

public class Lesson {

    public static final String EMPTY_CLASS_NUMBER = "-1";
    public static final int LESSONS_IN_DAY = 8;
    public static final String ADD_NEW_LESSON = "ADDNEWLESSS";
    public static final String EMPTY_LESSON = "Добавить урок";
    private int lessonNumber;
    private String classNumber;
    private String lessonTitle;
    private String weekDay;
    private String DAY = null;
    private boolean isView = false;

    public Lesson(int lessonNumber, String classNumber, String lessonTitle, String weekDay) {
        this.lessonNumber = lessonNumber;
        this.lessonTitle = lessonTitle;
        this.classNumber = classNumber;
        this.weekDay = weekDay;
    }

    public Lesson makeItView(){
        isView = true;
        return this;
    }

    public boolean isView(){
        return isView;
    }

    public void incLessonNumber(){
        this.lessonNumber ++;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public void makeItAddLesson (){
        this.lessonTitle = ADD_NEW_LESSON;
    }

    public boolean isAddNewLesson(){
        if (lessonTitle != null) {
            if (lessonTitle.equals(ADD_NEW_LESSON))
                return true;
            else
                return false;
        } else
            return false;
    }

    public Lesson makeItDay() {
        DAY = weekDay;
        return this;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public void setWeekDay(int i) {
        switch (i) {
            case 1:
                weekDay = WeekDays.MONDAY;
                break;
            case 2:
                weekDay = WeekDays.TUESDAY;
                break;
            case 3:
                weekDay = WeekDays.WEDNESDAY;
                break;
            case 4:
                weekDay = WeekDays.THURSDAY;
                break;
            case 5:
                weekDay = WeekDays.FRIDAY;
                break;
            case 6:
                weekDay = WeekDays.SATURDAY;
                break;
            case 7:
                weekDay = WeekDays.SUNDAY;
                break;
        }
    }

    @Override
    public String toString() {
        if (!isDay()) {
            return "" + lessonNumber + "    " + lessonTitle + " "
                    + classNumber;
        } else {
            return weekDay;
        }
    }

    public boolean isDay() {
        if (DAY == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isEmpty() {
        if (lessonTitle != null) {
            if (!isDay()) {
                return lessonTitle.equals(EMPTY_LESSON);
            } else {
                return isDay();
            }
        } else {
            return true;
        }
    }

    public void changeLesson(Lesson newLesson) {
        if (newLesson.isDay()) {
            this.weekDay = newLesson.getWeekDay();
            makeItDay();
        } else {
            this.lessonNumber = newLesson.getLessonNumber();
            this.lessonTitle = newLesson.getLessonTitle();
            this.classNumber = newLesson.getClassNumber();
            this.weekDay = newLesson.getWeekDay();
        }
    }

    public static ArrayList<Lesson> getEmptyShedule() {
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        boolean hasNextLesson = true;
        int weekDay = 1;
        lessons.add(new Lesson(0, "", "", WeekDays.MONDAY).makeItDay());
        for (int i = 1; hasNextLesson; i++) {
            Lesson lesson = new Lesson(0, null, null, null);
            if (weekDay < 7) {
                switch (weekDay) {
                    case 1:
                        lesson = new Lesson(i, Lesson.EMPTY_CLASS_NUMBER, Lesson.EMPTY_LESSON, WeekDays.MONDAY);
                        break;
                    case 2:
                        lesson = new Lesson(i, Lesson.EMPTY_CLASS_NUMBER, Lesson.EMPTY_LESSON, WeekDays.TUESDAY);
                        break;
                    case 3:
                        lesson = new Lesson(i, Lesson.EMPTY_CLASS_NUMBER, Lesson.EMPTY_LESSON, WeekDays.WEDNESDAY);
                        break;
                    case 4:
                        lesson = new Lesson(i, Lesson.EMPTY_CLASS_NUMBER, Lesson.EMPTY_LESSON, WeekDays.THURSDAY);
                        break;
                    case 5:
                        lesson = new Lesson(i, Lesson.EMPTY_CLASS_NUMBER, Lesson.EMPTY_LESSON, WeekDays.FRIDAY);
                        break;
                    case 6:
                        lesson = new Lesson(i, Lesson.EMPTY_CLASS_NUMBER, Lesson.EMPTY_LESSON, WeekDays.SATURDAY);
                        break;
                }
                if (i > LESSONS_IN_DAY) {
                    weekDay++;
                    lesson.setWeekDay(weekDay);
                    lesson.makeItDay();
                    i = 0;
                }
                if (lesson.getWeekDay() != WeekDays.SUNDAY)
                    lessons.add(lesson);
            } else {
                hasNextLesson = false;
            }
        }
        return lessons;
    }

}
