package com.Alan.eva.tools.timer;

import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;

public class MyTimerTask extends CountDownTimer {

    public MyTimerTask() {
        super(60 * 1000, 1000);
    }

    private AppCompatTextView tv;

    public void setTv(AppCompatTextView tv) {
        this.tv = tv;
    }

    private ITimeFinish timer;

    public void setTimer(ITimeFinish timer) {
        this.timer = timer;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (tv != null) {
            long sec = millisUntilFinished / 1000;
            tv.setText(String.valueOf(sec + "秒"));
        }
    }

    @Override
    public void onFinish() {
        if (timer != null) {
            timer.onTimeFinish();
        } else {
            throw new NullPointerException("time Finish 接口为空了，请添加");
        }
    }
}
