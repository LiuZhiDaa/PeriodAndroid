package ulric.li.xlib.intf;

/**
 * Created by WangYu on 2018/9/27.
 */
public interface IXNotifyListener<T> {

    void dispatch(T t);
}
