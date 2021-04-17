package book.service;

import java.util.List;

import com.xjh.dao.dataobject.Book;

public interface BookService {
    /**
     * 新增图书，返回自增主键
     *
     * @param book
     * @return
     */
    Long addBook(Book book);

    /**
     * 根据id删除图书
     *
     * @param id
     * @return
     */
    void deleteBook(long id);

    /**
     * 更新图书信息
     *
     * @param book
     * @return
     */
    void updateBook(Book book);


    /**
     * 查询所有图书
     *
     * @return List<Book>
     */
    List<Book> getAllBooks();


    /**
     * 根据id查询图书信息
     *
     * @param id
     * @return Book
     */
    Book getBook(long id);

    /**
     * 根据书名关键词模糊查询图书
     *
     * @param keywords
     * @return List<Book>
     */
    List<Book> getBooksLike(String keywords);

    /**
     * 根据图书类别查询图书
     *
     * @param typeId
     * @return List<Book>
     */
    List<Book> getBooksByTypeId(long typeId);

    /**
     * 根据图书类别统计图书数量
     *
     * @param typeId
     * @return
     */
    int countByType(long typeId);
}
