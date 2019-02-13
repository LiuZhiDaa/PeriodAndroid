package ulric.li.xlib.intf;

public interface IXObserver<T extends Object> {
    void addListener(T listener);

    void removeListener(T listener);

    void removeAllListener();

    void notify(IXNotifyListener<T> notifyListener);
}
