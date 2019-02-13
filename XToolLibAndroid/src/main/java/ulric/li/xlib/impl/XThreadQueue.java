package ulric.li.xlib.impl;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;

import ulric.li.XLibFactory;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;
import ulric.li.xlib.intf.IXThreadQueue;
import ulric.li.xlib.intf.IXThreadQueueListener;

public class XThreadQueue implements IXThreadQueue {
    private IXThreadPool mIXThreadPool = null;
    private boolean mIsWork = false;
    private boolean mIsNeedStop = false;
    private List<IXThreadQueueListener> mListListener = null;

    private static final int VALUE_INT_MESSAGE_ID = 0x9000;

    public XThreadQueue() {
        _init();
    }

    private void _init() {
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mListListener = new ArrayList<IXThreadQueueListener>();
    }

    @Override
    public boolean start() {
        if (mIsWork)
            return false;

        mIsWork = true;
        mIsNeedStop = false;
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                while (true) {
                    IXThreadQueueListener iXThreadQueueListener = null;
                    synchronized (mListListener) {
                        if (mIsNeedStop)
                            break;

                        if (mListListener.isEmpty()) {
                            try {
                                mListListener.wait();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (mIsNeedStop)
                            break;

                        iXThreadQueueListener = mListListener.get(0);
                        mListListener.remove(0);
                    }

                    if (null != iXThreadQueueListener)
                        iXThreadQueueListener.onTaskRun();

                    Message msg = new Message();
                    msg.what = VALUE_INT_MESSAGE_ID;
                    msg.obj = iXThreadQueueListener;
                    mIXThreadPool.sendMessage(this, msg);
                }
            }

            @Override
            public void onTaskComplete() {
                clear();
            }

            @Override
            public void onMessage(Message msg) {
                if (VALUE_INT_MESSAGE_ID == msg.what) {
                    IXThreadQueueListener iXThreadQueueListener = (IXThreadQueueListener) msg.obj;
                    if (null != iXThreadQueueListener)
                        iXThreadQueueListener.onTaskComplete();
                }
            }
        });

        return true;
    }

    @Override
    public void addTask(IXThreadQueueListener iXThreadQueueListener, boolean bFirst) {
        if (null == iXThreadQueueListener)
            return;

        synchronized (mListListener) {
            if (bFirst)
                mListListener.add(0, iXThreadQueueListener);
            else
                mListListener.add(iXThreadQueueListener);

            mListListener.notify();
        }
    }

    @Override
    public void stop() {
        synchronized (mListListener) {
            mIsNeedStop = true;
            mListListener.notify();
        }
    }

    private void clear() {
        mIsWork = false;
        mIsNeedStop = false;
    }
}
