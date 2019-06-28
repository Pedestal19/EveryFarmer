package com.example.efarmer.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.efarmer.models.App;

import java.io.IOException;


/**
 * Created by Hosanna on 05/10/2016.
 */
public class DataDB {
    private static final String TAG = "DataDB";
    App appState;
    App app;
    MyDatabaseHelper myDBconnection;
    public MyDatabaseHelper myConnection(Context context) {
        myDBconnection = MyDatabaseHelper.getInstance(context);
        try {
            myDBconnection.createDataBase();
        } catch (IOException e) {
        }
        if (myDBconnection.checkDataBase()) {
            myDBconnection.openDataBase();
        }
        return myDBconnection;
    }











}
