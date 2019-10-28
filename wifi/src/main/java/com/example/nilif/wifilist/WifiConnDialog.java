package com.example.nilif.wifilist;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kingcomchina.www.tcptool.R;

/**
 * Created by nilif on 2016/7/8.
 */
public class WifiConnDialog extends Dialog{
    private Context context;

    private ScanResult scanResult;
    private String wifiName;
    private int level;
    private String securigyLevel;

    private TextView txtWifiName;
    private TextView txtSinglStrength;
    private TextView txtSecurityLevel;
    private EditText edtPassword;
    private CheckBox cbxShowPass;

    private TextView txtBtnConn;
    private TextView txtBtnCancel;

    public WifiConnDialog(Context context, int theme) {
        super(context, theme);
    }

    private WifiConnDialog(Context context, int theme, String wifiName,
                           int singlStren, String securityLevl) {
        super(context, theme);
        this.context = context;
        this.wifiName = wifiName;
        this.level = singlStren;
        this.securigyLevel = securityLevl;
    }

    public WifiConnDialog(Context context, int theme, ScanResult scanResult,
                          OnNetworkChangeListener onNetworkChangeListener) {
        this(context, theme, scanResult.SSID, scanResult.level,
                scanResult.capabilities);
        this.scanResult = scanResult;
        this.onNetworkChangeListener = onNetworkChangeListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wifi_conn);
        setCanceledOnTouchOutside(false);

        initView();
        setListener();
    }

    private void setListener() {

        edtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    txtBtnConn.setEnabled(false);
                    cbxShowPass.setEnabled(false);

                } else {
                    txtBtnConn.setEnabled(true);
                    cbxShowPass.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cbxShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    // 文本正常显示
                    edtPassword
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = edtPassword.getText();
                    Selection.setSelection(etable, etable.length());

                } else {
                    // 文本以密码形式显示
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    // 下面两行代码实现: 输入框光标一直在输入文本后面
                    Editable etable = edtPassword.getText();
                    Selection.setSelection(etable, etable.length());

                }
            }
        });

        txtBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("txtBtnCancel");
                WifiConnDialog.this.dismiss();
            }
        });

        txtBtnConn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                WifiClass.WifiCipherType type = null;
                if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                    type = WifiClass.WifiCipherType.WIFICIPHER_WPA;
                } else if (scanResult.capabilities.toUpperCase()
                        .contains("WEP")) {
                    type = WifiClass.WifiCipherType.WIFICIPHER_WEP;
                } else {
                    type = WifiClass.WifiCipherType.WIFICIPHER_NOPASS;
                }

                // 连接网络
                WifiClass mWifiAdmin = new WifiClass(context);
                boolean bRet = mWifiAdmin.connect(scanResult.SSID, edtPassword
                        .getText().toString().trim(), type);
                if (bRet) {
                    showShortToast("连接成功");
                    onNetworkChangeListener.onNetWorkConnect();
                } else {
                    showShortToast("连接失败");
                    onNetworkChangeListener.onNetWorkConnect();
                }
                WifiConnDialog.this.dismiss();
            }
        });
    }

    private void initView() {
        txtWifiName = (TextView) findViewById(R.id.txt_wifi_name);
        txtSinglStrength = (TextView) findViewById(R.id.txt_signal_strength);
        txtSecurityLevel = (TextView) findViewById(R.id.txt_security_level);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        cbxShowPass = (CheckBox) findViewById(R.id.cbx_show_pass);
        txtBtnCancel = (TextView) findViewById(R.id.txt_btn_cancel);
        txtBtnConn = (TextView) findViewById(R.id.txt_btn_connect);

        txtWifiName.setText(wifiName);
//        txtSinglStrength.setText(WifiClass.singlLevToStr(level));
        txtSecurityLevel.setText(securigyLevel);

        txtBtnConn.setEnabled(false);
        cbxShowPass.setEnabled(false);

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        super.show();
        getWindow().setLayout((int) (size.x * 9 / 10),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showShortToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private OnNetworkChangeListener onNetworkChangeListener;
}
