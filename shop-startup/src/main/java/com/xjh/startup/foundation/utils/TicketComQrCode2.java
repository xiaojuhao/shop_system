/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.awt.*;

import com.xjh.startup.foundation.constants.EnumComType;

/**
 * @author 36181
 */
public class TicketComQrCode2 extends TicketCom {
    //单位全部都是像素了
    private int width;
    private int height;
    private int qrWidth;
    private int leftpadding1;
    private int leftpadding2;
    private String text1;
    private String text2;

    @Override
    public Font getFont() {
        return getFont(0);
    }


    @Override
    public int getComType() {
        return EnumComType.QRCODE2.type;
    }

    private Shape shape;

    @Override
    public Shape getComShape() {
        return shape;
    }

    @Override
    public void drawCom(Graphics2D graphics2D) {

    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getQrWidth() {
        return qrWidth;
    }

    public void setQrWidth(int qrWidth) {
        this.qrWidth = qrWidth;
    }

    public int getLeftpadding1() {
        return leftpadding1;
    }

    public void setLeftpadding1(int leftpadding1) {
        this.leftpadding1 = leftpadding1;
    }

    public int getLeftpadding2() {
        return leftpadding2;
    }

    public void setLeftpadding2(int leftpadding2) {
        this.leftpadding2 = leftpadding2;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }


}
