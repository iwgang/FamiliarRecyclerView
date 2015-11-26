package cn.iwgang.familiarrecyclerviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_linearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LinearLayoutActivity.class));
            }
        });
        findViewById(R.id.btn_linearLayoutHor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LinearLayoutActivity.class);
                intent.putExtra("isVertical", false);
                startActivity(intent);
            }
        });


        findViewById(R.id.btn_gridLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GridLayoutActivity.class));
            }
        });
        findViewById(R.id.btn_gridLayoutHor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GridLayoutActivity.class);
                intent.putExtra("isVertical", false);
                startActivity(intent);
            }
        });


        findViewById(R.id.btn_staggeredGridLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StaggeredGridActivity.class));
            }
        });
        findViewById(R.id.btn_staggeredGridLayoutHor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StaggeredGridActivity.class);
                intent.putExtra("isVertical", false);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_imitateListViewDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ImitateListViewDemoActivity.class));
            }
        });

        findViewById(R.id.btn_imitateGridViewDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ImitateGridViewDemoActivity.class));
            }
        });

        findViewById(R.id.btn_imitateStaggeredGridViewDemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ImitateStaggeredGridViewDemoActivity.class));
            }
        });
    }
}
