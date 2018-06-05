package com.shu.keephome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.keephome.util.HttpUtil;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.shu.keephome.util.CHexConver.hexStr2Str;

public class AddActivity extends AppCompatActivity {

    private final String TAG="AddActivity";
    ServerSocket serverSocket;//创建ServerSocket对象
    Socket clicksSocket;//连接通道，创建Socket对象
    Button startButton;
    Button sendButton;//发送按钮
    EditText portEditText;
    EditText receiveEditText;
    EditText sendEditText;//发送消息框
    InputStream inputstream;//创建输入数据流
    OutputStream outputStream;//创建输出数据流
    EditText text_WIFI_name; //wifi SSID
    EditText text_WIFI_pass; // wifi password
    EditText text_username;
    Button send_WIFI; // 发送wifi账号密码
    Boolean FLAG = true;

    private String state; //状态标志位

    private ProgressDialog progressDialog;//提示框



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
/**
 * 读一下手机wifi状态下的ip地址，只有知道它的ip才能连接它嘛
 */
        Toast.makeText(AddActivity.this,"当前网络：" + getConnectWifiSsid() + "\n 当前IP：" + getLocalIpAddress(), Toast.LENGTH_SHORT).show();

        startButton = (Button) findViewById(R.id.start_button);
        portEditText = (EditText) findViewById(R.id.port_EditText);
        receiveEditText = (EditText) findViewById(R.id.receive_EditText);
        sendButton = (Button) findViewById(R.id.send_button);
        sendEditText = (EditText) findViewById(R.id.message_EditText);

        text_WIFI_name = (EditText)findViewById(R.id.add_wifi);
        text_WIFI_pass = (EditText) findViewById(R.id.add_pass);
//        text_WIFI_name.setOnClickListener(text_WIFI_nameListener);
        text_WIFI_name.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // text_WIFI_name.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = text_WIFI_name.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > text_WIFI_name.getWidth()
                        - text_WIFI_name.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    text_WIFI_nameListener();
                }
                return false;
            }
        });
        send_WIFI = (Button) findViewById(R.id.send_WIFI);
        send_WIFI.setOnClickListener(send_WIFIListener);

        text_username = (EditText)findViewById(R.id.id_EditText);
        String inf = getIntent().getStringExtra("name")+" id:"+
                getIntent().getStringExtra("id");
        text_username.setText(inf);


        startButton.setOnClickListener(startButtonListener);
        sendButton.setOnClickListener(sendButtonListener);

        try
        {
            int port =Integer.valueOf(portEditText.getText().toString());//获取portEditText中的端口号
            serverSocket = new ServerSocket(port);//监听port端口，这个程序的通信端口就是port了
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        try{
            if (clicksSocket != null){
                outputStream.flush();
                inputstream.close();
                outputStream.close();
                clicksSocket.close();
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        super.onBackPressed();
    }

    /**
     * 启动WIFI设置界面
     */
    private void text_WIFI_nameListener(){
            // TODO Auto-generated method stub
            /**
             * 切换到WIFI设置界面
             */
            Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
            startActivity(wifiSettingsIntent);
    };

    /**
     * 发送WIFI账号密码
     * 向数据库请求绑定用户和设备
     * 第一步：弹窗
     */
    private View.OnClickListener send_WIFIListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            showDialog();
        }
    };

    /**
     * 向数据库请求绑定设备
     * @param userid,deviceid
     * @return
     */
    private void connect_Ali(String userid, String deviceid){
        // 组成url
        String userUrl = "http://39.106.213.217:8080/api/connect/" + deviceid + "/" + userid + "/";
        HttpUtil.getHttp(userUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(getApplicationContext(), "绑定失败，请联系作者！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    state = jsonObject.getString("state");
                }catch (Exception e){
                    e.printStackTrace();
                }
                AddActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(state)){
                            Toast.makeText(getApplicationContext(), "绑定成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "绑定失败，请联系作者！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    /**
     * 消息文本框
     * 确认向下位机发送
     */
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("绑定提示")
                .setMessage("即将绑定该设备与 用户："+ getIntent().getStringExtra("name")+"确认绑定该设备？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 从WIFI账号框和密码框集成账号密码发送
                         */
//            int c = 0x0a;
//            int d = 0x0b;
                        final String WIFI_inf = "\\a" + text_WIFI_name.getText().toString() + "," + text_WIFI_pass.getText().toString()+ ","+
                                getIntent().getStringExtra("id") +"\\b";
//            final String WIFI_inf = intToByte(c) + text_WIFI_name.getText().toString() + "," + text_WIFI_pass.getText().toString()+ intToByte(d);
                        new Thread() {
                            @Override
                            public void run() {
                                // 需要执行的方法
                                // 执行完毕后给handler发送一个空消息
                                //handler.sendEmptyMessage(0);
                                try
                                {   //获取输出流
                                    outputStream = clicksSocket.getOutputStream();
                                    //发送数据
                                    outputStream.write(WIFI_inf.getBytes());
                                    //outputStream.write("0".getBytes());
                                    //outputStream.flush();
                                    //outputStream.close();
                                }
                                catch (Exception e)
                                {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }.start();
//                        showProgressDialog();
//                        closeProgressDialog();
//                        showDialog1();
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

//    /**
//     * 向服务器发送绑定弹窗
//     */
//    private void showDialog1() {
//        new AlertDialog.Builder(this)
//                .setTitle("确认提示")
//                .setMessage("即将于服务器同步，请确认！")
//                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        connect_Ali(getIntent().getStringExtra("userid"),"5");
//                    }
//                })
//                .setNegativeButton("取消",null)
//                .show();
//    }


    public static byte intToByte(int x) {
        return (byte) x;
    }



    /**
     * 启动服务按钮监听事件
     */
    private View.OnClickListener startButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            /**
             * 启动服务器监听线程
             */
            if (FLAG){
                AddActivity.ServerSocket_thread serversocket_thread = new AddActivity.ServerSocket_thread();
                serversocket_thread.start();
                FLAG = false;
                startButton.setText("服务启动");
                startButton.setTextColor(getColor(R.color.colorPrimary));
                Toast.makeText(AddActivity.this,"当前网络：" + getConnectWifiSsid() + "\n 当前IP：" + getLocalIpAddress(),
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(AddActivity.this, "服务已经启动", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 发送消息按钮事件
     */
    private View.OnClickListener sendButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new Thread() {
                @Override
                public void run() {
                    // 需要执行的方法
                    // 执行完毕后给handler发送一个空消息
                    //handler.sendEmptyMessage(0);
                    try
                    {   //获取输出流
                        outputStream = clicksSocket.getOutputStream();
                        //发送数据
                        outputStream.write(sendEditText.getText().toString().getBytes());
                        //outputStream.write("0".getBytes());
                        //outputStream.flush();
                        //outputStream.close();
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    };


    /**
     * 服务器监听线程
     */
    class ServerSocket_thread extends Thread
    {
        public void run()//重写Thread的run方法
        {
            while (true)
            {
                try
                {
                    //监听连接 ，如果无连接就会处于阻塞状态，一直在这等着
                    clicksSocket = serverSocket.accept();
                    inputstream = clicksSocket.getInputStream();//获取输入流
                    //启动接收线程
                    AddActivity.Receive_Thread receive_Thread = new AddActivity.Receive_Thread();
                    receive_Thread.start();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *
     * 接收线程
     *
     */
    class Receive_Thread extends Thread//继承Thread
    {
        public void run()//重写run方法
        {
            while (true)
            {
                try
                {
                    final byte[] buf = new byte[1024];
                    final int len = inputstream.read(buf);
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            receiveEditText.setText(new String(buf,0,len));
                            byte[] rec_byte = buf;
                            Log.d(TAG, "run: rec" + rec_byte[0]);
                        }
                    });
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *
     * 获取WIFI下ip地址
     */
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
    /**
     * 获取WIFI SSID
     */
    private String getConnectWifiSsid(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

//    //将输入的16进制string转成字节
//    public static byte[] hexStringToBytes(String hexString) {
//        if (hexString == null || hexString.equals("")) {
//            return null;
//        }
//        hexString = hexString.toUpperCase();
//        int length = hexString.length() / 2;
//        char[] hexChars = hexString.toCharArray();
//        byte[] d = new byte[length];
//        for (int i = 0; i < length; i++) {
//            int pos = i * 2;
//            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
//        }
//        return d;
//    }
//    private static byte charToByte(char c) {
//        return (byte) "0123456789ABCDEF".indexOf(c);
//    }


}
