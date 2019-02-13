package ulric.li.xui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ulric.li.xui.intf.IXRecycleViewItem;

public abstract class XRecycleView<T extends IXRecycleViewItem> extends RecyclerView {
    protected Context mContext = null;
    protected XRecycleViewAdapter mXRecycleViewAdapter = null;
    protected List<T> mListItem = null;

    public abstract ViewHolder adapterOnCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void adapterOnBindViewHolder(ViewHolder holder, int position);

    public XRecycleView(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public XRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        _init();
    }

    private void _init() {
        mListItem = new ArrayList<>();
        mXRecycleViewAdapter = new XRecycleViewAdapter();
        setAdapter(mXRecycleViewAdapter);
    }

    public List<T> getItemList() {
        return mListItem;
    }

    public void setItemList(List<T> listItem) {
        if (null == listItem)
            return;

        mListItem = listItem;
        updateData();
    }

    public void updateData() {
        mXRecycleViewAdapter.notifyDataSetChanged();
    }

    public void updateItem(int nPosition) {
        mXRecycleViewAdapter.notifyItemChanged(nPosition);
    }

    class XRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return adapterOnCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            adapterOnBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return mListItem.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mListItem.get(position).getItemViewType();
        }
    }
}
