package cn.iwgang.familiarrecyclerviewdemo;

import android.graphics.Color;
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
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;

public class ImitateStaggeredGridViewDemoActivity extends AppCompatActivity {
    private FamiliarRefreshRecyclerView mCvRefreshStaggeredGridRecyclerView;
    private FamiliarRecyclerView mFamiliarRecyclerView;

    private List<String> mDatas;
    private List<Integer> mViewHeights;
    private MyAdapter mAdapter;
    private boolean isVertical = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_imitate_staggeredview);

        mDatas = new ArrayList<>();
        mViewHeights = new ArrayList<>();
        mDatas.addAll(getDatas());

        mCvRefreshStaggeredGridRecyclerView = (FamiliarRefreshRecyclerView)findViewById(R.id.cv_refreshStaggeredGridRecyclerView);
        mCvRefreshStaggeredGridRecyclerView.setLoadMoreView(new LoadMoreView(this));
        mCvRefreshStaggeredGridRecyclerView.setColorSchemeColors(Color.GRAY, Color.RED, Color.YELLOW, Color.GREEN);
        mCvRefreshStaggeredGridRecyclerView.setLoadMoreEnabled(true);

        mFamiliarRecyclerView = mCvRefreshStaggeredGridRecyclerView.getFamiliarRecyclerView();
        // ItemAnimator
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // head view
        mFamiliarRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, true, 0xFFFF5000, "Head View 1"));

        
        // Item Click and Item Long Click
        mCvRefreshStaggeredGridRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateStaggeredGridViewDemoActivity.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mCvRefreshStaggeredGridRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateStaggeredGridViewDemoActivity.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mCvRefreshStaggeredGridRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        mViewHeights.clear();
                        mDatas.addAll(getDatas());
                        mAdapter.notifyDataSetChanged();

                        mCvRefreshStaggeredGridRecyclerView.pullRefreshComplete();
                        Log.i("wg", "加载完成啦...");
                    }
                }, 1000);
            }
        });

        mCvRefreshStaggeredGridRecyclerView.setOnLoadMoreListener(new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int startPos = mDatas.size();
                        List<String> newDatas = getDatas();
                        mDatas.addAll(newDatas);
                        mAdapter.notifyItemInserted(startPos);

                        mCvRefreshStaggeredGridRecyclerView.loadMoreComplete();
                    }
                }, 1000);
            }
        });

        mAdapter = new MyAdapter();
        mCvRefreshStaggeredGridRecyclerView.setAdapter(mAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(ImitateStaggeredGridViewDemoActivity.this).inflate(R.layout.item_view_staggered_grid_ver, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            lp.height = mViewHeights.get(position);
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

    private List<String> getDatas() {
        List<String> tempDatas = new ArrayList<>();
        int curMaxData =  mDatas.size();
        for (int i = 0; i < 10; i++) {
            tempDatas.add("item:" + (curMaxData + i));
            mViewHeights.add((int)(300 + Math.random() * 300));
        }

        return tempDatas;
    }

}
