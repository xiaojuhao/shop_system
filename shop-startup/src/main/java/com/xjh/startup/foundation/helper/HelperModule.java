package com.xjh.startup.foundation.helper;

import com.google.inject.AbstractModule;

public class HelperModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(OrderPrinterHelper.class);
    }
}
