package com.example.pedometer.BottomNavigation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedometer.Components.SensorListener;
import com.example.pedometer.Database.Database;
import com.example.pedometer.R;
import com.example.pedometer.ui.TotalStepsHome;
import com.example.pedometer.util.API26Wrapper;
import com.example.pedometer.util.Logger;
import com.example.pedometer.util.Util;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.j4velin.lib.colorpicker.BuildConfig;

public class FragmentHome extends Fragment  implements SensorEventListener{

public static NumberFormat formatter=NumberFormat.getInstance(Locale.getDefault());
private ImageView levels,dialog;
private TextView stepsView,totalView,averageView,calories,stepsleft;
private PieModel sliceGoal,sliceCurrent;
 private PieChart pg;
 public static int totalstepsgoal=0;
 private ProgressBar progressBar;
 private int todayoffset, total_start, goal,since_boot, totaldays, goalreach;
    private boolean showSteps = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(Build.VERSION.SDK_INT>=26){
            API26Wrapper.startForegroundService(getActivity(),new Intent(getActivity(), SensorListener.class));
    }
        else{
            getActivity().startService(new Intent(getActivity(), SensorListener.class));
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater,  final  ViewGroup container,
                            final  Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_home,null);
        levels=view.findViewById(R.id.levels);
        stepsView=view.findViewById(R.id.stepsinpiechart);
       totalView=view.findViewById(R.id.total);
       averageView=view.findViewById(R.id.average);
       progressBar=view.findViewById(R.id.progressBar);
       stepsleft=view.findViewById(R.id.stepsleft);
      calories=view.findViewById(R.id.calories);
      dialog=view.findViewById(R.id.splitcounter);
      pg=view.findViewById(R.id.graph);
        levels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadtotalstepshomeFragment();
            }
        });
      dialog.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Dialog_Split.getDialog(getActivity(),total_start+Math.max(todayoffset+since_boot,0)).show();
          }
      });
      setPiechart();
      pg.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              showSteps = !showSteps;
              stepsDistanceChanges();
          }

      });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Database db = Database.getInstance(getActivity());

        if (BuildConfig.DEBUG) db.logState();
        // read todays offset
        todayoffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);

        goal = prefs.getInt("goal", (int) MyProfile.DEFAULT_GOAL);
        since_boot = db.getCurrentSteps();
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        // register a sensorlistener to live update the UI if a step is taken
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.no_sensor)
                    .setMessage(R.string.no_sensor_explain)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface dialogInterface) {
                            getActivity().finish();
                        }
                    }).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        }

        since_boot -= pauseDifference;

        total_start = db.getTotalWithoutToday();
        totaldays = db.getDays();

        db.close();

        stepsDistanceChanges();
    }

    private void stepsDistanceChanges() {
        if(showSteps){
            ((TextView) getView().findViewById(R.id.unit)).setText(getString(R.string.steps));

        }else{
            String unit=getActivity().getSharedPreferences("pedometer",Context.MODE_PRIVATE)
                    .getString("stepsize_unit",MyProfile.DEFAULT_STEP_UNIT);
            if(unit.equals("cm")){
                unit="km";
            }else{
                unit="mile";
            }
            ((TextView) getView().findViewById(R.id.unit)).setText(unit);
        }
         updatePie();
        updateBars();

    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            SensorManager sm=(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(this);


        } catch (Exception e) {
            if(BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }
        Database db= Database.getInstance(getActivity());
        db.saveCurrentSteps(since_boot);
        db.close();
    }

    private void updatePie() {
        if(BuildConfig.DEBUG) Logger.log("UI-updatesteps:"+since_boot);
        int steps_today=Math.max(todayoffset+since_boot,0);
        sliceCurrent.setValue(steps_today);
        if(goal-steps_today>0){
            if(pg.getData().size()==1){
                pg.addPieSlice(sliceGoal);
            }
            sliceGoal.setValue(goal-steps_today);}
            else{
                pg.clearChart();
                pg.addPieSlice(sliceCurrent);
            }
            pg.update();
            if(showSteps){
                stepsView.setText(formatter.format(steps_today));
                double kcal=steps_today*0.04;
                calories.setText(formatter.format(kcal));
                totalView.setText(formatter.format(total_start+steps_today));
                averageView.setText(formatter.format((total_start+steps_today)/totaldays));
                totalstepsgoal=total_start+steps_today;
                if(totalstepsgoal<3000){
                    levels.setBackgroundColor(Color.GRAY);
                    levels.setImageResource(R.drawable.editthree);
                    goalreach=3000;
                }
                if(totalstepsgoal>=3000 && totalstepsgoal<7000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editthree);
                    goalreach=7000;
                }
                if(totalstepsgoal>=7000 && totalstepsgoal<10000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editseven);
                    goalreach=10000;
                }
                if(totalstepsgoal>=10000 && totalstepsgoal<14000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.edit10);
                    goalreach=14000;
                }
                if(totalstepsgoal>=14000 && totalstepsgoal<20000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editfort);
                    goalreach=20000;
                }
                if(totalstepsgoal>=20000 && totalstepsgoal<30000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.edittwenty);
                    goalreach=30000;
                }
                if(totalstepsgoal>=30000 && totalstepsgoal<40000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editthirty);
                    goalreach=40000;
                }
                if(totalstepsgoal>=40000 && totalstepsgoal<60000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editforty);
                    goalreach=60000;
                }
                if(totalstepsgoal>=60000 && totalstepsgoal<70000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editforty);
                    goalreach=700000;
                }
                float set=((totalstepsgoal*100)/goalreach);
                int b=(int)Math.round(set);
                stepsleft.setText(formatter.format(goalreach-totalstepsgoal));
                progressBar.setProgress(b);
            }
            else{
                SharedPreferences prefs=getActivity().getSharedPreferences("pedometer",Context.MODE_PRIVATE);
                float stepsize=prefs.getFloat("stepsize_value",MyProfile.DEFAULT_STEP_SIZE);
                float distance_today=steps_today*stepsize;
                float distance_total=(steps_today+total_start)*stepsize;
                if(prefs.getString("stepsize_unit",MyProfile.DEFAULT_STEP_UNIT).equals("cm"))
                {
                    distance_today/=100000;
                    distance_total/=100000;

                }else{
                    distance_today /= 5280;
                    distance_total /= 5280;

                }
                stepsView.setText(formatter.format(distance_today));
                totalView.setText(formatter.format(distance_total));
                averageView.setText(formatter.format(distance_total / totaldays));
                totalstepsgoal=total_start+steps_today;
                if(totalstepsgoal<3000){
                    levels.setBackgroundColor(Color.GRAY);
                    levels.setImageResource(R.drawable.editthree);
                    goalreach=3000;
                }
                if(totalstepsgoal>=3000 && totalstepsgoal<7000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editthree);
                    goalreach=7000;
                }
                if(totalstepsgoal>=7000 && totalstepsgoal<10000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editseven);
                    goalreach=10000;
                }
                if(totalstepsgoal>=10000 && totalstepsgoal<14000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.edit10);
                    goalreach=14000;
                }
                if(totalstepsgoal>=14000 && totalstepsgoal<20000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editfort);
                    goalreach=20000;
                }
                if(totalstepsgoal>=20000 && totalstepsgoal<30000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.edittwenty);
                    goalreach=30000;
                }
                if(totalstepsgoal>=30000 && totalstepsgoal<40000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editthirty);
                    goalreach=40000;
                }
                if(totalstepsgoal>=40000 && totalstepsgoal<60000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editforty);
                    goalreach=60000;
                }
                if(totalstepsgoal>=60000 && totalstepsgoal<70000){
                    levels.setBackgroundColor(Color.BLUE);
                    levels.setImageResource(R.drawable.editforty);
                    goalreach=700000;
                }
                float set=((totalstepsgoal*100)/goalreach);
                int b=(int)Math.round(set);
                stepsleft.setText(formatter.format(goalreach-totalstepsgoal));
                progressBar.setProgress(b);
            }



    }
    private void updateBars() {

SimpleDateFormat df= new SimpleDateFormat("E",Locale.getDefault());
BarChart barChart =(BarChart) getView().findViewById(R.id.bargraph);
if(barChart.getData().size()>0) barChart.clearChart();
int steps;
float distance ,stepsize=MyProfile.DEFAULT_STEP_SIZE;
boolean stepsize_cm=true;
if(!showSteps){
    SharedPreferences prefs = getActivity().getSharedPreferences("pedometer",Context.MODE_PRIVATE);
    stepsize=prefs.getFloat("stepsize_value",MyProfile.DEFAULT_STEP_SIZE);
    stepsize_cm=prefs.getString("stepsize_unit",MyProfile.DEFAULT_STEP_UNIT).equals("cm");}
    barChart.setShowDecimal(!showSteps);
    BarModel bm;
    Database db=Database.getInstance(getActivity());
    List<Pair<Long,Integer>> last = db.getLastEntries(7);
    db.close();
    for(int i=last.size()-1;i>0;i--) {

        Pair<Long, Integer> current = last.get(i);
        steps = current.second;
        if (steps > 0) {
            bm = new BarModel(df.format(new Date(current.first)), 0, steps > goal ? Color.parseColor("#69F0AE") : Color.parseColor("#40C4FF"));
            if (showSteps) {
                bm.setValue(steps);
            } else {
                distance = steps * stepsize;
                if (stepsize_cm) {
                    distance /= 100000;
                } else {
                    distance /= 5280;
                }

                distance = Math.round(distance * 1000) / 1000f;//3 decimal places
                bm.setValue(distance);
            }

        barChart.addBar(bm);
    }
}
  if(barChart.getData().size()>0){
      barChart.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Dialog_Statistics.getDialog(getActivity(),since_boot).show();
          }
      });
  }

    }

    private void setPiechart() {
        sliceCurrent=new PieModel(0, Color.parseColor("#69F0AE"));
        pg.addPieSlice(sliceCurrent);
        sliceGoal=new PieModel(MyProfile.DEFAULT_GOAL, Color.parseColor("#40C4FF"));
        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
    }

    private void loadtotalstepshomeFragment() {
        Fragment newFragment=new TotalStepsHome();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment,newFragment)
                .commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(BuildConfig.DEBUG) Logger.log(
                "UI- sensorchanged| todatyoffset:"+todayoffset+"sinceboot:"+since_boot+event.values[0]);
        if(event.values[0]>Integer.MAX_VALUE || event.values[0]==0){
            todayoffset=-(int) event.values[0];
            Database db= Database.getInstance(getActivity());
            db.insertNewDay(Util.getToday(),(int)event.values[0]);
            db.close();

        }
        since_boot=(int)event.values[0];
        updatePie();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
     //will not change
    }
}