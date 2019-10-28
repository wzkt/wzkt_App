package com.Alan.eva.http.post;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/8.
 * 孩子温度提交请求
 */
public class ChildTempPost extends AbsHttp {
    private String cid;
    private String temp;

    @Override
    protected String domain() {
        return "temp/submit";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("cid", cid);
        builder.put("temp", temp);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
