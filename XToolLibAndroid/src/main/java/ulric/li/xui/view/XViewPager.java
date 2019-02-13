package ulric.li.xui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class XViewPager extends ViewPager {
    private Context mContext = null;
    private boolean mIsCanScroll = true;

    public XViewPager(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isEnabled() ? super.onTouchEvent(ev) : false;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isEnabled())
            super.scrollTo(x, y);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
}
