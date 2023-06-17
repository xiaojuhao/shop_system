package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("cart")
public class CartDO {
    @Id
    @Column("id")
    Integer id;
    @Column("deskId")
    Integer deskId;
    @Column("contents")
    String contents;
    @Column("createTime")
    Long createTime;
}
