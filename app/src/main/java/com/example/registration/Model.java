package com.example.registration;

import java.util.Comparator;

class Model {
    String id, title, desc, date, time;

    public Model(String id, String title, String desc, String date, String time) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    static class SortByDate implements Comparator<Model> {
        @Override
        public int compare(Model a, Model b) {
            return a.date.compareTo(b.date);
        }

    }
}
