package com.example.efarmer.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.efarmer.R;
import com.example.efarmer.adapters.CollectionsAdapter;
import com.example.efarmer.databases.DataDB;
import com.example.efarmer.models.App;
import com.example.efarmer.models.CollectionPOJO;

import java.util.ArrayList;



public class MyCollectionList extends AppCompatActivity {
    private static final String TAG = "MyCollectionList";
    ProgressDialog mProgressDialog;

    private int start = 0;
    private int limit = 7;
    App app;
    DataDB dataDB = new DataDB();
    TextView textViewNoRecord;
    ListView listView_taxpayers;
    ArrayList<CollectionPOJO> myList = new ArrayList<CollectionPOJO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollection_list);

        app = ((App)getApplicationContext());
        textViewNoRecord = (TextView)findViewById(R.id.textViewNoRecord);


        FloatingActionButton fabAddNews = (FloatingActionButton) findViewById(R.id.fabAddNews);

            fabAddNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intializeValues();
                    finish();
                    startActivity(new Intent(getApplicationContext(), ManageCollection.class));
                }
            });

        new RemoteDataTask().execute();

    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MyCollectionList.this, R.style.AppCompatAlertDialogStyle);
            mProgressDialog.setIcon(R.mipmap.ic_launcher);

            // Set progressdialog title
            mProgressDialog.setTitle(getString(R.string.app_name));
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            PopulateList(start, limit);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            //forPostExecute();
            listView_taxpayers = (ListView) findViewById(R.id.listView_taxpayers);
            //Create a button for load More
            Button btnLoadMore = new Button(getApplicationContext());
            btnLoadMore.setText("Load More");
            //Adding loadmore to building listview at footer
            listView_taxpayers.addFooterView(btnLoadMore);

            if(myList.size() > 0) {
                listView_taxpayers.setAdapter(new CollectionsAdapter(getApplicationContext(), myList));

                //listView action
                listView_taxpayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final CollectionPOJO clickedT = (CollectionPOJO) adapterView.getItemAtPosition(i);
                        app.setId(clickedT.getId());
                        app.setTitle(clickedT.getTitle());
                        app.setDuration(clickedT.getDuration());
                        app.setUrl(clickedT.getUrl());


                                final AlertDialog.Builder adb = new AlertDialog.Builder(
                                        MyCollectionList.this);
                                adb.setTitle("Options");
                                adb.setIcon(R.mipmap.ic_launcher);
                                adb.setMessage("Please confirm an action to perform");
                                adb.setPositiveButton("Edit News", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        app.setCommand("edit");
                                        startActivity(new Intent(getApplicationContext(), ManageCollection.class));
                                    }
                                });
                                adb.setNegativeButton("Delete News", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                         dataDB.myConnection(getApplicationContext()).deleteRecords("collection", "id",clickedT.getId());
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), MyCollectionList.class));
                                    }
                        });


                                adb.show();
//                            }
//                        }


                    }
                });
                textViewNoRecord.setVisibility(View.INVISIBLE);
            }
            else{
                textViewNoRecord.setVisibility(View.VISIBLE);
                textViewNoRecord.setText("Your Collections List is Empty");
            }
            // Close the progressdialog
            mProgressDialog.dismiss();



            // TODO: 09/10/2015 listen to load more event
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // Starting a new async task
                    new LoadMoreDataTask().execute();
                }
            });

            // Create an OnScrollListener
            listView_taxpayers.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view,
                                                 int scrollState) { // TODO Auto-generated method stub
                    int threshold = 1;
                    int count = listView_taxpayers.getCount();

                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (listView_taxpayers.getLastVisiblePosition() >= count
                                - threshold) {
                            // Execute LoadMoreDataTask AsyncTask
                            new LoadMoreDataTask().execute();
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub

                }

            });

        }
    }

    private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MyCollectionList.this);
            // Set progressdialog title
            mProgressDialog.setTitle(getString(R.string.app_name));
            // Set progressdialog message
            mProgressDialog.setMessage("Loading more...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            PopulateList(start += 7, limit);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // Locate listview last item
            int position = listView_taxpayers.getLastVisiblePosition();
            // Pass the results into ListViewAdapter.java
            if(myList.size() > 0) {
                listView_taxpayers.setAdapter(new CollectionsAdapter(getApplicationContext(), myList));
            }
            // Show the latest retrived results on the top
            listView_taxpayers.setSelectionFromTop(position, 0);
            // Close the progressdialog
            mProgressDialog.dismiss();

        }

    }

    void PopulateList(int start, int limit)
    {
        String sql = null;
        if(app.getSqlQuery() != null && !app.getSqlQuery().isEmpty())
        {
            sql = app.getSqlQuery() + " ORDER BY id DESC LIMIT " + start + "," + limit + ";";
        }
        else {
            sql = "SELECT * FROM collection ORDER BY id DESC LIMIT " + start + "," + limit + ";";
        }
        Cursor cursor = dataDB.myConnection(getApplicationContext()).selectAllFromTable(sql, true);
        if(cursor.moveToFirst() && cursor!=null) {
            do {
                CollectionPOJO collectionPOJO = new CollectionPOJO();

                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("Title"));
                String duration = cursor.getString(cursor.getColumnIndex("Duration"));
                String url = cursor.getString(cursor.getColumnIndex("URL"));

//                app.setLandrin(mlandrin);

                collectionPOJO.setId(id);
                collectionPOJO.setTitle(title);
                collectionPOJO.setDuration(duration);
                collectionPOJO.setUrl(url);



                myList.add(collectionPOJO);
            } while (cursor.moveToNext());
        }
    }

    private void intializeValues(){
        app.setTitle("");
        app.setDuration("");
        app.setUrl("");
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        finish();
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

}
