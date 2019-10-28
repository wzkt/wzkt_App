package com.Alan.eva.http.post;

import android.util.Log;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/21.
 * 解除绑定操作
 */
public class QueryDataListPost extends AbsHttp {
    private String pid;
    private String cid;
    private String ThermometerID;
    private String query_date;
    private String opt_temp;

    public void setUsername(String username) {
        Username = username;
    }

    private String Username;

    @Override
    protected String domain() {
        return BuildConfig.SERVER_URL_NEW+"api/data_analysis/";
    }

    public String getQuery_date() {
        return query_date;
    }

    public void setQuery_date(String query_date) {
        this.query_date = query_date;
    }

    public String getOpt_temp() {
        return opt_temp;
    }

    public void setOpt_temp(String opt_temp) {
        this.opt_temp = opt_temp;
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("username", Username);
        builder.put("mobile_num", pid);
        builder.put("thermometer_id", ThermometerID);
        builder.put("query_date", query_date);
        builder.put("opt_temp", opt_temp);
        return builder;
    }

    @Override
    public String toString() {
        //Log.e("hjs","QueryDataListPost");
        return "QueryDataListPost{" +
                "mobile_num='" + pid + '\'' +
                ", ThermometerID='" + ThermometerID + '\'' +
                ", query_date='" + query_date + '\'' +
                ", opt_temp='" + opt_temp + '\'' +
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
