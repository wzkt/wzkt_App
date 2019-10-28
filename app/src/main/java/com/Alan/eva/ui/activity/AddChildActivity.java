package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.post.CreateChildPost;
import com.Alan.eva.model.ChildSummary;
import com.Alan.eva.model.UserInfo;
import com.Alan.eva.result.CreateChildRes;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.tools.XUtils3ImageLoader;
import com.Alan.eva.ui.EApp;
import com.Alan.eva.ui.core.AbsActivity;
import com.Alan.eva.ui.dialog.AgeSelectorDialog;
import com.Alan.eva.ui.dialog.GenderDialog;
import com.Alan.eva.ui.dialog.HeightDialog;
import com.Alan.eva.ui.dialog.WeightDialog;
import com.Alan.eva.ui.widget.CircleImageView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.xutils.ex.DbException;

import java.util.ArrayList;

import static com.Alan.eva.R.id.btn_create_new_child_submit;
import static com.Alan.eva.ui.EApp.getApp;

/**
 * Created by CW on 2017/3/9.
 * 添加新孩子界面 11
 */
public class AddChildActivity extends AbsActivity implements View.OnClickListener, IResultHandler {
    private CircleImageView circle_create_new_child_portrait;
    private AppCompatEditText et_create_new_child_name;
    private AppCompatTextView tv_create_new_child_gender;
    private AppCompatTextView tv_create_new_child_age;
    private AppCompatTextView tv_create_new_child_height;
    private AppCompatTextView tv_create_new_child_weight;

    private AppCompatTextView tv_create_new_child_mac;

    private String localPic;//本地头像地址
    private String gender; //性别
    private String age; //年龄
    private String height; //身高
    private String weight; //体重
    private String name;
    private ImagePicker imagePicker;
    private final int PHOTO_PICKER = 0x0086;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_create_new_child;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_home_title = (Toolbar) getView(R.id.tool_bar_add_create_child);
        tool_bar_home_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_home_title.setTitle(R.string.add_new_child);
        setSupportActionBar(tool_bar_home_title);
        tool_bar_home_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_home_title.setNavigationOnClickListener((View v) -> currFinish());
        circle_create_new_child_portrait = (CircleImageView) getView(R.id.circle_create_new_child_portrait);
        et_create_new_child_name = (AppCompatEditText) getView(R.id.et_create_new_child_name);
        RelativeLayout rl_create_new_child_gender = (RelativeLayout) getView(R.id.rl_create_new_child_gender);
        RelativeLayout rl_create_new_child_age = (RelativeLayout) getView(R.id.rl_create_new_child_age);
        RelativeLayout rl_create_new_child_height = (RelativeLayout) getView(R.id.rl_create_new_child_height);
        RelativeLayout rl_create_new_child_weight = (RelativeLayout) getView(R.id.rl_create_new_child_weight);
        tv_create_new_child_gender = (AppCompatTextView) getView(R.id.tv_create_new_child_gender);
        tv_create_new_child_age = (AppCompatTextView) getView(R.id.tv_create_new_child_age);
        tv_create_new_child_height = (AppCompatTextView) getView(R.id.tv_create_new_child_height);
        tv_create_new_child_weight = (AppCompatTextView) getView(R.id.tv_create_new_child_weight);
        AppCompatButton btn_create_new_child_submit = (AppCompatButton) getView(R.id.btn_create_new_child_submit);
        tv_create_new_child_mac = (AppCompatTextView) getView(R.id.tv_create_new_child_mac);

        UserInfo myuserinfo  = EApp.getApp().getUserInfo(getCurrActivity());
        String pid =myuserinfo.getUid();
        String mac =myuserinfo.getMac();

        if(TextUtils.isEmpty(mac)){
            tv_create_new_child_mac.setText("未绑定蓝牙体温计、不能添加"+getString(R.string.child_haizi));
        }else{
            tv_create_new_child_mac.setText(mac);
        }

       // circle_create_new_child_portrait.setOnClickListener(this);
        rl_create_new_child_gender.setOnClickListener(this);
        rl_create_new_child_age.setOnClickListener(this);
        rl_create_new_child_height.setOnClickListener(this);
        rl_create_new_child_weight.setOnClickListener(this);
        btn_create_new_child_submit.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicker = ImagePicker.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.circle_create_new_child_portrait:
//                selectPhoto();
//                break;
            case R.id.rl_create_new_child_gender:
                showGender();
                break;
            case R.id.rl_create_new_child_age:
                showAgeSelector();
                break;
            case R.id.rl_create_new_child_height:
                showHeightSelector();
                break;
            case R.id.rl_create_new_child_weight:
                showWeightSelector();
                break;
            case btn_create_new_child_submit:
                submitCreate();
                break;
        }
    }


    /**
     * 去选择图片
     */
    private void selectPhoto() {
        imagePicker.setImageLoader(new XUtils3ImageLoader());
        imagePicker.setMultiMode(false);
        int focus = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_200);
        int output = getCurrActivity().getResources().getDimensionPixelOffset(R.dimen.size_120);
        imagePicker.setFocusWidth(focus);
        imagePicker.setFocusHeight(focus);
        imagePicker.setStyle(CropImageView.Style.CIRCLE);
        imagePicker.setOutPutX(output);
        imagePicker.setOutPutY(output);
        Intent intent = getIntent(ImageGridActivity.class);
        startActivityForResult(intent, PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == PHOTO_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (!Tools.isListEmpty(images)) {
                    ImageItem item = images.get(0);
                    localPic = item.path;
                  //  Tools.display(circle_create_new_child_portrait, localPic);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private GenderDialog genderDialog;

    /**
     * 显示性别选择对话框
     */
    private void showGender() {
        if (genderDialog == null) {
            genderDialog = new GenderDialog(getCurrActivity());
        }
        genderDialog.setOnMale(v -> {
            genderDialog.dismiss();
            gender = "1";
            tv_create_new_child_gender.setText("男");
        });
        genderDialog.setOnFemale(v -> {
            genderDialog.dismiss();
            gender = "2";
            tv_create_new_child_gender.setText("女");
        });
        genderDialog.create();
        genderDialog.show();
    }

    private AgeSelectorDialog ageSelectorDialog;

    /**
     * 显示选择年龄对话框
     */
    private void showAgeSelector() {
        if (ageSelectorDialog == null) {
            ageSelectorDialog = new AgeSelectorDialog(getCurrActivity());
            ageSelectorDialog.setOnAgeOk(v -> {
                ageSelectorDialog.dismiss();
                age = ageSelectorDialog.getAge();
                tv_create_new_child_age.setText(String.valueOf(age + "岁"));
            });
            ageSelectorDialog.create(Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        ageSelectorDialog.show();
    }

    private HeightDialog heightDialog;

    /**
     * 显示身高输入
     */
    private void showHeightSelector() {
        if (heightDialog == null) {
            heightDialog = new HeightDialog(getCurrActivity());
            heightDialog.setOnOK(v -> {
                heightDialog.dismiss();
                height = heightDialog.getHeight();
                tv_create_new_child_height.setText(String.valueOf(height + "cm"));
            });
            heightDialog.create(Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        heightDialog.show();
    }

    private WeightDialog weightDialog;

    /**
     * 显示体重选择对话框
     */
    private void showWeightSelector() {
        if (weightDialog == null) {
            weightDialog = new WeightDialog(getCurrActivity());
            weightDialog.setOnOK(v -> {
                weightDialog.dismiss();
                weight = weightDialog.getWeight();
                tv_create_new_child_weight.setText(String.valueOf(weight + "kg"));
            });
            weightDialog.create(Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        weightDialog.show();
    }

    private final int CHILD_CREATE = 0x0032;

    /**
     * 创建孩子
     */
    private void submitCreate() {

        UserInfo myuserinfo  = EApp.getApp().getUserInfo(getCurrActivity());
        String pid =myuserinfo.getUid();
        String mac =myuserinfo.getMac();

        if (TextUtils.isEmpty(mac)) {
            showTips("请先连接绑定蓝牙体温计");
            return;
        }
        name = et_create_new_child_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showTips("请输入"+getString(R.string.child_haizi)+"名称");
            return;
        }
        if (TextUtils.isEmpty(gender)) {
            showTips("请选择"+getString(R.string.child_haizi)+"性别");
            return;
        }
        if (TextUtils.isEmpty(age)) {
            showTips("请选择"+getString(R.string.child_haizi)+"年龄");
            return;
        }
        if (TextUtils.isEmpty(height)) {
            showTips("请选择"+getString(R.string.child_haizi)+"身高");
            return;
        }
        if (TextUtils.isEmpty(weight)) {
            showTips("请选择"+getString(R.string.child_haizi)+"体重");
            return;
        }

        CreateChildPost post = new CreateChildPost();
        post.code(CHILD_CREATE);
        post.handler(this);
        post.setPid(pid);
        //post.setPortrait(localPic);
        post.setThermometer_add(mac);
        post.setName(name);
        post.setGender(gender);
        post.setAge(age);
        post.setHeight(height);
        post.setWeight(weight);
        post.post();


        ChildSummary dbchild = new ChildSummary();
        dbchild.setName(name);
        dbchild.setAge(age);
        dbchild.setHeight(height);
        dbchild.setWeight(weight);
        dbchild.setMac(mac);
        dbchild.setGender(gender);
        saveorupdate(dbchild);
    }

    @Override
    public void handleStart(int code) {
        if (code == CHILD_CREATE) {
            loading("正在创建"+getString(R.string.child_haizi)+"...");
        }
    }

    @Override
    public void handleResult(String result, int code) {
        if (code == CHILD_CREATE) {
            CreateChildRes res = Tools.json2Bean(result, CreateChildRes.class);
            if (res.isOk()) {
                try{
                    UserInfo userInfo = getApp().getUserInfo(this);
                    if(userInfo==null){
                        userInfo = new UserInfo();
                    }
                    userInfo.setUsername(name);
                    getApp().setUserInfo(userInfo, this);
                }catch (Exception e){
                    e.printStackTrace();
                }
                showTips("创建成功");
                currFinish();

            }else{
                showTips(res.msg());
            }
        }
    }

    @Override
    public void handleFinish(int code) {
        if (code == CHILD_CREATE) {
            hide();
        }
    }

    @Override
    public void handleError(int code) {
        if (code == CHILD_CREATE) {
            showTips("创建失败，请重试");
        }
    }


    private void saveorupdate(ChildSummary child){
        ChildSummary first = null;
        try {
            first = EApp.getApp().db.findFirst(ChildSummary.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(first!=null){
            child.setId(first.getId());
        }
        try {
            EApp.getApp().db.saveOrUpdate(child);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }






}
