package ulric.li.xlib.intf;

import android.os.Message;

public interface IXThreadPoolListener {
    void onTaskRun();

    void onTaskComplete();

    void onMessage(Message msg);
}
