package com.Alan.eva.http.get;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/2.
 * 验证码获取
 */
public class VerifyCodeGet extends AbsHttp {
    private String phone;

    @Override
    protected String domain() {
        return BuildConfig.SERVER_URL_NEW+"api/request_verification_code/";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("mobile_num", phone);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
