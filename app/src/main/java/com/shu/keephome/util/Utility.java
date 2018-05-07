package com.shu.keephome.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shu.keephome.db.Data;
import com.shu.keephome.db.Device;
import com.shu.keephome.db.DeviceList;
import com.shu.keephome.db.NewData;
import com.shu.keephome.db.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 14623 on 2018/5/1.
 *
 */

public class Utility{


    /**
     * 解析和处理服务器返回的用户信息
     */
    public static boolean handleUserResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allusers = new JSONArray(response);
                for (int i = 0; i<allusers.length();i++){
                    JSONObject userObject = allusers.getJSONObject(i);
                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setName(userObject.getString("userName"));
                    user.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的设备信息
     */
    public static boolean handleDevResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allDevices = new JSONArray(response);
                for (int i = 0; i<allDevices.length();i++){
                    JSONObject deviceObject = allDevices.getJSONObject(i);
                    Device device = new Device();
                    device.setId(deviceObject.getInt("id"));
                    device.setName(deviceObject.getString("devName"));
                    device.setDevtag(deviceObject.getInt("devTag"));
                    device.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的设备数据信息
     */
    public static boolean handleDataResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allData = new JSONArray(response);
                for (int i = 0; i<allData.length();i++){
                    JSONObject dataObject = allData.getJSONObject(i);
                    NewData data = new NewData();
                    data.setCreated(dataObject.getString("created"));
                    data.setTemp(dataObject.getDouble("temp"));
                    data.setHum(dataObject.getDouble("hum"));
                    data.setPm2_5(dataObject.getDouble("pm2_5"));
                    data.setHcho(dataObject.getDouble("hcho"));
                    data.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的设备数据的最新一条数据信息
     */
    public static NewData handleNewDataResponse(String response){
        try {
            Gson gson = new Gson();
            NewData t = gson.fromJson(response, NewData.class);
            return t;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将返回的JSON数据解析成DeviceList实体类
     */
    public static List<DeviceList>  handleDeviceListResponse(String response){
        try {
            return new Gson().fromJson(response, new TypeToken<List<DeviceList>>()
            {}.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
