package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.repo.Repository;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    protected void testMembership() {
    }

    @FXML
    protected void testAttendance() {
    }

    @FXML
    protected void goNextPage() {
        CURRENT_PAGE++;
        paginationLabel.setText(CURRENT_PAGE + "");
        System.out.println("Clicked");
        listPage();
    }
    @FXML
    protected void goPreviousPage() {
        if (CURRENT_PAGE <= 1) {
            return;
        }
        CURRENT_PAGE--;
        paginationLabel.setText(CURRENT_PAGE + "");
        System.out.println("Clicked");
        listPage();

    }

    private void listPage() {
        List<User> collect = userRepo.findAll(CURRENT_PAGE, PAGE_SIZE).stream().map(o ->
                new User(o.getBarcode().getBarcodeId(), o.getFirstName(), o.getLastName(), o.getGender(), o.getTeam(), o.getCreatedAt(), o.getLastAttendanceTimestamp(), o.getLastMembershipPaymentTimestamp())).collect(Collectors.toList());
        items.setAll(collect);
    }

    @FXML private TableView<User> tableUsers;

    @FXML private ToggleButton headerBtnAttendance;
    @FXML private ToggleButton headerBtnMembership;
    @FXML private ToggleButton headerBtnStatistics;
    @FXML private ToggleButton headerBtnSettings;

    @FXML private AnchorPane panelAttendance;
    @FXML private AnchorPane panelMembership;
    @FXML private AnchorPane panelStatistics;
    @FXML private AnchorPane panelSettings;
    ObservableList<AnchorPane> panels =FXCollections.observableArrayList();

    @FXML
    private void onHeaderButtonClick(MouseEvent event) {
        panels.stream().forEach(anchorPane -> anchorPane.setVisible(false));
        if (event.getSource().equals(headerBtnAttendance)) {
            panelAttendance.setVisible(true);
        }
        if (event.getSource().equals(headerBtnMembership)) {
            panelMembership.setVisible(true);
        }
        if (event.getSource().equals(headerBtnStatistics)) {
            panelStatistics.setVisible(true);
        }
        if (event.getSource().equals(headerBtnSettings)) {
            panelSettings.setVisible(true);
        }
        System.out.println(event.getSource());
    }

    public void onBarcodeScanned(Long barcodeId) {
        System.out.println(barcodeId);
        UserEntity byId = userRepo.findById(barcodeId);
        System.out.println(byId.getFirstName());
        byId.setLastAttendanceTimestamp(LocalDateTime.now());
        userRepo.save(byId);
        listPage();
    }

    public void initialize(){
        System.out.println(tableUsers);

        headerBtnAttendance.setSelected(true);

        panelSettings.setVisible(false);
        panelMembership.setVisible(false);
        panelStatistics.setVisible(false);
        panels.addAll(List.of(panelAttendance, panelMembership, panelStatistics, panelSettings));


        // Create columns
        TableColumn<User, String> idColumn = new TableColumn<>("Id");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("Ime");
        TableColumn<User, Integer> lastNameColumn = new TableColumn<>("Prezime");
        TableColumn<User, Integer> genderNameColumn = new TableColumn<>("Pol");
        TableColumn<User, Integer> teamColumn = new TableColumn<>("Tim");
        TableColumn<User, Integer> lastAttendanceColumn = new TableColumn<>("lastAttendanceTimestamp");
        TableColumn<User, Integer> lastMembershipPaymentColumn = new TableColumn<>("lastMembershipPaymentTimestamp");
        TableColumn<User, Integer> createdAt = new TableColumn<>("createdAt");


        // Define how data should be displayed in columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("barcodeId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        genderNameColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("team"));
        lastAttendanceColumn.setCellValueFactory(new PropertyValueFactory<>("lastAttendanceTimestamp"));
        lastMembershipPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("lastMembershipPaymentTimestamp"));
        createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

                // Add columns to the TableView
        tableUsers.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, genderNameColumn, teamColumn, lastAttendanceColumn, lastMembershipPaymentColumn, createdAt);

        items = tableUsers.getItems();

        listPage();
//        paginationLabel.setText(CURRENT_PAGE + "");

    }


}