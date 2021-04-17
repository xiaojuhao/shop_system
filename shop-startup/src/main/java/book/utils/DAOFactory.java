package book.utils;


import book.dao.AdminDAO;
import book.dao.BookDAO;
import book.dao.DeskDAO;
import book.dao.ReaderDAO;
import book.dao.TypeDAO;
import book.dao.impl.AdminDAOImpl;
import book.dao.impl.BookDAOImpl;
import book.dao.impl.DeskDAOImpl;
import book.dao.impl.ReaderDAOImpl;
import book.dao.impl.TypeDAOImpl;

/**
 * 工厂类，用静态方法来生成各个DAO实例
 */
public class DAOFactory {
    /**
     * 静态方法，返回TypeDAO实现类的对象
     *
     * @return
     */
    public static TypeDAO getTypeDAOInstance() {
        return new TypeDAOImpl();
    }

    /**
     * 静态方法，返回BookDAO实现类的对象
     *
     * @return
     */
    public static BookDAO getBookDAOInstance() {
        return new BookDAOImpl();
    }

    /**
     * 静态方法，返回ReaderDAO实现类的对象
     *
     * @return
     */
    public static ReaderDAO getReaderDAOInstance() {
        return new ReaderDAOImpl();
    }

    public static AdminDAO getAdminDAOInstance() {
        return new AdminDAOImpl();
    }

    public static DeskDAO getDeskDAO() {
        return new DeskDAOImpl();
    }
}
