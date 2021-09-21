package com.xjh.startup.view.model;

import java.util.List;

import com.xjh.common.enumeration.EnumChoiceAction;

import lombok.Data;

@Data
public class DeskOrderParam {
    Integer orderId;
    Integer deskId;
    String deskName;
    Runnable callback;
    // 退菜时传入退菜记录
    List<String> returnList;
    EnumChoiceAction choiceAction;
}
