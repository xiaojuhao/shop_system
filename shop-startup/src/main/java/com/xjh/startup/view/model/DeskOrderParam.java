package com.xjh.startup.view.model;

import com.xjh.common.enumeration.EnumChoiceAction;
import lombok.Data;

import java.util.List;

@Data
public class DeskOrderParam {
    Integer orderId;
    Integer deskId;
    String deskName;
    Runnable callback;
    // 退菜时传入退菜记录
    List<String> returnList;
    // 拆台清单
    List<String> separateSubOrderIdList;

    EnumChoiceAction choiceAction;
}
