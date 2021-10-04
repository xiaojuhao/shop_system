/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import static com.xjh.startup.foundation.utils.PrinterImpl.BENPAO_ALIGN_CENTER;
import static com.xjh.startup.foundation.utils.PrinterImpl.BENPAO_ALIGN_RIGHT;

import java.awt.*;

/**
 * @author 36181
 */
public class TicketComTable extends TicketCom {
    private Object[][] columns;
    private String[][] datas;

    public int getColumnsNum() {
        if (columns != null) {
            return columns.length;
        }
        return 0;
    }

    public String getColumnTitle(int colIndex) {
        if (columns != null) {
            if (colIndex < columns.length) {
                return (String) columns[colIndex][0];
            }
        }
        return null;
    }

    public Object[][] getColumns() {
        return columns;
    }

    public String[][] getData() {
        return datas;
    }

    public void setColumns(Object[][] columns) {
        this.columns = columns;
    }

    public void setData(String[][] data) {
        this.datas = data;
    }

    @Override
    public int getComType() {
        return TicketCom.TYPE_TABLE;
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
        downLine(getFrontEnterNum(), graphics2D, getFont());
        int widthAll = ticketDesign.getMaxX() - ticketDesign.getMinX();
        int widthLeft = widthAll;
        int[] colWidths = new int[columns.length];
        int[] colAlign = new int[columns.length];
        String[] titleName = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            titleName[i] = (String) columns[i][0];
            JComboBoxItemInt jComboBoxItemIntWidthPercentage = (JComboBoxItemInt) columns[i][1];
            JComboBoxItemInt jComboBoxItemIntAlign = (JComboBoxItemInt) columns[i][2];
            int widthNow;
            if (i == columns.length - 1) {
                widthNow = widthLeft;
            } else {
                widthNow = widthAll * jComboBoxItemIntWidthPercentage.getValue() / 100;
            }
            if (jComboBoxItemIntAlign.getValue() == BENPAO_ALIGN_CENTER) {
                colAlign[i] = FontTool.ALIGN_TYPE_CENTER;
            } else if (jComboBoxItemIntAlign.getValue() == BENPAO_ALIGN_RIGHT) {
                colAlign[i] = FontTool.ALIGN_TYPE_RIGHT;
            } else {
                colAlign[i] = FontTool.ALIGN_TYPE_LEFT;
            }

            colWidths[i] = widthNow;
            widthLeft = widthLeft - widthNow;
        }
        graphics2D.setFont(getFont());


        yStart = ticketDesign.getNextComStartY();
        int yStartSave = yStart;

        writeRow(titleName, colWidths, colAlign, graphics2D);
        for (String[] data : datas) {
            writeRow(data, colWidths, colAlign, graphics2D);
        }
        ticketDesign.setNextComStartY(yStart);
        shape = new Rectangle(ticketDesign.getMinX(), yStartSave, widthAll, yStart - yStartSave);

        if (this.equals(ticketDesign.getSelectTicketCom())) {
            drawComSelected(graphics2D);
        } else if (shape.contains(ticketDesign.getNowMouseX(), ticketDesign.getNowMouseY())) {
            drawComMouseOn(graphics2D);
        }

        downLine(getBehindEnterNum(), graphics2D, getFont());

        TicketCom ticketCom = ticketDesign.getNextTicketCom(this);
        if (ticketCom instanceof TicketComText) {
            TicketComText ticketComText = (TicketComText) ticketCom;
            int height = ticketComText.getStartLineHeightMax(graphics2D, ticketDesign.getMinX());
            ticketDesign.setNextComStartY(ticketDesign.getNextComStartY() + height);
        }
    }

    private int yStart;

    protected void writeRow(String[] row, int[] colWidths, int[] colAlign, Graphics2D graphics2D) {
        if (row == null) {
            return;
        }
        if (row.length != columns.length) {
            return;
        }
        int xStart = ticketDesign.getMinX();
        int yMax = 0;
        for (int i = 0; i < row.length; i++) {
            int xEnd = xStart + colWidths[i];
            int yNow = FontTool.writeLinesStr(row[i], graphics2D, yStart, xStart, xEnd, colAlign[i]);
            xStart = xEnd;
            if (yNow > yMax) {
                yMax = yNow;
            }
        }

        yStart = yMax;
    }
}
