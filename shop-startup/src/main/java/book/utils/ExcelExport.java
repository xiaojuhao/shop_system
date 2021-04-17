package book.utils;

import java.util.List;

import book.entity.Book;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class ExcelExport {
    public static void export(List<Book> bookList) {
        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("d:/books.xlsx");
        // 合并单元格后的标题行，使用默认标题样式
        writer.merge(7, "图书信息表");
        // 一次性写出内容，使用默认样式
        writer.write(bookList);
        // 关闭writer，释放内存
        writer.close();
    }
}
