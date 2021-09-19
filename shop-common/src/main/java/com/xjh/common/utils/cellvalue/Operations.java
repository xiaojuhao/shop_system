package com.xjh.common.utils.cellvalue;

import java.util.ArrayList;
import java.util.List;

public class Operations {
    List<OperationButton> operations = new ArrayList<>();

    public List<OperationButton> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationButton> operations) {
        this.operations = operations;
    }

    public void add(OperationButton op) {
        this.operations.add(op);
    }
}
