package com.xjh.startup.foundation.printers.models;

import com.xjh.common.enumeration.EnumPrinterType;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.OrElse;
import com.xjh.startup.foundation.constants.EnumAlign;
import lombok.Data;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.xjh.common.utils.CommonUtils.collValueIsEmpty;
import static com.xjh.startup.foundation.printers.StringUtil.alignString;

@Data
public class TableRowModel {
    List<TableCellModel> cells = new ArrayList<>();

    public boolean isEmpty() {
        boolean empty = true;
        if (!collValueIsEmpty(cells)) {
            for (TableCellModel c : cells) {
                if (c != null && CommonUtils.isNotBlank(c.text)) {
                    empty = false;
                }
            }
        }
        return empty;
    }

    public void addCell(String text, int percents, int align) {
        TableCellModel cell = new TableCellModel();
        cell.setAlign(align);
        cell.setText(text);
        cell.setPercents(percents);
        cells.add(cell);
    }

    public static void main(String[] args) {
        System.out.println(CommonUtils.max(1, 2));
        TableRowModel row = new TableRowModel();
        row.setCells(new ArrayList<>());
        TableCellModel c1 = new TableCellModel();
        c1.setPercents(50);
        c1.setAlign(EnumAlign.LEFT.type);
        c1.setText("中华1人民共和共中央人民政府农业农村部种子管理处中华人a民共和共中央人民政府农业农村部种子管理处");

        TableCellModel c2 = new TableCellModel();
        c2.setPercents(25);
        c2.setAlign(EnumAlign.RIGHT.type);
        c2.setText("价格1");

        TableCellModel c3 = new TableCellModel();
        c3.setPercents(25);
        c3.setAlign(EnumAlign.RIGHT.type);
        c3.setText("总计");

        row.setCells(newArrayList(c1, c2, c3));
        for (TableRowModel t : row.split(EnumPrinterType.T80)) {
            System.out.println(t.formatStr());
        }
    }

    public List<TableRowModel> split(EnumPrinterType printerType) {
        if (CommonUtils.isEmpty(cells)) {
            return newArrayList(this);
        }
        int maxSplitRows = 1;
        for (TableCellModel cell : cells) {
            maxSplitRows = CommonUtils.max(cell.splitRows(printerType.numOfChars), maxSplitRows);
        }
        List<TableRowModel> splits = new ArrayList<>();
        for (int i = 0; i < maxSplitRows; i++) {
            TableRowModel row = new TableRowModel();
            row.setCells(new ArrayList<>());
            for (TableCellModel cell : cells) {
                row.getCells().add(cell.splitTo(maxSplitRows, printerType.numOfChars).get(i));
            }
            splits.add(row);
        }
        return splits;
    }

    public String toString() {
        return formatStr();
    }

    public String formatStr() {
        StringBuilder sb = new StringBuilder();
        for (TableCellModel c : cells) {
            String text = OrElse.orGet(c.text, "--");
            String padded = alignString(text, c.charWidth, EnumAlign.of(c.align));
            sb.append(padded).append("(").append(lengthOfStr(padded)).append(")|");
        }
        return sb.toString();
    }

    public static int lengthOfStr(String str) {
        if (str == null) {
            return 0;
        }
        int len = 0;
        for (Character c : str.toCharArray()) {
            len += lengthOfChar(c);
        }
        return len;
    }

    public static int lengthOfChar(char c) {
        return (c + "").getBytes(Charset.forName("GBK")).length;
    }
}
