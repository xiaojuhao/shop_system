package com.xjh.common.utils.cellvalue;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class InputNumber {
    Integer number;
    Consumer<Integer> onChange;

    public static InputNumber from(Integer number){
        InputNumber n = new InputNumber();
        n.setNumber(number);
        return n;
    }

    public HBox toGraphicNode(){
        HBox group = new HBox();
        TextField textField = new TextField(this.toString());
        textField.setPrefWidth(60);
        textField.setDisable(true);
        Button plus = new Button("+");
        plus.setOnMouseClicked(evt -> {
            this.setNumber(this.getNumber() + 1);
            textField.setText(this.toString());
            if (this.getOnChange() != null) {
                this.getOnChange().accept(this.getNumber());
            }
        });
        Button minus = new Button("-");
        minus.setOnMouseClicked(evt -> {
            if (this.getNumber() <= 1) {
                return;
            }
            this.setNumber(this.getNumber() - 1);
            textField.setText(this.toString());
            if (this.getOnChange() != null) {
                this.getOnChange().accept(this.getNumber());
            }
        });
        group.getChildren().addAll(textField, plus, minus);
        return group;
    }

    public String toString(){
        return number == null ? "" : number.toString();
    }

}
