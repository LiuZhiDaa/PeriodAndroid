package ulric.li.xui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import ulric.li.xui.intf.IXThreeStateCheckBoxType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class XExpandableListView<T1 extends Object, T2 extends Object> extends ExpandableListView {
    protected Context mContext = null;
    protected XExpandableListViewAdapter mXExpandableListViewAdapter = null;
    protected List<XExpandableListViewItem<T1>> mListGroupItem = null;
    protected Map<XExpandableListViewItem<T1>, List<XExpandableListViewItem<T2>>> mMapChildItem = null;

    public abstract View adapterGetGroupView(
            int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);

    public abstract View adapterGetChildView(
            int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

    public XExpandableListView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        mListGroupItem = new ArrayList<XExpandableListViewItem<T1>>();
        mMapChildItem = new HashMap<XExpandableListViewItem<T1>, List<XExpandableListViewItem<T2>>>();
        mXExpandableListViewAdapter = new XExpandableListViewAdapter();
        setAdapter(mXExpandableListViewAdapter);
    }

    public void setItem(List<T1> listGroupItem, Map<T1, List<T2>> mapChildItem) {
        if (null == listGroupItem)
            return;

        mListGroupItem.clear();
        mMapChildItem.clear();

        for (T1 groupItem : listGroupItem) {
            if (null == groupItem)
                continue;

            XExpandableListViewItem<T1> xExpandableListViewGroupItem = new XExpandableListViewItem<T1>();
            xExpandableListViewGroupItem.mT = groupItem;
            xExpandableListViewGroupItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED;
            mListGroupItem.add(xExpandableListViewGroupItem);

            List<T2> listChildItem = mapChildItem.get(groupItem);
            List<XExpandableListViewItem<T2>> listXExpandableListViewChildItem = new ArrayList<XExpandableListViewItem<T2>>();
            if (null != listChildItem) {
                for (T2 childItem : listChildItem) {
                    if (null == childItem)
                        continue;

                    XExpandableListViewItem<T2> xExpandableListViewChildItem = new XExpandableListViewItem<T2>();
                    xExpandableListViewChildItem.mT = childItem;
                    xExpandableListViewChildItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED;
                    listXExpandableListViewChildItem.add(xExpandableListViewChildItem);
                }
            }

            mMapChildItem.put(xExpandableListViewGroupItem, listXExpandableListViewChildItem);
        }

        updateData();
        setSelection(0);
    }

    public void updateData() {
        mXExpandableListViewAdapter.notifyDataSetChanged();
    }

    public int getGroupThreeStateCheckBoxType(int nGroupPosition) {
        return mListGroupItem.get(nGroupPosition).mThreeStateCheckBoxType;
    }

    public void setGroupThreeStateCheckBoxType(int nGroupPosition, int nThreeStateCheckBoxType) {
        if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE == nThreeStateCheckBoxType)
            return;

        XExpandableListViewItem<T1> xExpandableListViewGroupItem = mListGroupItem.get(nGroupPosition);
        if (null == xExpandableListViewGroupItem)
            return;

        if (xExpandableListViewGroupItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
            return;

        xExpandableListViewGroupItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
        List<XExpandableListViewItem<T2>> listXExpandableListViewChildItem = mMapChildItem.get(xExpandableListViewGroupItem);
        if (null != listXExpandableListViewChildItem) {
            for (XExpandableListViewItem<T2> xExpandableListViewChildItem : listXExpandableListViewChildItem) {
                if (null == xExpandableListViewChildItem)
                    continue;

                if (xExpandableListViewChildItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                    continue;

                xExpandableListViewChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
            }
        }

        updateData();
    }

    public int getChildThreeStateCheckBoxType(int nGroupPosition, int nChildPosition) {
        return mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition).mThreeStateCheckBoxType;
    }

    public void setChildThreeStateCheckBoxType(int nGroupPosition, int nChildPosition, int nThreeStateCheckBoxType) {
        if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE == nThreeStateCheckBoxType)
            return;

        XExpandableListViewItem<T2> xExpandableListViewChildItem = mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition);
        if (null == xExpandableListViewChildItem)
            return;

        if (xExpandableListViewChildItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
            return;

        xExpandableListViewChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;

        XExpandableListViewItem<T1> xExpandableListViewGroupItem = mListGroupItem.get(nGroupPosition);
        if (null == xExpandableListViewGroupItem)
            return;

        List<XExpandableListViewItem<T2>> listXExpandableListViewChildItem = mMapChildItem.get(xExpandableListViewGroupItem);
        if (null != xExpandableListViewChildItem) {
            int nCheckTypeCount = 0;
            for (XExpandableListViewItem<T2> item : listXExpandableListViewChildItem) {
                if (null == item)
                    continue;

                if (item.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                    nCheckTypeCount++;
            }

            if (nCheckTypeCount == listXExpandableListViewChildItem.size())
                xExpandableListViewGroupItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
            else
                xExpandableListViewGroupItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE;
        }

        updateData();
    }

    class XExpandableListViewAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return mListGroupItem.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mMapChildItem.get(mListGroupItem.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mListGroupItem.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mMapChildItem.get(mListGroupItem.get(groupPosition));
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * 1000 + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            return adapterGetGroupView(groupPosition, isExpanded, convertView, parent);
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return adapterGetChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
