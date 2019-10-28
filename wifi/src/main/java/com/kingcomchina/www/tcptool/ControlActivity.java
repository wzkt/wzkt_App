package com.kingcomchina.www.tcptool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

public class ControlActivity extends AppCompatActivity {
    private static final String TAG ="ControlActivity" ;
    private ToggleButton toggle;
    private MsgThread msgThread;
    private SendThread sendThread;
    private String open = "CMD_ON";
    private String off = "CMD_OFF";
    private String query = "CMD_QUERY";
    private boolean isOpen;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                String content = (String) msg.obj;
                Log.d(TAG, "收到服务器数据:" + content);
                updateToggle(content);
            }
        }
    };

    private void updateToggle(String content) {
        Log.d(TAG,"content:"+content);
        if(content.equals(open)){
            Log.d(TAG,"打开开关");
            isOpen=false;
            toggle.setChecked(false);
        }else if(content.equals(off)){
            Log.d(TAG,"关闭开关");
            isOpen=true;
            toggle.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        toggle = (ToggleButton) findViewById(R.id.toggle);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()

                .detectDiskReads().detectDiskWrites().detectNetwork()

                .penaltyLog().build());

        sendThread = new SendThread(handler);
        msgThread = new MsgThread(sendThread);

        msgThread.start();
        sendThread.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    msgThread.putMsg(query.getBytes());
                    SystemClock.sleep(5000);
                }
            }
        }).start();
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen){
                    msgThread.putMsg(open.getBytes());
                }else{
                    msgThread.putMsg(off.getBytes());
                }
            }
        });


    }
}
