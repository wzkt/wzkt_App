package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/23.
 * 孩子监护人列表获取
 */
public class ChildMonitorGet extends AbsHttp {
    private String pid;
    private String cid;

    @Override
    protected String domain() {
        return "bind/getList";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("pid", pid);
        builder.put("cid", cid);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
