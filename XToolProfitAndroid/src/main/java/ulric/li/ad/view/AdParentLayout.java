package ulric.li.ad.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by WangYu on 2018/9/11.
 */
public class AdParentLayout extends FrameLayout {
    private boolean mIsDispatch=false;

    public AdParentLayout(@NonNull Context context) {
        this(context,null);
    }

    public AdParentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AdParentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mIsDispatch || super.dispatchTouchEvent(ev);
    }

    public void setInterceptTouchEvent(boolean b) {
        mIsDispatch=b;
    }
}
