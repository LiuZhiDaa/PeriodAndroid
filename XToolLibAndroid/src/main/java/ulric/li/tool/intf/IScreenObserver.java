package ulric.li.tool.intf;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IScreenObserver extends IXManager, IXObserver<IScreenObserverListener> {
    boolean startListen();

    void stopListen();
}
