package com.youmu.loadlistview;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by youzh on 2015/3/20.
 */
public abstract class BaseLoadActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    @InjectView(R.id.listView)
    ListView mListView;
    @InjectView(R.id.refreshLayout)
    SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.empty_vs)
    ViewStub mEmptyVs;

    private int page = 0;
    private int oldPage = 0;
    private BaseAdapter mAdapter;
    private View mEmptyView;
    private Context ctx;
    private View mLoadLayout;// 加载更多的view
    private int totalItemCount;// Item总数量；
    private int lastVisibleItem;// 最后一个可见的item；
    boolean isLoading;// 正在加载；
    private View mFootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    public void initLoad(Context ctx, BaseAdapter adapter) {
        this.ctx = ctx;

        this.mAdapter = adapter;
        initView();
        loadFirstData();
    }

    /**
     * 加载第一页
     */
    protected void loadFirstData() {
        oldPage = page;
        page = 0;
        loadData(page);
    }

    /**
     * 加载下一页
     */
    protected void loadNextData() {
        oldPage = page;
        page++;
        loadData(page);
    }

    /**
     * 还原页码
     */
    protected void restorePage() {
        page = oldPage;
    }

    private void initView() {

        mSwipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_blue_dark, android.R.color.holo_green_light, android.R.color.holo_orange_light);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadFirstData();
            }
        });

        // 添加FootView
        mFootView = View.inflate(ctx, R.layout.loading_more, null);
        mLoadLayout = mFootView.findViewById(R.id.more_load_layout);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        // 显示自动加载
        mSwipeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mSwipeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSwipeLayout.setRefreshing(true);
            }
        });

        // 显示ListView为空的View
        mEmptyVs.setLayoutResource(R.layout.layout_empty);
        if (mEmptyView == null) {
            mEmptyView = mEmptyVs.inflate();
        } else {
            mEmptyVs.setVisibility(View.VISIBLE);
        }

        // 观察Adapter的数据变化
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mListView.getAdapter().getCount() == 0) {// 显示空的View
                    mEmptyVs.setVisibility(View.VISIBLE);
                } else {
                    mEmptyVs.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 加载数据
     */
    protected abstract void loadData(int page);

    /**
     * 数据加载完毕
     */
    protected void loadFinish() {
        mSwipeLayout.setRefreshing(false);
        isLoading = false;
//        mLoadLayout.setVisibility(View.GONE);
        if (oldPage == page) {
            if (mListView.getFooterViewsCount() > 0)
                mListView.removeFooterView(mFootView);
        } else if (mListView.getFooterViewsCount() == 0){
            mListView.addFooterView(mFootView);
        }
    }
    protected void clear() {
        if (mListView.getFooterViewsCount() > 0)
            mListView.removeFooterView(mFootView);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
//        view.getLastVisiblePosition();
        this.totalItemCount = totalItemCount;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if ((totalItemCount) == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                // 加载更多
                loadNextData();
            }
        }
    }
}
