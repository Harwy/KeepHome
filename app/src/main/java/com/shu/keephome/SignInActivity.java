package com.shu.keephome;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shu.keephome.gson.MailJson;
import com.shu.keephome.util.HttpUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {

    private EditText email; // 邮箱
    private EditText name;  // 用户名
    private EditText password;// 密码
    private EditText check;// 验证码
    private Button send_mail;// 发送验证码
    private Button sign_in;// 注册

    private String mode; // 返回验证码模式
    private String msg; // 返回验证码状态
    private String sign; // 返回注册状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = (EditText) findViewById(R.id.sign_email);
        name = (EditText) findViewById(R.id.sign_name);
        password = (EditText) findViewById(R.id.sign_password);
        check = (EditText) findViewById(R.id.sign_check);
        send_mail = (Button) findViewById(R.id.sign_btnmail);
        sign_in = (Button) findViewById(R.id.sign_btnin);

        // 验证码弹窗确认
        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmail(email.getText().toString())){
                    new AlertDialog.Builder(SignInActivity.this)
                            .setTitle("发送验证码？")
                            .setMessage("即将向邮箱："+ email.getText().toString() +"发送验证码")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkMailSend(email.getText().toString());
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }else{
                    Toast.makeText(SignInActivity.this, "邮箱格式非法，请确认您的邮箱地址", Toast.LENGTH_SHORT).show();
                }
            }

        });

        // 注册确认
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(name.getText().toString())){
                    Toast.makeText(SignInActivity.this, "请填写用户名", Toast.LENGTH_SHORT).show();
                }else if ("".equals(password.getText().toString())){
                    Toast.makeText(SignInActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
                }else if ("".equals(check.getText().toString())){
                    Toast.makeText(SignInActivity.this, "请填写验证码", Toast.LENGTH_SHORT).show();
                }else if (check.getText().length() != 6){
                    Toast.makeText(SignInActivity.this, "请填写6位验证码", Toast.LENGTH_SHORT).show();
                }else{
                    new AlertDialog.Builder(SignInActivity.this)
                            .setTitle("注册用户？")
                            .setMessage("即将创建："+ name.getText().toString() +"用户")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String signJSON = MailJson.SignJson(name.getText().toString(),password.getText().toString(),email.getText().toString(),check.getText().toString());
                                    SignIn(signJSON);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            }
        });

    }

    /**
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 向服务器请求向邮箱发送验证码
     */
    private void checkMailSend(String email){
        String url = "http://39.106.213.217:8080/api/mailcheck/";
        // 邮箱信息JSON
        String check_email = MailJson.MailJson(email,"1");

        HttpUtil.postHttp(url, check_email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String back = response.body().string();
                // 解析回传数据
                try{
                    JSONObject jsonObject = new JSONObject(back);
                    mode = jsonObject.getString("mode");
                    msg = jsonObject.getString("msg");
                }catch (Exception e){
                    e.printStackTrace();
                    mode = "1";
                    msg = "2";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(msg)){
                            // 验证码发送成功，提示查看邮箱
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setTitle("消息")
                                    .setIcon(R.drawable.ic_check_circle_light_green_600_24dp)
                                    .setMessage("验证码发送成功，请检查邮箱确认！")
                                    .setPositiveButton("我知道了",null)
                                    .show();
                        }else{
                            // 该邮箱已经注册
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setTitle("消息")
                                    .setIcon(R.drawable.ic_warning_red_500_24dp)
                                    .setMessage("该邮箱已经注册！")
                                    .setPositiveButton("我知道了",null)
                                    .show();
                        }
                    }
                });

            }
        });
    }

    /**
     * 用户注册
     */
    private void SignIn(String json){
        String url = "http://39.106.213.217:8080/api/signin/";
        HttpUtil.postHttp(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String back1 = response.body().string();
                // 解析回传数据
                try{
                    JSONObject jsonObject1 = new JSONObject(back1);
                    sign = jsonObject1.getString("sign");
                }catch (Exception e){
                    e.printStackTrace();
                    sign = "3";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("0".equals(sign)){
                            // 创建成功
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setTitle("消息")
                                    .setIcon(R.drawable.ic_check_circle_light_green_600_24dp)
                                    .setMessage("注册用户成功！")
                                    .setPositiveButton("我知道了",null)
                                    .show();
                        }else if ("1".equals(sign)){
                            // 用户已存在
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setTitle("消息")
                                    .setIcon(R.drawable.ic_warning_red_500_24dp)
                                    .setMessage("用户已存在！")
                                    .setPositiveButton("我知道了",null)
                                    .show();
                        }else if ("2".equals(sign)){
                            // 验证码错误
                            new AlertDialog.Builder(SignInActivity.this)
                                    .setTitle("消息")
                                    .setIcon(R.drawable.ic_warning_red_500_24dp)
                                    .setMessage("用户已存在！")
                                    .setPositiveButton("我知道了",null)
                                    .show();
                        }else{
                            Toast.makeText(SignInActivity.this, "网络错误",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
