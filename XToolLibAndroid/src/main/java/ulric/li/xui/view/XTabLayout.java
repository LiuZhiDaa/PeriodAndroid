package ulric.li.xui.view;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class XTabLayout extends TabLayout{
    private Context mContext = null;

    public XTabLayout(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isEnabled() ? super.onInterceptTouchEvent(ev) : true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isEnabled() ? super.onTouchEvent(ev) : false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    public void setSelectedIndicatorColor(int nColor) {
        try {
            Field field = TabLayout.class.getDeclaredField("mTabStrip");
            if (null == field)
                return;

            field.setAccessible(true);
            Class c = field.getType();
            if (null == c)
                return;

            Method method = c.getDeclaredMethod("setSelectedIndicatorColor", int.class);
            if (null == method)
                return;

            method.setAccessible(true);
            Object obj = field.get(this);
            if (null == obj)
                return;

            method.invoke(obj, nColor);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
