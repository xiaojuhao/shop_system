CREATE TABLE `accounts` (
  `accountId` int(11) NOT NULL AUTO_INCREMENT,
  `accountUser` varchar(50) NOT NULL,
  `accountNickName` varchar(225) NOT NULL,
  `accountPass` varchar(32) NOT NULL,
  `accountFather` varchar(50) DEFAULT NULL,
  `accountRight` text,
  `removeLimit` double DEFAULT NULL,
  `isDefault` int(1) NOT NULL,
  `creatTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`accountId`),
  UNIQUE KEY `accountUser` (`accountUser`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

CREATE TABLE `bill_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderNums` int(8) NOT NULL,
  `customerNums` int(8) NOT NULL,
  `h5OrderNums` int(8) NOT NULL,
  `totalDishesPrice` double(11,2) NOT NULL,
  `totalErasePrice` double(11,2) NOT NULL,
  `totalDiscountPrice` double(11,2) NOT NULL,
  `totalReturnPrice` double(11,2) NOT NULL,
  `totalHadPaidPrice` double(11,2) NOT NULL,
  `totalRefundPrice` double(11,2) NOT NULL,
  `totalFreePrice` double(11,2) NOT NULL,
  `totalFreeNums` int(8) NOT NULL,
  `totalEscapePrice` double(11,2) NOT NULL,
  `totalEscapeNums` int(8) NOT NULL,
  `totalUnpaidPrice` double(11,2) NOT NULL,
  `totalUnpaidNums` int(8) NOT NULL,
  `methodCashTotal` double(11,2) NOT NULL,
  `methodCashTotalNums` int(8) NOT NULL,
  `methodBankcardTotal` double(11,2) NOT NULL,
  `methodBankcardTotalNums` int(8) NOT NULL,
  `methodWechatTotal` double(11,2) NOT NULL,
  `methodWechatTotalNums` int(8) NOT NULL,
  `methodAlipayTotal` double(11,2) NOT NULL,
  `methodAlipayTotalNums` int(8) NOT NULL,
  `methodStoreCardTotal` double(11,2) NOT NULL,
  `methodStoreCardTotalNums` int(8) NOT NULL,
  `methodMeituanTotal` double(11,2) NOT NULL,
  `methodMeituanTotalNums` int(8) NOT NULL,
  `methodCouponTotal` double(11,2) NOT NULL,
  `methodCouponTotalNums` int(8) NOT NULL,
  `methodKouBeiTotal` double(11,2) NOT NULL,
  `methodKouBeiTotalNums` int(8) NOT NULL,
  `methodPublicSignalTotal` double(11,2) NOT NULL,
  `methodPublicSignalTotalNums` int(8) NOT NULL,
  `methodWechatCouponTotal` double(11,2) NOT NULL,
  `actualWechatCouponTotal` double(11,2) NOT NULL,
  `methodWechatCouponTotalNums` int(8) NOT NULL,
  `actualMeituanTotal` double(11,2) NOT NULL,
  `actualKouBeiTotal` double(11,2) NOT NULL,
  `actualPublicSignalTotal` double(11,2) NOT NULL,
  `totalReductionPrice` double(11,2) NOT NULL,
  `methodPackageTotal` double(11,2) NOT NULL,
  `methodPackageTotalNums` int(8) NOT NULL,
  `methodStoreReduceTotal` double(11,2) NOT NULL,
  `methodStoreReduceTotalNums` int(8) NOT NULL,
  `dateTime` bigint(13) NOT NULL,
  `totalReturnCashPrice` double(11,2) NOT NULL,
  `actualPackageTotal` double(11,2) NOT NULL,
  `methodOtherTotal` double(11,2) NOT NULL,
  `actualOtherTotal` double(11,2) NOT NULL,
  `methodOtherTotalNums` int(8) NOT NULL,
  `methodWandaTotal` double(11,2) NOT NULL,
  `actualWandaTotal` double(11,2) NOT NULL,
  `methodWandaTotalNums` int(8) NOT NULL,
  `methodWandaPackageTotal` double(11,2) NOT NULL,
  `actualWandaPackageTotal` double(11,2) NOT NULL,
  `methodWandaPackageTotalNums` int(8) NOT NULL,
  `methodSelfHelpPriceTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `actualStoreCardTotal` double(11,2) NOT NULL,
  `methodTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeCouponTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotalNums` int(8) NOT NULL DEFAULT '0',
  `methodTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖总金额',
  `methodTakeOutTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '外卖总数量',
  `actualTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖实际金额',
  `methodUnionpayPosTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '银联pos总金额',
  `methodUnionpayPosTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '银联pos总数量',
  `methodTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动总金额',
  `methodTrafficActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '交行活动总数量',
  `actualTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动实际金额',
  `methodMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动总金额',
  `methodMerchantsActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '招行活动总数量',
  `actualMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动实际金额',
  `methodMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动总金额',
  `methodMarketActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '商场活动总数量',
  `actualMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动实际金额',
  `methodMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠总金额',
  `methodMeituanShanhuiTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '美团闪惠总数量',
  `actualMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠实际金额',
  PRIMARY KEY (`id`),
  KEY `dateTime` (`dateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=1625 DEFAULT CHARSET=utf8;

CREATE TABLE `bill_list_night` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderNums` int(8) NOT NULL,
  `customerNums` int(8) NOT NULL,
  `h5OrderNums` int(8) NOT NULL,
  `totalDishesPrice` double(11,2) NOT NULL,
  `totalErasePrice` double(11,2) NOT NULL,
  `totalDiscountPrice` double(11,2) NOT NULL,
  `totalReturnPrice` double(11,2) NOT NULL,
  `totalHadPaidPrice` double(11,2) NOT NULL,
  `totalRefundPrice` double(11,2) NOT NULL,
  `totalFreePrice` double(11,2) NOT NULL,
  `totalFreeNums` int(8) NOT NULL,
  `totalEscapePrice` double(11,2) NOT NULL,
  `totalEscapeNums` int(8) NOT NULL,
  `totalUnpaidPrice` double(11,2) NOT NULL,
  `totalUnpaidNums` int(8) NOT NULL,
  `methodCashTotal` double(11,2) NOT NULL,
  `methodCashTotalNums` int(8) NOT NULL,
  `methodBankcardTotal` double(11,2) NOT NULL,
  `methodBankcardTotalNums` int(8) NOT NULL,
  `methodWechatTotal` double(11,2) NOT NULL,
  `methodWechatTotalNums` int(8) NOT NULL,
  `methodAlipayTotal` double(11,2) NOT NULL,
  `methodAlipayTotalNums` int(8) NOT NULL,
  `methodStoreCardTotal` double(11,2) NOT NULL,
  `methodStoreCardTotalNums` int(8) NOT NULL,
  `methodMeituanTotal` double(11,2) NOT NULL,
  `methodMeituanTotalNums` int(8) NOT NULL,
  `methodCouponTotal` double(11,2) NOT NULL,
  `methodCouponTotalNums` int(8) NOT NULL,
  `methodKouBeiTotal` double(11,2) NOT NULL,
  `methodKouBeiTotalNums` int(8) NOT NULL,
  `methodPublicSignalTotal` double(11,2) NOT NULL,
  `methodPublicSignalTotalNums` int(8) NOT NULL,
  `methodWechatCouponTotal` double(11,2) NOT NULL,
  `actualWechatCouponTotal` double(11,2) NOT NULL,
  `methodWechatCouponTotalNums` int(8) NOT NULL,
  `actualMeituanTotal` double(11,2) NOT NULL,
  `actualKouBeiTotal` double(11,2) NOT NULL,
  `actualPublicSignalTotal` double(11,2) NOT NULL,
  `totalReductionPrice` double(11,2) NOT NULL,
  `methodPackageTotal` double(11,2) NOT NULL,
  `methodPackageTotalNums` int(8) NOT NULL,
  `methodStoreReduceTotal` double(11,2) NOT NULL,
  `methodStoreReduceTotalNums` int(8) NOT NULL,
  `dateTime` bigint(13) NOT NULL,
  `totalReturnCashPrice` double(11,2) NOT NULL,
  `actualPackageTotal` double(11,2) NOT NULL,
  `methodOtherTotal` double(11,2) NOT NULL,
  `actualOtherTotal` double(11,2) NOT NULL,
  `methodOtherTotalNums` int(8) NOT NULL,
  `methodWandaTotal` double(11,2) NOT NULL,
  `actualWandaTotal` double(11,2) NOT NULL,
  `methodWandaTotalNums` int(8) NOT NULL,
  `methodWandaPackageTotal` double(11,2) NOT NULL,
  `actualWandaPackageTotal` double(11,2) NOT NULL,
  `methodWandaPackageTotalNums` int(8) NOT NULL,
  `methodSelfHelpPriceTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `actualStoreCardTotal` double(11,2) NOT NULL,
  `methodTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeCouponTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotalNums` int(8) NOT NULL DEFAULT '0',
  `methodTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖总金额',
  `methodTakeOutTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '外卖总数量',
  `actualTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖实际金额',
  `methodUnionpayPosTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '银联pos总金额',
  `methodUnionpayPosTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '银联pos总数量',
  `methodTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动总金额',
  `methodTrafficActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '交行活动总数量',
  `actualTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动实际金额',
  `methodMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动总金额',
  `methodMerchantsActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '招行活动总数量',
  `actualMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动实际金额',
  `methodMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动总金额',
  `methodMarketActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '商场活动总数量',
  `actualMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动实际金额',
  `methodMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠总金额',
  `methodMeituanShanhuiTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '美团闪惠总数量',
  `actualMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠实际金额',
  PRIMARY KEY (`id`),
  KEY `dateTime` (`dateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=1457 DEFAULT CHARSET=utf8;

CREATE TABLE `bill_list_noon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderNums` int(8) NOT NULL,
  `customerNums` int(8) NOT NULL,
  `h5OrderNums` int(8) NOT NULL,
  `totalDishesPrice` double(11,2) NOT NULL,
  `totalErasePrice` double(11,2) NOT NULL,
  `totalDiscountPrice` double(11,2) NOT NULL,
  `totalReturnPrice` double(11,2) NOT NULL,
  `totalHadPaidPrice` double(11,2) NOT NULL,
  `totalRefundPrice` double(11,2) NOT NULL,
  `totalFreePrice` double(11,2) NOT NULL,
  `totalFreeNums` int(8) NOT NULL,
  `totalEscapePrice` double(11,2) NOT NULL,
  `totalEscapeNums` int(8) NOT NULL,
  `totalUnpaidPrice` double(11,2) NOT NULL,
  `totalUnpaidNums` int(8) NOT NULL,
  `methodCashTotal` double(11,2) NOT NULL,
  `methodCashTotalNums` int(8) NOT NULL,
  `methodBankcardTotal` double(11,2) NOT NULL,
  `methodBankcardTotalNums` int(8) NOT NULL,
  `methodWechatTotal` double(11,2) NOT NULL,
  `methodWechatTotalNums` int(8) NOT NULL,
  `methodAlipayTotal` double(11,2) NOT NULL,
  `methodAlipayTotalNums` int(8) NOT NULL,
  `methodStoreCardTotal` double(11,2) NOT NULL,
  `methodStoreCardTotalNums` int(8) NOT NULL,
  `methodMeituanTotal` double(11,2) NOT NULL,
  `methodMeituanTotalNums` int(8) NOT NULL,
  `methodCouponTotal` double(11,2) NOT NULL,
  `methodCouponTotalNums` int(8) NOT NULL,
  `methodKouBeiTotal` double(11,2) NOT NULL,
  `methodKouBeiTotalNums` int(8) NOT NULL,
  `methodPublicSignalTotal` double(11,2) NOT NULL,
  `methodPublicSignalTotalNums` int(8) NOT NULL,
  `methodWechatCouponTotal` double(11,2) NOT NULL,
  `actualWechatCouponTotal` double(11,2) NOT NULL,
  `methodWechatCouponTotalNums` int(8) NOT NULL,
  `actualMeituanTotal` double(11,2) NOT NULL,
  `actualKouBeiTotal` double(11,2) NOT NULL,
  `actualPublicSignalTotal` double(11,2) NOT NULL,
  `totalReductionPrice` double(11,2) NOT NULL,
  `methodPackageTotal` double(11,2) NOT NULL,
  `methodPackageTotalNums` int(8) NOT NULL,
  `methodStoreReduceTotal` double(11,2) NOT NULL,
  `methodStoreReduceTotalNums` int(8) NOT NULL,
  `dateTime` bigint(13) NOT NULL,
  `totalReturnCashPrice` double(11,2) NOT NULL,
  `actualPackageTotal` double(11,2) NOT NULL,
  `methodOtherTotal` double(11,2) NOT NULL,
  `actualOtherTotal` double(11,2) NOT NULL,
  `methodOtherTotalNums` int(8) NOT NULL,
  `methodWandaTotal` double(11,2) NOT NULL,
  `actualWandaTotal` double(11,2) NOT NULL,
  `methodWandaTotalNums` int(8) NOT NULL,
  `methodWandaPackageTotal` double(11,2) NOT NULL,
  `actualWandaPackageTotal` double(11,2) NOT NULL,
  `methodWandaPackageTotalNums` int(8) NOT NULL,
  `methodSelfHelpPriceTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `actualStoreCardTotal` double(11,2) NOT NULL,
  `methodTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeCouponTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotalNums` int(8) NOT NULL DEFAULT '0',
  `methodTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖总金额',
  `methodTakeOutTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '外卖总数量',
  `actualTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖实际金额',
  `methodUnionpayPosTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '银联pos总金额',
  `methodUnionpayPosTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '银联pos总数量',
  `methodTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动总金额',
  `methodTrafficActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '交行活动总数量',
  `actualTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动实际金额',
  `methodMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动总金额',
  `methodMerchantsActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '招行活动总数量',
  `actualMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动实际金额',
  `methodMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动总金额',
  `methodMarketActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '商场活动总数量',
  `actualMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动实际金额',
  `methodMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠总金额',
  `methodMeituanShanhuiTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '美团闪惠总数量',
  `actualMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠实际金额',
  PRIMARY KEY (`id`),
  KEY `dateTime` (`dateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=1457 DEFAULT CHARSET=utf8;

CREATE TABLE `bill_list_supper` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderNums` int(8) NOT NULL,
  `customerNums` int(8) NOT NULL,
  `h5OrderNums` int(8) NOT NULL,
  `totalDishesPrice` double(11,2) NOT NULL,
  `totalErasePrice` double(11,2) NOT NULL,
  `totalDiscountPrice` double(11,2) NOT NULL,
  `totalReturnPrice` double(11,2) NOT NULL,
  `totalHadPaidPrice` double(11,2) NOT NULL,
  `totalRefundPrice` double(11,2) NOT NULL,
  `totalFreePrice` double(11,2) NOT NULL,
  `totalFreeNums` int(8) NOT NULL,
  `totalEscapePrice` double(11,2) NOT NULL,
  `totalEscapeNums` int(8) NOT NULL,
  `totalUnpaidPrice` double(11,2) NOT NULL,
  `totalUnpaidNums` int(8) NOT NULL,
  `methodCashTotal` double(11,2) NOT NULL,
  `methodCashTotalNums` int(8) NOT NULL,
  `methodBankcardTotal` double(11,2) NOT NULL,
  `methodBankcardTotalNums` int(8) NOT NULL,
  `methodWechatTotal` double(11,2) NOT NULL,
  `methodWechatTotalNums` int(8) NOT NULL,
  `methodAlipayTotal` double(11,2) NOT NULL,
  `methodAlipayTotalNums` int(8) NOT NULL,
  `methodStoreCardTotal` double(11,2) NOT NULL,
  `methodStoreCardTotalNums` int(8) NOT NULL,
  `methodMeituanTotal` double(11,2) NOT NULL,
  `methodMeituanTotalNums` int(8) NOT NULL,
  `methodCouponTotal` double(11,2) NOT NULL,
  `methodCouponTotalNums` int(8) NOT NULL,
  `methodKouBeiTotal` double(11,2) NOT NULL,
  `methodKouBeiTotalNums` int(8) NOT NULL,
  `methodPublicSignalTotal` double(11,2) NOT NULL,
  `methodPublicSignalTotalNums` int(8) NOT NULL,
  `methodWechatCouponTotal` double(11,2) NOT NULL,
  `actualWechatCouponTotal` double(11,2) NOT NULL,
  `methodWechatCouponTotalNums` int(8) NOT NULL,
  `actualMeituanTotal` double(11,2) NOT NULL,
  `actualKouBeiTotal` double(11,2) NOT NULL,
  `actualPublicSignalTotal` double(11,2) NOT NULL,
  `totalReductionPrice` double(11,2) NOT NULL,
  `methodPackageTotal` double(11,2) NOT NULL,
  `methodPackageTotalNums` int(8) NOT NULL,
  `methodStoreReduceTotal` double(11,2) NOT NULL,
  `methodStoreReduceTotalNums` int(8) NOT NULL,
  `dateTime` bigint(13) NOT NULL,
  `totalReturnCashPrice` double(11,2) NOT NULL,
  `actualPackageTotal` double(11,2) NOT NULL,
  `methodOtherTotal` double(11,2) NOT NULL,
  `actualOtherTotal` double(11,2) NOT NULL,
  `methodOtherTotalNums` int(8) NOT NULL,
  `methodWandaTotal` double(11,2) NOT NULL,
  `actualWandaTotal` double(11,2) NOT NULL,
  `methodWandaTotalNums` int(8) NOT NULL,
  `methodWandaPackageTotal` double(11,2) NOT NULL,
  `actualWandaPackageTotal` double(11,2) NOT NULL,
  `methodWandaPackageTotalNums` int(8) NOT NULL,
  `methodSelfHelpPriceTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `actualStoreCardTotal` double(11,2) NOT NULL,
  `methodTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeCouponTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeCouponTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeIntegralDeductionTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeIntegralDeductionTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodTinyLifeStorecardTotalNums` int(8) NOT NULL DEFAULT '0',
  `actualTinyLifeStorecardTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotal` double(11,2) NOT NULL DEFAULT '0.00',
  `methodWeChatUnionpayPayMentTotalNums` int(8) NOT NULL DEFAULT '0',
  `methodTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖总金额',
  `methodTakeOutTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '外卖总数量',
  `actualTakeOutTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '外卖实际金额',
  `methodUnionpayPosTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '银联pos总金额',
  `methodUnionpayPosTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '银联pos总数量',
  `methodTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动总金额',
  `methodTrafficActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '交行活动总数量',
  `actualTrafficActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '交行活动实际金额',
  `methodMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动总金额',
  `methodMerchantsActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '招行活动总数量',
  `actualMerchantsActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '招行活动实际金额',
  `methodMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动总金额',
  `methodMarketActivitiesTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '商场活动总数量',
  `actualMarketActivitiesTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '商场活动实际金额',
  `methodMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠总金额',
  `methodMeituanShanhuiTotalNums` int(8) NOT NULL DEFAULT '0' COMMENT '美团闪惠总数量',
  `actualMeituanShanhuiTotal` double(11,2) NOT NULL DEFAULT '0.00' COMMENT '美团闪惠实际金额',
  PRIMARY KEY (`id`),
  KEY `dateTime` (`dateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=1457 DEFAULT CHARSET=utf8;

CREATE TABLE `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskId` int(11) NOT NULL,
  `contents` text,
  `createTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=45589 DEFAULT CHARSET=utf8;

CREATE TABLE `cashcoupon` (
  `cashCouponId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `condition` double DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `minSerialNumId` int(11) DEFAULT NULL,
  `maxSerialNumId` int(11) DEFAULT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `startTime` bigint(20) DEFAULT NULL,
  `endTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`cashCouponId`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `config` (
  `config_id` int(11) NOT NULL AUTO_INCREMENT,
  `attr_name` varchar(255) NOT NULL,
  `attr_value` varchar(255) NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `mod_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `attr_name` (`attr_name`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `coupons_list` (
  `couponId` int(11) NOT NULL AUTO_INCREMENT,
  `couponName` varchar(200) NOT NULL,
  `couponAmount` double(10,2) NOT NULL,
  `actualAmount` double(10,2) NOT NULL,
  `type` int(1) NOT NULL,
  PRIMARY KEY (`couponId`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

CREATE TABLE `date_time_check` (
  `typeid` int(5) NOT NULL,
  `dateTime` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `desk_add_or_remove_delete` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskId` int(11) NOT NULL,
  `deleteTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `deskId` (`deskId`),
  KEY `deleteTime` (`deleteTime`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `desk_add_or_remove_update` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskId` int(11) NOT NULL,
  `lastUpdateTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `deskId` (`deskId`),
  KEY `lastUpdateTime` (`lastUpdateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;

CREATE TABLE `desk_delete` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskId` int(11) NOT NULL,
  `deleteTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `deskId` (`deskId`),
  KEY `deleteTime` (`deleteTime`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `desk_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskId` int(11) NOT NULL,
  `deskKey` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=28241 DEFAULT CHARSET=utf8;

CREATE TABLE `desk_type_delete` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskTypeId` int(11) NOT NULL,
  `deleteTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `deskTypeId` (`deskTypeId`),
  KEY `deleteTime` (`deleteTime`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `desk_type_update` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskTypeId` int(11) NOT NULL,
  `lastUpdateTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `deskTypeId` (`deskTypeId`),
  KEY `lastUpdateTime` (`lastUpdateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE `desks` (
  `deskId` int(11) NOT NULL AUTO_INCREMENT,
  `deskName` varchar(100) NOT NULL,
  `maxPersonNum` int(11) NOT NULL,
  `physicalStatus` int(1) NOT NULL COMMENT '1--空闲 2--使用中 3--已预约 4--已付款',
  `useStatus` int(1) NOT NULL,
  `belongDeskType` int(11) NOT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `orderId` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`deskId`),
  UNIQUE KEY `deskName` (`deskName`)
) ENGINE=MyISAM AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

CREATE TABLE `desktypes` (
  `typeId` int(11) NOT NULL AUTO_INCREMENT,
  `typeName` varchar(100) NOT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`typeId`),
  UNIQUE KEY `typeName` (`typeName`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `device` (
  `deviceId` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) NOT NULL,
  `deviceCode` varchar(255) NOT NULL,
  `remark` text,
  `status` int(1) NOT NULL COMMENT '1:激活;2:未激活;3:锁定',
  `loginTime` bigint(20) NOT NULL,
  PRIMARY KEY (`deviceId`),
  UNIQUE KEY `deviceCode` (`deviceCode`)
) ENGINE=MyISAM AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

CREATE TABLE `discount_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `discount_name` varchar(200) NOT NULL,
  `rate` double(10,2) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

CREATE TABLE `discountcard` (
  `discountCardId` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `condition` double DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `dishesIds` text,
  `dishesGroupIds` text,
  `status` int(11) DEFAULT NULL,
  `customerName` varchar(255) DEFAULT NULL,
  `customerPhone` varchar(255) DEFAULT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `startTime` bigint(20) DEFAULT NULL,
  `endTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`discountCardId`),
  UNIQUE KEY `code` (`code`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `discountcoupon` (
  `discountCouponId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `condition` double DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `dishesGroupIds` text,
  `status` int(1) DEFAULT NULL,
  `minSerialNumId` int(11) DEFAULT NULL,
  `maxSerialNumId` int(11) DEFAULT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `startTime` bigint(20) DEFAULT NULL,
  `endTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`discountCouponId`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_delete` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesId` int(11) NOT NULL,
  `deleteTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesId` (`dishesId`) USING BTREE,
  KEY `deleteTime` (`deleteTime`)
) ENGINE=MyISAM AUTO_INCREMENT=205 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_group_list` (
  `dishesGroupId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesGroupName` varchar(50) NOT NULL,
  `dishesGroupContent` text,
  `createTime` bigint(20) NOT NULL,
  PRIMARY KEY (`dishesGroupId`),
  UNIQUE KEY `dishesGroupName` (`dishesGroupName`),
  KEY `createTime` (`createTime`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_list` (
  `dishesId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesTypeId` int(11) NOT NULL,
  `dishesName` varchar(100) NOT NULL,
  `dishesPrice` double NOT NULL,
  `dishesStock` int(11) NOT NULL COMMENT '-1无限库存',
  `dishesDescription` text,
  `dishesImgs` text,
  `dishesUnitName` varchar(50) NOT NULL,
  `dishesStatus` int(2) DEFAULT NULL COMMENT '1--上架  0--下架',
  `dishesPrivateAttribute` text,
  `dishesPublicAttribute` varchar(100) DEFAULT NULL,
  `creatTime` bigint(20) DEFAULT NULL,
  `ifNeedMergePrint` int(1) NOT NULL DEFAULT '0' COMMENT '0 -- 不合并打印  1--合并打印',
  `ifNeedPrint` int(1) NOT NULL DEFAULT '1' COMMENT '0--不需要打印  1--需要打印',
  `validTime` text,
  `isHidden` int(11) DEFAULT '0',
  `sortby` int(11) DEFAULT '1',
  `ifdelete` int(1) NOT NULL DEFAULT '0',
  `printSortby` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`dishesId`),
  UNIQUE KEY `dishesName` (`dishesName`)
) ENGINE=InnoDB AUTO_INCREMENT=608 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_list11` (
  `dishesId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesTypeId` int(11) NOT NULL,
  `dishesName` varchar(100) NOT NULL,
  `dishesPrice` double NOT NULL,
  `dishesStock` int(11) NOT NULL COMMENT '-1无限库存',
  `dishesDescription` text,
  `dishesImgs` text,
  `dishesUnitName` varchar(50) NOT NULL,
  `dishesStatus` int(2) DEFAULT NULL COMMENT '1--上架  0--下架',
  `dishesPrivateAttribute` text,
  `dishesPublicAttribute` varchar(100) DEFAULT NULL,
  `creatTime` bigint(20) DEFAULT NULL,
  `ifNeedMergePrint` int(1) NOT NULL DEFAULT '0' COMMENT '0 -- 不合并打印  1--合并打印',
  `ifNeedPrint` int(1) NOT NULL DEFAULT '1' COMMENT '0--不需要打印  1--需要打印',
  `validTime` text,
  `isHidden` int(11) DEFAULT '0',
  `sortby` int(11) DEFAULT '1',
  `ifdelete` int(1) NOT NULL DEFAULT '0',
  `printSortby` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`dishesId`),
  UNIQUE KEY `dishesName` (`dishesName`)
) ENGINE=InnoDB AUTO_INCREMENT=518 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_menu` (
  `dishesMenuId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `width` int(11) NOT NULL,
  `dishesTypeId` int(11) NOT NULL,
  `dishesMenuImgSrc` text NOT NULL,
  `dishesMenuItems` text NOT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `sortby` int(11) DEFAULT '1',
  PRIMARY KEY (`dishesMenuId`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=279 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_menu_delete` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesMenuId` int(11) NOT NULL,
  `deleteTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesMenuId` (`dishesMenuId`),
  KEY `deleteTime` (`deleteTime`)
) ENGINE=MyISAM AUTO_INCREMENT=111 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_menu_item` (
  `dishesMenuItemId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesMenuId` int(11) NOT NULL,
  `dishesId` int(11) NOT NULL,
  `dishesArea` text NOT NULL,
  `priceArea` text NOT NULL,
  `numArea` text NOT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dishesMenuItemId`)
) ENGINE=InnoDB AUTO_INCREMENT=1177 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_menu_update` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesMenuId` int(11) NOT NULL,
  `lastUpdateTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesMenuId` (`dishesMenuId`),
  KEY `lastUpdateTime` (`lastUpdateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=76 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_package_delete` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesPackageId` int(11) NOT NULL,
  `deleteTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesPackageId` (`dishesPackageId`),
  KEY `deleteTime` (`deleteTime`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_package_dishes` (
  `dishesPackageDishesId` int(10) NOT NULL AUTO_INCREMENT,
  `dishesPackageId` int(10) NOT NULL,
  `dishesPackageTypeId` int(10) NOT NULL,
  `dishesId` int(10) NOT NULL,
  `dishesOptions` text,
  `dishesPriceId` int(10) NOT NULL,
  PRIMARY KEY (`dishesPackageDishesId`)
) ENGINE=MyISAM AUTO_INCREMENT=604 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_package_list` (
  `dishesPackageId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesPackageType` int(11) NOT NULL,
  `dishesPackageName` varchar(200) NOT NULL,
  `dishesPackagePrice` float(10,2) NOT NULL,
  `dishesPackageStatus` int(1) NOT NULL,
  `dishesPackageImg` text,
  `dishesPackageDishes` text,
  `creatTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dishesPackageId`),
  UNIQUE KEY `dishesPackageName` (`dishesPackageName`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_package_list_new` (
  `dishesPackageId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesPackageType` int(11) NOT NULL,
  `dishesPackageName` varchar(200) NOT NULL,
  `dishesPackagePrice` float(10,2) NOT NULL,
  `dishesPackageStatus` int(1) NOT NULL,
  `dishesPackageImg` text,
  `creatTime` bigint(20) DEFAULT NULL,
  `sortby` int(10) NOT NULL DEFAULT '1',
  PRIMARY KEY (`dishesPackageId`),
  UNIQUE KEY `dishesPackageName` (`dishesPackageName`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_package_type` (
  `dishesPackageTypeId` int(10) NOT NULL AUTO_INCREMENT,
  `dishesPackageId` int(10) NOT NULL,
  `dishesPackageTypeName` varchar(200) NOT NULL,
  `ifRequired` int(1) NOT NULL,
  `chooseNums` int(2) NOT NULL,
  PRIMARY KEY (`dishesPackageTypeId`)
) ENGINE=MyISAM AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_package_update` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesPackageId` int(11) NOT NULL,
  `lastUpdateTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesPackageId` (`dishesPackageId`),
  KEY `lastUpdateTime` (`lastUpdateTime`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_price` (
  `dishesPriceId` int(10) NOT NULL AUTO_INCREMENT,
  `dishesId` int(10) NOT NULL,
  `dishesPriceName` varchar(200) NOT NULL,
  `dishesPrice` double(10,2) NOT NULL,
  `creatTime` bigint(20) NOT NULL,
  PRIMARY KEY (`dishesPriceId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_sale` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesId` int(11) NOT NULL,
  `dishesName` varchar(200) NOT NULL,
  `saleNums` int(11) NOT NULL,
  `saleAmount` double(10,2) NOT NULL,
  `dateTime` bigint(20) NOT NULL,
  `if_dishes_package` int(1) NOT NULL,
  `dishes_price_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=65088 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_type_sale` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesTypeId` int(11) NOT NULL,
  `dishesTypeName` varchar(200) NOT NULL,
  `saleNums` int(11) NOT NULL,
  `saleAmount` double(10,2) NOT NULL,
  `dateTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=10269 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_types` (
  `typeId` int(11) NOT NULL AUTO_INCREMENT,
  `typeName` varchar(100) NOT NULL,
  `typeStatus` int(2) NOT NULL COMMENT '1--启用  2--停用',
  `creatTime` bigint(20) DEFAULT NULL,
  `ifRefund` int(1) NOT NULL DEFAULT '0' COMMENT '1--可以反结账  0 -- 不可以反结账',
  `sortby` int(11) DEFAULT '1',
  `validTime` text,
  `hidden_h5` int(11) DEFAULT '0',
  `hidden_flat` int(11) DEFAULT '0',
  PRIMARY KEY (`typeId`),
  UNIQUE KEY `typeName` (`typeName`)
) ENGINE=MyISAM AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_types_delete` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesTypeId` int(11) NOT NULL,
  `deleteTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesTypeId` (`dishesTypeId`),
  KEY `deleteTime` (`deleteTime`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_types_update` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesTypeId` int(11) NOT NULL,
  `lastUpdateTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesTypeId` (`dishesTypeId`),
  KEY `lastUpdateTime` (`lastUpdateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

CREATE TABLE `dishes_update` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dishesId` int(11) NOT NULL,
  `lastUpdateTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dishesId` (`dishesId`) USING BTREE,
  KEY `lastUpdateTime` (`lastUpdateTime`)
) ENGINE=MyISAM AUTO_INCREMENT=380 DEFAULT CHARSET=utf8;

CREATE TABLE `dishesattribute_list` (
  `dishesAttributeId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesAttributeName` varchar(100) NOT NULL,
  `dishesAttributeMarkInfo` varchar(200) DEFAULT NULL,
  `isValueRadio` int(1) NOT NULL COMMENT '1--单选 0--复选',
  `isSync` int(1) NOT NULL COMMENT '1---同步  0 --不同步',
  `dishesAttributeObj` text,
  `creatTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dishesAttributeId`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE `many_coupon` (
  `many_coupon_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `coupon_type` int(11) DEFAULT NULL,
  `condition` double DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `dishes_group_ids` text,
  `status` int(11) DEFAULT NULL,
  `serial_number` varchar(255) DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `start_time` bigint(20) DEFAULT NULL,
  `end_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`many_coupon_id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `serial_number` (`serial_number`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `member_level_list` (
  `memberLevelId` int(11) NOT NULL AUTO_INCREMENT,
  `memberLevelName` varchar(200) NOT NULL,
  `memberLevelRequiredPoints` bigint(20) NOT NULL,
  `memberLevelDiscount` float(4,2) NOT NULL DEFAULT '1.00',
  `createTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`memberLevelId`),
  UNIQUE KEY `memberLevelName` (`memberLevelName`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `member_list` (
  `memberId` int(11) NOT NULL AUTO_INCREMENT,
  `memberLevelId` int(11) NOT NULL,
  `memberName` varchar(100) NOT NULL,
  `memberBirthday` varchar(100) DEFAULT NULL,
  `memberSex` int(1) NOT NULL COMMENT '0--男  1--女',
  `memberTelphone` varchar(100) NOT NULL,
  `memberConsumptionNums` int(11) DEFAULT NULL,
  `memberLastConsumptionTime` bigint(20) DEFAULT NULL,
  `memberConsumptionPrice` double(10,2) DEFAULT '0.00',
  `memberBalance` double(10,2) DEFAULT '0.00',
  `memberPoints` bigint(20) DEFAULT NULL,
  `memberStatus` int(1) NOT NULL COMMENT '0--停用 1--启用',
  `createTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`memberId`),
  UNIQUE KEY `memberTelphone` (`memberTelphone`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE `order_coupon` (
  `order_coupon_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `openid` varchar(255) NOT NULL,
  `desk_id` int(11) NOT NULL,
  `serial_number_id` int(11) NOT NULL,
  `coupon_type` int(11) DEFAULT NULL,
  `coupon_id` int(11) DEFAULT NULL COMMENT '券的id有可能是代金券，也有可能是折扣券的id',
  `name` varchar(255) DEFAULT NULL,
  `condition` double DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`order_coupon_id`),
  KEY `order_id` (`order_id`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

CREATE TABLE `order_dishes_list` (
  `orderDishesId` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` int(11) NOT NULL,
  `subOrderId` int(11) NOT NULL,
  `dishesId` int(11) NOT NULL,
  `dishesTypeId` int(11) NOT NULL,
  `ifDishesPackage` int(11) NOT NULL DEFAULT '0' COMMENT '0--否  1--是',
  `orderDishesPrice` float(10,2) NOT NULL,
  `orderDishesDiscountPrice` float(10,2) NOT NULL,
  `orderDishesNums` int(11) NOT NULL DEFAULT '1',
  `orderDishesStatus` int(1) NOT NULL DEFAULT '0' COMMENT '0--未上菜  1--已下厨  2--已上菜',
  `orderDishesSaletype` int(1) NOT NULL DEFAULT '0' COMMENT '0--普通菜品  1--赠送   2--试吃  3--已退菜',
  `orderDishesOptions` text,
  `orderDishesIfrefund` int(1) DEFAULT '0' COMMENT '0--可退   1--不可退',
  `orderDishesIfchange` int(1) DEFAULT '0' COMMENT '0--可改变折扣  1--不可改变折扣',
  `createtime` bigint(20) DEFAULT NULL,
  `orderDishesDiscountInfo` text,
  `dishesPriceId` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`orderDishesId`),
  KEY `orderId` (`orderId`),
  KEY `subOrderId` (`subOrderId`)
) ENGINE=InnoDB AUTO_INCREMENT=184919 DEFAULT CHARSET=utf8;

CREATE TABLE `order_list` (
  `orderId` int(11) NOT NULL AUTO_INCREMENT,
  `orderStatus` int(2) NOT NULL COMMENT '1--未付款 2--已付款 3--部分支付 4--被合并餐桌 5--逃单 6--免单',
  `deskId` int(11) NOT NULL,
  `accountId` int(11) NOT NULL,
  `memberId` int(11) NOT NULL,
  `orderCustomerNums` int(11) DEFAULT NULL,
  `orderCustomerTelphone` varchar(50) DEFAULT NULL,
  `orderRecommender` varchar(100) DEFAULT NULL,
  `orderHadpaid` float(10,2) NOT NULL DEFAULT '0.00',
  `orderReduction` float(10,2) NOT NULL DEFAULT '0.00',
  `orderErase` float(10,2) NOT NULL,
  `orderRefund` float(10,2) NOT NULL,
  `createtime` bigint(20) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '0--用餐开始 1--用餐结束',
  `orderDiscountInfo` text,
  `fullReduceDishesPrice` float(10,2) NOT NULL DEFAULT '0.00',
  `orderReturnCash` double(10,2) NOT NULL DEFAULT '0.00',
  `discount_reason` varchar(255) DEFAULT '',
  `return_cash_reason` varchar(255) DEFAULT '',
  `orderType` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`orderId`)
) ENGINE=MyISAM AUTO_INCREMENT=2106260021 DEFAULT CHARSET=utf8;

CREATE TABLE `order_package_dishes_list` (
  `orderPackageDishesId` int(10) NOT NULL AUTO_INCREMENT,
  `orderDishesId` int(10) NOT NULL,
  `dishesId` int(10) NOT NULL,
  `dishesOptions` text,
  `dishesPriceId` int(10) NOT NULL,
  PRIMARY KEY (`orderPackageDishesId`)
) ENGINE=InnoDB AUTO_INCREMENT=34513 DEFAULT CHARSET=utf8;

CREATE TABLE `order_pays` (
  `orderpaysId` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` int(11) NOT NULL,
  `accountId` int(11) NOT NULL,
  `amount` float(10,2) NOT NULL,
  `actualAmount` float(10,2) NOT NULL,
  `voucherNums` int(11) NOT NULL DEFAULT '1',
  `paymentMethod` int(1) NOT NULL,
  `cardNumber` text,
  `paymentStatus` int(1) DEFAULT '0' COMMENT '支付状态0-未支付 1-成功',
  `remark` text,
  `createtime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`orderpaysId`)
) ENGINE=MyISAM AUTO_INCREMENT=37889 DEFAULT CHARSET=utf8;

CREATE TABLE `ordercart_log` (
  `orderCartId` varchar(255) NOT NULL,
  `recJson` text,
  `sendJson` text,
  `createTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`orderCartId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `prepaidcard` (
  `prePaidCardId` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `condition` double DEFAULT NULL,
  `balance` double DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `customerName` varchar(255) DEFAULT NULL,
  `customerPhone` varchar(255) DEFAULT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `startTime` bigint(20) DEFAULT NULL,
  `endTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`prePaidCardId`),
  UNIQUE KEY `code` (`code`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

CREATE TABLE `print_task_list` (
  `printTaskId` int(11) NOT NULL AUTO_INCREMENT,
  `printTaskName` varchar(225) NOT NULL,
  `printTaskContent` text,
  PRIMARY KEY (`printTaskId`),
  UNIQUE KEY `printTaskName` (`printTaskName`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `printer_dish` (
  `printer_dish_id` int(11) NOT NULL AUTO_INCREMENT,
  `printer_id` int(11) NOT NULL,
  `dish_id` int(11) NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `mod_time` bigint(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`printer_dish_id`),
  UNIQUE KEY `dish_id` (`dish_id`)
) ENGINE=MyISAM AUTO_INCREMENT=632 DEFAULT CHARSET=utf8;

CREATE TABLE `printer_list` (
  `printerId` int(11) NOT NULL AUTO_INCREMENT,
  `printerName` varchar(100) NOT NULL,
  `printerIp` varchar(20) NOT NULL,
  `printerPort` int(11) NOT NULL,
  `printerInfo` text,
  `printerType` int(1) NOT NULL COMMENT '1:80mm;0:58mm',
  `printerStatus` int(1) NOT NULL COMMENT '1:正常；0：关闭',
  `addTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`printerId`),
  UNIQUE KEY `printerName` (`printerName`),
  UNIQUE KEY `printerIp` (`printerIp`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE `recharge_activity` (
  `activityId` int(11) NOT NULL AUTO_INCREMENT,
  `activityName` varchar(200) NOT NULL,
  `activityLimitPrice` double NOT NULL,
  `activitySendPrice` double NOT NULL,
  `status` int(1) NOT NULL,
  `createtime` bigint(20) NOT NULL,
  PRIMARY KEY (`activityId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `recharge_list` (
  `rechargeId` int(11) NOT NULL AUTO_INCREMENT,
  `prePaidCardId` int(11) NOT NULL,
  `accountId` int(11) NOT NULL,
  `rechargeAmount` double NOT NULL,
  `sendAmount` double NOT NULL,
  `rechargeMethod` int(2) NOT NULL,
  `remark` text,
  `createtime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rechargeId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `reserve_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `telNumber` varchar(50) NOT NULL,
  `contents` text,
  `createTime` bigint(20) NOT NULL,
  `status` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;

CREATE TABLE `return_reason` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deskName` varchar(50) NOT NULL,
  `orderId` int(11) NOT NULL,
  `dishesName` varchar(200) NOT NULL,
  `returnReason` varchar(200) DEFAULT NULL,
  `addtime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2290 DEFAULT CHARSET=utf8;

CREATE TABLE `roll_back_test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

CREATE TABLE `serialnumber` (
  `serialNumberId` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `status` int(1) NOT NULL,
  PRIMARY KEY (`serialNumberId`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=100001 DEFAULT CHARSET=utf8;

CREATE TABLE `store` (
  `storeId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `discount` double NOT NULL,
  `status` int(1) NOT NULL,
  `stockMode` int(1) NOT NULL,
  `ownerPassword` varchar(100) NOT NULL,
  `clerkPassword` varchar(255) DEFAULT NULL,
  `storeDishesGroupIds` text,
  `managerDishesGroupIds` text,
  `memberDishesGroupIds` text,
  `weChatDishesGroupIds` text,
  `ifUsing` int(1) NOT NULL DEFAULT '0',
  `activityType` int(1) NOT NULL DEFAULT '0',
  `fullMoney` double(10,2) NOT NULL,
  `reduceMoney` double(10,2) NOT NULL,
  PRIMARY KEY (`storeId`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `suborder_list` (
  `subOrderId` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` int(11) NOT NULL,
  `subOrderStatus` int(11) NOT NULL COMMENT '0--未结  1--已结',
  `accountId` int(11) NOT NULL,
  `createtime` bigint(20) DEFAULT NULL,
  `orderType` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`subOrderId`)
) ENGINE=InnoDB AUTO_INCREMENT=165937174 DEFAULT CHARSET=utf8;

CREATE TABLE `tastecoupon` (
  `tasteCouponId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `dishesGroupIds` text,
  `status` int(11) DEFAULT NULL,
  `minSerialNumId` int(11) DEFAULT NULL,
  `maxSerialNumId` int(11) DEFAULT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `startTime` bigint(20) DEFAULT NULL,
  `endTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`tasteCouponId`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `test_sync` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msg` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8;

CREATE TABLE `ticketdesign_list` (
  `ticketDesignId` int(11) NOT NULL AUTO_INCREMENT,
  `ticketDesignName` varchar(100) NOT NULL,
  `ticketDesignType` int(1) NOT NULL,
  `content` text,
  `creatTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ticketDesignId`),
  UNIQUE KEY `ticketDesignName` (`ticketDesignName`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
