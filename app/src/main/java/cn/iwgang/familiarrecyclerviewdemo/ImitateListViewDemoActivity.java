package cn.iwgang.familiarrecyclerviewdemo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerViewOnScrollListener;

public class ImitateListViewDemoActivity extends AppCompatActivity {
    private FamiliarRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mFooterLoadMoreView;
    private ProgressBar mPbLoadMoreProgressBar;
    private TextView mTvLoadMoreText;

    private List<String> mDatas;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_imitate_listview);

        mDatas = new ArrayList<>();
        mDatas.addAll(getDatas());

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mSwipeRefreshLayout);
        mRecyclerView = (FamiliarRecyclerView)findViewById(R.id.mRecyclerView);

        // Item Click and Item Long Click
        mRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateListViewDemoActivity.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateListViewDemoActivity.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mRecyclerView.addOnScrollListener(new FamiliarRecyclerViewOnScrollListener(mRecyclerView.getLayoutManager()) {
            @Override
            public void onScrolledToTop() {
                Log.i("wg", "onScrolledToTop ...");
            }

            @Override
            public void onScrolledToBottom() {
                Log.i("wg", "onScrolledToBottom ...");
                if (mDatas.size() >= 50) {
                    return ;
                }

                // add footer view
                mPbLoadMoreProgressBar.setVisibility(View.VISIBLE);
                mTvLoadMoreText.setText("正在加载数据...");

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int startPos = mDatas.size() - 1;
                        List<String> newDatas = getDatas();
                        mDatas.addAll(newDatas);
                        mAdapter.notifyItemRangeChanged(startPos, newDatas.size());

                        mPbLoadMoreProgressBar.setVisibility(View.GONE);
                        mTvLoadMoreText.setText("松开加载更多");

                        if (mDatas.size() >= 50) {
                            mRecyclerView.removeFooterView(mFooterLoadMoreView);
                        }
                    }
                }, 1000);
            }
        });

        mFooterLoadMoreView = View.inflate(this, R.layout.footer_view_load_more, null);
        mFooterLoadMoreView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mPbLoadMoreProgressBar = (ProgressBar)mFooterLoadMoreView.findViewById(R.id.pb_progressBar);
        mTvLoadMoreText = (TextView)mFooterLoadMoreView.findViewById(R.id.tv_text);

        // ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // head view
        mRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, true, 0xFFFF5000, "Head View 1"));

        mRecyclerView.addFooterView(mFooterLoadMoreView);

        mRecyclerView.setEmptyViewRetainShowHeadOrFoot(true);

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        mDatas.addAll(getDatas());
                        mAdapter.notifyDataSetChanged();

                        mRecyclerView.addFooterView(mFooterLoadMoreView);

                        mPbLoadMoreProgressBar.setVisibility(View.GONE);
                        mTvLoadMoreText.setText("松开加载更多");

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private static final int VIEW_TYPE_1 = 101;
        private static final int VIEW_TYPE_2 = 102;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(ImitateListViewDemoActivity.this).inflate(viewType == VIEW_TYPE_1 ? R.layout.item_view_linear : R.layout.item_view_linear_type2, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_2) {
                holder.mTvTxt.setText(String.format("View Type 2 %s", mDatas.get(position)));
            } else {
                holder.mTvTxt.setText(mDatas.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 3 ? VIEW_TYPE_2 : VIEW_TYPE_1;
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
