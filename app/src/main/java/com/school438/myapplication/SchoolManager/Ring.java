package com.school438.myapplication.SchoolManager;

public class Ring {

    private String time;
    private int number;

    public Ring (int number, String time) {
        this.number = number;
        this.time = time;
    }

    public String getTime (){
        return time;
    }

    public int getNumber (){
        return number;
    }

}
