package com.shu.keephome;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.keephome.db.Data;
import com.shu.keephome.db.DeviceList;
import com.shu.keephome.util.HttpUtil;
import com.shu.keephome.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 14623 on 2018/5/6.
 * 设备列表碎片
 */

public class DataListFragment extends Fragment{

    private DrawerLayout mDrawerLayout;


    // 模拟数据
    private Data[] datas = {
            new Data("2018-5-5","0",100,25,32,45,1,false),
            new Data("2018-5-5","0",100,25,32,45,1,false),
            new Data("2018-5-5","0",100,25,32,45,1,false),
            new Data("2018-5-5","0",100,25,32,45,1,false),
            new Data("2018-5-5","0",100,25,32,45,1,false),
    };

    private List<DeviceList> dataList = new ArrayList<>();

    private DataListAdapter adapter;

    private RecyclerView recyclerView;

    private String userName;

    private String userAge;

    private String userEmail;

    private String userAddress;

    private int userSex;

    GridLayoutManager layoutManager;

    NavigationView navView;

    ActionBar actionBar;

    Toolbar toolbar;

    private TextView name;

    private TextView mail;

    private String userid;

    private ProgressDialog  progressDialog;

    /**
     * 绑定Layout元素
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawable_fragment, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        navView = (NavigationView) view.findViewById(R.id.nav_view);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Log.d(TAG, "onCreateView: actionBar = " + actionBar);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
        }

        Log.d(TAG, "onCreateView: hello");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataListAdapter(dataList);
        recyclerView.setAdapter(adapter);
//        // 用户名字，邮箱
//        View view_nav = inflater.inflate(R.layout.nav_header, container, false);
//
//        name = (TextView) view_nav.findViewById(R.id.username);
//        mail = (TextView) view_nav.findViewById(R.id.mail);
//        Log.d(TAG, "onCreateView: name = " + name);
//        // 待增加
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userid = getActivity().getIntent().getStringExtra("userid");  // 接收上一个活动LoginFragment传递的用户名
        // 查询用户信息
        initUser(userid);

        // 添加首次更新
    // 查询设备信息
        initData(userid);
        // 添加点击
//        recyclerView.setOnClickListener(new );
    }

    /**
     * 侧面栏
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    /**
     * 查询用户信息
     */
    private void initUser(String userid){
        showProgressDialog();
        // 组成url
        Log.d(TAG, "initUser: " + userid);
        String userUrl = "http://39.106.213.217:8080/api/prouser/" + userid + "/";
        HttpUtil.getHttp(userUrl, new Callback() {
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
                parseUser(s);  // 解析用户信息
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Log.d(TAG, "onActivityCreated: open");
                            Log.d(TAG, "run: 测试userName是否可用"+ userName);


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
     * 带入数据
     */
    private void initData(String userid){
        showProgressDialog();
        String userUrl = "http://39.106.213.217:8080/api/prouser/" + userid + "/product/";
        Log.d(TAG, "initData: userUrl = " + userUrl);
        HttpUtil.getHttp(userUrl, new Callback() {
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
                final String s = response.body().string();
                Log.d(TAG, "onResponse: s = "+s);
                final List<DeviceList> deviceLists = Utility.handleDeviceListResponse(s);
                Log.d(TAG, "showDeviceList: 设备列表 = " + deviceLists.size());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if (deviceLists != null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("deviceList", s);
                            editor.apply();
                            showDeviceList(deviceLists);
                        }else {
                            Toast.makeText(getActivity(), "获取设备列表失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    /**
     * 内容显示
     */
    private void showDeviceList(List<DeviceList> deviceList){
        dataList.clear();
        Log.d(TAG, "showDeviceList: 设备列表 = " + deviceList.size());
        for (int i = 0; i < deviceList.size();i++){
            dataList.add(deviceList.get(i));
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
