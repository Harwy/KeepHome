package com.shu.keephome.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 14623 on 2018/5/1.
 * 数据集
 */

public class Data extends DataSupport{
    String created;
    String message;
    float hum;
    float temp;
    float pm2_5;
    float hcho;
    int device;
    Boolean isDelete;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getHum() {
        return hum;
    }

    public void setHum(float hum) {
        this.hum = hum;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(float pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public float getHcho() {
        return hcho;
    }

    public void setHcho(float hcho) {
        this.hcho = hcho;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }
}
