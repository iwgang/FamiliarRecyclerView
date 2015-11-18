package cn.iwgang.familiarrecyclerviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class GridLayoutActivity extends ActionBarActivity {
    private FamiliarRecyclerView mRecyclerView;
    private List<String> mDatas;
    private MyAdapter mAdapter;

    private boolean isVertical = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_grid);

        isVertical = getIntent().getBooleanExtra("isVertical", true);

        mDatas = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            mDatas.add("test:" + i);
        }

        mRecyclerView = (FamiliarRecyclerView) findViewById(R.id.mRecyclerView);

        // Item Click and Item Long Click
        mRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(GridLayoutActivity.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(GridLayoutActivity.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // LayoutManager
        if (isVertical) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.HORIZONTAL, false));
        }

        mRecyclerView.setOnScrollListener(new FamiliarRecyclerViewOnScrollListener(mRecyclerView.getLayoutManager()) {
            @Override
            public void onScrolledToTop() {
                Log.i("wg", "onScrolledToTop ...");
            }

            @Override
            public void onScrolledToBottom() {
                Log.i("wg", "onScrolledToBottom ...");
            }
        });

        // ItemAnimator
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        mRecyclerView.setHeaderDividersEnabled(true);
        mRecyclerView.setFooterDividersEnabled(true);

        // head view
        mRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, isVertical, 0xFFFF5000, "Head View 1"));
        mRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, isVertical, Color.BLUE, "Head View 2"));

        // footer view
        mRecyclerView.addFooterView(HeaderAndFooterViewUtil.getFooterView(this, isVertical, 0xFF778899, "Foot View 1"));
        mRecyclerView.addFooterView(HeaderAndFooterViewUtil.getFooterView(this, isVertical, Color.RED, "Foot View 2"));

        mRecyclerView.setEmptyView(findViewById(R.id.tv_empty), true);

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutId = isVertical ? R.layout.item_view_grid : R.layout.item_view_grid_hor;
            return new MyViewHolder(LayoutInflater.from(GridLayoutActivity.this).inflate(layoutId, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mTvTxt.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvTxt = (TextView)itemView.findViewById(R.id.tv_txt);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_add:
//                mDatas.add(0, "new " + mDatas.size() + 1);
//                mAdapter.notifyItemInserted((mRecyclerView.getHeaderViewsCount() + 1) -1);

                mAdapter.notifyItemInserted(mRecyclerView.getHeaderViewsCount() + mDatas.size());
                mDatas.add("new " + mDatas.size() + 1);
                break;
            case R.id.id_action_delete:
//                mDatas.remove(0);
//                mAdapter.notifyItemRemoved((mRecyclerView.getHeaderViewsCount() + 1) -1);

                mAdapter.notifyItemRemoved(mRecyclerView.getHeaderViewsCount() + mDatas.size() - 1);
                mDatas.remove(mDatas.size() - 1);
                break;
        }
        return true;
    }
}
