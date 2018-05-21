package com.shu.keephome;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.shu.keephome.db.DataProduct;
import com.shu.keephome.db.DataProductList;
import com.shu.keephome.util.HttpUtil;
import com.shu.keephome.util.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.Math.round;

public class DataShowActivity extends AppCompatActivity {

    // 缓存参数
    private String device_id;

    private ScrollView data_show_layout;

    private TextView title_device_name;

    private TextView title_update_time;

    private TextView id_show;

    private TextView time_show;

    private TextView data_temp;

    private TextView data_hum;

    private TextView data_pm2_5;

    private TextView data_hcho;

    private ProgressDialog progressDialog;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;

    private Button home;

    double[] hum = new double[30];

    double[] temp = new double[30];

    double[] pm2_5 = new double[30];

    double[] hcho = new double[30];

    String[] created = new String[30];

    // 图表参数
    final LineChart[] mCharts = new LineChart[4];




    public class chartData{
        private double valueX;
        private double valueY;

        public double getValueY() {
            return valueY;
        }

        public void setValueY(double valueY) {
            this.valueY = valueY;
        }

        public double getValueX() {

            return valueX;
        }

        public void setValueX(double valueX) {
            this.valueX = valueX;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_show);

        // 初始化组件
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.data_refresh) ;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        data_show_layout = (ScrollView) findViewById(R.id.data_show_layout);
        title_device_name = (TextView) findViewById(R.id.title_device_name);
        title_update_time = (TextView) findViewById(R.id.title_update_time);
        id_show = (TextView) findViewById(R.id.id_show);
        time_show = (TextView) findViewById(R.id.time_show);
        data_temp = (TextView) findViewById(R.id.data_temp);
        data_hum = (TextView) findViewById(R.id.data_hum) ;
        data_pm2_5 = (TextView) findViewById(R.id.data_pm2_5) ;
        data_hcho = (TextView) findViewById(R.id.data_hcho) ;
        home = (Button) findViewById(R.id.nav_button);

        mCharts[0] = (LineChart) findViewById(R.id.chart);
        mCharts[1] = (LineChart) findViewById(R.id.chart_hum);
        mCharts[2] = (LineChart) findViewById(R.id.chart_pm2_5);
        mCharts[3] = (LineChart) findViewById(R.id.chart_hcho);

        // 图表
//        lineChart = (LineChartView)findViewById(R.id.line_chart);
//        lineChart_hum = (LineChartView)findViewById(R.id.line_chart_hum);


        // 缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        device_id = prefs.getString("device_id", null);

        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm");
        final String StringTime = ft.format(dNow).toString();

        if (device_id != null){
            // 有缓存时直接读取
            DataProductList dataProductList = Utility.handleDataProductListResponse(device_id);
            showProductList(dataProductList, StringTime);
        }else {
            // 无缓存时直接去服务器查询
            device_id = this.getIntent().getStringExtra("device_id");
            initData(device_id, StringTime);
        }
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }

        loadBingPic();  //加载图片

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(device_id, StringTime);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }




    /**
     * 获取设备下信息
     */
    private void initData(String device_id, final String StringTime){
        String userUrl = "http://39.106.213.217:8080/api/device/" + device_id + "/search/";
        HttpUtil.getHttp(userUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DataShowActivity.this, "网络错误",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                final DataProductList dataProductList = Utility.handleDataProductListResponse(s);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProductList(dataProductList, StringTime);
                        for (int i = 0; i < mCharts.length; i++) {
                            // add some transparency to the color with "& 0x90FFFFFF"
                            if (i == 0){
                                chartGet(mCharts[i], temp, i);
                            }else if (i == 1){
                                chartGet(mCharts[i], hum, i);
                            }else if (i == 2){
                                chartGet(mCharts[i], pm2_5, i);
                            }else {
                                chartGet(mCharts[i], hcho, i);
                            }

                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 数据展示
     */
    public void showProductList(DataProductList dataProductList, String StringTime){
        String device_name = dataProductList.devName;
        String device_id = "ID:"+ dataProductList.devId;
        List<DataProduct> dataProducts = dataProductList.nowtime;
        title_device_name.setText(device_name);
        id_show.setText(device_id);
        for (int i = 0; i< dataProducts.size(); i++){
            created[i] = dataProducts.get(i).created;
            hum[i] = dataProducts.get(i).hum;
            temp[i] = dataProducts.get(i).temp;
            pm2_5[i] = dataProducts.get(i).pm2_5;
            hcho[i] = dataProducts.get(i).hcho;
        }
        DataProduct dataProduct = dataProducts.get(0);
        SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            dataProduct.date = format.parse(dataProduct.created);
        }catch (ParseException e){
            dataProduct.date = new Date();
        }
        String device_created = "近期更新：" + String.valueOf(dataProduct.date);
        String hum = String.valueOf(dataProduct.hum) + "%";
        String temp = String.valueOf(dataProduct.temp) + "℃";
        String pm2_5 = String.valueOf(dataProduct.pm2_5);
        String hcho = String.valueOf(dataProduct.hcho);
        time_show.setText(device_created);
        title_update_time.setText(StringTime);
        data_temp.setText(temp);
        data_hum.setText(hum);
        data_pm2_5.setText(pm2_5);
        data_hcho.setText(hcho);
    }

    /**
     * 加载必应每日图片
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.getHttp(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DataShowActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(DataShowActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }


    /**
     * 图表加载
     */
    private void chartGet(LineChart chart, double[] a, int i){
        List<Entry> chartDataList = new ArrayList<>();
        for(int y=0;y<30;y++){
            chartData chartdata = new chartData();
            chartdata.setValueX(y+1);
            chartdata.setValueY((Math.round(a[29-y]*100))/100);
            chartDataList.add(new BarEntry((float)(chartdata.getValueX()),(float)(chartdata.getValueY())));
        }
        String[] db = new String[]{
                "近期温度折线图" ,
                "近期湿度折线图",
                "近期PM2.5折线图",
                "近期甲烷浓度折线图",
        };
        LineDataSet dataSet = new LineDataSet(chartDataList, db[i]); // add entries to dataset
        dataSet.setLineWidth(1.75f); // 线宽
        dataSet.setCircleSize(2f);// 显示的圆形大小
        dataSet.setColor(Color.rgb(89, 194, 230));// 折线显示颜色
        dataSet.setCircleColor(Color.rgb(89, 194, 230));// 圆形折点的颜色
        dataSet.setHighLightColor(Color.GREEN); // 高亮的线的颜色
        dataSet.setHighlightEnabled(true);
        dataSet.setValueTextColor(Color.rgb(89, 194, 230)); //数值显示的颜色
        dataSet.setValueTextSize(8f);     //数值显示的大小
//        dataSet.setHighLightColor(Color.RED);
//        dataSet.setDrawValues(true); // 是否在点上绘制Value
//        dataSet.setValueTextColor(Color.GREEN);
//        dataSet.setValueTextSize(12f);
        LineData lineData = new LineData(dataSet);
        chart.setTouchEnabled(true); //可点击
        chart.setDragEnabled(true);  //可拖拽
        chart.setScaleEnabled(true);  //可缩放
        chart.setPinchZoom(false);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
