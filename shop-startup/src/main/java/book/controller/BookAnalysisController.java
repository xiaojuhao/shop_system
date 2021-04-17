package book.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.xjh.dao.dataobject.Type;
import com.xjh.service.BookService;

import book.domain.GuiceContainer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

public class BookAnalysisController implements Initializable {
    @FXML
    private StackPane pieChartPane, barChartPane;

    private BookService bookService = GuiceContainer.getInstance(BookService.class);
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //        Safe.run(() -> {
        //            initPieChart();
        //            initBarChart();
        //        });
    }

    private void initPieChart() {
        List<Type> typeList = new ArrayList<>();
        for (Type type : typeList) {
            int count = bookService.countByType(type.getId());
            pieChartData.add(new PieChart.Data(type.getTypeName(), count));
        }
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("按图书类别统计饼图");
        pieChartPane.getChildren().add(chart);
    }

    private void initBarChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc =
                new BarChart<>(xAxis, yAxis);
        bc.setTitle("根据类别统计柱形图");
        xAxis.setLabel("图书类别");
        yAxis.setLabel("图书数量");
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("2018年统计数据");
        List<Type> typeList = new ArrayList<>();
        for (Type type : typeList) {
            int count = bookService.countByType(type.getId());
            series1.getData().add(new XYChart.Data(type.getTypeName(), count));
        }
        bc.getData().addAll(series1);
        //        barChartPane.getChildren().add(bc);
    }
}
