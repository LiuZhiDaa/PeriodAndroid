package com.period.app.core.clock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Observable;
import android.os.Build;
import android.os.Bundle;

/**
 * @author XuChuanting
 * on 2018/11/22-15:55
 */
public class CompatAlarmManager {

    public static final String DATA = "data_alarm_clock";
    private static final String ACTION_END_SUFFIX = ".action.custom.clock";
    private static CompatAlarmManager sManager;
    private AlarmObservable mAlarmObservable;

    private CompatAlarmManager() {
        mAlarmObservable = new AlarmObservable();
    }

    public static CompatAlarmManager getInstance() {
        if (sManager == null) {
            synchronized (CompatAlarmManager.class) {
                if (sManager == null) {
                    sManager = new CompatAlarmManager();
                }
            }
        }
        return sManager;
    }

    public static String getAlarmAction(Context context) {
        if (context == null) {
            return null;
        }
        return context.getPackageName() + ACTION_END_SUFFIX;
    }

    public void registerAlarmListener(OnAlarmReceivedListener listener) {
        if (listener != null) {
            mAlarmObservable.registerObserver(listener);
        }
    }

    public void registerReceiver(Context context) {
        if (context == null) {
            return;
        }
        AlarmBroadcastReceiver alarmBroadcastReceiver = new AlarmBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getAlarmAction(context));
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        context.registerReceiver(alarmBroadcastReceiver, intentFilter);
    }

    public void notifyAlarm(AlarmClockInfo info) {
        mAlarmObservable.notifyAlarmReceived(info);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void setAlarmClock(Context context, AlarmClockInfo alarmClockInfo) {
        if (context == null || alarmClockInfo == null) {
            return;
        }

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            return;
        }
        PendingIntent operation = createPendingIntent(context,alarmClockInfo.getRequestCode(), alarmClockInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(alarmClockInfo.mType, alarmClockInfo.mTriggerAtMillis, operation);
//            am.setWindow(alarmClockInfo.mType, alarmClockInfo.getTriggerAtMillis(), 100, operation);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(alarmClockInfo.mType, alarmClockInfo.mTriggerAtMillis, operation);
        } else {
            if (alarmClockInfo.mRepeat) {
                am.setRepeating(alarmClockInfo.mType, alarmClockInfo.mTriggerAtMillis, alarmClockInfo.mIntervalMillis, operation);
            } else {
                am.set(alarmClockInfo.mType, alarmClockInfo.mTriggerAtMillis, operation);
            }
        }
    }

    private PendingIntent createPendingIntent(Context context, int requestCode, AlarmClockInfo alarmClockInfo) {
        Intent intent = new Intent();
        String action = getAlarmAction(context);
        intent.setAction(action);
        if (alarmClockInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DATA, alarmClockInfo);
            intent.putExtra(DATA, bundle);
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void cancelAlarmClock(Context context, AlarmClockInfo alarmClockInfo) {
        if (context == null || alarmClockInfo == null) {
            return;
        }

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            return;
        }
        PendingIntent pendingIntent = createPendingIntent(context, alarmClockInfo.getRequestCode(), alarmClockInfo);
        am.cancel(pendingIntent);
    }

    interface OnAlarmReceivedListener {
        void onReceived(AlarmClockInfo info);
    }

    private class AlarmObservable extends Observable<OnAlarmReceivedListener> {
        void notifyAlarmReceived(AlarmClockInfo info) {
            synchronized (mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i++) {
                    mObservers.get(i).onReceived(info);
                }
            }
        }

    }
}
