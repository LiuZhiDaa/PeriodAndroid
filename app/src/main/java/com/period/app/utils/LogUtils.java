package com.period.app.utils;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ulric.li.utils.UtilsLog;

public class LogUtils {

    public static void mainLog(String str){
        JSONObject object = new JSONObject();
        try {
            object.put("belong",str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog("main","click_confirm",object);
    }

    public static void welcomeLogSkip(String str){
        welcomeLog("click_skip",str);
    }
    public static void welcomeLogLast(String str){
        welcomeLog("click_last",str);
    }
    public static void welcomeLogNext(String str){
        welcomeLog("click_next",str);
    }

    public static void welcomeLog(String val, String str){
        JSONObject object = new JSONObject();
        try {
            object.put("belong",str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog("collection", val, object);
    }

    public static void calendarLog(String str){
        UtilsLog.statisticsLog("calendar", str, null);
    }

    public static void mainConfirmLog(String str){
        JSONObject object = new JSONObject();
        try {
            object.put("belong", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog("main", "click_confirm", object);
        Log.e("LogUtils",str+"........................................main");
    }

    public static void calendarConfirmLog(String str){
        JSONObject object = new JSONObject();
        try {
            object.put("belong", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog("calendar", "click_confirm", object);
        Log.e("LogUtils",str+"........................................calendar");
    }
    public static void calendarLog(boolean isStart, boolean state,String str){
        JSONObject object = new JSONObject();
        try {
            if(isStart){
                if (state){
                    object.put("belong1", "menstrualStartOff");
                }else{
                    object.put("belong1", "menstrualStartON");
                }
            }else{
                if (state){
                    object.put("belong1", "menstrualEndOff");
                }else{
                    object.put("belong1", "menstrualEndON");
                }
            }
            object.put("belong2", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog("calendar", "click_confirm", object);

    }

    public static void mainLog(boolean isStart, boolean state,String str){
        JSONObject object = new JSONObject();
        try {
            if(isStart){
                if (state){
                    object.put("belong1", "menstrualStartOff");
                }else{
                    object.put("belong1", "menstrualStartON");
                }
            }else{
                if (state){
                    object.put("belong1", "menstrualEndOff");
                }else{
                    object.put("belong1", "menstrualEndON");
                }
            }
            object.put("belong2", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog("main", "click_confirm", object);

    }

    public static void moreLog(String str){
        UtilsLog.statisticsLog("more", str, null);
    }
    public static void settingLog(String str){
        UtilsLog.statisticsLog("reminderSetting", str, null);
    }
    public static void settingLogText(String str){
        JSONObject object = new JSONObject();
        try {
            object.put("belong",str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog("reminderSetting", "click_reminderText", object);
    }

    public static void reminderLog(String str){
        UtilsLog.statisticsLog("reminder", str, null);
    }


    public static void TwoFieldLog(String key,String value){
        UtilsLog.statisticsLog(key, value, null);
    }

    public static void ThreeFieldLog(String key1,String key2,String value){
        JSONObject object = new JSONObject();
        try {
            object.put("belong", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UtilsLog.statisticsLog(key1, key2, object);

    }


}
