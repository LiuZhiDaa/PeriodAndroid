package ulric.li.xlib.impl;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;

public class XTimer implements IXTimer {
    private Timer mTimer = null;
    private IXTimerListener mIXTimerListener = null;
    private Handler mHandler = null;
    private boolean mIsWork = false;

    private static final int VALUE_INT_MESSAGE_TIMER_ID = 0x9000;
    private static final int VALUE_INT_MESSAGE_TIMER_REPEAT_ID = 0x9001;

    public XTimer() {
        _init();
    }

    private void _init() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (VALUE_INT_MESSAGE_TIMER_ID == msg.what) {
                    if (null != mIXTimerListener)
                        mIXTimerListener.onTimerComplete();

                    clear();
                } else if (VALUE_INT_MESSAGE_TIMER_REPEAT_ID == msg.what) {
                    if (null != mIXTimerListener)
                        mIXTimerListener.onTimerRepeatComplete();
                }
            }
        };
    }

    @Override
    public boolean start(long lDelayTime, IXTimerListener iXTimerListener) {
        if (mIsWork || null == iXTimerListener)
            return false;

        mIsWork = true;
        mIXTimerListener = iXTimerListener;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = VALUE_INT_MESSAGE_TIMER_ID;
                mHandler.sendMessage(msg);
            }
        }, lDelayTime);

        return true;
    }

    @Override
    public boolean startRepeat(long lDelayTime, long lSpacingTime, IXTimerListener iXTimerListener) {
        if (mIsWork || lSpacingTime <= 0 || null == iXTimerListener)
            return false;

        mIsWork = true;
        mIXTimerListener = iXTimerListener;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = VALUE_INT_MESSAGE_TIMER_REPEAT_ID;
                mHandler.sendMessage(msg);
            }
        }, lDelayTime, lSpacingTime);

        return true;
    }

    @Override
    public void stop() {
        if (mIsWork && null != mTimer) {
            mTimer.cancel();
        }

        clear();
    }

    private void clear() {
        mIsWork = false;
        mTimer = null;
        mIXTimerListener = null;
    }
}
