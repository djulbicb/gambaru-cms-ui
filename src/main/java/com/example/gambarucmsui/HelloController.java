package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.repo.Repository;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HelloController {
    private final Repository<UserEntity> userRepo;
    private ObservableList<User> items;

    private final int PAGE_SIZE = 50;
    private int CURRENT_PAGE = 1;

    public HelloController(HashMap<Class, Repository> repos) {
        userRepo = repos.get(UserEntity.class);
    }


    @FXML
    Label paginationLabel;

    @FXML
    protected void goNextPage() {
        CURRENT_PAGE++;
        paginationLabel.setText(CURRENT_PAGE + "");
        System.out.println("Clicked");
        List<User> collect = userRepo.findAll(CURRENT_PAGE, PAGE_SIZE).stream().map(o -> new User(o.getBarcode().getBarcodeId(), o.getFirstName(), o.getLastName())).collect(Collectors.toList());
        items.setAll(collect);
    }
    @FXML
    protected void goPreviousPage() {
        if (CURRENT_PAGE <= 1) {
            return;
        }
        CURRENT_PAGE--;
        paginationLabel.setText(CURRENT_PAGE + "");
        System.out.println("Clicked");
        List<User> collect = userRepo.findAll(CURRENT_PAGE, PAGE_SIZE).stream().map(o -> new User(o.getBarcode().getBarcodeId(), o.getFirstName(), o.getLastName())).collect(Collectors.toList());
        items.setAll(collect);

    }
    @FXML
    private TableView<User> tableUsers;

    public void initialize(){
        System.out.println(tableUsers);

        // Create columns
        TableColumn<User, String> idColumn = new TableColumn<>("Id");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("Ime");
        TableColumn<User, Integer> lastNameColumn = new TableColumn<>("Prezime");

        // Define how data should be displayed in columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("barcodeId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Add columns to the TableView
        tableUsers.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn);

        items = tableUsers.getItems();

        List<User> collect = userRepo.findAll(CURRENT_PAGE, PAGE_SIZE).stream().map(o -> new User(o.getBarcode().getBarcodeId(), o.getFirstName(), o.getLastName())).collect(Collectors.toList());
        items.setAll(collect);
        paginationLabel.setText(CURRENT_PAGE + "");

    }


}