package cn.iwgang.familiarrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

/**
 * FamiliarRecyclerView
 * Created by iWgang on 15/10/31.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarRecyclerView extends RecyclerView {
    public static final int LAYOUT_MANAGER_TYPE_LINEAR = 0;
    public static final int LAYOUT_MANAGER_TYPE_GRID = 1;
    public static final int LAYOUT_MANAGER_TYPE_STAGGERED_GRID = 2;

    private static final int DEF_LAYOUT_MANAGER_TYPE = LAYOUT_MANAGER_TYPE_LINEAR;
    private static final int DEF_GRID_SPAN_COUNT = 2;
    private static final int DEF_LAYOUT_MANAGER_ORIENTATION = OrientationHelper.VERTICAL;
    private static final int DEF_DIVIDER_HEIGHT = 1;

    private List<View> mHeaderView = new ArrayList<View>();
    private List<View> mFooterView = new ArrayList<View>();
    private FamiliarWrapRecyclerViewAdapter mWrapFamiliarRecyclerViewAdapter;
    private RecyclerView.Adapter mReqAdapter;
    private GridLayoutManager mCurGridLayoutManager;
    private FamiliarDefaultItemDecoration mFamiliarDefaultItemDecoration;

    private Drawable mVerticalDivider;
    private Drawable mHorizontalDivider;
    private int mVerticalDividerHeight;
    private int mHorizontalDividerHeight;
    private int mItemViewBothSidesMargin;
    private boolean isHeaderDividersEnabled = false;
    private boolean isFooterDividersEnabled = false;
    private boolean isDefaultItemDecoration = true;
    private boolean isRetainShowHeadOrFoot = false;
    private int mEmptyViewResId;
    private View mEmptyView;
    private OnItemClickListener mTempOnItemClickListener;
    private OnItemLongClickListener mTempOnItemLongClickListener;
    private int mLayoutManagerType;

    public FamiliarRecyclerView(Context context) {
        this(context, null);
    }

    public FamiliarRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamiliarRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FamiliarRecyclerView);
        Drawable divider = ta.getDrawable(R.styleable.FamiliarRecyclerView_frv_divider);
        int dividerHeight = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_dividerHeight, -1);
        mVerticalDivider = ta.getDrawable(R.styleable.FamiliarRecyclerView_frv_dividerVertical);
        mHorizontalDivider = ta.getDrawable(R.styleable.FamiliarRecyclerView_frv_dividerHorizontal);
        mVerticalDividerHeight = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_dividerVerticalHeight, -1);
        mHorizontalDividerHeight = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_dividerHorizontalHeight, -1);
        mItemViewBothSidesMargin = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_itemViewBothSidesMargin, 0);
        mEmptyViewResId = ta.getResourceId(R.styleable.FamiliarRecyclerView_frv_emptyView, -1);
        isHeaderDividersEnabled = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_headerDividersEnabled, false);
        isFooterDividersEnabled = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_footerDividersEnabled, false);
        if (ta.hasValue(R.styleable.FamiliarRecyclerView_frv_layoutManager)) {
            int layoutManagerType = ta.getInt(R.styleable.FamiliarRecyclerView_frv_layoutManager, DEF_LAYOUT_MANAGER_TYPE);
            int layoutManagerOrientation = ta.getInt(R.styleable.FamiliarRecyclerView_frv_layoutManagerOrientation, DEF_LAYOUT_MANAGER_ORIENTATION);
            boolean isReverseLayout = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_isReverseLayout, false);
            int gridSpanCount = ta.getInt(R.styleable.FamiliarRecyclerView_frv_spanCount, DEF_GRID_SPAN_COUNT);

            switch (layoutManagerType) {
                case LAYOUT_MANAGER_TYPE_LINEAR:
                    processGridDivider(divider, dividerHeight, false, layoutManagerOrientation);
                    setLayoutManager(new LinearLayoutManager(context, layoutManagerOrientation, isReverseLayout));
                    break;
                case LAYOUT_MANAGER_TYPE_GRID:
                    processGridDivider(divider, dividerHeight, false, layoutManagerOrientation);
                    setLayoutManager(new GridLayoutManager(context, gridSpanCount, layoutManagerOrientation, isReverseLayout));
                    break;
                case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                    processGridDivider(divider, dividerHeight, false, layoutManagerOrientation);
                    setLayoutManager(new StaggeredGridLayoutManager(gridSpanCount, layoutManagerOrientation));
                    break;
            }
        }
        ta.recycle();
    }

    private void processGridDivider(Drawable divider, int dividerHeight, boolean isLinearLayoutManager, int layoutManagerOrientation) {
        if ((null == mVerticalDivider || null == mHorizontalDivider) && null != divider) {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && null == mHorizontalDivider) {
                    mHorizontalDivider = divider;
                } else if (layoutManagerOrientation == OrientationHelper.HORIZONTAL && null == mVerticalDivider) {
                    mVerticalDivider = divider;
                }
            } else {
                if (null == mVerticalDivider) {
                    mVerticalDivider = divider;
                }

                if (null == mHorizontalDivider) {
                    mHorizontalDivider = divider;
                }
            }
        }

        if (mVerticalDividerHeight > 0 && mHorizontalDividerHeight > 0) return ;

        if (dividerHeight > 0) {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && mHorizontalDividerHeight <= 0) {
                    mHorizontalDividerHeight = dividerHeight;
                } else if(layoutManagerOrientation == OrientationHelper.HORIZONTAL && mVerticalDividerHeight <= 0) {
                    mVerticalDividerHeight = dividerHeight;
                }
            } else {
                if (mVerticalDividerHeight <= 0) {
                    mVerticalDividerHeight = dividerHeight;
                }

                if (mHorizontalDividerHeight <= 0) {
                    mHorizontalDividerHeight = dividerHeight;
                }
            }
        } else {
            int dividerIntrinsicHeight = null != divider && divider.getIntrinsicHeight() > 0 ? divider.getIntrinsicHeight() : -1;

            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && mHorizontalDividerHeight <= 0) {
                    if (null != mHorizontalDivider && mHorizontalDivider.getIntrinsicHeight() > 0) {
                        mHorizontalDividerHeight = mHorizontalDivider.getIntrinsicHeight();
                    } else {
                        mHorizontalDividerHeight = dividerIntrinsicHeight > 0 ? dividerIntrinsicHeight : DEF_DIVIDER_HEIGHT;
                    }
                } else if(layoutManagerOrientation == OrientationHelper.HORIZONTAL && mVerticalDividerHeight <= 0) {
                    if (null != mVerticalDivider && mVerticalDivider.getIntrinsicHeight() > 0) {
                        mVerticalDividerHeight = mVerticalDivider.getIntrinsicHeight();
                    } else {
                        mVerticalDividerHeight = dividerIntrinsicHeight > 0 ? dividerIntrinsicHeight : DEF_DIVIDER_HEIGHT;
                    }
                }
            } else {
                if (mVerticalDividerHeight <= 0) {
                    if (null != mVerticalDivider && mVerticalDivider.getIntrinsicHeight() > 0) {
                        mVerticalDividerHeight = mVerticalDivider.getIntrinsicHeight();
                    } else {
                        mVerticalDividerHeight = dividerIntrinsicHeight > 0 ? dividerIntrinsicHeight : DEF_DIVIDER_HEIGHT;
                    }
                }

                if (mHorizontalDividerHeight <= 0) {
                    if (null != mHorizontalDivider && mHorizontalDivider.getIntrinsicHeight() > 0) {
                        mHorizontalDividerHeight = mHorizontalDivider.getIntrinsicHeight();
                    } else {
                        mHorizontalDividerHeight = dividerIntrinsicHeight > 0 ? dividerIntrinsicHeight : DEF_DIVIDER_HEIGHT;
                    }
                }
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // Only once
        if (mEmptyViewResId != -1) {
            if (null != getParent()) {
                ViewGroup parentView = ((ViewGroup)getParent());
                View tempEmptyView1 = parentView.findViewById(mEmptyViewResId);

                if (null != tempEmptyView1) {
                    mEmptyView = tempEmptyView1;

                    if (isRetainShowHeadOrFoot) parentView.removeView(tempEmptyView1);
                } else {
                    ViewParent pParentView = parentView.getParent();
                    if (null != pParentView && pParentView instanceof ViewGroup) {
                        View tempEmptyView2 = ((ViewGroup) pParentView).findViewById(mEmptyViewResId);
                        if (null != tempEmptyView2) {
                            mEmptyView = tempEmptyView2;

                            if (isRetainShowHeadOrFoot) ((ViewGroup) pParentView).removeView(tempEmptyView2);
                        }
                    }
                }
            }
            mEmptyViewResId = -1;
        } else if (isRetainShowHeadOrFoot && null != mEmptyView) {
            ((ViewGroup)mEmptyView.getParent()).removeView(mEmptyView);
        }

        if (null == adapter) {
            if (null != mReqAdapter) {
                if (!isRetainShowHeadOrFoot) {
                    mReqAdapter.unregisterAdapterDataObserver(mEmptyAdapterDataObserver);
                }
                mReqAdapter = null;
                mWrapFamiliarRecyclerViewAdapter = null;

                processEmptyView(0);
            }

            return;
        }

        mReqAdapter = adapter;
        mWrapFamiliarRecyclerViewAdapter = new FamiliarWrapRecyclerViewAdapter(this, adapter, mHeaderView, mFooterView, mLayoutManagerType);

        mWrapFamiliarRecyclerViewAdapter.setOnItemClickListener(mTempOnItemClickListener);
        mWrapFamiliarRecyclerViewAdapter.setOnItemLongClickListener(mTempOnItemLongClickListener);

        super.setAdapter(mWrapFamiliarRecyclerViewAdapter);

        mReqAdapter.registerAdapterDataObserver(mEmptyAdapterDataObserver);

            processEmptyView(mReqAdapter.getItemCount());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (null != mReqAdapter) {
            mReqAdapter.unregisterAdapterDataObserver(mEmptyAdapterDataObserver);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        if (null == layout) return ;

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

            mLayoutManagerType = LAYOUT_MANAGER_TYPE_GRID;
            setDivider(mVerticalDivider, mHorizontalDivider);
        } else if (layout instanceof StaggeredGridLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_STAGGERED_GRID;
            setDivider(mVerticalDivider, mHorizontalDivider);
        } else if (layout instanceof LinearLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_LINEAR;
            if (((LinearLayoutManager)layout).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                setDividerVertical(mVerticalDivider);
            } else {
                setDividerHorizontal(mHorizontalDivider);
            }
        }
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

        mFamiliarDefaultItemDecoration = new FamiliarDefaultItemDecoration(this, mVerticalDivider, mHorizontalDivider, mVerticalDividerHeight, mHorizontalDividerHeight);
        mFamiliarDefaultItemDecoration.setItemViewBothSidesMargin(mItemViewBothSidesMargin);
        mFamiliarDefaultItemDecoration.setHeaderDividersEnabled(isHeaderDividersEnabled);
        mFamiliarDefaultItemDecoration.setFooterDividersEnabled(isFooterDividersEnabled);
        super.addItemDecoration(mFamiliarDefaultItemDecoration);
    }

    private void processEmptyView(int curTotalNum) {
        if (!isRetainShowHeadOrFoot && null != mEmptyView) {
            boolean isShowEmptyView = curTotalNum == 0;
            mEmptyView.setVisibility(isShowEmptyView ? VISIBLE : GONE);
            setVisibility(isShowEmptyView ? GONE : VISIBLE);
        }
    }

    public void setEmptyView(View emptyView) {
        setEmptyView(emptyView, false);
    }

    public void setEmptyView(View emptyView, boolean isRetainShowHeadOrFoot) {
        this.mEmptyView = emptyView;
        this.isRetainShowHeadOrFoot = isRetainShowHeadOrFoot;
    }

    public void setEmptyViewRetainShowHeadOrFoot(boolean isRetainShowHeadOrFoot) {
        this.isRetainShowHeadOrFoot = isRetainShowHeadOrFoot;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public boolean isRetainShowHeadOrFoot() {
        return isRetainShowHeadOrFoot;
    }

    public void setDivider(Drawable divider) {
        if (!isDefaultItemDecoration || (mVerticalDividerHeight <= 0 && mHorizontalDividerHeight <= 0)) return ;

        if (this.mVerticalDivider != divider) {
            this.mVerticalDivider = divider;
        }

        if (this.mHorizontalDivider != divider) {
            this.mHorizontalDivider = divider;
        }

        if (null == mFamiliarDefaultItemDecoration) {
            addDefaultItemDecoration();
        } else {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDivider(Drawable dividerVertical, Drawable dividerHorizontal) {
        if (!isDefaultItemDecoration || (mVerticalDividerHeight <= 0 && mHorizontalDividerHeight <= 0)) return ;

        if (this.mVerticalDivider != dividerVertical) {
            this.mVerticalDivider = dividerVertical;
        }

        if (this.mHorizontalDivider != dividerHorizontal) {
            this.mHorizontalDivider = dividerHorizontal;
        }

        if (null == mFamiliarDefaultItemDecoration) {
            addDefaultItemDecoration();
        } else {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerVertical(Drawable dividerVertical) {
        if (!isDefaultItemDecoration || mVerticalDividerHeight <= 0) return ;

        if (this.mVerticalDivider != dividerVertical) {
            this.mVerticalDivider = dividerVertical;
        }

        if (null == mFamiliarDefaultItemDecoration) {
            addDefaultItemDecoration();
        } else {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHorizontal(Drawable dividerHorizontal) {
        if (!isDefaultItemDecoration || mHorizontalDividerHeight <= 0) return ;

        if (this.mHorizontalDivider != dividerHorizontal) {
            this.mHorizontalDivider = dividerHorizontal;
        }

        if (null == mFamiliarDefaultItemDecoration) {
            addDefaultItemDecoration();
        } else {
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHeight(int height) {
        this.mVerticalDividerHeight = height;
        this.mHorizontalDividerHeight = height;

        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerVerticalHeight(int height) {
        this.mVerticalDividerHeight = height;

        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHorizontalHeight(int height) {
        this.mHorizontalDividerHeight = height;

        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, false);
    }

    public void addHeaderView(View v, boolean isScrollTo) {
        if (mHeaderView.contains(v)) return;

        mHeaderView.add(v);
        if (null != mReqAdapter) {
            int pos = mHeaderView.size() - 1;
            mReqAdapter.notifyItemInserted(pos);

            if (isScrollTo) {
                scrollToPosition(pos);
            }
        }
    }

    public boolean removeHeaderView(View v) {
        if (!mHeaderView.contains(v)) return false;

        mReqAdapter.notifyItemRemoved(mHeaderView.indexOf(v));
        return mHeaderView.remove(v);
    }

    public void addFooterView(View v) {
        addFooterView(v, false);
    }

    public void addFooterView(View v, boolean isScrollTo) {
        if (mFooterView.contains(v)) return;

        mFooterView.add(v);
        if (null != mReqAdapter) {
            int pos = mReqAdapter.getItemCount() + getHeaderViewsCount() + mFooterView.size() - 1;
            mReqAdapter.notifyItemInserted(pos);
            if (isScrollTo) {
                scrollToPosition(pos);
            }
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
            case LAYOUT_MANAGER_TYPE_LINEAR:
                return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            case LAYOUT_MANAGER_TYPE_GRID:
                return ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                int[] firstVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                return firstVisibleItemPositions.length > 0 ? firstVisibleItemPositions[0] : -1;
        }

        return -1;
    }

    public void setHeaderDividersEnabled(boolean isHeaderDividersEnabled) {
        this.isHeaderDividersEnabled = isHeaderDividersEnabled;
        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setHeaderDividersEnabled(isHeaderDividersEnabled);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setFooterDividersEnabled(boolean isFooterDividersEnabled) {
        this.isFooterDividersEnabled = isFooterDividersEnabled;
        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setFooterDividersEnabled(isFooterDividersEnabled);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
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

    private AdapterDataObserver mEmptyAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            processEmptyView(null != mReqAdapter ? mReqAdapter.getItemCount() : 0);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            processEmptyView(null != mReqAdapter ? mReqAdapter.getItemCount() + itemCount : 0);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            processEmptyView(null != mReqAdapter ? mReqAdapter.getItemCount() - itemCount : 0);
        }
    };

}
