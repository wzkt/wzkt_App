package com.Alan.eva.http.post;

import android.text.TextUtils;

import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/5/15.
 * 意见反馈提交
 */
public class FeedbackPost extends AbsHttp {
    private String msg;
    private String phone;

    @Override
    protected String domain() {
        return "feedback/feedBack";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("msg", msg);
        if (!TextUtils.isEmpty(phone)) {
            builder.put("phone", phone);
        }
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
