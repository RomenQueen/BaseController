package com.hzaz.base.common_util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static boolean isPhone(String num) {
        if (num == null) return false;
        Pattern pattern = Pattern.compile("^1\\d{10}$");
        return pattern.matcher(num).find();
    }

    public static String getTimeShowStr(long time) {
        if (time > 0) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            return format.format(new Date(time));
        }
        LOG.utilLog("错误调用，time不能小于0");
        return "";
    }

    public static String getShowPhone(String member_name) {
        if (!TextUtils.isEmpty(member_name)) {
            if (member_name.length() > 7) {
                return member_name.substring(0, 3) + "****" + member_name.substring(7);
            }
        }
        return "";
    }

    public static String getShowEmail(String mail) {
        try {
            int atIndex = mail.indexOf("@");
            int qianMian = (atIndex - 4 < 1) ? 1 : (atIndex - 4);
            int houMian = qianMian + 4 > atIndex ? atIndex : (qianMian + 4);
            return mail.substring(0, qianMian) + "****" + mail.substring(houMian);
        } catch (Exception e) {
        }
        return "";
    }

    public static boolean isPassWordOk(String pass) {
        Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$");
        return pattern.matcher(pass).find();
    }

    public static boolean isNameOk(String pass) {
        Pattern pattern = Pattern.compile("^^[\\u4E00-\\u9FA5]{2,4}$$");
        return pattern.matcher(pass).find();
    }

    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
