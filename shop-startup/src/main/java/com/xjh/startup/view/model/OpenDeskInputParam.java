package com.xjh.startup.view.model;

import com.xjh.common.enumeration.OpenDeskResult;

public class OpenDeskInputParam {
    OpenDeskResult result;
    int customerNum;

    public OpenDeskResult getResult() {
        return result;
    }

    public void setResult(OpenDeskResult result) {
        this.result = result;
    }

    public int getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(int customerNum) {
        this.customerNum = customerNum;
    }
}
