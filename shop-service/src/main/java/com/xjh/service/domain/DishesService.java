package com.xjh.service.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.ImageHelper;
import com.xjh.common.valueobject.DishesImgVO;
import com.xjh.common.valueobject.PageCond;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.dataobject.DishesPrice;
import com.xjh.dao.dataobject.DishesType;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.dao.mapper.DishesPriceDAO;

@Singleton
public class DishesService {
    @Inject
    DishesDAO dishesDAO;
    @Inject
    DishesPriceDAO dishesPriceDAO;

    public int save(Dishes dishes) throws Exception {
        if (dishes.getDishesId() != null) {
            return dishesDAO.updateByDishesId(dishes);
        } else {
            int maxId = dishesDAO.maxDishesId();
            dishes.setDishesId(maxId + 1);
            return dishesDAO.insert(dishes);
        }
    }

    public Dishes getById(Integer id) {
        return dishesDAO.getById(id);
    }

    public List<Dishes> getByIds(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return dishesDAO.getByIds(ids);
    }

    public Map<Integer, Dishes> getByIdsAsMap(List<Integer> ids) {
        return CommonUtils.listToMap(getByIds(ids), Dishes::getDishesId);
    }

    public List<Dishes> selectList(Dishes cond) {
        return dishesDAO.selectList(cond);
    }

    public List<Dishes> getAllDishes() {
        return selectList(new Dishes());
    }

    public List<DishesPrice> queryDishesPrice(Integer dishesId) {
        return dishesPriceDAO.queryByDishesId(dishesId);
    }

    public List<DishesImgVO> resolveImgs(Dishes dishes) {
        if (dishes == null) {
            return new ArrayList<>();
        }
        return ImageHelper.resolveImgs(dishes.getDishesImgs());
    }

    public String getDishesImageUrl(Dishes dishes) {
        List<DishesImgVO> imgs = resolveImgs(dishes);
        if (imgs.size() > 0) {
            return imgs.get(0).getImageSrc();
        } else {
            return null;
        }
    }

    public int getDishesCurrentStatus(Dishes dishes) {
        if (CommonUtils.isBlank(dishes.getValidTime())) {
            return dishes.getDishesStatus();
        }
        JSONArray jSONArray = JSONArray.parseArray(dishes.getValidTime());
        if (jSONArray.size() <= 0) {
            return dishes.getDishesStatus();
        } else {
            boolean isOpen = false;
            Date date = new Date();
            String weekDay = this.getWeekOfDate(date);

            for (int i = 0; i < jSONArray.size(); ++i) {
                String ss = jSONArray.get(i).toString();
                String weekDayString = ss.substring(0, 1);
                String startTime = ss.substring(2, 7);
                String endTime = ss.substring(8);
                if (weekDay.equals(weekDayString) && this.compareDateSize(startTime + ":00", endTime + ":59")) {
                    isOpen = true;
                    break;
                }
            }

            if (dishes.getDishesStatus() == 1) {
                return isOpen ? 1 : 0;
            } else {
                return 0;
            }
        }
    }

    public int getDishesTypeCurrentStatus(DishesType dishesType) {
        if (CommonUtils.isBlank(dishesType.getValidTime())) {
            return dishesType.getTypeStatus();
        }
        JSONArray jSONArray = JSONArray.parseArray(dishesType.getValidTime());
        if (jSONArray.size() <= 0) {
            return dishesType.getTypeStatus();
        } else {
            boolean isOpen = false;
            Date date = new Date();
            String weekDay = this.getWeekOfDate(date);
            for (int i = 0; i < jSONArray.size(); ++i) {
                String ss = jSONArray.get(i).toString();
                String weekDayString = ss.substring(0, 1);
                String startTime = ss.substring(2, 7);
                String endTime = ss.substring(8);
                if (weekDay.equals(weekDayString) && this.compareDateSize(startTime + ":00", endTime + ":59")) {
                    isOpen = true;
                    break;
                }
            }

            if (dishesType.getTypeStatus() == 1) {
                return isOpen ? 1 : 2;
            } else {
                return 2;
            }
        }
    }

    private boolean compareDateSize(String startTime, String endTime) {
        try {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            Date dt1 = df.parse(startTime);
            Date dt2 = df.parse(endTime);
            Date dt = df.parse(df.format(date));
            return dt.getTime() > dt1.getTime() && dt.getTime() < dt2.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private String getWeekOfDate(Date dt) {
        String[] weekDays = new String[]{"7", "1", "2", "3", "4", "5", "6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(7) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public List<Dishes> pageQuery(Dishes cond, PageCond page) {
        return dishesDAO.pageQuery(cond, page);
    }
}
