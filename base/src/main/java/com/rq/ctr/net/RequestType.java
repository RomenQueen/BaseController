package com.rq.ctr.net;

import android.text.TextUtils;

import java.util.Map;


public class RequestType {
    private String type;
    private int pageNum = 0;
    public static final String PAGE_NUM_TAG = "pageNo";
    private int respCode;
    private String respMsg;

    public int getRespCode() {
        return respCode;
    }

    public String getErrorMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public RequestType(String type, Map<String, String> param) {
        this.type = type;
        try {
            this.pageNum = Integer.parseInt(param.get(PAGE_NUM_TAG));
        } catch (Exception e) {
        }
    }

    public RequestType(String type) {
        this.type = type;
    }

    public boolean is(String tag) {
        return TextUtils.equals(tag, type);
    }

    public int getPageNum() {
        return pageNum;
    }

    public boolean isNetError() {
        return respCode < 0;
    }

    public RequestType with(String respCode, String msg) {
        this.respMsg = msg;
        try {
            this.respCode = Integer.parseInt(respCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean is(int errorCode) {
        return respCode == errorCode;
    }
}
