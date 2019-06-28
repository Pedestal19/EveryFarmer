package com.example.efarmer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hosanna on 27/06/2019.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper singletonInstance;
    private static String DB_PATH;
    private static final String DB_NAME = "every_farmer.db";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase myDataBase;
    private Context myContext = null;


    public static synchronized MyDatabaseHelper getInstance(Context context){

        if(singletonInstance == null){
            singletonInstance = new MyDatabaseHelper(context.getApplicationContext());
        }

        return singletonInstance;
    }


    private MyDatabaseHelper(Context context)
    {

        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

    }

    // TODO: 23/09/2016 onCreate method
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    // TODO: 23/09/2016 onUpgrade method
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w("DB", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        if(newVersion>oldVersion){
            try {
                myContext.deleteDatabase(DB_NAME);
                createDataBase();
                openDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: 23/09/2016 to create database
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if(dbExist){
            //do nothing - database already exist
            Log.w("DB", "do nothing - database already exist ");

        }else{
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    // TODO: 23/09/2016 Check if database exist
    public boolean checkDataBase()
    {
//        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            Log.w("DB", "Checked and found database ");
        }catch(SQLiteException e){
            //database does't exist yet.
            Log.w("DB", "Database does not exist after checking ");
        }
        if(myDataBase != null){
            myDataBase.close();
        }
        return myDataBase != null ? true : false;
    }

    // TODO: 23/09/2016 Copy database
    private void copyDataBase() throws IOException
    {
        Log.w("DB", "Copying database... ");
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        Log.w("DB", "Opening database... ");
        String myPath = DB_PATH + DB_NAME;
        myDataBase = myDataBase.openDatabase(myPath, null, myDataBase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public long onInsertOrUpdate(ContentValues values, String _tableName)
    {
        long id;
        Log.d("onInsertOrUpdate", "insertOrIgnore on " + values);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            id=db.insertWithOnConflict(_tableName, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
        return id;
    }

    public long onUpdateOrIgnore(ContentValues values, String _tableName, String _fieldName, String _fieldValue)
    {
        long id;
        Log.d("onInsertOrUpdate", "insertOrIgnore on " + values);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            id=db.update(_tableName, values, _fieldName + "='" + _fieldValue + "'", null);
        } finally {
            db.close();
        }
        return id;
    }

    public long onInsert(ContentValues values, String _tableName)
    {
        long isSuccess = 0;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            isSuccess = db.insert(_tableName, null, values);
            db.close();
        }catch(Exception e){
            e.printStackTrace();
            isSuccess = 0;
        }
        myDataBase.close();
        return isSuccess;
    }

    public boolean onInsert(String query)
    {
        boolean isSuccess = false;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(query);
            db.close();
            isSuccess = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    // count all records
    public long countRecords(String _tableName){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) from " + _tableName, null);
        cursor.moveToFirst();

        long recCount = cursor.getInt(0);
        cursor.close();
        db.close();

        return recCount;
    }

    // count all records
    public long countRecordsWhere(String _tableName, String _columnName, String _param){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) from " + _tableName + " Where " +_columnName+"='"+_param+"'", null);
        cursor.moveToFirst();

        long recCount = cursor.getInt(0);
        cursor.close();
        db.close();

        return recCount;
    }




    public Cursor selectAllFromTable(String _tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + _tableName + ";", null);
        cursor.moveToFirst();

        cursor.close();
        db.close();

        return cursor;
    }

    public Cursor selectAllFromTable(String _from, String _fieldName, String _fieldValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + _from + " where " + _fieldName + " = '" +_fieldValue + "';";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }



    public List<String> universalSelect(String table_name, String column)
    {
        List<String> revenueNameList=new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " +column+ " from "+table_name+" order by "+column+";", null);
        if(cursor.moveToFirst()) {
          do {

              String revenue_name = cursor.getString(0);
              revenueNameList.add(revenue_name);
          }
               while(cursor.moveToNext());

        }


        cursor.close();
        db.close();

        return revenueNameList;
    }

    public List<String> universalSelectWithLimitOne(String table_name, String column)
    {
        List<String> revenueNameList=new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " +column+ " from "+table_name+ " order by "+column+" LIMIT 1;" , null);
        if(cursor.moveToFirst()) {
            do {

                String revenue_name = cursor.getString(0);
                revenueNameList.add(revenue_name);
            }
            while(cursor.moveToNext());

        }


        cursor.close();
        db.close();

        return revenueNameList;
    }


    public ArrayList<String> universalSelect2(String table_name, String column)
    {
        ArrayList<String> revenueNameList=new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " +column+ " from "+table_name+ " order by "+column+";", null);
        if(cursor.moveToFirst()) {
            do {

                String revenue_name = cursor.getString(0);
                revenueNameList.add(revenue_name);
            }
            while(cursor.moveToNext());

        }


        cursor.close();
        db.close();

        return revenueNameList;
    }



    public Cursor selectAllFromTableWithLimitAndCondition(String _tableName, String _limit, String _whereKey, String _whereValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + _tableName + " WHERE " + _whereKey + "='" + _whereValue +"' LIMIT " + _limit +";", null);
        cursor.moveToFirst();

        //cursor.close();
        db.close();

        return cursor;
    }

    public Cursor selectAllFromTable(String sql, boolean yes)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }
    // TODO: 23-Sep-15 selects columns from a table with limit using an order by without a where clause
;    public String selectColumnFromTableWithLimit(String _columns, String _table, int limit)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " LIMIT " +limit +";";
        String _what = null;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Log.i("Selected field record", cursor.getString(0));
                    _what = cursor.getString(0);
                } while (cursor.moveToNext());

            }
        } finally {
            cursor.close();
        }

        return _what;
    }

    public String selectFromTable(String what, String _from, String _whereColumn, String _whereValue)
    {
        //ArrayList<String>myVals=new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + what + " FROM " + _from + " where " + _whereColumn + " = '" +_whereValue + "';";
        String _what = null;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    //Log.i("Selected field for", cursor.getString(0));
                    _what = cursor.getString(0);
                   // myVals.add(_what);
                } while (cursor.moveToNext());

            }
        } finally {
            cursor.close();
        }

        return _what;
    }





    public List<String> selectFromTableArray(String what, String _from, String _whereColumn, String _whereValue)
    {
        List<String> valuesList=new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + what +" FROM " + _from + " where " + _whereColumn + " = '" +_whereValue + " ORDER BY DIMENSION'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            valuesList.add(cursor.getString(0));
            cursor.moveToNext();

        }


            cursor.close();


        return valuesList;
    }



    // TODO: 23-Sep-15 selects colums from a table using a where clause
    public Cursor selectColumnsFromTable(String _columns, String _table, String _whereColumn, String _whereValue)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " where " + _whereColumn + " = '" + _whereValue + "';";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    // TODO: 23-Sep-15 selects columns from a table using an order by without a where clause
    public Cursor selectColumnsFromTableOrderBy(String _columns, String _table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + ";";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    // TODO: 23-Sep-15 selects columns from a table using an order by without a where clause
    public Cursor selectColumnsFromTableOrderBy(String _columns, String _table, String ORDER_BY)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " "+ ORDER_BY +";";

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }



    public String selectFromTableWithLimitAndOrder(String what, String _from, String _whereColumn, String _whereValue, String _limit, String _order_by)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + what + " FROM " + _from + " where " + _whereColumn + " = '" +_whereValue + "' ORDER BY " + _order_by + " LIMIT " + _limit + ";";
        Log.e("selectFromTableWithLimitAndOrder", sql);
        String _what = null;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Log.i("Selected field for record", cursor.getString(0));
                    _what = cursor.getString(0);
                } while (cursor.moveToNext());

            }
        } finally {
            cursor.close();
        }

        return _what;
    }

    // TODO: 23-Sep-15  selects columns from a table using a where clause and order by
    public Cursor selectColumnsFromTableBy(String _columns, String _table, String _fieldName, String _fieldValue, String Order_by)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + _columns +" FROM " + _table + " where " + _fieldName + " = '" +_fieldValue + "' ORDER BY " + Order_by +";";
        Log.i("Sql query ", sql);
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    // deletes all records
    public boolean deleteRecords(String _tableName){

        boolean isSuccess = false;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from "+ _tableName);
            db.close();
            isSuccess = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    // deletes records with id
    public boolean deleteRecords(String _tableName, String _fieldName, String _fieldValue)
    {
        boolean isSuccess = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + _tableName + " where " + _fieldName + "=" + _fieldValue + ";");
            db.close();
            isSuccess = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

}
