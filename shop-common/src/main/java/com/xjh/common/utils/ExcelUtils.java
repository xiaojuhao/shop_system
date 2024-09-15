package com.xjh.common.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Date;
import java.util.List;

public class ExcelUtils {
    public static String readCell(Cell cell, boolean isDate){
        if(cell == null){
            return "";
        }
        String cellValue;
        if(cell.getCellTypeEnum() == CellType.NUMERIC){
            cellValue = cell.getNumericCellValue()+"";
        }else {
            cellValue = cell.getStringCellValue();
        }

        cellValue = CommonUtils.trim(cellValue);

        cellValue = cellValue.replaceAll("`", "");
        return cellValue;
    }

    public static <T> RowHeader<T> createHeader(List<String> headers, Class<T> dataClz){
        RowHeader<T> header = new RowHeader<>(dataClz);
        for(int i = 0; i < headers.size(); i++){
            header.addItem(i, headers.get(i));
        }
        return header;
    }
    public static <T> RowHeader<T> createHeader(Class<T> dataClz) {
        return new RowHeader<>(dataClz);
    }

    public static <T> RowHeader<T> readHeader(Sheet sheet, Class<T> dataClz) {
        return readHeader(sheet, 0, dataClz);
    }

    public static <T> RowHeader<T> readHeader(Sheet sheet, int headRow, Class<T> dataClz) {
        RowHeader<T> header = new RowHeader<>(dataClz);
        Row row = sheet.getRow(headRow);
        if (row != null) {
            for (int i = 0; i < row.getLastCellNum(); i++) {
                String colName = readCell(row.getCell(i), false);
                if (CommonUtils.isNotBlank(colName)) {
                    colName = colName.trim().replace("\n", "");
                    header.addItem(i, colName);
                }
            }
        }
        return header;
    }

    public static <T> Result<T> readRow(Row dataRow, RowHeader<T> header) {
        T t = header.newData();
        for (int i = 0; i < dataRow.getLastCellNum(); i++) {
            if(CommonUtils.isBlank(header.getIndexName(i))){
                continue;
            }
            ReflectionUtils.PropertyDescriptor pd = header.getIndexPd(i);
            if(pd != null){
                Class<?> ftype = pd.field.getType();
                String cellValue = readCell(dataRow.getCell(i), ftype == Date.class);
                pd.writeValue(t, ValueConvert.convert(cellValue, ftype));
            }else {
                System.out.println("第" + i + "列【"+header.getIndexName(i)+"】找不到模型映射关系");
                // return Result.fail("第" + i + "列【"+header.getIndexName(i)+"】找不到模型映射关系");
            }
        }
        return Result.success(t);
    }

}
