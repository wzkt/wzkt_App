package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.Alan.eva.R;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.SPUtils;
import com.Alan.eva.ui.core.AbsDialogCreator;
import com.Alan.eva.ui.widget.PickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CW on 2017/3/10.
 * 性别选择对话框
 */
public class KickDialog extends AbsDialogCreator {
    private TextView   tvKickCancel, tvKickSetting, tvShowTemp, tvAlarmTemp;
    private PickerView integerPic, decimalPic, setTimePic;
    private boolean isShowData = true;
    private boolean isRegister = false;
    private int isPlay = 0;// 为0时，高温报警执行，为1时，不执行
    private int isKick = 0;// 为0时，蹬被报警执行，为1时，不执行

    private final int TEMP = 0;// 为0时，表示选择高温报警温度
    private final int KICK = 1;// 为1时，表示选择蹬被报警温度
    private String setWarnOne;// 用户设置的报警值得整数部分
    private String setWarnTwo;// 用户设置的报警值得小数部分
    private String setKickTime;// 用户设置的蹬被报警值
    private int recordTemp = 0;// 用于判断烧的稳定性
    private SharedPreferences pre_tempValues, pre_kickValues;
    private PopupWindow setTimePopup;
    private PopupWindow setKickTimePopu;
    private String kickwendu = "kickwendu";// 设定的蹬被温度key

    public KickDialog(Context context) {
        super(context);
    }

    @Override
    public int getRootViewId() {
        return R.layout.setkucktime_popu;
    }

    @Override
    public void findView(View rootView) {
       // AppCompatTextView tv_gender_choose_female = (AppCompatTextView) rootView.findViewById(R.id.tv_gender_choose_female);
        //AppCompatTextView tv_gender_choose_male = (AppCompatTextView) rootView.findViewById(R.id.tv_gender_choose_male);
        startKickSettimePopu(rootView);
    }

    private void startKickSettimePopu(View view) {
        //View view = LayoutInflater.from(getContext()).inflate(R.layout.setkucktime_popu, null);
        tvKickCancel = (TextView) view.findViewById(R.id.firstF_tv_kickCancel);
        tvKickSetting = (TextView) view.findViewById(R.id.firstF_tv_kickSetting);
        setTimePic = (PickerView) view.findViewById(R.id.fisrstF_pick_settime);
        List<String> kickTime = new ArrayList<String>();
        for (int i = 10; i <= 40; i++) {
            kickTime.add("" + i);
        }
        setTimePic.setData(kickTime);
        pre_kickValues = getContext().getSharedPreferences(kickwendu, Context.MODE_PRIVATE);
        int values = pre_kickValues.getInt(kickwendu, 25);
        if (values == 25) {
            setKickTime = "25";
        }
        setKickTime = String.valueOf(values);
        setTimePic.setSelected(kickTime.indexOf(String.valueOf(values)));
        setTimePic.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                // TODO Auto-generated method stub
                setKickTime = text;
            }
        });
        tvKickCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });
        tvKickSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SharedPreferences pres = getContext().getSharedPreferences("kickalarm", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pres.edit();
                editor.putBoolean("kickIsSwitch", true);
                editor.commit();
                int values = Integer.valueOf(setKickTime);
                upDataKick(values);
                dismiss();
            }
        });

    }

    private void upDataKick(int values) {
        pre_kickValues = getContext().getSharedPreferences(kickwendu, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre_kickValues.edit();
        editor.putInt(kickwendu, values);
        editor.commit();
    }
}
