package cn.iwgang.familiarrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Wrap FamiliarRecyclerView Adapter
 * Created by iWgang on 15/10/31.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarWrapRecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    public static final int LAYOUT_MANAGER_TYPE_LINEAR = 0;
    public static final int LAYOUT_MANAGER_TYPE_GRID = 1;
    public static final int LAYOUT_MANAGER_TYPE_STAGGERED_GRID = 2;

    private List<View> mHeaderView;
    private List<View> mFooterView;
    private RecyclerView.Adapter mReqAdapter;
    private int curHeaderOrFooterPos;
    private int mLayoutManagerType = LAYOUT_MANAGER_TYPE_LINEAR;
    private FamiliarRecyclerView.OnItemClickListener mOnItemClickListener;
    private FamiliarRecyclerView.OnItemLongClickListener mOnItemLongClickListener;
    private FamiliarRecyclerView mFamiliarRecyclerView;

    public FamiliarWrapRecyclerViewAdapter(FamiliarRecyclerView familiarRecyclerView, RecyclerView.Adapter reqAdapter, List<View> mHeaderView, List<View> mFooterView, int layoutManagerType) {
        this.mFamiliarRecyclerView = familiarRecyclerView;
        this.mReqAdapter = reqAdapter;
        this.mHeaderView = mHeaderView;
        this.mFooterView = mFooterView;
        this.mLayoutManagerType = layoutManagerType;
    }

    public int getHeadersCount() {
        return null != mHeaderView ? mHeaderView.size() : 0;
    }

    public int getFootersCount() {
        return null != mFooterView ? mFooterView.size() : 0;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += mReqAdapter.getItemCount();

        if (null != mHeaderView && mHeaderView.size() > 0) {
            count += mHeaderView.size();
        }

        if (null != mFooterView && mFooterView.size() > 0) {
            count += mFooterView.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int headersCount = getHeadersCount();

        // header view
        if (position < headersCount) {
            this.curHeaderOrFooterPos = position;
            return VIEW_TYPE_HEADER;
        }

        // item view
        int adapterCount = 0;
        if (mReqAdapter != null && position >= headersCount) {
            int adjPosition = position - headersCount;
            adapterCount = mReqAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mReqAdapter.getItemViewType(adjPosition);
                return VIEW_TYPE_ITEM;
            }
        }

        // footer view
        this.curHeaderOrFooterPos = position - headersCount - adapterCount;
        return VIEW_TYPE_FOOTER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                // create header view
                EmptyHeaderOrFooterViewHolder headerViewHolder;
                View tempHeadView = mHeaderView.get(curHeaderOrFooterPos);
                if (mLayoutManagerType == LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                    FrameLayout mContainerView = new FrameLayout(mHeaderView.get(curHeaderOrFooterPos).getContext());
                    mContainerView.addView(tempHeadView);
                    headerViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                    StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setFullSpan(true);
                    headerViewHolder.itemView.setLayoutParams(layoutParams);
                } else {
                    headerViewHolder = new EmptyHeaderOrFooterViewHolder(tempHeadView);
                }
                return headerViewHolder;
            case VIEW_TYPE_FOOTER:
                // create footer view
                EmptyHeaderOrFooterViewHolder footerViewHolder;
                View tempFooterView = mFooterView.get(curHeaderOrFooterPos);
                if (mLayoutManagerType == LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                    FrameLayout mContainerView = new FrameLayout(mHeaderView.get(curHeaderOrFooterPos).getContext());
                    mContainerView.addView(tempFooterView);
                    footerViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                    StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setFullSpan(true);
                    footerViewHolder.itemView.setLayoutParams(layoutParams);
                } else {
                    footerViewHolder = new EmptyHeaderOrFooterViewHolder(mFooterView.get(curHeaderOrFooterPos));
                }
                return footerViewHolder;
            default:
                // create default item view
                RecyclerView.ViewHolder itemViewHolder = mReqAdapter.onCreateViewHolder(parent, viewType);
                // add click listener
                itemViewHolder.itemView.setOnClickListener(this);
                itemViewHolder.itemView.setOnLongClickListener(this);
                return itemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            final int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (mReqAdapter != null) {
                adapterCount = mReqAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mReqAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (mReqAdapter != null) {
            mReqAdapter.registerAdapterDataObserver(observer);
        }
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (mReqAdapter != null) {
            mReqAdapter.unregisterAdapterDataObserver(observer);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (mReqAdapter != null) {
            mReqAdapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (mReqAdapter != null) {
            mReqAdapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    public void setOnItemClickListener(FamiliarRecyclerView.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(FamiliarRecyclerView.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public void onClick(View v) {
        if (null != mOnItemClickListener) {
            mOnItemClickListener.onItemClick(mFamiliarRecyclerView, v, mFamiliarRecyclerView.getChildAdapterPosition(v) - mFamiliarRecyclerView.getHeaderViewsCount());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (null != mOnItemClickListener) {
            return mOnItemLongClickListener.onItemLongClick(mFamiliarRecyclerView, v, mFamiliarRecyclerView.getChildAdapterPosition(v) - mFamiliarRecyclerView.getHeaderViewsCount());
        }
        return false;
    }

    class EmptyHeaderOrFooterViewHolder extends RecyclerView.ViewHolder {
        public EmptyHeaderOrFooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
