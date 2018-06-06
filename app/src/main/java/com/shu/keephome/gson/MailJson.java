package com.shu.keephome.gson;

import com.google.gson.Gson;
import com.shu.keephome.db.CheckMail;
import com.shu.keephome.db.Mail;


/**
 * Created by 14623 on 2018/6/6.
 *
 */

public class MailJson {
    public static String MailJson(String email, String mode){
        // 组成数据
        Mail mail = new Mail();
        mail.setEmail(email);
        mail.setMode(mode);
        // 转化为json
        Gson gson = new Gson();
        String jsonObject = gson.toJson(mail);
        return jsonObject;
    }

    public static String SignJson(String name, String password, String email, String check){
        // 组成数据
        CheckMail checkMail = new CheckMail();
        checkMail.setName(name);
        checkMail.setPassword(password);
        checkMail.setEmail(email);
        checkMail.setCheck(check);
        // 转化为json
        Gson gson = new Gson();
        String jsonObject = gson.toJson(checkMail);
        return jsonObject;
    }


}
