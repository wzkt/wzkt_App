package com.Alan.eva.http.get;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/4/10.
 * 每月温度数据报表
 */
public class MonthProfileGet extends AbsHttp {
    private String year;
    private String month;
    private String cid;

    @Override
    protected String domain() {
        return "temp/profile";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("year", year);
        builder.put("month", month);
        builder.put("cid", cid);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
