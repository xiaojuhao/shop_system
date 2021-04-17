package book.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MenuBarController {
    BorderPane root;

    public MenuBarController(BorderPane root) {
        this.root = root;
    }

    public MenuBar renderMenuBar() {
        MenuBar bar = new MenuBar();
        // 主页
        bar.getMenus().add(createIndexMenu());
        // 菜品管理
        bar.getMenus().add(createDishesMenu());
        // 餐桌管理
        bar.getMenus().add(createTableMenu());
        // 会员管理
        bar.getMenus().add(createMemberMenu());
        // 打印管理
        bar.getMenus().add(createPrintMenu());
        // 卡券管理
        bar.getMenus().add(createCouponMenu());
        // 订单管理
        bar.getMenus().add(createOrderMenu());
        // 门店管理
        bar.getMenus().add(createShopMenu());
        // 设备管理
        bar.getMenus().add(createDeviceMenu());
        // 预约点菜
        bar.getMenus().add(createPreserveMenu());
        // 账号管理
        bar.getMenus().add(createAccountMenu());
        return bar;
    }

    private Menu createIndexMenu() {
        Menu menu = new Menu();
        Label label = new Label("主页");
        label.setOnMouseClicked(evt -> {
            System.out.println("主页点击。。。。。。");
            root.setCenter(new DeskController().view());
        });
        menu.setGraphic(label);
        return menu;
    }

    private Menu createAccountMenu() {
        Menu menu = new Menu("账号管理");
        MenuItem updatePassword = new MenuItem("修改密码");
        MenuItem updateName = new MenuItem("修改名称");
        MenuItem subAccountItem = new MenuItem("子账号管理");
        MenuItem exitMenuItem = new MenuItem("退出");
        exitMenuItem.setOnAction(actionEvent -> logout());
        menu.getItems().addAll(updatePassword, updateName, subAccountItem,
                new SeparatorMenuItem(), exitMenuItem);
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

    private Menu createTableMenu() {
        Menu menu = new Menu("餐桌管理");
        MenuItem updatePassword = new MenuItem("餐桌分类管理");
        updatePassword.setOnAction(evt -> switchView("book.fxml"));
        MenuItem updateName = new MenuItem("餐桌管理");
        updateName.setOnAction(evt -> switchView("view_book.fxml"));
        MenuItem tableView = new MenuItem("餐桌列表");
        tableView.setOnAction(evt -> switchView("view_desk.fxml"));
        menu.getItems().addAll(updatePassword, updateName, tableView);
        return menu;
    }

    private Menu createMemberMenu() {
        Menu menu = new Menu("会员管理");
        MenuItem updatePassword = new MenuItem("会员列表");
        MenuItem updateName = new MenuItem("会员分类管理");
        menu.getItems().addAll(updatePassword, updateName);
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

    private Menu createCouponMenu() {
        Menu menu = new Menu("卡券管理");
        MenuItem updatePassword = new MenuItem("代金券管理");
        MenuItem updateName = new MenuItem("多次使用代金券");
        MenuItem printJobMenu = new MenuItem("试吃券管理");
        MenuItem discount = new MenuItem("折扣券管理");
        MenuItem card = new MenuItem("储值卡管理");
        MenuItem serial = new MenuItem("序列号管理");
        menu.getItems().addAll(updatePassword, updateName, printJobMenu,
                discount, card, serial);
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

    private Menu createShopMenu() {
        Menu menu = new Menu("门店管理");
        MenuItem updatePassword = new MenuItem("门店管理");
        menu.getItems().addAll(updatePassword);
        return menu;
    }

    private Menu createDeviceMenu() {
        Menu menu = new Menu("设备管理");
        MenuItem updatePassword = new MenuItem("设备管理");
        menu.getItems().addAll(updatePassword);
        return menu;
    }

    private Menu createPreserveMenu() {
        Menu menu = new Menu("预约点菜");
        MenuItem updatePassword = new MenuItem("预约点菜列表");
        menu.getItems().addAll(updatePassword);
        return menu;
    }

    private void switchView(String fileName) {
        try {
            AnchorPane pane = new FXMLLoader(getClass().getResource("/fxml/" + fileName)).load();
            root.setCenter(pane);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void logout() {
        try {
            //关闭主界面
            Stage mainStage = (Stage) root.getScene().getWindow();
            mainStage.close();
            //弹出登录界面
            Stage loginStage = new Stage();
            loginStage.setTitle("Admin Login");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/style.css");
            loginStage.setMaximized(true);
            loginStage.getIcons().add(new Image("/img/logo.png"));
            loginStage.setScene(scene);
            loginStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
