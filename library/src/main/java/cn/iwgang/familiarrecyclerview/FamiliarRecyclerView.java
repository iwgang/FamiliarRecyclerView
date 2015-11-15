package cn.iwgang.familiarrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * FamiliarRecyclerView
 * Created by iWgang on 15/10/31.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarRecyclerView extends RecyclerView {
    private List<View> mHeaderView = new ArrayList<View>();
    private List<View> mFooterView = new ArrayList<View>();
    private FamiliarWrapRecyclerViewAdapter mWrapFamiliarRecyclerViewAdapter;
    private RecyclerView.Adapter mReqAdapter;
    private GridLayoutManager mCurGridLayoutManager;
    private int mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR;
    private FamiliarDefaultItemDecoration mFamiliarDefaultItemDecoration;

    private int mDividerHeight;
    private Drawable mDivider;

    private boolean isDefaultItemDecoration = true;

    private OnItemClickListener mTempOnItemClickListener;
    private OnItemLongClickListener mTempOnItemLongClickListener;

    public FamiliarRecyclerView(Context context) {
        this(context, null);
    }

    public FamiliarRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamiliarRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FamiliarRecyclerView);
        mDivider = ta.getDrawable(R.styleable.FamiliarRecyclerView_hrv_divider);
        int tempDividerHeight = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_hrv_dividerHeight, -1);
        if (mDivider != null) {
            mDividerHeight = mDivider.getIntrinsicHeight();

            if (tempDividerHeight == -1) {
                if (mDividerHeight <= 0) {
                    // default
                    mDividerHeight = 1;
                }
            } else {
                mDividerHeight = tempDividerHeight;
            }
        } else {
            mDividerHeight = 0;
        }
        ta.recycle();

        initView();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mReqAdapter = adapter;
        mWrapFamiliarRecyclerViewAdapter = new FamiliarWrapRecyclerViewAdapter(this, adapter, mHeaderView, mFooterView, mLayoutManagerType);

        mWrapFamiliarRecyclerViewAdapter.setOnItemClickListener(mTempOnItemClickListener);
        mWrapFamiliarRecyclerViewAdapter.setOnItemLongClickListener(mTempOnItemLongClickListener);

        super.setAdapter(mWrapFamiliarRecyclerViewAdapter);
    }

    private void initView() {

    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        if (layout instanceof GridLayoutManager) {
            mCurGridLayoutManager = ((GridLayoutManager) layout);
            mCurGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position < getHeaderViewsCount() || position >= mReqAdapter.getItemCount() + getHeaderViewsCount()) {
                        // header or footer span
                        return mCurGridLayoutManager.getSpanCount();
                    } else {
                        // default item span
                        return 1;
                    }
                }
            });

            mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID;
        } else if (layout instanceof StaggeredGridLayoutManager) {
            mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_STAGGERED_GRID;
        } else {
            // LinearLayoutManager
            mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR;
        }

       setDivider(mDivider);
    }

    @Override
    public void addItemDecoration(ItemDecoration decor) {
        if (null == decor) return ;

        // remove default ItemDecoration
        if (null != mFamiliarDefaultItemDecoration) {
            removeItemDecoration(mFamiliarDefaultItemDecoration);
            mFamiliarDefaultItemDecoration = null;
        }

        isDefaultItemDecoration = false;

        super.addItemDecoration(decor);
    }

    private void addDefaultItemDecoration() {
        if (null != mFamiliarDefaultItemDecoration) {
            removeItemDecoration(mFamiliarDefaultItemDecoration);
            mFamiliarDefaultItemDecoration = null;
        }

        mFamiliarDefaultItemDecoration = new FamiliarDefaultItemDecoration(this, mDivider, mDividerHeight);
        super.addItemDecoration(mFamiliarDefaultItemDecoration);
    }

    public void setDivider(Drawable divider) {
        if (null != divider) {
            this.mDivider = divider;

            if (isDefaultItemDecoration) {
                if (null == mFamiliarDefaultItemDecoration) {
                    addDefaultItemDecoration();
                } else {
                    mFamiliarDefaultItemDecoration.setDividerDrawable(mDivider);
                    invalidate();
                }
            }
        }
    }

    public void setDividerHeight(int height) {
        this.mDividerHeight = height;

        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setDividerDrawableSize(height);
            invalidate();
        }
    }

    public void addHeaderView(View v) {
        if (mHeaderView.contains(v)) return;

        mHeaderView.add(v);
        if (null != mReqAdapter) {
            int pos = mHeaderView.size() - 1;
            mReqAdapter.notifyItemInserted(pos);
            scrollToPosition(pos);
        }
    }

    public boolean removeHeaderView(View v) {
        if (!mHeaderView.contains(v)) return false;

        mReqAdapter.notifyItemRemoved(mHeaderView.indexOf(v));
        return mHeaderView.remove(v);
    }

    public void addFooterView(View v) {
        if (mFooterView.contains(v)) return;

        mFooterView.add(v);
        if (null != mReqAdapter) {
            int pos = mReqAdapter.getItemCount() + getHeaderViewsCount() + mFooterView.size() - 1;
            mReqAdapter.notifyItemInserted(pos);
            scrollToPosition(pos);
        }
    }

    public boolean removeFooterView(View v) {
        if (!mFooterView.contains(v)) return false;

        mReqAdapter.notifyItemRemoved(mReqAdapter.getItemCount() + getHeaderViewsCount() + mFooterView.indexOf(v));
        return mFooterView.remove(v);
    }

    public int getHeaderViewsCount() {
        return mHeaderView.size();
    }

    public int getFooterViewsCount() {
        return mFooterView.size();
    }

    public int getLastVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();
        if (null == layoutManager) return -1;

        switch (mLayoutManagerType) {
            case FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR:
                return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            case FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID:
                return ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            case FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                int[] firstVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                return firstVisibleItemPositions.length > 0 ? firstVisibleItemPositions[0] : -1;
        }

        return -1;
    }

    public void setHeaderDividersEnabled(boolean isHeaderDividersEnabled) {
        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setHeaderDividersEnabled(isHeaderDividersEnabled);
            invalidate();
        }
    }

    public void setFooterDividersEnabled(boolean isFooterDividersEnabled) {
        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setFooterDividersEnabled(isFooterDividersEnabled);
            invalidate();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        if (null == mWrapFamiliarRecyclerViewAdapter) {
            mTempOnItemClickListener = listener;
        } else {
            mWrapFamiliarRecyclerViewAdapter.setOnItemClickListener(listener);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (null == mWrapFamiliarRecyclerViewAdapter) {
            mTempOnItemLongClickListener = listener;
        } else {
            mWrapFamiliarRecyclerViewAdapter.setOnItemLongClickListener(listener);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position);
    }

}
