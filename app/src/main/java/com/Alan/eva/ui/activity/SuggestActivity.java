package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.post.FeedbackPost;
import com.Alan.eva.result.Res;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.core.AbsActivity;

public class SuggestActivity extends AbsActivity implements OnClickListener, IResultHandler {
    private AppCompatEditText edit_suggest_comment;
    private AppCompatEditText edit_suggest_contact;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_feed_back;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_title_common);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle("意见反馈");
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());
        edit_suggest_comment = (AppCompatEditText) getView(R.id.edit_suggest_comment);
        edit_suggest_contact = (AppCompatEditText) getView(R.id.edit_suggest_contact);
        AppCompatButton btn_suggest_submit = (AppCompatButton) getView(R.id.btn_suggest_submit);
        btn_suggest_submit.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_suggest_submit:
                submitSuggestion();
                break;
        }
    }

    private final int SUGGEST_POST = 0x00017;

    private void submitSuggestion() {
        String comment = edit_suggest_comment.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            showTips("请输入意见");
            return;
        }
        FeedbackPost post = new FeedbackPost();
        post.code(SUGGEST_POST);
        post.handler(this);
        post.setMsg(comment);
        String phone = edit_suggest_contact.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            post.setPhone(phone);
        }
        post.post();
    }

    @Override
    public void handleStart(int code) {
        if (code == SUGGEST_POST) {
            loading("正在提交，请稍候");
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == SUGGEST_POST) {
            Res res = Tools.json2Bean(result, Res.class);
            hide();
            showTips(res.msg());
            if (res.isOk()) {
                currFinish();
            }
        }
    }

    @Override
    public void handleFinish(int code) {
        if (code == SUGGEST_POST) {
            LogUtil.info("反馈结束");
        }
    }

    @Override
    public void handleError(int code) {
        if (code == SUGGEST_POST) {
            showTips("提交错误，请重试");
        }
    }
}
