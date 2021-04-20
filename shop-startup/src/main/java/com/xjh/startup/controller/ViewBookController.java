package com.xjh.startup.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.xjh.dao.dataobject.Book;
import com.xjh.service.domain.BookService;
import com.xjh.startup.foundation.guice.GuiceContainer;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class ViewBookController implements Initializable {
    @FXML
    private FlowPane bookPane;

    private List<Book> bookList;

    BookService bookService = GuiceContainer.getInstance(BookService.class);

    private static final int MAX_THREADS = 4;
    //线程池配置
    private final Executor exec = Executors.newFixedThreadPool(MAX_THREADS, runnable -> {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookList = bookService.getAllBooks();
        showBooks(bookList);
    }

    private void showBooks(List<Book> list) {
        ObservableList<Node> observableList = bookPane.getChildren();
        bookPane.getChildren().removeAll(observableList);
        for (Book book : list) {
            VBox vBox = new VBox();
            vBox.setPrefSize(240, 300);
            vBox.getStyleClass().add("box");
            vBox.setSpacing(10);
            vBox.setAlignment(Pos.CENTER);
            ImageView imageView = new ImageView();
            //利用线程池来加载图片，并设置为图书封面
            ///exec.execute(() -> imageView.setImage(new Image(book.getCover())));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.getStyleClass().add("hover-change");
            Label nameLabel = new Label(book.getName());
            nameLabel.getStyleClass().add("font-title");
            Label authorLabel = new Label("作者：" + book.getAuthor());
            Label priceLabel = new Label("价格：" + book.getPrice());
            Label stockLabel = new Label("库存：" + book.getStock());
            Button delBtn = new Button("删除");
            delBtn.getStyleClass().add("warning-theme");
            vBox.getChildren().addAll(imageView, nameLabel, authorLabel, priceLabel, stockLabel, delBtn);
            bookPane.getChildren().add(vBox);
            delBtn.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("确认对话框");
                alert.setContentText("确定要删除这行记录吗?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    bookService.deleteBook(book.getId());
                    bookPane.getChildren().remove(vBox);
                }
            });
        }
    }
}
