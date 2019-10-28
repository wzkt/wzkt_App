package com.Alan.eva.config;

import android.os.Bundle;

/**
 * Created by CW on 2017/2/23.
 * 蓝牙操作相关事件
 */
public class BleEvent {
    private int code;
    private Bundle extra;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Bundle getExtra() {
        return extra;
    }

    public void setExtra(Bundle extra) {
        this.extra = extra;
    }
}
