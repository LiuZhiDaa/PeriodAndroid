package com.period.app.core.clock;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.period.app.constants.RemindConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.reminder.IReminderMgr;
import com.period.app.utils.LogUtils;

import ulric.li.XLibFactory;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;


/**
 * @author XuChuanting
 * on 2018/11/28-20:22
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {


    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String alarmAction = CompatAlarmManager.getAlarmAction(context);
        Log.d("xiao", "action:"+action);
        if (TextUtils.equals(action, alarmAction)) {
            Bundle extras = intent.getBundleExtra(CompatAlarmManager.DATA);
            if (extras != null) {
                AlarmClockInfo alarmClockInfo = extras.getParcelable(CompatAlarmManager.DATA);
                    long triggerAtMillis = alarmClockInfo.getTriggerAtMillis();
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && alarmClockInfo != null && alarmClockInfo.isRepeat()) {
                    //闹钟触发
                    triggerAlarm(triggerAtMillis,alarmClockInfo);
                    long nextTriggerAtMillis = triggerAtMillis + alarmClockInfo.getIntervalMillis();
                    Log.d("nevermore", "nextTriggerAtMillis:"+nextTriggerAtMillis);
                    alarmClockInfo.setTriggerAtMillis(nextTriggerAtMillis);
                    CompatAlarmManager.getInstance().setAlarmClock(context, alarmClockInfo);
                }else{
                    //闹钟触发
                    triggerAlarm(triggerAtMillis,alarmClockInfo);
                }
                CompatAlarmManager.getInstance().notifyAlarm(alarmClockInfo);
            }
        } else if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
            onBootCompleted();
        }
    }

    private void onBootCompleted() {
        IReminderMgr rmgr= (IReminderMgr) XCoreFactory.getInstance().createInstance(IReminderMgr.class);
        rmgr.resetAllRemind();
    }


    private void triggerAlarm(long triggerAtMillis, AlarmClockInfo alarmClockInfo) {
        IReminderMgr rmgr= (IReminderMgr) XCoreFactory.getInstance().createInstance(IReminderMgr.class);
        rmgr.showNotification(alarmClockInfo.getRequestCode());
//        IXThreadPool ixThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
//        ixThreadPool.addTask(new IXThreadPoolListener() {
//            @Override
//            public void onTaskRun() {
//                try {
//                    Thread.sleep(1000*60);
//                } catch (InterruptedException e) {
//                }
//                Log.e("xiao","=============triggerAlarm==========");

                rmgr.setRemindAlarm(alarmClockInfo.getRequestCode(), true);
//            }
//
//            @Override
//            public void onTaskComplete() {
//
//            }
//
//            @Override
//            public void onMessage(Message msg) {
//
//            }
//        });

        log(alarmClockInfo.getRequestCode());
    }

    private void log(int type){
        switch (type){
            case RemindConstant.VAULE_INT_TYPE_MEN_START:
                LogUtils.reminderLog("receive_menstruationStartReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_MEN_END:
                LogUtils.reminderLog("receive_menstruationEndReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_START:
                LogUtils.reminderLog("receive_ovulationStartReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_DAY:
                LogUtils.reminderLog("receive_ovulationDaytReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_END:
                LogUtils.reminderLog("receive_ovulationEndReminder");
                break;

        }
    }


}
