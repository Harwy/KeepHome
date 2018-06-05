package com.shu.keephome;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.shu.keephome.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ControlActivity extends AppCompatActivity {

    private static final String TAG = "ControlActivity";


    private int state;
    private String device;

    private TextView text;
    private TextView text1;
    private TextView text2;
    private TextView text_inf;
    private ImageView img1;
    private ImageView img2;


    private Boolean FLAG_1 = false;
    private Boolean FLAG_2 = false;
    private Boolean FLAG_3 = false;
    private Boolean FLAG_4 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);



        SwitchButton mSwitchButton = (SwitchButton) findViewById(R.id.switchButton);
        SwitchButton mSwitchButton2 = (SwitchButton) findViewById(R.id.switchButton2);
        text = (TextView)findViewById(R.id.control_text);
        text1 = (TextView)findViewById(R.id.control_text2);
        text2 = (TextView)findViewById(R.id.control_text4);
        text_inf = (TextView) findViewById(R.id.control_text1);
        img1 = (ImageView) findViewById(R.id.control_img1);
        img2 = (ImageView) findViewById(R.id.control_img2);

        device = getIntent().getStringExtra("device");
        text.setText("目前控制设备 device：" +device);

        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    PostControl("1",device,"1");
                }else{
                    PostControl("2",device,"2");
                }
            }
        });

        mSwitchButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    PostControl("3",device,"3");
                }else{
                    PostControl("4",device,"4");
                }
            }
        });

    }

    private void PostControl(String control, String device, final String command){
        String url = "http://39.106.213.217:8080/api/control/"+ device + "/" + control + "/push/";
        HttpUtil.getHttp(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String back = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(back);
                    state = jsonObject.getInt("state");
                    Log.d(TAG, "onResponse: state" + state);
                }catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (command){
                            case "1":
                                if (state == 1){
                                    text1.setText("开灯");
                                    img1.setImageResource(R.drawable.ic_lamp_on);
                                    Toast.makeText(ControlActivity.this, "操作：开灯 成功",Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(ControlActivity.this, "操作：开灯 过于频繁",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "2":
                                if (state == 1){
                                    text1.setText("关灯");
                                    img1.setImageResource(R.drawable.ic_lamp_off);
                                    Toast.makeText(ControlActivity.this, "操作：关灯 成功",Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(ControlActivity.this, "操作：关灯 过于频繁",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "3":
                                if (state == 1){
                                    text2.setText("开启设备");
                                    img2.setImageResource(R.drawable.top_lamp_on);
                                    Toast.makeText(ControlActivity.this, "操作：开启设备 成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ControlActivity.this, "操作：开启设备 过于频繁",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "4":
                                if (state == 1){
                                    text2.setText("关闭设备");
                                    img2.setImageResource(R.drawable.top_lamp_off);
                                    Toast.makeText(ControlActivity.this, "操作：关闭设备 成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ControlActivity.this, "操作：关闭设备 过于频繁",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
    }

}
