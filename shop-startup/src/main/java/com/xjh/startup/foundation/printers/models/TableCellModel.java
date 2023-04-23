package com.xjh.startup.foundation.printers.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.xjh.startup.foundation.printers.models.TableRowModel.lengthOfChar;
import static com.xjh.startup.foundation.printers.models.TableRowModel.lengthOfStr;

@Data
public class TableCellModel {
    int percents;
    int charWidth;
    int align;
    String text;
    List<TableCellModel> splits = null;

    public int splitRows(int lineMaxNumChars) {
        if (text == null) {
            return 1;
        }
        int textLen = lengthOfStr(text);
        int charsPerLine = lineMaxNumChars * percents / 100;
        if (textLen % charsPerLine == 0) {
            return textLen / charsPerLine;
        } else {
            return textLen / charsPerLine + 1;
        }
    }

    public List<TableCellModel> splitTo(int splitsNum, int lineMaxNumChars) {
        int charsPerLine = lineMaxNumChars * percents / 100;
        List<String> splittedText = splitText(this.text, charsPerLine, splitsNum);
        if (this.splits == null) {
            this.splits = new ArrayList<>();
            for (int i = 0; i < splitsNum; i++) {
                TableCellModel cell = new TableCellModel();
                cell.setPercents(percents);
                cell.setAlign(align);
                cell.setCharWidth(charsPerLine);
                cell.setText(splittedText.get(i));
                this.splits.add(cell);
            }
        }
        return this.splits;
    }

    public static void main(String[] args) {
        String str = "中华人民共和国123456abcd";
        System.out.println(splitText(str, 3, 10));
        System.out.println(splitText(str, 4, 10));
        System.out.println(splitText(str, 5, 10));
        System.out.println(splitText(str, 6, 10));
        System.out.println(splitText(str, 7, 10));
    }

    private static List<String> splitText(String str, int perNum, int size) {
        List<String> list = new ArrayList<>();
        char[] chars = str.toCharArray();
        int charIdx = 0;
        for (int i = 0; i < size; i++) {
            StringBuilder sb = new StringBuilder();
            int cs = 0;
            while (charIdx < chars.length && cs < perNum - lengthOfChar(chars[charIdx])) {
                sb.append(chars[charIdx]);
                cs += lengthOfChar(chars[charIdx]);
                charIdx++;
            }
            list.add(sb.length() > 0 ? sb.toString() : "");
        }
        return list;
    }
}
