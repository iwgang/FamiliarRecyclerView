package cn.iwgang.familiarrecyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Familiar OnScrollListener
 * Created by iWgang on 15/11/13.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public abstract class FamiliarRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private int mLayoutManagerType = 1; // 1 LinearLayoutManager, 2 GridLayoutManager, 3 StaggeredGridLayoutManager
    private boolean isCanScrolledCallback = false;
    private int[] mStaggeredFirstPositions, mStaggeredLastPositions;

    private int callbackType = 0; // 0 not callback, 1 scrolled to top, 2 scrolled to bottom

    public FamiliarRecyclerViewOnScrollListener(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.getClass().isAssignableFrom(LinearLayoutManager.class)) {
            mLayoutManagerType = 1;
            mLinearLayoutManager = (LinearLayoutManager)layoutManager;
        } else if (layoutManager.getClass().isAssignableFrom(GridLayoutManager.class)) {
            mLayoutManagerType = 2;
            mGridLayoutManager = (GridLayoutManager)layoutManager;
        } else if (layoutManager.getClass().isAssignableFrom(StaggeredGridLayoutManager.class)) {
            mLayoutManagerType = 3;
            mStaggeredGridLayoutManager = (StaggeredGridLayoutManager)layoutManager;
            mStaggeredLastPositions = new int[mStaggeredGridLayoutManager.getSpanCount()];
            mStaggeredFirstPositions = new int[mStaggeredGridLayoutManager.getSpanCount()];
        }
    }

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!isCanScrolledCallback) return ;

        if (checkToTop()) {
            // scrolled to top
            callbackType = 1;
            if (!isIdleCallBack()) {
                onScrolledToTop();
            }
        } else if (checkToBottom(recyclerView)) {
            // scrolled to bottom
            callbackType = 2;
            if (!isIdleCallBack()) {
                onScrolledToBottom();
            }
        } else {
            callbackType = 0;
        }
    }

    @Override
    public final void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                isCanScrolledCallback = false;
                if (isIdleCallBack()) {
                    if (callbackType == 1) {
                        onScrolledToTop();
                    } else if (callbackType == 2) {
                        onScrolledToBottom();
                    }
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                isCanScrolledCallback = true;
                break;
        }
    }

    private boolean checkToTop() {
        switch (mLayoutManagerType) {
            case 1:
                return mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            case 2:
                return mGridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            case 3:
                mStaggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(mStaggeredFirstPositions);
                return  mStaggeredFirstPositions[0] == 0;
        }

        return false;
    }

    private boolean checkToBottom(RecyclerView recyclerView) {
        switch (mLayoutManagerType) {
            case 1:
                return mLinearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1;
            case 2:
                return mGridLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1;
            case 3:
                mStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(mStaggeredLastPositions);
                if (mStaggeredLastPositions.length > 0) {
                    int totalCount = recyclerView.getAdapter().getItemCount() - 1;
                    for (int curPos : mStaggeredLastPositions) {
                        if (curPos == totalCount) {
                            return true;
                        }
                    }
                }
        }

        return false;
    }

    public abstract void onScrolledToTop();
    public abstract void onScrolledToBottom();

    protected boolean isIdleCallBack(){
        return true;
    }

}