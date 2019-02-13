package ulric.li.xlib.impl;

import java.util.ArrayList;
import java.util.List;

import ulric.li.xlib.intf.IXNotifyListener;
import ulric.li.xlib.intf.IXObserver;

public abstract class XObserver<T extends Object> implements IXObserver<T> {
    protected List<T> mListListener = new ArrayList<T>();

    public void addListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            if (!mListListener.contains(listener))
                mListListener.add(listener);
        }
    }

    public void removeListener(T listener) {
        if (null == listener)
            return;

        synchronized (mListListener) {
            if (mListListener.contains(listener))
                mListListener.remove(listener);
        }
    }

    public void removeAllListener() {
        synchronized (mListListener) {
            mListListener.clear();
        }
    }

    protected List<T> getListListener() {
        synchronized (mListListener) {
            return new ArrayList<T>(mListListener);
        }
    }

    @Override
    public void notify(IXNotifyListener<T> notifyListener) {
        if (notifyListener != null) {
            for (T t : getListListener()) {
                if (t != null) {
                    notifyListener.dispatch(t);
                }
            }
        }
    }
}
