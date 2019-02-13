package ulric.li.xout.core.scene.intf;

public interface IOutSceneMgrListener {
    void onSceneMgrInitAsyncComplete();

    void onSceneTriggered(IOutScene iOutScene);
}
