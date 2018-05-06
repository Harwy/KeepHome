package com.shu.keephome.gson;

import com.google.gson.Gson;
import com.shu.keephome.db.Device;
import com.shu.keephome.db.Person;

/**
 * Created by 14623 on 2018/5/6.
 *
 */

public class PersonJson {

    public static String PersonJSON(String username, String password){
        // 组成数据
        Person person = new Person();
        person.setUsername(username);
        person.setPassword(password);
        // 转化为json
        Gson gson = new Gson();
        String jsonObject = gson.toJson(person);
        System.out.println("json组成为：" + jsonObject);
        return jsonObject;
    }
}
