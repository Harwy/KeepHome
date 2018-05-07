package com.shu.keephome;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shu.keephome.db.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawableActivity extends AppCompatActivity {

//    private DrawerLayout mDrawerLayout;
//
//
//    // 模拟数据
//    private Data[] datas = {
//            new Data("2018-5-5","0",100,25,32,45,1,false),
//            new Data("2018-5-5","0",100,25,32,45,1,false),
//            new Data("2018-5-5","0",100,25,32,45,1,false),
//            new Data("2018-5-5","0",100,25,32,45,1,false),
//            new Data("2018-5-5","0",100,25,32,45,1,false),
//    };
//
//    private List<Data> dataList = new ArrayList<>();
//
//    private DataListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
//        }

//        initData();
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new DataListAdapter(dataList);
//        recyclerView.setAdapter(adapter);

    }


//    /**
//     * 侧面栏
//     * @param item
//     * @return
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()){
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                break;
//            default:
//        }
//        return true;
//    }

//    /**
//     * 带入数据
//     */
//    private void initData(){
//        dataList.clear();
//        for (int i=0;i<50;i++){
//            Random random = new Random();
//            int index = random.nextInt(datas.length);
//            dataList.add(datas[index]);
//        }
//    }
}
