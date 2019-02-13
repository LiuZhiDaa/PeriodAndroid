package ulric.li.logic.alive;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import ulric.li.XLibFactory;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsTime;

public class AliveJobService extends JobService {
    private static int VALUE_INT_JOB_ID = 0x99999;

    @Override
    public boolean onStartJob(JobParameters params) {
        UtilsLog.aliveLog("job", null);
        UtilsLog.sendLog();
        UtilsBroadcast.sendAliveBroadcast(XLibFactory.getApplication());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void jobSchedule(Context context) {
        if (null == context)
            return;
        try {
            if (UtilsEnv.getAndroidSDK() >= 21) {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                JobInfo jobInfo = new JobInfo.Builder(VALUE_INT_JOB_ID, new ComponentName(context, AliveJobService.class))
                        .setPeriodic(UtilsTime.VALUE_LONG_TIME_ONE_MINUTE * 5)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build();

                jobScheduler.schedule(jobInfo);
            }
        } catch (Exception e) {
        }
    }
}
