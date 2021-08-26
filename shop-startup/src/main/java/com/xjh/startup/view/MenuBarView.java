package com.xjh.startup.view;

import com.xjh.startup.foundation.constants.CurrentAccount;
import com.xjh.startup.foundation.constants.MainStageHolder;

import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MenuBarView {
    BorderPane root;

    public MenuBarView(BorderPane root) {
        this.root = root;
    }

    public MenuBar renderMenuBar() {
        MenuBar bar = new MenuBar();
        // 主页
        bar.getMenus().add(createIndexMenu());
        // 菜品管理
        bar.getMenus().add(createDishesMenu());
        // 打印管理
        bar.getMenus().add(createPrintMenu());
        // 订单管理
        bar.getMenus().add(createOrderMenu());
        // 预约点菜
        bar.getMenus().add(createPreserveMenu());
        // 登录
        bar.getMenus().add(createAccountMenu());
        return bar;
    }

    private Menu createIndexMenu() {
        Menu menu = new Menu();
        Label label = new Label("主页");
        label.setOnMouseClicked(evt -> root.setCenter(new DeskListView()));
        menu.setGraphic(label);
        return menu;
    }

    private Menu createAccountMenu() {
        Menu menu = new Menu("登录管理");
        MenuItem exitMenuItem = new MenuItem("退出");
        exitMenuItem.setOnAction(actionEvent -> logout());
        menu.getItems().addAll(exitMenuItem);
        return menu;
    }

    private Menu createDishesMenu() {
        Menu menu = new Menu("菜品管理");
        MenuItem updatePassword = new MenuItem("菜品属性管理");
        MenuItem updateName = new MenuItem("菜品分类管理");
        MenuItem subAccountItem = new MenuItem("菜品管理");
        MenuItem dishesCollMenu = new MenuItem("菜品集合管理");
        MenuItem menuManager = new MenuItem("菜单管理");
        MenuItem taocan = new MenuItem("套餐管理");
        menu.getItems().addAll(updatePassword, updateName, subAccountItem,
                dishesCollMenu, menuManager,
                new SeparatorMenuItem(), taocan);
        return menu;
    }

    private Menu createPrintMenu() {
        Menu menu = new Menu("打印管理");
        MenuItem updatePassword = new MenuItem("打印机管理");
        MenuItem updateName = new MenuItem("小票样式管理");
        MenuItem printJobMenu = new MenuItem("打印任务管理");
        menu.getItems().addAll(updatePassword, updateName, printJobMenu);
        return menu;
    }

    private Menu createOrderMenu() {
        Menu menu = new Menu("订单管理");
        MenuItem updatePassword = new MenuItem("订单列表");
        MenuItem updateName = new MenuItem("菜品销售统计");
        MenuItem printJobMenu = new MenuItem("菜品分类销售统计");
        MenuItem discount = new MenuItem("退菜记录");
        menu.getItems().addAll(updatePassword, updateName, printJobMenu, discount);
        return menu;
    }

    private Menu createPreserveMenu() {
        Menu menu = new Menu("预约点菜");
        MenuItem updatePassword = new MenuItem("预约点菜列表");
        menu.getItems().addAll(updatePassword);
        return menu;
    }

    private void logout() {
        try {
            //
            CurrentAccount.hold(null);
            //打开登录框
            Stage primaryStage = MainStageHolder.get();
            primaryStage.setTitle("登录系统");
            primaryStage.setScene(LoginView.getLoginView());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
