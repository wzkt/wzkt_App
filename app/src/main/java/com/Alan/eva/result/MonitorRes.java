package com.Alan.eva.result;

import com.Alan.eva.result.Res;
import com.Alan.eva.tools.Tools;

/**
 * Created by CW on 2017/5/4.
 * 异地监测结果
 */
public class MonitorRes  {
    private String data;
    public String getData() {
        return data;
    }
    private int Code;

    public boolean isOk() {
        return Code == Tools.MSG_OK;
    }



    public int code() {
        return Code;
    }

    public QueryMonitorRes getMessage() {
        return Message;
    }

    private QueryMonitorRes Message;




}
