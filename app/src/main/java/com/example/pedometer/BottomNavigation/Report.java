package com.example.pedometer.BottomNavigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pedometer.Database.Database;
import com.example.pedometer.R;
import com.example.pedometer.ui.Acheivement;
import com.example.pedometer.util.Util;

import java.text.NumberFormat;
import java.util.Locale;


public class Report extends Fragment {
   private LinearLayout acheivement;
    public static NumberFormat formatter=NumberFormat.getInstance(Locale.getDefault());
   private TextView totalsteps,totalCalories,totldistance;
   private String unit;
   private int todayoffset,total_start,since_boot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_report,null);
        acheivement=view.findViewById(R.id.acheivement);
        acheivement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadacheviementfragment();
            }
        });
        totalCalories=view.findViewById(R.id.caloriestotal);
        totalsteps=view.findViewById(R.id.totalstep);
        totldistance=view.findViewById(R.id.distancetotal);
        Database db=Database.getInstance(getActivity());
        todayoffset = db.getSteps(Util.getToday());
        since_boot = db.getCurrentSteps();
        total_start = db.getTotalWithoutToday();
        totalsteps.setText(formatter.format(todayoffset+since_boot+total_start));
        double kcal=(todayoffset+since_boot+total_start)*0.04;
        totalCalories.setText(formatter.format(kcal));
        SharedPreferences prefs=getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        float stepsize=prefs.getFloat("stepsize_value",MyProfile.DEFAULT_STEP_SIZE);
        float distance_total=(todayoffset+since_boot+total_start)*stepsize;
        if(prefs.getString("stepsize_unit",MyProfile.DEFAULT_STEP_UNIT).equals("cm"))
        {
            distance_total/=100000;
            unit="km";

        }else{
            distance_total /= 5280;
            unit="mile";

        }
       totldistance.setText(formatter.format(distance_total)+" "+unit);


        return view;

    }
   private void loadacheviementfragment(){
           Fragment newFragment=new Acheivement();
           getParentFragmentManager().beginTransaction()
                   .replace(R.id.fragment,newFragment)
               .commit();

   }

}