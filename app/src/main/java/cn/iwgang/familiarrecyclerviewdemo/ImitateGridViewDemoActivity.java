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

public class ImitateGridViewDemoActivity extends AppCompatActivity {
    private FamiliarRefreshRecyclerView mCvRefreshGridRecyclerView;
    private FamiliarRecyclerView mFamiliarRecyclerView;

    private List<String> mDatas;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_imitate_gridview);

        mDatas = new ArrayList<>();
        mDatas.addAll(getDatas());

        mCvRefreshGridRecyclerView = (FamiliarRefreshRecyclerView)findViewById(R.id.cv_refreshGridRecyclerView);
        mCvRefreshGridRecyclerView.setLoadMoreView(new LoadMoreView(this));
        mCvRefreshGridRecyclerView.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        mCvRefreshGridRecyclerView.setLoadMoreEnabled(true);

        mFamiliarRecyclerView = mCvRefreshGridRecyclerView.getFamiliarRecyclerView();
        // ItemAnimator
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // head view
        mFamiliarRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, true, 0xFFFF5000, "Head View 1"));


        // Item Click and Item Long Click
        mCvRefreshGridRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateGridViewDemoActivity.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mCvRefreshGridRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateGridViewDemoActivity.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mCvRefreshGridRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        mDatas.addAll(getDatas());
                        mAdapter.notifyDataSetChanged();

                        mCvRefreshGridRecyclerView.pullRefreshComplete();
                        Log.i("wg", "加载完成啦...");
                    }
                }, 1000);
            }
        });

        mCvRefreshGridRecyclerView.setOnLoadMoreListener(new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int startPos = mDatas.size();
                        List<String> newDatas = getDatas();
                        mDatas.addAll(newDatas);
                        mAdapter.notifyItemInserted(startPos);

                        mCvRefreshGridRecyclerView.loadMoreComplete();
                    }
                }, 1000);
            }
        });

        mAdapter = new MyAdapter();
        mCvRefreshGridRecyclerView.setAdapter(mAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(ImitateGridViewDemoActivity.this).inflate(R.layout.item_view_grid, parent, false));
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
        int notifyPos;
        switch (item.getItemId()) {
            case R.id.id_action_add:
                notifyPos = mDatas.size();

                mDatas.add("new add item:" + notifyPos);
                mAdapter.notifyItemInserted(notifyPos);
                break;
            case R.id.id_action_delete:
                if (mDatas.isEmpty()) return true;

                notifyPos = mDatas.size() - 1;

                mDatas.remove(notifyPos);
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
        }

        return tempDatas;
    }

}
