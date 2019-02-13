package ulric.li.tool.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IScreenObserver;
import ulric.li.tool.intf.IScreenObserverListener;
import ulric.li.xlib.impl.XObserverAutoRemove;

public class ScreenObserver extends XObserverAutoRemove<IScreenObserverListener>
        implements IScreenObserver {
    private Context mContext = null;
    private boolean mListened = false;
    private BroadcastReceiver mBR = null;

    public ScreenObserver() {
        mContext = XLibFactory.getApplication();
        _init();
    }

    private void _init() {
        mBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    for (IScreenObserverListener listener : getListenerList())
                        listener.onScreenOn();
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    for (IScreenObserverListener listener : getListenerList())
                        listener.onScreenOff();
                } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    for (IScreenObserverListener listener : getListenerList())
                        listener.onUserPresent();
                }
            }
        };
    }

    @Override
    public boolean startListen() {
        if (mListened)
            return false;

        mListened = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mBR, intentFilter);
        return true;
    }

    @Override
    public void stopListen() {
        if (!mListened)
            return;

        mListened = false;
        mContext.unregisterReceiver(mBR);
    }
}
