package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.Alan.eva.R;
import com.Alan.eva.config.URlConfig;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.core.AbsActivity;

/**
 * Created by qc on 2016/1/26.
 * 公用web页面
 */
public class CommonWebActivity extends AbsActivity {
    private WebView wv_common_web;
    private Toolbar tool_bar_home_title;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_common_web;
    }

    @Override
    public void findView(View rootView) {
        wv_common_web = (WebView) getView(R.id.wv_common_web);
        tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_title_common);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebSettings webSettings = wv_common_web.getSettings();
        //支持JavaScript
        webSettings.setJavaScriptEnabled(false);
        //保存表单数据
        webSettings.setSaveFormData(true);
        //网页内容自适应
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        wv_common_web.setOnLongClickListener(v -> true);
        //Enable html5 cache
        Intent intent = getIntent();
        String url = intent.getStringExtra(URlConfig.URL_KEY_URL);
        String title = intent.getStringExtra(URlConfig.URL_KEY_TITLE);
        wv_common_web.loadUrl(url);

        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle(title);
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());
    }
}
