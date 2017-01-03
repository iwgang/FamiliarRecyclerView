package cn.iwgang.familiarrecyclerview.baservadapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * FamiliarBaseAdapter
 * Created by iWgang on 17/1/2.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public abstract class FamiliarBaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> mData;

    public FamiliarBaseAdapter() {
        this(null);
    }

    public FamiliarBaseAdapter(List<T> data) {
        mData = data == null ? new ArrayList<T>() : data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<T> getData() {
        return mData;
    }

    public int getDataSize() {
        return mData.size();
    }

    public T getData(int index) {
        return mData.size() > index ? mData.get(index) : null;
    }

    public void add(T d) {
        int startPos = mData.size();
        mData.add(d);
        notifyItemInserted(startPos);
    }

    public void addAll(List<T> data) {
        int curSize = mData.size();
        mData.addAll(data);
        if (curSize == 0) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(curSize, data.size());
        }
    }

    public void remove(T d) {
        if (mData.contains(d)) {
            int posIndex = mData.indexOf(d);
            mData.remove(d);
            notifyItemRemoved(posIndex);
        }
    }

    public void remove(int index) {
        if (mData.size() > index) {
            mData.remove(index);
            notifyItemRemoved(index);
        }
    }

    public boolean contains(T d) {
        return mData.contains(d);
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

}
