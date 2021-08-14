package com.xjh.startup.view;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.xjh.common.enumeration.EnumDesKStatus;
import com.xjh.common.utils.AlertBuilder;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Desk;
import com.xjh.dao.dataobject.Order;
import com.xjh.service.domain.DeskService;
import com.xjh.service.domain.OrderService;
import com.xjh.service.domain.model.OpenDeskParam;
import com.xjh.startup.foundation.guice.GuiceContainer;
import com.xjh.startup.view.model.OpenDeskInputParam;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DeskBox extends VBox {
    OrderService orderService = GuiceContainer.getInstance(OrderService.class);
    static final String TIME_FORMAT = "HH:mm:ss";
    static AtomicBoolean openingDesk = new AtomicBoolean(false);

    public DeskBox(SimpleObjectProperty<Desk> desk, double width) {
        double height = width / 2;
        this.setPrefSize(width, height);
        this.getStyleClass().add("desk");
        // this.setSpacing(10);
        // this.setAlignment(Pos.CENTER);
        EnumDesKStatus status = EnumDesKStatus.of(desk.get().getStatus());

        Canvas canvas = new Canvas();
        canvas.setWidth(width);
        canvas.setHeight(height);
        setBackground(this, status);
        canvas.setOnMouseClicked(evt -> this.onClickTable(desk));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 状态
        paintStatus(gc, width, status.remark());
        // 桌号
        paintTableNo(gc, desk.get(), width, height);

        this.getChildren().addAll(canvas);

        //        Label tableName = buildTableName(desk.get());
        //        Label statusLabel = new Label(status.remark());
        //        Label timeLabel = buildTimeLabel(desk.get());
        //        setBackground(this, status);

        // this.getChildren().addAll(tableName, statusLabel, timeLabel);

        desk.addListener((cc, _old, _new) -> {
            EnumDesKStatus desKStatus = EnumDesKStatus.of(_new.getStatus());
            setBackground(this, desKStatus);
            // statusLabel.setText(desKStatus.remark());

            Order order = orderService.getOrder(_new.getOrderId());

            Result<LocalDateTime> firstSubOrderTimeRs = orderService.firstSubOrderTime(_new.getOrderId());
            if (!firstSubOrderTimeRs.isSuccess()) {
                AlertBuilder.ERROR(firstSubOrderTimeRs.getMsg());
                return;
            }
            String time = "";
            LocalDateTime firstSubOrderTime = firstSubOrderTimeRs.getData();
            if (firstSubOrderTime != null && desKStatus != EnumDesKStatus.FREE) {
                long usedSeconds = DateBuilder.intervalSeconds(firstSubOrderTime, LocalDateTime.now());
                // timeLabel.setText("已用" + CommonUtils.formatSeconds(usedSeconds));
                time = CommonUtils.formatSeconds(usedSeconds);
            } else if (firstSubOrderTime == null) {
                // timeLabel.setText("未点菜");
                time = "未点菜";
            }
            //
            paintStatus(gc, width, desKStatus.remark());
            // 用餐时间
            paintTime(gc, width, height, time);
            // 人数
            if (order != null) {
                paintCustNum(gc, width, height, order.getOrderCustomerNums());
            }
        });

        // this.setOnMouseClicked(evt -> this.onClickTable(desk));
    }

    private Label buildTableName(Desk desk) {
        Label label = new Label(desk.getDeskName());
        label.setFont(new javafx.scene.text.Font("微软雅黑", 15));
        return label;
    }

    private Label buildTimeLabel(Desk desk) {
        Label timeLabel = new Label();
        timeLabel.setText(DateBuilder.base(desk.getOrderCreateTime()).format(TIME_FORMAT));
        return timeLabel;
    }

    private void setBackground(Node node, EnumDesKStatus status) {
        if (status == EnumDesKStatus.USED) {
            node.setStyle("-fx-background-color: #CD0000;");
        } else if (status == EnumDesKStatus.PAID) {
            node.setStyle("-fx-background-color: #00bfff;");
        } else {
            node.setStyle("-fx-background-color: #228B22;");
        }
    }

    private void onClickTable(SimpleObjectProperty<Desk> desk) {
        if (openingDesk.compareAndSet(false, true)) {
            try {
                DeskService deskService = GuiceContainer.getInstance(DeskService.class);
                EnumDesKStatus runStatus = EnumDesKStatus.of(desk.get().getStatus());
                if (runStatus == EnumDesKStatus.USED || runStatus == EnumDesKStatus.PAID) {
                    Window sceneWindow = this.getScene().getWindow();
                    Stage orderInfo = new Stage();
                    orderInfo.initOwner(sceneWindow);
                    orderInfo.initModality(Modality.WINDOW_MODAL);
                    orderInfo.initStyle(StageStyle.DECORATED);
                    orderInfo.centerOnScreen();
                    orderInfo.setWidth(sceneWindow.getWidth() / 10 * 9);
                    orderInfo.setHeight(sceneWindow.getHeight() / 10 * 9);
                    orderInfo.setTitle("订单详情");
                    orderInfo.setScene(new Scene(new OrderDetailView(desk.get())));
                    orderInfo.show();
                    System.gc();
                } else {
                    OpenDeskDialog dialog = new OpenDeskDialog(desk.get());
                    Optional<OpenDeskInputParam> result = dialog.showAndWait();
                    if (result.isPresent() && result.get().getResult() == 1) {
                        if (result.get().getCustomerNum() <= 0) {
                            AlertBuilder.ERROR("请输入就餐人数");
                            return;
                        }
                        OpenDeskParam openDeskParam = new OpenDeskParam();
                        openDeskParam.setDeskId(desk.get().getDeskId());
                        openDeskParam.setCustomerNum(result.get().getCustomerNum());
                        Result<String> openDeskRs = deskService.openDesk(openDeskParam);
                        if (!openDeskRs.isSuccess()) {
                            AlertBuilder.ERROR("开桌失败", openDeskRs.getMsg());
                        }
                    }
                }
            } finally {
                openingDesk.set(false);
            }
        }
    }

    private void paintStatus(GraphicsContext gc, double width, String statusName) {
        gc.save();
        gc.setFont(Font.font(12));
        gc.setFill(Color.PURPLE);
        gc.fillRect(width / 2, 0, width / 2, 25);
        gc.setFill(Color.WHITE);
        double statusOffset = width - CommonUtils.length(statusName) * 10 - 30;
        gc.fillText(statusName, statusOffset, 16);
        gc.restore();
    }

    private void paintTableNo(GraphicsContext gc, Desk desk, double width, double height) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(16));
        gc.fillText(desk.getDeskName(),
                width / 2 - CommonUtils.length(desk.getDeskName()) * 5,
                height / 2);
        gc.restore();
    }

    private void paintTime(GraphicsContext gc, double width, double height, String time) {
        gc.save();
        gc.clearRect(0, height - 20, width, 20);
        gc.setFont(Font.font(12));
        gc.setFill(Color.WHITE);
        gc.fillText(time, 10, height - 5);
        gc.restore();
    }

    private void paintCustNum(GraphicsContext gc, double width, double height, Integer num) {
        gc.save();
        gc.setFill(Color.GRAY);
        gc.fillOval(width - 20, height - 20, 20, 20);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(11));
        gc.fillText(num + "", width - 14, height - 6);
        gc.restore();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("DeskVeiew    销毁了。。。。。。。");
    }
}
