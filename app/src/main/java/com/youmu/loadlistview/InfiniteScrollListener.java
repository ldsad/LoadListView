package com.youmu.loadlistview;

import android.view.View;
import android.widget.AbsListView;

public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
    int totalItemCount;// 总数量；
    int lastVisibleItem;// 最后一个可见的item；
    boolean isLoading;// 正在加载；
    View footView;

    public InfiniteScrollListener(View footView) {
        this.footView = footView;

    }

    public void initLoading (boolean isLoading) {
        this.isLoading = isLoading;
    }

    public abstract void loadMore();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totalItemCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footView.setVisibility(View.VISIBLE);
                // 加载更多
                loadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }
}