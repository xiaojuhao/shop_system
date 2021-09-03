package com.xjh.ws;

import com.google.inject.AbstractModule;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;
import com.xjh.ws.handler.AddCartHandler;
import com.xjh.ws.handler.CheckDeskCartHandler;
import com.xjh.ws.handler.CheckDeskInfoHandler;
import com.xjh.ws.handler.ClearCartHandler;
import com.xjh.ws.handler.CloseDeskHandler;
import com.xjh.ws.handler.EraseHandler;
import com.xjh.ws.handler.GetH5DeskInfoHandler;
import com.xjh.ws.handler.GetPayCodeHandler;
import com.xjh.ws.handler.GetPayInfoHandler;
import com.xjh.ws.handler.GetUpdateDataPackageHandler;
import com.xjh.ws.handler.H5ValidateHandler;
import com.xjh.ws.handler.OpenDeskHandler;
import com.xjh.ws.handler.OrderCartHandler;
import com.xjh.ws.handler.RemoveDishesFromCartHandler;
import com.xjh.ws.handler.ReturnDishesHandler;
import com.xjh.ws.handler.SocketOpenHandler;
import com.xjh.ws.handler.UpdateCartDishesHandler;

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
        Logger.info("WebService模块注入 耗时:" + timeRecord.getCost());
    }
}
