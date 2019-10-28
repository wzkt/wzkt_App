package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.Alan.eva.R;
import com.Alan.eva.config.BLEConfig;
import com.Alan.eva.config.BleEvent;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.post.LoginPost;
import com.Alan.eva.http.post.QueryMonitorPost;
import com.Alan.eva.model.ChildSummary;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.model.VersionData;
import com.Alan.eva.result.ChildSummaryRes;
import com.Alan.eva.result.LoginRes;
import com.Alan.eva.result.QueryMonitorRes;
import com.Alan.eva.result.VersionRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.core.AbsActivity;
import com.Alan.eva.ui.dialog.MacInputDialog;
import com.Alan.eva.ui.dialog.SingleInputDialog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import static com.Alan.eva.config.BLEConfig.CMD_EXTRA;

public class LoginActivity extends AbsActivity implements IResultHandler, View.OnClickListener {
    private EditText et_login_phone;
    private EditText et_login_password;

    private final int LOGIN_CODE = 0x00040;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_login;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_title_common);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle("登录");
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());

        et_login_phone = (EditText) getView(R.id.et_login_phone);
        et_login_password = (EditText) getView(R.id.et_login_password);
        AppCompatTextView iv_login_forget_pwd = (AppCompatTextView) getView(R.id.iv_login_forget_pwd);
        AppCompatTextView iv_login_register = (AppCompatTextView) getView(R.id.iv_login_register);
        iv_login_forget_pwd.setOnClickListener(this);
        iv_login_register.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_login_forget_pwd: //忘记密码
                gotoActivity(ForgetPassActivity.class);
                break;
            case R.id.iv_login_register://注册
                gotoActivity(RegisterActivity.class);
                break;
        }
    }

    /**
     * 登录按钮
     *
     * @param view v
     */
    public void btnLogin(View view) {
        String phone = et_login_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showTips("请输入手机号");
            return;
        }
        String pwd = et_login_password.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            showTips("请输入数字密码");
            return;
        }
        LoginPost post = new LoginPost();
        post.code(LOGIN_CODE);
        post.handler(this);
        post.setPhone(phone);
        post.setPwd(pwd);
        post.get();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleStart(int code) {
        if (code == LOGIN_CODE) {
            LogUtil.info("登录开始");
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == LOGIN_CODE) {
            LoginRes res = Tools.json2Bean(result, LoginRes.class);
            LogUtil.info(""+res.isOk());

            //showTips(res.msg());
            if (res.isOk()) {
                UserInfo userInfo = res.getData();
                if(userInfo!=null) {

                }else{
                    userInfo = new UserInfo();
                    userInfo.setUid(""+ et_login_phone.getText().toString());
                    //userInfo.setCid(""+et_login_phone.getText().toString());
                }
                    EApp.getApp().setUserInfo(userInfo, getCurrActivity());
                    String uid = userInfo.getUid();
                    MobclickAgent.onProfileSignIn(String.valueOf("eve_" + uid));
                showTips(""+res.msg());
                setResult(RESULT_OK);
                //dialogshow(uid);
                currFinish();
            }else{
                showTips(""+res.msg());
            }
        }
    }





    @Override
    public void handleFinish(int code) {
        if (code == LOGIN_CODE) {
            LogUtil.info("登录结束");
        }
    }

    @Override
    public void handleError(int code) {
        if (code == LOGIN_CODE) {
            showTips("登录失败，请重试");
        }
    }
//
//    /**
//     * 发送命令
//     */
//    private void sendCmd(int code, String extra) {
//        bleEvent.setCode(code);
//        if (!TextUtils.isEmpty(extra)) {
//            Bundle bundle = new Bundle();
//            bundle.putString(CMD_EXTRA, extra);
//            bleEvent.setExtra(bundle);
//        }
//        EventBus.getDefault().post(bleEvent);
//    }
}
