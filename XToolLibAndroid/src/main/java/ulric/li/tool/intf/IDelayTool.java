package ulric.li.tool.intf;

import ulric.li.xlib.intf.IXObject;

public interface IDelayTool extends IXObject {
    void setSpacingTime(long lSpacingTime);

    boolean isExceedSpacing(boolean bUpdateBeforeTime);

    void updateBeforeTime();
}
