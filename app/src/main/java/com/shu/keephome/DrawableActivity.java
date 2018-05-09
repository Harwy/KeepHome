package com.shu.keephome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.keephome.db.Data;
import com.shu.keephome.db.DeviceList;
import com.shu.keephome.util.HttpUtil;
import com.shu.keephome.util.Utility;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static org.litepal.LitePalApplication.getContext;

public class DrawableActivity extends AppCompatActivity{

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

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

    private String userid;

    private ProgressDialog progressDialog;

    private SwipeRefreshLayout deviceRefresh; // 下拉刷新

    private int flag = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);

        deviceRefresh = (SwipeRefreshLayout) findViewById(R.id.device_refresh);
        deviceRefresh.setColorSchemeResources(R.color.colorPrimary);
        deviceRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(userid);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_view);

        actionBar = getSupportActionBar();
        Log.d(TAG, "onCreateView: actionBar = " + actionBar);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
        }

        Log.d(TAG, "onCreateView: hello");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataListAdapter(dataList);
        recyclerView.setAdapter(adapter);

        userid = this.getIntent().getStringExtra("userid");  // 接收上一个活动LoginFragment传递的用户名
        // 查询用户信息
        initUser(userid);
        // 添加首次更新
        // 查询设备信息
        initData(userid);

//        navView.setCheckedItem(R.id.nav_add);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                Log.d(TAG, "onNavigationItemSelected: 菜单："+ item);
                switch (item.getItemId()){
                    case R.id.nav_add :
                        Log.d(TAG, "onNavigationItemSelected: 111："+ item);
                        break;
                    case R.id.nav_inf :
                        Log.d(TAG, "onNavigationItemSelected: 111："+ item);
                        break;
                    case R.id.nav_about :
                        Intent intent = new Intent(DrawableActivity.this, AboutActivity.class);
                        intent.putExtra("index", item.getTitle());
                        startActivity(intent);
                        Log.d(TAG, "onNavigationItemSelected: 111："+ item.getTitle());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        adapter.setClickListener(new DataListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(DrawableActivity.this, DataShowActivity.class);
                intent.putExtra("device_id", position);  // 向下一个活动传递用户名
                Log.d(TAG, "onClick: position = "+ position);
                startActivity(intent);
                DrawableActivity.this.finish();
            }
        });

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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
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
                DrawableActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(DrawableActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                boolean result = true;
                parseUser(s);  // 解析用户信息
                if (result){
                    DrawableActivity.this.runOnUiThread(new Runnable() {
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
        if (flag == 1){
            showProgressDialog();
        }
        String userUrl = "http://39.106.213.217:8080/api/prouser/" + userid + "/product/";
        Log.d(TAG, "initData: userUrl = " + userUrl);
        HttpUtil.getHttp(userUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DrawableActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (flag == 1){
                            closeProgressDialog();
                            flag = 0;
                        }
                        Toast.makeText(DrawableActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        deviceRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                Log.d(TAG, "onResponse: s = "+s);
                final List<DeviceList> deviceLists = Utility.handleDeviceListResponse(s);
                Log.d(TAG, "showDeviceList: 设备列表 = " + deviceLists.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (flag == 1){
                            closeProgressDialog();
                            flag = 0;
                        }
                        if (deviceLists != null){
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DrawableActivity.this).edit();
//                            editor.putString("deviceList", s);
//                            editor.apply();
                            adapter.notifyDataSetChanged();
                            showDeviceList(deviceLists);
                            Log.d(TAG, "run: 更新列表");
                        }else {
                            Toast.makeText(DrawableActivity.this, "获取设备列表失败", Toast.LENGTH_SHORT).show();
                        }
                        deviceRefresh.setRefreshing(false);
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
            DeviceList d = deviceList.get((i));
            SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS");
            try {
                d.nowtime.date = format.parse(d.nowtime.created);
            }catch (ParseException e){
                d.nowtime.date = new Date();
            }
            Log.d(TAG, "showDeviceList: nowtime.data = " + d.nowtime.date);
            dataList.add(deviceList.get(i));
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

//    /**
//     * 下拉更新设备列表信息(待更新)
//     */
//    private void refreshDevices(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    Thread.sleep(2000);
//                    initData(userid);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.notifyDataSetChanged();
//                        deviceRefresh.setRefreshing(false);
//                    }
//                });
//            }
//        });
//    }

}
