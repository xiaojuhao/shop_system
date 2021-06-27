package com.xjh.service.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Cart;
import com.xjh.dao.mapper.CartDAO;
import com.xjh.service.domain.model.CartItemVO;

import cn.hutool.core.codec.Base64;

@Singleton
public class CartService {
    @Inject
    CartDAO cartDAO;

    public int addItem(Integer deskId, CartItemVO item) throws SQLException {
        Cart cart = new Cart();
        cart.setDeskId(deskId);
        List<CartItemVO> contentItems = selectByDeskId(deskId);
        contentItems.add(item);
        cart.setContents(Base64.encode(JSON.toJSONString(contentItems)));
        return cartDAO.save(cart);
    }

    public List<CartItemVO> selectByDeskId(Integer deskId) throws SQLException {
        Cart cart = cartDAO.selectByDeskId(deskId);
        if (cart != null) {
            String contents = cart.getContents();
            if (CommonUtils.isNotBlank(contents)) {
                contents = Base64.decodeStr(contents);
                return JSONArray.parseArray(contents, CartItemVO.class);
            }
        }
        return new ArrayList<>();
    }
}
