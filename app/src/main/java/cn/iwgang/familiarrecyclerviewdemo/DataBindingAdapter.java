package cn.iwgang.familiarrecyclerviewdemo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cn.iwgang.familiarrecyclerview.baservadapter.FamiliarBaseAdapter;

/**
 * DataBindingAdapter
 */
public abstract class DataBindingAdapter<T, DB extends ViewDataBinding> extends FamiliarBaseAdapter<T, DataBindingAdapter.DataBindingViewHolder<DB>> {
    private Context mContext;
    private int mItemLayoutId;

    public DataBindingAdapter(Context context, int itemLayoutId) {
        mContext = context;
        mItemLayoutId = itemLayoutId;
    }

    public abstract void setVariable(DB binding, T data);

    @Override
    public void onBindViewHolder(DataBindingViewHolder<DB> holder, int position) {
        setVariable(holder.getBinding(), getData(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public DataBindingViewHolder<DB> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(mContext), mItemLayoutId, parent, false));
    }

    static class DataBindingViewHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private DB binding;

        DataBindingViewHolder(DB binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public DB getBinding() {
            return this.binding;
        }
    }
}