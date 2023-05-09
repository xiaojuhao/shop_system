package com.xjh.common.valueobject;

import java.util.List;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.OrElse;
import lombok.Data;

@Data
public class CartVO {
    Integer id;
    Integer deskId;
    List<CartItemVO> contents;
    Long createTime;

    public int sumDishesNum() {
        int i = 0;
        if (CommonUtils.isNotEmpty(contents)) {
            for (CartItemVO vo : contents) {
                i += OrElse.orGet(vo.getNums(), 0);
            }
        }
        return i;
    }

}
