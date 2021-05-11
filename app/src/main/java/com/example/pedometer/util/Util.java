package com.example.pedometer.util;

import java.util.Calendar;

public abstract class Util {
    public static long getToday(){
        Calendar c= Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTimeInMillis();
    }
    public static long getTomorrow(){
        Calendar c= Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,1);
        c.set(Calendar.MILLISECOND,0);
        c.add(Calendar.DATE,1);
        return c.getTimeInMillis();
    }

}
