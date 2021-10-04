/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 36181
 */
public class FontTool {

    public static int ALIGN_TYPE_LEFT = 1;
    public static int ALIGN_TYPE_CENTER = 2;
    public static int ALIGN_TYPE_RIGHT = 3;
    public static int VERTICAL_ALIGN_TYPE_TOP = 1;
    public static int VERTICAL_ALIGN_TYPE_MIDDLE = 2;
    public static int VERTICAL_ALIGN_TYPE_BOTTOM = 3;

    public static Font[] getAllFonts() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return e.getAllFonts();
    }

    public static void writeLineStr(String str, Graphics2D graphics2D, Rectangle rectangle, int rowAlignType, int colAlignType) {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int y;
        if (colAlignType == VERTICAL_ALIGN_TYPE_TOP) {
            y = rectangle.y + ascent;
        } else if (colAlignType == VERTICAL_ALIGN_TYPE_BOTTOM) {
            y = (int) (rectangle.y + rectangle.getHeight() - descent);
        } else {
            int topGrap = (int) ((rectangle.getHeight() - fontMetrics.getHeight()) / 2);
            y = rectangle.y + topGrap;
            y = y + ascent;
        }

        int widthStr = fontMetrics.stringWidth(str);
        int x;
        if (rowAlignType == ALIGN_TYPE_LEFT) {
            x = rectangle.x;
        } else if (rowAlignType == ALIGN_TYPE_RIGHT) {
            x = rectangle.x + rectangle.width - widthStr;
        } else {
            int grap = (int) ((rectangle.getWidth() - widthStr) / 2);
            x = rectangle.x + grap;
        }

        graphics2D.drawString(str, x, y);
    }

    public static Object[] mergeRectangles(List<Rectangle> rectangles) {
        List<Point> pointsLeft = new ArrayList<>();
        List<Point> pointsRight = new ArrayList<>();
        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle rectangle = rectangles.get(i);
            pointsLeft.add(new Point(rectangle.x, rectangle.y));
            pointsLeft.add(new Point(rectangle.x, rectangle.y + rectangle.height));

            pointsRight.add(new Point(rectangle.x + rectangle.width, rectangle.y));
            pointsRight.add(new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height));

            if (i < (rectangles.size() - 1)) {
                Rectangle rectangleNex = rectangles.get(i + 1);
                int xEndNext = rectangleNex.x + rectangleNex.width;
                int xEnd = rectangle.x + rectangle.width;
                if (xEndNext > xEnd) {
                    pointsRight.add(new Point(xEnd, rectangleNex.y));
                } else {
                    pointsRight.add(new Point(xEndNext, rectangle.y + rectangle.height));
                }

                if (rectangle.x > rectangleNex.x) {
                    pointsLeft.add(new Point(rectangle.x, rectangleNex.y));
                }
            }
        }

        Polygon polygon = new Polygon();
        for (int i = 0; i < pointsLeft.size(); i++) {
            Point point = pointsLeft.get(i);
            polygon.addPoint(point.x, point.y);
        }
        Point[] points = new Point[3];
        int pointsIndex = 0;
        for (int i = pointsRight.size(); i > 0; i--) {
            Point point = pointsRight.get(i - 1);
            if (pointsIndex < 3) {
                points[pointsIndex] = point;
                pointsIndex++;
            }
            polygon.addPoint(point.x, point.y);
        }
        if (pointsIndex < 3) {
            return new Object[]{
                    polygon, points[0].x, points[0].y, points[1].y, points[1].y
            };
        }
        return new Object[]{
                polygon, points[0].x, points[0].y, points[1].y, points[2].y
        };
    }

    public static List<Rectangle> writeLinesStr(String str, Graphics2D graphics2D, int startX, int startY, int minX, int maxX, LineGrap lineGrap) {
        if (maxX <= 0) {
            return null;
        }

        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int height = fontMetrics.getHeight();

        int xNow = startX;
        //int lineGrapNow=lineGrap.getGrap(str, 0, xNow, graphics2D);
        int lineGrapNow;
        int yNow = startY + ascent;

        //List<Point> pointsLeft = new ArrayList<>();
        //List<Point> pointsRight = new ArrayList<>();
        char[] cs = str.toCharArray();

        int thisLineStartX = startX;
        int thisLineStartY = startY;
        int thisLineEndX = 0;

        List<Rectangle> linesShapes = new ArrayList<>();
        for (int i = 0; i < cs.length; i++) {
            String strNow = cs[i] + "";
            if (cs[i] == '\r') {
                thisLineEndX = xNow;
                xNow = minX;

                continue;
            }

            if (cs[i] == '\n') {
                lineGrapNow = lineGrap.getGrap(str, i + 1, xNow, graphics2D);
                if (i > 0) {
                    linesShapes.add(new Rectangle(thisLineStartX, yNow - ascent, thisLineEndX - thisLineStartX + 1, height));
                }

                yNow = yNow + descent + lineGrapNow + ascent;
                thisLineStartX = xNow;
                thisLineStartY = thisLineStartY + descent + lineGrapNow + ascent;

                continue;
            }
            int widthNow = fontMetrics.charWidth(cs[i]);
            if (xNow + widthNow <= (maxX)) {
                graphics2D.drawString(strNow, xNow, yNow);

                xNow = xNow + widthNow;
            } else {
                lineGrapNow = lineGrap.getGrap(str, i, minX, graphics2D);
                if (i > 0) {
                    linesShapes.add(new Rectangle(thisLineStartX, yNow - ascent, xNow - thisLineStartX + 1, height));
                }

                yNow = yNow + descent + lineGrapNow + ascent;
                xNow = minX;
                thisLineStartX = xNow;
                thisLineStartY = thisLineStartY + descent + lineGrapNow + ascent;

                i--;
            }
        }

        linesShapes.add(new Rectangle(thisLineStartX, yNow - ascent, xNow - thisLineStartX + 1, height));

        return linesShapes;

        //graphics2D.drawLine(xNow, yNow + descent, xNow + 10, yNow + descent);
        //System.out.println("yNow=["+yNow+"]");
        //return new Object[]{mergeRectangles(linesShapes),xNow - thisLineStartX + 1,yNow - ascent + height};
    }

    public static int writeLinesStr(String str, Graphics2D graphics2D, int startY, int minX, int maxX, int rowAlignType) {
        if (minX >= maxX) {
            return startY;
        }
        //System.out.println("startY["+startY+"],minX["+minX+"],maxX["+maxX+"],rowAlignType["+rowAlignType+"]");
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int widthMax = maxX - minX;
        char[] cs = str.toCharArray();
        String line = "";

        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == '\r') {
                continue;
            }
            if (cs[i] == '\n') {
                Rectangle rectangle = new Rectangle(minX, startY, widthMax, (int) ((float) fontMetrics.getHeight() * 1.2f));
                writeLineStr(line, graphics2D, rectangle, rowAlignType, FontTool.VERTICAL_ALIGN_TYPE_MIDDLE);

                startY = startY + rectangle.height;
                line = "";
                continue;
            }
            if (fontMetrics.stringWidth(line + cs[i]) <= widthMax) {
                line = line + cs[i];
            } else {
                Rectangle rectangle = new Rectangle(minX, startY, widthMax, (int) ((float) fontMetrics.getHeight() * 1.2f));
                writeLineStr(line, graphics2D, rectangle, rowAlignType, FontTool.VERTICAL_ALIGN_TYPE_MIDDLE);

                startY = startY + rectangle.height;
                line = "";
                i--;
            }
        }

        if (line.length() > 0) {
            Rectangle rectangle = new Rectangle(minX, startY, widthMax, (int) ((float) fontMetrics.getHeight() * 1.2f));
            writeLineStr(line, graphics2D, rectangle, rowAlignType, FontTool.VERTICAL_ALIGN_TYPE_MIDDLE);

            startY = startY + rectangle.height;
        }

        return startY;
    }

    public static void writeLinesStr(String str, Graphics2D graphics2D, Rectangle rectangle) {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int xNow = rectangle.x;
        int yNow = rectangle.y + ascent;
        int lineGrap = fontMetrics.getHeight() / 5;

        char[] cs = str.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            String strNow = cs[i] + "";
            if (cs[i] == '\r') {
                xNow = rectangle.x;
                continue;
            }

            if (cs[i] == '\n') {
                yNow = yNow + descent + lineGrap + ascent;
                continue;
            }
            int widthNow = fontMetrics.charWidth(cs[i]);
            if (xNow + widthNow <= (rectangle.x + rectangle.width) && yNow + descent <= (rectangle.y + rectangle.height)) {
                graphics2D.drawString(strNow, xNow, yNow);
                xNow = xNow + widthNow;
                //System.out.println("xNow["+xNow+"]yNow["+yNow+"]widthNow["+widthNow+"]");
            } else if (xNow + widthNow > (rectangle.x + rectangle.width)) {
                yNow = yNow + descent + lineGrap + ascent;
                xNow = rectangle.x;
                i--;
            } else {
                break;
            }
        }

        //System.out.println("main.FontTool.writeLinesStr(--------**************************************************------->)");
    }
}
