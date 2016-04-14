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

public class ImitateListViewDemoActivity extends AppCompatActivity {
    private FamiliarRefreshRecyclerView mCvRefreshListRecyclerView;
    private FamiliarRecyclerView mFamiliarRecyclerView;

    private List<String> mDatas;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_imitate_listview);

        mDatas = new ArrayList<>();
        mDatas.addAll(getDatas());

        mCvRefreshListRecyclerView = (FamiliarRefreshRecyclerView)findViewById(R.id.cv_refreshListRecyclerView);
        mCvRefreshListRecyclerView.setLoadMoreView(new LoadMoreView(this));
        mCvRefreshListRecyclerView.setColorSchemeColors(0xFFFF5000, Color.RED, Color.YELLOW, Color.GREEN);
        mCvRefreshListRecyclerView.setLoadMoreEnabled(true);

        mFamiliarRecyclerView = mCvRefreshListRecyclerView.getFamiliarRecyclerView();
        // ItemAnimator
        mFamiliarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // head view
        mFamiliarRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, true, 0xFFFF5000, "Head View 1"));


        // Item Click and Item Long Click
        mCvRefreshListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateListViewDemoActivity.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mCvRefreshListRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Log.i("wg", "onItemLongClick = " + familiarRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ImitateListViewDemoActivity.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });



        // head view
//        List<String> headDatas = new ArrayList<>();
//        headDatas.addAll(getDatas());
//        final MyAdapter2 headAdapter = new MyAdapter2(headDatas, 2);
//        View headView = View.inflate(this, R.layout.head_frc_grid_hor, null);
//        final FamiliarRecyclerView mHeadFamiliarRecyclerView = (FamiliarRecyclerView)headView.findViewById(R.id.mHorRecyclerView);
//        mHeadFamiliarRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
//            @Override
//            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
//                headAdapter.getData().remove(position);
//                headAdapter.notifyItemRemoved(position);
//            }
//        });
//        mHeadFamiliarRecyclerView.setAdapter(headAdapter);
//        mRecyclerView.addHeaderView(headView);
//        mRecyclerView.setOnHeadViewBindViewHolderListener(new FamiliarRecyclerView.OnHeadViewBindViewHolderListener() {
//            @Override
//            public void onHeadViewBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isInitializeInvoke) {
//                Log.i("wg", "onHeadViewBindViewHolder = " + isInitializeInvoke + " _ pos = " + position);
//                if (position == 1 && !isInitializeInvoke) {
//                    mHeadFamiliarRecyclerView.reRegisterAdapterDataObserver();
//                }
//            }
//        });

//        mFamiliarRecyclerView.setOnFooterViewBindViewHolderListener(new FamiliarRecyclerView.OnFooterViewBindViewHolderListener() {
//            @Override
//            public void onFooterViewBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isInitializeInvoke) {
//                Log.i("wg", "onFooterViewBindViewHolder = " + isInitializeInvoke + " _ pos = " + position);
//            }
//        });

        mCvRefreshListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        mDatas.addAll(getDatas());
                        mAdapter.notifyDataSetChanged();

                        mCvRefreshListRecyclerView.pullRefreshComplete();
                        Log.i("wg", "加载完成啦...");
                    }
                }, 1000);
            }
        });

        mCvRefreshListRecyclerView.setOnLoadMoreListener(new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int startPos = mDatas.size();
                        List<String> newDatas = getDatas();
                        mDatas.addAll(newDatas);
                        mAdapter.notifyItemInserted(startPos);

                        mCvRefreshListRecyclerView.loadMoreComplete();
                    }
                }, 1000);
            }
        });

        mAdapter = new MyAdapter();
        mCvRefreshListRecyclerView.setAdapter(mAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private static final int VIEW_TYPE_1 = 101;
        private static final int VIEW_TYPE_2 = 102;
        private MyAdapter2 myAdapter2;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(ImitateListViewDemoActivity.this).inflate(viewType == VIEW_TYPE_1 ? R.layout.item_view_linear : R.layout.item_frc_linear_hor, parent, false));
            if (viewType == VIEW_TYPE_2) {
                Log.i("wg", " VIEW_TYPE_2 ");

                List<String> mDatas = new ArrayList<>();
                mDatas.addAll(getDatas());

                FamiliarRecyclerView mHorFamiliarRecyclerView = (FamiliarRecyclerView)holder.itemView.findViewById(R.id.mHorRecyclerView);
                mHorFamiliarRecyclerView.setAdapter(myAdapter2 = new MyAdapter2(mDatas, 1));
                mHorFamiliarRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
                    @Override
                    public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                        myAdapter2.getData().remove(position);
                        myAdapter2.notifyItemRemoved(position);
                    }
                });
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_1:
                    holder.mTvTxt.setText(mDatas.get(position));
                    break;
                case VIEW_TYPE_2:
                    FamiliarRecyclerView mHorFamiliarRecyclerView = (FamiliarRecyclerView)holder.itemView.findViewById(R.id.mHorRecyclerView);
                    if (mHorFamiliarRecyclerView.getChildCount() > 0) {
                        mHorFamiliarRecyclerView.reRegisterAdapterDataObserver();
                    }
                    break;
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

    class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder> {
        private List<String> data;
        private int type;

        public MyAdapter2(List<String> data, int type) {
            this.data = data;
            this.type = type;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(ImitateListViewDemoActivity.this).inflate(type == 1 ? R.layout.item_view_linear_hor : R.layout.item_view_grid_hor, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mTvTxt.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public List<String> getData() {
            return data;
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
        for (int i = 0; i < 30; i++) {
            tempDatas.add("item:" + (curMaxData + i));
        }

        return tempDatas;
    }

}
