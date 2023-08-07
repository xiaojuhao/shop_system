/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.service.printers;

import com.xjh.common.enumeration.EnumAlign;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangh
 */
public class StringUtil {
    //如果是字母的话，width>=1,如果有汉字的话，width>=2，不然的话会陷入死循环，要加控制使其跳出循环
    public static List<String> getGroup(String s, int width, int columnIndex) throws Exception {
        List<String> list = new ArrayList<>();
        int widthCount = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // 数组和字母：长度+1， 其他：长度+2
            widthCount += (c <= 127 ? 1 : 2);

            if (widthCount <= width) {
                sb.append(c);
                if (i == s.length() - 1) {
                    list.add(sb.toString());
                }
            } else {
                if (sb.length() == 0) {
                    throw new Exception(columnIndex + "");
                }
                list.add(sb.toString());
                sb = new StringBuilder();
                widthCount = 0;
                i--;
            }
        }
        return list;
    }

    public static int getCharCount(String s) {
        int charCount = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // 数组和字母：长度+1， 其他：长度+2
            charCount += (c <= 127 ? 1 : 2);
        }
        return charCount;
    }

    //s <=  width
    public static String leftString(String s, int width) {
        int charCount = StringUtil.getCharCount(s);
        int rightPadding = width - charCount;
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < rightPadding; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    //s <=  width
    public static String centerString(String s, int width) {
        int charCount = StringUtil.getCharCount(s);
        int leftPadding = (width - charCount) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            sb.append(" ");
        }
        sb.append(s);
        int rightPadding = width - leftPadding - charCount;
        for (int i = 0; i < rightPadding; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String rightString(String s, int width) {
        int charCount = StringUtil.getCharCount(s);
        int leftPadding = width - charCount;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            sb.append(" ");
        }
        sb.append(s);
        return sb.toString();
    }

    public static String alignString(String s, int width, EnumAlign align) {
        if (align == EnumAlign.CENTER) {
            return centerString(s, width);
        } else if (align == EnumAlign.RIGHT) {
            return rightString(s, width);
        } else {
            return leftString(s, width);
        }
    }

}
