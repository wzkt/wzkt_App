package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.post.QueryMonitorListPost;
import com.Alan.eva.result.MonitorListRes;
import com.Alan.eva.result.MonitorRes;
import com.Alan.eva.result.QueryMonitorRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.adapter.MonitorAdapter;
import com.Alan.eva.ui.core.AbsActivity;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;

public class MonHisListActivity extends AbsActivity implements PullLoadMoreRecyclerView.PullLoadMoreListener, IResultHandler {

    private RecyclerView recycler_alarm_list_list;
    private MonitorAdapter adapter;

    private final int QUERY_HIS = 0x0510;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_alarm_list;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_child_detail_title = (Toolbar) getView(R.id.tool_bar_title_common);
        tool_bar_child_detail_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_child_detail_title.setTitle("历史体温数据");
        setSupportActionBar(tool_bar_child_detail_title);
        tool_bar_child_detail_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_child_detail_title.setNavigationOnClickListener((View v) -> currFinish());
        tool_bar_child_detail_title.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.title_bar_alarm_title_add) {
               // Intent intent = getIntent(MonitorActivity.class);
               // startActivityForResult(intent, QUERY_HIS);
            }
            return true;
        });
        recycler_alarm_list_list = (RecyclerView) getView(R.id.recycler_alarm_list_list);
        LinearLayoutManager manager = new LinearLayoutManager(getCurrActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_alarm_list_list.setLayoutManager(manager);
    }
    String uid;
    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        String mac =  intent.getStringExtra("mac");
        String username =  intent.getStringExtra("username");

//        username = "张六";
//        uid="18811571528";
//        mac="94e36d9e4e30";

        refresh(uid,mac,username);

//        String rsssss = "{\"Message\":\"OK\",\"Code\":1,\"historical_data\":[{\"count\":1,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:22:55\",\"power\":49},{\"count\":2,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:21:43\",\"power\":49},{\"count\":3,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:21:10\",\"power\":49},{\"count\":4,\"room_temperature\":24,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:19:31\",\"power\":49},{\"count\":5,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:18:20\",\"power\":49},{\"count\":6,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:17:44\",\"power\":49},{\"count\":7,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:16:08\",\"power\":49},{\"count\":8,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:15:38\",\"power\":49},{\"count\":9,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:15:01\",\"power\":49},{\"count\":10,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:13:51\",\"power\":49},{\"count\":11,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:13:15\",\"power\":49},{\"count\":12,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:12:39\",\"power\":49},{\"count\":13,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:12:04\",\"power\":49},{\"count\":14,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:11:29\",\"power\":49},{\"count\":15,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:10:22\",\"power\":49},{\"count\":16,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:09:20\",\"power\":49},{\"count\":17,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:08:45\",\"power\":49},{\"count\":18,\"room_temperature\":26,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:08:09\",\"power\":49},{\"count\":19,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:07:33\",\"power\":49},{\"count\":20,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:07:02\",\"power\":49},{\"count\":21,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:04:44\",\"power\":50},{\"count\":22,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:04:10\",\"power\":50},{\"count\":23,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:03:33\",\"power\":50},{\"count\":24,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:02:57\",\"power\":50},{\"count\":25,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:02:24\",\"power\":50},{\"count\":26,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:01:15\",\"power\":50},{\"count\":27,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:00:42\",\"power\":50},{\"count\":28,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 11:00:04\",\"power\":50},{\"count\":29,\"room_temperature\":25,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 10:59:31\",\"power\":50},{\"count\":30,\"room_temperature\":22,\"body_temperature\":32.0,\"create_time\":\"2018-06-23 10:58:53\",\"power\":50},{\"count\":31,\"room_temperature\":35,\"body_temperature\":34.76,\"create_time\":\"2018-06-23 10:55:18\",\"power\":50},{\"count\":32,\"room_temperature\":35,\"body_temperature\":34.88,\"create_time\":\"2018-06-23 10:54:43\",\"power\":50},{\"count\":33,\"room_temperature\":35,\"body_temperature\":35.0,\"create_time\":\"2018-06-23 10:53:33\",\"power\":50},{\"count\":34,\"room_temperature\":35,\"body_temperature\":35.1,\"create_time\":\"2018-06-23 10:52:56\",\"power\":50},{\"count\":35,\"room_temperature\":35,\"body_temperature\":35.25,\"create_time\":\"2018-06-23 10:52:20\",\"power\":50},{\"count\":36,\"room_temperature\":36,\"body_temperature\":35.38,\"create_time\":\"2018-06-23 10:51:46\",\"power\":50},{\"count\":37,\"room_temperature\":36,\"body_temperature\":35.61,\"create_time\":\"2018-06-23 10:50:11\",\"power\":50}]}";
//        handleResult(rsssss,QUERY_HIS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_list_title, menu);
        return true;
    }


    private void refresh(String uid,String mac,String username) {
        QueryMonitorListPost post = new QueryMonitorListPost();
        post.code(QUERY_HIS);
        post.handler(this);
        post.setThermometerID(mac);
        post.setPid(uid);
        post.setUsername(username);
        post.post();
    }

    private void notifyAdapter(ArrayList<QueryMonitorRes> arrayList) {
        if (adapter == null) {
            adapter = new MonitorAdapter(arrayList, getCurrActivity());
            recycler_alarm_list_list.setAdapter(adapter);
        } else {
            adapter.setDataList(arrayList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == QUERY_HIS) {
                if (data != null) {
//                    boolean needRefresh = data.getBooleanExtra("refresh", false);
//                    if (needRefresh) {
//                        refresh();
//                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void handleStart(int code) {

    }

    @Override
    public void handleResult(String result, int code) {

        //LogUtil.inf("result========="+result);
        if (code == QUERY_HIS) {


            MonitorListRes res = Tools.json2Bean(result, MonitorListRes.class);
            if (res.isOk()) {

                ArrayList<QueryMonitorRes> listdata=   res.getHistorical_data();
                LogUtil.info("listdata:" + listdata.size());
                if(listdata!=null){
                    notifyAdapter(listdata);
                }

            }else {
                    showTips(res.msg());
            }
//                String temp = res.getData();
//                tv_monitor_temp_data.setText(temp);
//                tv_monitor_temp_tips.setText("体温监测中");
//            } else {
//                String msg = res.getMessage().msg();
//                tv_monitor_temp_data.setText("--");
//                tv_monitor_temp_tips.setText(msg);
//            }
        }
    }

    @Override
    public void handleFinish(int code) {

    }

    @Override
    public void handleError(int code) {
        showTips("网络异常");
    }

    @Override
    public void onRefresh() {
        //refresh(uid,mac);
    }

    @Override
    public void onLoadMore() {

    }
}
