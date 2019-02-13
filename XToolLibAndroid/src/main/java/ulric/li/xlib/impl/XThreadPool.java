package ulric.li.xlib.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;

public class XThreadPool implements IXThreadPool {
    private ThreadPoolExecutor mThreadPoolExecutor = null;
    private Handler mHandler = null;

    private static final int VALUE_INT_CORE_POOL_SIZE = 3;
    private static final int VALUE_INT_MAX_POOL_SIZE = 10;
    private static final long VALUE_LONG_KEEP_ALIVE_TIME = 60L;

    private static final int VALUE_INT_MESSAGE_ID = 0x9999;

    public XThreadPool() {
        _init();
    }

    private void _init() {
        int coreNum = Runtime.getRuntime().availableProcessors();
        mThreadPoolExecutor = new ThreadPoolExecutor(coreNum * 2, coreNum * 2,
                VALUE_LONG_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                XThreadPoolObject xThreadPoolObject = (XThreadPoolObject) msg.obj;
                if (VALUE_INT_MESSAGE_ID == msg.what) {
                    IXThreadPoolListener iXThreadPoolListener = xThreadPoolObject.mIXThreadPoolListener;
                    if (null != iXThreadPoolListener)
                        iXThreadPoolListener.onTaskComplete();
                } else {
                    IXThreadPoolListener iXThreadPoolListener = xThreadPoolObject.mIXThreadPoolListener;
                    msg.obj = xThreadPoolObject.mObject;
                    if (null != iXThreadPoolListener)
                        iXThreadPoolListener.onMessage(msg);
                }
            }
        };
    }

    public void addTask(final IXThreadPoolListener iXThreadPoolListener) {
        if (null == iXThreadPoolListener)
            return;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                iXThreadPoolListener.onTaskRun();

                Message msg = new Message();
                msg.what = VALUE_INT_MESSAGE_ID;
                XThreadPoolObject xThreadPoolObject = new XThreadPoolObject();
                xThreadPoolObject.mIXThreadPoolListener = iXThreadPoolListener;
                msg.obj = xThreadPoolObject;
                mHandler.sendMessage(msg);
            }
        };

        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public void sendMessage(IXThreadPoolListener iXThreadPoolListener, Message msg) {
        if (null == msg || VALUE_INT_MESSAGE_ID == msg.what)
            return;

        XThreadPoolObject xThreadPoolObject = new XThreadPoolObject();
        xThreadPoolObject.mIXThreadPoolListener = iXThreadPoolListener;
        xThreadPoolObject.mObject = msg.obj;
        msg.obj = xThreadPoolObject;
        mHandler.sendMessage(msg);
    }

    public void shutdown() {
        mThreadPoolExecutor.shutdown();
    }

    public void shutdownNow() {
        mThreadPoolExecutor.shutdownNow();
    }

    class XThreadPoolObject {
        public IXThreadPoolListener mIXThreadPoolListener = null;
        public Object mObject = null;
    }
}