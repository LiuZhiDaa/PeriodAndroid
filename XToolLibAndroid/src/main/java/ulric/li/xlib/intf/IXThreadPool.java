package ulric.li.xlib.intf;

import android.os.Message;

public interface IXThreadPool extends IXManager {
    void addTask(IXThreadPoolListener iXThreadPoolListener);

    void sendMessage(IXThreadPoolListener iXThreadPoolListener, Message msg);

    void shutdown();

    void shutdownNow();
}
