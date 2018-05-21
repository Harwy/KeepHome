package com.shu.keephome;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
    Button send_WIFI; // 发送wifi账号密码
    Boolean FLAG = true;


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
        text_WIFI_name.setOnClickListener(text_WIFI_nameListener);
        send_WIFI = (Button) findViewById(R.id.send_WIFI);
        send_WIFI.setOnClickListener(send_WIFIListener);

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
    private View.OnClickListener text_WIFI_nameListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            /**
             * 切换到WIFI设置界面
             */
            Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
            startActivity(wifiSettingsIntent);
        }
    };

    /**
     * 发送WIFI账号密码
     */
    private View.OnClickListener send_WIFIListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            /**
             * 从WIFI账号框和密码框集成账号密码发送
             */
//            int c = 0x0a;
//            int d = 0x0b;
            final String WIFI_inf = "\\a" + text_WIFI_name.getText().toString() + "," + text_WIFI_pass.getText().toString()+"\\b";
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
        }
    };


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
                Toast.makeText(AddActivity.this,"当前网络：" + getConnectWifiSsid() + "\n 当前IP：" + getLocalIpAddress(), Toast.LENGTH_SHORT).show();
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


}
