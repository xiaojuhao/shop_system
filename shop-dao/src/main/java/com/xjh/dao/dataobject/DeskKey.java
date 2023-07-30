package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("desk_key")
public class DeskKey {
    @Id
    @Column("id")
    Integer id;
    @Column("deskId")
    Integer deskId;
    @Column("deskKey")
    String deskKey;
}
