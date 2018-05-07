package com.shu.keephome.db;

/**
 * Created by 14623 on 2018/5/7.
 * 设备；列表类
 */

public class DeviceList {

    public String devID ;

    public String devName;

    public int id;

    public nowtime nowtime;

    public class nowtime{

        public String created;

        public double hcho;

        public double hum;

        public double pm2_5;

        public double temp;

    }
}
