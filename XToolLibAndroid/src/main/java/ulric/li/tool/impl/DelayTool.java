package ulric.li.tool.impl;

import ulric.li.tool.intf.IDelayTool;

public class DelayTool implements IDelayTool {
    private long mTimeBefore = 0;
    private long mTimeSpacing = 0;

    public DelayTool() {
        _init();
    }

    private void _init() {
    }

    @Override
    public void setSpacingTime(long lSpacingTime) {
        mTimeSpacing = lSpacingTime;
    }

    @Override
    public boolean isExceedSpacing(boolean bUpdateBeforeTime) {
        long lTime = System.currentTimeMillis();
        if (lTime - mTimeBefore > mTimeSpacing) {
            if (bUpdateBeforeTime)
                mTimeBefore = lTime;

            return true;
        }

        return false;
    }

    @Override
    public void updateBeforeTime() {
        mTimeBefore = System.currentTimeMillis();
    }
}
