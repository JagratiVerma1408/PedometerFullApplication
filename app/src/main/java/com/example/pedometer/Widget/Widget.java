package com.example.pedometer.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.example.pedometer.R;
import com.example.pedometer.ui.ActivityMain;

public class Widget extends AppWidgetProvider {

    public static RemoteViews updateWidget(final int appWidgetId, final Context context, final int steps) {

    final SharedPreferences pref= context.getSharedPreferences("Widgets",Context.MODE_PRIVATE);
    final PendingIntent pendingIntent = PendingIntent .getActivity(context,appWidgetId,new Intent(context, ActivityMain.class),0);
    final RemoteViews view=new RemoteViews(context.getPackageName(), R.layout.widget);
    view.setOnClickPendingIntent(R.id.widget,pendingIntent);
    view.setTextColor(R.id.widgetsteps,pref.getInt("color_"+appWidgetId, Color.WHITE));
    view.setTextViewText(R.id.widgetsteps,String.valueOf(steps));
    view.setTextColor(R.id.widgettext,pref.getInt("color_"+appWidgetId, Color.WHITE));
    view.setInt(R.id.widget,"setBackgroundColor",pref.getInt("background_"+appWidgetId,Color.TRANSPARENT));
    return view;
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetUpdateService.enqueueUpdate(context);

    }

}
