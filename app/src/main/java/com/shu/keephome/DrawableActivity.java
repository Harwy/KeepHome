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
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.keephome.db.Data;
import com.shu.keephome.db.DeviceList;
import com.shu.keephome.service.AutoUpdateService;
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
    /**修改侧滑栏信息**/
    View headerLayout;
    TextView name;
    TextView email;


    ActionBar actionBar;

    Toolbar toolbar;

    private String userid;

    private ProgressDialog progressDialog;

    private SwipeRefreshLayout deviceRefresh; // 下拉刷新

    private int flag = 1;

    Intent intent_nav;

    private List<String> device_data_list = new ArrayList<>();




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
        /**修改用户名和邮箱*/
        headerLayout = navView.inflateHeaderView(R.layout.nav_header);
        name = (TextView) headerLayout.findViewById(R.id.username);
        email = (TextView) headerLayout.findViewById(R.id.mail);

        actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataListAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        if(userid != null){
            // 查询用户信息
            initUser(userid);
            // 添加首次更新
            // 查询设备信息
            initData(userid);
        }else{
            userid = this.getIntent().getStringExtra("userid");  // 接收上一个活动LoginFragment传递的用户名
            // 查询用户信息
            initUser(userid);
            // 添加首次更新
            // 查询设备信息
            initData(userid);
        }


//        navView.setCheckedItem(R.id.nav_add);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                Log.d(TAG, "onNavigationItemSelected: 菜单："+ item);
                switch (item.getItemId()){
                    case R.id.nav_add :
                        intent_nav = new Intent(DrawableActivity.this, AddActivity.class);
                        intent_nav.putExtra("id", userid);
                        intent_nav.putExtra("name", userName);
                        startActivity(intent_nav);
                        break;
                    case R.id.nav_inf :
                        intent_nav = new Intent(DrawableActivity.this, InfActivity.class);
                        intent_nav.putExtra("index", userid);
                        startActivity(intent_nav);
                        break;
                    case R.id.nav_about :
                        intent_nav = new Intent(DrawableActivity.this, AboutActivity.class);
                        intent_nav.putExtra("index", item.getTitle());
                        startActivity(intent_nav);
                        break;
                    case R.id.nav_exit:
                        SharedPreferences sp = getContext().getSharedPreferences("com.shu.keephome_preferences" ,getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        Log.d(TAG, "onNavigationItemSelected: " + sp.getBoolean("is_enter", false));
                        editor.putBoolean("is_enter", false);
                        editor.apply();
                        Log.d(TAG, "onNavigationItemSelected: " + sp.getBoolean("is_enter", false));
                        intent_nav = new Intent(DrawableActivity.this, LoginActivity.class);
                        startActivity(intent_nav);
                        finish();
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
                intent.putExtra("device_id", device_data_list.get(position));  // 向下一个活动传递用户名
                startActivity(intent);
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
                            name.setText(userName);
                            email.setText(userEmail);
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
//                Log.d(TAG, "onResponse: s = "+s);
                final List<DeviceList> deviceLists = Utility.handleDeviceListResponse(s);
//                Log.d(TAG, "showDeviceList: 设备列表 = " + deviceLists.size());
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
            DeviceList d = deviceList.get(i);
            device_data_list.add(String.valueOf(deviceList.get(i).id));
            Log.d(TAG, "showDeviceList: deviceList.get(i).id :" + deviceList.get(i).id);
            SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS");
            try {
                d.nowtime.date = format.parse(d.nowtime.created);
            }catch (ParseException e){
                d.nowtime.date = new Date();
            }
            Log.d(TAG, "showDeviceList: nowtime.data = " + d.nowtime.date);
            dataList.add(deviceList.get(i));
        }
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        Log.d(TAG, "showDeviceList: 后台服务启动");
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


}
