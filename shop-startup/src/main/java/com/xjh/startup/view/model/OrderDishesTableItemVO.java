package com.xjh.startup.view.model;

import com.xjh.common.utils.cellvalue.RichText;

public class OrderDishesTableItemVO {
    String col1;
    String col2;
    RichText col3;
    RichText col4;
    RichText col5;
    String col6;
    RichText col7;

    public OrderDishesTableItemVO(String col1, String col2, RichText col3, RichText col4, RichText col5, String col6, RichText col7) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
        this.col5 = col5;
        this.col6 = col6;
        this.col7 = col7;
    }

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }

    public RichText getCol3() {
        return col3;
    }

    public void setCol3(RichText col3) {
        this.col3 = col3;
    }

    public RichText getCol4() {
        return col4;
    }

    public void setCol4(RichText col4) {
        this.col4 = col4;
    }

    public RichText getCol5() {
        return col5;
    }

    public void setCol5(RichText col5) {
        this.col5 = col5;
    }

    public String getCol6() {
        return col6;
    }

    public void setCol6(String col6) {
        this.col6 = col6;
    }

    public RichText getCol7() {
        return col7;
    }

    public void setCol7(RichText col7) {
        this.col7 = col7;
    }
}
