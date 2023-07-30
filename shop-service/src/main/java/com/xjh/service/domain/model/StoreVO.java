package com.xjh.service.domain.model;

import com.xjh.common.valueobject.DishesGroupVO;
import lombok.Data;

import java.util.List;

@Data
public class StoreVO {
    private int storeId;
    private String name;
    private String address;
    private String email;
    private String phone;
    private double discount;
    private int status;
    private int stockMode;
    private String ownerPassword;
    private String clerkPassword;
    private List<DishesGroupVO> storeDishesGroups;
    private List<DishesGroupVO> managerDishesGroups;
    private List<DishesGroupVO> memberDishesGroups;
    private List<DishesGroupVO> weChatDishesGroups;
    private int ifUsing;
    private int activityType;
    private double fullMoney;
    private double reduceMoney;
}
