package ulric.li.xui.view;

import android.arch.lifecycle.Lifecycle;
import android.support.v7.app.AppCompatActivity;

/**
 * 继承XDialog,show 和dissmiss的时候会判断activity生命周期，
 * 使用起来更加安全
 * Created by WangYu on 2019/1/16.
 */
public class XDialog2 extends XDialog {
    private AppCompatActivity mActivity;
    private Lifecycle mActivityLifecycle;

    public XDialog2(AppCompatActivity context) {
        super(context);
        init(context);
    }

    public XDialog2(AppCompatActivity context, int nResThemeID) {
        super(context, nResThemeID);
        init(context);
    }

    private void init(AppCompatActivity context) {
        mActivity=context;
        mActivityLifecycle = mActivity.getLifecycle();
    }

    @Override
    public void show() {
        try {
            //判断至少是大于create状态才show
            if (mActivityLifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                super.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (mActivityLifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
