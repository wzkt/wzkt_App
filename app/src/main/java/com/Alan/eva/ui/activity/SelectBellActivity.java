package com.Alan.eva.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;

import com.Alan.eva.R;
import com.Alan.eva.model.SysBellBean;
import com.Alan.eva.tools.MediaManager;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.adapter.SysBellAdapter;
import com.Alan.eva.ui.core.AbsActivity;

import java.util.ArrayList;

public class SelectBellActivity extends AbsActivity {

    private RecyclerView recycle_select_bell_list;
    private ArrayList<SysBellBean> bellList;
    private String name;
    private String path;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_choose_bell_ring;
    }

    @Override
    public void findView(View rootView) {
        Toolbar tool_bar_child_detail_title = (Toolbar) getView(R.id.tool_bar_title_common);
        tool_bar_child_detail_title.setTitleTextColor(Tools.getColor(getCurrActivity(), R.color.white));
        tool_bar_child_detail_title.setTitle("系统铃声");
        setSupportActionBar(tool_bar_child_detail_title);
        tool_bar_child_detail_title.setNavigationIcon(R.mipmap.ic_flag_back);
        tool_bar_child_detail_title.setNavigationOnClickListener((View v) -> currFinish());
        tool_bar_child_detail_title.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.title_bar_sys_bells_ok) {
                if (!TextUtils.isEmpty(path)) {
                    Intent intent = new Intent();
                    intent.putExtra("name", name);
                    intent.putExtra("path", path);
                    setResult(RESULT_OK, intent);
                }
                currFinish();
            }
            return true;
        });
        recycle_select_bell_list = (RecyclerView) getView(R.id.recycle_select_bell_list);
        LinearLayoutManager manager = new LinearLayoutManager(getCurrActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_select_bell_list.setLayoutManager(manager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findBell();
        SysBellAdapter adapter = new SysBellAdapter(bellList, this);
        recycle_select_bell_list.setAdapter(adapter);
    }

    private void findBell() {
        bellList = new ArrayList<>();
        // 系统铃声查询
        Cursor bellCursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null, null);
        if (bellCursor != null) {
            int count = bellCursor.getCount();
            if (count > 0) {
                while (bellCursor.moveToNext()) {
                    SysBellBean bellBean = new SysBellBean();
                    String bellName = bellCursor.getString(bellCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                    String bellPath = bellCursor.getString(bellCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                    bellBean.setName(bellName);
                    bellBean.setPath(bellPath);
                    bellBean.setCheck(false);
                    bellList.add(bellBean);
                }
            }
            bellCursor.close();
        } else {
            bellList.clear();
        }
    }

    private MediaManager mediaManager;

    // 播放选择的铃声
    public void playMusic(String name, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        this.name = name;
        this.path = path;
        if (mediaManager == null) {
            mediaManager = new MediaManager();
        }
        mediaManager.startPlay(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sys_bells_title, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaManager != null) {
            mediaManager.stopPlay();
        }
    }
}
