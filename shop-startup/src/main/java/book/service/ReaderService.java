package book.service;

import java.util.List;

import com.xjh.dao.dataobject.Reader;

/**
 * 读者业务逻辑接口
 */
public interface ReaderService {
    /**
     * 查询所有读者信息
     *
     * @return List<Reader>
     */
    List<Reader> getAllReaders();

    /**
     * 根据id删除读者
     *
     * @param id
     */
    void deleteReader(long id);

    /**
     * 新增一个读者，返回自增主键
     *
     * @param reader
     * @return long
     */
    Long addReader(Reader reader);

    /**
     * 根据身份角色统计读者数量
     *
     * @param role
     * @return
     */
    int countByRole(String role);

    /**
     * 根据身份角色统计读者数量
     *
     * @param department
     * @return
     */
    int countByDepartment(String department);
}
