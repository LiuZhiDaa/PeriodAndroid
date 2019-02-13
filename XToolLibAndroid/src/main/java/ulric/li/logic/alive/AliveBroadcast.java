package ulric.li.logic.alive;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsTime;

public class AliveBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UtilsLog.aliveLog("broadcast", null);
        UtilsLog.sendLog();

        updateAliveBroadcast(context);
        UtilsBroadcast.sendAliveBroadcast(context);
    }

    public static void registerAliveBroadcast(Context context) {
        if (null == context)
            return;

        Intent intentAlive = new Intent(context, AliveBroadcast.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intentAlive, PendingIntent.FLAG_NO_CREATE);
        if (null != pi)
            return;

        updateAliveBroadcast(context);
    }

    private static void updateAliveBroadcast(Context context) {
        if (null == context)
            return;

        Intent intentAlive = new Intent(context, AliveBroadcast.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intentAlive, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + UtilsTime.VALUE_LONG_TIME_ONE_MINUTE * 30, pi);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
