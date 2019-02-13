package com.period.app.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CalendarUtil {

    public static int year = 0;
    public static int month = 0;

    /**
     * 获取特定时间的前一段时间
     * @param date
     * @param filed  Calendar.DAY_OF_WEEK
     * @param amount
     * @return
     */
    public static Date getDate(Date date,int filed,int amount){
        if(date==null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);                           //设置时间为当前时间
        calendar.add(filed,amount);                       //设置当前amount天之前
        Date lastData = calendar.getTime();               //获取amount天前的时间
        return lastData;
    }

    public static String getStringDate(long date){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static long getZeroDate(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //重置当天的时间
        calendar.set(java.util.Calendar.HOUR_OF_DAY,0);
        calendar.set(java.util.Calendar.MINUTE,0);
        calendar.set(java.util.Calendar.SECOND,0);
        calendar.set(java.util.Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    public static long getTimeForMillis(long time, int hour, int min){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
//        int advanceDay = getAdvanceDay(type);
//        if (advanceDay != 0) {
//            calendar.add(Calendar.DAY_OF_MONTH, -advanceDay);
//        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int daysBetween(long smdate, long bdate)
    {
        long between_days=(bdate-smdate)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }
}
