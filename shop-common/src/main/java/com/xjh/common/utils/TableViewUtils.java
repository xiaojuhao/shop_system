package com.xjh.common.utils;

import static com.xjh.common.utils.ImageHelper.buildImageView;

import com.xjh.common.utils.cellvalue.ImageSrc;
import com.xjh.common.utils.cellvalue.InputNumber;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.OperationButton;
import com.xjh.common.utils.cellvalue.Operations;
import com.xjh.common.utils.cellvalue.RichText;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class TableViewUtils {
    public static <T> TableColumn<T, Object> newCol(String name, String filed, double width) {
        TableColumn<T, Object> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        if (width > 0) {
            c.setMinWidth(width);
        }
        c.setSortable(false);
        c.setCellValueFactory(new PropertyValueFactory<>(filed));
        c.setCellFactory(col -> {
            TableCell<T, Object> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, ov, nv) -> {
                if (nv == null) {
                    return;
                }
                if (nv instanceof RichText) {
                    RichText val = (RichText) nv;
                    cell.textProperty().set(CommonUtils.stringify(val.getText()));
                    if (val.getColor() != null) {
                        cell.setTextFill(val.getColor());
                    }
                    if (val.getPos() != null) {
                        cell.setAlignment(val.getPos());
                    }
                } else if (nv instanceof Money) {
                    Money val = (Money) nv;
                    cell.textProperty().set(CommonUtils.formatMoney(val.getAmount()));
                    if (val.getColor() != null) {
                        cell.setTextFill(val.getColor());
                    }
                    if (val.getPos() != null) {
                        cell.setAlignment(val.getPos());
                    }
                } else if (nv instanceof InputNumber) {
                    InputNumber number = (InputNumber) nv;
                    HBox group = new HBox();
                    Label label = new Label(number.toString());
                    label.setPrefWidth(60);
                    Button plus = new Button("+");
                    plus.setOnMouseClicked(evt -> {
                        number.setNumber(number.getNumber() + 1);
                        label.setText(number.toString());
                        if (number.getOnChange() != null) {
                            number.getOnChange().accept(number.getNumber());
                        }
                    });
                    Button minus = new Button("-");
                    minus.setOnMouseClicked(evt -> {
                        if (number.getNumber() <= 1) {
                            return;
                        }
                        number.setNumber(number.getNumber() - 1);
                        label.setText(number.toString());
                        if (number.getOnChange() != null) {
                            number.getOnChange().accept(number.getNumber());
                        }
                    });
                    group.getChildren().addAll(label, plus, minus);
                    cell.setGraphic(group);
                } else if (nv instanceof OperationButton) {
                    OperationButton ob = (OperationButton) nv;
                    Button op = new Button(ob.getTitle());
                    op.setOnMouseClicked(evt -> CommonUtils.safeRun(ob.getAction()));
                    cell.setGraphic(op);
                } else if (nv instanceof Operations) {
                    Operations ops = (Operations) nv;
                    HBox hbox = new HBox();
                    hbox.setSpacing(3);
                    for (OperationButton ob : ops.getOperations()) {
                        Button op = new Button(ob.getTitle());
                        op.setOnMouseClicked(evt -> CommonUtils.safeRun(ob.getAction()));
                        hbox.getChildren().add(op);
                    }
                    cell.setGraphic(hbox);
                } else if (nv instanceof ImageSrc) {
                    ImageSrc img = (ImageSrc) nv;
                    ImageView iv = buildImageView(img.getImgUrl());
                    assert iv != null;
                    if (img.getWidth() > 0) {
                        iv.setFitWidth(img.getWidth());
                    }
                    if (img.getHeight() > 0) {
                        iv.setFitHeight(img.getHeight());
                    }
                    cell.setGraphic(iv);
                } else {
                    cell.textProperty().set(CommonUtils.stringify(nv));
                }
            });
            return cell;
        });
        return c;
    }
}
