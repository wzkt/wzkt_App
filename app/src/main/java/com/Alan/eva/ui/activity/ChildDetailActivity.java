package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.ChildMonitorGet;
import com.Alan.eva.http.get.DeleteMyChildGet;
import com.Alan.eva.http.get.ModifyCidGet;
import com.Alan.eva.http.post.AddChildMonitorPost;
import com.Alan.eva.model.ChildMonitorData;
import com.Alan.eva.model.ChildSummary;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.result.AddMonitorRes;
import com.Alan.eva.result.ChildMonitorRes;
import com.Alan.eva.result.Res;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.adapter.ChildMonitorAdapter;
import com.Alan.eva.ui.core.AbsActivity;
import com.Alan.eva.ui.dialog.OperateDialog;
import com.Alan.eva.ui.dialog.SingleInputDialog;
import com.Alan.eva.ui.widget.CircleImageView;
import com.Alan.eva.ui.widget.StretchableList;

import org.xutils.ex.DbException;

import java.util.ArrayList;

import static com.Alan.eva.ui.EApp.getApp;

/**
 * Created by CW on 2017/3/21.
 * 孩子详情
 */
public class ChildDetailActivity extends AbsActivity implements IResultHandler, View.OnClickListener {
    private CircleImageView circle_child_detail_portrait;
    private AppCompatTextView tv_child_detail_name;
    private AppCompatTextView tv_child_detail_gender;
    private AppCompatTextView tv_child_detail_age;
    private AppCompatTextView tv_child_detail_height;
    private AppCompatTextView tv_child_detail_weight;
    private AppCompatCheckBox cb_child_detail_set_default;
    private RelativeLayout rl_child_detail_monitor_title;
    private StretchableList stretch_list_child_bind_uses;
    private AppCompatTextView tv_child_detail_no_monitor;
    private AppCompatButton btn_child_detail_start_monitor;
    private AppCompatButton btn_child_detail_delete;
    private String cid;
    private String pic;
    private String phone;
    private String childName;
    private final int CHILD_MONITOR = 0x00044;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_child_detail;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_child_detail_title = (Toolbar) getView(R.id.tool_bar_child_detail_title);
        tool_bar_child_detail_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        UserInfo userInfo = getApp().getUserInfo(this);

        tool_bar_child_detail_title.setTitle(R.string.child_detail);
        AppCompatTextView tv_child_mac= (AppCompatTextView) getView(R.id.tv_child_mac);

        if(userInfo!=null){
            if(userInfo.getMac()!=null){
                tv_child_mac.setText(userInfo.getMac());
            }
        }

        setSupportActionBar(tool_bar_child_detail_title);
        tool_bar_child_detail_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_child_detail_title.setNavigationOnClickListener((View v) -> currFinish());
        circle_child_detail_portrait = (CircleImageView) getView(R.id.circle_child_detail_portrait);
        circle_child_detail_portrait.setClickable(false);
        tv_child_detail_name = (AppCompatTextView) getView(R.id.tv_child_detail_name);
        tv_child_detail_gender = (AppCompatTextView) getView(R.id.tv_child_detail_gender);
        tv_child_detail_age = (AppCompatTextView) getView(R.id.tv_child_detail_age);
        tv_child_detail_height = (AppCompatTextView) getView(R.id.tv_child_detail_height);
        tv_child_detail_weight = (AppCompatTextView) getView(R.id.tv_child_detail_weight);
        cb_child_detail_set_default = (AppCompatCheckBox) getView(R.id.cb_child_detail_set_default);
        rl_child_detail_monitor_title = (RelativeLayout) getView(R.id.rl_child_detail_monitor_title);

        btn_child_detail_start_monitor = (AppCompatButton) getView(R.id.btn_child_detail_start_monitor);
        AppCompatButton btn_child_detail_health_detail = (AppCompatButton) getView(R.id.btn_child_detail_health_detail);
        stretch_list_child_bind_uses = (StretchableList) getView(R.id.stretch_list_child_bind_uses);
        AppCompatTextView tv_child_detail_add_monitor = (AppCompatTextView) getView(R.id.tv_child_detail_add_monitor);
        tv_child_detail_no_monitor = (AppCompatTextView) getView(R.id.tv_child_detail_no_monitor);
        tv_child_detail_no_monitor.setVisibility(View.GONE);
        btn_child_detail_delete = (AppCompatButton) getView(R.id.btn_child_detail_delete);
       // circle_child_detail_portrait.setOnClickListener(this);
        btn_child_detail_health_detail.setOnClickListener(this);
        tv_child_detail_add_monitor.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        cid = intent.getStringExtra("cid");
        pic = intent.getStringExtra("pic");
        childName = intent.getStringExtra("name");
        String gender = intent.getStringExtra("gender");
        String age = intent.getStringExtra("age");
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");
        boolean isOwner = intent.getBooleanExtra("owner", false);
        boolean isDefault = intent.getBooleanExtra("default", false);

        //Tools.display(circle_child_detail_portrait, pic);
        tv_child_detail_name.setText(childName);
        tv_child_detail_gender.setText(TextUtils.equals(gender, "1") ? "男孩" : "女孩");
        tv_child_detail_age.setText(String.valueOf(age + "岁"));
        tv_child_detail_height.setText(String.valueOf(height + "cm"));
        tv_child_detail_weight.setText(String.valueOf(weight + "kg"));
        if (isOwner) {  //如果是自己的孩子，则可以本地监测、添加监护人、删除孩子
            btn_child_detail_start_monitor.setText("本地监测");
            btn_child_detail_start_monitor.setOnClickListener(new OnLocalMonitor());
            cb_child_detail_set_default.setVisibility(View.VISIBLE);
            if (isDefault) { //如果是默认的则不能点击添加默认
                cb_child_detail_set_default.setChecked(true);
                cb_child_detail_set_default.setEnabled(false);
            } else {//如果不是默认的则可以点击
                cb_child_detail_set_default.setEnabled(true);
                cb_child_detail_set_default.setOnCheckedChangeListener(new OnDefaultChange());
            }
            rl_child_detail_monitor_title.setVisibility(View.VISIBLE);
            stretch_list_child_bind_uses.setVisibility(View.VISIBLE);
            btn_child_detail_delete.setVisibility(View.VISIBLE);
            btn_child_detail_delete.setOnClickListener(this);
            getMonitor();
        } else {  //如果不是自己的孩子则不能查看监护人列表、添加监护人、删除孩子，并且只能远程监测
            btn_child_detail_start_monitor.setText("远程监测");
            btn_child_detail_start_monitor.setOnClickListener(v -> {
                Intent monitor = getIntent(MonitorActivity.class);
                monitor.putExtra("cid", cid);
                startActivity(monitor);
            });
            cb_child_detail_set_default.setVisibility(View.GONE);
            rl_child_detail_monitor_title.setVisibility(View.GONE);
            stretch_list_child_bind_uses.setVisibility(View.GONE);
            btn_child_detail_delete.setOnClickListener(this);
            //btn_child_detail_delete.setVisibility(View.GONE);
        }
    }

    private class OnDefaultChange implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                cb_child_detail_set_default.setEnabled(false);
                UserInfo info = getApp().getUserInfo(getCurrActivity());
                info.setCid(cid);
                String uid = info.getUid();
                getApp().setUserInfo(info, getCurrActivity());
                updateCid(uid);
            }
        }
    }

    private final int UPDATE_CID = 0x0089;

    private void updateCid(String uid) {
        ModifyCidGet get = new ModifyCidGet();
        get.setUid(uid);
        get.setCid(cid);
        get.code(UPDATE_CID);
        get.handler(this);
        get.get();
    }

    private void getMonitor() {
        monitorList = new ArrayList<>();
        UserInfo info = getApp().getUserInfo(getCurrActivity());
        if (info == null) {
            return;
        }
        String uid = info.getUid();
        ChildMonitorGet get = new ChildMonitorGet();
        get.code(CHILD_MONITOR);
        get.handler(this);
        get.setPid(uid);
        get.setCid(cid);
        get.get();
    }

    @Override
    public void handleStart(int code) {
        if (code == CHILD_MONITOR) {
            LogUtil.info("获取监护人列表开始");
        } else if (code == ADD_MONITOR) {
            LogUtil.info("添加监护人开始");
        } else if (code == UPDATE_CID) {
            LogUtil.info("修改默认孩子开始");
        } else if (code == CHILD_DELETE) {
            loading("正在删除...");
        }
    }

    private ArrayList<ChildMonitorData> monitorList;

    private ChildMonitorAdapter monitorAdapter;

    @Override
    public void handleResult(String result, int code) {
        if (code == CHILD_MONITOR) {
            ChildMonitorRes res = Tools.json2Bean(result, ChildMonitorRes.class);
            if (res.isOk()) {
                monitorList = res.getData();
                notifyMonitor(monitorList);
                tv_child_detail_no_monitor.setVisibility(View.GONE);
            } else {
                monitorList = new ArrayList<>();
                monitorList.clear();
                notifyMonitor(monitorList);
                tv_child_detail_no_monitor.setVisibility(View.VISIBLE);
            }
        } else if (code == ADD_MONITOR) {
            AddMonitorRes res = Tools.json2Bean(result, AddMonitorRes.class);
            showTips(res.msg());
            if (res.isOk()) {
                String bid = res.getData();
                ChildMonitorData data = new ChildMonitorData();
                data.setPhone(phone);
                data.setBid(bid);
                monitorList.add(data);
                notifyMonitor(monitorList);
            }
        } else if (code == UPDATE_CID) {
            Res res = Tools.json2Bean(result, Res.class);
            showTips(res.msg());
            if (!res.isOk()) {
                cb_child_detail_set_default.setEnabled(true);
                cb_child_detail_set_default.setChecked(false);
            }
        } else if (code == CHILD_DELETE) {
            Res res = Tools.json2Bean(result, Res.class);
            showTips(res.msg());
            if (res.isOk()) {
                UserInfo info = EApp.getApp().getUserInfo(getCurrActivity());
                String childId = info.getCid();
                if (TextUtils.equals(cid, childId)) { //如果删除的孩子是默认孩子，那就修改修改用户信息
                    info.setCid("");
                    EApp.getApp().setUserInfo(info, getCurrActivity());
                }
                currFinish();
            }
        }
    }

    private void notifyMonitor(ArrayList<ChildMonitorData> list) {
        if (monitorAdapter == null) {
            monitorAdapter = new ChildMonitorAdapter(monitorList, getCurrActivity());
            stretch_list_child_bind_uses.setAdapter(monitorAdapter);
        } else {
            monitorAdapter.setDataList(list);
            monitorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void handleFinish(int code) {
        if (code == CHILD_MONITOR) {
            LogUtil.info("获取监护人列表结束");
        } else if (code == ADD_MONITOR) {
            LogUtil.info("添加监护人结束");
        } else if (code == UPDATE_CID) {
            LogUtil.info("修改默认孩子结束");
        } else if (code == CHILD_DELETE) {
            hide();
        }
    }

    @Override
    public void handleError(int code) {
        if (code == CHILD_MONITOR) {
            LogUtil.info("获取监护人列表错误");
        } else if (code == ADD_MONITOR) {
            LogUtil.info("添加监护人错误");
        } else if (code == UPDATE_CID) {
            LogUtil.info("修改默认孩子错误");
        } else if (code == CHILD_DELETE) {
            LogUtil.info("删除孩子错误");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_child_detail_add_monitor: //添加监护人
                showAddMonitor();
                break;
            case R.id.btn_child_detail_start_monitor:
                showLocalMonitor();
                break;
//            case R.id.circle_child_detail_portrait:
//                Intent intent = getIntent(BigPicActivity.class);
//                intent.putExtra("url", pic);
//                startActivity(intent);
//                break;
            case R.id.btn_child_detail_health_detail:
                Intent health = getIntent(HealthDetailActivity.class);
                health.putExtra("cid", cid);
                startActivity(health);
                break;
            case R.id.btn_child_detail_delete:
                deleteChild();
                break;
        }
    }

    /**
     * 显示删除孩子对话框
     */
    private void deleteChild() {
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setContent(""+getString(R.string.child_dele_txt));
        dialog.setOk("删除");
        dialog.setOnOk(v -> {
            dialog.dismiss();
            delete();
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private final int CHILD_DELETE = 0x0065;

    private void delete() {
        delqueryChildSummary();
        DeleteMyChildGet get = new DeleteMyChildGet();
        get.code(CHILD_DELETE);
        get.handler(this);
        UserInfo info = EApp.getApp().getUserInfo(getCurrActivity());
        if (info == null) {
            showTips("您的登录信息有误，请重新的登录");
            return;
        }
        String uid = info.getUid();
        get.setCid(cid);
        get.setUid(uid);
        get.get();
        this.finish();
    }


    private void delqueryChildSummary(){
        try {
            ChildSummary first = null;
            try {
                first = EApp.getApp().db.findFirst(ChildSummary.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            EApp.getApp().db.delete(first);
        }catch (Exception e){
        }
    }


    /**
     * 添加本地监测按钮监听事件
     */
    private class OnLocalMonitor implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            showLocalMonitor();
        }
    }

    /**
     * 显示本地监测对话框，确定是否去监测该孩子
     */
    private void showLocalMonitor() {
        OperateDialog dialog = new OperateDialog(getCurrActivity());
        dialog.setOk("开始监测");
        dialog.setContent("是否开始对" + childName + "进行体温监测？");
        dialog.setOnOk(v -> {
            dialog.dismiss();
            /***************************保存孩子id，以便下次直接监测******************************/
            UserInfo info = getApp().getUserInfo(getCurrActivity());
            String childId = info.getCid();
            if (!TextUtils.equals(childId, cid)) {
                info.setCid(cid);
                String uid = info.getUid();
                getApp().setUserInfo(info, getCurrActivity());
                ModifyCidGet get = new ModifyCidGet();
                get.setUid(uid);
                get.setCid(cid);
                get.get();
            }
            /***************************        end    *******************************************/
            Intent intent = getIntent(HomeActivity.class);
            intent.putExtra("cid", cid);
            startActivity(intent);
            currFinish();
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private final int ADD_MONITOR = 0x0046;

    private void showAddMonitor() {
        UserInfo info = getApp().getUserInfo(getCurrActivity());
        if (info == null) {
            showTips("您的登录信息有误，请重新登录");
            return;
        }
        String uid = info.getUid();
        SingleInputDialog inputDialog = new SingleInputDialog(getCurrActivity());
        inputDialog.setTitle("请输入监护人手机号");
        inputDialog.setContentHint("请输入手机号");
        inputDialog.setOnOk(v -> {
            phone = inputDialog.getContent();
            if (TextUtils.isEmpty(phone)) {
                inputDialog.errorAlert("请输入手机号");
                return;
            }
            inputDialog.dismiss();
            addMonitor(uid, phone);
        });
        int wid = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
        inputDialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputDialog.show();
    }

    private void addMonitor(String uid, String phone) {
        AddChildMonitorPost post = new AddChildMonitorPost();
        post.code(ADD_MONITOR);
        post.handler(this);
        post.setPhone(phone);
        post.setPid(uid);
        post.setCid(cid);
        post.post();
    }
}
