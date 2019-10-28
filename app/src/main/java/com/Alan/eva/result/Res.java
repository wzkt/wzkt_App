package com.Alan.eva.result;

import com.Alan.eva.tools.Tools;

/**
 * Created by CW on 2017/3/2.
 * 抽象数据回调
 */
public class Res {
    private int Code;
    private String Message;

    public boolean isOk() {
        return Code == Tools.MSG_OK;
    }


    public String msg() {
        return Message;

    }

    public int code() {
        return Code;
    }
}
