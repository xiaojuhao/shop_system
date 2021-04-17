package book.domain;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xjh.dao.DaoModule;

import book.datasource.DataSourceModule;
import book.service.ServiceModule;
import book.utils.Holder;

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
            Injector ij = Guice.createInjector(
                    new DaoModule(),
                    new ServiceModule(),
                    new DataSourceModule());
            injector.set(ij);
            return ij;
        }
    }

    public static <T> T getInstance(Class<T> clz) {
        return getInjector().getInstance(clz);
    }
}
