package cn.iwgang.familiarrecyclerview.baservadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * FamiliarEasyAdapter
 * Created by iWgang on 17/1/2.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public abstract class FamiliarEasyAdapter<T> extends FamiliarBaseAdapter<T, FamiliarEasyAdapter.ViewHolder> {
    private Context mContext;
    private int mItemLayoutId;

    public FamiliarEasyAdapter(Context context, int itemLayoutId) {
        this(context, itemLayoutId, null);
    }

    public FamiliarEasyAdapter(Context context, int itemLayoutId, List<T> data) {
        super(data);
        mContext = context;
        mItemLayoutId = itemLayoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(mItemLayoutId, parent, false));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;
        private View mItemView;

        ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mViews = new SparseArray<>();
        }

        public <T extends View> T findView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mItemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

    }
}
