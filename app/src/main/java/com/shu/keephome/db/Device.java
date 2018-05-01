package com.shu.keephome.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 14623 on 2018/5/1.
 * 设备信息表
 */

public class Device extends DataSupport{
    int id;

    String name;

    int devtag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDevtag() {
        return devtag;
    }

    public void setDevtag(int devtag) {
        this.devtag = devtag;
    }
}
