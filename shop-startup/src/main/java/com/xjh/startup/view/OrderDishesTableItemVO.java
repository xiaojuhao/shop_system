package com.xjh.startup.view;

import javafx.beans.property.SimpleStringProperty;

public class OrderDishesTableItemVO {
    SimpleStringProperty col1;
    SimpleStringProperty col2;
    SimpleStringProperty col3;
    SimpleStringProperty col4;
    SimpleStringProperty col5;
    SimpleStringProperty col6;
    SimpleStringProperty col7;

    public OrderDishesTableItemVO(String col1, String col2, String col3, String col4, String col5, String col6, String col7) {
        this.col1 = new SimpleStringProperty(col1);
        this.col2 = new SimpleStringProperty(col2);
        this.col3 = new SimpleStringProperty(col3);
        this.col4 = new SimpleStringProperty(col4);
        this.col5 = new SimpleStringProperty(col5);
        this.col6 = new SimpleStringProperty(col6);
        this.col7 = new SimpleStringProperty(col7);
    }

    public String getCol1() {
        return col1.get();
    }

    public SimpleStringProperty col1Property() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1.set(col1);
    }

    public String getCol2() {
        return col2.get();
    }

    public SimpleStringProperty col2Property() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2.set(col2);
    }

    public String getCol3() {
        return col3.get();
    }

    public SimpleStringProperty col3Property() {
        return col3;
    }

    public void setCol3(String col3) {
        this.col3.set(col3);
    }

    public String getCol4() {
        return col4.get();
    }

    public SimpleStringProperty col4Property() {
        return col4;
    }

    public void setCol4(String col4) {
        this.col4.set(col4);
    }

    public String getCol5() {
        return col5.get();
    }

    public SimpleStringProperty col5Property() {
        return col5;
    }

    public void setCol5(String col5) {
        this.col5.set(col5);
    }

    public String getCol6() {
        return col6.get();
    }

    public SimpleStringProperty col6Property() {
        return col6;
    }

    public void setCol6(String col6) {
        this.col6.set(col6);
    }

    public String getCol7() {
        return col7.get();
    }

    public SimpleStringProperty col7Property() {
        return col7;
    }

    public void setCol7(String col7) {
        this.col7.set(col7);
    }
}
