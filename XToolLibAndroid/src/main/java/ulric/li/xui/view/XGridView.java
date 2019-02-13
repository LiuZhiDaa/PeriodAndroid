
package ulric.li.xui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import ulric.li.xui.intf.IXThreeStateCheckBoxType;

public abstract class XGridView<T extends Object> extends GridView {
    protected Context mContext = null;
    protected XGridViewAdapter mXGridViewAdapter = null;
    protected List<XListViewItem<T>> mListItem = null;

    public abstract View adapterGetView(int position, View convertView,
            ViewGroup parent);

    public XGridView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        mListItem = new ArrayList<XListViewItem<T>>();
        mXGridViewAdapter = new XGridViewAdapter();
        setAdapter(mXGridViewAdapter);
    }

    public void setItemList(List<T> listItem) {
        if (null == listItem)
            return;

        mListItem.clear();
        for (T item : listItem) {
            if (null == item)
                continue;

            XListViewItem<T> xListViewItem = new XListViewItem<T>();
            xListViewItem.mT = item;
            xListViewItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_NORMAL;
            mListItem.add(xListViewItem);
        }

        updateData();
        setSelection(0);
    }

    public void updateData() {
        mXGridViewAdapter.notifyDataSetChanged();
    }

    public int getThreeStateCheckBoxType(int nPosition) {
        return mListItem.get(nPosition).mThreeStateCheckBoxType;
    }

    public void setThreeStateCheckBoxType(int nPosition, int nThreeStateCheckBoxType) {
        mListItem.get(nPosition).mThreeStateCheckBoxType = nThreeStateCheckBoxType;
        updateData();
    }

    class XGridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mListItem.size();
        }

        @Override
        public Object getItem(int position) {
            return mListItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return adapterGetView(position, convertView, parent);
        }
    }
}
