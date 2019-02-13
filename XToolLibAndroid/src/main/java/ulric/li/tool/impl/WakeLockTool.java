package ulric.li.tool.impl;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IWakeLockTool;

public class WakeLockTool implements IWakeLockTool {
    private Context mContext = null;
    private WakeLock mWakeLock = null;

    public WakeLockTool() {
        mContext = XLibFactory.getApplication();
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean setWakeLock(int nWakeLockType, String strWakeLockName) {
        if (TextUtils.isEmpty(strWakeLockName))
            return false;

        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(nWakeLockType, strWakeLockName);
        return null != mWakeLock;
    }

    @Override
    public void acquire() {
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    @Override
    public void acquire(long time) {
        if (mWakeLock != null) {
            mWakeLock.acquire(time);
        }
    }

    @Override
    public void release() {
        if (mWakeLock==null||!mWakeLock.isHeld())
            return;

        mWakeLock.release();
    }

}
