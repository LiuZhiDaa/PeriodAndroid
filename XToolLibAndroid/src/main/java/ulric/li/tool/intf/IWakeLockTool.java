package ulric.li.tool.intf;

import ulric.li.xlib.intf.IXObject;

public interface IWakeLockTool extends IXObject {
    boolean setWakeLock(int nWakeLockType, String strWakeLockName);

    void acquire();

    void acquire(long time);

    void release();
}
