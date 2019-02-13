package ulric.li.mode.impl;

import ulric.li.mode.intf.IXDecoratorAdditionalTask;
import ulric.li.mode.intf.IXDecoratorComponent;

public abstract class XDecoratorAdditionalTask implements IXDecoratorAdditionalTask {
    private IXDecoratorComponent mIXDecoratorComponent = null;

    XDecoratorAdditionalTask(IXDecoratorComponent iXDecoratorComponent){
        mIXDecoratorComponent = iXDecoratorComponent;
    }

    @Override
    public abstract void doTask();

    @Override
    public void callTask() {
        if (null != mIXDecoratorComponent)
            mIXDecoratorComponent.doTask();
    }
}
