package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/4/20.
 * 孩子概要情况获取
 */
public class ChildSummaryGet extends AbsHttp {
    private String cid;

    @Override
    protected String domain() {
        return "child/summary";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("cid", cid);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
