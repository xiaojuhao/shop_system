package com.xjh.common.valueobject;

import java.util.List;

import lombok.Data;

@Data
public class CartVO {
    Integer id;
    Integer deskId;
    List<CartItemVO> contents;
    Long createTime;

}
