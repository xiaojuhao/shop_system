package book.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.xjh.dao.dataobject.Admin;

import book.domain.GuiceContainer;
import book.service.AdminService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class AdminController implements Initializable {
    @FXML
    private ListView<Admin> adminListView;

    private AdminService adminService = GuiceContainer.getInstance(AdminService.class);

    private ObservableList<Admin> observableList = FXCollections.observableArrayList();

    private List<Admin> adminList = new ArrayList<>();

    private static final int MAX_THREADS = 4;
    //线程池配置
    private final Executor exec = Executors.newFixedThreadPool(MAX_THREADS, runnable -> {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminList = adminService.getAllAdmins();
        observableList.setAll(adminList);
        adminListView.setItems(observableList);
        adminListView.setCellFactory(new Callback<ListView<Admin>, ListCell<Admin>>() {
            @Override
            public ListCell<Admin> call(ListView<Admin> param) {
                return new ListCell<Admin>() {
                    @Override
                    public void updateItem(Admin item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            HBox container = new HBox();
                            container.setSpacing(20);
                            container.getStyleClass().add("box");
                            container.setMouseTransparent(true);
                            ImageView imageView = new ImageView();
                            //利用线程池来加载图片，并设置为管理员头像
                            exec.execute(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImage(new Image(item.getAvatar()));
                                }
                            });
                            imageView.setFitHeight(100);
                            imageView.setFitWidth(100);
                            Label accountLabel = new Label(item.getAccount());
                            Label nameLabel = new Label(item.getName());
                            container.getChildren().addAll(imageView, accountLabel, nameLabel);
                            setGraphic(container);
                        }
                    }
                };
            }
        });
    }

}
