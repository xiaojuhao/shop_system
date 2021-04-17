package book.dao;

import com.google.inject.AbstractModule;

import book.dao.impl.AdminDAOImpl;
import book.dao.impl.BookDAOImpl;
import book.dao.impl.DeskDAOImpl;
import book.dao.impl.ReaderDAOImpl;
import book.dao.impl.TypeDAOImpl;

public class DaoModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AdminDAO.class).to(AdminDAOImpl.class);
        bind(BookDAO.class).to(BookDAOImpl.class);
        bind(DeskDAO.class).to(DeskDAOImpl.class);
        bind(ReaderDAO.class).to(ReaderDAOImpl.class);
        bind(TypeDAO.class).to(TypeDAOImpl.class);
    }
}
