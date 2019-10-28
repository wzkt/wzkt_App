package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/23.
 * 删除监护人请求
 */
public class DeleteMonitorGet extends AbsHttp {
    private String bid;

    @Override
    protected String domain() {
        return "bind/unbind";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("bid", bid);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }
}
