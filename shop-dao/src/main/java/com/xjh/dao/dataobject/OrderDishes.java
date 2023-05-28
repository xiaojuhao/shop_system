package com.xjh.dao.dataobject;

import com.xjh.common.utils.OrElse;
import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Table("order_dishes_list")
public class OrderDishes {
    @Id
    @Column
    Long orderDishesId;
    @Column
    Integer orderId;
    @Column
    Integer subOrderId;
    @Column
    Integer dishesTypeId;
    @Column
    Integer ifDishesPackage;
    @Column
    Integer dishesId;
    @Column
    Double orderDishesPrice;
    @Column
    Double orderDishesDiscountPrice;
    @Column
    Integer orderDishesNums;
    @Column
    Integer orderDishesStatus;
    @Column
    Integer orderDishesSaletype;
    @Column
    String orderDishesOptions;
    @Column
    Integer orderDishesIfrefund;
    @Column
    Integer orderDishesIfchange;
    @Column
    Long createtime;
    @Column
    String orderDishesDiscountInfo;
    @Column
    Integer dishesPriceId;

    public Double sumOrderDishesPrice() {
        BigDecimal singlePrice = BigDecimal.valueOf(OrElse.orGet(this.orderDishesPrice, 0D));
        BigDecimal num = BigDecimal.valueOf(OrElse.orGet(this.orderDishesNums, 1));
        return singlePrice.multiply(num).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public Double sumOrderDishesDiscountPrice() {
        BigDecimal singlePrice = BigDecimal.valueOf(OrElse.orGet(this.orderDishesDiscountPrice, 0D));
        BigDecimal num = BigDecimal.valueOf(OrElse.orGet(this.orderDishesNums, 1));
        return singlePrice.multiply(num).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
