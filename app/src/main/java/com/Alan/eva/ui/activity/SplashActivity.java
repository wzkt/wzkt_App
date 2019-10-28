package com.Alan.eva.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.Alan.eva.R;
import com.Alan.eva.ui.core.AbsActivity;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AbsActivity {
    private ViewPager vp_user_guide_pager;
    private String[] arrImageNames = null;
    private List<View> totalList = new ArrayList<>();
    private RadioButton[] arrRadioButton = null;
    private RadioGroup mRadioGroup;

    @Override
    public Activity getCurrActivity() {
        return this;
    }

    @Override
    public int getRootViewId() {
        return R.layout.ac_user_guide;
    }

    @Override
    public void findView(View rootView) {
        vp_user_guide_pager = (ViewPager) getView(R.id.vp_user_guide_pager);
        mRadioGroup = (RadioGroup) getView(R.id.rg_guide_indicator);
        arrImageNames = getResources().getStringArray(R.array.arrImageNames);
        initView();
        initDots();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initDots() {
        arrRadioButton = new RadioButton[arrImageNames.length];
        for (int i = 0; i < arrImageNames.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setButtonDrawable(R.drawable.dots);
            mRadioGroup.addView(radioButton);
            arrRadioButton[i] = radioButton;
        }
        arrRadioButton[0].setChecked(true);
        mRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            for (int j = 0; j < arrImageNames.length; j++) {
                if (arrRadioButton[j].getId() == i) {
                    vp_user_guide_pager.setCurrentItem(j);
                }
            }
        });
    }

    private void initView() {
        for (int i = 0; i < arrImageNames.length - 1; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ScaleType.FIT_XY);
            // 给图片赋值
            imageView.setImageResource(getResources().getIdentifier(arrImageNames[i], "drawable", getPackageName()));
            totalList.add(imageView);
        }
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.welcom_last, null);
        totalList.add(view);
        TextView tvStart = (TextView) view.findViewById(R.id.wl_tv_start);
        tvStart.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
        vp_user_guide_pager.setAdapter(new MyPagerAdapter(totalList));
        vp_user_guide_pager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                arrRadioButton[arg0].setChecked(true);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    class MyPagerAdapter extends PagerAdapter {
        private List<View> list = null;

        MyPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }

}
