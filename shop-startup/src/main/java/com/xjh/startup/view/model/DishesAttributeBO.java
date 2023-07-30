package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;
import com.xjh.common.valueobject.DishesAttributeVO;
import lombok.Data;

@Data
public class DishesAttributeBO {
    Integer dishesAttributeId;
    String dishesAttributeName;
    String dishesAttributeMarkInfo;
    RichText isValueRadio;
    RichText createTime;
    Operations operations = new Operations();
    DishesAttributeVO attachment;
}
