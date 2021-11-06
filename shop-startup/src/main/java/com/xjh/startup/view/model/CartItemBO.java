package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.InputNumber;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;

import lombok.Data;

@Data
public class CartItemBO {
    Integer cartDishesId;
    Integer dishesId;
    RichText dishesTypeName;
    RichText dishesName;
    String dishesPriceId;
    Money dishesPrice;
    InputNumber nums;
    Money totalPrice;
    String ifDishesPackage;
    String attrRemark;
    Integer seqNo;
}
