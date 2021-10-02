package com.xjh.dao.query;

import lombok.Data;

@Data
public class DishesQuery {
    int pageNo = 1;
    int pageSize = 20;
    String dishesName;
    String status;

}
