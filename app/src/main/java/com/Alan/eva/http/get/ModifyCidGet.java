package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/4/24.
 * 修改用户默认孩子id的
 */
public class ModifyCidGet extends AbsHttp {
    private String uid;
    private String cid;

    @Override
    protected String domain() {
        return "user/updateCid";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("uid", uid);
        builder.put("cid", cid);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
