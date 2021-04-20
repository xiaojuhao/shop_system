package com.xjh.startup.foundation.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xjh.common.utils.Holder;
import com.xjh.dao.datasource.DataSourceModule;
import com.xjh.dao.DaoModule;
import com.xjh.service.ServiceModule;

public class GuiceContainer {
    static Holder<Injector> injector = new Holder<>();

    public static Injector getInjector() {
        if (injector.get() != null) {
            return injector.get();
        }
        synchronized (GuiceContainer.class) {
            if (injector.get() != null) {
                return injector.get();
            }
            long start = System.currentTimeMillis();
            Injector ij = Guice.createInjector(
                    new DaoModule(),
                    new ServiceModule(),
                    new DataSourceModule()
            );
            injector.set(ij);
            System.out.println("初始化Guice, 耗时:" + (System.currentTimeMillis() - start));
            return ij;
        }
    }

    public static <T> T getInstance(Class<T> clz) {
        return getInjector().getInstance(clz);
    }
}
