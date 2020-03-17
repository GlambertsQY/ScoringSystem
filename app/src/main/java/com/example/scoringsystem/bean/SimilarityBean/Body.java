package com.example.scoringsystem.bean.SimilarityBean;

public class Body {
    private String ret_code;
    private String remark;
    private String showapi_fee_code;
    private Data data;

    public Body(String ret_code, String remark, String showapi_fee_code, Data data) {
        this.ret_code = ret_code;
        this.remark = remark;
        this.showapi_fee_code = showapi_fee_code;
        this.data = data;
    }

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShowapi_fee_code() {
        return showapi_fee_code;
    }

    public void setShowapi_fee_code(String showapi_fee_code) {
        this.showapi_fee_code = showapi_fee_code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
