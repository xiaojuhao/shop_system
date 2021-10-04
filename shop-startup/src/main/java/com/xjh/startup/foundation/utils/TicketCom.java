/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.awt.*;

import lombok.Data;

@Data
public abstract class TicketCom {
    private int size;
    private String name;
    private int frontEnterNum;
    private int behindEnterNum;
    private int frontLen;
    private int behindLen;
    protected final TicketDesign ticketDesign = new TicketDesign();
    //protected final int ticketDesign.getMinX();
    //protected final int ticketDesign.getMaxX();

    public abstract int getComType();

    public abstract Shape getComShape();

    public abstract void drawCom(Graphics2D graphics2D);

    public Font getFont() {
        return getFont(getSize());
    }

    public Font getFont(int size) {
        switch (size) {
            case 2:
                return new Font("新宋体", Font.PLAIN, 23);
            case 3:
                return new Font("新宋体", Font.PLAIN, 35);
            case 4:
                return new Font("新宋体", Font.PLAIN, 47);
            case 5:
                return new Font("新宋体", Font.PLAIN, 59);
            case 6:
                return new Font("新宋体", Font.PLAIN, 71);
            case 7:
                return new Font("新宋体", Font.PLAIN, 83);
            case 8:
                return new Font("新宋体", Font.PLAIN, 95);
            default:
                return new Font("新宋体", Font.PLAIN, 12);
        }
    }

    protected void drawComMouseOn(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(102, 204, 0, 100));
        graphics2D.fill(getComShape());
        graphics2D.setColor(new Color(0, 102, 51, 100));
        graphics2D.draw(getComShape());
    }

    protected void drawComSelected(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(153, 153, 255, 100));
        graphics2D.fill(getComShape());
        graphics2D.setColor(new Color(102, 102, 255, 100));
        graphics2D.draw(getComShape());
    }

    protected void downLine(int lineNum, Graphics2D graphics2D, Font font) {
        if (lineNum > 0) {
            for (int i = 0; i < lineNum; i++) {
                if (i == 0) {
                    //System.out.println(getName() + ".font=["+font+"]");
                    ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + graphics2D.getFontMetrics(font).getHeight());
                } else {
                    //System.out.println(getName() + ".getFont(0)=["+getFont(0)+"]");
                    ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + (graphics2D.getFontMetrics(getFont(0)).getHeight()));
                }
                ticketDesign.setNextComStartX(ticketDesign.getMinX());
            }
        }
    }

    protected void moveDistance(int len, Graphics2D graphics2D) {
        if (len > 0) {
            int charWidth = graphics2D.getFontMetrics(getFont(0)).charWidth(' ');
            charWidth = charWidth * len;
            charWidth = charWidth + ticketDesign.getNextComStartX();
            while (charWidth > ticketDesign.getMaxX()) {
                charWidth = charWidth - (ticketDesign.getMaxX() - ticketDesign.getNextComStartX());
                downLine(1, graphics2D, getFont());
            }
            ticketDesign.setNextComStartX(charWidth);
        }
    }
}