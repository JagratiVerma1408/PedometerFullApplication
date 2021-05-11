package com.example.pedometer.BottomNavigation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedometer.Components.SensorListener;
import com.example.pedometer.Database.Database;
import com.example.pedometer.Database.DatabaseHandler;
import com.example.pedometer.R;
import com.example.pedometer.util.API23Wrapper;
import com.example.pedometer.util.API26Wrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MyProfile extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final float DEFAULT_STEP_SIZE = Locale.getDefault() == Locale.US ? 2.5f : 75f;;
    public static final String DEFAULT_STEP_UNIT =Locale.getDefault() == Locale.US ? "ft" : "cm" ;
    public static final float DEFAULT_GOAL = 10000;
    private LinearLayout setgoal,stepsize,shownotification,export,importl;
    private TextView goal;
    public static int Position;
    private Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view= inflater.inflate(R.layout.fragment_my_profile,null);
        setgoal=view.findViewById(R.id.setgoallayout);
        stepsize=view.findViewById(R.id.stepsizelayout);
       shownotification=view.findViewById(R.id.shownotilayout);
       importl=view.findViewById(R.id.importlayout);
        export=view.findViewById(R.id.exportlayout);
        goal=view.findViewById(R.id.goaltext);
       spinner=view.findViewById(R.id.spinnergender);
       setgoal.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               loadsetgoal();
           }
       });
        stepsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences prefs= getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
                 v= getActivity().getLayoutInflater().inflate(R.layout.stepsize, null);
                final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                final RadioGroup unit =v.findViewById(R.id.unit);
                final EditText value=v.findViewById(R.id.value);
                unit.check(prefs.getString("stepsize_unit",DEFAULT_STEP_UNIT).equals("cm") ? R.id.cm : R.id.ft);
                value.setText(String.valueOf(prefs.getFloat("stepsize_value",DEFAULT_STEP_SIZE)));
                builder.setView(v);
                builder.setTitle(R.string.set_step_size);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      try{
                          prefs.edit().putFloat("stepsize_value",
                                  Float.valueOf(value.getText().toString()))
                                  .putString("stepsize_unit",unit.getCheckedRadioButtonId()==R.id.cm ? "cm" : "ft")
                                  .apply();
                          value.setText(getString(R.string.step_size_summary,
                                  Float.valueOf(value.getText().toString()),
                                  unit.getCheckedRadioButtonId()==R.id.cm ? "cm" : "ft"));
                      } catch (NumberFormatException e) {
                          e.printStackTrace();
                      }
                      dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                Dialog dialog = builder.create();
                dialog.show();

            }
        });
         shownotification.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 loadNotification();
             }
         });
         importl.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(hasexternalpermissio()){
                     importCsv();
                 }
                 else if(Build.VERSION.SDK_INT>=23){
                     API23Wrapper.requestPermission(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                 }
                 else{
                     Toast.makeText(getActivity(), ""+R.string.permission_external_storage, Toast.LENGTH_SHORT).show();
                 }
             }
         });
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasexternalpermissio()){
                    exportCsv();
                }
                else if(Build.VERSION.SDK_INT>=23){
                    API23Wrapper.requestPermission(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
                else{
                    Toast.makeText(getActivity(), ""+R.string.permission_external_storage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        loadSpinnerData();
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(Position);
        return view;
    }

    private void loadSpinnerData() {
        DatabaseHandler db =DatabaseHandler.getInstance(getActivity());
        List<String> labels=db.getAllLables();
        ArrayAdapter<String> dataadapter = new ArrayAdapter<>(this.getActivity(),R.layout.support_simple_spinner_dropdown_item,labels);
        spinner.setAdapter(dataadapter);
    }

    private void exportCsv() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final File f = new File(Environment.getExternalStorageDirectory(), "Pedometer.csv");
            if (f.exists()) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.file_already_exists)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                writetoFile(f);
                            }

                        }) .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).create().show();
                return;
            } else{
                writetoFile(f);
            }
        }
        else{
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.error_external_storage_not_available)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).create().show();

        }

    }

    private void writetoFile(final File f) {
        BufferedWriter out;
        try{
            f.createNewFile();
            out=new BufferedWriter(new FileWriter(f));
        } catch (IOException e) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.error_file, e.getMessage()))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).create().show();
            e.printStackTrace();
            return;
        }
        Database db=Database.getInstance(getActivity());
        Cursor c= db.query(new String[]{"date","steps"},"date>0",null,null,null,"date",null);
        try{
            if(c!=null&& c.moveToFirst()){
                while(!c.isAfterLast()){
                    out.append(c.getString(0)).append(";")
                            .append(String.valueOf(Math.max(0,c.getInt(1)))).append("\n");
                    c.moveToNext();
                }
            }
           out.flush();
            out.close();
        } catch (Exception e) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.error_file, e.getMessage()))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).create().show();
            e.printStackTrace();
            return;

        }finally{
         if(c!=null) c.close();
            db.close();


        }
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.data_saved, f.getAbsolutePath()))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).create().show();
    }

    private void importCsv() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File f = new File(Environment.getExternalStorageDirectory(), "Pedometer.csv");
            if (!f.exists() || !f.canRead()) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.file_cant_read, f.getAbsolutePath()))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        }).create().show();
                return;
            }
            Database db = Database.getInstance(getActivity());
            String line;
            String[] data;
            int ignore = 0, inserted = 0, overwritten = 0;
            BufferedReader in;
            try {
                in = new BufferedReader(new FileReader(f));
                while ((line = in.readLine()) != null) {
                    data = line.split(";");
                    try {
                        if (db.insertDayFromBackup(Long.valueOf(data[0]),
                                Integer.valueOf(data[1]))) {
                            inserted++;
                        } else {
                            overwritten++;
                        }
                    } catch (Exception e) {
                        ignore++;
                    }}
                    in.close();
                }
             catch (IOException e) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.error_file,e.getMessage()))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        }).create().show();
                e.printStackTrace();
                return;

            } finally {
                db.close();
            }
            String message = getString(R.string.entries_imported, inserted + overwritten);
            if (overwritten > 0)
                message += "\n\n" + getString(R.string.entries_overwritten, overwritten);

            if (ignore > 0)
                message += "\n\n" + getString(R.string.entries_ignored, ignore);
            new AlertDialog.Builder(getActivity())
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).create().show();

        }
        else{
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.error_external_storage_not_available)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).create().show();

        }
    }
    private boolean hasexternalpermissio() {
             return getActivity().getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,getActivity().getPackageName())==PackageManager.PERMISSION_GRANTED;
    }

    private void loadNotification() {
        NotificationManager manager=(NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if((Boolean) check()){
            manager.notify(SensorListener.NOTIFICATION_ID,SensorListener.getNotification(getActivity()));

        }else {
            manager.cancel(SensorListener.NOTIFICATION_ID);
        }

    }

    private Boolean check() {
        API26Wrapper.launchNotificationSettings(getActivity());
        return true;
    }

    private void loadsetgoal() {
        final SharedPreferences prefs= getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        final NumberPicker np = new NumberPicker(getActivity());
        np.setMinValue(1);
        np.setMaxValue(100000);
        np.setValue(prefs.getInt("goal",10000));
        builder.setView(np);
        builder.setTitle(R.string.set_goal);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                np.clearFocus();
                prefs.edit().putInt("goal",np.getValue()).commit();
                goal.setText(getString(R.string.goal_summary,np.getValue()));
                dialog.dismiss();
                getActivity().startService(new Intent(getActivity(), SensorListener.class).putExtra("updateNotificationState",true));
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  Position=position;
                  spinner.setSelection(Position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}