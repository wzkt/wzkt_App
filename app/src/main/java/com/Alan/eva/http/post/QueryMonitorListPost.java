package com.Alan.eva.http.post;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;
import com.Alan.eva.tools.LogUtil;

/**
 * Created by CW on 2017/3/21.
 *
 */
public class QueryMonitorListPost extends AbsHttp {
    private String pid;
    private String cid;
    private String ThermometerID;

    public void setUsername(String username) {
        Username = username;
    }

    private String Username;

    @Override
    protected String domain() {
        return BuildConfig.SERVER_URL_NEW+"api/Historical_data_query/";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("Username", Username);
        builder.put("Mobile_num", pid);
        builder.put("ThermometerID", ThermometerID);
        return builder;
    }

    @Override
    public String toString() {
        return "QueryMonitorListPost{" +
                "Mobile_num='" + pid + '\'' +
                ", ThermometerID='" + ThermometerID + '\'' +
                ", Username='" + Username + '\'' +
                '}';
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

    public void setThermometerID(String tid) {
        this.ThermometerID = tid;
    }
}
