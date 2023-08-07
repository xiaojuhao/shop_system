package com.xjh.service.printers;

import com.google.inject.AbstractModule;

public class PrinterModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(OrderPrinterHelper.class);
    }
}
