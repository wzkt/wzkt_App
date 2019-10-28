package com.Alan.eva.http.core;

/**
 * Created by qc on 2015/12/23.
 * 加载更多
 */
public abstract class LoadMoreHttp extends AbsHttp {
    private int pageIndex = 1;

    protected ReqParam setParams(ReqParam builder) {
        builder.put("page", "" + pageIndex);
        return builder;
    }

    public void refresh() {
        pageIndex = 1;
    }

    public void loadMore() {
        ++pageIndex;
    }

    public boolean isRefresh() {
        return pageIndex > 1;
    }
}
