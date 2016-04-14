package cn.iwgang.familiarrecyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Support pull refresh and load more
 * Created by iWgang on 16/04/13.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarRefreshRecyclerView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {
    private FamiliarRecyclerView mRvList;
    private IFamiliarLoadMore mLoadMoreView;

    private OnPullRefreshListener mOnPullRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private FamiliarRecyclerViewOnScrollListener mFamiliarRecyclerViewOnScrollListener;
    private boolean isPullRefreshEnabled = true;
    private boolean isLoadMoreEnabled = false;
    private boolean isSetLoadMoreView = false;

    public FamiliarRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        mRvList = new FamiliarRecyclerView(getContext(), attrs);
        addView(mRvList, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (!isPullRefreshEnabled) return;

        callOnPullRefresh();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRvList.setAdapter(adapter);
    }

    public void setLoadMoreView(IFamiliarLoadMore loadMoreView) {
        if (null == loadMoreView) {
            if (null != mLoadMoreView) {
                mRvList.removeFooterView(mLoadMoreView.getView());
                mRvList.removeOnScrollListener(mFamiliarRecyclerViewOnScrollListener);
                isSetLoadMoreView = false;
                mLoadMoreView = null;
            }
            return;
        }

        mLoadMoreView = loadMoreView;
        initializeLoadMoreView();
        isSetLoadMoreView = null != mLoadMoreView;
    }

    /**
     * Get FamiliarRecyclerView
     *
     * @return FamiliarRecyclerView
     */
    public FamiliarRecyclerView getFamiliarRecyclerView() {
        return mRvList;
    }

    /**
     * Automatic pull refresh
     */
    protected void autoRefresh() {
        if (!isPullRefreshEnabled) return;

        setRefreshing(true);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callOnPullRefresh();
            }
        }, 1000);
    }

    public void setPullRefreshEnabled(boolean enabled) {
        if (isPullRefreshEnabled == enabled) return;

        setEnabled(enabled);

        if (!enabled) {
            setRefreshing(false);
        }

        this.isPullRefreshEnabled = enabled;
    }

    public void setLoadMoreEnabled(boolean enabled) {
        if (!isSetLoadMoreView || isLoadMoreEnabled == enabled || null == mLoadMoreView) return;

        if (!enabled) {
            mRvList.removeFooterView(mLoadMoreView.getView());
        } else {
            mRvList.addFooterView(mLoadMoreView.getView());
        }

        isLoadMoreEnabled = enabled;
    }

    public void pullRefreshComplete() {
        setRefreshing(false);
    }

    public void loadMoreComplete() {
        if (isSetLoadMoreView) {
            mLoadMoreView.showNormal();
        }
    }

    private void initializeLoadMoreView() {
        if (null == mFamiliarRecyclerViewOnScrollListener) {
            mFamiliarRecyclerViewOnScrollListener = new FamiliarRecyclerViewOnScrollListener(mRvList.getLayoutManager()) {
                @Override
                public void onScrolledToTop() {
                }

                @Override
                public void onScrolledToBottom() {
                    if (!isLoadMoreEnabled || !isSetLoadMoreView || mLoadMoreView.isLoading()) return;

                    mLoadMoreView.showLoading();
                    callOnLoadMore();
                }
            };
        }
        mRvList.addOnScrollListener(mFamiliarRecyclerViewOnScrollListener);
        mLoadMoreView.getView().setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    }

    private void callOnPullRefresh() {
        if (null != mOnPullRefreshListener) {
            mOnPullRefreshListener.onPullRefresh();
        }
    }

    private void callOnLoadMore() {
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    public void setOnPullRefreshListener(OnPullRefreshListener onPullRefreshListener) {
        this.mOnPullRefreshListener = onPullRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setOnItemClickListener(final FamiliarRecyclerView.OnItemClickListener onItemClickListener) {
        if (null != onItemClickListener) {
            mRvList.setOnItemClickListener(onItemClickListener);
        }
    }

    public void setOnItemLongClickListener(final FamiliarRecyclerView.OnItemLongClickListener onItemLongClickListener) {
        if (null != onItemLongClickListener) {
            mRvList.setOnItemLongClickListener(onItemLongClickListener);
        }
    }

    public interface OnPullRefreshListener {
        void onPullRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
