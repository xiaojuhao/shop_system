/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.awt.*;

import com.google.zxing.WriterException;
import com.xjh.startup.foundation.constants.EnumComType;

/**
 * @author 36181
 */
public class TicketComQrCode extends TicketCom {
    @Override
    public Font getFont() {
        return getFont(0);
    }


    private String content = "a";

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int getComType() {
        return EnumComType.QRCODE.type;
    }

    private Shape shape;

    @Override
    public Shape getComShape() {
        return shape;
    }

    @Override
    public void drawCom(Graphics2D graphics2D) {
        downLine(getFrontEnterNum(), graphics2D, getFont());

        float sizeF = ((float) getSize() / 100f);
        if (sizeF > 1) {
            sizeF = 1f;
        } else if (sizeF < 0.2) {
            sizeF = 0.2f;
        }

        int width = (int) ((float) (ticketDesign.getMaxX() - ticketDesign.getMinX()) * sizeF);
        int grap = ticketDesign.getMaxX() - ticketDesign.getMinX() - width;

        try {
            PrinterCmdUtil.drawQRcode(getContent(), graphics2D,
                    ticketDesign.getMinX() + (grap / 2),
                    ticketDesign.getNextComStartY(),
                    width, width);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }

        shape = new Rectangle(ticketDesign.getMinX() + (grap / 2), ticketDesign.getNextComStartY(), width, width);
        ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + width);
        if (this.equals(ticketDesign.getSelectTicketCom())) {
            drawComSelected(graphics2D);
        } else if (shape.contains(ticketDesign.getNowMouseX(), ticketDesign.getNowMouseY())) {
            drawComMouseOn(graphics2D);
        }
        ticketDesign.setNextComStartX(ticketDesign.getMinX());
        ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + 1);

        downLine(getBehindEnterNum(), graphics2D, getFont());

        TicketCom ticketCom = ticketDesign.getNextTicketCom(this);
        if (ticketCom instanceof TicketComText) {
            TicketComText ticketComText = (TicketComText) ticketCom;
            int height = ticketComText.getStartLineHeightMax(graphics2D, ticketDesign.getMinX());
            ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + height);
        }
    }

}
