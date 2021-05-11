package com.example.pedometer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.example.pedometer.Database.DatabaseHandler;
import com.example.pedometer.R;


public class SplashScreen extends FragmentActivity {
    DatabaseHandler helper=new DatabaseHandler(this);
    String databasecheck="not null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        runNextScreen();
        askForFullScreen();
    }
    private void askForFullScreen()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    private void runNextScreen() {
        String pass=helper.serachifnotnull();
        if (databasecheck.equals(pass)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, ActivityMain.class));
                    finish();
                }
            }, 1500);

        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, GenderSelection.class));
                    finish();
                }
            }, 1500);

        }


    }}

