package com.kingcomchina.www.tcptool;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Author:      JerryChow
 * Date:        2017/5/31 15:22
 * QQ:          384114651
 * Email:       zhoumricecream@163.com
 * Description:
 */
public class SendThread extends Thread {
    private Socket socket;
    private int port = 10000;
    private String ip = "192.168.4.1";
    private Handler handler;


    public SendThread(Handler handler) {
        this.handler = handler;
        try {
            socket = new Socket(ip, port);
            socket.setKeepAlive(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();
        Log.d("TAG", "接收服务器消息");
        try {
            if (socket == null) {
                return;
            }
            Log.d("TAG", "receive socket:" + socket);
            InputStream br = null;
            br = socket.getInputStream();
            byte[] b=new byte[1024];
            while (true) {
                int length=br.read(b);
                String content = new String(b,0,length);
                Message message = Message.obtain();
                message.what = 0x01;
                message.obj = content;
                Log.d("TAG", "收到数据:" + content);
                handler.sendMessage(message);
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void sendMsg(byte[] msg) {

        OutputStream outputStream = null;
        try {
            if (socket != null) {
                Log.d("TAG", "send socket:" + socket);
                outputStream = socket.getOutputStream();
                // while(true){
                //SystemClock.sleep(7000);
                outputStream.write(msg);
                Log.d("TAG", "发送数据:" + new String(msg));
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
