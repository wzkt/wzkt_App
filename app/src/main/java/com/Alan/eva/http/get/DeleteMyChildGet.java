package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/15.
 * 删除孩子操作
 */
public class DeleteMyChildGet extends AbsHttp {
    private String cid;
    private String uid;

    @Override
    protected String domain() {
        return "child/delete";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("cid", cid);
        builder.put("pid", uid);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
