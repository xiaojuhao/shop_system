package com.xjh.common.utils.cellvalue;

import java.util.function.Consumer;

public class InputNumber {
    Integer number;
    Consumer<Integer> onChange;

    public static InputNumber from(Integer number){
        InputNumber n = new InputNumber();
        n.setNumber(number);
        return n;
    }

    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }

    public String toString(){
        return number == null ? "" : number.toString();
    }

    public Consumer<Integer> getOnChange() {
        return onChange;
    }

    public void setOnChange(Consumer<Integer> onChange) {
        this.onChange = onChange;
    }
}
