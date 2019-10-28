package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/5/4.
 * 远程监测温度
 */
public class MonitorGet extends AbsHttp {
    private String cid;

    @Override
    protected String domain() {
        return "temp/get";
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
