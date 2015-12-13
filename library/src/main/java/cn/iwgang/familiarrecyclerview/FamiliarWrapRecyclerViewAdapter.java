package cn.iwgang.familiarrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap FamiliarRecyclerView Adapter
 * Created by iWgang on 15/10/31.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarWrapRecyclerViewAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {
    private static final int MIN_INTERVAL_CLICK_TIME = 100;

    private static final int VIEW_TYPE_HEADER = -1;
    private static final int VIEW_TYPE_FOOTER = -2;
    private static final int VIEW_TYPE_EMPTY_VIEW = -3;

    private List<View> mHeaderView;
    private List<View> mFooterView;
    private RecyclerView.Adapter mReqAdapter;
    private int curHeaderOrFooterPos;
    private int mLayoutManagerType = FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR;
    private FamiliarRecyclerView.OnItemClickListener mOnItemClickListener;
    private FamiliarRecyclerView.OnItemLongClickListener mOnItemLongClickListener;
    private FamiliarRecyclerView mFamiliarRecyclerView;
    private long mLastClickTime;
    private FamiliarRecyclerView.OnHeadViewBindViewHolderListener mOnHeadViewBindViewHolderListener;
    private FamiliarRecyclerView.OnFooterViewBindViewHolderListener mOnFooterViewBindViewHolderListener;
    private List<Integer> mHeadOrFooterInitInvokeViewBindViewFlag = new ArrayList<>();

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
        int tempItemCount = mReqAdapter.getItemCount();
        if (mFamiliarRecyclerView.isKeepShowHeadOrFooter()) {
            count += tempItemCount == 0 ? 1 : tempItemCount;
        } else {
            count += tempItemCount;
        }

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
        if (mReqAdapter != null && mReqAdapter.getItemCount() > 0 && position >= headersCount) {
            int adjPosition = position - headersCount;
            adapterCount = mReqAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mReqAdapter.getItemViewType(adjPosition);
            }
        } else if (mFamiliarRecyclerView.isKeepShowHeadOrFooter()) {
            // empty view
            if (position == headersCount) return VIEW_TYPE_EMPTY_VIEW;
        }

        // footer view
        this.curHeaderOrFooterPos = position - headersCount - adapterCount;
        return VIEW_TYPE_FOOTER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                // create header view
                EmptyHeaderOrFooterViewHolder headerViewHolder;
                View tempHeadView = mHeaderView.get(curHeaderOrFooterPos);
                if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                    FrameLayout mContainerView = new FrameLayout(tempHeadView.getContext());
                    mContainerView.addView(tempHeadView);
                    headerViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                    StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setFullSpan(true);
                    headerViewHolder.itemView.setLayoutParams(layoutParams);
                } else {
                    headerViewHolder = new EmptyHeaderOrFooterViewHolder(tempHeadView);
                }
                return headerViewHolder;
            }
            case VIEW_TYPE_FOOTER: {
                // create footer view
                EmptyHeaderOrFooterViewHolder footerViewHolder;
                View tempFooterView;

                // fix fast delete IndexOutOfBoundsException
                int footerViewCount = mFooterView.size();
                if (curHeaderOrFooterPos >= footerViewCount) {
                    curHeaderOrFooterPos = footerViewCount - 1;
                    tempFooterView = mFooterView.get(curHeaderOrFooterPos);
                    ViewParent tempFooterViewParent = tempFooterView.getParent();
                    if (null != tempFooterViewParent) {
                        ((ViewGroup)tempFooterViewParent).removeView(tempFooterView);
                    }
                } else {
                    tempFooterView = mFooterView.get(curHeaderOrFooterPos);
                }
                if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                    FrameLayout mContainerView = new FrameLayout(tempFooterView.getContext());
                    mContainerView.addView(tempFooterView);
                    footerViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                    StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setFullSpan(true);
                    footerViewHolder.itemView.setLayoutParams(layoutParams);
                } else {
                    footerViewHolder = new EmptyHeaderOrFooterViewHolder(tempFooterView);
                }
                return footerViewHolder;
            }
            case VIEW_TYPE_EMPTY_VIEW: {
                EmptyHeaderOrFooterViewHolder emptyViewHolder;
                View emptyView = mFamiliarRecyclerView.getEmptyView();
                emptyView.setVisibility(View.VISIBLE);
                if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
                    FrameLayout mContainerView = new FrameLayout(emptyView.getContext());
                    mContainerView.addView(emptyView);
                    emptyViewHolder = new EmptyHeaderOrFooterViewHolder(mContainerView);
                    StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setFullSpan(true);
                    emptyViewHolder.itemView.setLayoutParams(layoutParams);
                } else {
                    emptyViewHolder = new EmptyHeaderOrFooterViewHolder(emptyView);
                }

                return emptyViewHolder;
            }
            default:
                // create default item view
                RecyclerView.ViewHolder itemViewHolder = mReqAdapter.onCreateViewHolder(parent, viewType);
                // add click listener
                if (null != mOnItemClickListener) {
                    itemViewHolder.itemView.setOnClickListener(this);
                }
                if (null != mOnItemLongClickListener) {
                    itemViewHolder.itemView.setOnLongClickListener(this);
                }
                return itemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int curItemViewType = getItemViewType(position);

        if (curItemViewType == VIEW_TYPE_HEADER && null != mOnHeadViewBindViewHolderListener) {
            // head view
            boolean isInitializeInvoke = false;
            int tempHeadViewHashCode = holder.itemView.hashCode();
            if (!mHeadOrFooterInitInvokeViewBindViewFlag.contains(tempHeadViewHashCode)) {
                mHeadOrFooterInitInvokeViewBindViewFlag.add(tempHeadViewHashCode);
                isInitializeInvoke = true;
            }
            mOnHeadViewBindViewHolderListener.onHeadViewBindViewHolder(holder, position, isInitializeInvoke);
        } else if (curItemViewType == VIEW_TYPE_FOOTER && null != mOnFooterViewBindViewHolderListener) {
            // footer view
            boolean isInitializeInvoke = false;
            int tempFooterViewHashCode = holder.itemView.hashCode();
            if (!mHeadOrFooterInitInvokeViewBindViewFlag.contains(tempFooterViewHashCode)) {
                mHeadOrFooterInitInvokeViewBindViewFlag.add(tempFooterViewHashCode);
                isInitializeInvoke = true;
            }
            mOnFooterViewBindViewHolderListener.onFooterViewBindViewHolder(holder, position - getHeadersCount() - (null != mReqAdapter ? mReqAdapter.getItemCount() : 0), isInitializeInvoke);
        } else if (curItemViewType >= 0) {
            // item view
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

    public void setOnItemClickListener(FamiliarRecyclerView.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(FamiliarRecyclerView.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public void onClick(View v) {
        long curTime = System.currentTimeMillis();
        if (null != mOnItemClickListener && curTime - mLastClickTime > MIN_INTERVAL_CLICK_TIME) {
            mLastClickTime = curTime;
            mOnItemClickListener.onItemClick(mFamiliarRecyclerView, v, mFamiliarRecyclerView.getChildAdapterPosition(v) - mFamiliarRecyclerView.getHeaderViewsCount());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        long curTime = System.currentTimeMillis();
        if (null != mOnItemClickListener && curTime - mLastClickTime > MIN_INTERVAL_CLICK_TIME) {
            mLastClickTime = curTime;
            return mOnItemLongClickListener.onItemLongClick(mFamiliarRecyclerView, v, mFamiliarRecyclerView.getChildAdapterPosition(v) - mFamiliarRecyclerView.getHeaderViewsCount());
        }
        return false;
    }

    public void setOnHeadViewBindViewHolderListener(FamiliarRecyclerView.OnHeadViewBindViewHolderListener mOnHeadViewBindViewHolderListener) {
        this.mOnHeadViewBindViewHolderListener = mOnHeadViewBindViewHolderListener;
    }

    public void setOnFooterViewBindViewHolderListener(FamiliarRecyclerView.OnFooterViewBindViewHolderListener mOnFooterViewBindViewHolderListener) {
        this.mOnFooterViewBindViewHolderListener = mOnFooterViewBindViewHolderListener;
    }

    class EmptyHeaderOrFooterViewHolder extends RecyclerView.ViewHolder {
        public EmptyHeaderOrFooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
