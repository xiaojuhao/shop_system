package com.xjh.common.valueobject;

import com.xjh.common.utils.OrElse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartVO {
    int id;
    int deskId;
    List<CartItemVO> contents = new ArrayList<>();
    long createTime;

    public int sumDishesNum() {
        int i = 0;
        for (CartItemVO vo : contents) {
            i += OrElse.orGet(vo.getNums(), 0);
        }
        return i;
    }


    public List<CartItemVO> getContents() {
        return contents;
    }

    public void setContents(List<CartItemVO> contents) {
        this.contents = contents;
    }

}
