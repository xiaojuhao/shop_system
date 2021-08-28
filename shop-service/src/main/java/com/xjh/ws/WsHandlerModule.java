package com.xjh.ws;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.ws.handler.AddCartHandler;
import com.xjh.ws.handler.CloseDeskHandler;
import com.xjh.ws.handler.GetPayCodeHandler;
import com.xjh.ws.handler.GetPayInfoHandler;
import com.xjh.ws.handler.GetUpdateDataPackageHandler;
import com.xjh.ws.handler.OpenDeskHandler;
import com.xjh.ws.handler.OrderCartHandler;
import com.xjh.ws.handler.SocketOpenHandler;

public class WsHandlerModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        bind(AddCartHandler.class);
        bind(CloseDeskHandler.class);
        bind(GetPayCodeHandler.class);
        bind(GetPayInfoHandler.class);
        bind(OpenDeskHandler.class);
        bind(OrderCartHandler.class);
        bind(SocketOpenHandler.class);
        bind(GetUpdateDataPackageHandler.class);
        Logger.info("WebService模块注入 耗时:" + timeRecord.getCost());
    }
}
