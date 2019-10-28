package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.VerifyCodeGet;
import com.Alan.eva.http.post.ForgetPwdPost;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.result.Res;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.tools.timer.MyTimerTask;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.core.AbsActivity;

public class ChangePassActivity extends AbsActivity implements OnClickListener, IResultHandler {
    private AppCompatEditText edt_forget_pwd_phone;
    private AppCompatEditText edt_change_old_pwd;
    private AppCompatEditText edt_forget_pwd_pwd_ensure;
    private AppCompatEditText edt_change_new_pwd;
    private final int VERIFY_GET = 0x0030;
    private final int FORGET_POST = 0x0031;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_change_pwd;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_title_common);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle("修改密码");
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());
        edt_forget_pwd_phone = (AppCompatEditText) findViewById(R.id.edt_change_pwd_phone);
       // edt_forget_pwd_verify = (AppCompatEditText) findViewById(R.id.edt_forget_pwd_verify);
        //edt_forget_pwd_get_verify = (AppCompatTextView) findViewById(R.id.edt_forget_pwd_get_verify);
        edt_change_old_pwd = (AppCompatEditText) findViewById(R.id.edt_change_pwd_pwd);


        edt_change_new_pwd = (AppCompatEditText) findViewById(R.id.edt_change_new_pwd);
        edt_forget_pwd_pwd_ensure = (AppCompatEditText) findViewById(R.id.edt_change_new_pwd_ensure);
        Button btn_forget_pwd_submit = (Button) findViewById(R.id.btn_forget_pwd_submit);

        btn_forget_pwd_submit.setOnClickListener(this);

        UserInfo userInfo = EApp.getApp().getUserInfo(this);
       if(userInfo!=null) {
           String uid = userInfo.getUid();
           if(TextUtils.isEmpty(uid)) {
               edt_forget_pwd_phone.setText("" + uid);
           }
       }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_forget_pwd_get_verify:  //获取验证码
               // getVerifyCode();
                break;
            case R.id.btn_forget_pwd_submit:  //提交
                submitForget();
                break;
        }
    }

//    private void getVerifyCode() {
//        String phone = edt_forget_pwd_phone.getText().toString();
//        if (TextUtils.isEmpty(phone)) {
//            showTips("请输入手机号");
//            return;
//        }
//        VerifyCodeGet get = new VerifyCodeGet();
//        get.code(VERIFY_GET);
//        get.handler(this);
//        get.setPhone(phone);
//        get.get();
//        edt_forget_pwd_get_verify.setClickable(false);
//        MyTimerTask task = new MyTimerTask();
//        task.onTick(60 * 1000);
//        task.setTv(edt_forget_pwd_get_verify);
//        task.setTimer(() -> {
//            edt_forget_pwd_get_verify.setText("获取验证码");
//            edt_forget_pwd_get_verify.setClickable(true);
//        });
//        task.start();
//    }

    private void submitForget() {
        String phone = edt_forget_pwd_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showTips("请输入手机号");
            return;
        }
        String pwd = edt_change_old_pwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            showTips("请输入旧密码");
            return;
        }
        String validate = edt_change_new_pwd.getText().toString();
        if (TextUtils.isEmpty(validate)) {
            showTips("请输入新密码");
            return;
        }

        String pwdEnsure = edt_forget_pwd_pwd_ensure.getText().toString();
        if (TextUtils.isEmpty(pwdEnsure)) {
            showTips("请输入确认密码");
            return;
        }
        if (!TextUtils.equals(validate, pwdEnsure)) {
            showTips("密码和确认密码不一致，请重新输入");
            return;
        }
        ForgetPwdPost post = new ForgetPwdPost();
        post.code(FORGET_POST);
        post.handler(this);
        post.setPhone(phone);
        post.setVerify(pwd);
        post.setPwd(pwdEnsure);
        post.post();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleStart(int code) {
        if (code == FORGET_POST) {
            LogUtil.info("修改密码");
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == VERIFY_GET) {
            Res res = Tools.json2Bean(result, Res.class);
            showTips(res.msg());
            if(res.code()==1){
                finish();
            }
        }else if(code == FORGET_POST){
            Res res = Tools.json2Bean(result, Res.class);
            showTips(res.msg());
            if(res.code()==1){
                finish();
            }
        }
    }

    @Override
    public void handleFinish(int code) {
        if (code == FORGET_POST) {
            LogUtil.info("修改密码");
        }
    }

    @Override
    public void handleError(int code) {
        if (code == VERIFY_GET) {
            showTips("修改失败，请重试");
        } else if (code == FORGET_POST) {
            showTips("修改失败，请重试");
        }
    }
}
