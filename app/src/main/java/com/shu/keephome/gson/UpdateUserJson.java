package com.shu.keephome.gson;

import com.google.gson.Gson;
import com.shu.keephome.db.Person;
import com.shu.keephome.db.User;

/**
 * Created by 14623 on 2018/5/10.
 *
 */

public class UpdateUserJson {
    public static String UpdateUserJSON(String username, String address, String email, String age, int sex){
        // 组成数据
        User user = new User();
        user.setUserName(username);
        user.setUserAddress(address);
        user.setUserEmail(email);
        user.setUserAge(age);
        user.setUserSex(sex);
        // 转化为json
        Gson gson = new Gson();
        String jsonObject = gson.toJson(user);
        System.out.println("json组成为：" + jsonObject);
        return jsonObject;
    }
}
