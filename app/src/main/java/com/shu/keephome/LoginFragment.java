package com.shu.keephome;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.keephome.gson.PersonJson;
import com.shu.keephome.util.HttpUtil;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 14623 on 2018/5/6.
 *
 */

public class LoginFragment extends Fragment {

    private String account;

    private String psw;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private Button login;

    private EditText username;

    private EditText password;

    private CheckBox rememberPass;

    private TextView tip;

    private ProgressDialog  progressDialog;


    private String status; // 登录认证状态字

    private String userid; // 用户登录名下id

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        login = (Button) view.findViewById(R.id.login);
        username = (EditText) view.findViewById(R.id.edit_username);
        password = (EditText)view.findViewById(R.id.edit_password);
        rememberPass = (CheckBox) view.findViewById(R.id.remember_pass);
        tip = (TextView) view.findViewById(R.id.login_tip);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember){
            //账号密码设置到文本框
            String account = pref.getString("account", "");
            String psw = pref.getString("psw", "");
            username.setText(account);
            password.setText(psw);
            rememberPass.setChecked(true);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = username.getText().toString();
                psw = password.getText().toString();
                // 测试，如果账号是root，且密码是123456，登录成功
                // 将账号和密码发送至服务器，根据返回信息判定结果1.登录成功，用户名作为参数传递 2.账号不存在 3.密码错误

                //构成用户类,JSON化person类
                String json = PersonJson.PersonJSON(account, psw);

                // 联网认证
                requestPerson(json);
            }
        });
            }


    public void requestPerson(final String json){
        showProgressDialog();
        String personUrl = "http://39.106.213.217:8080/api/login/";
        HttpUtil.postHttp(personUrl, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                boolean result = true;
                parsePerson(s);
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("0".equals(status)){
                                editor = pref.edit();
                                if (rememberPass.isChecked()){ // 复选框是否被选中
                                    editor.putBoolean("remember_password", true);
                                    editor.putString("account", account);
                                    editor.putString("psw", psw);
                                }else {
                                    editor.clear();
                                }
                                editor.apply();
                                Intent intent = new Intent(getActivity(), DrawableActivity.class);
                                intent.putExtra("userid", userid);  // 向下一个活动传递用户名
                                startActivity(intent);
                                getActivity().finish();
                            }else if ("1".equals(status)){
                                Toast.makeText(getActivity(), "该用户不存在", Toast.LENGTH_SHORT).show();
                                tip.setText("该用户不存在");
                                tip.setTextColor(Color.parseColor("#FF0000"));
                            }else if ("2".equals(status)){
                                Toast.makeText(getActivity(), "密码输入错误", Toast.LENGTH_SHORT).show();
                                tip.setText("密码输入错误");
                                tip.setTextColor(Color.parseColor("#FF0000"));
                            }else {
                                Toast.makeText(getActivity(), "联网出错咯", Toast.LENGTH_SHORT).show();
                                tip.setText("联网出错咯");
                                tip.setTextColor(Color.parseColor("#FF0000"));
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 解析用户状态字
     * @param jsondata
     */
    public void parsePerson(String jsondata){
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            String  msg = jsonObject.getString("msg");
            String  id = jsonObject.getString("id");
            status = msg;
            userid = id;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

        /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
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
}
