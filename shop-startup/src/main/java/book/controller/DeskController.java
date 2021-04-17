package book.controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import com.xjh.dao.DeskDAO;
import com.xjh.dao.dataobject.Desk;

import book.domain.GuiceContainer;
import book.enumeration.EnumDesKStatus;
import book.utils.CommonUtils;
import book.utils.DateBuilder;
import book.utils.ThreadUtils;
import cn.hutool.core.lang.Holder;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class DeskController implements Initializable {
    DeskDAO deskDAO = GuiceContainer.getInstance(DeskDAO.class);
    static Random random = new Random();

    static Holder<ScrollPane> holder = new Holder<>();

    public ScrollPane view() {
        ObservableList<SimpleObjectProperty<Desk>> tables = FXCollections.observableArrayList();
        Map<Long, SimpleObjectProperty<Desk>> tableMap = new HashMap<>();

        ScrollPane s = new ScrollPane();
        holder.set(s);
        FlowPane pane = new FlowPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPrefWidth(600);
        s.setFitToWidth(true);
        s.setContent(pane);

        ThreadUtils.runInDaemon(() -> {
            try {
                synchronized (DeskController.class) {
                    List<Desk> desks = listAllDesks();
                    tables.clear();
                    tableMap.clear();
                    desks.forEach(d -> {
                        SimpleObjectProperty<Desk> desk = new SimpleObjectProperty<>(d);
                        tables.add(desk);
                        tableMap.put(d.getId(), desk);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            while (holder.get() == s) {
                try {
                    CommonUtils.sleep(1000);
                    deskDAO.clearOrder((long) random.nextInt(22));
                    Desk placeDesk = new Desk();
                    placeDesk.setId((long) random.nextInt(22));
                    placeDesk.setOrderId("ORD");
                    placeDesk.setOrderCreateTime(LocalDateTime.now());
                    deskDAO.placeOrder(placeDesk);
                    synchronized (DeskController.class) {
                        List<Desk> desks = listAllDesks();
                        desks.forEach(d -> {
                            ObjectProperty<Desk> t = tableMap.get(d.getId());
                            if (t == null || t.get() == null) {
                                SimpleObjectProperty<Desk> desk = new SimpleObjectProperty<>(d);
                                tables.add(desk);
                                tableMap.put(d.getId(), desk);
                            } else {
                                Desk dd = t.get();
                                if (CommonUtils.ne(dd.getVerNo(), d.getVerNo())) {
                                    Platform.runLater(() -> t.set(d));
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("************** DeskController Destroyed......."
                    + this + "\t\t" + Thread.currentThread().getName());
            System.gc();
        });
        tables.addListener((ListChangeListener<SimpleObjectProperty<Desk>>) c -> Platform.runLater(() -> {
            while (c.next()) {
                Platform.runLater(() -> {
                    pane.getChildren().addAll(CommonUtils.collect(c.getAddedSubList(),
                            desk -> this.render(desk, pane)));
                });
            }
        }));

        return s;
    }

    private synchronized List<Desk> listAllDesks() throws SQLException {
        return deskDAO.select(new Desk());
    }

    VBox render(SimpleObjectProperty<Desk> tableProperty, FlowPane pane) {
        Desk table = tableProperty.get();
        double boxWidth = Math.max(pane.getWidth() / 6 - 15, 200);
        VBox vBox = new VBox();
        vBox.setPrefSize(boxWidth, boxWidth / 2);
        vBox.getStyleClass().add("desk");
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        EnumDesKStatus status = EnumDesKStatus.of(table.getStatus());
        if (status == EnumDesKStatus.USED || status == EnumDesKStatus.PAID) {
            vBox.setStyle("-fx-background-color: #ed6871;");
        } else {
            vBox.setStyle("-fx-background-color: #228B22;");
        }
        Label statusLabel = new Label(status.remark());
        Label timeLabel = new Label();
        timeLabel.setText(DateBuilder.base(
                table.getOrderCreateTime()).format("HH:mm:ss"));
        tableProperty.addListener((cc, old, newV) -> {
            LocalDateTime orderTime = newV.getOrderCreateTime();
            EnumDesKStatus s = EnumDesKStatus.of(newV.getStatus());
            Platform.runLater(() -> statusLabel.setText(s.remark()));
            if (s == EnumDesKStatus.USED || s == EnumDesKStatus.PAID) {
                vBox.setStyle("-fx-background-color: #ed6871;");
            } else {
                vBox.setStyle("-fx-background-color: #228B22;");
            }
            if (CommonUtils.ne(old.getOrderCreateTime(), orderTime)) {
                Platform.runLater(() ->
                        timeLabel.setText(DateBuilder.base(orderTime).format("HH:mm:ss")));
            }
        });

        Label tableNameLabel = new Label(table.getDeskName());
        tableNameLabel.setFont(new javafx.scene.text.Font("微软雅黑", 15));
        vBox.getChildren().addAll(tableNameLabel, statusLabel, timeLabel);
        vBox.setOnMouseClicked(evt -> {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Login Dialog");
            dialog.setHeaderText("Look, a Custom Login Dialog");
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            username.setText("作者：" + table.getDeskName());
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);
            dialog.getDialogPane().setContent(grid);
            ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
            dialog.showAndWait();

        });
        return vBox;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("DeskController被销毁了。。。。。。。。");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("****** " + this.getClass().getName() + " initialize.......");
    }
}
