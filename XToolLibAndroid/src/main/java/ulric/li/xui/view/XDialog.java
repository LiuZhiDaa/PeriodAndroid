package ulric.li.xui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import ulric.li.xlib.R;
import ulric.li.xui.utils.UtilsSize;

public class XDialog extends Dialog {
    private Context mContext = null;

    public XDialog(Context context) {
        super(context, R.style.DialogTheme);
        mContext = context;
        _init();
    }

    public XDialog(Context context, int nResThemeID) {
        super(context, nResThemeID);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void show(boolean bIsFillHorizontalScreen, boolean bIsFillVerticalScreen) {
        super.show();
        if (bIsFillHorizontalScreen || bIsFillVerticalScreen) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            if (bIsFillHorizontalScreen) {
                lp.width = UtilsSize.getScreenWidth(mContext);
            }

            if (bIsFillVerticalScreen) {
                lp.height = UtilsSize.getScreenHeight(mContext);
            }

            getWindow().setAttributes(lp);
        }
    }
}
