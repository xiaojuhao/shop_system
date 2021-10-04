package com.xjh.startup.foundation.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.dao.DaoModule;
import com.xjh.dao.DataSourceModule;
import com.xjh.service.ServiceModule;
import com.xjh.startup.foundation.helper.HelperModule;
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
            TimeRecord timeRecord = TimeRecord.start();
            Injector ij = Guice.createInjector(
                    new DaoModule(),
                    new ServiceModule(),
                    new DataSourceModule(),
                    new WsHandlerModule(),
                    new HelperModule()
            );
            injector.hold(ij);
            Logger.info("初始化Guice, 耗时:" + timeRecord.getCost());
            return ij;
        }
    }

    public static <T> T getInstance(Class<T> clz) {
        return getInjector().getInstance(clz);
    }
}
