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

    public void add(OperationButton first, OperationButton... ops) {
        if (first != null) {
            this.operations.add(first);
        }
        if (ops != null) {
            for (OperationButton op : ops) {
                if (op != null) {
                    this.operations.add(op);
                }
            }
        }

    }
}
