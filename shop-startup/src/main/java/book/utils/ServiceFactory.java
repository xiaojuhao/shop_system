package book.utils;

import book.service.AdminService;
import book.service.AnalysisService;
import book.service.BookService;
import book.service.ReaderService;
import book.service.TypeService;
import book.service.impl.AdminServiceImpl;
import book.service.impl.AnalysisServiceImpl;
import book.service.impl.BookServiceImpl;
import book.service.impl.ReaderServiceImpl;
import book.service.impl.TypeServiceImpl;

/**
 * 业务逻辑类工厂
 */
public class ServiceFactory {
    public static TypeService getTypeServiceInstance() {
        return new TypeServiceImpl();
    }

    public static BookService getBookServiceInstance() {
        return new BookServiceImpl();
    }

    public static ReaderService getReaderServiceInstance() {
        return new ReaderServiceImpl();
    }

    public static AdminService getAdminServiceInstance() {
        return new AdminServiceImpl();
    }

    public static AnalysisService getAnalysisServiceInstance() {
        return new AnalysisServiceImpl();
    }
}
