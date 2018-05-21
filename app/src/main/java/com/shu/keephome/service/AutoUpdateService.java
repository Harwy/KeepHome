package com.shu.keephome.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.shu.keephome.DrawableActivity;
import com.shu.keephome.LoginActivity;
import com.shu.keephome.LoginFragment;
import com.shu.keephome.R;
import com.shu.keephome.util.HttpUtil;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class AutoUpdateService extends Service {

    private int FLAG = 0;

    private String message;

    private String noticeTopic;

    public int NOTIFICATION_ID = 10;

    private String noticeHum = "环境温度过高";

    private String noticeTemp = "环境湿度过高";

    private String noticePm2_5 = "环境PM2.5过高";

    private String noticeWarning = "危险！甲烷浓度过高！";

    public AutoUpdateService() {
    }


    @Override
    public void onCreate() {
        Intent notifyIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notifyIntent,0);

        Notification notification = new Notification.Builder(this).
                setSmallIcon(R.drawable.ic_notice).
                setContentTitle("后台启动").
                setContentText("正在保驾护航你的家~").
                setContentIntent(pendingIntent).
                setDefaults(Notification.DEFAULT_ALL). // 设置用手机默认的震动或声音来提示
                build();

        // 设置为前台服务,在系统状态栏显示
        startForeground(1, notification);

        super.onCreate();
    }

    /**
     * 定时更新主线程
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        updateBingPic();
        Log.d(TAG, "onStartCommand: >>>>>>>后台在工作哦");
        updateNotice();
        if (FLAG == 1){
            Log.d(TAG, "onStartCommand: message" + message);
            if (message.equals("1")){
                noticeTopic = noticeHum;
            }else if (message.equals("2")){
                noticeTopic = noticeTemp;
            }else if (message.equals("3")){
                noticeTopic = noticePm2_5;
            }else if (message.equals("4")){
                noticeTopic = noticeWarning;
            }
            Log.d(TAG, "onStartCommand: noticeTopic" + noticeTopic);
            startNotification(noticeTopic);
            FLAG = 0;
        }

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 10 * 1000;    // 10s的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0 , i , 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //在Service结束后关闭AlarmManager
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent i = new Intent(this, AutoUpdateService.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        manager.cancel(pi);
    }

    /**
     * 进程销毁
     * @param intent
     * @return
     */


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        initNotification();
            return null;

//        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void startNotification(String message) {
        // BEGIN_INCLUDE(build_action)
        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder mNotifyBuilder =
                new Notification.Builder(this)
                        .setContentTitle("警告！")
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setFullScreenIntent(pendingIntent, false)
                        .setContentIntent(pendingIntent);
//                        .setAutoCancel(true);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

// Builds the notification and issues it.
        mNotifyMgr.notify(NOTIFICATION_ID++, mNotifyBuilder.build());
        // END_INCLUDE(send_notification)
    }


    /**
     * 更新必应每日一图
     */
    private void updateBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.getHttp(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }


    /**
     * 更新警报消息队列通知栏
     */
    private void updateNotice(){
        String requestNotice = "http://39.106.213.217:8080/api/notice/";
        HttpUtil.getHttp(requestNotice, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String notice = response.body().string();
                Log.d(TAG, "onResponse: notice" + notice);
                try {
                    JSONObject jsonObject = new JSONObject(notice);
                    int state = jsonObject.getInt("state");
                    Log.d(TAG, "onResponse: state" + state);
                    if (state == 1){
                        message = jsonObject.getString("command");
                        FLAG = 1;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }


}
