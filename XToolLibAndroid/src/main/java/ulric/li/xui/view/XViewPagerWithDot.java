package ulric.li.xui.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class XViewPagerWithDot extends FrameLayout {
    public interface IXViewPagerLayoutListener {
        void onPageScrollStateChanged(int arg0);

        void onPageScrolled(int arg0, float arg1, int arg2);

        void onPageSelected(int arg0);
    }

    private IXViewPagerLayoutListener mListener = null;

    public void setListener(IXViewPagerLayoutListener listener) {
        mListener = listener;
    }

    private Context mContext = null;
    private XViewPager mXViewPager = null;
    private XViewPagerWithDotPagerAdapter mXViewPagerWithDotPagerAdapter = null;
    private List<View> mListViewPage = null;
    private LinearLayout mLLDot = null;
    private int mDotSize = 0;
    private int mDotSpaceSize = 0;
    private int mDotNormalBackgroundID = 0;
    private int mDotFocusBackgroundID = 0;

    public XViewPagerWithDot(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XViewPagerWithDot(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        mListViewPage = new ArrayList<View>();

        mXViewPager = new XViewPager(mContext);
        addView(mXViewPager, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mXViewPagerWithDotPagerAdapter = new XViewPagerWithDotPagerAdapter();
        mXViewPager.setAdapter(mXViewPagerWithDotPagerAdapter);
        mXViewPager.addOnPageChangeListener(new XViewPagerWithDotOnPageChangeListener());

        mLLDot = new LinearLayout(mContext);
        mLLDot.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lpDot = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpDot.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(mLLDot, lpDot);
    }

    public void setNeedDot(boolean bIsNeedDot) {
        mLLDot.setVisibility(bIsNeedDot ? VISIBLE : GONE);
        postInvalidate();
    }

    public void setDotMarginBottom(int nDotMarginBottom) {
        LayoutParams lpDot = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpDot.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lpDot.bottomMargin = nDotMarginBottom;
        mLLDot.setLayoutParams(lpDot);
        postInvalidate();
    }

    public void setDotSize(int nDotSize, int nDotSpaceSize) {
        mDotSize = nDotSize;
        mDotSpaceSize = nDotSpaceSize;
        postInvalidate();
    }

    public void setDotBackground(int nDotNormalBackgroundID, int nDotFocusBackgroundID) {
        mDotNormalBackgroundID = nDotNormalBackgroundID;
        mDotFocusBackgroundID = nDotFocusBackgroundID;
        postInvalidate();
    }

    public void addPage(int nResPageID) {
        addPage(LayoutInflater.from(mContext).inflate(nResPageID, null));
    }

    public void addPage(View vPage) {
        if (null == vPage)
            return;

        ImageView ivDot = new ImageView(mContext);
        LinearLayout.LayoutParams lpDot = new LinearLayout.LayoutParams(mDotSize, mDotSize);
        if (mLLDot.getChildCount() > 0) {
            lpDot.leftMargin = mDotSpaceSize;
        }

        mLLDot.addView(ivDot, lpDot);
        selectDot(mXViewPager.getCurrentItem());

        mListViewPage.add(vPage);
        updateData();
    }

    public void selectPage(int nIndex) {
        if (nIndex < 0 || nIndex >= mListViewPage.size())
            return;

        mXViewPager.setCurrentItem(nIndex);
        selectDot(nIndex);
    }

    public void updateData() {
        mXViewPagerWithDotPagerAdapter.notifyDataSetChanged();
    }

    private void selectDot(int nIndex) {
        for (int i = 0; i < mLLDot.getChildCount(); i++) {
            mLLDot.getChildAt(i).setBackgroundResource(mDotNormalBackgroundID);
            if (i == nIndex)
                mLLDot.getChildAt(i).setBackgroundResource(mDotFocusBackgroundID);
        }
    }

    class XViewPagerWithDotPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mListViewPage.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViewPage.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViewPage.get(arg1));
            return mListViewPage.get(arg1);
        }
    }

    class XViewPagerWithDotOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (null != mListener)
                mListener.onPageScrollStateChanged(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            selectDot(arg0);
            if (null != mListener)
                mListener.onPageScrolled(arg0, arg1, arg2);
        }

        @Override
        public void onPageSelected(int arg0) {
            if (null != mListener)
                mListener.onPageSelected(arg0);
        }
    }
}
