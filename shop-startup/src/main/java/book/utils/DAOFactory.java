package book.utils;

import com.xjh.dao.AdminDAO;
import com.xjh.dao.BookDAO;
import com.xjh.dao.DeskDAO;
import com.xjh.dao.ReaderDAO;
import com.xjh.dao.TypeDAO;
import com.xjh.dao.impl.AdminDAOImpl;
import com.xjh.dao.impl.BookDAOImpl;
import com.xjh.dao.impl.DeskDAOImpl;
import com.xjh.dao.impl.ReaderDAOImpl;
import com.xjh.dao.impl.TypeDAOImpl;

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
