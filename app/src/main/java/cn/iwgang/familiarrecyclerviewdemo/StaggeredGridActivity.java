package cn.iwgang.familiarrecyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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

public class StaggeredGridActivity extends AppCompatActivity {
    private FamiliarRecyclerView mRecyclerView;
    private List<String> mDatas;
    private List<Integer> mViewHeights;
    private MyAdapter mAdapter;

    private boolean isVertical = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_layout_staggered_grid);

        isVertical = getIntent().getBooleanExtra("isVertical", true);

        if (isVertical) {
            setContentView(R.layout.act_layout_staggered_grid_ver);
        } else {
            setContentView(R.layout.act_layout_staggered_grid_hor);
        }

        mDatas = new ArrayList<>();
        mViewHeights = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            mDatas.add("test:" + i);
            mViewHeights.add((int)(300 + Math.random() * 300));
        }

        mRecyclerView = (FamiliarRecyclerView) findViewById(R.id.mRecyclerView);

        // Item Click and Item Long Click
        mRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(StaggeredGridActivity.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(StaggeredGridActivity.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // set LayoutManager in code
//        if (isVertical) {
//            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        } else {
//            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
//        }

        mRecyclerView.addOnScrollListener(new FamiliarRecyclerViewOnScrollListener(mRecyclerView.getLayoutManager()) {
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
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        mRecyclerView.setHeaderDividersEnabled(false);
//        mRecyclerView.setFooterDividersEnabled(false);

        // head view
        mRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, isVertical, 0xFFFF5000, "Head View 1"));
//        mRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, isVertical, Color.BLUE, "Head View 2"));

        // footer view
        mRecyclerView.addFooterView(HeaderAndFooterViewUtil.getFooterView(this, isVertical, 0xFF778899, "Foot View 1"));
//        mRecyclerView.addFooterView(HeaderAndFooterViewUtil.getFooterView(this, isVertical, Color.RED, "Foot View 2"));

        mRecyclerView.setEmptyView(findViewById(R.id.tv_empty), true);

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(StaggeredGridActivity.this).inflate(isVertical ? R.layout.item_view_staggered_grid_ver : R.layout.item_view_staggered_grid_hor, parent, false));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (isVertical) {
                lp.height = mViewHeights.get(position);
            } else {
                lp.width = mViewHeights.get(position);
            }
            holder.mTvTxt.setText(mDatas.get(position));
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTxt;

        public MyViewHolder(View view) {
            super(view);
            mTvTxt = (TextView) view.findViewById(R.id.tv_txt);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int notifyPos;

        switch (item.getItemId()) {
            case R.id.id_action_add:
                notifyPos = mDatas.size();

                mDatas.add("new " + mDatas.size());
                mViewHeights.add((int)(100 + Math.random() * 300));
                mAdapter.notifyItemInserted(notifyPos);
                break;
            case R.id.id_action_delete:
                if (mDatas.isEmpty()) return true;

                notifyPos = mDatas.size() - 1;
                mDatas.remove(notifyPos);
                mViewHeights.remove(mViewHeights.size() - 1);
                mAdapter.notifyItemRemoved(notifyPos);
                break;
        }
        return true;
    }
}
