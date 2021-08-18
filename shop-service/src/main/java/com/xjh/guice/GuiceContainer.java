package com.xjh.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;
import com.xjh.dao.DaoModule;
import com.xjh.dao.DataSourceModule;
import com.xjh.service.ServiceModule;
import com.xjh.ws.WsHandlerModule;

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
                    new DataSourceModule(),
                    new WsHandlerModule()
            );
            injector.hold(ij);
            Logger.info("初始化Guice, 耗时:" + (System.currentTimeMillis() - start));
            return ij;
        }
    }

    public static <T> T getInstance(Class<T> clz) {
        return getInjector().getInstance(clz);
    }
}
