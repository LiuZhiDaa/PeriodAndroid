package ulric.li.logic.alive;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by WangYu on 2018/9/3.
 */
public class UtilsBroadcast {
    private static final String BROADCAST_ACTION = "com.start.out.service";

    public static void sendAliveBroadcast(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }
}
