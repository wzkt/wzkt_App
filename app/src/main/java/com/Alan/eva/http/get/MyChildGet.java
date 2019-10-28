package com.Alan.eva.http.get;

import com.Alan.eva.http.core.LoadMoreHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/9.
 * 获取我的孩子列表
 */
public class MyChildGet extends LoadMoreHttp {
    private String pid;

    @Override
    protected String domain() {
        return "child/myChild";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("pid", pid);
        return super.setParams(builder);
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
