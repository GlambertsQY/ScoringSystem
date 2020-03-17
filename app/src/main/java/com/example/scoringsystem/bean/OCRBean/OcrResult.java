package com.example.scoringsystem.bean.OCRBean;

public class OcrResult {
    private String code;
    private Data data;
    private String desc;
    private String sid;

    public OcrResult(String code, Data data, String desc, String sid) {
        this.code = code;
        this.data = data;
        this.desc = desc;
        this.sid = sid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
