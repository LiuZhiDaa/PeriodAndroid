package ulric.li.xui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class XSquareView extends FrameLayout {
    protected Context mContext = null;
    private float mWidthWeight = 1;
    private float mHeightWeight = 1;

    public XSquareView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int nWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int nWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (nWidthSpecSize * mHeightWeight / mWidthWeight), nWidthSpecMode));
    }

    public void setWeight(float fWidthWeight, float fHeightWeight) {
        if (0 == fWidthWeight || 0 == fHeightWeight)
            return;
        
        mWidthWeight = fWidthWeight;
        mHeightWeight = fHeightWeight;
    }
}