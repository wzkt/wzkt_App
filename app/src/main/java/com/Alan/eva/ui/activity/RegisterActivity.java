package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.Alan.eva.R;
import com.Alan.eva.config.URlConfig;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.VerifyCodeGet;
import com.Alan.eva.http.post.RegisterPost;
import com.Alan.eva.result.Res;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.tools.timer.MyTimerTask;
import com.Alan.eva.ui.core.AbsActivity;

public class RegisterActivity extends AbsActivity implements OnClickListener, IResultHandler {
    private AppCompatEditText et_reg_phone;
    private AppCompatEditText et_reg_verify;
    private AppCompatTextView edt_reg_get_verify;
    private AppCompatEditText et_reg_pwd;

    private AppCompatCheckBox cb_reg_user_agreement;

    private final int VERIFY_GET = 0x0040;
    private final int REGISTER_POST = 0x0041;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_register;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_title_common);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle("注册");
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());
        et_reg_phone = (AppCompatEditText) findViewById(R.id.et_reg_phone);
        et_reg_verify = (AppCompatEditText) findViewById(R.id.et_reg_verify);
        edt_reg_get_verify = (AppCompatTextView) findViewById(R.id.edt_reg_get_verify);
        et_reg_pwd = (AppCompatEditText) findViewById(R.id.et_reg_pwd);
        cb_reg_user_agreement = (AppCompatCheckBox) getView(R.id.cb_reg_user_agreement_checked);
        AppCompatTextView tv_reg_user_agreement = (AppCompatTextView) getView(R.id.tv_reg_user_agreement);
        AppCompatButton ben_reg_register = (AppCompatButton) findViewById(R.id.btn_reg_register);
        String agreement = "同意接受<font color=\"blue\">智能温度计用户使用协议</font>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_reg_user_agreement.setText(Html.fromHtml(agreement, Html.FROM_HTML_MODE_COMPACT));
        } else {
            //noinspection deprecation
            tv_reg_user_agreement.setText(Html.fromHtml(agreement));
        }
        tv_reg_user_agreement.setOnClickListener(this);
        edt_reg_get_verify.setOnClickListener(this);
        ben_reg_register.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_reg_get_verify:// 获得验证码
                getVerifyCode();
                break;
            case R.id.btn_reg_register:  //注册
                register();
                break;
            case R.id.tv_reg_user_agreement: //查看用户协议
                Intent agreement = getIntent(CommonWebActivity.class);
                agreement.putExtra(URlConfig.URL_KEY_TITLE, URlConfig.USER_AGREEMENT);
                agreement.putExtra(URlConfig.URL_KEY_URL, URlConfig.USER_AGREEMENT_URL);
                startActivity(agreement);
                break;
        }
    }

    private void getVerifyCode() {
        String phone = et_reg_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showTips("请输入手机号");
            return;
        }
        VerifyCodeGet get = new VerifyCodeGet();
        get.code(VERIFY_GET);
        get.handler(this);
        get.setPhone(phone);
        get.get();
        edt_reg_get_verify.setClickable(false);
        MyTimerTask task = new MyTimerTask();
        task.onTick(60 * 1000);
        task.setTv(edt_reg_get_verify);
        task.setTimer(() -> {
            edt_reg_get_verify.setText("获取验证码");
            edt_reg_get_verify.setClickable(true);
        });
        task.start();
    }

    private void register() {
        String phone = et_reg_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showTips("请输入手机号");
            return;
        }
        String validate = et_reg_verify.getText().toString();
        if (TextUtils.isEmpty(validate)) {
            showTips("请输入验证码");
            return;
        }
        String pwd = et_reg_pwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            showTips("请输入数字密码");
            return;
        }
        boolean checked = cb_reg_user_agreement.isChecked();
        if (!checked) {
            showTips("请同意用户使用协议");
            return;
        }
        RegisterPost post = new RegisterPost();
        post.code(REGISTER_POST);
        post.handler(this);
        post.setPhone(phone);
        post.setVerify(validate);
        post.setPwd(pwd);
        post.post();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleStart(int code) {

    }

    @Override
    public void handleResult(String result, int code) {
        if (code == VERIFY_GET) {
            Res res = Tools.json2Bean(result, Res.class);
            showTips(res.msg());
        } else if (code == REGISTER_POST) {
            Res res = Tools.json2Bean(result, Res.class);
            showTips(res.msg());
            if (res.isOk()) {
                currFinish();
            }
        }
    }

    @Override
    public void handleFinish(int code) {
        if (code == REGISTER_POST) {
            LogUtil.info("注册成功，请登录");
        }
    }

    @Override
    public void handleError(int code) {
        if (code == VERIFY_GET) {
            showTips("获取验证码失败，请重试");
        } else if (code == REGISTER_POST) {
            showTips("注册失败，请重试");
        }
    }
}
