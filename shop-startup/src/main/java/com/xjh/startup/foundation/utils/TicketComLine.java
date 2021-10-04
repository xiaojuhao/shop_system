/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.awt.*;


/**
 * @author 36181
 */
public class TicketComLine extends TicketCom {

    @Override
    public int getComType() {
        return TYPE_LINE;
    }

    private Shape shape;

    @Override
    public Shape getComShape() {
        return shape;
    }

    @Override
    protected void downLine(int lineNum, Graphics2D graphics2D, Font font) {
        ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + (lineNum * graphics2D.getFontMetrics(getFont(0)).getHeight()));
        ticketDesign.setNextComStartX(ticketDesign.getMinX());
    }

    @Override
    public void drawCom(Graphics2D graphics2D) {
        //graphics2D.drawLine(ticketDesign.getMinX(), ticketDesign.getNextComStartY(), ticketDesign.getMaxX(), ticketDesign.getNextComStartY());
        graphics2D.setFont(getFont());
        downLine(getFrontEnterNum(), graphics2D, getFont());
        //downLine(1, graphics2D, getFont());

        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        Rectangle rectangle = new Rectangle(ticketDesign.getMinX(), ticketDesign.getNextComStartY(), ticketDesign.getMaxX() - ticketDesign.getMinX(), fontMetrics.getHeight());
        String lineStr = "-";
        while (fontMetrics.stringWidth(lineStr + " -") < ticketDesign.getMaxX() - ticketDesign.getMinX()) {
            lineStr = lineStr + " -";
        }

        shape = rectangle;
        if (this.equals(ticketDesign.getSelectTicketCom())) {
            drawComSelected(graphics2D);
        } else if (shape.contains(ticketDesign.getNowMouseX(), ticketDesign.getNowMouseY())) {
            drawComMouseOn(graphics2D);
        }

        ticketDesign.setNextComStartX(ticketDesign.getMinX());
        ticketDesign.setNextComStartY(rectangle.y + rectangle.height + 1);
        downLine(getBehindEnterNum(), graphics2D, getFont());

        TicketCom ticketCom = ticketDesign.getNextTicketCom(this);
        if (ticketCom instanceof TicketComText) {
            TicketComText ticketComText = (TicketComText) ticketCom;
            int height = ticketComText.getStartLineHeightMax(graphics2D, ticketDesign.getMinX());
            ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + height);
        }
    }

}
