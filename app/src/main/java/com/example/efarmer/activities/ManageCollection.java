package com.example.efarmer.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.efarmer.R;
import com.example.efarmer.databases.DataDB;
import com.example.efarmer.models.App;


public class ManageCollection extends AppCompatActivity {

    private AddNewsTask mRegTask = null;
    private EditNewsTask mEditTask = null;

    private ProgressDialog pDialog;


    EditText et_title, et_duration, et_url, et_id;

    Button add;

    private static final String TAG = "Manage Collection";


    App app;
    private ProgressDialog myDialog;
    DataDB dataDB = new DataDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_collection);
        app = ((App) getApplicationContext());
        getIds();
        checkCommand();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.getCommand() != null) {
                    if(app.getCommand().equalsIgnoreCase("edit")){
                        attemptEditing();
                    }else {
                        attemptAdding();
                    }
                }
                else {
                    attemptAdding();
                }
            }
        });

    }

    private void checkCommand() {

        if (app.getCommand() != null) {
            if(app.getCommand().equalsIgnoreCase("edit")){
                Log.e("debug", app.getId());
                Log.e("debug", app.getTitle());
                Log.e("debug", app.getDuration());
                Log.e("debug", app.getUrl());

                if (app.getTitle()!=null && app.getDuration()!=null && app.getUrl()!=null && app.getId()!=null) {
                    et_id.setText(app.getId());
                    et_title.setText(app.getTitle());
                    et_duration.setText(app.getDuration());
                    et_url.setText(app.getUrl());
                    add.setText("Update Collection");
                } else {
                    Toast.makeText(getApplicationContext(), "Null Params", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void attemptAdding() {

        et_title.setError(null);
        et_duration.setError(null);
        et_url.setError(null);

        boolean cancel = false;
        View focusView = null;


        String title = et_title.getText().toString();
        String duration = et_duration.getText().toString();
        String url = et_url.getText().toString();




        if (TextUtils.isEmpty(title)) {
            et_title.setError(getString(R.string.error_field_required));
            focusView = et_title;
            cancel = true;

        }
        if (TextUtils.isEmpty(duration)) {
            et_duration.setError(getString(R.string.error_field_required));
            focusView = et_duration;
            cancel = true;

        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            focusView.requestFocus();
        } else {
            // TODO: 22/09/2015 check if gps is on


                pDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
                // Showing progress dialog before making http request
                pDialog.setMessage("Adding to your collection...");
                pDialog.setIcon(R.mipmap.ic_launcher);
                pDialog.setTitle(getString(R.string.app_name));

                pDialog.show();


                mRegTask = new AddNewsTask(title, duration, url);
                mRegTask.execute((Void) null);


        }

    }

    private class AddNewsTask extends AsyncTask<Void, Void, Boolean> {
        private final String mtitle, mduration, murl;

        AddNewsTask(String title, String duration, String url) {
            mtitle = title;
            mduration = duration;
            murl = url;

            Log.e("title::: ", title + ". Trying do in Background");
            Log.e("duration::: ", duration + ". Trying do in Background");
            Log.e("url::: ", url + ". Trying do in Background");


        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt register user via a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000); //2000
            } catch (InterruptedException e) {
                Log.e("doInBackground", e.toString() + ". Trying do in Background");
                return false;
            }
            String httpparams;
            try {

                // TODO: 11/10/2016 save user data
                ContentValues contentValues = new ContentValues();


                contentValues.put("title",mtitle);
                contentValues.put("duration", mduration);
                contentValues.put("url", murl);

                long rowInserted = dataDB.myConnection(getApplicationContext()).onInsertOrUpdate(contentValues, "collection");
                    if (rowInserted != -1) {

                        Log.e(TAG, "New row added, row id: " + " to collection table");


                        return true;
                    }

                else {

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;
            hidePDialog();

            if (success) {
                Log.i("buildingRegistrationLog", "Registration Successful");
                finish();

                app.setId(null);
                app.setTitle(null);
                app.setDuration(null);
                app.setUrl(null);

                Intent qRIntent = new Intent(getApplicationContext(), MyCollectionList.class);


                startActivity(qRIntent);

                Toast.makeText(getApplicationContext(), "News Added Successfully", Toast.LENGTH_SHORT).show();
//

            }

            else {
                Log.i("collection reg", "Failed");
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

            }
        }


        @Override
        protected void onCancelled() {
            mRegTask = null;
            hidePDialog();
        }
    }

    private void attemptEditing() {

        et_title.setError(null);
        et_duration.setError(null);
        et_url.setError(null);

        boolean cancel = false;
        View focusView = null;


        String title = et_title.getText().toString();
        String duration = et_duration.getText().toString();
        String url = et_url.getText().toString();
        String id = et_id.getText().toString();




        if (TextUtils.isEmpty(title)) {
            et_title.setError(getString(R.string.error_field_required));
            focusView = et_title;
            cancel = true;

        }
        if (TextUtils.isEmpty(duration)) {
            et_duration.setError(getString(R.string.error_field_required));
            focusView = et_duration;
            cancel = true;

        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            focusView.requestFocus();
        } else {
            // TODO: 22/09/2015 check if gps is on


            pDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            // Showing progress dialog before making http request
            pDialog.setMessage("Updating to your collection...");
            pDialog.setIcon(R.mipmap.ic_launcher);
            pDialog.setTitle(getString(R.string.app_name));

            pDialog.show();


            mEditTask = new EditNewsTask(Long.parseLong(id),title, duration, url);
            mEditTask.execute((Void) null);


        }

    }

    private class EditNewsTask extends AsyncTask<Void, Void, Boolean> {
        private final String mtitle, mduration, murl;
        private final long mid;

        EditNewsTask(long id, String title, String duration, String url) {
            mtitle = title;
            mduration = duration;
            murl = url;
            mid = id;

            Log.e("title::: ", title + ". Trying do in Background");
            Log.e("duration::: ", duration + ". Trying do in Background");
            Log.e("url::: ", url + ". Trying do in Background");


        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt register user via a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000); //2000
            } catch (InterruptedException e) {
                Log.e("doInBackground", e.toString() + ". Trying do in Background");
                return false;
            }
            String httpparams;
            try {

                // TODO: 11/10/2016 save user data
                ContentValues contentValues = new ContentValues();


                contentValues.put("title",mtitle);
                contentValues.put("duration", mduration);
                contentValues.put("url", murl);

                long rowInserted = dataDB.myConnection(getApplicationContext()).onUpdateOrIgnore(contentValues, "collection","id", Long.toString(mid));
                if (rowInserted != -1) {

                    Log.e(TAG, "New row updated, row id: " + " to collection table");


                    return true;
                }

                else {

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;
            hidePDialog();

            if (success) {
                Log.i("buildingRegistrationLog", "Registration Successful");
                finish();

                app.setId(null);
                app.setTitle(null);
                app.setDuration(null);
                app.setUrl(null);
                app.setCommand(null);

                Intent qRIntent = new Intent(getApplicationContext(), MyCollectionList.class);


                startActivity(qRIntent);

                Toast.makeText(getApplicationContext(), "News Updated Successfully", Toast.LENGTH_SHORT).show();
//

            }

            else {
                Log.i("collection reg", "Failed");
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

            }
        }


        @Override
        protected void onCancelled() {
            mRegTask = null;
            hidePDialog();
        }
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    private void hidemyDialog() {
        if (myDialog != null) {
            myDialog.dismiss();
            myDialog = null;
        }
    }




    private void getIds(){

        et_title = (EditText) findViewById(R.id.et_title);
        et_duration = (EditText) findViewById(R.id.et_duration);

        et_url = (EditText) findViewById(R.id.et_url);
        et_id = (EditText)findViewById(R.id.et_id);
        add = (Button) findViewById(R.id.btn_add);

    }










}
