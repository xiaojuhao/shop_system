package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("store")
public class Store {
    @Id
    @Column("storeId")
    private Integer storeId;
    @Column("name")
    private String name;
    @Column("address")
    private String address;
    @Column("email")
    private String email;
    @Column("phone")
    private String phone;
    @Column("discount")
    private Double discount;
    @Column("status")
    private Integer status;
    @Column("stockMode")
    private Integer stockMode;
    @Column("ownerPassword")
    private String ownerPassword;
    @Column("clerkPassword")
    private String clerkPassword;
    @Column("storeDishesGroupIds")
    private String storeDishesGroupIds;
    @Column("managerDishesGroupIds")
    private String managerDishesGroupIds;
    @Column("memberDishesGroupIds")
    private String memberDishesGroupIds;
    @Column("weChatDishesGroupIds")
    private String weChatDishesGroupIds;
    @Column("ifUsing")
    private Integer ifUsing;
    @Column("activityType")
    private Integer activityType;
    @Column("fullMoney")
    private Double fullMoney;
    @Column("reduceMoney")
    private Double reduceMoney;
}
