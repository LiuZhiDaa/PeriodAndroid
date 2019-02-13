package com.period.app.core.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.period.app.R;
import com.period.app.bean.ReminderInfoBean;
import com.period.app.constants.RemindConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.clock.AlarmClockInfo;
import com.period.app.core.clock.CompatAlarmManager;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.main.MainActivity;
import com.period.app.utils.CalendarUtil;

import java.util.Calendar;


/**
 * Created by WangGuiLi
 * on 2018/12/19
 */
public class ReminderMgr implements IReminderMgr {
    private static final CharSequence CHANNEL_NAME = "period";
    private Context mContext;
    private String[] mTypes;
    private SharedPreferences sp;
    private String[] mReminderCustomTexts;

    public ReminderMgr() {
        mContext = XCoreFactory.getApplication();
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mReminderCustomTexts = mContext.getResources().getStringArray(R.array.reminder_custom_texts);
        mTypes = new String[]{RemindConstant.MEN_START, RemindConstant.MEN_END, RemindConstant.OVULATION_START, RemindConstant.OVULATION_DAY, RemindConstant.OVULATION_END};
    }

    private ReminderInfoBean queryRemindInfo(int type) {
        String json = sp.getString(mTypes[type], null);
        ReminderInfoBean bean = null;
        if (json == null) {
            bean = initDefaultRemindInfo(type);
        } else {
            bean = JSON.parseObject(json, ReminderInfoBean.class);
        }
        return bean;
    }


    private ReminderInfoBean initDefaultRemindInfo(int type) {
        ReminderInfoBean reminderInfoBean = new ReminderInfoBean();
        reminderInfoBean.setAdvancedDay(0);
        reminderInfoBean.setReminderTime("10:30");
        reminderInfoBean.setReminderCustomText(mReminderCustomTexts[type]);
        return reminderInfoBean;
    }

    @Override
    public boolean isSwitchOn(int type) {
        return queryRemindInfo(type).isReminde();
    }


    @Override
    public void setSwitch(int type, boolean isSwitchOn) {
        ReminderInfoBean bean = queryRemindInfo(type);
        bean.setReminde(isSwitchOn);
        sp.edit().putString(mTypes[type], JSON.toJSONString(bean)).apply();
    }

    @Override
    public String getReminderCustomText(int type) {
        return queryRemindInfo(type).getReminderCustomText();
    }

    @Override
    public void setReminderText(int type, String reminderText) {
        ReminderInfoBean bean = queryRemindInfo(type);
        bean.setReminderCustomText(reminderText);
        sp.edit().putString(mTypes[type], JSON.toJSONString(bean)).apply();

    }

    @Override
    public String getReminderTime(int type) {
        return queryRemindInfo(type).getReminderTime();
    }

    @Override
    public void setReminderTime(int type, String reminderTime) {
        ReminderInfoBean bean = queryRemindInfo(type);
        bean.setReminderTime(reminderTime);
        sp.edit().putString(mTypes[type], JSON.toJSONString(bean)).apply();
    }

    @Override
    public int getAdvanceDay(int type) {
        return queryRemindInfo(type).getAdvancedDay();
    }

    @Override
    public void setAdvanceDay(int type, int advanceDay) {
        ReminderInfoBean bean = queryRemindInfo(type);
        bean.setAdvancedDay(advanceDay);
        sp.edit().putString(mTypes[type], JSON.toJSONString(bean)).apply();

    }

    @Override
    public void setRemindAlarm(int type, boolean next) {
        IPredictionManger mPredictionMgr = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);
        IDataMgr iDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        long nextTriggerTime = 0;
        String reminderTime = getReminderTime(type);
        String[] times = reminderTime.split(":");
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);
        long currTime = 0;
        if(next) {
            nextTriggerTime = CalendarUtil.getZeroDate(System.currentTimeMillis()) + ((long)(iDataMgr.getPhyCycle()))*1000*60*60*24;
        }else{
            currTime = System.currentTimeMillis();
            switch (type) {
                case RemindConstant.VAULE_INT_TYPE_MEN_START:
                    nextTriggerTime = mPredictionMgr.getNextMStartTime(currTime, hour, minute);
                    break;
                case RemindConstant.VAULE_INT_TYPE_MEN_END:
                    nextTriggerTime = mPredictionMgr.getNextMEndTime(currTime, hour, minute);
                    break;
                case RemindConstant.VAULE_INT_TYPE_OVULATION_START:
                    nextTriggerTime = mPredictionMgr.getNextOvulationStartTime(currTime, hour, minute);
                    break;
                case RemindConstant.VAULE_INT_TYPE_OVULATION_DAY:
                    nextTriggerTime = mPredictionMgr.getNextOvulationDayTime(currTime, hour, minute);
                    break;
                case RemindConstant.VAULE_INT_TYPE_OVULATION_END:
                    nextTriggerTime = mPredictionMgr.getNextOvulationEndTime(currTime, hour, minute);
                    break;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nextTriggerTime);
        int advanceDay = getAdvanceDay(type);
        if (advanceDay != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, -advanceDay);
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        nextTriggerTime = calendar.getTimeInMillis();
        AlarmClockInfo alarmInfo = new AlarmClockInfo.Builder()
                .setTriggerAtMillis(nextTriggerTime)
                .setRequestCode(type)
                .build();
        if (isSwitchOn(type)) {
            CompatAlarmManager.getInstance().setAlarmClock(mContext, alarmInfo);
        } else {
            CompatAlarmManager.getInstance().cancelAlarmClock(mContext, alarmInfo);
        }
    }

    @Override
    public void resetAllRemind() {
        for (int i = 0; i < 5; i++) {
            setRemindAlarm(i, false);
        }
    }

    @Override
    public void showNotification(int type) {
        if (isSwitchOn(type)) {
            Context context = XCoreFactory.getApplication();
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
            String channelId = context.getPackageName() + "_1";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("notification", type);
            Log.e("UtilsLog","=====notification===="+type);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    NotificationChannel channel = new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
                    channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, Notification.AUDIO_ATTRIBUTES_DEFAULT);
                    channel.enableLights(true); //是否在桌面icon右上角展示小红点
                    channel.setLightColor(Color.BLUE); //小红点颜色
                    channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                    notificationManager.createNotificationChannel(channel);
                }
            }
            Notification notification = builder
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(getReminderCustomText(type))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();
            managerCompat.notify(type, notification);
        }
    }


}
