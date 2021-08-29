package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;

@Table("desk_key")
public class DeskKey {
    @Id
    @Column("id")
    Integer id;
    @Column("deskId")
    Integer deskId;
    @Column("deskKey")
    String deskKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeskId() {
        return deskId;
    }

    public void setDeskId(Integer deskId) {
        this.deskId = deskId;
    }

    public String getDeskKey() {
        return deskKey;
    }

    public void setDeskKey(String deskKey) {
        this.deskKey = deskKey;
    }
}
