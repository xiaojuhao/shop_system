package com.xjh.startup.another;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.cellvalue.Money;
import com.xjh.common.utils.cellvalue.RichText;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TableViewTest extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<DataModel> tv = new TableView<>();
        ObservableList<TableColumn<DataModel, ?>> list = tv.getColumns();
        list.addAll(
                newCol("菜品ID", "dishesId", 100),
                newCol("菜品名称", "dishesName", 200),
                newPriceColumn(),
                newCol("数量", "nums", 100),
                newCol("小计", "totalPrice", 100)
        );
        tv.getItems().addAll(loadData());
        primaryStage.setScene(new Scene(tv, 800, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    static AtomicLong index = new AtomicLong();
    private TableColumn<DataModel, Money> newPriceColumn() {
        TableColumn<DataModel, Money> column = new TableColumn<>("价格");
        column.setStyle("-fx-border-width: 0px; ");
        column.setMinWidth(100);
        column.setCellValueFactory(cellData -> {
            // System.out.println("setCellValueFactory + " + index.incrementAndGet());
            DataModel data = cellData.getValue();
            Money money = new Money(data.dishesPrice).with(Color.RED).with(Pos.CENTER);
            return new SimpleObjectProperty<>(money);
        });
        // 图片
        column.setCellFactory(col -> {
            // System.out.println("setCellFactory + " + index.incrementAndGet());
            TableCell<DataModel, Money> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, ov, nv) -> {
                // System.out.println(obs + ", " + ov + ", " + nv + " , " + index.incrementAndGet());
                if (nv != null) {
                    if (nv.getAmount() > 50) {
                        HBox graph = new HBox();
                        graph.setAlignment(Pos.CENTER);
                        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/icon/crown.png")));
                        imageView.setFitHeight(25);
                        imageView.setPreserveRatio(true);
                        graph.getChildren().add(imageView);
                        cell.graphicProperty().set(graph);
                    } else {
                        cell.textProperty().set(CommonUtils.formatMoney(nv.getAmount()));
                        cell.setAlignment(Pos.CENTER);
                        cell.setTextFill(Color.RED);
                    }
                }
            });
            return cell;
        });
        return column;
    }

    private TableColumn<DataModel, SimpleObjectProperty<Object>> newCol(String name, String filed, double width) {
        TableColumn<DataModel, SimpleObjectProperty<Object>> c = new TableColumn<>(name);
        c.setStyle("-fx-border-width: 0px; ");
        c.setMinWidth(width);
        c.setCellValueFactory(new PropertyValueFactory<>(filed));
        c.setCellFactory(new Callback<TableColumn<DataModel, SimpleObjectProperty<Object>>, TableCell<DataModel, SimpleObjectProperty<Object>>>() {

            public TableCell<DataModel, Object> call(TableColumn param) {
                return new TableCell<DataModel, Object>() {
                    public void updateItem(Object item, boolean empty) {
                        if (item instanceof RichText) {
                            RichText richText = (RichText) item;
                            setText(CommonUtils.stringify(richText.getText()));
                            if (richText.getColor() != null) {
                                setTextFill(richText.getColor());
                            }
                            if (richText.getPos() != null) {
                                setAlignment(richText.getPos());
                            }
                        } else if (item instanceof Money) {
                            Money money = (Money) item;
                            this.setText(item.toString());
                            if (money.getColor() != null) {
                                setTextFill(money.getColor());
                            }
                            if (money.getPos() != null) {
                                setAlignment(money.getPos());
                            }
                        } else {
                            setText(CommonUtils.stringify(item));
                        }
                    }
                };
            }
        });
        return c;
    }


    private List<DataModel> loadData() {
        List<DataModel> list = new ArrayList<>();
        {
            DataModel model = new DataModel();
            model.setDishesId(10001);
            model.setDishesName(new RichText("中华海草"));
            model.setDishesPrice(9.9);
            list.add(model);
        }
        {
            DataModel model = new DataModel();
            model.setDishesId(10002);
            model.setDishesName(new RichText("鳗鱼饭"));
            model.setDishesPrice(60.0);
            list.add(model);
        }
        {
            DataModel model = new DataModel();
            model.setDishesId(10003);
            model.setDishesName(new RichText("天妇罗"));
            model.setDishesPrice(23.0);
            list.add(model);
        }
        {
            DataModel model = new DataModel();
            model.setDishesId(10004);
            model.setDishesName(new RichText("牛舌").with(Color.RED));
            model.setDishesPrice(66.0);
            list.add(model);
        }
        {
            DataModel model = new DataModel();
            model.setDishesId(10005);
            model.setDishesName(new RichText("和牛-牛排").with(Color.RED));
            model.setDishesPrice(266.0);
            list.add(model);
        }

        return list;
    }

    public static class DataModel {
        Integer dishesId;
        RichText dishesName;
        String dishesPriceId;
        Double dishesPrice;
        String nums;
        String totalPrice;
        String ifDishesPackage;

        public Integer getDishesId() {
            return dishesId;
        }

        public void setDishesId(Integer dishesId) {
            this.dishesId = dishesId;
        }

        public RichText getDishesName() {
            return dishesName;
        }

        public void setDishesName(RichText dishesName) {
            this.dishesName = dishesName;
        }

        public String getDishesPriceId() {
            return dishesPriceId;
        }

        public void setDishesPriceId(String dishesPriceId) {
            this.dishesPriceId = dishesPriceId;
        }

        public Double getDishesPrice() {
            return dishesPrice;
        }

        public void setDishesPrice(Double dishesPrice) {
            this.dishesPrice = dishesPrice;
        }

        public String getNums() {
            return nums;
        }

        public void setNums(String nums) {
            this.nums = nums;
        }

        public String getIfDishesPackage() {
            return ifDishesPackage;
        }

        public void setIfDishesPackage(String ifDishesPackage) {
            this.ifDishesPackage = ifDishesPackage;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }
    }
}
