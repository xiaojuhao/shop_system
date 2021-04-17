package book.dao;

import java.sql.SQLException;
import java.util.List;

import book.entity.Desk;


public interface DeskDAO {
    int insert(Desk desk) throws SQLException;

    List<Desk> select(Desk desk) throws SQLException;

    int placeOrder(Desk desk) throws SQLException;

    int clearOrder(Long id) throws SQLException;
}
