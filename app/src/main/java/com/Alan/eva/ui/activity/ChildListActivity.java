package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.MyChildGet;
import com.Alan.eva.model.ChildModel;
import com.Alan.eva.model.ChildSummary;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.result.ChildRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.adapter.MyChildAdapter;
import com.Alan.eva.ui.core.AbsActivity;
import com.Alan.eva.ui.widget.FlickeringTextView;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.xutils.ex.DbException;

import java.util.ArrayList;

/**
 * Created by CW on 2017/3/8.
 * 孩子列表界面
 */
public class ChildListActivity extends AbsActivity implements PullLoadMoreRecyclerView.PullLoadMoreListener, IResultHandler {
    private FlickeringTextView flicking_text_child_list;
    private PullLoadMoreRecyclerView pull_refresh_child_list;
    private String uid;
    private ArrayList<ChildModel> myChildren;
    private MyChildAdapter myChildAdapter;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_child_list;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_my_child_list);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle(R.string.my_child_list);
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());
        tool_bar_home_title.setOnMenuItemClickListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.title_bar_add_new_child:
                    Intent intent = getIntent(AddChildActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });
        flicking_text_child_list = (FlickeringTextView) getView(R.id.flicking_text_child_list);
        pull_refresh_child_list = (PullLoadMoreRecyclerView) getView(R.id.pull_refresh_child_list);
        pull_refresh_child_list.setLinearLayout();
        pull_refresh_child_list.setColorSchemeResources(R.color.orange_255_102_64, R.color.blue_0_160_226, R.color.colorAccent);
        pull_refresh_child_list.setHasMore(false);
        pull_refresh_child_list.setOnPullLoadMoreListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        myChildren = new ArrayList<>();

       // Intent intentstartMonitor = new Intent(this,MonitorActivity.class);
        //startActivity(intentstartMonitor);



    }

    private ChildSummary queryChildSummary(){
        try {
            ChildSummary first = null;
            try {
                first = EApp.getApp().db.findFirst(ChildSummary.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (first != null) LogUtil.inf(first.toString());
            //添加查询条件进行查询
            return first;
        }catch (Exception e){
            return null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        ChildSummary dbchild = queryChildSummary();
        if(dbchild!=null){
            ChildModel first = new ChildModel();
            first.setAge(dbchild.getAge());
            first.setGender(dbchild.getGender());
            first.setHeight(dbchild.getHeight());
            first.setWeight(dbchild.getWeight());
            first.setName(dbchild.getName());


            if(myChildren!=null){
                myChildren.clear();
                myChildren.add(first);
                notifyList(myChildren);
            }
        }else{
            myChildren.clear();
            notifyList(myChildren);
        }


        pull_refresh_child_list.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_child_add, menu);
        return true;
    }


    @Override
    public void handleResult(String result, int code) {
        if (code == MY_CHILD_GET) {
            ChildRes res = Tools.json2Bean(result, ChildRes.class);
            if (!res.isOk()) {
                //showTips(res.msg());
                return;
            }
            myChildren = res.getData();
            if (Tools.isListEmpty(myChildren)) {
                String str = getString(R.string.child_info_txt);
                showTips(""+str);
                return;
            }
            notifyList(myChildren);
        }
    }

    /**
     * 更新列表数据
     *
     * @param list 数据
     */
    private void notifyList(ArrayList<ChildModel> list) {
        if (myChildAdapter == null) {
            myChildAdapter = new MyChildAdapter(list, this);
            pull_refresh_child_list.setAdapter(myChildAdapter);
        } else {
            myChildAdapter.setDataList(list);
            myChildAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void handleStart(int code) {
        if (code == MY_CHILD_GET) {
            if (flicking_text_child_list.getVisibility() == View.VISIBLE) {
                flicking_text_child_list.setFlickering(true);
            }
        }
    }

    @Override
    public void handleFinish(int code) {
        if (code == MY_CHILD_GET) {
            pull_refresh_child_list.setPullLoadMoreCompleted();
            if (flicking_text_child_list.getVisibility() == View.VISIBLE) {
                flicking_text_child_list.setFlickering(false);
                flicking_text_child_list.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void handleError(int code) {
        if (code == MY_CHILD_GET) {
            //showTips("孩子列表获取失败");
            if (flicking_text_child_list.getVisibility() == View.GONE) {
                flicking_text_child_list.setVisibility(View.VISIBLE);
                flicking_text_child_list.setFlickering(false);
            }
        }
    }

    private MyChildGet childGet;
    private final int MY_CHILD_GET = 0x0011;

    @Override
    public void onRefresh() {
        if (childGet == null) {
            childGet = new MyChildGet();
            childGet.code(MY_CHILD_GET);
            childGet.handler(this);
            childGet.setPid(uid);
        }
        childGet.refresh();
        childGet.get();
    }

    @Override
    public void onLoadMore() {
    }
}
