package com.xjh.startup.view;

import com.sun.scenario.animation.shared.TimerReceiver;
import com.xjh.common.utils.*;
import com.xjh.common.valueobject.AccountVO;
import com.xjh.dao.dataobject.Account;
import com.xjh.service.domain.AccountService;
import com.xjh.service.jobs.SchedJobService;
import com.xjh.startup.foundation.InitializeSystem;
import com.xjh.startup.foundation.constants.MainStageHolder;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.xjh.common.utils.CommonUtils.strContains;
import static com.xjh.common.utils.Const.*;

public class LoginController implements Initializable {
    @FXML
    private TextField accountField;
    @FXML
    private PasswordField passwordField;
    @FXML
    ImageView wxImg;
    @FXML
    ImageView zfbImg;
    @FXML
    ImageView dingdingImg;

    public void login()  {
        if (!SysConfigView.checkConfig()) {
            AlertBuilder.ERROR("提示", "系统基础配置缺失，请先配置!");
            return;
        }
        try {
            AccountService accountService = GuiceContainer.getInstance(AccountService.class);
            String account = accountField.getText().trim();
            String password = passwordField.getText().trim();
            // 调用登录功能
            Result<Account> accountCheck = accountService.checkPwd(account, password);
            if (accountCheck.isSuccess()) {
                AccountVO accVO = CopyUtils.convert(accountCheck.getData(), AccountVO.class);
                accVO.addRole(role_clerk);
                if ("1".equals(accVO.getAccountUser()) // test
                        || "root".equals(accVO.getAccountUser()) // root
                        || strContains(accVO.getAccountNickName(), "管理员"))  // 管理员
                {
                    accVO.addRole(role_su);
                }
                if (strContains(accVO.getAccountNickName(), "店长")) {
                    accVO.addRole(role_manager);
                }
                CurrentAccount.hold(accVO);
                // 创建主界面舞台
                Stage mainStage = MainStageHolder.get();
                //读入布局
                // FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                BorderPane main = new BorderPane();
                main.setTop(new MenuBarView(main).renderMenuBar());
                // 主体内容
                main.setCenter(new DeskListView());
                Scene scene = new Scene(main);
                scene.getStylesheets().add("/css/style.css");
                mainStage.setTitle("小句号点餐系统");
                Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
                double width = screenRectangle.getWidth();
                double height = screenRectangle.getHeight();
                mainStage.setWidth(width - 10);
                mainStage.setHeight(height - 10);
                mainStage.setScene(scene);

                TimeRecord timeRecord = TimeRecord.start();
                // 启动调度任务
                Safe.run(() -> GuiceContainer.getInstance(SchedJobService.class).startAllJobs());
                Logger.info("启动WebSocket服务器，cost " + timeRecord.getCostAndReset());

                Safe.run(() -> GuiceContainer.getInstance(InitializeSystem.class).loadImagesAsync());

            } else {
                AlertBuilder.ERROR("提示", accountCheck.getMsg());
            }
        }catch (Exception | Error ex){
            AlertBuilder.ERROR("提示", ex.getMessage());
        }
    }

    public void showConfig() {
        Stage cfgStg = new Stage();
        cfgStg.initOwner(accountField.getScene().getWindow());
        cfgStg.initModality(Modality.WINDOW_MODAL);
        cfgStg.initStyle(StageStyle.DECORATED);
        cfgStg.centerOnScreen();
        cfgStg.setWidth(600);
        cfgStg.setHeight(500);
        cfgStg.setTitle("系统配置");
        cfgStg.setScene(new Scene(new SysConfigView()));
        cfgStg.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountField.setText("1");
        passwordField.setText("1");
        wxImg.setImage(new Image("/img/weixin.png"));
        zfbImg.setImage(new Image("/img/zhifubao.png"));
        dingdingImg.setImage(new Image("/img/dingding.jpeg"));
    }
}
