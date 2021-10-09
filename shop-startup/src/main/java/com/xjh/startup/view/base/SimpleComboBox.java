package com.xjh.startup.view.base;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class SimpleComboBox<T> extends ComboBox<T> {
    public SimpleComboBox(List<T> list, Function<T, String> converter, Consumer<T> onSelect) {
        this.setItems(FXCollections.observableArrayList(list));
        this.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return converter.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
        if (onSelect != null) {
            this.valueProperty().addListener((obs, old, _new) -> {
                if (_new != null) {
                    onSelect.accept(_new);
                }
            });
        }
        this.getSelectionModel().selectFirst();
    }

    public void select(Predicate<T> test){
        this.getItems().forEach(it -> {
            if(test.test(it)){
                this.getSelectionModel().select(it);
            }
        });
    }
}
