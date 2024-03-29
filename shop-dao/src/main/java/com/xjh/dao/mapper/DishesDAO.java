package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.PageResult;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.OrElse;
import com.xjh.common.valueobject.PageCond;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.query.DishesQuery;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DishesDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int updateByDishesId(Dishes dishes) throws SQLException {
        return Db.use(ds).update(
                EntityUtils.create(dishes),
                EntityUtils.idCond(dishes)
        );
    }

    public int insert(Dishes dishes) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(dishes));
    }

    public int maxDishesId() throws SQLException {
        List<Entity> rs = Db.use(ds).query("select max(dishesId) as dishesId from dishes_list");
        return rs.get(0).getInt("dishesId");
    }

    public Dishes getById(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            Dishes cond = new Dishes();
            cond.setDishesId(id);
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, Dishes.class).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Dishes> getByIds(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        try {
            Entity cond = EntityUtils.pureCreate(Dishes.class);
            cond.set("dishesId", ids.toArray());
            List<Entity> list = Db.use(ds).findAll(cond);
            return EntityUtils.convertList(list, Dishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Dishes> selectList(Dishes cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return EntityUtils.convertList(list, Dishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Dishes> pageQuery(DishesQuery query) {
        try {
            int pageNo = OrElse.orGet(query.getPageNo(), 1);
            int pageSize = OrElse.orGet(query.getPageSize(), 20);
            StringBuilder where = new StringBuilder();
            List<Object> params = new ArrayList<>();
            if (CommonUtils.isNotBlank(query.getDishesName())) {
                where.append(" and dishesName like ?");
                params.add("%" + query.getDishesName() + "%");
            }
            if (query.getStatus() != null) {
                where.append(" and dishesStatus = ?");
                params.add(query.getStatus());
            }
            String sql = "select * from dishes_list where 1=1 " + where
                    + " limit " + (pageNo - 1) * pageSize + "," + pageSize;

            // System.out.println(sql + ", " + JSON.toJSONString(params));
            List<Entity> list = Db.use(ds).query(sql, params.toArray(new Object[0]));
            return EntityUtils.convertList(list, Dishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int pageCount(DishesQuery query) {
        try {
            StringBuilder where = new StringBuilder();
            List<Object> params = new ArrayList<>();
            if (CommonUtils.isNotBlank(query.getDishesName())) {
                where.append(" and dishesName like ?");
                params.add("%" + query.getDishesName() + "%");
            }
            if (query.getStatus() != null) {
                where.append(" and dishesStatus = ?");
                params.add(query.getStatus());
            }
            String sql = "select count(*) as cnt from dishes_list where 1=1 " + where;

            // System.out.println(sql + ", " + JSON.toJSONString(params));
            List<Entity> list = Db.use(ds).query(sql, params.toArray(new Object[0]));
            return list.get(0).getInt("cnt");
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public PageResult<Dishes> pageQuery(Dishes cond, PageCond page) {
        int pageNo = OrElse.orGet(page.getPageNo(), 1);
        int pageSize = OrElse.orGet(page.getPageSize(), 20);
        try {
            StringBuilder where = new StringBuilder();
            List<Object> params = new ArrayList<>();
            if (cond.getDishesId() != null) {
                where.append(" and dishesId = ?");
                params.add(cond.getDishesId());
            }
            if (CommonUtils.isNotBlank(cond.getDishesName())) {
                where.append(" and dishesName like ?");
                params.add("%" + cond.getDishesName() + "%");
            }
            if (cond.getDishesTypeId() != null) {
                where.append(" and dishesTypeId = ?");
                params.add(cond.getDishesTypeId());
            }
            if (cond.getDishesStatus() != null) {
                where.append(" and dishesStatus = ?");
                params.add(cond.getDishesStatus());
            }
            String sql = "select * from dishes_list where 1=1 " + where
                    + " limit " + (pageNo - 1) * pageSize + "," + pageSize;
            // System.out.println(sql + ", " + JSON.toJSONString(params));
            int total = Db.use(ds).queryNumber("select count(*) from dishes_list where 1=1 " + where, params.toArray(new Object[0])).intValue();
            List<Entity> list = Db.use(ds).query(sql, params.toArray(new Object[0]));
            List<Dishes> dataList = EntityUtils.convertList(list, Dishes.class);

            PageResult<Dishes> result = new PageResult<>(pageNo, pageSize, total);
            result.addAll(dataList);
            System.out.println("总页数:" + result.getTotalPage() + ", 当前页: " + pageNo + ", 每页:" + pageSize);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new PageResult<>(pageNo, pageSize, 0);
        }
    }
}
