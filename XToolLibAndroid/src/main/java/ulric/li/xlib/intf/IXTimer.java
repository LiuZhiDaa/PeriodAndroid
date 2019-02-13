package ulric.li.xlib.intf;

public interface IXTimer extends IXObject {
    boolean start(long lDelayTime, IXTimerListener iXTimerListener);

    boolean startRepeat(long lDelayTime, long lSpacingTime, IXTimerListener iXTimerListener);

    void stop();
}
