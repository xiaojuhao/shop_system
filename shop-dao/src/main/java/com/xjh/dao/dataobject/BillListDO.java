package com.xjh.dao.dataobject;

import com.xjh.common.enumeration.EnumPayMethod;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.dao.foundation.*;
import lombok.Data;

@Data
@Table("bill_list")
public class BillListDO {

    @Id
    @Column("id")
    public int id;
    public int orderNums;
    public int customerNums;
    @Column
    public int h5OrderNums;
    @Column
    public double totalDishesPrice;
    @Column
    public double totalErasePrice;
    @Column
    public double totalDiscountPrice;
    @Column
    public double totalReturnPrice;
    @Column
    public double totalHadPaidPrice;
    @Column
    public double totalRefundPrice;
    @Column
    public double totalFreePrice;
    @Column
    public int totalFreeNums;
    @Column
    public double totalEscapePrice;
    @Column
    public int totalEscapeNums;
    @Column
    public double totalUnpaidPrice;
    @Column
    public int totalUnpaidNums;
    @Column
    @SumTotalPrice(EnumPayMethod.CASH)
    public double methodCashTotal;
    @Column
    public int methodCashTotalNums;
    @Column
    @SumTotalPrice(EnumPayMethod.BANKCARD)
    public double methodBankcardTotal;
    @Column
    public int methodBankcardTotalNums;
    @Column
    @SumTotalPrice(EnumPayMethod.WECHAT)
    public double methodWechatTotal;
    @Column
    public int methodWechatTotalNums;
    @Column
    @SumTotalPrice(EnumPayMethod.ALIPAY)
    public double methodAlipayTotal;
    @Column
    public int methodAlipayTotalNums;
    @Column
    @SumTotalPrice(EnumPayMethod.STORECARD)
    public double methodStoreCardTotal;
    @Column
    public int methodStoreCardTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.STORECARD)
    public double actualStoreCardTotal;
    @Column
    @SumTotalPrice(EnumPayMethod.MEITUAN_COUPON)
    public double methodMeituanTotal;
    @Column
    public int methodMeituanTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.MEITUAN_COUPON)
    public double actualMeituanTotal;
    @Column
    @SumTotalPrice(EnumPayMethod.COUPON)
    public double methodCouponTotal;
    @Column
    public int methodCouponTotalNums;
    @Column
    @SumTotalPrice(EnumPayMethod.KOUBEI)
    public double methodKouBeiTotal;
    @Column
    public int methodKouBeiTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.KOUBEI)
    public double actualKouBeiTotal;
    @Column
    @SumTotalPrice(EnumPayMethod.WECHAT_OFFICIAL)
    public double methodPublicSignalTotal;
    @Column
    public int methodPublicSignalTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.WECHAT_OFFICIAL)
    public double actualPublicSignalTotal;
    @Column
    public long dateTime;
    @Column
    @SumTotalPrice(EnumPayMethod.WECHAT_COUPON)
    public double methodWechatCouponTotal;
    @Column
    public int methodWechatCouponTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.WECHAT_COUPON)
    public double actualWechatCouponTotal;
    @Column
    public double totalReductionPrice;
    @Column
    @SumTotalPrice(EnumPayMethod.MEITUAN_PACKAGE)
    public double methodPackageTotal;
    @Column
    public int methodPackageTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.MEITUAN_PACKAGE)
    public double actualPackageTotal;
    @Column
    @SumTotalPrice(EnumPayMethod.STORE_REDUCTION)
    public double methodStoreReduceTotal;
    @Column
    public int methodStoreReduceTotalNums;
    @Column
    public double totalReturnCashPrice;

    @Column
    @SumActualPrice(EnumPayMethod.OHTER)
    public double methodOtherTotal;
    @Column
    public int methodOtherTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.OHTER)
    public double actualOtherTotal;

    // 万达券
    @Column
    @SumTotalPrice(EnumPayMethod.WANDA_COUPON)
    public double methodWandaTotal;
    @Column
    public int methodWandaTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.WANDA_COUPON)
    public double actualWandaTotal;

    // 万达套餐券
    @Column
    @SumTotalPrice(EnumPayMethod.WANDA_PACKAGE)
    public double methodWandaPackageTotal;
    @Column
    public int methodWandaPackageTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.WANDA_PACKAGE)
    public double actualWandaPackageTotal;

    @Column
    @SumTotalPrice(EnumPayMethod.SELFHELP_PRICE)
    public double methodSelfHelpPriceTotal;

    // 微生活代金券
    @Column
    @SumTotalPrice(EnumPayMethod.TINY_LIFE_COUPON)
    public double methodTinyLifeCouponTotal;
    @Column
    public int methodTinyLifeCouponTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.TINY_LIFE_COUPON)
    public double actualTinyLifeCouponTotal;

    // 微生活积分抵扣
    @Column
    @SumTotalPrice(EnumPayMethod.TINY_LIFE_INTEGRAL_DEDUCTION)
    public double methodTinyLifeIntegralDeductionTotal;
    @Column
    public int methodTinyLifeIntegralDeductionTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.TINY_LIFE_INTEGRAL_DEDUCTION)
    public double actualTinyLifeIntegralDeductionTotal;

    // 微生活储值卡
    @Column
    @SumTotalPrice(EnumPayMethod.TINY_LIFE_STORECARD)
    public double methodTinyLifeStorecardTotal;
    @Column
    public int methodTinyLifeStorecardTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.TINY_LIFE_STORECARD)
    public double actualTinyLifeStorecardTotal;


    @Column
    @SumTotalPrice(EnumPayMethod.WECHAT_UNIONPAY_PAYMEN)
    public double methodWeChatUnionpayPayMentTotal;
    @Column
    public int methodWeChatUnionpayPayMentTotalNums;

    //外卖
    @Column
    @SumTotalPrice(EnumPayMethod.外卖)
    public double methodTakeOutTotal;
    @Column
    public int methodTakeOutTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.外卖)
    public double actualTakeOutTotal;

    //银联pos
    @Column
    @SumTotalPrice(EnumPayMethod.UNIONPAY_POS)
    public double methodUnionpayPosTotal;
    @Column
    public int methodUnionpayPosTotalNums;

    //交行活动
    @Column
    @SumTotalPrice(EnumPayMethod.TRAFFIC_ACTIVITIES)
    public double methodTrafficActivitiesTotal;
    @Column
    public int methodTrafficActivitiesTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.TRAFFIC_ACTIVITIES)
    public double actualTrafficActivitiesTotal;

    //招行活动
    @Column
    @SumTotalPrice(EnumPayMethod.MERCHANTS_ACTIVITIES)
    public double methodMerchantsActivitiesTotal;
    @Column
    public int methodMerchantsActivitiesTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.MERCHANTS_ACTIVITIES)
    public double actualMerchantsActivitiesTotal;

    //商场活动
    @Column
    @SumTotalPrice(EnumPayMethod.MARKET_ACTIVITIES)
    public double methodMarketActivitiesTotal;
    @Column
    public int methodMarketActivitiesTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.MARKET_ACTIVITIES)
    public double actualMarketActivitiesTotal;

    //美团闪惠
    @Column
    @SumTotalPrice(EnumPayMethod.MEITUAN_SHANHUI)
    public double methodMeituanShanhuiTotal;
    @Column
    public int methodMeituanShanhuiTotalNums;
    @Column
    @SumActualPrice(EnumPayMethod.MEITUAN_SHANHUI)
    public double actualMeituanShanhuiTotal;

    // 平均每客单价
    public Money custAvgPrice() {
        if (customerNums <= 0) {
            return new Money(0D);
        }
        return new Money(actualAmountFull() / customerNums);
    }

    public Money avgPrice() {
        if (orderNums <= 0) {
            return new Money(0D);
        }
        return new Money(actualAmountFull() / orderNums);
    }

    public double actualAmountFull() {
        return methodCashTotal + methodBankcardTotal + methodWechatTotal + methodAlipayTotal + actualMeituanTotal + actualKouBeiTotal + actualPackageTotal + actualPublicSignalTotal + actualOtherTotal + methodCouponTotal + actualTinyLifeCouponTotal + actualTinyLifeIntegralDeductionTotal + actualTinyLifeStorecardTotal + methodWechatTotal + actualWandaTotal + actualWandaPackageTotal;
    }


}
