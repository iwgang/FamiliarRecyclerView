package cn.iwgang.familiarrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * FamiliarDefaultLoadMoreView
 * Created by iWgang on 17/1/2.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
class FamiliarDefaultLoadMoreView extends FrameLayout implements IFamiliarLoadMore {
    private ProgressBar mPbLoad;
    private TextView mTvLoadText;

    private boolean isLoading = false;

    public FamiliarDefaultLoadMoreView(Context context) {
        this(context, null);
    }

    public FamiliarDefaultLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamiliarDefaultLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.frv_view_def_load_more, this);
        mPbLoad = (ProgressBar) findViewById(R.id.frv_pbLoad);
        mTvLoadText = (TextView) findViewById(R.id.frv_tvLoadText);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void showNormal() {
        isLoading = false;
        mPbLoad.setVisibility(GONE);
        mTvLoadText.setText(getResources().getString(R.string.frv_def_load_more_view_status_normal));
    }

    @Override
    public void showLoading() {
        isLoading = true;
        mPbLoad.setVisibility(VISIBLE);
        mTvLoadText.setText(getResources().getString(R.string.frv_def_load_more_view_status_loading));
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

}