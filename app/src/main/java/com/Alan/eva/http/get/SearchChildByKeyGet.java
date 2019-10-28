package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/20.
 * 通过孩子姓名查找孩子列表
 */
public class SearchChildByKeyGet extends AbsHttp {
    private String name;
    private String uid;

    @Override
    protected String domain() {
        return "child/find";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("name", name);
        builder.put("uid",uid);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
