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
    private static final int DEF_DIVIDER_HEIGHT = 30;

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
    private boolean isKeepShowHeadOrFooter = false;
    private boolean isNotShowGridEndDivider = false;
    private int mEmptyViewResId;
    private View mEmptyView;
    private OnItemClickListener mTempOnItemClickListener;
    private OnItemLongClickListener mTempOnItemLongClickListener;
    private OnHeadViewBindViewHolderListener mTempOnHeadViewBindViewHolderListener;
    private OnFooterViewBindViewHolderListener mTempOnFooterViewBindViewHolderListener;
    private int mLayoutManagerType;
    private Drawable mDefAllDivider;
    private int mDefAllDividerHeight;
    private boolean needInitAddItemDescration = false;
    private boolean hasShowEmptyView = false;

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
        mDefAllDivider = ta.getDrawable(R.styleable.FamiliarRecyclerView_frv_divider);
        mDefAllDividerHeight = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_dividerHeight, -1);
        mVerticalDivider = ta.getDrawable(R.styleable.FamiliarRecyclerView_frv_dividerVertical);
        mHorizontalDivider = ta.getDrawable(R.styleable.FamiliarRecyclerView_frv_dividerHorizontal);
        mVerticalDividerHeight = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_dividerVerticalHeight, -1);
        mHorizontalDividerHeight = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_dividerHorizontalHeight, -1);
        mItemViewBothSidesMargin = (int)ta.getDimension(R.styleable.FamiliarRecyclerView_frv_itemViewBothSidesMargin, 0);
        mEmptyViewResId = ta.getResourceId(R.styleable.FamiliarRecyclerView_frv_emptyView, -1);
        isKeepShowHeadOrFooter = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_isEmptyViewKeepShowHeadOrFooter, false);
        isHeaderDividersEnabled = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_headerDividersEnabled, false);
        isFooterDividersEnabled = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_footerDividersEnabled, false);
        isNotShowGridEndDivider = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_isNotShowGridEndDivider, false);
        if (ta.hasValue(R.styleable.FamiliarRecyclerView_frv_layoutManager)) {
            int layoutManagerType = ta.getInt(R.styleable.FamiliarRecyclerView_frv_layoutManager, DEF_LAYOUT_MANAGER_TYPE);
            int layoutManagerOrientation = ta.getInt(R.styleable.FamiliarRecyclerView_frv_layoutManagerOrientation, DEF_LAYOUT_MANAGER_ORIENTATION);
            boolean isReverseLayout = ta.getBoolean(R.styleable.FamiliarRecyclerView_frv_isReverseLayout, false);
            int gridSpanCount = ta.getInt(R.styleable.FamiliarRecyclerView_frv_spanCount, DEF_GRID_SPAN_COUNT);

            switch (layoutManagerType) {
                case LAYOUT_MANAGER_TYPE_LINEAR:
                    setLayoutManager(new LinearLayoutManager(context, layoutManagerOrientation, isReverseLayout));
                    break;
                case LAYOUT_MANAGER_TYPE_GRID:
                    setLayoutManager(new GridLayoutManager(context, gridSpanCount, layoutManagerOrientation, isReverseLayout));
                    break;
                case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                    setLayoutManager(new StaggeredGridLayoutManager(gridSpanCount, layoutManagerOrientation));
                    break;
            }
        }
        ta.recycle();
    }

    private void processDefDivider(boolean isLinearLayoutManager, int layoutManagerOrientation) {
        if (!isDefaultItemDecoration) return ;

        if ((null == mVerticalDivider || null == mHorizontalDivider) && null != mDefAllDivider) {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && null == mHorizontalDivider) {
                    mHorizontalDivider = mDefAllDivider;
                } else if (layoutManagerOrientation == OrientationHelper.HORIZONTAL && null == mVerticalDivider) {
                    mVerticalDivider = mDefAllDivider;
                }
            } else {
                if (null == mVerticalDivider) {
                    mVerticalDivider = mDefAllDivider;
                }

                if (null == mHorizontalDivider) {
                    mHorizontalDivider = mDefAllDivider;
                }
            }
        }

        if (mVerticalDividerHeight > 0 && mHorizontalDividerHeight > 0) return ;

        if (mDefAllDividerHeight > 0) {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && mHorizontalDividerHeight <= 0) {
                    mHorizontalDividerHeight = mDefAllDividerHeight;
                } else if(layoutManagerOrientation == OrientationHelper.HORIZONTAL && mVerticalDividerHeight <= 0) {
                    mVerticalDividerHeight = mDefAllDividerHeight;
                }
            } else {
                if (mVerticalDividerHeight <= 0) {
                    mVerticalDividerHeight = mDefAllDividerHeight;
                }

                if (mHorizontalDividerHeight <= 0) {
                    mHorizontalDividerHeight = mDefAllDividerHeight;
                }
            }
        } else {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && mHorizontalDividerHeight <= 0) {
                    if (null != mHorizontalDivider) {
                        if (mHorizontalDivider.getIntrinsicHeight() > 0) {
                            mHorizontalDividerHeight = mHorizontalDivider.getIntrinsicHeight();
                        } else {
                            mHorizontalDividerHeight = DEF_DIVIDER_HEIGHT;
                        }
                    }
                } else if(layoutManagerOrientation == OrientationHelper.HORIZONTAL && mVerticalDividerHeight <= 0) {
                    if (null != mVerticalDivider) {
                        if (mVerticalDivider.getIntrinsicHeight() > 0) {
                            mVerticalDividerHeight = mVerticalDivider.getIntrinsicHeight();
                        } else {
                            mVerticalDividerHeight = DEF_DIVIDER_HEIGHT;
                        }
                    }
                }
            } else {
                if (mVerticalDividerHeight <= 0 && null != mVerticalDivider) {
                    if (mVerticalDivider.getIntrinsicHeight() > 0) {
                        mVerticalDividerHeight = mVerticalDivider.getIntrinsicHeight();
                    } else {
                        mVerticalDividerHeight = DEF_DIVIDER_HEIGHT;
                    }
                }

                if (mHorizontalDividerHeight <= 0 && null != mHorizontalDivider) {
                    if (mHorizontalDivider.getIntrinsicHeight() > 0) {
                        mHorizontalDividerHeight = mHorizontalDivider.getIntrinsicHeight();
                    } else {
                        mHorizontalDividerHeight = DEF_DIVIDER_HEIGHT;
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

                    if (isKeepShowHeadOrFooter) parentView.removeView(tempEmptyView1);
                } else {
                    ViewParent pParentView = parentView.getParent();
                    if (null != pParentView && pParentView instanceof ViewGroup) {
                        View tempEmptyView2 = ((ViewGroup) pParentView).findViewById(mEmptyViewResId);
                        if (null != tempEmptyView2) {
                            mEmptyView = tempEmptyView2;

                            if (isKeepShowHeadOrFooter) ((ViewGroup) pParentView).removeView(tempEmptyView2);
                        }
                    }
                }
            }
            mEmptyViewResId = -1;
        } else if (isKeepShowHeadOrFooter && null != mEmptyView) {
            ViewParent emptyViewParent = mEmptyView.getParent();
            if (null != emptyViewParent && emptyViewParent instanceof ViewGroup) {
                ((ViewGroup) emptyViewParent).removeView(mEmptyView);
            }
        }

        if (null == adapter) {
            if (null != mReqAdapter) {
                if (!isKeepShowHeadOrFooter) {
                    mReqAdapter.unregisterAdapterDataObserver(mReqAdapterDataObserver);
                }
                mReqAdapter = null;
                mWrapFamiliarRecyclerViewAdapter = null;

                processEmptyView();
            }

            return;
        }

        mReqAdapter = adapter;
        mWrapFamiliarRecyclerViewAdapter = new FamiliarWrapRecyclerViewAdapter(this, adapter, mHeaderView, mFooterView, mLayoutManagerType);

        mWrapFamiliarRecyclerViewAdapter.setOnItemClickListener(mTempOnItemClickListener);
        mWrapFamiliarRecyclerViewAdapter.setOnItemLongClickListener(mTempOnItemLongClickListener);
        mWrapFamiliarRecyclerViewAdapter.setOnHeadViewBindViewHolderListener(mTempOnHeadViewBindViewHolderListener);
        mWrapFamiliarRecyclerViewAdapter.setOnFooterViewBindViewHolderListener(mTempOnFooterViewBindViewHolderListener);

        mReqAdapter.registerAdapterDataObserver(mReqAdapterDataObserver);
        super.setAdapter(mWrapFamiliarRecyclerViewAdapter);

        if (needInitAddItemDescration && null != mFamiliarDefaultItemDecoration) {
            needInitAddItemDescration = false;
            super.addItemDecoration(mFamiliarDefaultItemDecoration);
        }

        processEmptyView();
    }

    public void reRegisterAdapterDataObserver() {
        if (null != mReqAdapter && !mReqAdapter.hasObservers()) {
            mReqAdapter.registerAdapterDataObserver(mReqAdapterDataObserver);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mReqAdapter && mReqAdapter.hasObservers()) {
            mReqAdapter.unregisterAdapterDataObserver(mReqAdapterDataObserver);
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
            processDefDivider(false, mCurGridLayoutManager.getOrientation());
            initDefaultItemDecoration();
        } else if (layout instanceof StaggeredGridLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_STAGGERED_GRID;
            processDefDivider(false, ((StaggeredGridLayoutManager) layout).getOrientation());
            initDefaultItemDecoration();
        } else if (layout instanceof LinearLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_LINEAR;
            processDefDivider(true, ((LinearLayoutManager)layout).getOrientation());
            initDefaultItemDecoration();
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

    private void initDefaultItemDecoration() {
        if (!isDefaultItemDecoration) return ;

        if (null != mFamiliarDefaultItemDecoration) {
            super.removeItemDecoration(mFamiliarDefaultItemDecoration);
            mFamiliarDefaultItemDecoration = null;
        }

        mFamiliarDefaultItemDecoration = new FamiliarDefaultItemDecoration(this, mVerticalDivider, mHorizontalDivider, mVerticalDividerHeight, mHorizontalDividerHeight);
        mFamiliarDefaultItemDecoration.setItemViewBothSidesMargin(mItemViewBothSidesMargin);
        mFamiliarDefaultItemDecoration.setHeaderDividersEnabled(isHeaderDividersEnabled);
        mFamiliarDefaultItemDecoration.setFooterDividersEnabled(isFooterDividersEnabled);
        mFamiliarDefaultItemDecoration.setNotShowGridEndDivider(isNotShowGridEndDivider);

        if (null != getAdapter()) {
            needInitAddItemDescration = false;
            super.addItemDecoration(mFamiliarDefaultItemDecoration);
        } else {
            needInitAddItemDescration = true;
        }
    }

    private void processEmptyView() {
        if (null != mEmptyView) {
            boolean isShowEmptyView = (null != mReqAdapter ? mReqAdapter.getItemCount() : 0) == 0;

            if (isShowEmptyView == hasShowEmptyView) return ;

            if (isKeepShowHeadOrFooter) {
                if (hasShowEmptyView) {
                    mWrapFamiliarRecyclerViewAdapter.notifyItemRemoved(getHeaderViewsCount());
                }
            } else {
                mEmptyView.setVisibility(isShowEmptyView ? VISIBLE : GONE);
                setVisibility(isShowEmptyView ? GONE : VISIBLE);
            }

            this.hasShowEmptyView = isShowEmptyView;
        }
    }

    /**
     * Set EmptyView (before setAdapter)
     * @param emptyView your EmptyView
     */
    public void setEmptyView(View emptyView) {
        setEmptyView(emptyView, false);
    }

    /**
     * Set EmptyView (before setAdapter)
     * @param emptyView your EmptyView
     * @param isKeepShowHeadOrFooter is Keep show HeadView or FooterView
     */
    public void setEmptyView(View emptyView, boolean isKeepShowHeadOrFooter) {
        this.mEmptyView = emptyView;
        this.isKeepShowHeadOrFooter = isKeepShowHeadOrFooter;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyViewKeepShowHeadOrFooter(boolean isKeepShowHeadOrFoot) {
        this.isKeepShowHeadOrFooter = isKeepShowHeadOrFoot;
    }

    public boolean isShowEmptyView() {
        return hasShowEmptyView;
    }

    public boolean isKeepShowHeadOrFooter() {
        return isKeepShowHeadOrFooter;
    }

    public void setDivider(int height, Drawable divider) {
        if (!isDefaultItemDecoration || height <= 0) return ;

        this.mVerticalDividerHeight = height;
        this.mHorizontalDividerHeight = height;

        if (this.mVerticalDivider != divider) {
            this.mVerticalDivider = divider;
        }

        if (this.mHorizontalDivider != divider) {
            this.mHorizontalDivider = divider;
        }

        if (null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            mFamiliarDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDivider(Drawable divider) {
        if (!isDefaultItemDecoration || (mVerticalDividerHeight <= 0 && mHorizontalDividerHeight <= 0)) return ;

        if (this.mVerticalDivider != divider) {
            this.mVerticalDivider = divider;
        }

        if (this.mHorizontalDivider != divider) {
            this.mHorizontalDivider = divider;
        }

        if (null != mFamiliarDefaultItemDecoration) {
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

        if (null != mFamiliarDefaultItemDecoration) {
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

        if (null != mFamiliarDefaultItemDecoration) {
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

        if (null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHeight(int height) {
        if (!isDefaultItemDecoration) return ;

        this.mVerticalDividerHeight = height;
        this.mHorizontalDividerHeight = height;

        if (null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerVerticalHeight(int height) {
        if (!isDefaultItemDecoration) return ;

        this.mVerticalDividerHeight = height;

        if (null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHorizontalHeight(int height) {
        if (!isDefaultItemDecoration) return ;

        this.mHorizontalDividerHeight = height;

        if (null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setItemViewBothSidesMargin(int bothSidesMargin) {
        if (!isDefaultItemDecoration) return ;

        this.mItemViewBothSidesMargin = bothSidesMargin;

        if (null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setItemViewBothSidesMargin(mItemViewBothSidesMargin);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * HeadView onBindViewHolder callback
     * @param onHeadViewBindViewHolderListener OnHeadViewBindViewHolderListener
     */
    public void setOnHeadViewBindViewHolderListener(FamiliarRecyclerView.OnHeadViewBindViewHolderListener onHeadViewBindViewHolderListener) {
        if (null != mWrapFamiliarRecyclerViewAdapter) {
            mWrapFamiliarRecyclerViewAdapter.setOnHeadViewBindViewHolderListener(onHeadViewBindViewHolderListener);
        } else {
            this.mTempOnHeadViewBindViewHolderListener = onHeadViewBindViewHolderListener;
        }
    }

    /**
     * FooterView onBindViewHolder callback
     * @param onFooterViewBindViewHolderListener OnFooterViewBindViewHolderListener
     */
    public void setOnFooterViewBindViewHolderListener(FamiliarRecyclerView.OnFooterViewBindViewHolderListener onFooterViewBindViewHolderListener) {
        if (null != mWrapFamiliarRecyclerViewAdapter) {
            mWrapFamiliarRecyclerViewAdapter.setOnFooterViewBindViewHolderListener(onFooterViewBindViewHolderListener);
        } else {
            this.mTempOnFooterViewBindViewHolderListener = onFooterViewBindViewHolderListener;
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, false);
    }

    public void addHeaderView(View v, boolean isScrollTo) {
        if (mHeaderView.contains(v)) return;

        mHeaderView.add(v);
        if (null != mWrapFamiliarRecyclerViewAdapter) {
            int pos = mHeaderView.size() - 1;
            mWrapFamiliarRecyclerViewAdapter.notifyItemInserted(pos);

            if (isScrollTo) {
                scrollToPosition(pos);
            }
        }
    }

    public boolean removeHeaderView(View v) {
        if (!mHeaderView.contains(v)) return false;

        if (null != mWrapFamiliarRecyclerViewAdapter) {
            mWrapFamiliarRecyclerViewAdapter.notifyItemRemoved(mHeaderView.indexOf(v));
        }
        return mHeaderView.remove(v);
    }

    public void addFooterView(View v) {
        addFooterView(v, false);
    }

    public void addFooterView(View v, boolean isScrollTo) {
        if (mFooterView.contains(v)) return;

        mFooterView.add(v);
        if (null != mWrapFamiliarRecyclerViewAdapter) {
            int pos = (null == mReqAdapter ? 0 : mReqAdapter.getItemCount()) + getHeaderViewsCount() + mFooterView.size() - 1;
            mWrapFamiliarRecyclerViewAdapter.notifyItemInserted(pos);
            if (isScrollTo) {
                scrollToPosition(pos);
            }
        }
    }

    public boolean removeFooterView(View v) {
        if (!mFooterView.contains(v)) return false;

        if (null != mWrapFamiliarRecyclerViewAdapter) {
            int pos = (null == mReqAdapter ? 0 : mReqAdapter.getItemCount()) + getHeaderViewsCount() + mFooterView.indexOf(v);
            mWrapFamiliarRecyclerViewAdapter.notifyItemRemoved(pos);
        }
        return mFooterView.remove(v);
    }

    public int getHeaderViewsCount() {
        return mHeaderView.size();
    }

    public int getFooterViewsCount() {
        return mFooterView.size();
    }

    public int getFirstVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();

        if (null == layoutManager) return 0;

        int ret = -1;

        switch (mLayoutManagerType) {
            case LAYOUT_MANAGER_TYPE_LINEAR:
                ret = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() - getHeaderViewsCount();
                break;
            case LAYOUT_MANAGER_TYPE_GRID:
                ret = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() - getHeaderViewsCount();
                break;
            case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                StaggeredGridLayoutManager tempStaggeredGridLayoutManager = (StaggeredGridLayoutManager)layoutManager;
                int[] firstVisibleItemPositions = new int[tempStaggeredGridLayoutManager.getSpanCount()];
                tempStaggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(firstVisibleItemPositions);
                ret = firstVisibleItemPositions[0] - getHeaderViewsCount();
                break;
        }

        return ret < 0 ? 0 : ret;
    }

    public int getLastVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();
        if (null == layoutManager) return -1;

        int curItemCount = (null != mReqAdapter ? mReqAdapter.getItemCount() - 1 : 0);
        int ret = -1;

        switch (mLayoutManagerType) {
            case LAYOUT_MANAGER_TYPE_LINEAR:
                ret = ((LinearLayoutManager)layoutManager).findLastCompletelyVisibleItemPosition() - getHeaderViewsCount();
                if (ret > curItemCount) {
                    ret -= getFooterViewsCount();
                }
                break;
            case LAYOUT_MANAGER_TYPE_GRID:
                ret = ((GridLayoutManager)layoutManager).findLastCompletelyVisibleItemPosition() - getHeaderViewsCount();
                if (ret > curItemCount) {
                    ret -= getFooterViewsCount();
                }
                break;
            case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                StaggeredGridLayoutManager tempStaggeredGridLayoutManager = (StaggeredGridLayoutManager)layoutManager;
                int[] lastVisibleItemPositions = new int[tempStaggeredGridLayoutManager.getSpanCount()];
                tempStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastVisibleItemPositions);
                if (lastVisibleItemPositions.length > 0) {
                    int maxPos = lastVisibleItemPositions[0];
                    for (int curPos : lastVisibleItemPositions) {
                        if (curPos > maxPos) maxPos = curPos;
                    }
                    ret = maxPos - getHeaderViewsCount();
                    if (ret > curItemCount) {
                        ret -= getFooterViewsCount();
                    }
                }
                break;
        }

        return ret < 0 ? (null != mReqAdapter ? mReqAdapter.getItemCount() - 1 : 0) : ret;
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

    public void setNotShowGridEndDivider(boolean isNotShowGridEndDivider) {
        this.isNotShowGridEndDivider = isNotShowGridEndDivider;
        if (isDefaultItemDecoration && null != mFamiliarDefaultItemDecoration) {
            mFamiliarDefaultItemDecoration.setNotShowGridEndDivider(isNotShowGridEndDivider);

            if (null != mWrapFamiliarRecyclerViewAdapter) {
                mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public int getCurLayoutManagerType() {
        return mLayoutManagerType;
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

    public interface OnHeadViewBindViewHolderListener {
        void onHeadViewBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isInitializeInvoke);
    }

    public interface OnFooterViewBindViewHolderListener {
        void onFooterViewBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isInitializeInvoke);
    }

    private AdapterDataObserver mReqAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapFamiliarRecyclerViewAdapter.notifyDataSetChanged();
            processEmptyView();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapFamiliarRecyclerViewAdapter.notifyItemInserted(getHeaderViewsCount() + positionStart);
            processEmptyView();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapFamiliarRecyclerViewAdapter.notifyItemRemoved(getHeaderViewsCount() + positionStart);
            processEmptyView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapFamiliarRecyclerViewAdapter.notifyItemRangeChanged(getHeaderViewsCount() + positionStart, itemCount);
            processEmptyView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapFamiliarRecyclerViewAdapter.notifyItemMoved(getHeaderViewsCount() + fromPosition, getHeaderViewsCount() + toPosition);
        }
    };

}