package com.Alan.eva.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
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
public class PopuDialog extends AbsDialogCreator {
    private TextView tvCancel, tvSetting, tvKickCancel, tvKickSetting, tvShowTemp, tvAlarmTemp;
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


    private View.OnClickListener onPopuOk;

    public PopuDialog(Context context) {
        super(context);
    }
    public void setOnPopuOk(View.OnClickListener onAgeOk) {
        this.onPopuOk = onAgeOk;
    }

    @Override
    public int getRootViewId() {
        return R.layout.settime_popu;
    }

    @Override
    public void findView(View rootView) {
       // AppCompatTextView tv_gender_choose_female = (AppCompatTextView) rootView.findViewById(R.id.tv_gender_choose_female);
        //AppCompatTextView tv_gender_choose_male = (AppCompatTextView) rootView.findViewById(R.id.tv_gender_choose_male);
        startSettimePopu(rootView);
    }
    private void startSettimePopu(View view) {
        //View view = LayoutInflater.from(getCurrActivity()).inflate(R.layout.settime_popu, null);
        tvCancel = (TextView) view.findViewById(R.id.firstF_tv_cancel);
        tvSetting = (TextView) view.findViewById(R.id.firstF_tv_setting);
        integerPic = (PickerView) view.findViewById(R.id.fisrstF_temp_integer);
        decimalPic = (PickerView) view.findViewById(R.id.fisrstF_temp_decimal);
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int i = 32; i <= 41; i++) {
            data.add("" + i);
        }
        for (int i = 0; i <= 9; i++) {
            seconds.add("." + i);
        }
        integerPic.setData(data);
        integerPic.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                // TODO Auto-generated method stub
                setWarnOne = text;

            }
        });
        decimalPic.setData(seconds);
        decimalPic.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                // TODO Auto-generated method stub
                setWarnTwo = text;
            }
        });
        pre_tempValues = getContext().getSharedPreferences("sp_da", Context.MODE_PRIVATE);
        float h = pre_tempValues.getFloat("temperature", (float) 0);
        if (h == 0.0) {
            setWarnOne = "37";
            setWarnTwo = ".5";

        } else {
            String tempA = String.valueOf(h);// 得到设定的string值
            String tempZ = tempA.substring(0, 2);
            String tempX = tempA.substring(2, 4);
            setWarnOne = tempZ;
            setWarnTwo = tempX;
            integerPic.setSelected(data.indexOf(tempZ));
            decimalPic.setSelected(seconds.indexOf(tempX));

        }
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(setTimePopup!=null)setTimePopup.dismiss();
                dismiss();
            }
        });
        tvSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SPUtils.put(getContext(), "TempIsSwitch", true);
                float values = Float.valueOf(setWarnOne + setWarnTwo);
                upDataTmep(values);
                pre_tempValues = getContext().getSharedPreferences("sp_da", Context.MODE_PRIVATE);
                float h = pre_tempValues.getFloat("temperature", (float) 0);
               if(tvAlarmTemp!=null)tvAlarmTemp.setText("" + h);
                if(setTimePopup!=null)setTimePopup.dismiss();
                LogUtil.inf("h "+h );
                dismiss();


            }
        });

//        int width = getCurrActivity().getWindowManager().getDefaultDisplay().getWidth();
//        int height = getCurrActivity().getWindowManager().getDefaultDisplay().getHeight();
//        setTimePopup = new PopupWindow(view, width, height / 3, true);
//        setTimePopup.setAnimationStyle(android.R.style.Animation_Translucent);
//        // 设置一个弹窗的背景，控制弹窗消失时不许有这个属性
//        setTimePopup.setBackgroundDrawable(new ColorDrawable());
//        View parent = getCurrActivity().getWindow().getDecorView();
//        // 第一个参数父容器，第二参数显示的位置，第3,4参数是坐标点x,y
//        setTimePopup.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }
    private void upDataTmep(float values) {
        pre_tempValues = getContext().getSharedPreferences("sp_da", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre_tempValues.edit();
        editor.putFloat("temperature", values);
        editor.commit();
    }
}
