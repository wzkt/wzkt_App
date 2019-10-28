package com.Alan.eva.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.Alan.eva.R;
import com.Alan.eva.model.ChildModel;
import com.Alan.eva.ui.activity.ChildListActivity;
import com.Alan.eva.ui.core.AbsRAdapter;
import com.Alan.eva.ui.holder.MyChildHolder;

import java.util.ArrayList;

/**
 * Created by CW on 2017/3/9.
 * 我的孩子列表适配器
 */
public class MyChildAdapter extends AbsRAdapter<ChildModel, MyChildHolder> {

    public MyChildAdapter(ArrayList<ChildModel> dataList, ChildListActivity activity) {
        super(dataList, activity);
    }

    @Override
    public MyChildHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = getInflater().inflate(R.layout.item_my_child_list, parent, false);
        MyChildHolder holder = new MyChildHolder(root);
        holder.setActivity(getActivity());
        return holder;
    }

    @Override
    public void onBindViewHolder(MyChildHolder holder, int position) {
        ChildModel model = getDataList().get(position);
        holder.bindData(model);
    }
}
