package book.service.impl;

import java.sql.SQLException;

import book.dao.AdminDAO;
import book.dao.BookDAO;
import book.dao.ReaderDAO;
import book.dao.TypeDAO;
import book.service.AnalysisService;
import book.utils.DAOFactory;


public class AnalysisServiceImpl implements AnalysisService {
    private TypeDAO typeDAO = DAOFactory.getTypeDAOInstance();
    private BookDAO bookDAO = DAOFactory.getBookDAOInstance();
    private ReaderDAO readerDAO = DAOFactory.getReaderDAOInstance();
    private AdminDAO adminDAO = DAOFactory.getAdminDAOInstance();

    @Override
    public int getTypesCount() {
        int n = 0;
        try {
            n = typeDAO.countTypes();
        } catch (SQLException e) {
            System.err.println("统计类别总数出现异常");
        }
        return n;
    }

    @Override
    public int getBooksCount() {
        int n = 0;
        try {
            n = bookDAO.countBooks();
        } catch (SQLException e) {
            System.err.println("统计图书总数出现异常");
        }
        return n;
    }

    @Override
    public int getReadersCount() {
        int n = 0;
        try {
            n = readerDAO.countReaders();
        } catch (SQLException e) {
            System.err.println("统计读者总数出现异常");
        }
        return n;
    }

    @Override
    public int getAdminsCount() {
        int n = 0;
        try {
            n = adminDAO.countAdmins();
        } catch (SQLException e) {
            System.err.println("统计管理员总数出现异常");
        }
        return n;
    }
}
