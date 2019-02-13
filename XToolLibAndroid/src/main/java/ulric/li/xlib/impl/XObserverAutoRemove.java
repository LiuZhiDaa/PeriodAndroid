package ulric.li.xlib.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ulric.li.xlib.intf.IXNotifyListener;
import ulric.li.xlib.intf.IXObserver;

public abstract class XObserverAutoRemove<T extends Object> implements IXObserver<T> {
    protected List<WeakReference<T>> mListListener = new ArrayList<WeakReference<T>>();

    // private ReferenceQueue<T> mRQ = new ReferenceQueue<T>();

    public void addListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            if (!isContains(listener)) {
                WeakReference<T> wr = new WeakReference<T>(listener);
                mListListener.add(wr);
            }
        }
    }

    public void removeListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            for (int nIndex = 0; nIndex < mListListener.size(); nIndex++) {
                WeakReference<T> wr = mListListener.get(nIndex);
                T t = wr.get();
                if (null == t)
                    continue;

                if (t.equals(listener)) {
                    mListListener.remove(nIndex);
                    return;
                }
            }
        }
    }

    public void removeAllListener() {
        synchronized (mListListener) {
            mListListener.clear();
        }
    }

    protected List<T> getListenerList() {
        synchronized (mListListener) {
            checkListener();
            List<T> list = new ArrayList<T>();
            for (WeakReference<T> wr : mListListener) {
                T t = wr.get();
                if (null == t)
                    continue;

                list.add(t);
            }

            return list;
        }
    }

    private boolean isContains(T listener) {
        if (null == listener)
            return false;

        synchronized (mListListener) {
            for (WeakReference<T> wr : mListListener) {
                T t = wr.get();
                if (null == t)
                    continue;

                if (t.equals(listener))
                    return true;
            }

            return false;
        }
    }

    private void checkListener() {
        synchronized (mListListener) {
            for (int nIndex = 0; nIndex < mListListener.size(); ) {
                WeakReference<T> wr = mListListener.get(nIndex);
                T t = wr.get();
                if (null == t) {
                    mListListener.remove(nIndex);
                } else {
                    nIndex++;
                }
            }
        }
    }

    @Override
    public void notify(IXNotifyListener<T> notifyListener) {
        if (notifyListener != null) {
            for (T t : getListenerList()) {
                if (t != null) {
                    notifyListener.dispatch(t);
                }
            }
        }
    }
}
