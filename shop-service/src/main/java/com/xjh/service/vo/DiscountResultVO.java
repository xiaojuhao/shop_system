package com.xjh.service.vo;

import com.xjh.common.utils.CommonUtils;
import lombok.Data;

import java.math.BigDecimal;

import static com.xjh.common.utils.CommonUtils.add;
import static com.xjh.common.utils.CommonUtils.subtract;

@Data
public class DiscountResultVO {
    BigDecimal totalPrice; // 总价格
    BigDecimal discountPrice; // 折扣后的价格
    BigDecimal oldDiscountPrice; // 原折扣方案的折扣金额
    BigDecimal discountAmount; // 优惠额度

    public void addTotalPrice(Double totalPrice) {
        this.totalPrice = add(this.totalPrice, totalPrice);
    }

    public void addDiscountPrice(Double discountPrice) {
        this.discountPrice = add(this.discountPrice, discountPrice);
    }

    public void addOldDiscountPrice(Double oldDiscountPrice) {
        this.oldDiscountPrice = add(this.oldDiscountPrice, oldDiscountPrice);
    }

    public BigDecimal getDiscountAmount() {
        return subtract(totalPrice, discountPrice);
    }

    public BigDecimal getOldDiscountAmount() {
        return subtract(totalPrice, oldDiscountPrice);
    }
}
