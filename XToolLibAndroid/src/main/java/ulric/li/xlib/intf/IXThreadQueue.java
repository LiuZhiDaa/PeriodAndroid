package ulric.li.xlib.intf;

public interface IXThreadQueue extends IXObject {
    boolean start();

    void addTask(IXThreadQueueListener iXThreadQueueListener, boolean bFirst);

    void stop();
}
