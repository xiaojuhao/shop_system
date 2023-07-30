package com.xjh.dao.dataobject;

import com.xjh.dao.foundation.Column;
import com.xjh.dao.foundation.Id;
import com.xjh.dao.foundation.Table;
import lombok.Data;

@Data
@Table("print_task_list")
public class PrinterTaskDO {
    @Id
    @Column("printTaskId")
    Integer printTaskId;

    @Column("printTaskName")
    String printTaskName;

    @Column("printTaskContent")
    String printTaskContent;
}
