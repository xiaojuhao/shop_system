package com.xjh.common.valueobject;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.CopyUtils;
import lombok.Data;

@Data
public class OrderOverviewVO {
    public String deskName;
    public Integer deskId;
    public String orderId;
    public int customerNum;
    public String orderTime;
    public String payStatusName;
    public double orderNeedPay;
    public double totalPrice;
    public String discountName;
    public double discountAmount;
    public double discountableAmount;
    public double orderHadpaid;
    public double orderRefund;
    public double orderReduction;
    public double deduction;
    public double orderErase;
    public double returnDishesPrice;
    public double returnedCash;
    public String payInfoRemark;

    public long copytime;

    public OrderOverviewVO newVer(){
        OrderOverviewVO newObj = CopyUtils.cloneObj(this);
        newObj.copytime = CommonUtils.randomNumber(1, Integer.MAX_VALUE);
        return this;
    }
}
