package ulric.li.xout.core.status;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

/**
 * Created by WangYu on 2018/6/11.
 */
public interface IAppStatusMgr extends IXManager,IXObserver<IAppStatusListener> {

    void init();

    /**
     * 宿主app页面是否在前台
     * @return
     */
    boolean isAppForeground();

    /**
     * 应用是否在前台
     * @return
     */
    boolean isApplicationForeground();

    int getActivityCount();

    boolean isProactiveOutPageForeground();


}
