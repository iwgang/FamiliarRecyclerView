package cn.iwgang.familiarrecyclerviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;
import cn.iwgang.familiarrecyclerview.baservadapter.FamiliarEasyAdapter;

public class ImitateNewListViewDemoActivity extends AppCompatActivity {
    private FamiliarRefreshRecyclerView mCvRefreshListRecyclerView;
    private FamiliarEasyAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout_imitate_listview);

        mCvRefreshListRecyclerView = (FamiliarRefreshRecyclerView) findViewById(R.id.cv_refreshListRecyclerView);
        mCvRefreshListRecyclerView.setColorSchemeColors(0xFFFF5000, Color.RED, Color.YELLOW, Color.GREEN);
        mCvRefreshListRecyclerView.setLoadMoreEnabled(true);

        FamiliarRecyclerView familiarRecyclerView = mCvRefreshListRecyclerView.getFamiliarRecyclerView();
        // ItemAnimator
        familiarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // head view
        familiarRecyclerView.addHeaderView(HeaderAndFooterViewUtil.getHeadView(this, true, 0xFFFF5000, "Head View 1"));

        // Item Click and Item Long Click
        mCvRefreshListRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Toast.makeText(ImitateNewListViewDemoActivity.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mCvRefreshListRecyclerView.setOnItemLongClickListener(new FamiliarRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Toast.makeText(ImitateNewListViewDemoActivity.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mCvRefreshListRecyclerView.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.replaceAll(genData(0));
                        mCvRefreshListRecyclerView.pullRefreshComplete();
                        mCvRefreshListRecyclerView.setLoadMoreEnabled(true);
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
                        mAdapter.addAll(genData(mAdapter.getDataSize()));
                        mCvRefreshListRecyclerView.loadMoreComplete();

                        if (mAdapter.getDataSize() > 100) {
                            mCvRefreshListRecyclerView.setLoadMoreEnabled(false);
                        }
                    }
                }, 1000);
            }
        });

        mAdapter = new FamiliarEasyAdapter<String>(this, R.layout.item_view_linear, genData(0)) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                TextView tvTxt = holder.findView(R.id.tv_txt);
                tvTxt.setText(mAdapter.getData(position));
            }
        };
        mCvRefreshListRecyclerView.setAdapter(mAdapter);
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
                mAdapter.add("new add item:" + mAdapter.getDataSize());
                break;
            case R.id.id_action_delete:
                mAdapter.remove(mAdapter.getDataSize() - 1);
                break;
        }
        return true;
    }

    private List<String> genData(int startNum) {
        List<String> tempData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            tempData.add("item:" + (startNum + i));
        }
        return tempData;
    }

}
