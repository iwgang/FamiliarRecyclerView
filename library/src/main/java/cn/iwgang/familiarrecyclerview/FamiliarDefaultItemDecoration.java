package cn.iwgang.familiarrecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * FamiliarRecyclerView Default ItemDecoration
 * Created by iWgang on 15/11/08.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public class FamiliarDefaultItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDividerDrawable;
    private int mDividerDrawableSize;

    private int mLayoutManagerType = 1; // 1 LinearLayoutManager, 2 GridLayoutManager, 3 StaggeredGridLayoutManager
    private int mOrientation = 1; // 1 vertical，2 horizontal
    private int mGridSpanCount = 0;
    private boolean isHeaderDividersEnabled;
    private boolean isFooterDividersEnabled;

    public FamiliarDefaultItemDecoration(RecyclerView mRecyclerView, Drawable mDividerDrawable, int mDividerDrawableHeight) {
        this.mDividerDrawable = mDividerDrawable;
        this.mDividerDrawableSize = mDividerDrawableHeight;

        RecyclerView.LayoutManager mLayoutManager = mRecyclerView.getLayoutManager();
        if (mLayoutManager.getClass().isAssignableFrom(LinearLayoutManager.class)) {
            mLayoutManagerType = 1;

            LinearLayoutManager curLinearLayoutManager = (LinearLayoutManager)mLayoutManager;
            if (curLinearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = 2;
            } else {
                mOrientation = 1;
            }
        } else if (mLayoutManager.getClass().isAssignableFrom(GridLayoutManager.class)) {
            mLayoutManagerType = 2;

            GridLayoutManager curGridLayoutManager = (GridLayoutManager)mLayoutManager;
            mGridSpanCount = curGridLayoutManager.getSpanCount();
            if (curGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = 2;
            } else {
                mOrientation = 1;
            }
        } else if (mLayoutManager.getClass().isAssignableFrom(StaggeredGridLayoutManager.class)) {
            mLayoutManagerType = 3;

            StaggeredGridLayoutManager curStaggeredGridLayoutManager = (StaggeredGridLayoutManager)mLayoutManager;
            mGridSpanCount = curStaggeredGridLayoutManager.getSpanCount();
            if (curStaggeredGridLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mOrientation = 2;
            } else {
                mOrientation = 1;
            }
        }
    }

    public void setDividerDrawableSize(int mDividerDrawableSize) {
        this.mDividerDrawableSize = mDividerDrawableSize;
    }

    public void setDividerDrawable(Drawable mDividerDrawable) {
        this.mDividerDrawable = mDividerDrawable;
    }

    public void setHeaderDividersEnabled(boolean isHeaderDividersEnabled) {
        this.isHeaderDividersEnabled = isHeaderDividersEnabled;
    }

    public void setFooterDividersEnabled(boolean isFooterDividersEnabled) {
        this.isFooterDividersEnabled = isFooterDividersEnabled;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        switch (mLayoutManagerType) {
            case 1:
                // LinearLayoutManager
                if (mOrientation == 2) {
                    drawDividerDrawable(c, parent, 2);
                } else {
                    drawDividerDrawable(c, parent, 1);
                }
                break;
            case 2:
                // GridLayoutManager
                drawDividerDrawable(c, parent, 3);
                break;
            case 3:
                // StaggeredGridLayoutManager
                drawDividerDrawable(c, parent, 3);
                break;
        }
    }

    /**
     * Draw Divider Drawable
     * @param c           Canvas
     * @param parent      RecyclerView
     * @param orientation 1 vertical，2 horizontal 3 all
     */
    private void drawDividerDrawable(Canvas c, RecyclerView parent, int orientation) {
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
        boolean isGridItemLayoutLastRow = false;
        boolean isGridItemLayoutLastColumn = false;
        boolean isGridLayoutLastNum = false;

        for (int pos = 0; pos < parent.getChildCount(); pos ++) {
            View childView = parent.getChildAt(pos);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int position = params.getViewAdapterPosition();

            // intercept filter
            if (isInterceptFilter(position, headersCount, footerCount, itemViewCount)) continue ;

            if (isHeadViewPos(headersCount, position)) {
                // head
                if (mOrientation == 2) {
                    final int left = childView.getRight() + params.rightMargin;
                    final int right = left + mDividerDrawableSize;
                    mDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                    mDividerDrawable.draw(c);
                } else {
                    final int top = childView.getBottom() + params.bottomMargin;
                    final int bottom = top + mDividerDrawableSize;
                    mDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                    mDividerDrawable.draw(c);
                }
                continue ;
            } else if (isFooterViewPos(headersCount, footerCount, itemViewCount, position)) {
                // footer
                if (mOrientation == 2) {
                    final int left = childView.getLeft() - params.leftMargin - mDividerDrawableSize;
                    final int right = left + mDividerDrawableSize;
                    mDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                    mDividerDrawable.draw(c);
                } else {
                    int top = childView.getTop() - params.topMargin - mDividerDrawableSize;
                    int bottom = top + mDividerDrawableSize;
                    mDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                    mDividerDrawable.draw(c);
                }
                continue ;
            }

            switch (orientation) {
                case 1: {
                    // vertical
                    Log.i("wg", "aaaaaa 1 " + isGridItemLayoutLastRow(position, itemViewCount, headersCount));
                    if (!isGridItemLayoutLastRow(position, itemViewCount, headersCount)) {
                        Log.i("wg", "aaaaaa 2");
                        final int top = childView.getBottom() + params.bottomMargin;
                        final int bottom = top + mDividerDrawableSize;
                        mDividerDrawable.setBounds(parentLeft, top, parentRight, bottom);
                        mDividerDrawable.draw(c);
                    }
                    break;
                }
                case 2: {
                    // horizontal
                    if (!isGridItemLayoutLastRow(position, itemViewCount, headersCount)) {
                        final int left = childView.getRight() + params.rightMargin;
                        final int right = left + mDividerDrawableSize;
                        mDividerDrawable.setBounds(left, parentTop, right, parentBottom);
                        mDividerDrawable.draw(c);
                    }
                    break;
                }
                case 3: {
                    // all
                    if ((mLayoutManagerType == 2 || mLayoutManagerType == 3)) {
                        isGridItemLayoutLastRow = isGridItemLayoutLastRow(position, itemViewCount, headersCount);
                        isGridItemLayoutLastColumn = isGridItemLayoutLastColumn(position, headersCount, childView);
                    }

                    if (mLayoutManagerType == 2 && position == (itemViewCount + headersCount - 1)) {
                        isGridLayoutLastNum = true;
                    }

                    if (mOrientation == 2) {
                        if (!isGridLayoutLastNum && !isGridItemLayoutLastColumn) {
                            int horizontalLeft = childView.getLeft() - params.leftMargin;
                            int horizontalTop = childView.getBottom() + params.bottomMargin;
                            int horizontalRight = childView.getRight() + params.rightMargin;
                            if (!isGridItemLayoutLastRow) {
                                horizontalRight += mDividerDrawableSize;
                            }
                            int horizontalBottom = horizontalTop + mDividerDrawableSize;
                            mDividerDrawable.setBounds(horizontalLeft, horizontalTop, horizontalRight, horizontalBottom);
                            mDividerDrawable.draw(c);
                        }

                        if (!isGridItemLayoutLastRow) {
                            int verticalLeft = childView.getRight() + params.rightMargin;
                            int verticalTop = childView.getTop() - params.topMargin;
                            int verticalRight = verticalLeft + mDividerDrawableSize;
                            int verticalBottom = childView.getBottom() + params.bottomMargin;
                            mDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                            mDividerDrawable.draw(c);
                        }
                    } else {
                        if (mLayoutManagerType == 2) {
                            if (!isGridLayoutLastNum && !isGridItemLayoutLastColumn) {
                                int verticalLeft = childView.getRight() + params.rightMargin;
                                int verticalTop = childView.getTop() - params.topMargin;
                                int verticalRight = verticalLeft + mDividerDrawableSize;
                                int verticalBottom = childView.getBottom() + params.bottomMargin;
                                mDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                                mDividerDrawable.draw(c);
                            }
                        } else {
                            if (!isGridItemLayoutLastColumn) {
                                int verticalLeft = childView.getRight() + params.rightMargin;
                                int verticalTop = childView.getTop() - params.topMargin;
                                int verticalRight = verticalLeft + mDividerDrawableSize;
                                int verticalBottom = childView.getBottom() + params.bottomMargin;
                                mDividerDrawable.setBounds(verticalLeft, verticalTop, verticalRight, verticalBottom);
                                mDividerDrawable.draw(c);
                            }
                        }

                        if (!isGridItemLayoutLastRow) {
                            int horizontalLeft = childView.getLeft() - params.leftMargin;
                            int horizontalTop = childView.getBottom() + params.bottomMargin;
                            int horizontalRight = childView.getRight() + params.rightMargin + mDividerDrawableSize;
                            int horizontalBottom = horizontalTop + mDividerDrawableSize;
                            mDividerDrawable.setBounds(horizontalLeft, horizontalTop, horizontalRight, horizontalBottom);
                            mDividerDrawable.draw(c);
                        }
                    }
                    break;
                }
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
            if (mOrientation == 2) {
                outRect.set(0, 0, mDividerDrawableSize, 0);
            } else {
                outRect.set(0, 0, 0, mDividerDrawableSize);
            }
            return ;
        } else if (isFooterViewPos(headersCount, footerCount, itemViewCount, position)) {
            // footer
            if (mOrientation == 2) {
                outRect.set(mDividerDrawableSize, 0, 0, 0);
            } else {
                outRect.set(0, mDividerDrawableSize, 0, 0);
            }
            return ;
        }

        if (mLayoutManagerType == 2 || mLayoutManagerType == 3) {
            if (isGridItemLayoutLastRow(position, itemViewCount, headersCount)) {
                if (mLayoutManagerType == 2) {
                    // GridLayoutManager

                    // (好吧，太绕英文不懂描述，还是中文吧)
                    // 最后一个item，并且不是在最右边，则添加偏移量，避免item的宽/高等于上面一个item的宽+mDividerDrawableSize
                    boolean isLastItem = position + 1 == itemViewCount + headersCount;
                    if (isLastItem && position + 1 - headersCount == (int) Math.ceil((float) itemViewCount / mGridSpanCount) * mGridSpanCount) {
                        return ;
                    }

                    if (mOrientation == 2) {
                        outRect.set(0, 0, 0, mDividerDrawableSize);
                    } else {
                        outRect.set(0, 0, mDividerDrawableSize, 0);
                    }
                } else {
                    // StaggeredGridLayoutManager
                    if (!isGridItemLayoutLastColumn(position, headersCount, view)) {
                        if (mOrientation == 2) {
                            outRect.set(0, 0, 0, mDividerDrawableSize);
                        } else {
                            outRect.set(0, 0, mDividerDrawableSize, 0);
                        }
                    }
                }
            } else if (isGridItemLayoutLastColumn(position, headersCount, view)) {
                if (mOrientation == 2) {
                    outRect.set(0, 0, mDividerDrawableSize, 0);
                } else {
                    outRect.set(0, 0, 0, mDividerDrawableSize);
                }
            } else {
                outRect.set(0, 0, mDividerDrawableSize, mDividerDrawableSize);
            }
        } else {
            if (mOrientation == 2) {
                outRect.set(0, 0, mDividerDrawableSize, 0);
            } else {
                outRect.set(0, 0, 0, mDividerDrawableSize);
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

        if (mLayoutManagerType == 1  && position + 1 == itemViewCount + headersCount) return true;

        return false;
    }

    private boolean isGridItemLayoutLastRow(int position, int itemViewCount, int headersCount) {
        if (mLayoutManagerType == 1) return false;

        return Math.ceil((float)itemViewCount / mGridSpanCount) == Math.ceil((float)(position - headersCount + 1) / mGridSpanCount);
    }

    private boolean isGridItemLayoutLastColumn(int position, int headersCount, View view) {
        if (mLayoutManagerType == 2) {
            if ((position + 1 - headersCount) % mGridSpanCount == 0) {
                return true;
            }
        } else if (mLayoutManagerType == 3) {
            int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
            if (spanIndex == mGridSpanCount-1) return true;
        }

        return false;
    }

}