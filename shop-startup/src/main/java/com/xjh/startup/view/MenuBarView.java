package com.xjh.startup.view;

import com.xjh.common.utils.CurrentAccount;
import com.xjh.startup.foundation.constants.MainStageHolder;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.MediumForm;
import com.xjh.startup.view.ordermanage.OrderDishesSaleStatisticsView;
import com.xjh.startup.view.ordermanage.OrderDishesTypeSaleStatisticsView;
import com.xjh.startup.view.ordermanage.OrderManageListView;
import com.xjh.startup.view.ordermanage.OrderManageReturnedListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import static com.xjh.common.utils.Const.role_su;

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
        MenuItem dishesAttrManger = new MenuItem("菜品属性管理");
        dishesAttrManger.setOnAction(evt -> openView("菜品属性管理", new DishesAttributeManageView()));

        MenuItem dishesCate = new MenuItem("菜品分类管理");
        dishesCate.setOnAction(evt -> openView("菜品分类管理", new DishesTypeManageListView()));

        MenuItem dishesManager = new MenuItem("菜品管理");
        dishesManager.setOnAction(evt -> openView("菜品管理", new DishesManageListView()));

        MenuItem dishesCollMenu = new MenuItem("菜品集合管理");
        dishesCollMenu.setOnAction(evt -> openView("菜品集合管理", new DishesGroupManageListView(), 600, 500));
        // MenuItem menuManager = new MenuItem("菜单管理");
        // menuManager.setOnAction(e -> openWebView("菜单管理"));

        MenuItem taocan = new MenuItem("套餐管理");
        taocan.setOnAction(evt -> openView("套餐管理", new PackageDishesManageListView()));

        menu.getItems().addAll(dishesAttrManger, dishesCate, dishesManager,
                dishesCollMenu, taocan);
        return menu;
    }

    private Menu createPrintMenu() {
        Menu menu = new Menu("打印管理");
        MenuItem printerList = new MenuItem("打印机管理");
        printerList.setOnAction(evt -> openView("打印机管理", new PrinterManageListView()));
        // MenuItem updateName = new MenuItem("小票样式管理");
        MenuItem printJobMenu = new MenuItem("打印任务管理");
        printJobMenu.setOnAction(evt -> openView("打印任务管理", new PrinterTaskManageView(), 600, 400));
        menu.getItems().addAll(printerList, printJobMenu);
        return menu;
    }

    private Menu createOrderMenu() {
        Menu menu = new Menu("订单管理");
        MenuItem orderList = new MenuItem("订单列表");
        orderList.setOnAction(evt -> openView("订单列表", new OrderManageListView()));

        MenuItem dishesSaleStatistics = new MenuItem("菜品销售统计");
        dishesSaleStatistics.setOnAction(evt -> openView("菜品销售统计", new OrderDishesSaleStatisticsView()));

        MenuItem dishesTypeSaleStatistics = new MenuItem("菜品分类销售统计");
        dishesTypeSaleStatistics.setOnAction(evt -> openView("菜品分类销售统计", new OrderDishesTypeSaleStatisticsView()));

        MenuItem returnedDishes = new MenuItem("退菜记录");
        returnedDishes.setOnAction(evt -> openView("退菜记录", new OrderManageReturnedListView()));

        menu.getItems().addAll(orderList, dishesSaleStatistics, dishesTypeSaleStatistics, returnedDishes);
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

    //    private void openWebView(String title) {
    //        try {
    //            WebView w = new WebView();
    //            WebEngine engine = w.getEngine();
    //            engine.setJavaScriptEnabled(true);
    //            engine.load(HtmlLoader.load("ws.html").toString());
    //            openView(title, w);
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //        }
    //    }

    private void openView(String title, Parent content) {
        Window sceneWindow = MainStageHolder.get().getScene().getWindow();
        double width = sceneWindow.getWidth() * 0.96;
        double height = sceneWindow.getHeight() * 0.96;
        if (content instanceof MediumForm) {
            width = sceneWindow.getWidth() * 0.66;
            height = sceneWindow.getHeight() * 0.66;
        }
        openView(title, content, width, height);
    }

    private void openView(String title, Parent content, double width, double height) {
        Window sceneWindow = MainStageHolder.get().getScene().getWindow();
        Stage stage = new Stage();
        stage.initOwner(sceneWindow);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setTitle(title);
        if (content != null) {
            stage.setScene(new Scene(content));
            if (content instanceof Initializable) {
                ((Initializable) content).initialize();
            }
        }
        stage.showAndWait();
    }
}
