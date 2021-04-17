package book.controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import book.entity.Reader;
import book.service.ReaderService;
import book.utils.ServiceFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * 读者信息控制器
 */
public class ReaderController implements Initializable {
    @FXML
    private FlowPane readerPane;
    private ReaderService readerService = ServiceFactory.getReaderServiceInstance();
    private List<Reader> readerList = new ArrayList<>();


    private static final int MAX_THREADS = 8;
    //线程池配置
    private final Executor exec = Executors.newFixedThreadPool(MAX_THREADS, runnable -> {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readerList = readerService.getAllReaders();
        showReaders(readerList);
    }

    //通过循环遍历readerList集合，创建Hbox来显示每个读者信息
    private void showReaders(List<Reader> readerList) {
        //移除之前的记录
        readerPane.getChildren().clear();
        for (Reader reader : readerList) {
            HBox hBox = new HBox();
            hBox.setPrefSize(300, 240);
            hBox.getStyleClass().add("box");
            hBox.setSpacing(30);
            //左边垂直布局放头像和身份
            VBox leftBox = new VBox();
            leftBox.setAlignment(Pos.TOP_CENTER);
            leftBox.setSpacing(30);
            //开启一个线程，用来读取来自网络的头像
            ImageView imageView = new ImageView();
            //利用线程池来加载图片，并设置为读者头像
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    imageView.setImage(new Image(reader.getAvatar()));
                }
            });
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            Circle circle = new Circle();
            circle.setCenterX(40.0);
            circle.setCenterY(40.0);
            circle.setRadius(40.0);
            imageView.setClip(circle);
            imageView.getStyleClass().add("hover-change");
            Label roleLabel = new Label(reader.getRole());
            leftBox.getChildren().addAll(imageView, roleLabel);
            //右边垂直布局放姓名、部门、邮箱、电话
            VBox rightBox = new VBox();
            rightBox.setSpacing(15);
            Label nameLabel = new Label(reader.getName());
            nameLabel.getStyleClass().add("font-title");
            Label departmentLabel = new Label(reader.getDepartment());
            Label emailLabel = new Label(reader.getEmail());
            Label mobileLabel = new Label(reader.getMobile());
            Label dateLabel = new Label(reader.getJoinDate().toString());
            Button delBtn = new Button("删除");
            delBtn.getStyleClass().add("warning-theme");
            rightBox.getChildren().addAll(nameLabel, departmentLabel,
                    emailLabel, mobileLabel, dateLabel, delBtn);
            //左右两个垂直布局加入水平布局
            hBox.getChildren().addAll(leftBox, rightBox);
            //水平布局加入大的内容容器
            readerPane.getChildren().add(hBox);
            //删除按钮事件
            delBtn.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("确认对话框");
                alert.setContentText("确定要删除这行记录吗?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    long id = reader.getId();
                    //删除掉这行记录
                    readerService.deleteReader(id);
                    //从流式面板移除当前这个人的布局
                    readerPane.getChildren().remove(hBox);
                }
            });
        }
    }

    //新增读者方法
    public void addReader() {
        //创建一个Reader对象
        Reader reader = new Reader();
        //新建一个舞台
        Stage stage = new Stage();
        stage.setTitle("新增读者界面");
        //创建一个垂直布局，用来放新增用户的各个组件
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        Label infoLabel = new Label("输入读者信息：");
        infoLabel.setPrefHeight(50);
        infoLabel.setPrefWidth(580);
        infoLabel.setAlignment(Pos.CENTER);
        //给文本添加样式
        infoLabel.getStyleClass().addAll("green-theme", "font-title");
        TextField nameField = new TextField();
        nameField.setPromptText("请输入姓名");
        //输入框无焦点
        nameField.setFocusTraversable(false);
        TextField avatarField = new TextField();
        avatarField.setPromptText("请输入头像地址");
        avatarField.setFocusTraversable(false);
        //性别，两个单选按钮为一个组，教师单选按钮默认被选中
        HBox roleBox = new HBox();
        roleBox.setSpacing(20);
        ToggleGroup group = new ToggleGroup();
        RadioButton teacherButton = new RadioButton("教师");
        teacherButton.setToggleGroup(group);
        teacherButton.setSelected(true);
        teacherButton.setUserData("教师");
        RadioButton studentButton = new RadioButton("学生");
        studentButton.setToggleGroup(group);
        studentButton.setUserData("学生");
        roleBox.getChildren().addAll(teacherButton, studentButton);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                //给读者对象设置选中的角色
                System.out.println(group.getSelectedToggle().getUserData().toString());
                reader.setRole(group.getSelectedToggle().getUserData().toString());
            }
        });
        //院系部门数组
        String[] departments = {"机械工程学院", "电气工程学院", "航空工程学院", "交通工程学院",
                "计算机与软件学院", "经济管理学院", "商务贸易学院", "艺术设计学院"};
        //数组转为List
        List<String> list = Arrays.asList(departments);
        //将list中的数据加入observableList
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        //创建院系下拉框
        ComboBox<String> depComboBox = new ComboBox<>();
        depComboBox.setPromptText("请选择院系");
        //给下拉框初始化值
        depComboBox.setItems(observableList);
        //下拉框选项改变事件
        depComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //将选中的值设置给读者的部门属性
                reader.setDepartment(newValue);
            }
        });
        //创建一个日期选择器对象，并初始化值为当前日期
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        //邮箱输入框
        TextField emailField = new TextField();
        emailField.setPromptText("请输入邮箱");
        emailField.setFocusTraversable(false);
        //电话输入框
        TextField mobileField = new TextField();
        mobileField.setPromptText("请输入电话");
        mobileField.setFocusTraversable(false);
        //新增按钮
        FlowPane flowPane = new FlowPane();
        Button addBtn = new Button("新增");
        addBtn.setPrefWidth(120);
        addBtn.getStyleClass().addAll("green-theme", "btn-radius");
        flowPane.getChildren().add(addBtn);
        flowPane.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(infoLabel, nameField, avatarField, roleBox, depComboBox, datePicker,
                emailField, mobileField, flowPane);
        Scene scene = new Scene(vBox, 450, 380);
        scene.getStylesheets().add("/css/style.css");
        stage.getIcons().add(new Image("/img/logo.png"));
        stage.setScene(scene);
        stage.show();
        //点击新增按钮，将界面数据封装成一个Reader对象，写入数据库
        addBtn.setOnAction(event -> {
            String nameString = nameField.getText().trim();
            String avatarString = avatarField.getText().trim();
            String dateString = datePicker.getEditor().getText();
            String emailString = emailField.getText().trim();
            String mobileString = mobileField.getText().trim();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date joinDate = null;
            try {
                joinDate = df.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            reader.setName(nameString);
            reader.setAvatar(avatarString);
            reader.setJoinDate(joinDate);
            reader.setEmail(emailString);
            reader.setMobile(mobileString);
            System.out.println(reader.getName() + reader.getRole() + reader.getMobile());
            readerService.addReader(reader);
            stage.close();
            //重新读取一下数据显示
            readerList = readerService.getAllReaders();
            showReaders(readerList);
        });
    }

    //加载图片方法
    public void loadImages() {
        //        String path = pathField.getText();
        //        long start = System.currentTimeMillis();
        //        List<JLabel> images = ImageLoader.getInstance().loadImages(path);
        //        for (JLabel label :images) {
        //            contentPanel.add(label);
        //        }
        //        contentPanel.updateUI();
        //        long end = System.currentTimeMillis();
        //        System.out.println("加载需要" + (end - start) + "毫秒！");
        //
    }

}
