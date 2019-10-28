package com.Alan.eva.ui.holder;

import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.Alan.eva.R;
import com.Alan.eva.model.ChildModel;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.activity.ChildDetailActivity;
import com.Alan.eva.ui.core.AbsViewHolder;
import com.Alan.eva.ui.widget.CircleImageView;

/**
 * Created by CW on 2017/3/9.
 * 我的孩子列表holder
 */
public class MyChildHolder extends AbsViewHolder {
    private CircleImageView circle_child_portrait;
    private LinearLayout ll_child_list_info_holder;
    private AppCompatTextView tv_item_my_child_name;
    private AppCompatTextView tv_item_my_child_age;
    private AppCompatTextView tv_item_my_child_height;
    private AppCompatTextView tv_item_my_child_weight;
    private AppCompatTextView tv_child_list_owner;
    private AppCompatTextView tv_child_list_default;


    public MyChildHolder(View itemView) {
        super(itemView);
        circle_child_portrait = (CircleImageView) itemView.findViewById(R.id.circle_child_portrait);
        ll_child_list_info_holder = (LinearLayout) itemView.findViewById(R.id.ll_child_list_info_holder);
        tv_item_my_child_name = (AppCompatTextView) itemView.findViewById(R.id.tv_item_child_list_name);
        tv_item_my_child_age = (AppCompatTextView) itemView.findViewById(R.id.tv_item_child_list_age);
        tv_item_my_child_height = (AppCompatTextView) itemView.findViewById(R.id.tv_item_child_list_height);
        tv_item_my_child_weight = (AppCompatTextView) itemView.findViewById(R.id.tv_item_child_list_weight);
        tv_child_list_owner = (AppCompatTextView) itemView.findViewById(R.id.tv_child_list_owner);
        tv_child_list_default = (AppCompatTextView) itemView.findViewById(R.id.tv_child_list_default);
    }

    public void bindData(ChildModel model) {
        String name = model.getName();
        String gender = model.getGender();
        String age = model.getAge();
        String height = model.getHeight();
        String weight = model.getWeight();
        String portrait = model.getPortrait();
        boolean isOwner = model.isOwner();
        boolean isDefault = model.isDefault();
        tv_item_my_child_name.setText(name);
        tv_item_my_child_name.setSelected(TextUtils.equals(gender, "1"));
        if(age!=null)tv_item_my_child_age.setText(String.format("%s岁", age));
        if(height!=null) tv_item_my_child_height.setText(String.format("%scm", height));
        if(weight!=null) tv_item_my_child_weight.setText(String.format("%skg", weight));

        circle_child_portrait.setClickable(false);
        //if(!TextUtils.isEmpty(portrait))Tools.display(circle_child_portrait, portrait);

        if (isOwner) {
            tv_child_list_owner.setText("亲");
            tv_child_list_default.setVisibility(isDefault ? View.VISIBLE : View.GONE);
        } else {
            tv_child_list_owner.setVisibility(View.GONE);
            tv_child_list_default.setVisibility(View.GONE);
        }
        ll_child_list_info_holder.setOnClickListener(new OnDetail(model));
    }

    private class OnDetail implements View.OnClickListener {
        private ChildModel model;

        OnDetail(ChildModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            Intent intent = getIntent(ChildDetailActivity.class);
            String cid = model.getCid();
            String pic = model.getPortrait();
            String name = model.getName();
            String gender = model.getGender();
            String age = model.getAge();
            String height = model.getHeight();
            String weight = model.getWeight();
            boolean isOwner = model.isOwner();
            boolean isDefault = model.isDefault();

            intent.putExtra("cid", cid);
            intent.putExtra("pic", pic);
            intent.putExtra("name", name);
            intent.putExtra("gender", gender);
            intent.putExtra("age", age);
            intent.putExtra("height", height);
            intent.putExtra("weight", weight);
            intent.putExtra("owner", isOwner);
            intent.putExtra("default", isDefault);
            getActivity().startActivity(intent);
        }
    }
}
