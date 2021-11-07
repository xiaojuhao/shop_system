package com.xjh.common.valueobject;

import lombok.Data;

@Data
public class PageCond {
    Integer pageNo = 1;
    Integer pageSize = 20;

    public void increasePageNo() {
        if (getPageNo() != null) {
            setPageNo(getPageNo() + 1);
        } else {
            setPageNo(1);
        }
    }

    public void decreasePageNo() {
        if (getPageNo() == null) {
            setPageNo(1);
        }
        if (getPageNo() > 1) {
            setPageNo(getPageNo() - 1);
        }
    }
}
