package com.xjh.service.domain.model;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Cart;

import cn.hutool.core.codec.Base64;

public class CartVO {
    Integer id;
    Integer deskId;
    List<CartItemVO> contents;
    Long createTime;

    public static CartVO from(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartVO vo = new CartVO();
        vo.setId(cart.getId());
        vo.setDeskId(cart.getDeskId());
        vo.setCreateTime(cart.getCreateTime());
        if (CommonUtils.isNotBlank(cart.getContents())) {
            String str = Base64.decodeStr(cart.getContents());
            vo.setContents(JSONArray.parseArray(str, CartItemVO.class));
        }
        return vo;
    }

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
