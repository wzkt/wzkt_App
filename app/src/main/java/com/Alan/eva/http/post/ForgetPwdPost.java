package com.Alan.eva.http.post;

import com.Alan.eva.BuildConfig;
import com.Alan.eva.http.core.AbsHttp;
import com.Alan.eva.http.core.ReqParam;

/**
 * Created by CW on 2017/3/2.
 * 忘记密码 提交
 */
public class ForgetPwdPost extends AbsHttp {
    private String phone;
    private String verify;
    private String pwd;

    @Override
    protected String domain() {
        return BuildConfig.SERVER_URL_NEW+"api/passwordchange/";
    }

    @Override
    protected ReqParam setParams(ReqParam builder) {
        builder.put("mobile_num", phone);
        builder.put("pwd_old", verify);
        builder.put("pwd_new", pwd);
        return builder;
    }

    @Override
    protected boolean addFile(ReqParam params) {
        return false;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
