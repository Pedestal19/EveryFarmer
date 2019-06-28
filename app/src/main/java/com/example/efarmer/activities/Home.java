package com.example.efarmer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.efarmer.R;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private Button myCollection, onlineNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getIds();

        myCollection.setOnClickListener(this);
        onlineNews.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_myCollection:
                startActivity(new Intent(getApplicationContext(), MyCollectionList.class));
                finish();
                break;
            case R.id.btn_onlineNews:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
    }

    private void getIds(){
        myCollection = (Button)findViewById(R.id.btn_myCollection);
        onlineNews = (Button)findViewById(R.id.btn_onlineNews);
    }
}
