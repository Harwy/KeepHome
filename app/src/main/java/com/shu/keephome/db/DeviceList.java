package com.shu.keephome.db;

import java.util.Date;

/**
 * Created by 14623 on 2018/5/7.
 * 设备；列表类
 */

public class DeviceList {

    public String devId ;

    public String devName;

    public int id;

    public nowtime nowtime;

    public class nowtime{

        public String created;

        public String message;

        public double hcho;

        public double hum;

        public double pm2_5;

        public double temp;

        public Date date;

    }
}
