package com.xjh.startup.another;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Arrays;


public class DragSelectionTable extends Application {
    private TableView<Person> table = new TableView<Person>();
    private final ObservableList<Person> data =
            FXCollections.observableArrayList(
                    new Person("Jacob", "Smith", "jacob.smith@example.com"),
                    new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
                    new Person("Ethan", "Williams", "ethan.williams@example.com"),
                    new Person("Emma", "Jones", "emma.jones@example.com"),
                    new Person("Michael", "Brown", "michael.brown@example.com")
            );


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(450);
        stage.setHeight(500);
        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));
        table.setEditable(true);
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory( new PropertyValueFactory<Person, String>("lastName"));
        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

        final Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory = new DragSelectionCellFactory();
        firstNameCol.setCellFactory(cellFactory);
        lastNameCol.setCellFactory(cellFactory);
        emailCol.setCellFactory(cellFactory);
        table.setItems(data);
        table.getColumns().addAll(Arrays.asList(firstNameCol, lastNameCol, emailCol));
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setScene(scene);
        stage.show();
    }


    public static class DragSelectionCell extends TableCell<Person, String> {
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
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else {
                setText(item);
            }
        }
    }


    public static class DragSelectionCellFactory implements Callback<TableColumn<Person, String>, TableCell<Person, String>> {
        @Override
        public TableCell<Person, String> call(final TableColumn<Person, String> col) {
            return new DragSelectionCell();
        }
    }


    public static class Person {
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty email;
        private Person(String fName, String lName, String email) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.email = new SimpleStringProperty(email);
        }

        public String getFirstName() {
            return firstName.get();
        }
        public void setFirstName(String fName) {
            firstName.set(fName);
        }
        public String getLastName() {
            return lastName.get();
        }
        public void setLastName(String fName) {
            lastName.set(fName);
        }
        public String getEmail() {
            return email.get();
        }
        public void setEmail(String fName) {
            email.set(fName);
        }
    }
}