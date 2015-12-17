package cn.iwgang.familiarrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * FamiliarRecyclerView Default ItemDecoration
 * Created by iWgang on 15/11/08.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarDefaultItemDecoration extends RecyclerView.ItemDecoration {
    private FamiliarRecyclerView mFamiliarRecyclerView;
    private Drawable mVerticalDividerDrawable;
    private Drawable mHorizontalDividerDrawable;
    private int mVerticalDividerDrawableHeight;
    private int mHorizontalDividerDrawableHeight;
    private int mItemViewBothSidesMargin;

    private int mLayoutManagerType = FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR; // FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_*
    private int mOrientation = OrientationHelper.VERTICAL; // OrientationHelper.VERTICAL or OrientationHelper.HORIZONTAL
    private int mGridSpanCount = 0;
    private boolean isHeaderDividersEnabled;
    private boolean isFooterDividersEnabled;
    private float mUnDivisibleValue = 0;
    private boolean isDivisible = true;

    public FamiliarDefaultItemDecoration(FamiliarRecyclerView familiarRecyclerView, Drawable dividerVertical, Drawable dividerHorizontal, int dividerDrawableSizeVertical, int dividerDrawableSizeHorizontal) {
        this.mFamiliarRecyclerView = familiarRecyclerView;
        this.mVerticalDividerDrawable = dividerVertical;
        this.mHorizontalDividerDrawable = dividerHorizontal;
        this.mVerticalDividerDrawableHeight = dividerDrawableSizeVertical;
        this.mHorizontalDividerDrawableHeight = dividerDrawableSizeHorizontal;
        initLayoutManagerType();
    }

    private void initLayoutManagerType() {
        this.mLayoutManagerType = mFamiliarRecyclerView.getCurLayoutManagerType();
        RecyclerView.LayoutManager layoutManager = mFamiliarRecyclerView.getLayoutManager();
        switch (mLayoutManagerType) {
            case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR:
                LinearLayoutManager curLinearLayoutManager = (LinearLayoutManager)layoutManager;
                if (curLinearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    mOrientation = OrientationHelper.HORIZONTAL;
                } else {
                    mOrientation = OrientationHelper.VERTICAL;
                }
                break;
            case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID:
                GridLayoutManager curGridLayoutManager = (GridLayoutManager)layoutManager;
                mGridSpanCount = curGridLayoutManager.getSpanCount();
                if (curGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    mOrientation = OrientationHelper.HORIZONTAL;
                } else {
                    mOrientation = OrientationHelper.VERTICAL;
                }
                break;
            case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                StaggeredGridLayoutManager curStaggeredGridLayoutManager = (StaggeredGridLayoutManager)layoutManager;
                mGridSpanCount = curStaggeredGridLayoutManager.getSpanCount();
                if (curStaggeredGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    mOrientation = OrientationHelper.HORIZONTAL;
                } else {
                    mOrientation = OrientationHelper.VERTICAL;
                }
                break;
            default:
                this.mLayoutManagerType = FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR;
        }

        initDivisible();
    }

    private void initDivisible() {
        int tempDividerDrawableSize = mOrientation == OrientationHelper.VERTICAL ? mHorizontalDividerDrawableHeight : mVerticalDividerDrawableHeight;

        if (mGridSpanCount > 0 && tempDividerDrawableSize % mGridSpanCount != 0) {
            float t1 = (float)tempDividerDrawableSize / mGridSpanCount;
            mUnDivisibleValue = t1 - (int)t1;
            isDivisible = false;
        } else {
            mUnDivisibleValue = 0;
            isDivisible = true;
        }
    }

    public void setVerticalDividerDrawable(Drawable verticalDividerDrawable) {
        this.mVerticalDividerDrawable = verticalDividerDrawable;
    }

    public void setHorizontalDividerDrawable(Drawable horizontalDividerDrawable) {
        this.mHorizontalDividerDrawable = horizontalDividerDrawable;
    }

    public void setVerticalDividerDrawableHeight(int verticalDividerDrawableHeight) {
        this.mVerticalDividerDrawableHeight = verticalDividerDrawableHeight;
        initDivisible();
    }

    public void setHorizontalDividerDrawableHeight(int horizontalDividerDrawableHeight) {
        this.mHorizontalDividerDrawableHeight = horizontalDividerDrawableHeight;
        initDivisible();
    }

    public void setItemViewBothSidesMargin (int itemViewBothSidesMargin) {
        this.mItemViewBothSidesMargin = itemViewBothSidesMargin;
    }

    public void setHeaderDividersEnabled(boolean isHeaderDividersEnabled) {
        this.isHeaderDividersEnabled = isHeaderDividersEnabled;
    }

    public void setFooterDividersEnabled(boolean isFooterDividersEnabled) {
        this.isFooterDividersEnabled = isFooterDividersEnabled;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (null != mVerticalDividerDrawable || null != mHorizontalDividerDrawable) {
            drawDividerDrawable(c, parent);
        }
    }

    /**
     * Draw Divider Drawable
     * @param c           Canvas
     * @param parent      RecyclerView
     */
    private void drawDividerDrawable(Canvas c, RecyclerView parent) {
        int headersCount = 0;
        int footerCount = 0;
        int itemViewCount;

        FamiliarRecyclerView curFamiliarRecyclerView = null;
        if (parent instanceof FamiliarRecyclerView) {
            curFamiliarRecyclerView = (FamiliarRecyclerView) parent;
            headersCount = curFamiliarRecyclerView.getHeaderViewsCount();
            footerCount = curFamiliarRecyclerView.getFooterViewsCount();
            itemViewCount = curFamiliarRecyclerView.getAdapter().getItemCount() - headersCount - footerCount;
        } else {
            itemViewCount = parent.getAdapter().getItemCount();
        }

        final int parentLeft = parent.getPaddingLeft();
        final int parentRight = parent.getWidth() - parent.getPaddingRight();
        final int parentTop = parent.getPaddingTop();
        final int parentBottom = parent.getHeight() - parent.getPaddingBottom();
        boolean isGridItemLayoutLastRow, isGridItemLayoutFirstRow, isGridItemLayoutLastColumn;
        boolean isGridLayoutLastNum = false;

        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams childViewParams = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int position = childViewParams.getViewAdapterPosition();

            // intercept filter
            if (isInterceptFilter(position, headersCount, footerCount, itemViewCount)) continue ;

            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR
                    && (!isHeaderDividersEnabled || headersCount == 0) && position - headersCount == 0) {
                continue ;
            }

            int traX = (int)(ViewCompat.getTranslationX(childView));
            int traY = (int)(ViewCompat.getTranslationY(childView));

            boolean isEmptyView = isEmptyView(curFamiliarRecyclerView, position, headersCount);

            // head or footer or empty
            if (isHeadViewPos(headersCount, position) || isFooterViewPos(headersCount, footerCount, itemViewCount, position) || isEmptyView) {
                if (isEmptyView && (!isHeaderDividersEnabled || headersCount == 0)) {
                    continue ;
                }

                if (mOrientation == OrientationHelper.HORIZONTAL) {
                    final int left = childView.getLeft() - childViewParams.leftMargin - mVerticalDividerDrawableHeight;
                    final int right = left + mVerticalDividerDrawableHeight;
                    mVerticalDividerDrawable.setBounds(left + traX, parentTop + traY, right + traX, parentBottom + traY);
                    mVerticalDividerDrawable.draw(c);
                } else {
                    final int top = childView.getTop() - childViewParams.topMargin - mHorizontalDividerDrawableHeight;
                    final int bottom = top + mHorizontalDividerDrawableHeight;
                    mHorizontalDividerDrawable.setBounds(parentLeft + traX, top + traY, parentRight + traX, bottom + traY);
                    mHorizontalDividerDrawable.draw(c);
                }
                continue ;
            }

            switch (mLayoutManagerType) {
                case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR:
                    // LinearLayoutManager
                    if (mOrientation == OrientationHelper.VERTICAL) {
                        int left = parentLeft;
                        int right = parentRight;
                        if (mItemViewBothSidesMargin > 0 && position - headersCount > 0) {
                            left += mItemViewBothSidesMargin;
                            right -= mItemViewBothSidesMargin;
                        }
                        final int top = childView.getTop() - childViewParams.topMargin - mHorizontalDividerDrawableHeight;
                        final int bottom = top + mHorizontalDividerDrawableHeight;
                        mHorizontalDividerDrawable.setBounds(left + traX, top + traY, right + traX, bottom + traY);
                        mHorizontalDividerDrawable.draw(c);
                    } else {
                        int top = parentTop;
                        int bottom = parentBottom;
                        if (mItemViewBothSidesMargin > 0 && position - headersCount > 0) {
                            top += mItemViewBothSidesMargin;
                            bottom -= mItemViewBothSidesMargin;
                        }

                        final int left = childView.getLeft() - childViewParams.leftMargin - mVerticalDividerDrawableHeight;
                        final int right = left + mVerticalDividerDrawableHeight;
                        mVerticalDividerDrawable.setBounds(left + traX, top + traY, right + traX, bottom + traY);
                        mVerticalDividerDrawable.draw(c);
                    }
                    break;
                case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID:
                case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                    // GridLayoutManager or StaggeredGridLayoutManager
                    isGridItemLayoutLastRow = isGridItemLayoutLastRow(position, itemViewCount, headersCount);
                    isGridItemLayoutLastColumn = isGridItemLayoutLastColumn(position, headersCount, childView);
                    isGridItemLayoutFirstRow = isGridItemLayoutFirstRow(position, headersCount);

                    if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && position == (itemViewCount + headersCount - 1)) {
                        isGridLayoutLastNum = true;
                    }

                    if (mOrientation == OrientationHelper.HORIZONTAL) {
                        // horizontal draw divider
                        if (!isGridLayoutLastNum && !isGridItemLayoutLastColumn) {
                            int horizontalLeft = childView.getLeft() - childViewParams.leftMargin;
                            int horizontalTop = childView.getBottom() + childViewParams.bottomMargin;
                            int horizontalRight = childView.getRight() + childViewParams.rightMargin;
                            int horizontalBottom = horizontalTop + mHorizontalDividerDrawableHeight;
                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID && !isGridItemLayoutLastRow) {
                                horizontalRight += mVerticalDividerDrawableHeight;
                            }
                            mHorizontalDividerDrawable.setBounds(horizontalLeft + traX, horizontalTop + traY, horizontalRight + traX, horizontalBottom + traY);
                            mHorizontalDividerDrawable.draw(c);
                        }

                        if ((!isHeaderDividersEnabled || headersCount == 0) && isGridItemLayoutFirstRow) {
                            continue ;
                        }

                        int verticalTop;
                        int verticalBottom;
                        if (isGridItemLayoutFirstRow) {
                            // Only draw the first line of the first grid
                            if (position - headersCount == 0) {
                                verticalTop = parent.getTop();
                                verticalBottom = parent.getBottom();
                            } else {
                                continue ;
                            }
                        } else if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && isGridItemLayoutLastRow) {
                            // Only draw the last line of the first grid
                            if (isGridItemLayoutFirstColumn(position, headersCount, childView)) {
                                verticalTop = parent.getTop() + mItemViewBothSidesMargin;
                                verticalBottom = parent.getBottom() - mItemViewBothSidesMargin;
                            } else {
                                continue ;
                            }
                        } else {
                            verticalTop = childView.getTop() - childViewParams.topMargin;
                            verticalBottom = childView.getBottom() + childViewParams.bottomMargin;
                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && !isGridItemLayoutLastColumn) {
                                verticalBottom += mHorizontalDividerDrawableHeight;
                            }
                        }

                        int verticalLeft = childView.getLeft() - childViewParams.leftMargin - mVerticalDividerDrawableHeight;
                        int verticalRight = verticalLeft + mVerticalDividerDrawableHeight;

                        mVerticalDividerDrawable.setBounds(verticalLeft + traX, verticalTop + traY, verticalRight + traX, verticalBottom + traY);
                        mVerticalDividerDrawable.draw(c);
                    } else {
                        // draw vertical divider
                        if (!isGridItemLayoutLastColumn
                                && ((mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && !isGridLayoutLastNum)
                                || mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID)) {
                            int verticalLeft = childView.getRight() + childViewParams.rightMargin;
                            int verticalTop = childView.getTop() - childViewParams.topMargin;
                            int verticalRight = verticalLeft + mVerticalDividerDrawableHeight;
                            int verticalBottom = childView.getBottom() + childViewParams.bottomMargin;
                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID && !isGridItemLayoutLastRow) {
                                verticalBottom += mHorizontalDividerDrawableHeight;
                            }
                            mVerticalDividerDrawable.setBounds(verticalLeft + traX, verticalTop + traY, verticalRight + traX, verticalBottom + traY);
                            mVerticalDividerDrawable.draw(c);
                        }

                        if ((!isHeaderDividersEnabled || headersCount == 0) && isGridItemLayoutFirstRow) {
                            continue ;
                        }

                        int horizontalLeft;
                        int horizontalRight;

                        if (isGridItemLayoutFirstRow) {
                            // Only draw the first line of the first grid
                            if (position - headersCount == 0) {
                                horizontalLeft = parent.getLeft();
                                horizontalRight = parent.getRight();
                            } else {
                                continue ;
                            }
                        } else if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && isGridItemLayoutLastRow) {
                            // Only draw the last line of the first grid
                            if (isGridItemLayoutFirstColumn(position, headersCount, childView)) {
                                horizontalLeft = parent.getLeft() + mItemViewBothSidesMargin;
                                horizontalRight = parent.getRight() - mItemViewBothSidesMargin;
                            } else {
                                continue ;
                            }
                        } else {
                            horizontalLeft = childView.getLeft() - childViewParams.leftMargin;
                            horizontalRight = childView.getRight() + childViewParams.rightMargin;
                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && !isGridItemLayoutLastColumn) {
                                horizontalRight += mVerticalDividerDrawableHeight;
                            }
                        }

                        int horizontalTop = childView.getTop() - childViewParams.topMargin - mHorizontalDividerDrawableHeight;
                        int horizontalBottom = horizontalTop + mHorizontalDividerDrawableHeight;
                        mHorizontalDividerDrawable.setBounds(horizontalLeft + traX, horizontalTop + traY, horizontalRight + traX, horizontalBottom + traY);
                        mHorizontalDividerDrawable.draw(c);

                    }
                    break;
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int position = params.getViewAdapterPosition();
        int headersCount;
        int footerCount;
        int itemViewCount;
        FamiliarRecyclerView curFamiliarRecyclerView = null;
        if (parent instanceof FamiliarRecyclerView) {
            curFamiliarRecyclerView = (FamiliarRecyclerView) parent;

            footerCount = curFamiliarRecyclerView.getFooterViewsCount();
            headersCount = curFamiliarRecyclerView.getHeaderViewsCount();
            itemViewCount = curFamiliarRecyclerView.getAdapter().getItemCount() - headersCount - footerCount;
        } else {
            headersCount = 0;
            footerCount = 0;
            itemViewCount = parent.getAdapter().getItemCount();
        }

        // intercept filter
        if (isInterceptFilter(position, headersCount, footerCount, itemViewCount)) return;

        // set headView or footerView
        if (isHeadViewPos(headersCount, position)) {
            // head
            if (mOrientation == OrientationHelper.HORIZONTAL) {
                outRect.set(mVerticalDividerDrawableHeight, 0, 0, 0);
            } else {
                outRect.set(0, mHorizontalDividerDrawableHeight, 0, 0);
            }
            return ;
        } else if (isFooterViewPos(headersCount, footerCount, itemViewCount, position)) {
            // footer
            if (mOrientation == OrientationHelper.HORIZONTAL) {
                outRect.set(mVerticalDividerDrawableHeight, 0, 0, 0);
            } else {
                outRect.set(0, mHorizontalDividerDrawableHeight, 0, 0);
            }
            return ;
        } else if (isEmptyView(curFamiliarRecyclerView, position, headersCount)) {
            // emptyView
            if (isHeaderDividersEnabled && headersCount > 0) {
                if (mOrientation == OrientationHelper.HORIZONTAL) {
                    outRect.set(mVerticalDividerDrawableHeight, 0, 0, 0);
                } else {
                    outRect.set(0, mHorizontalDividerDrawableHeight, 0, 0);
                }
            }
            return ;
        }

        // set itemView
        if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID || mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
            processGridOffsets(outRect, position, headersCount, view);
        } else {
            int topOrLeftSize;
            if ((!isHeaderDividersEnabled || headersCount == 0) && position - headersCount == 0) {
                topOrLeftSize = 0;
            } else {
                topOrLeftSize = mOrientation == OrientationHelper.VERTICAL ? mHorizontalDividerDrawableHeight : mVerticalDividerDrawableHeight;
            }

            if (mOrientation == OrientationHelper.HORIZONTAL) {
                outRect.set(topOrLeftSize, mItemViewBothSidesMargin, 0, mItemViewBothSidesMargin);
            } else {
                outRect.set(mItemViewBothSidesMargin, topOrLeftSize, mItemViewBothSidesMargin, 0);
            }
        }
    }

    private void processGridOffsets(Rect outRect, int position, int headersCount, View view) {
        int curGridNum;
        if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID) {
            curGridNum = (position - headersCount) % mGridSpanCount;
        } else {
            curGridNum = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
        }

        int leftOrTopOffset, rightOrBottomOffset;
        float mDividerLeftBaseOffset, mDividerRightBaseOffset;

        final int tempDividerDrawableSize = mOrientation == OrientationHelper.HORIZONTAL ? mHorizontalDividerDrawableHeight : mVerticalDividerDrawableHeight;
        if (mItemViewBothSidesMargin > 0) {
            // item view left and right margin
            mDividerLeftBaseOffset = (float)tempDividerDrawableSize / mGridSpanCount * curGridNum - mItemViewBothSidesMargin * 2 / mGridSpanCount * curGridNum + mItemViewBothSidesMargin;
            mDividerRightBaseOffset = (float)tempDividerDrawableSize / mGridSpanCount * (mGridSpanCount - (curGridNum + 1)) + mItemViewBothSidesMargin * 2 / mGridSpanCount * (curGridNum + 1) - mItemViewBothSidesMargin;
        } else {
            mDividerLeftBaseOffset = (float)tempDividerDrawableSize / mGridSpanCount * curGridNum;
            mDividerRightBaseOffset = (float)tempDividerDrawableSize / mGridSpanCount * (mGridSpanCount - (curGridNum + 1));
        }

        if (!isDivisible && mUnDivisibleValue > 0) {
            leftOrTopOffset = Math.round(mDividerLeftBaseOffset - mUnDivisibleValue * (curGridNum + 1));
            rightOrBottomOffset = Math.round(mDividerRightBaseOffset + mUnDivisibleValue * (curGridNum + 1));
        } else {
            leftOrTopOffset = (int)mDividerLeftBaseOffset;
            rightOrBottomOffset = (int)mDividerRightBaseOffset;
        }

        int topOrLeftSize;
        if ((!isHeaderDividersEnabled || headersCount == 0) && isGridItemLayoutFirstRow(position, headersCount)) {
            topOrLeftSize = 0;
        } else {
            topOrLeftSize = mOrientation == OrientationHelper.VERTICAL ? mHorizontalDividerDrawableHeight : mVerticalDividerDrawableHeight;
        }

        if (isGridItemLayoutLastColumn(position, headersCount, view)) {
            // last Column
            if (mOrientation == OrientationHelper.HORIZONTAL) {
                outRect.set(topOrLeftSize, leftOrTopOffset, 0, mItemViewBothSidesMargin);
            } else {
                outRect.set(leftOrTopOffset, topOrLeftSize, mItemViewBothSidesMargin, 0);
            }
        } else if (isGridItemLayoutFirstColumn(position, headersCount, view)) {
            // first column
            if (mOrientation == OrientationHelper.HORIZONTAL) {
                outRect.set(topOrLeftSize, mItemViewBothSidesMargin, 0, rightOrBottomOffset);
            } else {
                outRect.set(mItemViewBothSidesMargin, topOrLeftSize, rightOrBottomOffset, 0);
            }
        } else {
            // middle column
            if (mOrientation == OrientationHelper.HORIZONTAL) {
                outRect.set(topOrLeftSize, leftOrTopOffset, 0, rightOrBottomOffset);
            } else {
                outRect.set(leftOrTopOffset, topOrLeftSize, rightOrBottomOffset, 0);
            }
        }
    }

    private boolean isHeadViewPos(int headersCount, int position) {
        return isHeaderDividersEnabled && headersCount > 0 && position < headersCount;
    }

    private boolean isFooterViewPos(int headersCount, int footerCount, int itemViewCount, int position) {
        return isFooterDividersEnabled && footerCount > 0 && position >= itemViewCount + headersCount;
    }

    private boolean isInterceptFilter(int position, int headersCount, int footerCount, int itemViewCount) {
        if (isHeaderDividersEnabled && headersCount > 0 && position == 0) return true;

        if (!isHeaderDividersEnabled && position < headersCount) return true;

        if ((!isFooterDividersEnabled || footerCount == 0) && position >= itemViewCount + headersCount) return true;

        return false;
    }

    private boolean isEmptyView(FamiliarRecyclerView familiarRecyclerView, int position, int headersCount) {
        if (null != familiarRecyclerView && familiarRecyclerView.isKeepShowHeadOrFooter()
                && familiarRecyclerView.isShowEmptyView() && position == headersCount) {
            return true;
        }

        return false;
    }

    private boolean isGridItemLayoutLastRow(int position, int itemViewCount, int headersCount) {
        if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR) return false;

        return Math.ceil((float)itemViewCount / mGridSpanCount) == Math.ceil((float)(position - headersCount + 1) / mGridSpanCount);
    }

    private boolean isGridItemLayoutFirstRow(int position, int headersCount) {
        return position - headersCount < mGridSpanCount;
    }

    private boolean isGridItemLayoutLastColumn(int position, int headersCount, View view) {
        if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID) {
            if ((position + 1 - headersCount) % mGridSpanCount == 0) {
                return true;
            }
        } else if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
            int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            if (spanIndex == mGridSpanCount-1) return true;
        }

        return false;
    }

    private boolean isGridItemLayoutFirstColumn(int position, int headersCount, View view) {
        if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID) {
            return (position + 1 - headersCount) % mGridSpanCount == 1;
        } else if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
            int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            if (spanIndex == 0) return true;
        }

        return false;
    }

}