package com.xjh.service.store;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.cellvalue.*;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.xjh.service.store.ImageHelper.buildImageView;


public class TableViewUtils {

    static String ROW_INDEX = "_rowIndex_";

    public static Supplier<String> rowIndex() {
        return () -> ROW_INDEX;
    }

    public static <T> TableColumn<T, Object> newCol(String colTitle, Supplier<?> sp, double width) {
        return newCol1(colTitle, p -> new SimpleObjectProperty<>(sp.get()), width);
    }

    public static <T> TableColumn<T, Object> newCol(String colTitle, Function<T, Object> converter, double width) {
        return newCol1(colTitle, p -> new SimpleObjectProperty<>(converter.apply(p.getValue())), width);
    }

    public static <T> TableColumn<T, Object> newCol(String colTitle, String propertyName, double width) {
        return newCol1(colTitle, new PropertyValueFactory<>(propertyName), width);
    }

    public static <T> TableColumn<T, Object> newCol1(
            String colTitle,
            Callback<CellDataFeatures<T, Object>, ObservableValue<Object>> value,
            double width) {
        TableColumn<T, Object> c = new TableColumn<>(colTitle);
        c.setStyle("-fx-border-width: 0px; -fx-font-size: 18px;");
        if (width > 0) {
            c.setMinWidth(width);
        }
        c.setSortable(false);
        // 属性
        c.setCellValueFactory(value);
        c.setCellFactory(col -> {
            TableCell<T, Object> cell = new DragSelectionCell<T, Object>();
            cell.itemProperty().addListener((obs, ov, nv) -> render(cell, obs, nv));
            return cell;
        });
        return c;
    }

    private static void render(TableCell<?, Object> cell, Observable obs, Object nv) {
        if (nv instanceof String && nv.equals(ROW_INDEX)) {
            cell.textProperty().set((cell.getIndex() + 1) + "");
        } else if (nv instanceof Node) {
            Group group = new Group();
            group.getChildren().add((Node) nv);
            cell.setGraphic(group);
        } else if (nv instanceof RichText) {
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
            cell.setGraphic(number.toGraphicNode());
        } else if (nv instanceof OperationButton) {
            OperationButton ob = (OperationButton) nv;
            Button op = new Button(ob.getTitle().getText().toString());
            if (ob.getTitle().getColor() != null) {
                op.setTextFill(ob.getTitle().getColor());
            }
            if (ob.getTitleProperty() != null) {
                op.textProperty().bind(ob.getTitleProperty());
            }
            op.setOnMouseClicked(evt -> CommonUtils.safeRun(ob.getAction()));
            cell.setGraphic(op);
        } else if (nv instanceof Operations) {
            Operations ops = (Operations) nv;
            HBox hbox = new HBox();
            hbox.setSpacing(3);
            for (OperationButton ob : ops.getOperations()) {
                if (!ob.checkRoles()) {
                    continue;
                }
                RichText title = ob.getTitle();
                Button op = new Button(title.getText().toString());
                if (title.getColor() != null) {
                    op.setTextFill(title.getColor());
                }
                if (title.getPos() != null) {
                    op.setAlignment(title.getPos());
                }
                if (ob.getTitleProperty() != null) {
                    op.textProperty().bind(ob.getTitleProperty());
                }
                op.setOnMouseClicked(evt -> {
                    CommonUtils.safeRun(ob.getAction());
                    if (ob.getConsumer() != null) {
                        ob.getConsumer().accept(obs);
                    }
                });
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
        } else if (nv instanceof StringProperty) {
            StringProperty sp = (StringProperty) nv;
            cell.textProperty().bind(sp);
        } else if (nv instanceof Property) {
            Property<?> sp = (Property<?>) nv;
            render(cell, obs, sp.getValue());
            sp.addListener((x, o, n) -> render(cell, obs, n));
        } else if(nv != null){
            cell.textProperty().set(CommonUtils.stringify(nv));
        }
    }

    public static class DragSelectionCell<S,T> extends TableCell<S, T> {
        public DragSelectionCell() {
            setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    startFullDrag();
                    // getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
                    getTableColumn().getTableView().getSelectionModel().select(getIndex());
                }
            });


            setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
                    // getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
                    getTableColumn().getTableView().getSelectionModel().select(getIndex());
                }
            });
        }
    }
}
