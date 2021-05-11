package com.example.pedometer.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pedometer.BottomNavigation.FragmentHome;
import com.example.pedometer.Database.Database;
import com.example.pedometer.R;
import com.example.pedometer.util.Util;

import java.text.NumberFormat;
import java.util.Locale;


public class TotalStepsHome extends Fragment {
    ImageView backarrow,levelimg,three,seven,ten,fourt,twenty,thirty,fourty,sixty;
    private CardView more;
    TextView stepsleft,leveltext,imgtext;
    public static int totalsteps=0,goal=3000;
    private int todayoffset,total_start,since_boot;
    public static NumberFormat formatter=NumberFormat.getInstance(Locale.getDefault());
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_total_steps_home,null);
        backarrow=view.findViewById(R.id.backarrow);
        stepsleft=view.findViewById(R.id.stepslefttonextlevel);
        leveltext=view.findViewById(R.id.levtext);
        imgtext=view.findViewById(R.id.mainacheivemnttext);
        progressBar=view.findViewById(R.id.progressBar);
        levelimg=view.findViewById(R.id.mainacheivemnt);
        three=view.findViewById(R.id.three);
        ten=view.findViewById(R.id.ten);
        seven=view.findViewById(R.id.seven);
        fourt=view.findViewById(R.id.fourteen);
        twenty=view.findViewById(R.id.twenty);
        thirty=view.findViewById(R.id.thirty);
        fourty=view.findViewById(R.id.fourty);
        sixty=view.findViewById(R.id.sixty);
        more=view.findViewById(R.id.more);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAcheivementFragment();
            }
        });
        loadtotalsteps();
        loadimages();
        levelimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogShow();
            }

        });
        return view;

    }
    private void loadAcheivementFragment() {
        Fragment newFragment=new FragmentHome();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment,newFragment)
                .commit();
    }
    private void loadtotalsteps(){
        Database db=Database.getInstance(getActivity());
        todayoffset = db.getSteps(Util.getToday());
        since_boot = db.getCurrentSteps();
        total_start = db.getTotalWithoutToday();
        totalsteps=todayoffset+since_boot+total_start;
    }
    private void loadimages(){
        if(totalsteps<3000){
            levelimg.setBackgroundColor(Color.GRAY);
            levelimg.setImageResource(R.drawable.editthree);
            goal=3000;
            stepsleft.setText(formatter.format(3000-totalsteps));
            leveltext.setText("1");

        }
        if(totalsteps>=3000 && totalsteps<7000){
            levelimg.setBackgroundColor(Color.BLUE);
            three.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editthree);
            goal=7000;
            stepsleft.setText(formatter.format(7000-totalsteps));
            leveltext.setText("2");

        }
        if(totalsteps>=7000 && totalsteps<10000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editseven);
            goal=10000;
            three.setBackgroundColor(Color.BLUE);
            seven.setBackgroundColor(Color.BLUE);
            stepsleft.setText(formatter.format(10000-totalsteps));
            leveltext.setText("3");
            imgtext.setText("Keep Fit");
        }
        if(totalsteps>=10000 && totalsteps<14000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.edit10);
            goal=14000;
            three.setBackgroundColor(Color.BLUE);
            seven.setBackgroundColor(Color.BLUE);
            ten.setBackgroundColor(Color.BLUE);
            stepsleft.setText(formatter.format(14000-totalsteps));
            leveltext.setText("4");
            imgtext.setText("Healthy Day");
        }
        if(totalsteps>=14000 && totalsteps<20000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editfort);
            goal=20000;
            stepsleft.setText(formatter.format(20000-totalsteps));
            leveltext.setText("5");
            three.setBackgroundColor(Color.BLUE);
            seven.setBackgroundColor(Color.BLUE);
            ten.setBackgroundColor(Color.BLUE);
            fourt.setBackgroundColor(Color.BLUE);
            imgtext.setText("Slimmer Day");
        }
        if(totalsteps>=20000 && totalsteps<30000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.edittwenty);
            goal=30000;
            stepsleft.setText(formatter.format(30000-totalsteps));
            leveltext.setText("6");
            three.setBackgroundColor(Color.BLUE);
            seven.setBackgroundColor(Color.BLUE);
            ten.setBackgroundColor(Color.BLUE);
            fourt.setBackgroundColor(Color.BLUE);
            twenty.setBackgroundColor(Color.BLUE);
            imgtext.setText("Hiker");
        }
        if(totalsteps>=30000 && totalsteps<40000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editthirty);
            goal=40000;
            stepsleft.setText(formatter.format(40000-totalsteps));
            leveltext.setText("7");
            three.setBackgroundColor(Color.BLUE);
            seven.setBackgroundColor(Color.BLUE);
            ten.setBackgroundColor(Color.BLUE);
            fourt.setBackgroundColor(Color.BLUE);
            imgtext.setText("Explorer");
        }
        if(totalsteps>=40000 && totalsteps<60000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editforty);
            goal=60000;
            stepsleft.setText(formatter.format(60000-totalsteps));
            leveltext.setText("8");
            three.setBackgroundColor(Color.BLUE);
            seven.setBackgroundColor(Color.BLUE);
            ten.setBackgroundColor(Color.BLUE);
            fourt.setBackgroundColor(Color.BLUE);
            imgtext.setText("Hero");
        }
        if(totalsteps>=60000 && totalsteps<70000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editforty);
            goal=700000;
            stepsleft.setText(formatter.format(70000-totalsteps));
            leveltext.setText("9");
            three.setBackgroundColor(Color.BLUE);
            seven.setBackgroundColor(Color.BLUE);
            ten.setBackgroundColor(Color.BLUE);
            fourt.setBackgroundColor(Color.BLUE);
            imgtext.setText("Conqueror");
        }
        float set=((totalsteps*100)/goal);
        int b=(int)Math.round(set);
        progressBar.setProgress(b);

    }
    protected void DialogShow(){
        final Dialog dialog=new Dialog(getActivity());
        dialog.setCancelable(true);
        View view=getActivity().getLayoutInflater().inflate(R.layout.acheivementdialog,null);
        dialog.setContentView(view);
        loadtotalsteps();
        ImageView levelimg=view.findViewById(R.id.imageview);
        TextView leveltext=view.findViewById(R.id.textlevel);
        if(totalsteps<3000){
            levelimg.setBackgroundColor(Color.GRAY);
            levelimg.setImageResource(R.drawable.editthree);
            leveltext.setText("1");

        }
        if(totalsteps>=3000 && totalsteps<7000){
            levelimg.setBackgroundColor(Color.BLUE);

            levelimg.setImageResource(R.drawable.editthree);

            leveltext.setText("2");

        }
        if(totalsteps>=7000 && totalsteps<10000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editseven);

            leveltext.setText("3");
        }
        if(totalsteps>=10000 && totalsteps<14000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.edit10);

            leveltext.setText("4");
        }
        if(totalsteps>=14000 && totalsteps<20000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editfort);

            leveltext.setText("5");

        }
        if(totalsteps>=20000 && totalsteps<30000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.edittwenty);

            leveltext.setText("6");

        }
        if(totalsteps>=30000 && totalsteps<40000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editthirty);

            leveltext.setText("7");

        }
        if(totalsteps>=40000 && totalsteps<60000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editforty);

            leveltext.setText("8");

        }
        if(totalsteps>=60000 && totalsteps<70000){
            levelimg.setBackgroundColor(Color.BLUE);
            levelimg.setImageResource(R.drawable.editforty);

            leveltext.setText("9");
        }
        Button close=view.findViewById(R.id.ok);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}