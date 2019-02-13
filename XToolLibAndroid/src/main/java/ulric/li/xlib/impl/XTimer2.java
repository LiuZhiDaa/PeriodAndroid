package ulric.li.xlib.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;

public class XTimer2 implements IXTimer {
    private Handler mHandler = null;
    private Runnable mRunnable = null;
    private IXTimerListener mIXTimerListener = null;
    private boolean mIsWork = false;
    private long mSpacingTime = 0;

    public XTimer2() {
        _init();
    }

    private void _init() {
        mHandler = new Handler(Looper.getMainLooper());
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (0 == mSpacingTime) {
                    if (null != mIXTimerListener)
                        mIXTimerListener.onTimerComplete();

                    clear();
                } else {
                    if (null != mIXTimerListener)
                        mIXTimerListener.onTimerRepeatComplete();

                    mHandler.postDelayed(this, mSpacingTime);
                }
            }
        };
    }

    @Override
    public boolean start(long lDelayTime, IXTimerListener iXTimerListener) {
        if (mIsWork || null == iXTimerListener)
            return false;

        mIsWork = true;
        mSpacingTime = 0;
        mIXTimerListener = iXTimerListener;
        mHandler.postDelayed(mRunnable, lDelayTime);
        return true;
    }

    @Override
    public boolean startRepeat(long lDelayTime, long lSpacingTime, IXTimerListener iXTimerListener) {
        if (mIsWork || lSpacingTime <= 0 || null == iXTimerListener)
            return false;

        mIsWork = true;
        mSpacingTime = lSpacingTime;
        mIXTimerListener = iXTimerListener;
        mHandler.postDelayed(mRunnable, lDelayTime);
        return true;
    }

    @Override
    public void stop() {
        if (mIsWork) {
            mHandler.removeCallbacks(mRunnable);
        }

        clear();
    }

    private void clear() {
        mIsWork = false;
        mIXTimerListener = null;
    }
}
