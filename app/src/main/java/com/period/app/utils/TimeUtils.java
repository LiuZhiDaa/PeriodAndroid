package com.period.app.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static int getMonthOfDay(int year,int month){
        int day = 0;
        if(year%4==0&&year%100!=0||year%400==0){
            day = 29;
        }else{
            day = 28;
        }
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;
        }
        return 0;
    }

    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(long smdate, long bdate)
    {
        long between_days=(bdate-smdate)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }
    /**
     * 计算日期是否在月经期内
     * @param  currtime 当前时间
     * @param  menstrual  最近的月经开始日期
     * @param  cycle 月经周期
     *
     */
    public static  boolean isStayMenstruation(long currtime,long menstrual,int cycle){
        Long l = Long.parseLong(String.valueOf(cycle));
        if (currtime>=menstrual&&currtime<1000*60*60*24*l+menstrual){
            return true;
        }
        return false;
    }

    /**
     * 
     * @return
     */
    public static int dayOfMenstruation(){
        int day =0;
        return day;
    }

    /**
     * 计算日期是否是排卵日
     * * @param currtime 当前时间
     * @param  menstrual  最近的月经开始日期
     * @param  cycle 生理周期
     */
    public static  boolean isOvulationDay(long currtime,long menstrual,int cycle){
        Long l = Long.parseLong(String.valueOf(cycle-14));
        if (currtime==menstrual+1000*60*60*24*l){
            return true;
        }
        return false;
    }
    /**
     * * 计算日期是否是排卵期
     * * @param currtime 当前时间
     * @param  menstrual  最近的月经开始日期
     * @param  cycle 生理周期
     *
     */
    public static  boolean isOvulation(long currtime,long menstrual,int cycle){
        Long l = Long.parseLong(String.valueOf(cycle-19));
        Long l2 = Long.parseLong(String.valueOf(cycle-10));
        if (currtime>=menstrual+1000*60*60*24*l&&currtime<=menstrual+1000*60*60*24*l2){
            return true;
        }
        return false;
    }


    /***
     * 判断当前日期与最近月经日期间隔是否处于一个生理周期
     * @param currtime 当前时间
     * @param  menstrual  最近的月经开始日期
     * @param  cycle 生理周期
     */
    public static  boolean isStaySamePhysiology(long currtime,long menstrual,int cycle){
        long between_days=(currtime-menstrual)/(1000*3600*24);
        if (Integer.parseInt(String.valueOf(between_days))<cycle)
        {
            return true;
        }

        return false;
    }

    /**
     * 当前日期未与最近月经日期处于一个生理周期推测计算得到与当前日期最接近的月经开始日期
     * @param currtime 当前时间
     * @param  menstrual  最近的月经开始日期
     * @param  cycle 生理周期
     */
    public static long  getNewlyTime(long currtime,long menstrual,int cycle){
        long between_days=Integer.parseInt(String.valueOf((currtime-menstrual)/(1000*3600*24)));
        return menstrual+1000*3600*24*(cycle*(between_days/cycle));
    }

    public static long  getNewlyTimes(long currtime,long menstrual,int cycle){
        long between_days=Integer.parseInt(String.valueOf((currtime-menstrual)/(1000*3600*24)));
        return menstrual+1000*3600*24*(cycle*(between_days/cycle));
    }


}
