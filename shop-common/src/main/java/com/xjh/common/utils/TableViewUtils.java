package com.xjh.common.utils;

import com.xjh.common.utils.cellvalue.InputNumber;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
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
                } else if(nv instanceof InputNumber){
                    InputNumber number = (InputNumber)nv;
                    HBox group = new HBox();
                    Label label = new Label(number.toString());
                    label.setPrefWidth(60);
                    Button plus = new Button("+");
                    plus.setOnMouseClicked(evt -> {
                        number.setNumber(number.getNumber() + 1);
                        label.setText(number.toString());
                        if(number.getOnChange() != null){
                            number.getOnChange().accept(number.getNumber());
                        }
                    });
                    Button minus = new Button("-");
                    minus.setOnMouseClicked(evt -> {
                        if(number.getNumber() <= 1){
                            return;
                        }
                        number.setNumber(number.getNumber() - 1);
                        label.setText(number.toString());
                        if(number.getOnChange() != null){
                            number.getOnChange().accept(number.getNumber());
                        }
                    });
                    group.getChildren().addAll(label, plus, minus);
                    cell.setGraphic(group);
                } else {
                    cell.textProperty().set(CommonUtils.stringify(nv));
                }
            });
            return cell;
        });
        return c;
    }
}
