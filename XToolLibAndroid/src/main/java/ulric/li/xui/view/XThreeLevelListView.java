package ulric.li.xui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import ulric.li.xlib.R;
import ulric.li.xui.intf.IXThreeStateCheckBoxType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class XThreeLevelListView<T1 extends Object, T2 extends Object, T3 extends Object> extends ExpandableListView {
    protected Context mContext = null;
    protected XExpandableListViewAdapter mXExpandableListViewAdapter = null;
    protected List<XThreeLevelListViewItem<T1>> mListGroupItem = null;
    protected Map<XThreeLevelListViewItem<T1>, List<XThreeLevelListViewItem<T2>>> mMapChildItem = null;
    protected Map<XThreeLevelListViewItem<T2>, List<XThreeLevelListViewItem<T3>>> mMapSubChildItem = null;

    private static final int VALUE_INT_VIEW_TYPE_GROUP = 0x1000;
    private static final int VALUE_INT_VIEW_TYPE_CHILD = 0x1001;

    public abstract View adapterGetGroupView(
            int groupPosition, View convertView, ViewGroup parent);

    public abstract View adapterGetChildView(
            int groupPosition, int childPosition, boolean isNeedExpandedOrCollapse, View convertView, ViewGroup parent);

    public abstract View adapterGetSubChildView(
            int groupPosition, int childPosition, int subChildPosition, boolean isLastChild, View convertView, ViewGroup parent);

    public XThreeLevelListView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XThreeLevelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        mListGroupItem = new ArrayList<XThreeLevelListViewItem<T1>>();
        mMapChildItem = new HashMap<XThreeLevelListViewItem<T1>, List<XThreeLevelListViewItem<T2>>>();
        mMapSubChildItem = new HashMap<XThreeLevelListViewItem<T2>, List<XThreeLevelListViewItem<T3>>>();
        mXExpandableListViewAdapter = new XExpandableListViewAdapter();
        setAdapter(mXExpandableListViewAdapter);
    }

    public void setItem(List<T1> listGroupItem, Map<T1, List<T2>> mapChildItem, Map<T2, List<T3>> mapSubChildItem) {
        if (null == listGroupItem)
            return;

        mListGroupItem.clear();
        mMapChildItem.clear();
        mMapSubChildItem.clear();

        for (T1 groupItem : listGroupItem) {
            if (null == groupItem)
                continue;

            XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem = new XThreeLevelListViewItem<T1>();
            xThreeLevelListViewGroupItem.mT = groupItem;
            xThreeLevelListViewGroupItem.mIsExpand = false;
            xThreeLevelListViewGroupItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED;
            mListGroupItem.add(xThreeLevelListViewGroupItem);

            List<T2> listChildItem = mapChildItem.get(groupItem);
            List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = new ArrayList<XThreeLevelListViewItem<T2>>();
            if (null != listChildItem) {
                for (T2 childItem : listChildItem) {
                    if (null == childItem)
                        continue;

                    XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem = new XThreeLevelListViewItem<T2>();
                    xThreeLevelListViewChildItem.mT = childItem;
                    xThreeLevelListViewChildItem.mIsExpand = false;
                    xThreeLevelListViewChildItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED;
                    listXThreeLevelListViewChildItem.add(xThreeLevelListViewChildItem);

                    List<T3> listSubChildItem = mapSubChildItem.get(childItem);
                    List<XThreeLevelListViewItem<T3>> listXThreeLevelListViewSubChildItem = new ArrayList<XThreeLevelListViewItem<T3>>();
                    if (null != listSubChildItem) {
                        for (T3 subChildItem : listSubChildItem) {
                            if (null == subChildItem)
                                continue;

                            XThreeLevelListViewItem<T3> xThreeLevelListViewSubChildItem = new XThreeLevelListViewItem<T3>();
                            xThreeLevelListViewSubChildItem.mT = subChildItem;
                            xThreeLevelListViewSubChildItem.mIsExpand = false;
                            xThreeLevelListViewSubChildItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_CHECKED;
                            listXThreeLevelListViewSubChildItem.add(xThreeLevelListViewSubChildItem);
                        }
                    }

                    mMapSubChildItem.put(xThreeLevelListViewChildItem, listXThreeLevelListViewSubChildItem);
                }
            }

            mMapChildItem.put(xThreeLevelListViewGroupItem, listXThreeLevelListViewChildItem);
        }

        updateData();
        setSelection(0);
    }

    public void updateData() {
        mXExpandableListViewAdapter.notifyDataSetChanged();
    }

    public boolean isGroupExpandEx(int nGroupPosition) {
        return mListGroupItem.get(nGroupPosition).mIsExpand;
    }

    public void expandGroupEx(int nGroupPosition) {
        mListGroupItem.get(nGroupPosition).mIsExpand = true;
        updateData();
    }

    public void collapseGroupEx(int nGroupPosition) {
        mListGroupItem.get(nGroupPosition).mIsExpand = false;
        updateData();
    }

    public int getGroupThreeStateCheckBoxType(int nGroupPosition) {
        return mListGroupItem.get(nGroupPosition).mThreeStateCheckBoxType;
    }

    public void setGroupThreeStateCheckBoxType(int nGroupPosition, int nThreeStateCheckBoxType) {
        if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE == nThreeStateCheckBoxType)
            return;

        XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem = mListGroupItem.get(nGroupPosition);
        if (null == xThreeLevelListViewGroupItem)
            return;

        if (xThreeLevelListViewGroupItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
            return;

        xThreeLevelListViewGroupItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
        List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
        if (null != listXThreeLevelListViewChildItem) {
            for (XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem : listXThreeLevelListViewChildItem) {
                if (null == xThreeLevelListViewChildItem)
                    continue;

                if (xThreeLevelListViewChildItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                    continue;

                xThreeLevelListViewChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
                List<XThreeLevelListViewItem<T3>> listXThreeLevelListViewSubChildItem = mMapSubChildItem.get(xThreeLevelListViewChildItem);
                if (null == listXThreeLevelListViewSubChildItem)
                    continue;

                for (XThreeLevelListViewItem<T3> xThreeLevelListViewSubChildItem : listXThreeLevelListViewSubChildItem) {
                    if (null == xThreeLevelListViewSubChildItem)
                        continue;

                    if (xThreeLevelListViewSubChildItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                        continue;

                    xThreeLevelListViewSubChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
                }
            }
        }

        updateData();
    }

    public boolean isChildExpandEx(int nGroupPosition, int nChildPosition) {
        return mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition).mIsExpand;
    }

    public void expandChildEx(int nGroupPosition, int nChildPosition) {
        mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition).mIsExpand = true;
        expandGroup(getChildRealGroupPosition(nGroupPosition, nChildPosition));
    }

    public void collapseChildEx(int nGroupPosition, int nChildPosition) {
        mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition).mIsExpand = false;
        collapseGroup(getChildRealGroupPosition(nGroupPosition, nChildPosition));
    }

    public int getChildThreeStateCheckBoxType(int nGroupPosition, int nChildPosition) {
        return mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition).mThreeStateCheckBoxType;
    }

    public void setChildThreeStateCheckBoxType(int nGroupPosition, int nChildPosition, int nThreeStateCheckBoxType) {
        if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE == nThreeStateCheckBoxType)
            return;

        XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem = mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition);
        if (null == xThreeLevelListViewChildItem)
            return;

        if (xThreeLevelListViewChildItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
            return;

        xThreeLevelListViewChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
        List<XThreeLevelListViewItem<T3>> listXThreeLevelListViewSubChildItem = mMapSubChildItem.get(xThreeLevelListViewChildItem);
        if (null != listXThreeLevelListViewSubChildItem) {
            for (XThreeLevelListViewItem<T3> xThreeLevelListViewSubChildItem : listXThreeLevelListViewSubChildItem) {
                if (null == xThreeLevelListViewSubChildItem)
                    continue;

                if (xThreeLevelListViewSubChildItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                    continue;

                xThreeLevelListViewSubChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
            }
        }

        XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem = mListGroupItem.get(nGroupPosition);
        if (null == xThreeLevelListViewGroupItem)
            return;

        List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
        if (null != listXThreeLevelListViewChildItem) {
            int nCheckTypeCount = 0;
            for (XThreeLevelListViewItem<T2> item : listXThreeLevelListViewChildItem) {
                if (null == item)
                    continue;

                if (item.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                    nCheckTypeCount++;
            }

            if (nCheckTypeCount == listXThreeLevelListViewChildItem.size())
                xThreeLevelListViewGroupItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
            else
                xThreeLevelListViewGroupItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE;
        }

        updateData();
    }

    public int getSubChildThreeStateCheckBoxType(int nGroupPosition, int nChildPosition, int nSubChildPosition) {
        return mMapSubChildItem.get(mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition)).get(nSubChildPosition).mThreeStateCheckBoxType;
    }

    public void setSubChildThreeStateCheckBoxType(int nGroupPosition, int nChildPosition, int nSubChildPosition, int nThreeStateCheckBoxType) {
        if (IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE == nThreeStateCheckBoxType)
            return;

        XThreeLevelListViewItem<T3> xThreeLevelListViewSubChildItem = mMapSubChildItem.get(mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition)).get(nSubChildPosition);
        if (null == xThreeLevelListViewSubChildItem)
            return;

        if (xThreeLevelListViewSubChildItem.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
            return;

        xThreeLevelListViewSubChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;

        XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem = mMapChildItem.get(mListGroupItem.get(nGroupPosition)).get(nChildPosition);
        if (null == xThreeLevelListViewChildItem)
            return;

        List<XThreeLevelListViewItem<T3>> listXThreeLevelListViewSubChildItem = mMapSubChildItem.get(xThreeLevelListViewChildItem);
        if (null != listXThreeLevelListViewSubChildItem) {
            int nCheckTypeCount = 0;
            for (XThreeLevelListViewItem<T3> item : listXThreeLevelListViewSubChildItem) {
                if (null == item)
                    continue;

                if (item.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                    nCheckTypeCount++;
            }

            if (nCheckTypeCount == listXThreeLevelListViewSubChildItem.size())
                xThreeLevelListViewChildItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
            else
                xThreeLevelListViewChildItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE;
        }

        XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem = mListGroupItem.get(nGroupPosition);
        if (null == xThreeLevelListViewGroupItem)
            return;

        List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
        if (null != listXThreeLevelListViewChildItem) {
            int nCheckTypeCount = 0;
            for (XThreeLevelListViewItem<T2> item : listXThreeLevelListViewChildItem) {
                if (null == item)
                    continue;

                if (item.mThreeStateCheckBoxType == nThreeStateCheckBoxType)
                    nCheckTypeCount++;
            }

            if (nCheckTypeCount == listXThreeLevelListViewChildItem.size())
                xThreeLevelListViewGroupItem.mThreeStateCheckBoxType = nThreeStateCheckBoxType;
            else
                xThreeLevelListViewGroupItem.mThreeStateCheckBoxType = IXThreeStateCheckBoxType.VALUE_INT_THREE_STATE_CHECK_BOX_TYPE_INCOMPLETE;
        }

        updateData();
    }

    private int getChildRealGroupPosition(int nGroupPosition, int nChildPosition) {
        int nGroupIndex = 0;
        int nGroupPositionIndex = 0;
        for (XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem : mListGroupItem) {
            if (null == xThreeLevelListViewGroupItem)
                continue;

            nGroupIndex++;
            if (!xThreeLevelListViewGroupItem.mIsExpand) {
                nGroupPositionIndex++;
                continue;
            }

            List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
            if (null != listXThreeLevelListViewChildItem) {
                int nChildPositionIndex = 0;
                for (XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem : listXThreeLevelListViewChildItem) {
                    if (null == xThreeLevelListViewChildItem)
                        continue;

                    if (nGroupPositionIndex == nGroupPosition && nChildPositionIndex == nChildPosition)
                        return nGroupIndex;

                    nGroupIndex++;
                    nChildPositionIndex++;
                }
            }

            nGroupPositionIndex++;
        }

        return 0;
    }

    class XExpandableListViewAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            int nGroupCount = 0;
            for (XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem : mListGroupItem) {
                if (null == xThreeLevelListViewGroupItem)
                    continue;

                nGroupCount++;
                if (!xThreeLevelListViewGroupItem.mIsExpand)
                    continue;

                List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
                if (null != listXThreeLevelListViewChildItem)
                    nGroupCount += listXThreeLevelListViewChildItem.size();
            }

            return nGroupCount;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int nGroupIndex = 0;
            for (XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem : mListGroupItem) {
                if (null == xThreeLevelListViewGroupItem)
                    continue;

                nGroupIndex++;
                if (!xThreeLevelListViewGroupItem.mIsExpand)
                    continue;

                List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
                if (null != listXThreeLevelListViewChildItem) {
                    for (XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem : listXThreeLevelListViewChildItem) {
                        if (null == xThreeLevelListViewChildItem)
                            continue;

                        if (nGroupIndex == groupPosition) {
                            return mMapSubChildItem.get(xThreeLevelListViewChildItem).size();
                        }

                        nGroupIndex++;
                    }
                }
            }

            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            int nGroupIndex = 0;
            for (XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem : mListGroupItem) {
                if (null == xThreeLevelListViewGroupItem)
                    continue;

                if (nGroupIndex == groupPosition)
                    return xThreeLevelListViewGroupItem;

                nGroupIndex++;
                if (!xThreeLevelListViewGroupItem.mIsExpand)
                    continue;

                List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
                if (null != listXThreeLevelListViewChildItem) {
                    for (XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem : listXThreeLevelListViewChildItem) {
                        if (null == xThreeLevelListViewChildItem)
                            continue;

                        if (nGroupIndex == groupPosition)
                            return xThreeLevelListViewChildItem;

                        nGroupIndex++;
                    }
                }
            }

            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            int nGroupIndex = 0;
            for (XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem : mListGroupItem) {
                if (null == xThreeLevelListViewGroupItem)
                    continue;

                nGroupIndex++;
                if (!xThreeLevelListViewGroupItem.mIsExpand)
                    continue;

                List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
                if (null != listXThreeLevelListViewChildItem) {
                    for (XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem : listXThreeLevelListViewChildItem) {
                        if (null == xThreeLevelListViewChildItem)
                            continue;

                        if (nGroupIndex == groupPosition)
                            return mMapSubChildItem.get(xThreeLevelListViewChildItem).get(childPosition);

                        nGroupIndex++;
                    }
                }
            }

            return null;
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
            int nGroupIndex = 0;
            int nGroupPosition = 0;
            for (XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem : mListGroupItem) {
                if (null == xThreeLevelListViewGroupItem)
                    continue;

                if (nGroupIndex == groupPosition) {
                    View view = null;
                    if (null == convertView) {
                        view = adapterGetGroupView(nGroupPosition, convertView, parent);
                    } else {
                        view = adapterGetGroupView(nGroupPosition,
                                VALUE_INT_VIEW_TYPE_GROUP == (int)convertView.getTag(R.id.id_key_three_level_list_view_group_type) ? convertView : null, parent);
                    }

                    if (null != view)
                        view.setTag(R.id.id_key_three_level_list_view_group_type, VALUE_INT_VIEW_TYPE_GROUP);

                    return view;
                }

                nGroupIndex++;
                if (!xThreeLevelListViewGroupItem.mIsExpand) {
                    nGroupPosition++;
                    continue;
                }

                List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
                if (null != listXThreeLevelListViewChildItem) {
                    int nChildPosition = 0;
                    for (XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem : listXThreeLevelListViewChildItem) {
                        if (null == xThreeLevelListViewChildItem)
                            continue;

                        if (nGroupIndex == groupPosition) {
                            View view = null;
                            if (null == convertView) {
                                view = adapterGetChildView(nGroupPosition, nChildPosition,
                                        isExpanded != xThreeLevelListViewChildItem.mIsExpand, convertView, parent);
                            } else {
                                view = adapterGetChildView(nGroupPosition, nChildPosition,
                                        isExpanded != xThreeLevelListViewChildItem.mIsExpand,
                                        VALUE_INT_VIEW_TYPE_CHILD == (int)convertView.getTag(R.id.id_key_three_level_list_view_group_type) ? convertView : null, parent);
                            }

                            if (null != view)
                                view.setTag(R.id.id_key_three_level_list_view_group_type, VALUE_INT_VIEW_TYPE_CHILD);

                            return view;
                        }

                        nGroupIndex++;
                        nChildPosition++;
                    }
                }

                nGroupPosition++;
            }

            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            int nGroupIndex = 0;
            int nGroupPosition = 0;
            for (XThreeLevelListViewItem<T1> xThreeLevelListViewGroupItem : mListGroupItem) {
                if (null == xThreeLevelListViewGroupItem)
                    continue;

                nGroupIndex++;
                if (!xThreeLevelListViewGroupItem.mIsExpand) {
                    nGroupPosition++;
                    continue;
                }

                List<XThreeLevelListViewItem<T2>> listXThreeLevelListViewChildItem = mMapChildItem.get(xThreeLevelListViewGroupItem);
                if (null != listXThreeLevelListViewChildItem) {
                    int nChildPosition = 0;
                    for (XThreeLevelListViewItem<T2> xThreeLevelListViewChildItem : listXThreeLevelListViewChildItem) {
                        if (null == xThreeLevelListViewChildItem)
                            continue;

                        if (nGroupIndex == groupPosition)
                            return adapterGetSubChildView(nGroupPosition, nChildPosition, childPosition, isLastChild, convertView, parent);

                        nGroupIndex++;
                        nChildPosition++;
                    }
                }

                nGroupPosition++;
            }

            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
