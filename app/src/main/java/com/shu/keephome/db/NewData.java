package com.shu.keephome.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 14623 on 2018/5/5.
 *
 */

public class NewData extends DataSupport {
    String created;
    String message;
    double hum;
    double temp;
    double pm2_5;
    double hcho;
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

    public double getHum() {
        return hum;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(double pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public double getHcho() {
        return hcho;
    }

    public void setHcho(double hcho) {
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
