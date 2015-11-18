package cn.iwgang.familiarrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

/**
 * FamiliarRecyclerView Default ItemDecoration
 * Created by iWgang on 15/11/08.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarDefaultItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDividerDrawable;
    private int mDividerDrawableSize;
    private int mItemViewBothSidesMargin;

    private int mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR; // FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_*
    private int mOrientation = LinearLayout.VERTICAL; // LinearLayout.VERTICAL or LinearLayout.HORIZONTAL
    private int mGridSpanCount = 0;
    private boolean isHeaderDividersEnabled;
    private boolean isFooterDividersEnabled;

    private boolean isPl = false;

    private float mUnDivisibleValue = 0;
    private boolean isDivisible = true;

    public FamiliarDefaultItemDecoration(RecyclerView mRecyclerView, Drawable mDividerDrawable, int mDividerDrawableHeight) {
        this.mDividerDrawable = mDividerDrawable;
        this.mDividerDrawableSize = mDividerDrawableHeight;

        RecyclerView.LayoutManager mLayoutManager = mRecyclerView.getLayoutManager();
        if (mLayoutManager.getClass().isAssignableFrom(LinearLayoutManager.class)) {
            mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR;

            LinearLayoutManager curLinearLayoutManager = (LinearLayoutManager)mLayoutManager;
            if (curLinearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = LinearLayout.HORIZONTAL;
            } else {
                mOrientation = LinearLayout.VERTICAL;
            }
        } else if (mLayoutManager.getClass().isAssignableFrom(GridLayoutManager.class)) {
            mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID;

            GridLayoutManager curGridLayoutManager = (GridLayoutManager)mLayoutManager;
            mGridSpanCount = curGridLayoutManager.getSpanCount();
            if (curGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = LinearLayout.HORIZONTAL;
            } else {
                mOrientation = LinearLayout.VERTICAL;
            }
        } else if (mLayoutManager.getClass().isAssignableFrom(StaggeredGridLayoutManager.class)) {
            mLayoutManagerType = FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_STAGGERED_GRID;

            StaggeredGridLayoutManager curStaggeredGridLayoutManager = (StaggeredGridLayoutManager)mLayoutManager;
            mGridSpanCount = curStaggeredGridLayoutManager.getSpanCount();
            if (curStaggeredGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = LinearLayout.HORIZONTAL;
            } else {
                mOrientation = LinearLayout.VERTICAL;
            }
        }

        initDivisible();
    }

    private void initDivisible() {
        if (mGridSpanCount > 0 && mDividerDrawableSize % mGridSpanCount != 0) {
            float t1 = (float)mDividerDrawableSize / mGridSpanCount;
            mUnDivisibleValue = t1 - (int)t1;
            isDivisible = false;
        } else {
            mUnDivisibleValue = 0;
            isDivisible = true;
        }
    }

    public void setItemViewBothSidesMargin (int itemViewBothSidesMargin) {
        this.mItemViewBothSidesMargin = itemViewBothSidesMargin;
    }

    public void setDividerDrawableSize(int dividerDrawableSize) {
        this.mDividerDrawableSize = dividerDrawableSize;

        initDivisible();
    }

    public void setDividerDrawable(Drawable dividerDrawable) {
        this.mDividerDrawable = dividerDrawable;
    }

    public void setHeaderDividersEnabled(boolean isHeaderDividersEnabled) {
        this.isHeaderDividersEnabled = isHeaderDividersEnabled;
    }

    public void setFooterDividersEnabled(boolean isFooterDividersEnabled) {
        this.isFooterDividersEnabled = isFooterDividersEnabled;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (null != mDividerDrawable) {
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
                if (mOrientation == LinearLayout.HORIZONTAL) {
                    final int left = childView.getRight() + childViewParams.rightMargin;
                    final int right = left + mDividerDrawableSize;
                    mDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                    mDividerDrawable.draw(c);
                } else {
                    final int top = childView.getBottom() + childViewParams.bottomMargin;
                    final int bottom = top + mDividerDrawableSize;
                    mDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                    mDividerDrawable.draw(c);
                }
                continue;
            } else if (isFooterViewPos(headersCount, footerCount, itemViewCount, position)) {
                // footer
                if (mOrientation == LinearLayout.HORIZONTAL) {
                    final int left = childView.getLeft() - childViewParams.leftMargin - mDividerDrawableSize;
                    final int right = left + mDividerDrawableSize;
                    mDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                    mDividerDrawable.draw(c);
                } else {
                    int top = childView.getTop() - childViewParams.topMargin - mDividerDrawableSize;
                    int bottom = top + mDividerDrawableSize;
                    mDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                    mDividerDrawable.draw(c);
                }
                continue;
            }

            switch (mLayoutManagerType) {
                case FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR:
                    if (mOrientation == LinearLayout.VERTICAL) {
                        if (position + 1 != itemViewCount + headersCount) {
                            final int top = childView.getBottom() + childViewParams.bottomMargin;
                            final int bottom = top + mDividerDrawableSize;
                            mDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                            mDividerDrawable.draw(c);
                        }
                    } else {
                        final int left = childView.getRight() + childViewParams.rightMargin;
                        final int right = left + mDividerDrawableSize;
                        mDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                        mDividerDrawable.draw(c);
                    }
                    break;
                case FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID:
                case FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                    isGridItemLayoutLastRow = isGridItemLayoutLastRow(position, itemViewCount, headersCount);
                    isGridItemLayoutLastColumn = isGridItemLayoutLastColumn(position, headersCount, childView);

                    if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID && position == (itemViewCount + headersCount - 1)) {
                        isGridLayoutLastNum = true;
                    }

                    if (mOrientation == LinearLayout.HORIZONTAL) {
                        if (!isGridLayoutLastNum && !isGridItemLayoutLastColumn) {
                            int horizontalLeft = childView.getLeft() - childViewParams.leftMargin;
                            int horizontalTop = childView.getBottom() + childViewParams.bottomMargin;
                            int horizontalRight = childView.getRight() + childViewParams.rightMargin;
                            if (!isGridItemLayoutLastRow) {
                                horizontalRight += mDividerDrawableSize;
                            }
                            int horizontalBottom = horizontalTop + mDividerDrawableSize;
                            mDividerDrawable.setBounds(horizontalLeft, horizontalTop, horizontalRight, horizontalBottom);
                            mDividerDrawable.draw(c);
                        }

                        if (!isGridItemLayoutLastRow) {
                            int verticalLeft = childView.getRight() + childViewParams.rightMargin;
                            int verticalTop = childView.getTop() - childViewParams.topMargin;
                            int verticalRight = verticalLeft + mDividerDrawableSize;
                            int verticalBottom = childView.getBottom() + childViewParams.bottomMargin;
                            mDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                            mDividerDrawable.draw(c);
                        }
                    } else {
                        if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID) {
                            if (!isGridLayoutLastNum && !isGridItemLayoutLastColumn) {
                                int verticalLeft = childView.getRight() + childViewParams.rightMargin;
                                int verticalTop = childView.getTop() - childViewParams.topMargin;
                                int verticalRight = verticalLeft + mDividerDrawableSize;
                                int verticalBottom = childView.getBottom() + childViewParams.bottomMargin;
                                mDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                                mDividerDrawable.draw(c);
                            }
                        } else {
                            if (!isGridItemLayoutLastColumn) {
                                int verticalLeft = childView.getRight() + childViewParams.rightMargin;
                                int verticalTop = childView.getTop() - childViewParams.topMargin;
                                int verticalRight = verticalLeft + mDividerDrawableSize;
                                int verticalBottom = childView.getBottom() + childViewParams.bottomMargin;
                                mDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                                mDividerDrawable.draw(c);
                            }
                        }

                        if (!isGridItemLayoutLastRow) {
                            int horizontalLeft = childView.getLeft() - childViewParams.leftMargin;
                            int horizontalTop = childView.getBottom() + childViewParams.bottomMargin;
                            int horizontalRight = childView.getRight() + childViewParams.rightMargin;
                            int horizontalBottom = horizontalTop + mDividerDrawableSize;
                            if (!isGridItemLayoutLastColumn) {
                                horizontalRight += mDividerDrawableSize;
                            }
                            mDividerDrawable.setBounds(horizontalLeft, horizontalTop, horizontalRight, horizontalBottom);
                            mDividerDrawable.draw(c);
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
            if (mOrientation == LinearLayout.HORIZONTAL) {
                outRect.set(0, 0, mDividerDrawableSize, 0);
            } else {
                outRect.set(0, 0, 0, mDividerDrawableSize);
            }
            return ;
        } else if (isFooterViewPos(headersCount, footerCount, itemViewCount, position)) {
            // footer
            if (mOrientation == LinearLayout.HORIZONTAL) {
                outRect.set(mDividerDrawableSize, 0, 0, 0);
            } else {
                outRect.set(0, mDividerDrawableSize, 0, 0);
            }
            return ;
        }

        if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID
                || mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
            int curGridNum;
            if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID) {
                curGridNum = (position - headersCount) % mGridSpanCount;
            } else {
                curGridNum = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            }

            int leftOrTopOffset, rightOrBottomOffset;
            float mDividerLeftBaseOffset, mDividerRightBaseOffset;

            if (mItemViewBothSidesMargin > 0) {
                // item view left and right margin
                mDividerLeftBaseOffset = (float)mDividerDrawableSize / mGridSpanCount * curGridNum - mItemViewBothSidesMargin * 2 / mGridSpanCount * curGridNum + mItemViewBothSidesMargin;
                mDividerRightBaseOffset = (float)mDividerDrawableSize / mGridSpanCount * (mGridSpanCount - (curGridNum + 1)) + mItemViewBothSidesMargin * 2 / mGridSpanCount * (curGridNum + 1) - mItemViewBothSidesMargin;
            } else {
                mDividerLeftBaseOffset = (float)mDividerDrawableSize / mGridSpanCount * curGridNum;
                mDividerRightBaseOffset = (float)mDividerDrawableSize / mGridSpanCount * (mGridSpanCount - (curGridNum + 1));
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
                    if (mOrientation == LinearLayout.HORIZONTAL) {
                        outRect.set(0, leftOrTopOffset, 0, mItemViewBothSidesMargin);
                    } else {
                        outRect.set(leftOrTopOffset, 0, mItemViewBothSidesMargin, 0);
                    }
                } else {
                    if (mOrientation == LinearLayout.HORIZONTAL) {
                        outRect.set(0, leftOrTopOffset, 0, rightOrBottomOffset);
                    } else {
                        outRect.set(leftOrTopOffset, 0, rightOrBottomOffset, 0);
                    }
                }
            } else if (isGridItemLayoutLastColumn(position, headersCount, view)) {
                // last column
                if (mOrientation == LinearLayout.HORIZONTAL) {
                    outRect.set(0, leftOrTopOffset, mDividerDrawableSize, mItemViewBothSidesMargin);
                } else {
                    outRect.set(leftOrTopOffset, 0, mItemViewBothSidesMargin, mDividerDrawableSize);
                }
            } else if (isGridItemLayoutFirstColumn(position, headersCount, view)) {
                // first column
                if (mOrientation == LinearLayout.HORIZONTAL) {
                    outRect.set(0, mItemViewBothSidesMargin, mDividerDrawableSize, rightOrBottomOffset);
                } else {
                    outRect.set(mItemViewBothSidesMargin, 0, rightOrBottomOffset, mDividerDrawableSize);
                }
            } else {
                // middle column
                if (mOrientation == LinearLayout.HORIZONTAL) {
                    outRect.set(0, leftOrTopOffset, mDividerDrawableSize, rightOrBottomOffset);
                } else {
                    outRect.set(leftOrTopOffset, 0, rightOrBottomOffset, mDividerDrawableSize);
                }
            }
        } else {
            if (mOrientation == LinearLayout.HORIZONTAL) {
                if (mItemViewBothSidesMargin > 0) {
                    if (position + 1 == itemViewCount + headersCount) {
                        outRect.set(0, mItemViewBothSidesMargin, 0, mItemViewBothSidesMargin);
                    } else {
                        outRect.set(0, mItemViewBothSidesMargin, mDividerDrawableSize, mItemViewBothSidesMargin);
                    }
                } else {
                    outRect.set(0, 0, mDividerDrawableSize, 0);
                }
            } else {
                if (mItemViewBothSidesMargin > 0) {
                    if (position + 1 == itemViewCount + headersCount) {
                        outRect.set(mItemViewBothSidesMargin, 0, mItemViewBothSidesMargin, 0);
                    } else {
                        outRect.set(mItemViewBothSidesMargin, 0, mItemViewBothSidesMargin, mDividerDrawableSize);
                    }
                } else {
                    outRect.set(0, 0, 0, mDividerDrawableSize);
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

        if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR && mItemViewBothSidesMargin <= 0 && position + 1 == itemViewCount + headersCount) return true;

        return false;
    }

    private boolean isGridItemLayoutLastRow(int position, int itemViewCount, int headersCount) {
        if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_LINEAR) return false;

        return Math.ceil((float)itemViewCount / mGridSpanCount) == Math.ceil((float)(position - headersCount + 1) / mGridSpanCount);
    }

    private boolean isGridItemLayoutFirstRow(int position, int headersCount) {
        return position - headersCount < mGridSpanCount;
    }

    private boolean isGridItemLayoutLastColumn(int position, int headersCount, View view) {
        if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID) {
            if ((position + 1 - headersCount) % mGridSpanCount == 0) {
                return true;
            }
        } else if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
            int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            if (spanIndex == mGridSpanCount-1) return true;
        }

        return false;
    }

    private boolean isGridItemLayoutFirstColumn(int position, int headersCount, View view) {
        if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_GRID) {
            return (position + 1 - headersCount) % mGridSpanCount == 1;
        } else if (mLayoutManagerType == FamiliarWrapRecyclerViewAdapter.LAYOUT_MANAGER_TYPE_STAGGERED_GRID) {
            int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            if (spanIndex == 0) return true;
        }

        return false;
    }

}