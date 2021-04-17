package book.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.xjh.common.utils.ComponentUtil;
import com.xjh.common.utils.ExcelExport;
import com.xjh.dao.dataobject.Book;
import com.xjh.dao.dataobject.Type;
import com.xjh.service.BookService;

import book.domain.GuiceContainer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookController implements Initializable {
    //布局文件中的表格视图对象，用来显示数据库中读取的所有图书信息
    @FXML
    private TableView<Book> bookTable;

    //布局文件中的下拉框组件对象，用来显示数据库中读取的所有图书类别
    @FXML
    private ComboBox<Type> typeComboBox;

    //布局文件中的输入文本框对象，用来输入搜索关键词
    @FXML
    private TextField keywordsField;

    //图书模型数据集合，可以实时相应数据变化，无需刷新
    private ObservableList<Book> bookData = FXCollections.observableArrayList();

    //图书类型模型数据集合
    private ObservableList<Type> typeData = FXCollections.observableArrayList();

    //图书Service对象，从DAO工厂通过静态方法获得
    private BookService bookService = GuiceContainer.getInstance(BookService.class);

    //图书集合，存放数据库图书表各种查询的结果
    private List<Book> bookList = null;

    //类别集合，存放数据库类别表查询结果
    private List<Type> typeList = null;

    //表格中的编辑列
    private TableColumn<Book, Book> editCol = new TableColumn<>("操作");

    //表格中的删除列
    private TableColumn<Book, Book> delCol = new TableColumn<>("操作");

    //初始化方法，通过调用对图书表格和列表下拉框的两个封装方法，实现数据初始化
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        initComBox();
    }

    //表格初始化方法
    private void initTable() {
        //水平方向不显示滚动条，表格的列宽会均匀分布
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //1.调用查询所有图书的方法，
        bookList = bookService.getAllBooks();
        //将实体集合作为参数，调用显示数据的方法，可以在界面的表格中显示图书模型集合的值
        showBookData(bookList);

        //2.编辑列的相关设置
        editCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editCol.setCellFactory(param -> new TableCell<Book, Book>() {
            //通过ComponentUtil工具类的静态方法，传入按钮文字和样式，获得一个按钮对象
            private final Button editButton = ComponentUtil.getButton("编辑", "blue-theme");

            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (book == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(editButton);
                //点击编辑按钮，弹出窗口，输入需要修改的图书价格
                editButton.setOnAction(event -> {
                    TextInputDialog dialog = new TextInputDialog("请输入价格");
                    dialog.setTitle("图书修改界面");
                    dialog.setHeaderText("书名：" + book.getName());
                    dialog.setContentText("请输入新的价格:");
                    Optional<String> result = dialog.showAndWait();
                    //确认输入了内容，避免NPE
                    if (result.isPresent()) {
                        //获取输入的新价格并转化成Double数据
                        String priceString = result.get();
                        book.setPrice(Double.parseDouble(priceString));
                        //更新图书信息
                        bookService.updateBook(book);
                    }
                });
            }
        });
        //将编辑列加入图书表格
        bookTable.getColumns().add(editCol);

        //3.删除列的相关设置
        delCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        delCol.setCellFactory(param -> new TableCell<Book, Book>() {
            private final Button deleteButton = ComponentUtil.getButton("删除", "warning-theme");

            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (book == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(deleteButton);
                //点击删除按钮，需要将这一行从表格移除，同时从底层数据库真正删除
                deleteButton.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("确认对话框");
                    alert.setHeaderText("书名：" + book.getName());
                    alert.setContentText("确定要删除这行记录吗?");
                    Optional<ButtonType> result = alert.showAndWait();
                    //点击了确认按钮，执行删除操作，同时移除一行模型数据
                    if (result.get() == ButtonType.OK) {
                        bookData.remove(book);
                        bookService.deleteBook(book.getId());
                    }
                });
            }
        });
        //将除列加入图书表格
        bookTable.getColumns().add(delCol);

        //4.图书表格双击事件,双击弹出显示图书详情的界面
        bookTable.setRowFactory(tv ->

        {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                //判断鼠标双击了一行
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    //获得该行的图书ID属性
                    long id = row.getItem().getId();
                    //根据id查询到图书的完整信息
                    Book book = bookService.getBook(id);
                    //创建一个新的图书详情界面窗口
                    Stage bookInfoStage = new Stage();
                    bookInfoStage.setTitle("图书详情界面");
                    //用VBox显示具体图书信息
                    VBox vBox = new VBox();
                    vBox.setSpacing(10);
                    vBox.setAlignment(Pos.CENTER);
                    vBox.setPrefSize(600, 400);
                    vBox.setPadding(new Insets(10, 10, 10, 10));
                    Label nameLabel = new Label("书名：" + book.getName());
                    nameLabel.getStyleClass().add("font-title");
                    Label authorLabel = new Label("作者：" + book.getAuthor());
                    Label priceLabel = new Label("价格:" + book.getPrice());
                    Label stockLabel = new Label("库存：" + book.getStock());
                    ImageView bookImgView = new ImageView(new Image(book.getCover()));
                    bookImgView.setFitHeight(150);
                    bookImgView.setFitWidth(120);
                    Label summaryLabel = new Label(book.getSummary());
                    summaryLabel.setPrefWidth(400);
                    summaryLabel.setWrapText(true);
                    summaryLabel.getStyleClass().add("box");
                    vBox.getChildren().addAll(nameLabel, authorLabel, priceLabel, stockLabel, bookImgView, summaryLabel);
                    Scene scene = new Scene(vBox, 640, 480);
                    //因为是一个新的窗口，需要重新读入一下样式表，这个界面就可以使用style.css样式表中的样式了
                    scene.getStylesheets().add("/css/style.css");
                    bookInfoStage.setScene(scene);
                    bookInfoStage.show();
                }
            });
            return row;
        });
    }

    //下拉框初始化方法
    private void initComBox() {
        //2.将typeList集合加入typeData模型数据集合
        typeData.addAll(typeList);
        //3.将数据模型设置给下拉框
        typeComboBox.setItems(typeData);

        //4.下拉框选择事件监听，根据选择不同的类别，图书表格中会过滤出该类别的图书
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    // System.out.println(newValue.getId() + "," + newValue.getTypeName());
                    //移除掉之前的数据
                    bookTable.getItems().removeAll(bookData);
                    //根据选中的类别查询该类别所有图书
                    bookList = bookService.getBooksByTypeId(newValue.getId());
                    //重新显示数据
                    showBookData(bookList);
                }
        );
    }

    //显示图书表格数据的方法
    private void showBookData(List<Book> bookList) {
        bookData.addAll(bookList);
        bookTable.setItems(bookData);
    }

    //弹出新增图书界面方法
    public void newBookStage() throws Exception {
        Stage addBookStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/add_book.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/style.css");
        AddBookController addBookController = fxmlLoader.getController();
        addBookController.setBookData(bookData);
        addBookStage.setTitle("新增图书界面");
        //界面大小不可变
        addBookStage.setResizable(false);
        addBookStage.setScene(scene);
        addBookStage.show();
    }

    //根据关键词搜索方法
    public void search() {
        bookTable.getItems().removeAll(bookData);
        //获得输入的查询关键字
        String keywords = keywordsField.getText().trim();
        bookList = bookService.getBooksLike(keywords);
        showBookData(bookList);
    }

    //数据导出方法，采用hutool提供的工具类
    public void export() {
        ExcelExport.export(bookList);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示信息");
        alert.setHeaderText("图书数据已导出!请到D盘根目录查看!");
        alert.showAndWait();
    }
}
