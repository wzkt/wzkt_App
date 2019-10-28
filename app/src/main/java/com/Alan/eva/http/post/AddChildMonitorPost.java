package com.Alan.eva.http.post;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/21.
 * 解除绑定操作
 */
public class AddChildMonitorPost extends AbsHttp {
    private String pid;
    private String cid;
    private String phone;

    @Override
    protected String domain() {
        return "bind/addMonitor";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("pid", pid);
        builder.put("cid", cid);
        builder.put("phone", phone);
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

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
