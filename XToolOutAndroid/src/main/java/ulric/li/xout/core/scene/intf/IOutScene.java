package ulric.li.xout.core.scene.intf;

import org.json.JSONObject;

import ulric.li.xlib.intf.IXObject;

public interface IOutScene extends IXObject {
    String getType();

    boolean isNeedTriggered(JSONObject condition);

    boolean updateTriggered();

    boolean executeAsync(IOutSceneListener iOutSceneListener);
}
