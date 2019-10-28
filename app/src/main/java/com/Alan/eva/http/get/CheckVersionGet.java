package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/5/15.
 * 检查更新请求
 */
public class CheckVersionGet extends AbsHttp {
    private String code;

    @Override
    protected String domain() {
        return "update/checkVersion";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("code", code);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
