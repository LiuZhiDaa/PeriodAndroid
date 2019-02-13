package ulric.li.xui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class XDisableView extends FrameLayout {
    public interface IXDisableViewListener {
        boolean onInterceptTouchEvent(MotionEvent ev);

        boolean onTouchEvent(MotionEvent ev);
    }

    private IXDisableViewListener mListener = null;

    public void setListener(IXDisableViewListener listener) {
        mListener = listener;
    }

    private Context mContext = null;

    public XDisableView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XDisableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null != mListener)
            return mListener.onInterceptTouchEvent(ev);

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != mListener)
            return mListener.onTouchEvent(event);

        return true;
    }
}
