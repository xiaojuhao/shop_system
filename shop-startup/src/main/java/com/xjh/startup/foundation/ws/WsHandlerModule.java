package com.xjh.startup.foundation.ws;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.service.ws.NotifyService;
import com.xjh.startup.foundation.ws.handler.*;

public class WsHandlerModule extends AbstractModule {
    @Override
    protected void configure() {
        TimeRecord timeRecord = TimeRecord.start();
        bind(AddCartHandler.class);
        bind(CheckDeskCartHandler.class);
        bind(CheckDeskInfoHandler.class);
        bind(ClearCartHandler.class);
        bind(CloseDeskHandler.class);
        bind(EraseHandler.class);
        bind(GetH5DeskInfoHandler.class);
        bind(GetPayCodeHandler.class);
        bind(GetPayInfoHandler.class);
        bind(GetUpdateDataPackageHandler.class);
        bind(H5ValidateHandler.class);
        bind(OpenDeskHandler.class);
        bind(OrderCartHandler.class);
        bind(RemoveDishesFromCartHandler.class);
        bind(ReturnDishesHandler.class);
        bind(SocketOpenHandler.class);
        bind(UpdateCartDishesHandler.class);
        bind(LoginHandler.class);
        bind(NotifyService.class);
        Logger.info("WebService模块注入 耗时:" + timeRecord.getCost());
    }
}
