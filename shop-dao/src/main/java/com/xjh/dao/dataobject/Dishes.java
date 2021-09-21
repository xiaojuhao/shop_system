package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Table;

import lombok.Data;

@Data
@Table("dishes_list")
public class Dishes {
    @Column("dishesId")
    Integer dishesId;

    @Column("dishesTypeId")
    Integer dishesTypeId;

    @Column("dishesName")
    String dishesName;

    @Column("dishesPrice")
    Double dishesPrice;

    @Column("dishesStock")
    Integer dishesStock;

    @Column("dishesDescription")
    String dishesDescription;

    @Column("dishesImgs")
    String dishesImgs;

    @Column("dishesUnitName")
    String dishesUnitName;

    @Column("dishesStatus")
    Integer dishesStatus;

    @Column("dishesPrivateAttribute")
    String dishesPrivateAttribute;

    @Column("dishesPublicAttribute")
    String dishesPublicAttribute;

    @Column("creatTime")
    Long creatTime;

    @Column("ifNeedMergePrint")
    Integer ifNeedMergePrint;

    @Column("ifNeedPrint")
    Integer ifNeedPrint;

    @Column("validTime")
    String validTime;

    @Column("isHidden")
    Integer isHidden;

    @Column("ifdelete")
    Integer ifdelete;
}
