package com.shu.keephome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.keephome.db.DataProduct;
import com.shu.keephome.db.DataProductList;
import com.shu.keephome.util.HttpUtil;
import com.shu.keephome.util.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DataShowActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_show);

        // 初始化组件
        data_show_layout = (ScrollView) findViewById(R.id.data_show_layout);
        title_device_name = (TextView) findViewById(R.id.title_device_name);
        title_update_time = (TextView) findViewById(R.id.title_update_time);
        id_show = (TextView) findViewById(R.id.id_show);
        time_show = (TextView) findViewById(R.id.time_show);
        data_temp = (TextView) findViewById(R.id.data_temp);
        data_hum = (TextView) findViewById(R.id.data_hum) ;
        data_pm2_5 = (TextView) findViewById(R.id.data_pm2_5) ;
        data_hcho = (TextView) findViewById(R.id.data_hcho) ;

        device_id = this.getIntent().getStringExtra("device_id");
        Toast.makeText(DataShowActivity.this, device_id, Toast.LENGTH_SHORT).show();

        initData(device_id);
    }

    /**
     * 获取设备下信息
     */
    private void initData(String device_id){
        String userUrl = "http://39.106.213.217:8080/api/device/" + device_id + "/search/";
        HttpUtil.getHttp(userUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DataShowActivity.this, "网络错误",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                final DataProductList dataProductList = Utility.handleDataProductListResponse(s);
                Date dNow = new Date( );
                SimpleDateFormat ft = new SimpleDateFormat ("hh:mm");
                final String StringTime = ft.format(dNow).toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProductList(dataProductList, StringTime);
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




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
