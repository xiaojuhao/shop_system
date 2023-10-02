package com.xjh.startup.foundation;

import com.google.inject.Singleton;
import com.xjh.common.utils.Logger;
import com.xjh.common.valueobject.DishesImgVO;
import com.xjh.dao.dataobject.Dishes;
import com.xjh.dao.mapper.DishesDAO;
import com.xjh.service.store.ImageHelper;
import org.apache.poi.ss.formula.functions.T;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Semaphore;


@Singleton
public class InitializeSystem {
    @Inject
    DishesDAO dishesDAO;

    public void loadImagesAsync() {
        Semaphore semaphore = new Semaphore(30);
        new Thread(() -> {
            List<Dishes> dishesList = dishesDAO.selectList(new Dishes());
            for (Dishes d : dishesList) {
                try {
                    List<DishesImgVO> imgs = ImageHelper.resolveImgs(d.getDishesImgs());
                    for (DishesImgVO img : imgs) {
                        // 本地已存在
                        if (ImageHelper.localExists(img.getImageSrc())) {
                            continue;
                        }
                        Logger.info("预加载图片: " + img.getImageSrc());
                        semaphore.acquire();
                        new Thread(() -> {
                            try {
                                ImageHelper.resolveImgUrl(img.getImageSrc());
                            } catch (Exception ex) {
                                Logger.info("loadImagesAsync异常: " + ex.getMessage());
                            } finally {
                                semaphore.release();
                            }
                        }).start();
                    }
                } catch (Exception ee) {
                    Logger.info("loadImagesAsync异常: " + ee.getMessage());
                }
            }
        }).start();
    }
}
