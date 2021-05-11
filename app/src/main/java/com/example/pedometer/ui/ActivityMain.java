package com.example.pedometer.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.pedometer.BottomNavigation.FragmentHome;
import com.example.pedometer.BottomNavigation.MyProfile;
import com.example.pedometer.BottomNavigation.Report;
import com.example.pedometer.R;

public class ActivityMain extends FragmentActivity  {
    AHBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(final  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        initiateviews();
        setupbottomnavigation();
        defaultfeatures();
        loadhomefragment();
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position==0){
                    loadhomefragment();
                }
                if (position==1){
                    loadreportfragment();
                }
                if (position==2){
                    loadmprofilefragment();
                }

                return true;
            }
        });

    }
    private void initiateviews(){
        bottomNavigation=findViewById(R.id.bottom_navigation);

    }
    private void setupbottomnavigation(){
        AHBottomNavigationItem itemone=new AHBottomNavigationItem(R.string.tab_1,R.drawable.ic_baseline_home_24,R.color.item_selected);
        AHBottomNavigationItem itemtwo=new AHBottomNavigationItem(R.string.tab_2,R.drawable.ic_baseline_signal_cellular_alt_24,R.color.item_selected);
        AHBottomNavigationItem itemthree=new AHBottomNavigationItem(R.string.tab_3,R.drawable.ic_baseline_account_circle_24,R.color.item_selected);
        bottomNavigation.addItem(itemone);
        bottomNavigation.addItem(itemtwo);
        bottomNavigation.addItem(itemthree);

    }
    private void defaultfeatures(){
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setAccentColor(Color.parseColor("#2979FF"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }
    private void loadhomefragment(){
        Fragment newFragment=new FragmentHome();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment,newFragment)
                .commit();
    }
    private void loadreportfragment(){
        Fragment newFragment=new Report();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment,newFragment)
                .commit();
    }
    private void loadmprofilefragment(){
        Fragment newFragment=new MyProfile();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment,newFragment)
                .commit();
    }
}
