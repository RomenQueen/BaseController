package com.hzaz.base.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzaz.base.common_util.LOG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseBean implements Serializable {// 统一网络回调基本数据
    private String msg;
    private int error_code = 0;
    private int errorCode = 0;
    private int code = 0;

    private String all;

    public BaseBean() {
    }

    public int getError_code() {
        return error_code + errorCode + code;
    }

    public final void setAll(String all) {
        this.all = all;
    }

    public String getMsg() {
        return msg;
    }

    public void setRespMsg(String respMsg) {
        this.msg = respMsg;
    }

    public <T> T get(Class clazz, String... key) {
        JSONObject json;
        try {
            json = new JSONObject(all);
            if (key.length == 1) {
                return getByClass(clazz, json, key[0]);
            } else if (key.length > 1) {
                for (int i = 0; i < key.length; i++) {
                    if (i == key.length - 1) {
                        return getByClass(clazz, json, key[i]);
                    }
                    json = json.getJSONObject(key[i]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (T) new Object();
    }

    public static <T> T getByClass(Class clazz, JSONObject json, String key) {
        if (clazz.isAssignableFrom(String.class)) {
            return (T) json.optString(key);
        }
        if (clazz.isAssignableFrom(Float.class)) {
            Object o = Float.parseFloat(json.optString(key));
            return (T) o;
        }
        if (clazz.isAssignableFrom(Integer.class)) {
            return (T) ((Integer) json.optInt(key, 0));
        }
        if (clazz.isAssignableFrom(Boolean.class)) {
            return (T) ((Boolean) json.optBoolean(key, false));
        }
        if (clazz.isAssignableFrom(Long.class)) {
            return (T) ((Long) json.optLong(key, 0L));
        }
        if (clazz.isAssignableFrom(Double.class)) {
            return (T) ((Double) json.optDouble(key, 0));
        }
        return (T) new Object();
    }

    public <T> List<T> getList(String... key) {
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();

        LOG.e("BaseBean", "WorkerListController.type:" + type);
        LOG.e("BaseBean", "WorkerListController.type:" + type.getClass());
        LOG.e("BaseBean", "WorkerListController.type:" + type.getClass().getSimpleName());
        JSONObject json;
        try {
            json = new JSONObject(all);
            if (key.length == 1) {
                List<T> lists = new Gson().fromJson(json.optString(key[0]), type);
                return lists;
            } else if (key.length > 1) {
                for (int i = 0; i < key.length; i++) {
                    if (i == key.length - 1) {
                        List<T> lists = new Gson().fromJson(json.optString(key[i]), type);
                        return lists;
                    }
                    json = json.getJSONObject(key[i]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

    public String getAll() {
        return all;
    }
}
