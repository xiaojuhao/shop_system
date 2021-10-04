/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.awt.*;
import java.util.List;

/**
 * @author 36181
 */
public class TicketComText extends TicketCom implements LineGrap {

    private String sampleContent;

    private Shape shape;

    //new Font("新宋体", Font.PLAIN, 23) => 对应一行24个字母
    //new Font("新宋体", Font.PLAIN, 35) => 对应一行16个字母
    //new Font("新宋体", Font.PLAIN, 47) => 对应一行12个字母
    //new Font("新宋体", Font.PLAIN, 59) => 对应一行9个字母
    //new Font("新宋体", Font.PLAIN, 71) => 对应一行8个字母
    //new Font("新宋体", Font.PLAIN, 83) => 对应一行6个字母
    //new Font("新宋体", Font.PLAIN, 95) => 对应一行6个字母
    @Override
    public int getComType() {
        return TicketCom.TYPE_TEXT;
    }

    @Override
    public Shape getComShape() {
        return shape;
    }

    @Override
    public void drawCom(Graphics2D graphics2D) {
        //System.out.println("============================================================================>" + getName());
        //System.out.println(ticketDesign.getNextComStartX() + "," + ticketDesign.getNextComStartY());
        downLine(getFrontEnterNum(), graphics2D, getFont());
        moveDistance(getFrontLen(), graphics2D);

        //System.out.println(ticketDesign.getNextComStartX() + "," + ticketDesign.getNextComStartY());
        graphics2D.setFont(getFont());
        List<Rectangle> rectangles = FontTool.writeLinesStr(sampleContent, graphics2D, ticketDesign.getNextComStartX(), ticketDesign.getNextComStartY() - graphics2D.getFontMetrics().getHeight(), ticketDesign.getMinX(), ticketDesign.getMaxX(), this);
        Object[] objects = FontTool.mergeRectangles(rectangles);
        shape = (Shape) objects[0];
        if (this.equals(ticketDesign.getSelectTicketCom())) {
            drawComSelected(graphics2D);
        } else if (shape.contains(ticketDesign.getNowMouseX(), ticketDesign.getNowMouseY())) {
            drawComMouseOn(graphics2D);
        }
        ticketDesign.setNextComStartX((int) objects[1]);
        ticketDesign.setNextComStartY((int) objects[2]);

        Font font = getFont();
        TicketCom ticketComNext = ticketDesign.getNextTicketCom(this);
        if (ticketComNext != null) {
            if (ticketComNext instanceof TicketComText) {
                TicketComText ticketComText = (TicketComText) ticketComNext;
                font = ticketComText.getFont();
            } else {
                ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + 1);
            }
        }
        downLine(getBehindEnterNum(), graphics2D, font);
        moveDistance(getBehindLen(), graphics2D);

        graphics2D.drawLine(ticketDesign.getNextComStartX(), ticketDesign.getNextComStartY(), ticketDesign.getNextComStartX() + 100, ticketDesign.getNextComStartY());
    }

    public void setSampleContent(String sampleContent) {
        this.sampleContent = sampleContent;
    }

    public String getSampleContent() {
        return sampleContent;
    }

    @Override
    public int getGrap(String str, int nextStartIndex, int nextX, Graphics2D graphics2D) {
        int lineHeight = graphics2D.getFontMetrics().getHeight();
        int grapDefault = lineHeight / 5;
        if (getBehindEnterNum() > 0) {
            return grapDefault;
        }

        int behindLen = getBehindLen();
        if (behindLen > 0) {
            int len = graphics2D.getFontMetrics(getFont(0)).charWidth(' ');
            len = len * behindLen;
            if (nextX + len > ticketDesign.getMaxX()) {
                return grapDefault;
            }
        }

        int nextHeight = getStartLineHeightMax(graphics2D, nextX, str.substring(nextStartIndex, str.length()));
        if (nextHeight > lineHeight) {
            return (int) (((float) nextHeight * 1.2f) - lineHeight);
        }
        return grapDefault;
    }

    public int getStartLineHeightMax(Graphics2D graphics2D, int startLineX) {
        if (getFrontEnterNum() > 0) {
            return 0;
        }

        int frontLen = getFrontLen();
        if (frontLen > 0) {
            int len = graphics2D.getFontMetrics(getFont(0)).charWidth(' ');
            len = len * frontLen;
            if (startLineX + len > ticketDesign.getMaxX()) {
                return 0;
            } else {
                startLineX = startLineX + len;
            }
        }

        return getStartLineHeightMax(graphics2D, startLineX, sampleContent);
    }

    public int getStartLineHeightMax(Graphics2D graphics2D, int startLineX, String thisLeftStr) {

        FontMetrics fontMetrics = graphics2D.getFontMetrics(getFont());
        int thisHeight = fontMetrics.getHeight();
        int len = fontMetrics.stringWidth(thisLeftStr);
        if (startLineX + len < ticketDesign.getMaxX()) {
            TicketCom ticketCom = ticketDesign.getNextTicketCom(this);
            if (ticketCom != null) {
                if (ticketCom instanceof TicketComText) {
                    TicketComText ticketComText = (TicketComText) ticketCom;
                    String first = ticketComText.getSampleContent().substring(0, 1);
                    int firstWidth = graphics2D.getFontMetrics(ticketComText.getFont()).stringWidth(first);
                    if (startLineX + len + firstWidth < ticketDesign.getMaxX()) {
                        int nextHeight = ticketComText.getStartLineHeightMax(graphics2D, startLineX + len);
                        if (nextHeight > thisHeight) {
                            return nextHeight;
                        }
                    }
                }
            }
        }
        return thisHeight;
    }
}
