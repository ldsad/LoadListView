package com.youmu.loadlistview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseLoadActivity {
    @InjectView(R.id.listView)
    ListView mListview;
    @InjectView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.empty_vs)
    ViewStub mEmptyVs;
    private ArrayList<String> mDataList = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mDataList);
        initLoad(this, mRefreshLayout, mListview, mEmptyVs, mAdapter);
    }

    private void getData(final String dataName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            for (int i = 0; i < 15; i++) {
                                mDataList.add(dataName + i);
                            }
                            mAdapter.notifyDataSetChanged();
                            loadFinish();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear) {
            mDataList.clear();
            mAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void loadData(int page) {
        if (page == 0) {
            mDataList.clear();
            getData("Refresh 0页数据: ");
        } else {
            getData("More" + page +"页数据: ");
        }
    }
}
