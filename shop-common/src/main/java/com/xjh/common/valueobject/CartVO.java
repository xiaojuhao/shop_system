package com.xjh.common.valueobject;

import java.util.List;

public class CartVO {
    Integer id;
    Integer deskId;
    List<CartItemVO> contents;
    Long createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }

    public List<CartItemVO> getContents() {
        return contents;
    }

    public void setContents(List<CartItemVO> contents) {
        this.contents = contents;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
