package com.xjh.startup.view;

import com.xjh.dao.dataobject.PrinterDO;
import com.xjh.dao.mapper.PrinterDAO;
import com.xjh.startup.foundation.ioc.GuiceContainer;
import com.xjh.startup.view.base.Initializable;
import com.xjh.startup.view.base.SimpleForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

import static com.xjh.common.utils.TableViewUtils.newCol;

public class PrinterKitchenSettings extends SimpleForm implements Initializable {
    PrinterDAO printerDAO = GuiceContainer.getInstance(PrinterDAO.class);

    @Override
    public void initialize() {
        this.getChildren().clear();
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 10, 0));

        List<PrinterDO> printers = printerDAO.selectList(new PrinterDO());
        ObservableList<BO> items = FXCollections.observableArrayList(printers.stream().map(it -> {
            BO bo = new BO();
            bo.setPrinterId(it.getPrinterId());
            bo.setPrinterName(it.getPrinterName());
            Button button = new Button("关联菜品");
            button.setPrefWidth(180);
            button.setOnAction(evt -> openView(it));
            bo.setOperation(button);
            return bo;
        }).collect(Collectors.toList()));
        TableView<BO> tableView = new TableView<>();
        tableView.getColumns().addAll(
                newCol("ID", "printerId", 100),
                newCol("名称", "printerName", 200),
                newCol("操作", "operation", 200)
        );
        tableView.setItems(items);
        tableView.refresh();
        addLine(new Label("后厨打印设置"));
        addLine(tableView);
    }

    public void openView(PrinterDO printer) {
        Window sceneWindow = this.getScene().getWindow();
        Stage stage = new Stage();
        stage.initOwner(sceneWindow);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("关联菜品");
        stage.setScene(new Scene(new PrinterKitchenDishesSettings(printer)));
        stage.showAndWait();
    }

    @Data
    public static class BO {
        Integer printerId;
        String printerName;
        Node operation;
    }
}
