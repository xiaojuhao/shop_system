package com.xjh.common.utils;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class ExcelExport {
    static String readCell(Cell cell){
        if(cell == null){
            return "";
        }
        if(cell.getCellTypeEnum() == CellType.NUMERIC){
            return cell.getNumericCellValue()+"";
        }
        return cell.getStringCellValue();
    }

    public static void main(String[] args) throws Exception {
        String file = "D:\\shop_system_data\\config.xlsx";
        InputStream is = new FileInputStream(file);
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(is);
        EncryptionInfo encryptionInfo = new EncryptionInfo(poifsFileSystem);
        Decryptor decryptor = Decryptor.getInstance(encryptionInfo);
        decryptor.verifyPassword("123456");
        Workbook wb = new XSSFWorkbook(decryptor.getDataStream(poifsFileSystem));

        Sheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {
            for (Cell cell : row) {
                System.out.print(readCell(cell) + " ");
            }
            System.out.println();
        }
    }
}
