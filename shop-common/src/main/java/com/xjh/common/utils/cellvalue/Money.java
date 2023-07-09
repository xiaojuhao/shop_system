package com.xjh.common.utils.cellvalue;

import com.xjh.common.utils.CommonUtils;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.math.BigDecimal;

public class Money {
    Double amount;
    Color color;
    Pos pos;

    public Money() {
    }

    public Money(BigDecimal amount) {
        this(amount != null ? amount.doubleValue() : BigDecimal.ZERO.doubleValue());
    }

    public Money(Double amount) {
        if (amount == null) {
            this.amount = 0D;
        } else {
            this.amount = amount;
        }
    }

    public Money with(Color color) {
        this.color = color;
        return this;
    }

    public Money with(Pos pos) {
        this.pos = pos;
        return this;
    }

    public Money with(Double amount) {
        this.amount = amount;
        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Pos getPos() {
        return pos;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public String toString() {
        return CommonUtils.formatMoney(amount);
    }


}
