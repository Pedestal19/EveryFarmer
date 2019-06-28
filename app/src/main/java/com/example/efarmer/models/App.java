package com.example.efarmer.models;

import android.app.Activity;
import android.app.Application;

/**
 * Created by Hosanna on 23/09/2016.
 */
public class App extends Application {

    private String id, title, duration, url;
    private String SqlQuery, command;


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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSqlQuery() {
        return SqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        SqlQuery = sqlQuery;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
