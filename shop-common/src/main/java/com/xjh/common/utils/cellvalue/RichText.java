package com.xjh.common.utils.cellvalue;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class RichText {
    Color color;
    Pos pos;
    Object text;

    public RichText() {
    }

    public RichText(Object text) {
        this.text = text;
    }

    public RichText ofText(Object text) {
        this.text = text;
        return this;
    }

    public RichText with(Color color) {
        this.color = color;
        return this;
    }

    public RichText with(Pos pos) {
        this.pos = pos;
        return this;
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

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }
}
