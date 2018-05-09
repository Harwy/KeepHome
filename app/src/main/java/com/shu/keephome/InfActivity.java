package com.shu.keephome;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.shu.keephome.gson.UpdateUserJson;
import com.shu.keephome.util.HttpUtil;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * 查看个人信息，以及编辑修改个人信息
 */

public class InfActivity extends AppCompatActivity {

    private EditText inf_name;

    private EditText inf_age;

    private EditText inf_email;

    private EditText inf_address;

    private Button inf_update;

    private String userid;

    private ProgressDialog progressDialog;

    /**
     * 用户类
     *
     */
    private String userName;

    private String userAge;

    private String userEmail;

    private String userAddress;

    private RadioGroup inf_sex;

    private RadioButton male;

    private RadioButton female;

    private int userSex;

    private int send_sex;

    private String status; // 状态字

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf);

        inf_name = (EditText) findViewById(R.id.inf_name);
        inf_age = (EditText) findViewById(R.id.inf_age);
        inf_email = (EditText) findViewById(R.id.inf_email);
        inf_address = (EditText) findViewById(R.id.inf_address);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.femle);
        inf_sex = (RadioGroup) findViewById(R.id.sex);
        inf_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (inf_sex.getCheckedRadioButtonId()){
                    case R.id.male:
                        send_sex = 1;
                        break;
                    case R.id.femle:
                        send_sex = 2;
                        break;
                }}
        });

        inf_update = (Button) findViewById(R.id.inf_update);
        userid = this.getIntent().getStringExtra("index");
        init_user(userid);
        inf_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = UpdateUserJson.UpdateUserJSON(
                                inf_name.getText().toString(),
                                inf_address.getText().toString(),
                                inf_email.getText().toString(),
                                inf_age.getText().toString(),
                                send_sex);
                Log.d(TAG, "onClick: data"+ data);
                update(data);
            }
        });

    }


    /**
     * 查询用户信息
     */
    private void init_user(String userid){
        showProgressDialog();
        // 组成url
        Log.d(TAG, "initUser: " + userid);
        String userUrl = "http://39.106.213.217:8080/api/prouser/" + userid + "/";
        HttpUtil.getHttp(userUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(InfActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                boolean result = true;
                parseUser(s);  // 解析用户信息
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            inf_name.setText(userName);
                            inf_address.setText(userAddress);
                            inf_email.setText(userEmail);
                            inf_age.setText(userAge);
                            if (userSex == 1){
                                inf_sex.check(R.id.male);
                            }else {
                                inf_sex.check(R.id.femle);
                            }
                            Log.d(TAG, "onActivityCreated: open");


                        }
                    });
                }

            }
        });
    }

    /**
     * 用户信息类解析
     *
     */
    private void parseUser(String jsonData){
        try {
            Log.d(TAG, "parseUser: "+ jsonData);
            JSONObject jsonObject = new JSONObject(jsonData);
            String  name = jsonObject.getString("userName");
            String  Age = jsonObject.getString("userAge");
            int Sex = jsonObject.getInt("userSex");
            String Address = jsonObject.getString("userAddress");
            String Email = jsonObject.getString("userEmail");
            userName = name;
            userAddress = Address;
            userEmail = Email;
            userSex = Sex;
            userAge = Age;
            Log.d(TAG, "parseUser: "+ userName);
            Log.d(TAG, "parseUser: " + userEmail);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 更新用户信息
     */
    private void update(String request){
        showProgressDialog();
        String userUrl = "http://39.106.213.217:8080/api/edit_user/";
        HttpUtil.postHttp(userUrl, request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(InfActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                parseUpdate(s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("0".equals(status)){
                            Toast.makeText(InfActivity.this, "用户信息修改成功", Toast.LENGTH_SHORT).show();
                        }else if ("1".equals(status)){
                            Toast.makeText(InfActivity.this, "用户信息修改失败", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(InfActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                closeProgressDialog();
            }
        });

    }

    /**
     * 解析反馈信息
     */
    private void parseUpdate(String jsondata){
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            String  edit = jsonObject.getString("edit");
            status = edit;
        }catch (Exception e){
            e.printStackTrace();
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
