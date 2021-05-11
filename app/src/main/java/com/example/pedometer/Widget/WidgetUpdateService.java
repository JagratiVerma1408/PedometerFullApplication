package com.example.pedometer.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.pedometer.Database.Database;
import com.example.pedometer.util.Util;

public class WidgetUpdateService extends JobIntentService {
    private static final int JOB_ID=42;
    public static void enqueueUpdate(Context context){
        enqueueWork(context,WidgetUpdateService.class,JOB_ID,new Intent());
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Database db = Database.getInstance(this);
        int steps=Math.max(db.getCurrentSteps()+db.getSteps(Util.getToday()),0);
        db.close();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int [] appWidgetIds= appWidgetManager.getAppWidgetIds(new ComponentName(this,Widget.class));
        for(int appWidgetId : appWidgetIds){
            appWidgetManager
                    .updateAppWidget( appWidgetId,Widget.updateWidget(appWidgetId, this,steps));
        }

    }
}
