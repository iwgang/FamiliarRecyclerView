package cn.iwgang.familiarrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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

    public FamiliarDefaultItemDecoration(RecyclerView recyclerView, Drawable dividerVertical, Drawable dividerHorizontal, int dividerDrawableSizeVertical, int dividerDrawableSizeHorizontal) {
        this.mVerticalDividerDrawable = dividerVertical;
        this.mHorizontalDividerDrawable = dividerHorizontal;
        this.mVerticalDividerDrawableHeight = dividerDrawableSizeVertical;
        this.mHorizontalDividerDrawableHeight = dividerDrawableSizeHorizontal;

        init(recyclerView);
    }

    private void init(RecyclerView mRecyclerView) {
        RecyclerView.LayoutManager mLayoutManager = mRecyclerView.getLayoutManager();
        if (mLayoutManager.getClass().isAssignableFrom(LinearLayoutManager.class)) {
            mLayoutManagerType = FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR;

            LinearLayoutManager curLinearLayoutManager = (LinearLayoutManager)mLayoutManager;
            if (curLinearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = OrientationHelper.HORIZONTAL;
            } else {
                mOrientation = OrientationHelper.VERTICAL;
            }
        } else if (mLayoutManager.getClass().isAssignableFrom(GridLayoutManager.class)) {
            mLayoutManagerType = FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID;

            GridLayoutManager curGridLayoutManager = (GridLayoutManager)mLayoutManager;
            mGridSpanCount = curGridLayoutManager.getSpanCount();
            if (curGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = OrientationHelper.HORIZONTAL;
            } else {
                mOrientation = OrientationHelper.VERTICAL;
            }
        } else if (mLayoutManager.getClass().isAssignableFrom(StaggeredGridLayoutManager.class)) {
            mLayoutManagerType = FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID;

            StaggeredGridLayoutManager curStaggeredGridLayoutManager = (StaggeredGridLayoutManager)mLayoutManager;
            mGridSpanCount = curStaggeredGridLayoutManager.getSpanCount();
            if (curStaggeredGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = OrientationHelper.HORIZONTAL;
            } else {
                mOrientation = OrientationHelper.VERTICAL;
            }
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

        if (parent instanceof FamiliarRecyclerView) {
            FamiliarRecyclerView curFamiliarRecyclerView = (FamiliarRecyclerView) parent;
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
        boolean isGridItemLayoutLastRow, isGridItemLayoutLastColumn;
        boolean isGridLayoutLastNum = false;

        for (int pos = 0; pos < parent.getChildCount(); pos++) {
            View childView = parent.getChildAt(pos);
            RecyclerView.LayoutParams childViewParams = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int position = childViewParams.getViewAdapterPosition();

            // intercept filter
            if (isInterceptFilter(position, headersCount, footerCount, itemViewCount)) continue;

            if (isHeadViewPos(headersCount, position)) {
                // head
                if (mOrientation == OrientationHelper.HORIZONTAL) {
                    final int left = childView.getRight() + childViewParams.rightMargin;
                    final int right = left + mVerticalDividerDrawableHeight;
                    mVerticalDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                    mVerticalDividerDrawable.draw(c);
                } else {
                    final int top = childView.getBottom() + childViewParams.bottomMargin;
                    final int bottom = top + mHorizontalDividerDrawableHeight;
                    mHorizontalDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                    mHorizontalDividerDrawable.draw(c);
                }
                continue;
            } else if (isFooterViewPos(headersCount, footerCount, itemViewCount, position)) {
                // footer
                if (mOrientation == OrientationHelper.HORIZONTAL) {
                    final int left = childView.getLeft() - childViewParams.leftMargin - mVerticalDividerDrawableHeight;
                    final int right = left + mVerticalDividerDrawableHeight;
                    mVerticalDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                    mVerticalDividerDrawable.draw(c);
                } else {
                    int top = childView.getTop() - childViewParams.topMargin - mHorizontalDividerDrawableHeight;
                    int bottom = top + mHorizontalDividerDrawableHeight;
                    mHorizontalDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                    mHorizontalDividerDrawable.draw(c);
                }
                continue;
            }

            switch (mLayoutManagerType) {
                case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR:
                    if (mOrientation == OrientationHelper.VERTICAL) {
                        if (position + 1 != itemViewCount + headersCount) {
                            final int top = childView.getBottom() + childViewParams.bottomMargin;
                            final int bottom = top + mHorizontalDividerDrawableHeight;
                            mHorizontalDividerDrawable.setBounds(parentLeft + mItemViewBothSidesMargin, top, parentRight - mItemViewBothSidesMargin, bottom);
                            mHorizontalDividerDrawable.draw(c);
                        }
                    } else {
                        final int left = childView.getRight() + childViewParams.rightMargin;
                        final int right = left + mVerticalDividerDrawableHeight;
                        mVerticalDividerDrawable.setBounds(left, parentTop + mItemViewBothSidesMargin, right, parentBottom - mItemViewBothSidesMargin);
                        mVerticalDividerDrawable.draw(c);
                    }
                    break;
                case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID:
                case FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                    isGridItemLayoutLastRow = isGridItemLayoutLastRow(position, itemViewCount, headersCount);
                    isGridItemLayoutLastColumn = isGridItemLayoutLastColumn(position, headersCount, childView);

                    if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && position == (itemViewCount + headersCount - 1)) {
                        isGridLayoutLastNum = true;
                    }

                    if (mOrientation == OrientationHelper.HORIZONTAL) {
                        if (!isGridLayoutLastNum && !isGridItemLayoutLastColumn) {
                            int horizontalLeft = childView.getLeft() - childViewParams.leftMargin;
                            int horizontalTop = childView.getBottom() + childViewParams.bottomMargin;
                            int horizontalRight = childView.getRight() + childViewParams.rightMargin;
                            int horizontalBottom = horizontalTop + mHorizontalDividerDrawableHeight;
                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID && !isGridItemLayoutLastRow) {
                                horizontalRight += mVerticalDividerDrawableHeight;
                            }
                            mHorizontalDividerDrawable.setBounds(horizontalLeft, horizontalTop, horizontalRight, horizontalBottom);
                            mHorizontalDividerDrawable.draw(c);
                        }

                        if (!isGridItemLayoutLastRow) {
                            int verticalLeft = childView.getRight() + childViewParams.rightMargin;
                            int verticalTop = childView.getTop() - childViewParams.topMargin;
                            int verticalRight = verticalLeft + mVerticalDividerDrawableHeight;
                            int verticalBottom = childView.getBottom() + childViewParams.bottomMargin;

                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && !isGridItemLayoutLastColumn) {
                                verticalBottom += mHorizontalDividerDrawableHeight;
                            }

                            mVerticalDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                            mVerticalDividerDrawable.draw(c);
                        }
                    } else {
                        if ((mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && !isGridLayoutLastNum && !isGridItemLayoutLastColumn)
                                || (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID && !isGridItemLayoutLastColumn)) {
                            int verticalLeft = childView.getRight() + childViewParams.rightMargin;
                            int verticalTop = childView.getTop() - childViewParams.topMargin;
                            int verticalRight = verticalLeft + mVerticalDividerDrawableHeight;
                            int verticalBottom = childView.getBottom() + childViewParams.bottomMargin;
                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID && !isGridItemLayoutLastRow) {
                                verticalBottom += mHorizontalDividerDrawableHeight;
                            }
                            mVerticalDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                            mVerticalDividerDrawable.draw(c);
                        }

                        if (!isGridItemLayoutLastRow) {
                            int horizontalLeft = childView.getLeft() - childViewParams.leftMargin;
                            int horizontalTop = childView.getBottom() + childViewParams.bottomMargin;
                            int horizontalRight = childView.getRight() + childViewParams.rightMargin;
                            int horizontalBottom = horizontalTop + mHorizontalDividerDrawableHeight;
                            if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID && !isGridItemLayoutLastColumn) {
                                horizontalRight += mVerticalDividerDrawableHeight;
                            }
                            mHorizontalDividerDrawable.setBounds(horizontalLeft, horizontalTop, horizontalRight, horizontalBottom);
                            mHorizontalDividerDrawable.draw(c);
                        }
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
        if (parent instanceof FamiliarRecyclerView) {
            FamiliarRecyclerView curFamiliarRecyclerView = (FamiliarRecyclerView) parent;

            footerCount = curFamiliarRecyclerView.getFooterViewsCount();
            headersCount = curFamiliarRecyclerView.getHeaderViewsCount();
            itemViewCount = curFamiliarRecyclerView.getAdapter().getItemCount() - headersCount - footerCount;
        } else {
            headersCount = 0;
            footerCount = 0;
            itemViewCount = parent.getAdapter().getItemCount();
        }

        // intercept filter
        if (isInterceptFilter(position, headersCount, footerCount, itemViewCount)) return ;

        if (isHeadViewPos(headersCount, position)) {
            // head
            if (mOrientation == OrientationHelper.HORIZONTAL) {
                outRect.set(0, 0, mVerticalDividerDrawableHeight, 0);
            } else {
                outRect.set(0, 0, 0, mHorizontalDividerDrawableHeight);
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
        }

        if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_GRID
                || mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
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

            if (isGridItemLayoutLastRow(position, itemViewCount, headersCount)) {
                // last row
                if (isGridItemLayoutLastColumn(position, headersCount, view)) {
                    if (mOrientation == OrientationHelper.HORIZONTAL) {
                        outRect.set(0, leftOrTopOffset, 0, mItemViewBothSidesMargin);
                    } else {
                        outRect.set(leftOrTopOffset, 0, mItemViewBothSidesMargin, 0);
                    }
                } else {
                    if (mOrientation == OrientationHelper.HORIZONTAL) {
                        outRect.set(0, leftOrTopOffset, 0, rightOrBottomOffset);
                    } else {
                        outRect.set(leftOrTopOffset, 0, rightOrBottomOffset, 0);
                    }
                }
            } else if (isGridItemLayoutLastColumn(position, headersCount, view)) {
                // last column
                if (mOrientation == OrientationHelper.HORIZONTAL) {
                    outRect.set(0, leftOrTopOffset, mVerticalDividerDrawableHeight, mItemViewBothSidesMargin);
                } else {
                    outRect.set(leftOrTopOffset, 0, mItemViewBothSidesMargin, mHorizontalDividerDrawableHeight);
                }
            } else if (isGridItemLayoutFirstColumn(position, headersCount, view)) {
                // first column
                if (mOrientation == OrientationHelper.HORIZONTAL) {
                    outRect.set(0, mItemViewBothSidesMargin, mVerticalDividerDrawableHeight, rightOrBottomOffset);
                } else {
                    outRect.set(mItemViewBothSidesMargin, 0, rightOrBottomOffset, mHorizontalDividerDrawableHeight);
                }
            } else {
                // middle column
                if (mOrientation == OrientationHelper.HORIZONTAL) {
                    outRect.set(0, leftOrTopOffset, mVerticalDividerDrawableHeight, rightOrBottomOffset);
                } else {
                    outRect.set(leftOrTopOffset, 0, rightOrBottomOffset, mHorizontalDividerDrawableHeight);
                }
            }
        } else {
            if (mOrientation == OrientationHelper.HORIZONTAL) {
                if (mItemViewBothSidesMargin > 0) {
                    if (position + 1 == itemViewCount + headersCount) {
                        outRect.set(0, mItemViewBothSidesMargin, 0, mItemViewBothSidesMargin);
                    } else {
                        outRect.set(0, mItemViewBothSidesMargin, mVerticalDividerDrawableHeight, mItemViewBothSidesMargin);
                    }
                } else {
                    outRect.set(0, 0, mVerticalDividerDrawableHeight, 0);
                }
            } else {
                if (mItemViewBothSidesMargin > 0) {
                    if (position + 1 == itemViewCount + headersCount) {
                        outRect.set(mItemViewBothSidesMargin, 0, mItemViewBothSidesMargin, 0);
                    } else {
                        outRect.set(mItemViewBothSidesMargin, 0, mItemViewBothSidesMargin, mHorizontalDividerDrawableHeight);
                    }
                } else {
                    outRect.set(0, 0, 0, mHorizontalDividerDrawableHeight);
                }
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
        if ((!isHeaderDividersEnabled || headersCount == 0) && position < headersCount) return true;

        if ((!isFooterDividersEnabled || footerCount == 0) && position >= itemViewCount + headersCount) return true;

        if (mLayoutManagerType == FamiliarRecyclerView.LAYOUT_MANAGER_TYPE_LINEAR && mItemViewBothSidesMargin <= 0 && position + 1 == itemViewCount + headersCount) return true;

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