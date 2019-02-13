package ulric.li.xout.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;

/**
 * Created by WangYu on 2018/8/17.
 */
public class Timer3 implements IXTimer {

    private ScheduledThreadPoolExecutor mScheduled;
    private IXTimerListener mIXTimerListener;
    private Handler mHandler;
    private boolean mIsWork = false;

    private static final int VALUE_INT_MESSAGE_TIMER_ID = 0x9000;
    private static final int VALUE_INT_MESSAGE_TIMER_REPEAT_ID = 0x9001;
    private ScheduledFuture<?> mScheduledFuture;

    public Timer3() {
        mScheduled = new ScheduledThreadPoolExecutor(2);
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
        mScheduledFuture = mScheduled.schedule(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = VALUE_INT_MESSAGE_TIMER_ID;
                mHandler.sendMessage(msg);
            }
        }, lDelayTime, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public boolean startRepeat(long lDelayTime, long lSpacingTime, IXTimerListener iXTimerListener) {
        if (mIsWork || null == iXTimerListener)
            return false;

        mIsWork = true;
        mIXTimerListener = iXTimerListener;
        mScheduledFuture = mScheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = VALUE_INT_MESSAGE_TIMER_REPEAT_ID;
                mHandler.sendMessage(msg);
            }
        }, lDelayTime, lSpacingTime, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void stop() {
        if (mScheduledFuture != null && !mScheduledFuture.isCancelled()) {
            mIsWork=false;
            mScheduledFuture.cancel(true);
        }
    }

    private void clear() {
        mIsWork = false;
        mIXTimerListener = null;
    }
}
